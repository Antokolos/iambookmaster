package com.iambookmaster.server.logic;

import java.io.UnsupportedEncodingException;

import com.iambookmaster.client.beans.ObjectBean;
import com.iambookmaster.client.beans.Paragraph;
import com.iambookmaster.client.beans.ParagraphConnection;
import com.iambookmaster.client.exceptions.TimeoutException;
import com.iambookmaster.client.locale.AppConstants;
import com.iambookmaster.client.locale.AppMessages;
import com.iambookmaster.client.model.Model;
import com.iambookmaster.client.paragraph.BookCreatorListener;
import com.iambookmaster.client.paragraph.TextBookDecrator;
import com.iambookmaster.server.LogicException;

public class ServerBookCreator extends AbstractModelProcessor {

	private ExtendedBookCreator creator;
	private StringBuffer iterations;
	private BookCreatorListener listener;
	private ServerBookCreatorListener bookCreatorListener;
	private Model model;
	public ServerBookCreator(Model mod,AppConstants appConst,AppMessages appMess) {
		super(mod,appConst,appMess);
		this.model = mod;
		iterations = new StringBuffer();
		creator = new ExtendedBookCreator(model);
		listener = new BookCreatorListener() {
			public void algorithmError(int code) {
				appendErrorText(appMessages.serverBookGenerationError(code));
				appendErrorEndLine();
			}
			public void allIterationsFailed() {
				appendErrorText(appMessages.serverBookGenerationFailed(iterations.toString()));
				appendErrorEndLine();
			}
			public void iterationFailed(int fail, int total) {
				iterations.append(appMessages.serverBookGenerationIterationFailed(fail,total));
			}
			public void noSupported() {
				//impossible to be here
				appendErrorEndLine();
			}
			
			public void numberNotSet(Paragraph paragraph) {
				appendParagraph(paragraph);
				appendErrorText(appConstants.serverBookGenerationNoNumber());
				appendErrorEndLine();
			}
			
			public void numberTooLarge(Paragraph paragraph, int max) {
				appendParagraph(paragraph);
				appendErrorText(appMessages.serverBookGenerationTooBig(max));
				appendErrorEndLine();
			}
			
			public void numbersDuplicated(Paragraph paragraph,
					Paragraph paragraph2) {
				appendParagraph(paragraph);
				appendErrorText(appConstants.serverBookGenerationParagraphAndParagraph());
				appendParagraph(paragraph2);
				appendErrorText(appConstants.serverBookGenerationTheSameNumber());
				appendErrorEndLine();
			}
			public boolean checkTimiout() {
				return bookCreatorListener.checkTimiout();
			}
			public void numberNotSet(ObjectBean objectBean) {
				appendObject(objectBean);
				appendErrorText(appConstants.serverBookGenerationNoSecretKey());
				appendErrorEndLine();
			}
			public void wrongObjectSecretKey(ParagraphConnection connection) {
				appendObject(connection.getObject());
				appendErrorText(appMessages.serverBookGenerationWrongSecredKey(connection.getObject().getKey()));
				appendParagraphConnection(connection);
				appendErrorEndLine();
			}
			public void tooManyObjects() {
				appendErrorText(appMessages.serverBookGenerationTooManyObjects(model.getObjects().size(),model.getParagraphs().size()));
				appendErrorEndLine();
			}
		}; 
		
	}
	
	public void create(ServerBookCreatorListener bookCreatorListener) throws TimeoutException,LogicException{ 
		this.bookCreatorListener = bookCreatorListener;
		clearErrors();
		creator.generateBook(listener);
		if (getErrors() != null) {
			throw new LogicException(getErrors());
		}
	}

	public void continueCreation(ServerBookCreatorListener bookCreatorListener) throws TimeoutException,LogicException {
		this.bookCreatorListener = bookCreatorListener;
		creator.continueCreation(listener);
		if (getErrors() != null) {
			throw new LogicException(getErrors());
		}
	}

	public Paragraph[] recreationValidate() throws LogicException{
		Paragraph[] paragraphs = creator.validateBookNumbers(listener);
		if (paragraphs==null) {
			throw new LogicException(getErrors());
		} else {
			return paragraphs;
		}
	}

	public String getText(Paragraph[] paragraphs) throws LogicException {
		TextBookDecrator decrator = new ServerTextBookDecrator(model,appConstants,appMessages);
		creator.createText(paragraphs, listener, decrator);
		return decrator.toText();
	}

	public String getHTML(Paragraph[] paragraphs) {
		HTMLBookDecrator decrator = new HTMLBookDecrator(model,appConstants,appMessages);
		creator.createText(paragraphs, listener, decrator);
		try {
			return new String(decrator.toBytes(),HTMLBookDecrator.ENCODING);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public String getURQ(Paragraph[] paragraphs) {
		URQBookDecrator decrator = new URQBookDecrator(model,appConstants,appMessages);
		creator.createText(paragraphs, listener, decrator);
		try {
			return new String(decrator.toBytes(),URQBookDecrator.ENCODING);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

}
