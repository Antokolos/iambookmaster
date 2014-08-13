package com.iambookmaster.server.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.logging.Logger;

import com.iambookmaster.client.beans.ObjectBean;
import com.iambookmaster.client.beans.Paragraph;
import com.iambookmaster.client.beans.ParagraphConnection;
import com.iambookmaster.client.exceptions.TimeoutException;
import com.iambookmaster.client.model.Model;
import com.iambookmaster.client.paragraph.BookCreator;
import com.iambookmaster.client.paragraph.BookCreatorListener;

public class ExtendedBookCreator extends BookCreator {

	private static final Logger log = Logger.getLogger(ExtendedBookCreator.class.getName());
	
	public ExtendedBookCreator(Model model) {
		super(model);
	}
	
	private HashMap<Paragraph, HashSet<Paragraph>> closest;
	private HashMap<Paragraph, ArrayList<ParagraphConnection>> conditionalConnections;
	private ArrayList<ObjectBean> usageQanity;
	
	public void generateBook(BookCreatorListener listener) throws TimeoutException {
		attempt = 0;
		conditionalConnections = new HashMap<Paragraph, ArrayList<ParagraphConnection>>();
		closest = new HashMap<Paragraph, HashSet<Paragraph>>(model.getParagraphs().size());
		final LinkedHashMap<ObjectBean, int[]> qanity = new LinkedHashMap<ObjectBean, int[]>();
		ArrayList<ParagraphConnection> connections = model.getParagraphConnections();
		for (int i = 0; i < connections.size(); i++) {
			ParagraphConnection connection = connections.get(i);
			addClosest(closest,connection.getFrom(),connection.getTo());
			addClosest(closest,connection.getTo(),connection.getFrom());
			if (connection.getObject() != null) {
				//connection with conditions
				ArrayList<ParagraphConnection> list = conditionalConnections.get(connection.getFrom());
				if (list==null) {
					list = new ArrayList<ParagraphConnection>();
					conditionalConnections.put(connection.getFrom(), list);
				}
				if (qanity.containsKey(connection.getObject())) {
					qanity.get(connection.getObject())[0]++;
				} else {
					qanity.put(connection.getObject(), new int[]{1});
				}
				list.add(connection);
			}
		}
		ArrayList<ObjectBean> usage = new ArrayList<ObjectBean>();
		usage.addAll(qanity.keySet());
		Collections.sort(usage, new Comparator<ObjectBean>(){
			public int compare(ObjectBean o1, ObjectBean o2) {
				int s1= qanity.get(o1)[0];
				int s2= qanity.get(o2)[0];
				if (s1>s2) {
					return 1;
				} else if (s1<s2) {
					return -1;
				} else {
					return 0;
				}
			}
		});
		usageQanity = new ArrayList<ObjectBean>();
		for (ObjectBean objectBean : usage) {
			if (qanity.get(objectBean)[0]>1) {
				usageQanity.add(objectBean);
			}
		}
		dispersion(listener);
	}
	
