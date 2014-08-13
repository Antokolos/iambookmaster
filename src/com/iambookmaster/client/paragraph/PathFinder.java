package com.iambookmaster.client.paragraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;

import com.iambookmaster.client.beans.AbstractParameter;
import com.iambookmaster.client.beans.Alchemy;
import com.iambookmaster.client.beans.Battle;
import com.iambookmaster.client.beans.Modificator;
import com.iambookmaster.client.beans.NPC;
import com.iambookmaster.client.beans.ObjectBean;
import com.iambookmaster.client.beans.Paragraph;
import com.iambookmaster.client.beans.ParagraphConnection;
import com.iambookmaster.client.beans.Parameter;
import com.iambookmaster.client.exceptions.TimeoutException;
import com.iambookmaster.client.model.Model;
import com.iambookmaster.client.model.Model.FullParagraphDescriptonBuilder;
import com.iambookmaster.client.model.ParagraphParsingHandler;
import com.iambookmaster.client.paragraph.PathFinder.WayFinder.ParagraphTransition;

public class PathFinder {

	public static final int FIND_ALL_USED = 0;
	public static final int FIND_ALL = 1;
	public static final int FIND_ONE = 2;
	public static final int FIND_MIN_MAX = 3;
	
	private static final int STEP_INIT = 0;
	private static final int STEP_PREPARE = 1;
	private static final int STEP_CHECK_ALL_SUCCESS = 2;
	private static final int STEP_CHECK_UNUSED_PARAGRAPHS = 3;
	private static final int STEP_CHECK_UNUSED_CONNECTIONS = 4;
	
	private Model model;
	private PathFinderErrorListener errorListener = new PathFinderErrorListener() {
		public void uselessObjectInFailOrSuccess(Paragraph paragraph) {
		}
		public void updateStatus(int paragraphs, int connections) {
		}
		public void unusedParameter(Parameter parameter) {
		}
		public void unusedParagraphConnection(ParagraphConnection bean) {
		}
		public void unusedModificator(Modificator modificator) {
		}
		public void unriachebleParagraph(Paragraph bean) {
		}
		public void twoOutputConnectionsWithTheSameObject(Paragraph paragraph, ObjectBean object) {
		}
		public void twoInputConnectionsWithTheSameObject(Paragraph paragraph, ObjectBean object) {
		}
		public void startLocationIsNotDefined() {
		}
		public void startHasIncomeConection(Paragraph paragraph) {
		}
		public void startFromFialOrSuccessParagraph(Paragraph paragraph) {
		}
		public void parametersInFromFialOrSuccessParagraph(Paragraph paragraph) {
		}
		public void parameterNotSetInConnection(ParagraphConnection connection) {
		}
		public void outwayFromFialOrSuccessParagraph(Paragraph paragraph) {
		}
		public void objectCannotBeUsed(ObjectBean bean) {
		}
		public void objectCannotBeFound(ObjectBean object) {
		}
		public void noWayToSuccess(Paragraph paragraph) {
		}
		public void noWayFromNormalParagraph(Paragraph paragraph) {
		}
		public void noSuccessParagraphs() {
		}
		public void mustGoAndNormaConnectionsInParagraph(Paragraph paragraph) {
		}
		public void modificatorsInFialOrSuccessParagraph(Paragraph paragraph) {
		}
		public void modificatorNotSetInConnection(ParagraphConnection connection) {
		}
		public void modificatorIsSetNowhere(Modificator modificator) {
		}
		public void gotAndLostObjectInTheSameParagraph(Paragraph paragraph, ObjectBean bean) {
		}
		public void duplicateConnectionBetweenParagraphs(ParagraphConnection connection) {
		}
		public void done() {
		}
		public void conditionalChain(ParagraphConnection connection) {
		}
		public boolean checkTimeout() {
			return false;
		}
		public boolean canContinue() {
			return true;
		}
		public boolean canBePassed(ParagraphConnection connection, Paragraph paragraph) {
			return true;
		}
		public void bothDirConnectionHasObject(ParagraphConnection connection) {
		}
		public void battleIsUsedNowhere(Battle battle) {
		}
		public void alreadyHaveThatObject(Paragraph current, ObjectBean object) {
		}
		public void alchemyIsUsedNowhere(Alchemy alchemy) {
		}
		public void NPCIsUsedNowhere(NPC npc) {
		}
	};
	
	public PathFinder(Model mod) {
		this.model = mod;
	}
	
	
	public PathFinderErrorListener getErrorListener() {
		return errorListener;
	}

	public void setErrorListener(PathFinderErrorListener errorListener) {
		this.errorListener = errorListener;
	}


	private int step;
	private Stepper stepper = new Stepper();
	private WayFinder finder = new WayFinder();
	private boolean checkSuccess;
	private boolean showDiagInfo;
	private HashMap<ObjectBean,HashSet<Paragraph>> alreadyHaveThatObjects;  
	
	public void setShowDiagInfo(boolean showDiagInfo) {
		this.showDiagInfo = showDiagInfo;
	}

