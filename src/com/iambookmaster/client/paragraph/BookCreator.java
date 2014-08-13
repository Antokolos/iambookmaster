package com.iambookmaster.client.paragraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.iambookmaster.client.beans.Greeting;
import com.iambookmaster.client.beans.Modificator;
import com.iambookmaster.client.beans.ObjectBean;
import com.iambookmaster.client.beans.Paragraph;
import com.iambookmaster.client.beans.ParagraphConnection;
import com.iambookmaster.client.exceptions.TimeoutException;
import com.iambookmaster.client.locale.AppLocale;
import com.iambookmaster.client.model.Model;
import com.iambookmaster.client.model.Model.FullParagraphDescriptonBuilder;
import com.iambookmaster.client.model.ParagraphDescriptionLinkProvider;

public class BookCreator {

	protected Model model;
	
	public BookCreator(Model model) {
		this.model = model;
	}

	public String createText(boolean reExport,BookCreatorListener listener) throws TimeoutException {
		if (reExport) {
			//paragraph numbers already set
			Paragraph[] book = validateBookNumbers(listener);
			if (book==null) {
				return null;
			} else {
				TextBookDecrator decrator = new TextBookDecrator(model,AppLocale.getAppConstants(),AppLocale.getAppMessages());
//				BookDecorator decrator = new URQBookDecrator(model,AppLocale.getAppConstants(),AppLocale.getAppMessages());
				createText(book, listener, decrator);
				return decrator.toText();
			}
		} else if (model.getSettings().getMinimalSeparation()==0) {
			//no separation at all
			ArrayList<Paragraph> all = model.getParagraphs();
			int size = all.size();
			for (int i = 0; i < size; i++) {
				all.get(i).setNumber(i+1);
			}
			Paragraph[] book = validateBookNumbers(listener);
			if (book==null) {
				return null;
			} else {
				BookDecorator decrator = new TextBookDecrator(model,AppLocale.getAppConstants(),AppLocale.getAppMessages());
				createText(book, listener, decrator);
				return decrator.toString();
			}
		} else {
			listener.noSupported();
			return null;
		}
	}
	
	
	public Paragraph[] validateBookNumbers(BookCreatorListener listener) {
		ArrayList<Paragraph> all = model.getParagraphs();
		Paragraph[] book = new Paragraph[all.size()];
		int max = all.size();
		boolean critical=false;
		//validation, step 1
		for (Paragraph paragraph:all) {
			int num = paragraph.getNumber(); 
			int numZero = num-1; 
			if (num<1) {
				//number is not set
				paragraph.setNumber(0);
				listener.numberNotSet(paragraph);
				critical = true;
			} else if (num>max) {
				listener.numberTooLarge(paragraph,max);
				critical = true;
			} else if (book[numZero] != null){
				listener.numbersDuplicated(paragraph,book[numZero]);
				critical = true;
			} else {
				book[numZero] = paragraph;
			}
			for (ObjectBean bean : paragraph.getGotObjects()) {
				if (bean.getKey()==0) {
					listener.numberNotSet(bean);
					critical = true;
				}
			}
		}
		//validation, step 2
		if (model.getSettings().isHiddenUsingObjects()) {
			//check object numbers
			ArrayList<ParagraphConnection> allConnections = model.getParagraphConnections();
			for (ParagraphConnection connection : allConnections) {
				if (connection.getObject() != null) {
					if (connection.getObject().getKey()==0) {
						listener.numberNotSet(connection.getObject());
						critical = true;
					} else if (connection.getFrom().getNumber()+connection.getObject().getKey() != connection.getTo().getNumber()) {
						//wrong key, directs us to wrong number
						listener.wrongObjectSecretKey(connection);
						critical = true;
					}
				}
			}
		}
		if (critical) {
			return null;
		} else {
			return book;
		}
	}

