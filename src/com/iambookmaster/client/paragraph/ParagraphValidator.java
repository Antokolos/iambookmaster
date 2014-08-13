package com.iambookmaster.client.paragraph;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.iambookmaster.client.Images;
import com.iambookmaster.client.Styles;
import com.iambookmaster.client.beans.Alchemy;
import com.iambookmaster.client.beans.Battle;
import com.iambookmaster.client.beans.Modificator;
import com.iambookmaster.client.beans.NPC;
import com.iambookmaster.client.beans.ObjectBean;
import com.iambookmaster.client.beans.Paragraph;
import com.iambookmaster.client.beans.ParagraphConnection;
import com.iambookmaster.client.beans.Parameter;
import com.iambookmaster.client.common.EditorTab;
import com.iambookmaster.client.common.MaskPanel;
import com.iambookmaster.client.common.ScrollContainer;
import com.iambookmaster.client.editor.ModelPersist;
import com.iambookmaster.client.exceptions.TimeoutException;
import com.iambookmaster.client.locale.AppConstants;
import com.iambookmaster.client.locale.AppLocale;
import com.iambookmaster.client.locale.AppMessages;
import com.iambookmaster.client.model.Model;
import com.iambookmaster.client.model.Model.FullParagraphDescriptonBuilder;
		
		public class ParagraphValidator extends ScrollContainer implements EditorTab {
		
			private final AppConstants appConstants = AppLocale.getAppConstants();
			private final AppMessages appMessages = AppLocale.getAppMessages();
			
			private VerticalPanel errorList;
			private Model model;
			private int errorCount;
			public ParagraphValidator(Model mod) {
				model = mod;
				errorList = new VerticalPanel();
				errorList.setStyleName("editor_panel");
				errorList.setSpacing(2);
				errorList.setSize("100%", "100%");
				setScrollWidget(errorList);
				resetHeight();
			}
			
			private void stopProgress() {
				MaskPanel.hide();
			}
			
			public void startTesting(final boolean validateText,final boolean validateMap,final boolean deploy,
					final ExportBookCallback callback) {
				startProgress();
				DeferredCommand.addCommand(new Command() {
					public void execute() {
						try {
							validate(validateText,validateMap,false,deploy);
						} catch (TimeoutException e) {
							//we cannot be here in client side
						}
						stopProgress();
						if (errorCount==0) {
							callback.onSuccess(null);
						} else {
							callback.onError();
						}
					}
				});
			}
		
			public void startTesting(final boolean validateText,final boolean validateMap) {
				startProgress();
				DeferredCommand.addCommand(new Command() {
					public void execute() {
						try {
							validate(validateText,validateMap,false,false);
						} catch (TimeoutException e) {
							//we cannot be here on client side
						}
						stopProgress();
					}
				});
			}
			
			/**
			 * Export text of book
			 * @param reExport 
			 * @return
			 */
			public void createText(final ExportBookCallback listener,final boolean reExport) {
				startProgress();
				DeferredCommand.addCommand(new Command() {
					public void execute() {
						try {
							validate(true,reExport==false,false,true);
						} catch (TimeoutException e) {
							//we cannot be here on client size
						}
						if (errorCount==0) {
							try {
								String text = generateText(reExport);
								if (text==null || errorCount>0) {
									listener.onError();
								} else  {
									listener.onSuccess(text);
								}
							} catch (TimeoutException e) {
								listener.onError();
							}
						}
						stopProgress();
					}
		
				});
			}
			
			private String generateText(boolean reExport) throws TimeoutException{
				addMessage(appConstants.validatorBookGeneration(),false);
				BookCreator creator = new BookCreator(model);
				String text = creator.createText(reExport,new BookCreatorListener() {
		
					public void algorithmError(int code) {
						addError(appMessages.validatorAlgorithmError(code));
					}
		
					public void allIterationsFailed() {
						addError(appConstants.validatorAllAttempsFailed());
					}
		
					public void iterationFailed(int fail, int total) {
						addWarning(appMessages.validatorIterationFailed(fail,total));
					}
		
					public void numberNotSet(Paragraph paragraph) {
						addError(appMessages.validationParagraphNumberNotSet(paragraph.getName()));
					}
		
					public void numberTooLarge(Paragraph paragraph, int max) {
						addError(appMessages.validationParagraphNumberOutOfRange(paragraph.getName(),max));
					}
		
					public void numbersDuplicated(Paragraph paragraph, Paragraph paragraph2) {
						addError(appMessages.validationParagraphNameDuplicated(paragraph.getName(),paragraph.getNumber(),paragraph.getName()));
					}
		
					public void noSupported() {
						addError("Generation paragraph numbers is not supported on client side");
					}
					
					/**
					 * Always false in client mode
					 */
					public boolean checkTimiout() {
						return false;
					}
		
					public void numberNotSet(ObjectBean objectBean) {
						addError(appMessages.validationObjectDoesNotHaveSecretKey(objectBean.getName()));
					}
		
					public void wrongObjectSecretKey(ParagraphConnection connection) {
						addError(appMessages.validationObjectHasWrongSecretKey(connection.getObject().getName(),connection.getObject().getKey(),connection.getFrom().getNumber(),connection.getFrom().getName(),connection.getTo().getNumber(),connection.getTo().getName()));
					}
		
					public void tooManyObjects() {
						addError(appMessages.serverBookGenerationTooManyObjects(model.getObjects().size(),model.getParagraphs().size()));
					}
					
				});
				addMessage("Done",true);
				return text;
			}
			
			private void startProgress() {
				errorCount = 0;
				errorList.clear();
				HTML html = new HTML("&nbsp;");
				errorList.add(html);
				errorList.setCellHeight(html,"99%");
				MaskPanel.show();
			}
			
			private void validate(boolean validateText, boolean validateMap, final boolean checkSecretKeys, boolean deploy) throws TimeoutException{
				//begin from Data validation
				if (model instanceof ModelPersist) {
					ModelPersist modelPersist = (ModelPersist) model;
					modelPersist.checkIntegrity();
				}
				if (validateMap) {
					addMessage(appConstants.validatorMapOfParagraphs(),false);
					//map validation
					PathFinder pathFinder = new PathFinder(model);
					pathFinder.setCheckSuccessOnly(deploy);
					pathFinder.validate(new ExtendedPathFinderErrorListener());
					addMessage(appConstants.validatorDone(),true);
				}
				if (validateText) {
					addMessage(appConstants.validatorContenValidation(),false);
					ArrayList<Paragraph> paragrapghs = model.getParagraphs();
					FullParagraphDescriptonBuilder builder = model.getFullParagraphDescriptonBuilder();
					builder.setHiddenUsingObjects(checkSecretKeys);
					builder.setEmptyConditionIsError(model.getSettings().isHiddenUsingObjects()==false);
					for (int i = 0; i < paragrapghs.size(); i++) {
						Paragraph paragraph = paragrapghs.get(i);
						ArrayList<String> errors = new ArrayList<String>();
						builder.getFullParagraphDescripton(paragraph, null, errors,null);
						for (int j = 0; j < errors.size(); j++) {
							addError(appMessages.validatorParagraphTextError(paragraph.getName(),errors.get(j)),paragraph);
						}
					}
					addMessage(appConstants.validatorDone(),true);
				}
				//end
			}
		
			private void addMessage(String text,boolean end) {
				new Message(text,end);
			}
		
			private void addError(String text) {
				errorCount++;
				new Message(text,Images.ERROR);
			}
		
			private void addWarning(String text) {
				new Message(text,Images.WARNING);
			}
		
			private void addWarning(String text,ObjectBean object) {
				new Message(text,Images.WARNING,object);
			}
		
			private void addError(String text,Paragraph paragraph) {
				errorCount++;
				new Message(text,Images.ERROR,paragraph);
			}
		
			private void addError(String text, ParagraphConnection connection) {
				errorCount++;
				new Message(text,Images.ERROR,connection);
			}
		
			private void addError(String text, ObjectBean object) {
				errorCount++;
				new Message(text,Images.ERROR,object);
			}
			
			public class Message extends HorizontalPanel implements ClickListener{
				private static final String STYLE = "validation_line";
				private static final String STYLE_END = "validation_line_end";
				private Image img;
				private Label label;
				private Paragraph paragraph;
				private ParagraphConnection connection;
				private ObjectBean object;
				
				public Message(String text, String image) {
					setWidth("100%");
					img = new Image(image);
					img.setStyleName(STYLE);
					add(img);
					setCellWidth(img,"1%");
					label = new Label(text);
					label.setWidth("100%");
					label.setStyleName(STYLE);
					add(label);
					setCellWidth(label,"99%");
					errorList.insert(this,errorList.getWidgetCount()-1);
					errorList.setCellWidth(this,"100%");
				}
		
				public Message(String text, String image, Paragraph paragraph) {
					this(text,image);
					makeClickable();
					this.paragraph = paragraph; 
				}
		
				private void makeClickable() {
					img.addStyleName(Styles.CLICKABLE);
					img.addClickListener(this);
					label.addStyleName(Styles.CLICKABLE);
					label.addClickListener(this);
				}
		
				public Message(String text, String image, ParagraphConnection connection) {
					this(text,image);
					makeClickable();
					this.connection = connection; 
				}
		
				public Message(String text, String image, ObjectBean object) {
					this(text,image);
					makeClickable();
					this.object = object; 
				}
		
				public Message(String text,boolean end) {
					setWidth("100%");
					label = new Label(text);
					if (end) {
						label.setStyleName(STYLE_END);
					} else {
						label.setStyleName(STYLE);
					}
					label.setWidth("100%");
					add(label);
					setCellWidth(label,"100%");
					errorList.insert(this,errorList.getWidgetCount()-1);
					errorList.setCellWidth(this,"100%");
				}
		
				public void onClick(Widget sender) {
					if (paragraph != null) {
						model.selectParagraph(paragraph, null);
						model.editParagraph(paragraph, null);
					} else if (connection != null) {
						model.selectParagraphConnection(connection, null);
					} else if (object != null) {
						model.selectObject(object, null);
					}
				}
				
			}
		
		public void activate() {
			resetHeight();
		}
	
		public void deactivate() {
		}
	
		public void close() {
		}

		public void findCommercialParagraph() {
			startProgress();
			for (Paragraph paragraph : model.getParagraphs()) {
				paragraph.setCommercial(true);
			}
			addMessage(appConstants.validatorCommercialParagraphs(),false);
			//searching commercial paragraphs
			PathFinder pathFinder = new PathFinder(model) {

				@Override
				protected void passedParagraph(Paragraph current) {
					current.setCommercial(false);
				}
				
			};
			pathFinder.setCheckSuccessOnly(false);
			try {
				pathFinder.validate(new StandardPathFinderErrorListener(){
					public void objectCannotBeFound(ObjectBean object) {
					}

					public void objectCannotBeUsed(ObjectBean bean) {
					}

					public void unriachebleParagraph(Paragraph bean) {
					}

					public void unusedParagraphConnection(ParagraphConnection bean) {
					}

					public void unusedModificator(Modificator modificator) {
					}

					public void unusedParameter(Parameter parameter) {
					}

					public void battleIsUsedNowhere(Battle battle) {
					}

					public void alchemyIsUsedNowhere(Alchemy alchemy) {
					}

					public void NPCIsUsedNowhere(NPC npc) {
					}

					public boolean canBePassed(ParagraphConnection connection, Paragraph paragraph) {
						//commercial paragraphs cannot be passed
						return paragraph.getType() != Paragraph.TYPE_COMMERCIAL;
					}

					public void noWayToSuccess(Paragraph paragraph) {
					}
				});
			} catch (TimeoutException e) {
				//we cannot be here
			}
			model.refreshParagraphs();
			addMessage(appConstants.validatorDone(),true);
		}

	public class ExtendedPathFinderErrorListener extends StandardPathFinderErrorListener {
		public void objectCannotBeFound(ObjectBean object) {
			addError(appMessages.validationObjectCannotBeGot(object.getName()),object);
		}

		public void objectCannotBeUsed(ObjectBean object) {
			addWarning(appMessages.validationObjectIsNotUsed(object.getName()),object);
		}

		public void unriachebleParagraph(Paragraph paragraph) {
			addError(appMessages.validationParagraphCannotBeRiached(paragraph.getName()),paragraph);
		}

		public void unusedParagraphConnection(ParagraphConnection connection) {
			addError(appMessages.validationConnectionCannotBeUsed(connection.getFrom().getName(),connection.getTo().getName()),connection);
		}
		public void unusedModificator(Modificator modificator) {
			addError(appMessages.unusedModificator(modificator.getName()));
		}

		public void unusedParameter(Parameter parameter) {
			addError(appMessages.unusedParameter(parameter.getName()));
		}

		public void NPCIsUsedNowhere(NPC npc) {
			addError(appMessages.NPCIsUsedNowhere(npc.getName()));
		}

		public void alchemyIsUsedNowhere(Alchemy alchemy) {
			addError(appMessages.alchemyIsUsedNowhere(alchemy.getName()));
		}

		public void battleIsUsedNowhere(Battle battle) {
			addError(appMessages.battleIsUsedNowhere(battle.getName()));
		}

		public boolean canBePassed(ParagraphConnection connection, Paragraph paragraph) {
			return true;
		}

		public void noWayToSuccess(Paragraph paragraph) {
			addError(appMessages.validationParagraphSuccessUnavailable(paragraph.getName()),paragraph);
		}

	}
	
	public abstract class StandardPathFinderErrorListener implements PathFinderErrorListener {
		final long end = new Date().getTime()+18000;
		HashSet<ObjectBean> reFoundObjects = new HashSet<ObjectBean>();
		
		public void alreadyHaveThatObject(Paragraph paragraph,ObjectBean bean) {
			if (bean.isUncountable()==false) {
				if (reFoundObjects.contains(bean)==false) {
					reFoundObjects.add(bean); 
					addError(appMessages.validationObjectCanBeFoundSomeTimes(paragraph.getName(),bean.getName()),paragraph);
				}
			}
		}

		public void bothDirConnectionHasObject(ParagraphConnection connection) {
			addError(appMessages.validationTwoWayConnectionHasCondition(connection.getFrom().getName(),connection.getTo().getName(),connection.getObject().getName()),connection);
		}

		public void duplicateConnectionBetweenParagraphs(ParagraphConnection connection) {
			addError(appMessages.validationConnectionIsDuplicated(connection.getFrom().getName(),connection.getTo().getName()),connection);
		}

		public void noWayFromNormalParagraph(Paragraph paragraph) {
			addError(appMessages.validationParagraphNoOutputConnections(paragraph.getName()),paragraph);
		}

		public void outwayFromFialOrSuccessParagraph(Paragraph paragraph) {
			addError(appMessages.validationParagraphHasOutputConnections(paragraph.getName()),paragraph);
		}

		public void startFromFialOrSuccessParagraph(Paragraph paragraph) {
			addError(appMessages.validationParagraphCannotBeStart(paragraph.getName()),paragraph);
		}

		public void uselessObjectInFailOrSuccess(Paragraph paragraph) {
			addError(appMessages.validationParagraphCannotHaveObject(paragraph.getName()),paragraph);
		}

		public void startLocationIsNotDefined() {
			addError(appConstants.validatorStartParagraphIsNotSet());
		}

		public void startHasIncomeConection(Paragraph paragraph) {
			addError(appConstants.validatorStartParagraphHasIncomeConnections(),paragraph);
		}

		public boolean checkTimeout() {
			//not time-out on client side
			return false;
		}

		public void conditionalChain(ParagraphConnection connection) {
			addError(appMessages.validatorConditionChainDetected(connection.getFrom().getName(),connection.getTo().getName()),connection);
		}

		public void gotAndLostObjectInTheSameParagraph(Paragraph paragraph, ObjectBean bean) {
			addError(appMessages.validatorObjectIsLostAndFoundInTheSamePlace(paragraph.getName(),bean.getName()),paragraph);
		}

		public void twoInputConnectionsWithTheSameObject(Paragraph paragraph, ObjectBean object) {
			addError(appMessages.twoInputConnectionsWithTheSameObject(paragraph.getName(),object.getName()),paragraph);
		}

		public void twoOutputConnectionsWithTheSameObject(Paragraph paragraph, ObjectBean object) {
			addError(appMessages.twoOutputConnectionsWithTheSameObject(paragraph.getName(),object.getName()),paragraph);
		}

		public void modificatorIsSetNowhere(Modificator modificator) {
			addError(appMessages.modificatorIsSetNowhere(modificator.getName()));
		}

		public void modificatorNotSetInConnection(ParagraphConnection connection) {
			addError(appMessages.modificatorNotSetInConnection(connection.getFrom().getName(),connection.getTo().getName()),connection);
		}

		public void modificatorsInFialOrSuccessParagraph(Paragraph paragraph) {
			addError(appMessages.modificatorsInFialOrSuccessParagraph(paragraph.getName()),paragraph);
		}

		public void parameterNotSetInConnection(ParagraphConnection connection) {
			addError(appMessages.parameterNotSetInConnection(connection.getFrom().getName(),connection.getTo().getName()),connection);
		}

		public void parametersInFromFialOrSuccessParagraph(Paragraph paragraph) {
			addError(appMessages.modificatorsInFialOrSuccessParagraph(paragraph.getName()),paragraph);
		}

		public void noSuccessParagraphs() {
			addError(appConstants.noSuccessParagraphsDefined());
		}

		public void updateStatus(int paragraphs, int connections) {
			MaskPanel.setText(appMessages.validationStatus(paragraphs,connections));
		}
		int check=100;
		public boolean canContinue() {
			boolean can = MaskPanel.isShown(); 
			if (--check==0) {
				check = 100;
				if (new Date().getTime()>end) {
					MaskPanel.hide();
					return false;
				}
			}
			return can;
		}

		public void mustGoAndNormaConnectionsInParagraph(Paragraph paragraph) {
			addError(appMessages.mustGoAndNormaConnectionsInParagraph(paragraph.getName()),paragraph);
		}

		public void done() {
		}
	}

	public void testConnections() {
		startProgress();
		DeferredCommand.addCommand(new Command() {
			public void execute() {
				validateConnections();
				stopProgress();
			}
		});
	}

	private void validateConnections() {
		addMessage(appConstants.validateConnectionsStart(),false);
		ArrayList<ParagraphConnection> connections = model.getParagraphConnections();
		HashMap<Paragraph,ArrayList<ParagraphConnection>> list = new HashMap<Paragraph, ArrayList<ParagraphConnection>>(model.getParagraphs().size());
		//collect outcome connections
		for (ParagraphConnection connection : connections) {
			ArrayList<ParagraphConnection> data = list.get(connection.getFrom());
			if (data==null) {
				data = new ArrayList<ParagraphConnection>();
				list.put(connection.getFrom(),data);
			}
			data.add(connection);
			if (connection.isBothDirections()) {
				data = list.get(connection.getTo());
				if (data==null) {
					data = new ArrayList<ParagraphConnection>();
					list.put(connection.getTo(),data);
				}
				data.add(connection);
			}
		}
		//scan connections
		main_connection:
		for (Iterator<Paragraph> iterator = list.keySet().iterator(); iterator.hasNext();) {
			Paragraph paragraph = iterator.next();
			ArrayList<ParagraphConnection> data = list.get(paragraph);
			
			ParagraphConnection passed = null;
			for (Iterator<ParagraphConnection> iterator2 = data.iterator(); iterator2.hasNext();) {
				ParagraphConnection connection = iterator2.next();
				if (connection.isConditional()) {
					if (model.getSettings().isSkipMustGoParagraphs() && connection.getStrictness()==ParagraphConnection.STRICTNESS_MUST) {
						//remove all MUST paragraphs
						iterator2.remove();
						continue;
					}
					if (connection.isHiddenUsage(model.getSettings())) {
						//hidden usage, does not require name
						iterator2.remove();
						continue;
					}
//					if (passed != null && passed.isReverceCondition(connection)) {
//						//remove both
//						if (data.size()==2) {
//							//just 2 connection with reverce condition - no checks'
//							continue main_connection;
//						}
//					}
//					passed = connection;
				}
			}
			if (data.size()<2) {
				//one or zero - no reason to check connection names
				continue;
			}
			//all connections in list must have names
			Iterator<ParagraphConnection> iterator2 = data.iterator();
			while (iterator2.hasNext()) {
				ParagraphConnection connection = iterator2.next();
				if (emptyConnectionName(connection.getNameFrom())) {
					addError(appMessages.validateConnectionEmptyNameFrom(connection.getFrom().getName(),connection.getTo().getName()),connection);
				}
				if (connection.isBothDirections()) {
					if (emptyConnectionName(connection.getNameTo())) {
						addError(appMessages.validateConnectionEmptyNameTO(connection.getFrom().getName(),connection.getTo().getName()),connection);
					}
				}
			}
		}
		addMessage(appConstants.validatorDone(),true);
	}

	private boolean emptyConnectionName(String name) {
		return name==null || name.trim().length()==0;
	}
	
}