	public ArrayList<ArrayList<Paragraph>> findWays(Paragraph start, Paragraph end, GameState objects, HashSet<Paragraph> notUsed, HashSet<ParagraphConnection> unUsedConnections,int findMode) {
		try {
			return finder.findWays(start,end,null,objects,notUsed,unUsedConnections,findMode);
		} catch (TimeoutException e) {
			return null;
		}
	}
	
	public void continueProgess() throws TimeoutException {
		switch (step) {
		case STEP_PREPARE:
			stepper.prepare();
			break;
		case STEP_CHECK_ALL_SUCCESS:
			stepper.checkSuccess();
			break;
		case STEP_CHECK_UNUSED_PARAGRAPHS:
			stepper.checkUnusedParagraphs();
			break;
		case STEP_CHECK_UNUSED_CONNECTIONS:
			stepper.checkUnusedConnections();
			break;
		}
	}
	
	public HashMap<Paragraph, ArrayList<ParagraphTransition>> getMap() {
		return finder.getMap();
	}
	
	public void validate(PathFinderErrorListener listener) throws TimeoutException{
		step=STEP_INIT;
		errorListener = listener;
		finder.init();
		step=STEP_PREPARE;
		if (listener.checkTimeout()) {
			return;
		}
		stepper.prepare();
	}
	
