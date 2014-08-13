package com.iambookmaster.server.logic;

import com.iambookmaster.client.beans.ObjectBean;
import com.iambookmaster.client.beans.Paragraph;
import com.iambookmaster.client.beans.ParagraphConnection;
import com.iambookmaster.client.locale.AppConstants;
import com.iambookmaster.client.locale.AppMessages;
import com.iambookmaster.client.model.Model;

public abstract class AbstractModelProcessor {

	private StringBuffer errors=new StringBuffer();
	private Model model;
	protected final AppConstants appConstants;
	protected final AppMessages appMessages;
	
	protected Model getModel() {
		return model;
	}
	
	public AbstractModelProcessor(Model model, AppConstants appConstants,AppMessages appMessages) {
		super();
		this.model = model;
		this.appConstants = appConstants;
		this.appMessages = appMessages;
	}

	protected void appendObject(ObjectBean object) {
		errors.append(appMessages.serverErrorItem(object.getName()));
	}

	protected void appendParagraph(Paragraph paragraph) {
		errors.append(getParagraphDescription(paragraph));
	}

	private String getParagraphDescription(Paragraph paragraph) {
		if (model.getSettings().isShowParagraphNumbers()) {
			return appMessages.serverErrorParagraphWithNumber(
					paragraph.isFail() ? appConstants.serverParagraphFail(): 
					paragraph.isSuccess() ? appConstants.serverParagraphSuccess():
					appConstants.serverParagraphNormal(),
					paragraph.getNumber(),
					paragraph.getName());
		} else {
			return appMessages.serverErrorParagraphNoNumber(
					paragraph.isFail() ? appConstants.serverParagraphFail(): 
					paragraph.isSuccess() ? appConstants.serverParagraphSuccess():
					appConstants.serverParagraphNormal(),
					paragraph.getName());
		}
	}
	protected void appendParagraphConnection(ParagraphConnection connection) {
		errors.append(appMessages.serverErrorParagraphConnection(
				connection.isBothDirections() ? appConstants.serverErrorConnectionTwoWay() : appConstants.serverErrorConnectionOneWay(),
				getParagraphDescription(connection.getFrom()),
				getParagraphDescription(connection.getTo())));
	}
	
	protected void clearErrors() {
		errors.setLength(0);
	}

	protected void appendErrorText(String text) {
		errors.append(text);
	}
	
	protected void appendErrorText(int code) {
		errors.append(code);
	}

	protected void appendErrorEndLine() {
		errors.append('\n');
	}

	public String getErrors() {
		if (errors.length()==0) {
			return null;
		} else {
			return errors.toString();
		}
	}
	
	

}
