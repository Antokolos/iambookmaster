package com.iambookmaster.client.iphone;

import com.google.code.gwt.database.client.service.ListCallback;
import com.google.code.gwt.database.client.service.ScalarCallback;
import com.google.code.gwt.database.client.service.VoidCallback;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Label;
import com.iambookmaster.client.beans.Greeting;
import com.iambookmaster.client.beans.Paragraph;
import com.iambookmaster.client.iphone.common.IPhoneButton;
import com.iambookmaster.client.iphone.data.IPhoneDataService;
import com.iambookmaster.client.iphone.data.IPhoneFileBean;
import com.iambookmaster.client.iphone.images.IPhoneImages;
import com.iambookmaster.client.iphone.images.IPhoneStyles;
import com.iambookmaster.client.locale.AppConstants;
import com.iambookmaster.client.locale.AppLocale;
import com.iambookmaster.client.model.Model;
import com.iambookmaster.client.player.PlayerState;

public class IPhoneIntroScreen {

	private static final AppConstants appConstants = AppLocale.getAppConstants();
	private static final IPhoneStyles css = IPhoneImages.INSTANCE.css();
	
	private IPhoneIntroScreenListener owner;
	private IPhoneViewListenerAdapter listener;
	private Model model;
	private ClickHandler help;
	private ClickHandler about;
	private ClickHandler start;
	private ClickHandler continueGame;
	private PlayerState state;
	private boolean showAbout;
	private IPhoneCanvas canvas;
	private boolean hasContinue;
	private ClickHandler moreBooks;
	protected IPhoneFeedbackPanelListener feedbackListener;
	private IPhoneDataService ds;
	
	public IPhoneIntroScreen(IPhoneIntroScreenListener lst,Model md,PlayerState state, IPhoneDataService dbService) {
		this.owner = lst;
		this.model = md;
		this.state = state;
		this.ds =  dbService;
		showAbout = model.getSettings().isShowAboutOnStart();
		feedbackListener = new IPhoneFeedbackPanelListener() {
			public void close() {
				canvas.clearWithAnimation(true);
				draw(canvas);
			}
		};
		help = new ClickHandler() {
			public void onClick(ClickEvent event) {
				showHelp(false);
			}
		};
		about = new ClickHandler() {
			public void onClick(ClickEvent event) {
				showAbout=true;
				canvas.clear();
				draw(canvas);
			}
		};
		start = new ClickHandler() {
			public void onClick(ClickEvent event) {
				owner.start();
			}
		};
		continueGame = new ClickHandler() {
			public void onClick(ClickEvent event) {
				owner.continueGame();
			}
		};
		moreBooks = new ClickHandler() {
			public void onClick(ClickEvent event) {
				IPhoneFeedbackPanel feedbackPanel = new IPhoneFeedbackPanel(model,ds);
				feedbackPanel.show(feedbackListener, canvas, false);
			}
		};
		listener = new IPhoneViewListenerAdapter(){
			@Override
			public void redraw(IPhoneCanvas canvas) {
				canvas.clear();
				draw(canvas);
				
			}

			@Override
			public void back() {
				showHelp(true);
			}

			@Override
			public void forward() {
				owner.start();
			}

		};
	}
	
	private void showHelp(boolean leftToRight) {
		Model model = new Model(appConstants,  AppLocale.getAppMessages());
		try {
			model.fromJSON(IPhoneImages.INSTANCE.helpModel().getText());
			new IPhonePlayer(canvas,model,new IPhoneDataService() {
				@Override
				public void storeState(String data) {
					//nothing
				}
				
				@Override
				public void loadLastState(Model model, ScalarCallback<String> callback) {
					//nothing
				}

				@Override
				public void donate(VoidCallback callback) {
				}

				@Override
				public void calculateDonate(ScalarCallback<String> callback) {
				}

				@Override
				public void selectAvailableFiles(String exention, ListCallback<IPhoneFileBean> callback) {
				}

				@Override
				public void loadSingleFile(String name, ScalarCallback<String> callback) {
				}

			},new IPhonePlayerListener() {
				public boolean onParagraph(Paragraph paragraph) {
					if (paragraph.isFail() || paragraph.isSuccess()) {
						//return here
						canvas.clearWithAnimation(false);
						draw(canvas);
						return false;
					} else {
						return true;
					}
				}

				public boolean onOpenPlayerList() {
					return true;
				}

				public boolean onOpenFeedback() {
					return true;
				}
			});
			
		} catch (Exception e) {
		}
	}