	public void createText(Paragraph[] book, final BookCreatorListener listener, final BookDecorator decrator) {
		//update paragraph number
		for (int i = 0; i < book.length; i++) {
			book[i].setNumber(i+1);
		}
		ArrayList<Greeting> greetings = model.getSettings().getGreetings();
		if (greetings != null) {
			decrator.startGreeting();
			for (Greeting greeting:greetings) {
				decrator.addGreeting(greeting);
			}
			decrator.endGreeting();
		}
		HashMap<Paragraph, ArrayList<ParagraphConnection>> links = new HashMap<Paragraph, ArrayList<ParagraphConnection>>(model.getParagraphs().size());
		HashMap<Paragraph, ArrayList<ParagraphConnection>> inLinks = new HashMap<Paragraph, ArrayList<ParagraphConnection>>(model.getParagraphs().size());
		ArrayList<ParagraphConnection> connections = model.getParagraphConnections();
		final HashMap<Modificator,Paragraph> absModificators = new HashMap<Modificator, Paragraph>();
		for (int i = 0; i < connections.size(); i++) {
			ParagraphConnection connection = connections.get(i);
			addLink(links,connection.getFrom(),connection);
			addLink(inLinks, connection.getTo(), connection);
			if (connection.isBothDirections()) {
				addLink(links,connection.getTo(),connection);
				addLink(inLinks, connection.getFrom(), connection);
			}
			if (connection.getType()==ParagraphConnection.TYPE_MODIFICATOR) {
				if (connection.getModificator()==null) {
					listener.algorithmError(BookCreatorListener.ERROR_MODIFICATOR_NOT_SET);
				} else if (connection.getModificator().isAbsolute()){
					if (absModificators.containsKey(connection.getModificator())) {
						if (absModificators.get(connection.getModificator()) != connection.getTo()) {
							listener.algorithmError(BookCreatorListener.ERROR_NON_UNIQUE_ABSOLUTE_MODIFICATOR);
						}
					} else {
						absModificators.put(connection.getModificator(),connection.getTo());
					}
				}
			}
		}
		final HashSet<Paragraph> used = new HashSet<Paragraph>(book.length);
		for (int i = 0; i < book.length; i++) {
			if (book[i]==null) {
				listener.algorithmError(0);
			}
			if (used.contains(book[i])) {
				listener.algorithmError(1);
			}
			if (book[i]==model.getStartParagraph()) {
				//start
				decrator.setStartParagraph(book[i]);
			}
			used.add(book[i]);
		}
		
		FullParagraphDescriptonBuilder builder = model.getFullParagraphDescriptonBuilder();
		builder.setPlayerMode(decrator.isPlayerMode());
		builder.setParagraphParsingHandler(decrator.getParagraphParsingHandler());
		builder.setCheckSecretKeys(true);
		builder.setEmptyConditionIsError(model.getSettings().isHiddenUsingObjects()==false);
		builder.setLinkProvider(new ParagraphDescriptionLinkProvider() {
			public String getLinkTo(Paragraph from, Paragraph to, ParagraphConnection connection) {
				if (used.contains(to)) {
					if (connection.getType()==ParagraphConnection.TYPE_MODIFICATOR && connection.getModificator().isAbsolute() && decrator.isHideAbsoluteModificators()) {
						//nothing, abs. modificator has own code
						return "";
					} else {
						return decrator.decorateNumber(to.getNumber(),from,to,connection);
					}
				} else {
					listener.algorithmError(BookCreatorListener.ERROR_PARAGRAPH_NUMBER_NOT_SET);
					return "";
				}
			}

			public String getModificatorValue(Modificator modificator) {
				Paragraph paragraph = absModificators.get(modificator);
				if (paragraph==null) {
					return null;
				} else {
					return String.valueOf(paragraph.getNumber());
				}
			}
		});
		decrator.startBook();
		for (int i = 0; i < book.length; i++) {
			connections = links.get(book[i]);
			ArrayList<ParagraphConnection> inConnections = inLinks.get(book[i]);
			decrator.appendParagraph(book[i].getNumber(),builder.getFullParagraphDescripton(book[i], null, connections),book[i],connections,inConnections);
		}
		decrator.endBook();
		model.refreshParagraphs();
	}

	private void addLink(HashMap<Paragraph, ArrayList<ParagraphConnection>> links, Paragraph from, ParagraphConnection to) {
		ArrayList<ParagraphConnection> list = links.get(from);
		if (list==null) {
			list = new ArrayList<ParagraphConnection>();
			links.put(from, list);
		}
		list.add(to);
	}

}