	/**
	 * Top level class in checking
	 * @author ggadyatskiy
	 */
	class Stepper {
		private ArrayList<Paragraph> list;
		private ArrayList<Paragraph> success;
		private HashSet<Paragraph> unUsed;
		private HashSet<ParagraphConnection> unUsedConnections;
		private Paragraph start;
		private void prepare() throws TimeoutException {
			//scan for dumb paragraph (not fail, but without output)
			list = model.getParagraphs();
			success = new ArrayList<Paragraph>();
			HashSet<ObjectBean> unriachableObjects = new HashSet<ObjectBean>();
			unriachableObjects.addAll(model.getObjects());
			HashSet<Modificator> unriachableModificators = new HashSet<Modificator>();
			HashSet<Parameter> unusedParameters = new HashSet<Parameter>();
			HashSet<Battle> unusedBattles = new HashSet<Battle>();
			HashSet<NPC> unusedNPC = new HashSet<NPC>();
			HashSet<Alchemy> unusedAlchemy = new HashSet<Alchemy>();
			ArrayList<AbstractParameter> params = model.getParameters();
			for (AbstractParameter parameter : params) {
				if (parameter instanceof Modificator) {
					unriachableModificators.add((Modificator)parameter);
				} else if (parameter instanceof Parameter) {
					unusedParameters.add((Parameter)parameter);
				} else if (parameter instanceof Battle) {
					unusedBattles.add((Battle)parameter);
				} else if (parameter instanceof Alchemy) {
					Alchemy alchemy = (Alchemy)parameter;
					if (alchemy.isOnDemand()) {
						unusedAlchemy.add(alchemy);
					}
				} else if (parameter instanceof NPC) {
					unusedNPC.add((NPC)parameter);
				}
			}
			for (AbstractParameter parameter : params) {
				for (Iterator<Parameter> iterator = unusedParameters.iterator(); iterator.hasNext();) {
					Parameter param = iterator.next();
					if (parameter != param && parameter.dependsOn(param)) {
						iterator.remove();
					}
				}
			}
			//list of unused paragraphs
			unUsed = new HashSet<Paragraph>(list.size());
			unUsed.addAll(model.getParagraphs());
			//list of unused connections
			unUsedConnections = new HashSet<ParagraphConnection>(model.getParagraphConnections().size());
			unUsedConnections.addAll(model.getParagraphConnections());
			start = model.getStartParagraph();
			boolean critError=false;
			
			HashSet<Paragraph> inConnections = new HashSet<Paragraph>(model.getParagraphs().size());
			HashMap<Paragraph,ArrayList<ObjectBean>> inConditionsConnections = new HashMap<Paragraph,ArrayList<ObjectBean>>();
			ArrayList<ParagraphConnection> cons = model.getParagraphConnections(); 
			for (ParagraphConnection connection : cons) {
				if (inConnections.contains(connection.getTo())==false) {
					inConnections.add(connection.getTo());
				}
				switch (connection.getType()) {
				case ParagraphConnection.TYPE_MODIFICATOR:
				case ParagraphConnection.TYPE_NO_MODIFICATOR:
					if (connection.getModificator()==null) {
						errorListener.modificatorNotSetInConnection(connection);
					} else {
						unriachableModificators.remove(connection.getModificator());
					}
					break;
				case ParagraphConnection.TYPE_PARAMETER_LESS:
				case ParagraphConnection.TYPE_PARAMETER_MORE:
					if (connection.getParameter()==null) {
						errorListener.parameterNotSetInConnection(connection);
					} else {
						unusedParameters.remove(connection.getParameter());
					}
					break;

				default:
					//normal - check Object
					if (connection.getObject() != null) {
						ArrayList<ObjectBean> objs = inConditionsConnections.get(connection.getTo()); 
						if (objs==null) {
							//first income conditional connection
							objs = new ArrayList<ObjectBean>();
							objs.add(connection.getObject());
							inConditionsConnections.put(connection.getTo(),objs);
						} else if (objs.contains(connection.getObject())){
							if (model.getSettings().isHiddenUsingObjects()) {
								//two income connections with the same object, impossible to have for secret keys
								errorListener.twoInputConnectionsWithTheSameObject(connection.getTo(),connection.getObject());
							}
						} else {
							objs.add(connection.getObject());
						}
					}
				}
				if (connection.isBothDirections()) {
					if (inConnections.contains(connection.getFrom())==false) {
						inConnections.add(connection.getFrom());
					}
				}
			}

			//check unused stuff
			for (Modificator modificator : unriachableModificators) {
				errorListener.unusedModificator(modificator);
			}
			for (Parameter parameter : unusedParameters) {
				errorListener.unusedParameter(parameter);
			}
			unriachableModificators.clear();
			for (AbstractParameter parameter : params) {
				if (parameter instanceof Modificator) {
					unriachableModificators.add((Modificator)parameter);
				}
			}
			
			for (ParagraphConnection connection : cons) {
				if (connection.getObject() != null && inConditionsConnections.containsKey(connection.getFrom())) {
//					critError = true;
					errorListener.conditionalChain(connection);
				}
			}

			for (Paragraph paragraph:list) {
				if (paragraph.getBattle() != null) {
					unusedBattles.remove(paragraph.getBattle());
					if (paragraph.getEnemies() != null && paragraph.getEnemies().size()>0) {
						unusedNPC.removeAll(paragraph.getEnemies());
					}
				}
				if (paragraph.getAlchemy() != null && paragraph.getAlchemy().size()>0) {
					for (Alchemy alchemy : paragraph.getAlchemy().keySet()) {
						boolean value = paragraph.getAlchemy().get(alchemy);
						if (value) {
							unusedAlchemy.remove(alchemy);
						}
					}
				}
				ArrayList<WayFinder.ParagraphTransition> out = finder.connections.get(paragraph);
				if (out != null && model.getSettings().isSkipMustGoParagraphs()) {
					int must=0;
					int norm=0;
					for (ParagraphTransition transition : out) {
						if (transition.connection.isBothDirections()==false) {
							if (transition.connection.getStrictness()==ParagraphConnection.STRICTNESS_MUST) {
								must++;
							} else {
								norm++;
							}
						}
					}
					if (must>0 && norm>0) {
						errorListener.mustGoAndNormaConnectionsInParagraph(paragraph);
					} 
				}
				if (paragraph.isSuccess()) {
					success.add(paragraph);
				}
				if (paragraph.getGotObjects().size()>0 || paragraph.getLostObjects().size()>0) {
					if (paragraph.isFail() || paragraph.isSuccess()) {
						errorListener.uselessObjectInFailOrSuccess(paragraph);
					}
					unriachableObjects.removeAll(paragraph.getGotObjects());
					for (ObjectBean bean : paragraph.getLostObjects()) {
						if (paragraph.getGotObjects().contains(bean)) {
							errorListener.gotAndLostObjectInTheSameParagraph(paragraph,bean);
						}
					}
				}
				if (paragraph.getChangeModificators() != null) {
					for (Modificator modificator : paragraph.getChangeModificators().keySet()) {
						boolean value = paragraph.getChangeModificators().get(modificator);
						if (value) {
							unriachableModificators.remove(modificator);
						}
					}
				}
				if (paragraph.isFail() || paragraph.isSuccess()) {
					if (paragraph.getChangeModificators() != null && paragraph.getChangeModificators().size()>0) {
						errorListener.modificatorsInFialOrSuccessParagraph(paragraph);
					}
					if (paragraph.getChangeParameters() != null && paragraph.getChangeParameters().size()>0) {
						errorListener.parametersInFromFialOrSuccessParagraph(paragraph);
					}
					if (out != null) {
						critError = true;
						errorListener.outwayFromFialOrSuccessParagraph(paragraph);
					}
					if (paragraph==start) {
						critError = true;
						errorListener.startFromFialOrSuccessParagraph(paragraph);
					}
				} else if (out == null) {
					critError = true;
					errorListener.noWayFromNormalParagraph(paragraph);
				} else {
					//check for two connection with the same object (impossible for secret keys)
					HashSet<ObjectBean> objs = null;
					for (WayFinder.ParagraphTransition transition:out) {
						if (transition.connection.getObject() != null) {
							if (objs == null) {
								objs = new HashSet<ObjectBean>();
							} else if (objs.contains(transition.connection.getObject())){
								if (model.getSettings().isHiddenUsingObjects()) {
									//two connections with the same object, impossible for secret keys
									errorListener.twoOutputConnectionsWithTheSameObject(transition.connection.getFrom(),transition.connection.getObject());
								}
								continue;
							}
							objs.add(transition.connection.getObject());
						}
					}
				}
				if (paragraph==start) {
					if (inConnections.contains(paragraph)) {
						critError = true;
						errorListener.startHasIncomeConection(paragraph);
					}
				} else if (paragraph.getType()==Paragraph.TYPE_NORMAL || paragraph.isFail() || paragraph.isSuccess()) {
					if (inConnections.contains(paragraph)==false) {
						critError = true;
						errorListener.unriachebleParagraph(paragraph);
					}
				} 
			}
			//check for objects, which cannot be found
			for (Iterator<ObjectBean> iter = unriachableObjects.iterator(); iter.hasNext();) {
				ObjectBean bean = iter.next();
				errorListener.objectCannotBeFound(bean);
			}
			if (unriachableObjects.isEmpty()) {
				unriachableObjects = null;
			}
			
			//check for modificators, which cannot be set
			for (Modificator modificator : unriachableModificators) {
				errorListener.modificatorIsSetNowhere(modificator);
			}
			if (unriachableModificators.isEmpty()) {
				unriachableModificators = null;
			}
			
			//unused Alchemy
			for (Alchemy alchemy : unusedAlchemy) {
				errorListener.alchemyIsUsedNowhere(alchemy);
			}
			unusedAlchemy = null;
			for (Battle battle : unusedBattles) {
				errorListener.battleIsUsedNowhere(battle);
			}
			unusedBattles = null;
			for (NPC npc : unusedNPC) {
				errorListener.NPCIsUsedNowhere(npc);
			}
			unusedNPC = null;
			if (start == null) {
				errorListener.startLocationIsNotDefined();
			} else if (critError){
				//critical error found, stop
			} else if (unriachableModificators != null || unriachableObjects != null){
				//critical error
				return;
			} else {
				//check that each success can be reached
				step=STEP_CHECK_ALL_SUCCESS;
				if (errorListener.checkTimeout()) {
					throw new TimeoutException();
				}
				checkSuccessCounter = 0;
				checkSuccess();
			}
		}