	private void draw(IPhoneCanvas canvas) {
		this.canvas = canvas;
		canvas.setListener(listener);
		Label label = new Label(model.getSettings().getBookTitle());
		label.setStyleName(css.bookName());
		canvas.add(label);
		label = new Label(model.getSettings().getBookAuthors());
		label.setStyleName(css.bookAuthors());
		canvas.add(label);
		label = new Label(model.getSettings().getBookDescription());
		label.setStyleName(css.bookDescription());
		canvas.add(label);
		if (model.getSettings().isDemoVersion() && (model.getDemoInfoText() != null && model.getDemoInfoText().length()>0)) {
			label = new Label(model.getDemoInfoText());
			label.setStyleName(css.bookDemoVersion());
			canvas.add(label);
		}
		addSelection(canvas,appConstants.iphoneStartNewGame(),start);
		if (hasContinue) {
			addSelection(canvas,appConstants.iphoneContinueGame(),continueGame);
		}	
		addSelection(canvas,appConstants.iphoneHelp(),help);

		//view others books
		addSelection(canvas,appConstants.iphoneViewMore(),moreBooks);
		
		if (showAbout==false && model.getSettings().getGreetings().isEmpty()==false) {
			addSelection(canvas,appConstants.iphoneGreetings(),about);
		}
		
//		if (model.getSettings().isDisableAudio()==false) {
//			IPhoneAudioState audioState = new IPhoneAudioState(state.isAllowAudio(),canvas) {
//				@Override
//				protected void setEnabled(boolean enabled) {
//					super.setEnabled(enabled);
//					state.setAllowAudio(enabled);
//				}
//			};
//			canvas.add(audioState);
//		}

		if (model.getPlayerRules().length()>0) {
			Label rules = new Label(model.getPlayerRules());
			rules.setStyleName(css.bookDescription());
			canvas.add(rules);
		}
		
		if (showAbout && model.getSettings().getGreetings().isEmpty()==false) {
			//show greetings
			Label title = new Label(appConstants.iphoneGreetings());
			title.setStyleName(css.greetingTitle());
			canvas.add(title);
			for (Greeting greeting : model.getSettings().getGreetings()) {
				IPhoneGreetingWidgetFactory.create(greeting,canvas,model.getContentPlayer());
			}
			
		}

		canvas.done();
	}

	private void addSelection(IPhoneCanvas canvas, String titel,final ClickHandler handler) {
//		final FlowPanel left = new FlowPanel();
//		left.setStyleName(css.menuItemLeft());
//		left.addStyleName(css.menuItem());
//		FlowPanel right = new FlowPanel();
//		right.setStyleName(css.menuItemRight());
//		left.add(right);
//		HTML label = new HTML("<button class=\"btnCommon\"><div>"+titel+"</div></button>");
		IPhoneButton label = new IPhoneButton(titel,handler);
		if (canvas.isVertical()) {
//			label.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
			label.setWidth(IPhoneViewerOldBook.toPixels(canvas.getClientWidth()-30));
//			label.setWidth("auto");
		}
//		label.setStyleName(css.menuItemCenter());
//		label.setStyleName(css.stateSelection());
//		label.setStyleName(css.stateSelection());
//		label.addClickHandler(handler);
//		right.add(label);
		canvas.add(label);
		canvas.addClickHandler(label, handler);
	}

	public void show(boolean hasContinue,IPhoneCanvas canvas) {
		this.hasContinue = hasContinue;
		canvas.setListener(listener);
		draw(canvas);
	}

}
