package com.iambookmaster.client.iphone;

import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Label;
import com.iambookmaster.client.beans.Paragraph;
import com.iambookmaster.client.common.Base64Coder;
import com.iambookmaster.client.iphone.common.IPhoneButton;
import com.iambookmaster.client.iphone.data.IPhoneDataService;
import com.iambookmaster.client.iphone.images.IPhoneImages;
import com.iambookmaster.client.iphone.images.IPhoneStyles;
import com.iambookmaster.client.locale.AppConstants;
import com.iambookmaster.client.locale.AppLocale;
import com.iambookmaster.client.locale.AppMessages;
import com.iambookmaster.client.model.Model;
import com.iambookmaster.client.player.Feedback;

public class IPhoneFeedbackPanel implements Feedback {

	private static final AppConstants appConstants = AppLocale.getAppConstants();
	private static final AppMessages appMessages = AppLocale.getAppMessages();
	private static final IPhoneStyles css = IPhoneImages.INSTANCE.css();
	private static final String KEY_QUESTION = "question";
	protected static final String KEY_FEEDBACK = "feedback";
	protected static final String KEY_FIND_OTHERS = "others";
	protected static final String KEY_MAKE_OWN = "own";
	protected static final String KEY_BUY = "buy";
	protected static final String KEY_PAPER = "paper";
	
	private Model model;
	private IPhoneFeedbackPanelListener owner;
	private IPhoneCanvas canvas;
	private IPhoneViewListenerAdapter listener;
	private Paragraph paragraph;
	private ClickHandler goToFeedback;
	private ClickHandler viewOthers;
	private ClickHandler back;
//	private ClickHandler createOwnGame;
//	private ClickHandler goToQuestion;
	private ClickHandler buyFullVersion;
	private ClickHandler donateHandler;
	private ClickHandler buyPapperBook;
	private IPhoneDataService ds;
	
	public static void goToFullVersion(Model model,String state) {
		sendSignal(IPhoneDataService.generateCommandURL("full", state));
	}
	private static void sendSignal(String url) {
		Frame frame = new Frame();
		frame.setVisible(false);
		Document.get().getBody().appendChild(frame.getElement());
		frame.setUrl(url);
	}
	
	public static void buyFullVersion(Model model) {
		model.getContentPlayer().openURL(createURL(KEY_BUY,model));
	}
	private static String createURL(String key,Model model) {
//		"http://www.iambookmaster.com/feedback?lang"
//		StringBuilder builder = new StringBuilder(Base64Coder.decodeString("aHR0cDovL3d3dy5pYW1ib29rbWFzdGVyLmNvbS9mZWVkYmFjaz9sYW5nPQ=="));
		StringBuilder builder = new StringBuilder("http://localhost:8080/iambookmaster/feedback?lang");
		builder.append(appConstants.locale());
		builder.append("&book=").append(model.getGameKey()==null ? model.getGameId() : model.getGameKey());
		builder.append("&key=").append(key);
		return builder.toString();
	}
	public IPhoneFeedbackPanel(Model md,IPhoneDataService dataService) {
		this.model = md;
		this.ds = dataService;
		goToFeedback = new ClickHandler() {
			public void onClick(ClickEvent event) {
				model.getContentPlayer().openURL(createURL(KEY_FEEDBACK,model));
			}
		};
		viewOthers = new ClickHandler() {
			public void onClick(ClickEvent event) {
				model.getContentPlayer().openURL(createURL(KEY_FIND_OTHERS,model));
			}
		};
		back = new ClickHandler() {
			public void onClick(ClickEvent event) {
				owner.close();
			}
		};
//		createOwnGame = new ClickHandler() {
//			public void onClick(ClickEvent event) {
//				model.getContentPlayer().openURL(createURL(KEY_MAKE_OWN,model));
//			}
//		};
//		goToQuestion = new ClickHandler() {
//			public void onClick(ClickEvent event) {
//				model.getContentPlayer().openURL(createURL(KEY_QUESTION,model));
//			}
//		};
		buyPapperBook = new ClickHandler() {
			public void onClick(ClickEvent event) {
				model.getContentPlayer().openURL(createURL(KEY_PAPER,model));
			}
		};
		
		buyFullVersion = new ClickHandler() {
			public void onClick(ClickEvent event) {
				model.getContentPlayer().openURL(createURL(KEY_BUY,model));
			}
		};
		donateHandler = new ClickHandler() {
			public void onClick(ClickEvent event) {
				IPhoneThankyouPanel panel = new IPhoneThankyouPanel(model,ds);
				panel.show(new IPhoneThankyouPanelListener() {
					public void close() {
						canvas.setListener(listener);
						draw(canvas, true, true);
					}
				}, canvas, false);
			}
		};
		
		listener = new IPhoneViewListenerAdapter(){

			@Override
			public void back() {
				owner.close();
			}

			@Override
			public void redraw(IPhoneCanvas viewer) {
				draw(viewer,false,false);
			}

			
			@Override
			public void forward() {
			}

		};
	}
	
	private void draw(IPhoneCanvas canvas,boolean animate,boolean leftToRight) {
		if (animate) {
			canvas.clearWithAnimation(leftToRight);
		} else {
			canvas.clear();
		}
		Label label;
		
		label = new Label(model.getSettings().getBookTitle());
		label.setStyleName(css.bookName());
		canvas.add(label);
		label = new Label(model.getSettings().getBookAuthors());
		label.setStyleName(css.bookAuthors());
		canvas.add(label);
		
		label = new Label(appConstants.iphoneFeedbackInstructions());
		label.setStyleName(css.feedbackInstructions());
		canvas.add(label);
		if (model.getSettings().isDemoVersion() && !ds.isLinkedVersionPresent()) {
			addButton(canvas,appConstants.iphoneBuyFullVersion(),buyFullVersion);
		}
		addButton(canvas,appConstants.iphoneFeedbackGo(),goToFeedback);
//		addButton(canvas,appConstants.iphoneQuestionGo(),goToQuestion);
		addButton(canvas,appConstants.iphoneViewOthers(),viewOthers);
//		addButton(canvas,appConstants.iphoneCreateOwnGame(),createOwnGame);
		label = new Label(appConstants.iphoneBuyPaperBookText());
		label.setStyleName(css.feedbackDescription());
		canvas.add(label);
		addButton(canvas,appConstants.iphoneBuyPaperBook(),buyPapperBook);
		if (ds.isInAppAvailable()) {
			label = new Label(appConstants.iphoneDonationText());
			label.setStyleName(css.feedbackDescription());
			canvas.add(label);
			addButton(canvas,appConstants.iphoneDonation(),donateHandler);
		}
		
		addButton(canvas,appConstants.iphoneBack(),back);

		canvas.done();
	}

	private void addButton(IPhoneCanvas canvas, String title, ClickHandler handler) {
		IPhoneButton button = new IPhoneButton(title,handler);
		canvas.add(button);
		canvas.addClickHandler(button, handler);
		if (canvas.isVertical()) {
			button.setWidth(IPhoneViewerOldBook.toPixels(canvas.getClientWidth()-30));
		}
	}
//	private void applyLableStyle(Label label) {
//		if (canvas.isVertical()==false) {
//			label.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
//		}
//	}
	public void show(IPhoneFeedbackPanelListener owner, IPhoneCanvas canvas,boolean leftToRight) {
		this.owner = owner;
		this.canvas = canvas;
		canvas.setListener(listener);
		draw(canvas,true,leftToRight);
	}

}