	private int attempt;
	private StringBuffer randomHistory;
	
	
	/**
	 * find place for each paragraph
	 * @param listener
	 * @return
	 */
	private void dispersion(BookCreatorListener listener) throws TimeoutException{
		//create list of paragraphs, which cannot be together
		//start dispersion
		ArrayList<Paragraph> all = model.getParagraphs();
		int size = all.size();
		Paragraph[] book = new Paragraph[size];
		
		outer:
//		for (;attempt<model.getSettings().getMaxAttemptCount();attempt++) {
		for (;attempt<10;attempt++) {
			if (attempt>0) {
				//clear
				log.info(model.getGameId()+", randomHistory="+randomHistory.toString());
				System.out.println("randomHistory="+randomHistory.toString());
				for (int i = 0; i < book.length; i++) {
					book[i]=null;
				}
			}
			log.info(model.getGameId()+", new attempt to create book");
			System.out.println("new attempt to create book");
			
			randomHistory = new StringBuffer();
			HashSet<Paragraph> located = new HashSet<Paragraph>(size);
			if (listener.checkTimiout()) {
				throw new TimeoutException();
			}
			HashMap<ObjectBean,Integer> keys = null;
			if (model.getSettings().isHiddenUsingObjects()) {
				//generate secret keys
				ArrayList<Paragraph> conditinals = new ArrayList<Paragraph>();
				for (Paragraph paragraph:all) {
					if (conditionalConnections.containsKey(paragraph)==false) {
						//simple paragraph - find place later
						continue;
					}
					conditinals.add(paragraph);
				}
				if (conditinals.size()>0) {
					//we have paragraphs with conditional objects
					Collections.sort(conditinals, new Comparator<Paragraph>(){
						public int compare(Paragraph o1, Paragraph o2) {
							int s1= conditionalConnections.get(o1).size();
							int s2= conditionalConnections.get(o2).size();
							if (s1>s2) {
								return 1;
							} else if (s1<s2) {
								return -1;
							} else {
								return 0;
							}
						}
					});
					keys = new HashMap<ObjectBean,Integer>(model.getObjects().size());
					
					//set secret keys for multiply-used objects
					int keyValue=model.getSettings().getMinimalSeparation();
					boolean negative=false;
					for (ObjectBean object : usageQanity) {
						if (negative) {
							//just assign negative value
							keys.put(object,0-keyValue);
							negative = false;
						} else {
							//find next value
							while (true) {
								keyValue++;
								switch (model.getSettings().getFineSecretKeys()) {
								case 5:
									if (keyValue % 5 >0) {
										continue;
									}
									break;
								case 10:
									if (keyValue % 10 >0) {
										continue;
									}
									break;
								}
								if (keyValue>book.length) {
									//too many secret objects
									listener.tooManyObjects();
									listener.allIterationsFailed();
									return;
								}
								//find next key
								keys.put(object,keyValue);
								negative = true;
								break;
							}
						}
					}
					
					PlaceValidator validator = new ConditionalPlaceValidator(keys,located);
					//generate keys for each object
					for (Paragraph paragraph:conditinals) {
						if (located.contains(paragraph)) {
							//we cannot be here due to chain is not allowed, but reserve for future
							continue;
						}
						located.add(paragraph);
						int pos = placeFound(book, paragraph, validator);
						if (pos<0) {
							//cannot be located
							listener.iterationFailed(located.size(),size);
							continue outer;
						}
						if (book[pos] == null) {
							book[pos] = paragraph;
							log.info(model.getGameId()+", 1.book["+pos+"]="+paragraph.getName());
							System.out.println("1.book["+pos+"]="+paragraph.getName());
						} else if (book[pos] != paragraph) {
							listener.algorithmError(6);
							continue outer;
						}
					}
				}
			}
			for (int pi = 0; pi < size; pi++) {
				Paragraph paragraph = all.get(pi);
				if (located.contains(paragraph)) {
					//already placed
					continue;
				}
				int candidate = placeFound(book,paragraph,null);
				if (candidate<0) {
					//we cannot find place for this paragraph, try again for the beginning
					listener.iterationFailed(pi,size);
					continue outer;
				}
				if (book[candidate]==null) {
					book[candidate] = paragraph;
					log.info(model.getGameId()+", 2.book["+candidate+"]="+paragraph.getName());
					System.out.println("2.book["+candidate+"]="+paragraph.getName());
				} else if (book[candidate] != paragraph){
					listener.algorithmError(5);
					continue outer;
				}
					
			}
			log.info(model.getGameId()+", randomHistory="+randomHistory.toString());
			System.out.println("randomHistory="+randomHistory.toString());
			for (int i = 0; i < book.length; i++) {
				if (book[i]==null) {
					//ops!!!, error
					listener.algorithmError(4);
					continue outer;
				} else {
					book[i].setNumber(i+1);
				}
			}
			//YES!!!!
			if (keys != null) {
				//remember secret keys
				for (ObjectBean object : keys.keySet()) {
					object.setKey(keys.get(object));
				}
			}
			return;
		}
		//we cannot create book
		listener.allIterationsFailed();
	}
		
	interface PlaceValidator {
		boolean accept(Paragraph[] book, Paragraph paragraph,int candidatePosition);
	}
	
	public class ConditionalPlaceValidator implements PlaceValidator  {
		
		public class PostConditionalPlaceValidator implements PlaceValidator {