		private int checkSuccessCounter=0;
		private void checkSuccess() throws TimeoutException{
			if (success.isEmpty()) {
				errorListener.noSuccessParagraphs();
				return;
			}
			boolean passed=false;
			for (;checkSuccessCounter < success.size(); checkSuccessCounter++) {
				GameState objects = new GameState();
				Paragraph sc = success.get(checkSuccessCounter);
				ArrayList<ArrayList<Paragraph>> result = finder.findWays(start,sc,null,objects,unUsed,unUsedConnections,FIND_ONE);
				if (result==null || result.size()==0) {
					errorListener.noWayToSuccess(sc);
				} else {
					passed = true;
					unUsed.remove(sc);
				}
			}
			step=STEP_CHECK_UNUSED_PARAGRAPHS;
			if (errorListener.checkTimeout()) {
				throw new TimeoutException();
			}
			if (passed) {
				if (checkSuccess==false) {
					if (unUsed.size()>0) {
						checkUnusedParagraphs();
					} else if (unUsedConnections.size()>0) {
						checkUnusedConnections();
					}
				}
			} else {
				printUnused();
			}
		}
		
		private void printUnused() {
			for (Paragraph paragraph : unUsed) {
				errorListener.unriachebleParagraph(paragraph);
			}
			for (ParagraphConnection connection : unUsedConnections) {
				errorListener.unusedParagraphConnection(connection);
			}
		}

		private void checkUnusedParagraphs() throws TimeoutException{
			//check that all unused in success paragraphs can be reached
			GameState objects = new GameState();
			ArrayList<ArrayList<Paragraph>> result = findWays(start,null,objects,unUsed,unUsedConnections,FIND_ALL_USED);
			if (result==null) {
				printUnused();
			} else {
				objects.clear();
				if (errorListener.canContinue()) {
					if (unUsedConnections.size()>0) {
						step=STEP_CHECK_UNUSED_CONNECTIONS;
						if (errorListener.checkTimeout()) {
							throw new TimeoutException();
						}
						checkUnusedConnections();
					}
				} else {
					printUnused();
				}
			}
		}
		
		private void checkUnusedConnections()  throws TimeoutException{
			//check that all unused connections can be used
			GameState objects = new GameState();
			while (true) {
				Iterator<ParagraphConnection> iterator = unUsedConnections.iterator();
				if (iterator.hasNext()==false) {
					break;
				}
				ParagraphConnection connection = iterator.next();
				objects.clear();
				ArrayList<ArrayList<Paragraph>> result = finder.findWays(start,connection,objects,unUsed,unUsedConnections,FIND_ALL);
				if (result==null) {
					break;
				} else if (result.size()==0) {
					errorListener.unusedParagraphConnection(connection);
					unUsedConnections.remove(connection);
				}
			}
			printUnused();
		}

	}
	
	/*-------------------------------------------------------------------*/
	public class WayFinder {
		
		private ArrayList<ArrayList<Paragraph>> findWays(Paragraph start, ParagraphConnection connection, GameState objects, HashSet<Paragraph> notUsed, HashSet<ParagraphConnection> unUsedConnections, int findMode) throws TimeoutException{
			return findWays(start,null,connection,objects,notUsed,unUsedConnections,findMode);
		}

