package com.iambookmaster.client.player;

import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.NamedFrame;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.iambookmaster.client.beans.Paragraph;
import com.iambookmaster.client.common.JSONParser;
import com.iambookmaster.client.locale.AppConstants;
import com.iambookmaster.client.locale.AppLocale;
import com.iambookmaster.client.locale.AppMessages;
import com.iambookmaster.client.model.Model;

public abstract class FeedbackPanel extends VerticalPanel implements Feedback{

	private static AppConstants appConstants = AppLocale.getAppConstants();
	private static AppMessages appMessages = AppLocale.getAppMessages();
	private static int COUNTER;
	
	private FeedbackLevel story;
	private FeedbackLevel complexity;
	private FeedbackLevel view;
	private TextArea note;
	private HTML otherBooks;
	private HTML otherAuthors;
	private Button submit;
	private HTML submitMail;
	private FormPanel form;
	private VerticalPanel formPanel;
	private Model model;
//	private boolean asEmail;
	private String url;
	private NamedFrame frame;
	public FeedbackPanel(boolean viewOthers,String url,Paragraph paragraph,Model model,boolean asEmail, String allBooksURL) {
		this.model = model;
//		this.asEmail = asEmail;
		
		this.url = url;
		setSpacing(5);
		setSize("100%", "100%");
		setStyleName(PlayerStyles.ABOUT_PANEL);
		Label label = new Label(appConstants.feedbackPleaseProvide());
		add(label);
		setCellHeight(label, "1%");
		setCellWidth(label, "100%");
		Grid grid = new Grid(3,2);
		story = addItem(grid,appConstants.feedbackStory(),0);
		complexity = addItem(grid,appConstants.feedbackComplexity(),1);
		view = addItem(grid,appConstants.feedbackView(),2);
		add(grid);
		setCellHeight(grid, "1%");
		setCellWidth(grid, "100%");
		note = new TextArea();
		note.setSize("100%","100%");
		if (viewOthers) {
			if (model.getGameKey()==null) {
				//no game key, use gameId
				otherBooks = new HTML(appMessages.feedbackViewOtherBooks("otherBooksKey",model.getGameKey()));
			} else {
				otherBooks = new HTML(appMessages.feedbackViewOtherBooks("otherBooksId",model.getGameId()));
			}
			add(otherBooks);
			setCellHeight(otherBooks, "1%");
			setCellWidth(otherBooks, "100%");
		}
		if (asEmail) {
			ChangeHandler changeHandler = new ChangeHandler() {
				public void onChange(ChangeEvent event) {
					applyEmail();
				}
			};
			story.addChangeHandler(changeHandler);
			complexity.addChangeHandler(changeHandler);
			view.addChangeHandler(changeHandler);
			note.addChangeHandler(changeHandler);
		}
		otherAuthors = new HTML(appMessages.feedbackViewOthersAuthors(allBooksURL));
		add(otherAuthors);
		setCellHeight(otherAuthors, "1%");
		setCellWidth(otherAuthors, "100%");
		
		label = new Label(appConstants.feedbackAddNote());
		add(label);
		setCellHeight(label, "1%");
		setCellWidth(label, "100%");
		add(note);
		setCellHeight(note, "99%");
		setCellWidth(note, "100%");
		DeferredCommand.addCommand(new Command(){
			public void execute() {
				int h = note.getOffsetHeight();
				if (h<40) {
					note.setVisibleLines(3);
				}
			}
		});
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel.setSize("100%", "100%");
		add(horizontalPanel);
		setCellHeight(horizontalPanel, "1%");
		setCellWidth(horizontalPanel, "100%");
		if (asEmail) {
			submitMail = new HTML();
			horizontalPanel.add(submitMail);
			horizontalPanel.setCellWidth(submitMail, "50%");
			horizontalPanel.setCellHorizontalAlignment(submitMail, HasHorizontalAlignment.ALIGN_CENTER);
			applyEmail();
		} else {
			frame = new NamedFrame(FeedbackPanel.class.getName()+String.valueOf(++COUNTER));
			frame.setHeight("1px");
			frame.setWidth("1px");
			frame.setVisible(false);
			Document.get().getBody().appendChild(frame.getElement());
			
			submit = new Button(appConstants.feedbackSubmit());
			submit.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					submitFeedback();
					DeferredCommand.addCommand(new Command(){
						public void execute() {
							onClose();
						}
					});
				}
			});
			horizontalPanel.add(submit);
			horizontalPanel.setCellWidth(submit, "50%");
			horizontalPanel.setCellHorizontalAlignment(submit, HasHorizontalAlignment.ALIGN_CENTER);
			form = new FormPanel(frame);
			form.setVisible(false);
			form.setAction(url);
			form.setEncoding(FormPanel.ENCODING_URLENCODED);
			formPanel = new VerticalPanel();
			form.setMethod(FormPanel.METHOD_GET);
			if (paragraph != null) {
				formPanel.add(new Hidden(PARAGRAPH,paragraph.getId()));
			}
			formPanel.add(new Hidden(GAME_ID,model.getGameId()));
			if (model.getGameKey() != null) { 
				formPanel.add(new Hidden(GAME_KEY,model.getGameKey()));
			}
			formPanel.add(new Hidden(GAME_TITLE,model.getSettings().getBookTitle()));
			formPanel.add(new Hidden(GAME_AUTHORS,model.getSettings().getBookAuthors()));
			form.setMethod(FormPanel.METHOD_POST);
			form.add(formPanel);
			horizontalPanel.add(form);
			horizontalPanel.setCellWidth(form,"1px");
		}
		Button closeButton = new Button(AppLocale.getAppConstants().buttonClose(),new ClickHandler() {
			public void onClick(ClickEvent event) {
				onClose();
			}
		});
		horizontalPanel.add(closeButton);
		horizontalPanel.setCellHorizontalAlignment(closeButton,HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel.setCellWidth(closeButton,"100%");
	}
	
	protected abstract void onClose();

	private void applyEmail() {
		StringBuffer buffer = new StringBuffer();
		buffer.append('\n');
		buffer.append(model.getSettings().getBookTitle());
		buffer.append('\n');
		buffer.append(model.getSettings().getBookAuthors());
		buffer.append('\n');
		buffer.append(appConstants.feedbackBodyHead());
		buffer.append('\n');
		buffer.append(STORY_RATING);
		buffer.append('=');
		buffer.append(String.valueOf(story.getValue()));
		buffer.append('\n');
		buffer.append(COMPLEXITY_RATING);
		buffer.append('=');
		buffer.append(String.valueOf(complexity.getValue()));
		buffer.append('\n');
		buffer.append(APPEARENCE_RATING);
		buffer.append('=');
		buffer.append(String.valueOf(view.getValue()));
		buffer.append('\n');
		if (model.getGameKey() == null) {
			buffer.append(GAME_ID);
			buffer.append('=');
			buffer.append(model.getGameId());
		} else {
			buffer.append(GAME_KEY);
			buffer.append('=');
			buffer.append(model.getGameKey());
		}
		if (note.getText().trim().length()>0) {
			buffer.append('\n');
			buffer.append(NOTE);
			buffer.append('=');
			buffer.append(note.getText().trim());
		}
		submitMail.setHTML(appMessages.feedbackSubmitMail(url,JSONParser.mailToEncode(appConstants.feedbackSubject())+(model.getSettings().getFeedbackEmail() != null ? "&cc="+model.getSettings().getFeedbackEmail():""),JSONParser.mailToEncode(buffer.toString())));
	}

	private  void submitFeedback() {
		formPanel.add(new Hidden(STORY_RATING,String.valueOf(story.getValue())));
		formPanel.add(new Hidden(COMPLEXITY_RATING,String.valueOf(complexity.getValue())));
		formPanel.add(new Hidden(APPEARENCE_RATING,String.valueOf(view.getValue())));
		formPanel.add(new Hidden(NOTE,note.getText().trim()));
		form.submit();
	}
	
	private FeedbackLevel addItem(Grid grid, String title,int row) {
		Label label = new Label(title,false); 
		grid.setWidget(row, 0, label);
		grid.getRowFormatter().setStyleName(row, PlayerStyles.FEEDBACK_ROW);
		FeedbackLevel level = new FeedbackLevel();
		grid.setWidget(row, 1, level);
		return level;
	}
}