			public boolean accept(Paragraph[] book, Paragraph paragraph, int candidatePosition) {
				int keyVal = candidatePosition-currentParagraph;
				switch (model.getSettings().getFineSecretKeys()) {
				case 5:
					if (keyVal % 5 != 0) {
						return false;
					}
					break;
				case 10:
					if (keyVal % 10 != 0) {
						return false;
					}
					break;
				}
				//look for the same key in global
				for (Integer key:keys.values()) {
					if (key.intValue()==keyVal) {
						return false;
					}
				}
				//look for the same key in local
				if (localKeys.size()>0) {
					for (Integer key:localKeys.values()) {
						if (key.intValue()==keyVal) {
							return false;
						}
					}
				}
				//can be used
				return true;
//				return canBePlaced(book,paragraph,candidatePosition);
				
			}
			
		}
		
		private PostConditionalPlaceValidator postValiadtor;
		private HashMap<ObjectBean,Integer> keys;
		private HashMap<ObjectBean,Integer> localKeys;
		private HashSet<Paragraph> located;
		
		public ConditionalPlaceValidator(HashMap<ObjectBean, Integer> keys, HashSet<Paragraph> located) {
			this.keys = keys; 
			this.located = located;
			postValiadtor = new PostConditionalPlaceValidator();
			localKeys = new HashMap<ObjectBean, Integer>(model.getObjects().size());
		}

		private int[] mess;
		private int messCounter;
		private int currentParagraph;
		
		/**
		 * Check that this Paragraph can be places in candidatePosition of the book
		 * @param book
		 * @param paragraph
		 * @param candidatePosition
		 * @return
		 */
		public boolean accept(Paragraph[] book, Paragraph paragraph,int candidatePosition) {
			//take it place
			book[candidatePosition] = paragraph;
			log.info(model.getGameId()+", 3.book["+candidatePosition+"]="+paragraph.getName());
			System.out.println("3.book["+candidatePosition+"]="+paragraph.getName());
			ArrayList<ParagraphConnection> connections = conditionalConnections.get(paragraph);
			if (mess==null || mess.length<connections.size()) {
				//initialize or extend
				mess = new int[connections.size()];
			}
			for (int i = 0; i < mess.length; i++) {
				mess[i]=-1;
			}
			messCounter = 0;
			if (_accept(book,paragraph,candidatePosition,connections)) {
				if (localKeys.size()>0) {
					//add new keys to global 
					keys.putAll(localKeys);
				}
				for (int i = 0; i < messCounter; i++) {
					//mark all used paragraphs as placed
					located.add(book[mess[i]]);
				}
				return true;
			}
			//remove our mess
			book[candidatePosition] = null;
			log.info(model.getGameId()+", 4.book["+candidatePosition+"]=null");
			System.out.println("4.book["+candidatePosition+"]=null");
			//clean our mess
			for (int i = 0; i < messCounter; i++) {
				book[mess[i]] = null;
				log.info(model.getGameId()+", 5.book["+mess[i]+"]=null");
				System.out.println("5.book["+mess[i]+"]=null");
			}
			return false;
		}
		
		public boolean _accept(Paragraph[] book, Paragraph paragraph,int candidatePosition, ArrayList<ParagraphConnection> connections) {
			if (localKeys.size()>0) {
				localKeys.clear();
			}
			for (ParagraphConnection connection : connections) {
				Integer value  =  keys.get(connection.getObject());
				if (value==null) {
					value = localKeys.get(connection.getObject());
				}
				int childPos;
				if (value==null) {
					//no value
					currentParagraph = candidatePosition;
					childPos = placeFound(book,connection.getTo(),postValiadtor);
					if (childPos<0) {
						//not acceptable, clean our mess and leave
						return false;
					} else {
						//remember new value in local map
						localKeys.put(connection.getObject(), childPos-candidatePosition);
					}
				} else {
					//value already assigned
					childPos = candidatePosition+value.intValue();
					if (childPos<0 || childPos>=book.length) {
						//not acceptable
						return false;
					}
					if (canBePlaced(book,connection.getTo(),childPos)==false) {
						//not acceptable, clean our mess and leave
						return false;
					}
					//ok, go next
				}
				//remember to clean
				addMess(childPos);
				book[childPos] = connection.getTo();
				log.info(model.getGameId()+", 6.book["+childPos+"]="+connection.getTo().getName());
				System.out.println("6.book["+childPos+"]="+connection.getTo().getName());
				
			}
			return true;
		}