		private HashMap<Paragraph, ArrayList<ParagraphTransition>> connections;
		
		private void init() {
			if (connections != null) {
				return;
			}
			connections = new HashMap<Paragraph, ArrayList<ParagraphTransition>>(model.getParagraphs().size());
			LinkedHashSet<ObjectBean> unusedObjects = new LinkedHashSet<ObjectBean>();
			unusedObjects.addAll(model.getObjects());
			ArrayList<ParagraphConnection> list = model.getParagraphConnections();
			for (int i = 0; i < list.size(); i++) {
				ParagraphConnection connection = list.get(i);
				if (connection.getObject() != null) {
					unusedObjects.remove(connection.getObject());
				}
				ArrayList<ParagraphTransition> out = connections.get(connection.getFrom());
				if (out==null) {
					out = new ArrayList<ParagraphTransition>();
					connections.put(connection.getFrom(), out);
				}
				if (out.contains(connection.getTo())) {
					//duplicate
					if (errorListener != null) {
						errorListener.duplicateConnectionBetweenParagraphs(connection);
					}
				} else {
					out.add(new ParagraphTransition(connection.getTo(),connection));
				}
				if (connection.isBothDirections()) {
					if (connection.getObject() != null && errorListener != null) {
						errorListener.bothDirConnectionHasObject(connection);
					}
					out = connections.get(connection.getTo());
					if (out==null) {
						out = new ArrayList<ParagraphTransition>();
						connections.put(connection.getTo(), out);
					}
					if (out.contains(connection.getFrom())) {
						//duplicate
						if (errorListener != null) {
							errorListener.duplicateConnectionBetweenParagraphs(connection);
						}
					} else {
						out.add(new ParagraphTransition(connection.getFrom(),connection));
					}
				}
			}
			
			FullParagraphDescriptonBuilder builder = model.getFullParagraphDescriptonBuilder();
			builder.setHiddenUsingObjects(false);
			builder.setCheckSecretKeys(false);
			builder.setPlayerMode(true);
			FindParsingHandler handler = new FindParsingHandler();
			builder.setParagraphParsingHandler(handler);
			for (Paragraph paragraph : connections.keySet()) {
				ArrayList<ParagraphTransition> mess = connections.get(paragraph);
				if (mess.size()==1) {
					continue;
				}
				boolean found=false;
				for (ParagraphTransition transition : mess) {
					if (isMustFollow(transition.connection)) {
						//these connections has to be ordered by the proper way
						found = true;
						break;
					}
				}
				if (found) {
					ArrayList<ParagraphConnection> conns = new ArrayList<ParagraphConnection>(mess.size());
					for (ParagraphTransition transition : mess) {
						conns.add(transition.connection);
					}
					handler.reset(mess);
					builder.getFullParagraphDescripton(paragraph, null, conns);
					//to fill result list
					handler.getResult();
//					connections.put(paragraph,handler.getResult());
				}
			}
			
			//check for objects, which cannot be used
			if (errorListener != null) {
				for (Iterator<ObjectBean> iter = unusedObjects.iterator(); iter.hasNext();) {
					ObjectBean bean = iter.next();
					errorListener.objectCannotBeUsed(bean);
				}
			}
		}
		
		
		/**
		 * Main method, it looks for ways from Paragraph "start" to Paragraph "end"
		 * @param start
		 * @param end
		 * @param endConnection
		 * @param objects
		 * @param notUsed
		 * @param unUsedConnections
		 * @param findMode
		 * @return possible way
		 */
		private ArrayList<ParagraphStep> way;
		private Paragraph current;
		private int score;
		private ArrayList<ArrayList<Paragraph>> findWays(Paragraph start, Paragraph end, ParagraphConnection endConnection, GameState objects, HashSet<Paragraph> notUsed, HashSet<ParagraphConnection> unUsedConnections,int findMode) throws TimeoutException{
			ArrayList<ArrayList<Paragraph>> result = new ArrayList<ArrayList<Paragraph>>();
			if (start==end) {
				result.add(new ArrayList<Paragraph>());
				return result;
			}
			init();
			way = new ArrayList<ParagraphStep>();
			current = start;
			score=0;
//			int iteration=0;
			main:
			while (true) {
//				iteration++;
//				if (iteration==22383) {
//					System.out.println("!!!");
//				}
				if (errorListener.canContinue()==false) {
					return null;
				}
				if (showDiagInfo) {
					System.out.print("-------------\nWAY(");
					System.out.print(score);
					System.out.print("): ");
					for (ParagraphStep step : way) {
						System.out.print(step.paragraph.getName());
						System.out.print('(');
						System.out.print(step.wayCounter);
						System.out.print('/');
						System.out.print(step.outcome.size());
						System.out.print(')');
						System.out.print(',');
					}
					System.out.println(current.getName());
				}
						
				if (notUsed != null) {
					if (notUsed.remove(current)) {
						passedParagraph(current);
						errorListener.updateStatus(notUsed.size(),unUsedConnections == null ? 0:unUsedConnections.size());
					}
				}
				
				if (current==end) {
					//got it
					break;
				}
				
				if (objects.apply(current,errorListener)) {
					score++;
					if (showDiagInfo) {
						System.out.println("New score="+score+", unUsedConnections.size()="+unUsedConnections.size());
					}
					
				}
				
				ArrayList<ParagraphTransition> out = connections.get(current); 
				
				if (out != null) {
					//next level
					ParagraphStep step = new ParagraphStep(current,out,score,way,findMode==FIND_ONE ? null:unUsedConnections,objects);
					Paragraph next = step.getNextParagraph(end,endConnection,objects,notUsed,unUsedConnections,way);
					if (next==null) {
						if (step.isFound()) {
							//go it
							way.add(step);
							if (findMode==FIND_ONE) {
								addWay(findMode,result,way,end,endConnection);
								break;
							} else if (findMode==FIND_ALL_USED) { 
								if (notUsed.size()==0 && unUsedConnections.size()==0) {
									break;
								}
							} else if (findMode==FIND_ALL) { 
								addWay(findMode,result,way,end,endConnection);
							}
						}
					} else {
						way.add(step);
						current = next;
						continue;
					}
				}
				//not way to go - go back
				if (way.size()>0) {
					//check for timeout
					if (errorListener != null && errorListener.checkTimeout()) {
						throw new TimeoutException();
					}
					//go back
					int len = way.size()-1;
					//get prev. step
					ParagraphStep step = way.get(len);
					while (true) {
						Paragraph next = step.getNextParagraph(end,endConnection,step.state,notUsed,unUsedConnections,way);
						if (next==null) {
							if (step.isFound()) {
								//go it
								addWay(findMode,result,way,end,endConnection);
								if (findMode==FIND_ONE) {
									break main;
								} else if (findMode==FIND_ALL_USED) { 
									if (notUsed.size()==0 && unUsedConnections.size()==0) {
										break main;
									}
								}
							} else if (len>0){
								//go back-back
								way.remove(len);
								len--;
								step = way.get(len);
							} else {
								//end
								break main;
							}
						} else {
							score = step.getScore();
							objects = new GameState(step.state);
							current = next;
							continue main;
						}
					}
				}
				//no way at all
				break;
			}
//			System.out.println(iteration);
			return result;
		}
		
