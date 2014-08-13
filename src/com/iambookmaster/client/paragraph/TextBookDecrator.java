package com.iambookmaster.client.paragraph;

import java.util.ArrayList;

import com.iambookmaster.client.beans.Greeting;
import com.iambookmaster.client.beans.Paragraph;
import com.iambookmaster.client.beans.ParagraphConnection;
import com.iambookmaster.client.locale.AppConstants;
import com.iambookmaster.client.locale.AppMessages;
import com.iambookmaster.client.model.Model;
import com.iambookmaster.client.model.ParagraphParsingHandler;

public class TextBookDecrator implements BookDecorator {
	
	protected StringBuffer buffer;
	
	private AppConstants appConstants;
	private AppMessages appMessages;
	private Model model;
	public TextBookDecrator(Model mod,AppConstants appConstants,AppMessages appMessages) {
		this.model = mod;
		this.appConstants = appConstants;
		this.appMessages = appMessages;
		buffer = new StringBuffer();
		buffer.append(model.getSettings().getBookTitle());
		buffer.append('\n');
		buffer.append('\n');
		buffer.append(model.getSettings().getBookAuthors());
		buffer.append('\n');
		buffer.append(model.getSettings().getBookDescription());
		buffer.append('\n');
		buffer.append(appConstants.bookCreatedByText());
		buffer.append('\n');
		if (model.getBookRules().length() >0) {
			buffer.append('\n');
			buffer.append(appConstants.bookRules());
			buffer.append('\n');
			buffer.append(model.getBookRules());
			buffer.append('\n');
		}
	}

	public void setStartParagraph(Paragraph paragraph) {
		buffer.append(appMessages.textDecoratorStartParagraph(paragraph.getNumber()));
		buffer.append("\n\n");
	}

	public String decorateNumber(int number,Paragraph from,Paragraph to, ParagraphConnection connection) {
		return "<"+number+">";
	}

	public void appendParagraph(int number,String text,Paragraph paragraph,ArrayList<ParagraphConnection> connections,ArrayList<ParagraphConnection> incomeConnections) {
		buffer.append(number);
		buffer.append(". ");
		
		if (model.getSettings().isHiddenUsingObjects() && incomeConnections != null && incomeConnections.size()>0){
			boolean first=true;
			for (ParagraphConnection connection : incomeConnections) {
				if (connection.getType()==ParagraphConnection.TYPE_NORMAL && connection.getObject() != null) {
					if (first) {
						buffer.append('[');
						first = false;
					} else {
						buffer.append(',');
					}
					if (connection.getTo()==paragraph) {
						buffer.append(connection.getFrom().getNumber());
					} else {
						buffer.append(connection.getTo().getNumber());
					}
				}
			}
			if (first==false) {
				buffer.append("] ");
			}
		}
		buffer.append(text);
		buffer.append("\n\n");
	}

	public byte[] toBytes() {
		return null;
	}


	public void endBook() {
	}

	public void startBook() {
	}

	public void addGreeting(Greeting greeting) {
		buffer.append(greeting.getName());
		if (greeting.getUrl() != null && greeting.getUrl().length()>0) {
			buffer.append(" (");
			buffer.append(greeting.getUrl());
			buffer.append(")");
		}
		buffer.append('\n');
		if (greeting.getText() != null && greeting.getText().length()>0) {
			buffer.append(greeting.getText());
			buffer.append('\n');
		}
		buffer.append('\n');
	}

	public void endGreeting() {
		buffer.append('\n');
	}

	public void startGreeting() {
		buffer.append(appConstants.decoratorStartGreetings());
		buffer.append('\n');
	}

	public boolean isPlayerMode() {
		return false;
	}

	public ParagraphParsingHandler getParagraphParsingHandler() {
		return null;
	}

	public boolean isHideAbsoluteModificators() {
		return true;
	}

	public String toText() {
		return buffer.toString();
	}
}