		private void addMess(int childPos) {
			mess[messCounter++]=childPos;	
		}

		private boolean canBePlaced(Paragraph[] book, Paragraph paragraph, int candidatePosition) {
			//check that current paragraph can be placed here
			if (book[candidatePosition] != null) {
				//already taken
				return false;
			}
			for (int i = candidatePosition-model.getSettings().getMinimalSeparation(),l=candidatePosition+model.getSettings().getMinimalSeparation(); i <= l; i++) {
				if (i<0) {
					//before the first paragraph
					continue;
				}
				if (i>=book.length) {
					//out of range - it is OK
					return true;
				}
				if (book[i]==null) {
					//empty
					continue;
				}
				if (closest.get(book[i]).contains(paragraph)) {
					//ups...it cannot be here
					return false;
				}
			}
			return true;
		}
	}

	private int placeFound(Paragraph[] book, Paragraph paragraph, PlaceValidator validator) {
		int startPos = getStartPos(book);
		HashSet<Paragraph> close = closest.get(paragraph);
		int from = startPos+model.getSettings().getMinimalSeparation()+1;
		int to = from+model.getSettings().getMinimalSeparation()+1;
		int candidate = -1;
		for (int i = startPos; i < book.length; i++) {
			if (book[i]==null) {
				//unused paragraph
				if (i>=from && candidate<0) {
					//can be used for us
					candidate = i;
					to = i + model.getSettings().getMinimalSeparation();
				}
			} else if (close.contains(book[i])) {
				//prohibited
				candidate = -1;
				from = i+model.getSettings().getMinimalSeparation()+1;
				continue;
			}
			if (i>to && candidate>=0) {
				//found !!!
				if (book[candidate] == null) {
					if (validator==null) {
						//no conditions
						return candidate;
					} else if (validator.accept(book,paragraph,candidate)){
						//meets conditions
						return candidate;
					}
				}
				candidate++;
				to=candidate+model.getSettings().getMinimalSeparation();
			}
		}
		//check for the end
		while (candidate>=0 && candidate<book.length) {
			//end of the book, can be used
			if (book[candidate] == null) {
				if (validator==null) {
					//no conditions
					return candidate;
				} else if (validator.accept(book,paragraph,candidate)){
					//meets conditions
					return candidate;
				}
			}
			candidate++;
		}
		//scan from start
		candidate=-1;
		from = 0;
		to = model.getSettings().getMinimalSeparation();
		for (int i = 0; i < startPos; i++) {
			if (book[i]==null) {
				if (i>=from && candidate<0) {
					candidate = i;
					to = i + model.getSettings().getMinimalSeparation();
				}
			} else if (close.contains(book[i])) {
				//prohibited
				candidate = -1;
				from = i+model.getSettings().getMinimalSeparation()+1;
				to = from+model.getSettings().getMinimalSeparation();
				continue;
			}
			if (i>to && candidate>=0) {
				if (book[candidate] == null) {
					//found !!!
					if (validator==null) {
						//no conditions
						return candidate;
					} else if (validator.accept(book,paragraph,candidate)){
						//meets conditions
						return candidate;
					}
				}
				candidate++;
				to = candidate+model.getSettings().getMinimalSeparation();
			}
		}
		//cannot be found
		return -1;
	}

	private String[] randomSource;
//	private String[] randomSource = "178,25,210".split(",");
	private int randomSourceConter;
	
	private int getStartPos(Paragraph[] book) {
		int res=-1;
		if (randomSource != null && randomSourceConter<randomSource.length) {
			try {
				res = Integer.parseInt(randomSource[randomSourceConter++]);
			} catch (NumberFormatException e) {
			}
		}
		if (res<0) {
			res = (int)Math.round(Math.random()*book.length);
		}
		if (randomHistory.length()>0) {
			randomHistory.append(',');
		}
		randomHistory.append(res);
		return res;
	}

	private void addClosest(HashMap<Paragraph, HashSet<Paragraph>> closest, Paragraph from, Paragraph to) {
		HashSet<Paragraph> list = closest.get(from);
		if (list==null) {
			list = new HashSet<Paragraph>();
			closest.put(from, list);
		}
		list.add(to);
	}

	public void continueCreation(BookCreatorListener listener) throws TimeoutException{
		dispersion(listener);
	}
	
}