		private void addWay(int findMode,ArrayList<ArrayList<Paragraph>> result, ArrayList<ParagraphStep> wayStep, Paragraph end,ParagraphConnection endConnection) {
			ArrayList<Paragraph> way = new ArrayList<Paragraph>(wayStep.size()+1);
			for (ParagraphStep paragraphStep : wayStep) {
				way.add(paragraphStep.paragraph);
			}
			if (end != null) {
				way.add(end);
			} else {
				//endConnection
				if (way.get(way.size()-1)==endConnection.getFrom()) {
					//TO paragraph
					way.add(endConnection.getTo());
				} else {
					//FROM paragraph
					way.add(endConnection.getFrom());
				}
			}
			if (findMode==FIND_MIN_MAX) {
				if (result.size()==0) {
					//first
					result.add(way);
					result.add(way);
				} else {
					if (result.get(0).size()>way.size()) {
						result.set(0, way);
					}
					if (result.get(1).size()<way.size()) {
						result.set(1, way);
					}
				}
			} else if (findMode==FIND_ALL_USED){
				if (result.size()==0) {
					//we need just one
					result.add(way);
				}
			} else {
				result.add(way);
			}
		}
	
		public class ParagraphStep {
			private GameState state;
			private Paragraph paragraph;
			private int wayCounter;
			private ArrayList<ParagraphTransition> outcome;
			private boolean found;
			private int score;
	
			public int getWayCounter() {
				return wayCounter;
			}
	
			public Paragraph getNextParagraph(Paragraph end,ParagraphConnection endConnection, GameState objects, HashSet<Paragraph> notUsed, HashSet<ParagraphConnection> unUsedConnections, ArrayList<ParagraphStep> way) {
				found = false;
				next:
				for (; wayCounter < outcome.size();) {
					for (int i = 0; i < wayCounter; i++) {
						ParagraphTransition transition = outcome.get(i);
						if (isMustFollow(transition.getConnection())) {
							if (objects.canBePassed(transition)) {
								 //must-connection is active, others cannot be used now
								 wayCounter = outcome.size();
								 return null;
							}
						}
					}
					ParagraphTransition transition = outcome.get(wayCounter++);
//					if (transition.getParagraph().getNumber()==108) {
//						System.out.println("!!!");
//					}
					if (objects.canBePassed(transition)) {
						//can be passed
						if (transition.paragraph==end) {
							//FOUND!!!!
							found = true;
							useTransition(transition,notUsed,unUsedConnections);
							return null;
						}
						if (transition.paragraph.isFail() || transition.paragraph.isSuccess()) {
							//fail or other success, nothing to do
							useTransition(transition,notUsed,unUsedConnections);
							continue;
						}
						//check for end-connection
						if (transition.getConnection()==endConnection) {
							//FOUND !!!
							found = true;
							useTransition(transition,notUsed,unUsedConnections);
							return null;
						}
						//drill it down
						for (int i = way.size()-1; i >=0; i--) {
							ParagraphStep step = way.get(i);
							if (step!=this) {
								//skip yourself
								if (step.paragraph==transition.paragraph) {
									//already use this way 
									if (step.getScore()<score) {
										//we can continue, something happened
										break;
									} else {
										//useless way
										if (unUsedConnections != null && unUsedConnections.contains(transition.connection)) {
											//useless way...but we can pass it
											unUsedConnections.remove(transition.connection);
										}
										continue next;
									}
								}
							}
						}
						useTransition(transition,notUsed,unUsedConnections);
						return transition.paragraph;
					}
				}
				//no next
				return null;
			}
	
