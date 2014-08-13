package com.iambookmaster.server.logic;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import com.iambookmaster.client.beans.Greeting;
import com.iambookmaster.client.beans.Paragraph;
import com.iambookmaster.client.beans.ParagraphConnection;
import com.iambookmaster.client.common.ColorProvider;
import com.iambookmaster.client.locale.AppConstants;
import com.iambookmaster.client.locale.AppMessages;
import com.iambookmaster.client.model.Model;
import com.iambookmaster.client.model.ParagraphParsingHandler;
import com.iambookmaster.client.paragraph.BookDecorator;

public class HTMLBookDecrator implements BookDecorator {
	public static final String ENCODING = "UTF-8";
	private StringBuffer buffer;
	private final AppConstants appConstants;
	private final AppMessages appMessages;
	private final Model model;
	
	public HTMLBookDecrator(Model mod,AppConstants appConstants,AppMessages appMessages) {
		this.model = mod;
		this.appConstants = appConstants;
		this.appMessages = appMessages;
		buffer = new StringBuffer();
		buffer.append("<html><head>\n");
		buffer.append("<meta http-equiv=\"content-type\" content=\"text/html;charset=utf-8\" />\n");
		buffer.append("<title>");
		buffer.append(model.getSettings().getBookTitle());
		buffer.append(' ');
		buffer.append(model.getSettings().getBookAuthors());
		buffer.append("</title>\n");
		buffer.append("<style type=\"text/css\">\n);");
		buffer.append("body {\n");
		buffer.append("background: ");
		if (model.getSettings().getTextBackground()==0) {
			buffer.append("white");
		} else {
			buffer.append(ColorProvider.getColorName(model.getSettings().getTextBackground()));
		}
		buffer.append(";\n");
		buffer.append("color: ");
		buffer.append(ColorProvider.getColorName(model.getSettings().getTextColor()));
		buffer.append(";\n");
		buffer.append("}\n");
		buffer.append(".title {\n");
		buffer.append("font-size: 2em;\n");
		buffer.append("font-weight: bold;\n");
		buffer.append("}\n");
		buffer.append(".authors {\n");
		buffer.append("font-size: 1.3em;\n");
		buffer.append("font-weight: bold;\n");
		buffer.append("}\n");
		buffer.append(".description {\n");
		buffer.append("font-size: 1em;\n");
		buffer.append("}\n");
		buffer.append(".paragraph {\n");
		buffer.append("font-size: 1em;\n");
		buffer.append("}\n");
		buffer.append(".paragraphNumber {\n");
		buffer.append("font-weight: bold;\n");
		buffer.append("}\n");
		buffer.append(".paragraphIncomeNumbers {\n");
		buffer.append("}\n");
		buffer.append(".paragraphLink {\n");
		buffer.append("}\n");
		buffer.append(".greetingBox {\n");
		buffer.append("}\n");
		buffer.append(".greeting {\n");
		buffer.append("}\n");
		buffer.append(".greetingText {\n");
		buffer.append("}\n");
		buffer.append(".iambm {font-size: smaller;\n");
		buffer.append("}\n");
		buffer.append(".rulesBox {\n");
		buffer.append("}\n");
		buffer.append("</style></head><body>\n");
		buffer.append("<div class=\"title\">");
		buffer.append(model.getSettings().getBookTitle());
		buffer.append("</div>\n");
		buffer.append("<div class=\"authors\">");
		buffer.append(model.getSettings().getBookAuthors());
		buffer.append("</div>\n");
		buffer.append("<p class=\"description\">");
		buffer.append(model.getSettings().getBookDescription());
		buffer.append("</p>\n");
		buffer.append("<p class=\"iambm\">");
		buffer.append(appConstants.bookCreatedBy());
		buffer.append("</p>\n");
		if (model.getBookRules().length()>0) {
			buffer.append("<p class=\"rulesBox\">");
			buffer.append(model.getBookRules().replace("\n", "<br/>\n"));
			buffer.append("</p>\n");
		}
	}

	public void appendParagraph(int number,String text,Paragraph paragraph,ArrayList<ParagraphConnection> connections,ArrayList<ParagraphConnection> incomeConnections) {
		buffer.append("<p class=\"paragraph\"><a name=\"par");
		buffer.append(number);
		buffer.append("\"><span class=\"paragraphNumber\">");
		buffer.append(number);
		buffer.append("</span>&nbsp;</a>");
		
		if (model.getSettings().isHiddenUsingObjects() && incomeConnections != null && incomeConnections.size()>0){
			boolean first=true;
			for (ParagraphConnection connection : incomeConnections) {
				if (connection.getType()==ParagraphConnection.TYPE_NORMAL && connection.getObject() != null) {
					if (first) {
						buffer.append("<span class=\"paragraphIncomeNumbers\">[");
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
				buffer.append("]</span>&nbsp;");
			}
		}
		buffer.append(text.replace("\n", "<br/>\n"));
		buffer.append("</p>\n");
	}

	public String decorateNumber(int number,Paragraph from,Paragraph to, ParagraphConnection connection) {
		StringBuffer buffer = new StringBuffer("&nbsp;<a href=\"#par");
		buffer.append(number);
		buffer.append("\" class=\"paragraphLink\">");
		buffer.append(number);
		buffer.append("</a>&nbsp;");
		return buffer.toString();
	}

	public void setStartParagraph(Paragraph paragraph) {
		buffer.append("<p class=\"paragraph\"><a href=\"#par");
		buffer.append(paragraph.getNumber());
		buffer.append("\">");
		buffer.append(appConstants.decoratorStart());
		buffer.append("</a></p>");
	}

	public void endBook() {
		buffer.append("<body></html>\n");
	}

	public void startBook() {
	}

	public byte[] toBytes() {
		try {
			return buffer.toString().getBytes(ENCODING);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public void addGreeting(Greeting greeting) {
		buffer.append("<p class=\"greeting\">");
		if (greeting.getImageUrl() != null && greeting.getImageUrl().length()>0) {
			buffer.append("<img src=\"");
			buffer.append(greeting.getImageUrl());
			buffer.append("\"/>");
		}
		if (greeting.getUrl() != null && greeting.getUrl().length()>0) {
			buffer.append("<a href=\"");
			buffer.append(greeting.getUrl());
			buffer.append("\">");
			buffer.append(greeting.getName());
			buffer.append("</a>");
		} else {
			buffer.append(greeting.getName());
		}
		if (greeting.getText() != null && greeting.getText().length()>0) {
			buffer.append("<p class=\"greetingText\">");
			buffer.append(greeting.getText());
			buffer.append("</p>");
		}
		buffer.append("</p>");
	}

	public void endGreeting() {
		buffer.append("</p>");
	}

	public void startGreeting() {
		buffer.append("<p class=\"greetingBox\">");
		buffer.append(appConstants.decoratorStartGreetings());
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
}