			private void useTransition(ParagraphTransition transition, HashSet<Paragraph> notUsed, HashSet<ParagraphConnection> unUsedConnections) {
				boolean update=false;
				if (notUsed != null) {
					update = notUsed.remove(transition.paragraph);
				}
				if (unUsedConnections != null) {
					update = unUsedConnections.remove(transition.getConnection()) || update;
				}
				if (update) {
					passedParagraph(current);
					errorListener.updateStatus(notUsed == null ? 0: notUsed.size(),unUsedConnections == null ? 0:unUsedConnections.size());
				}
			}
	
			public ParagraphStep(Paragraph paragraph, ArrayList<ParagraphTransition> out, int score, ArrayList<ParagraphStep> way,HashSet<ParagraphConnection> unusedConnection,GameState state) {
				this.score = score;
				this.paragraph = paragraph;
				this.state = new GameState(state);
//				if (unusedConnection==null) {
					this.outcome = out;
					for (int i = way.size()-1; i >=0; i--) {
						ParagraphStep step = way.get(i);
						if (step.paragraph==paragraph && step.outcome.size() > step.wayCounter+1) {
							//found it
							ParagraphTransition transition = step.outcome.get(step.wayCounter-1);
							if (isMustFollow(transition.connection)==false) {
								//can be re-ordered
								outcome = new ArrayList<ParagraphTransition>(step.outcome.size()+1);
								outcome.addAll(step.outcome);
								outcome.remove(step.wayCounter-1);
								outcome.add(transition);
							}
							break;
						}
					}
//				} else {
//					this.outcome = new ArrayList<ParagraphTransition>(out.size());
//					for (ParagraphTransition transition : out) {
//						if (isMustFollow(transition.connection) || unusedConnection.contains(transition.connection)) {
//							//add must-go or unknown connections first
//							outcome.add(transition);
//						}
//					}
//					for (ParagraphTransition transition : out) {
//						if (isMustFollow(transition.connection) || unusedConnection.contains(transition.connection)) {
//							//add already passed connections later
//							continue;
//						}
//						outcome.add(transition);
//					}
//				}
			}
	
			public int getScore() {
				return score;
			}
	
			public boolean isFound() {
				return found;
			}
			
		}
	
		public class ParagraphTransition {
			private Paragraph paragraph;
			private ParagraphConnection connection;
			public ObjectBean getObject() {
				return connection.getObject();
			}
			
			public ParagraphConnection getConnection() {
				return connection;
			}
			public ParagraphTransition(Paragraph paragraph, ParagraphConnection connection) {
				super();
				this.paragraph = paragraph;
				this.connection = connection;
			}

			public Paragraph getParagraph() {
				return paragraph;
			}
			
		}
	
		public HashMap<Paragraph, ArrayList<ParagraphTransition>> getMap() {
			init();
			return connections;
		}
		
	}
	
	public class FindParsingHandler implements ParagraphParsingHandler {
		private ArrayList<ParagraphTransition> list;
		private ArrayList<ParagraphTransition> result;
		public void reset(ArrayList<ParagraphTransition> list) {
			this.list = new ArrayList<ParagraphTransition>(list.size());
			this.list.addAll(list);
			result = list;
			result.clear();
		}
		
		public void addAlchemy(Paragraph paragraph, String toValue,Alchemy alchemy) {
		}

		public void addLinkTo(Paragraph current, Paragraph next,ParagraphConnection connection) {
			for (int i = 0; i < list.size(); i++) {
				ParagraphTransition transition = list.get(i);
				if (transition.connection==connection) {
					list.remove(i);
					result.add(transition);
					break;
				}
			}
		}

		public void addAlchemyFromValue(Paragraph paragraph, String value) {
		}

		public void addObject(Paragraph paragraph,ObjectBean objectBean, String key) {
		}

		public void addText(Paragraph paragraph,String text) {
		}

		public ArrayList<ParagraphTransition> getResult() {
			if (list.size()>0) {
				result.addAll(list);
				list.clear();
			}
			return result;
		}

		public void addBattle(Battle battle, Paragraph paragraph) {
		}
		
	}
	
	public class GameState {
		private HashSet<ObjectBean> objects;		
		private HashSet<ObjectBean> allObjects;		
		private HashSet<Modificator> modificators;	
		private HashSet<Modificator> allModificators;	
		
		public GameState() {
			objects=new HashSet<ObjectBean>();		
			allObjects=new HashSet<ObjectBean>();		
			modificators = new HashSet<Modificator>();	
			allModificators = new HashSet<Modificator>();	
		}
		public GameState(GameState clone) {
			objects=new HashSet<ObjectBean>(clone.objects);		
			allObjects=new HashSet<ObjectBean>(clone.allObjects);		
			modificators = new HashSet<Modificator>(clone.modificators);	
			allModificators = new HashSet<Modificator>(clone.allModificators);	
		}
		public void clear() {
			objects.clear();
			allObjects.clear();
			modificators.clear();
			allModificators.clear();
		}
		
//		public void clear() {
//			objects.clear();
//			modificators.clear();
//			allObjects.clear();
//			allModificators.clear();
//		};
		
		public boolean canBePassed(ParagraphTransition transition) {
			if (errorListener.canBePassed(transition.connection,transition.paragraph)==false) {
				return false;
			}
			switch (transition.getConnection().getType()) {
			case ParagraphConnection.TYPE_ENEMY_VITAL_LESS:
				return true;
			case ParagraphConnection.TYPE_MODIFICATOR:
				return modificators.contains(transition.getConnection().getModificator());
			case ParagraphConnection.TYPE_NO_MODIFICATOR:
				return modificators.contains(transition.getConnection().getModificator())==false;
			case ParagraphConnection.TYPE_PARAMETER_LESS:
				//TODO add parameters check
				return true;
			case ParagraphConnection.TYPE_PARAMETER_MORE:
				//TODO add parameters check
				return true;
			case ParagraphConnection.TYPE_VITAL_LESS:
				return true;
			default:
				//normal
				if (transition.getObject()==null) {
					return true;
				} else if (transition.getConnection().getStrictness()==ParagraphConnection.STRICTNESS_MUST_NOT) {
					return objects.contains(transition.getObject())==false;
				} else {
					return objects.contains(transition.getObject());
				}
			}
		}

		public boolean apply(Paragraph paragraph, PathFinderErrorListener errorListener) {
			boolean step=false;
			HashMap<Modificator,Boolean> mods = paragraph.getChangeModificators();
			if (mods != null && mods.size()>0) {
				for (Modificator modificator : mods.keySet()) {
					boolean value = mods.get(modificator);
					if (value) {
						if (modificators.add(modificator) && allModificators.add(modificator)) {
							//does not exists, add
//							System.out.println("add "+modificator.getName());
							step = true;
						}
					} else if (modificators.remove(modificator)) {
//						System.out.println("clear "+modificator.getName());
						step = true;
					}
				}
			}
			if (paragraph.getGotObjects().isEmpty()==false) {
				for (ObjectBean bean : paragraph.getGotObjects()) {
					allObjects.add(bean);
					if (objects.add(bean)) {
						//next level
//						System.out.println("got "+bean.getName());
						step = true;
					} else if (bean.isUncountable()==false && errorListener != null) {
						//already have it
						if (alreadyHaveThatObjects==null) {
							alreadyHaveThatObjects = new HashMap<ObjectBean, HashSet<Paragraph>>();
						}
						HashSet<Paragraph> set;
						if (alreadyHaveThatObjects.containsKey(bean)) {
							set = alreadyHaveThatObjects.get(bean);
							if (set.contains(paragraph)) {
								continue;
							}
						} else {
							set = new HashSet<Paragraph>();
							alreadyHaveThatObjects.put(bean,set);
						}
						set.add(paragraph);
						errorListener.alreadyHaveThatObject(paragraph,bean);
					}
				}
			}
			if (paragraph.getLostObjects().isEmpty()==false) {
				for (ObjectBean bean : paragraph.getLostObjects()) {
					if (objects.remove(bean)) {
						//next level
//						System.out.println("lost "+bean.getName());
						step = true;
					}
				}
			}
			return step;
		}

		public void remove(Modificator modificator) {
			modificators.remove(modificator);
		}

		public boolean contains(ObjectBean bean) {
			return objects.contains(bean);
		}

		public HashSet<ObjectBean> getObjects() {
			return objects;
		}

		public HashSet<Modificator> getModificators() {
			return modificators;
		}
		
	}

	public boolean isMustFollow(ParagraphConnection connection) {
		//TODO add parameters control
		if (connection.getStrictness()==ParagraphConnection.STRICTNESS_MUST) {
			return connection.getType() == ParagraphConnection.TYPE_NORMAL ||
					connection.getType() == ParagraphConnection.TYPE_MODIFICATOR ||
					connection.getType() == ParagraphConnection.TYPE_NO_MODIFICATOR;
		} else {
			return false;
		}
	}

	protected void passedParagraph(Paragraph current) {
	}

	public void setCheckSuccessOnly(boolean checkSuccess) {
		this.checkSuccess = checkSuccess;
	}

}
