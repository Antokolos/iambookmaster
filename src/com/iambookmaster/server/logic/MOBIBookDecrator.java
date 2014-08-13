package com.iambookmaster.server.logic;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.iambookmaster.client.beans.Greeting;
import com.iambookmaster.client.beans.Paragraph;
import com.iambookmaster.client.beans.ParagraphConnection;
import com.iambookmaster.client.locale.AppConstants;
import com.iambookmaster.client.locale.AppMessages;
import com.iambookmaster.client.model.Model;
import com.iambookmaster.client.model.ParagraphParsingHandler;
import com.iambookmaster.client.paragraph.BookDecorator;

public class MOBIBookDecrator implements BookDecorator {
	private static final String ENCODING = "UTF-8";
	private static final String SERVER_SIDE_FILE = "server.asp";
	private static final Object PARAGRAPH_NUMBER = "n";
	private static final String ENCODING_SERVER = "1251";
	private static final String ENCODING_SERVER_FULL = "windows-1251";
	private StringBuffer buffer;
	private final AppConstants appConstants;
	private final AppMessages appMessages;
	private final Model model;
	private int numberCounter;
	private StringBuilder headerBuffer;
	private StringBuilder titleBuffer;
	
	public MOBIBookDecrator(Model mod,AppConstants appConstants,AppMessages appMessages) {
		this.model = mod;
		this.appConstants = appConstants;
		this.appMessages = appMessages;
		headerBuffer = new StringBuilder();
		headerBuffer.append("<?xml version=\"1.0\" encoding=\"");
		headerBuffer.append(ENCODING);
		headerBuffer.append("\"?>\n");
		headerBuffer.append("<package unique-identifier=\"uid\"><metadata>\n");
		headerBuffer.append("<dc-metadata xmlns:dc=\"http://purl.org/metadata/dublin_core\" xmlns:oebpackage=\"http://openebook.org/namespaces/oeb-package/1.0/\">\n");
		headerBuffer.append("<dc:Identifier id=\"uid\">");
		headerBuffer.append(model.getGameId());
		headerBuffer.append("</dc:Identifier>\n");
		headerBuffer.append("<dc:Title>");
		headerBuffer.append(model.getSettings().getBookTitle());
		headerBuffer.append("</dc:Title>\n");
		headerBuffer.append("<dc:Date>");
		headerBuffer.append(new Date().toString());
		headerBuffer.append("</dc:Date>\n");
		headerBuffer.append("<dc:Language>");
		headerBuffer.append(appConstants.locale());
		headerBuffer.append("</dc:Language>\n");
		headerBuffer.append("</dc-metadata><x-metadata/></metadata><manifest>\n");
		headerBuffer.append("<item id=\"engine\" href=\"");
		headerBuffer.append(SERVER_SIDE_FILE);
		headerBuffer.append("\" media-type=\"text/asp\" />\n");
		headerBuffer.append("<item id=\"default\" href=\"default.htm\" media-type=\"text/x-oeb1-document\" />\n");
		headerBuffer.append("</manifest><spine><itemref idref=\"default\"/></spine><guide></guide></package>\n");
//		titelBuffer.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n");
//		titelBuffer.append("<html><head>\n");
//		titelBuffer.append("<meta content=\"text/html; charset=utf-8\" http-equiv=\"content-type\">");
//		titelBuffer.append("<title>");
//		titelBuffer.append(model.getSettings().getBookTitle());
//		titelBuffer.append("<title></head><body>\n");
	}

	public void appendParagraph(int number,String text,Paragraph paragraph,ArrayList<ParagraphConnection> connections,ArrayList<ParagraphConnection> incomeConnections) {
//		buffer.append("case ");
//		buffer.append(number);
//		buffer.append(":Response.Write('");
//		String txt = text.replace("\n", "<br/>").replace("'", "\\'");
//		buffer.append(txt);
//		buffer.append("');break;\n");
//		buffer.append("Case ");
//		buffer.append(number);
//		buffer.append("\r\n Response.Write('");
//		String txt = text.replace("\n", "<br/>").replace("'", "\\'");
//		buffer.append(txt);
//		buffer.append(number);
//		buffer.append("');\r\n");
		buffer.append("if (counter==");
		buffer.append(number);
//		buffer.append(") Response.Write('");
//		buffer.append("');\r\n");
		buffer.append(")%>");
		String txt = text.replace("\n", "<br/>").replace("'", "\\'");
		buffer.append(txt);
//		buffer.append(number);
		buffer.append("<%\r\n");
		numberCounter = 0;
	}
	
	public String decorateNumber(int number,Paragraph from,Paragraph to, ParagraphConnection connection) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("&nbsp;<a href=\"");
		buffer.append(SERVER_SIDE_FILE);
		buffer.append('?');
		buffer.append(PARAGRAPH_NUMBER);
		buffer.append('=');
		buffer.append(number);
		buffer.append("\">&laquo;");
		buffer.append(++numberCounter);
		buffer.append("&raquo;</a>&nbsp;");
		return buffer.toString();
	}

	public void setStartParagraph(Paragraph paragraph) {
		titleBuffer = new StringBuilder();
		titleBuffer.append("<html><head><meta content=\"text/html; charset=");
		titleBuffer.append(ENCODING);
		titleBuffer.append("\" http-equiv=\"content-type\"></head><body>\n");
		titleBuffer.append("<p>");
		titleBuffer.append(model.getSettings().getBookTitle());
		titleBuffer.append("</p><p>");
		titleBuffer.append(model.getSettings().getBookAuthors());
		titleBuffer.append("</p><p>");
		titleBuffer.append(model.getSettings().getBookDescription());
		titleBuffer.append("</p><p><a href=\"");
		titleBuffer.append(SERVER_SIDE_FILE);
		titleBuffer.append('?');
		titleBuffer.append(PARAGRAPH_NUMBER);
		titleBuffer.append('=');
		titleBuffer.append(model.getStartParagraph().getNumber());
		titleBuffer.append("\">");
		titleBuffer.append(appConstants.decoratorStart());
		titleBuffer.append("</a></p>");
	}

	public void endBook() {
		titleBuffer.append("</body></html>\n");
//		buffer.append("Case Else\r\nResponse.Write('Wrong Paragraph Number');\r\nEnd Select;\r\n%></p>\r\n</body></html>\r\n");
		buffer.append("%></p>\r\n</body></html>\r\n");
//		buffer.append("default:Response.Write('Wrong Paragraph Number');\n}\n%></p>\n</body></html>\n");
	}

	public void startBook() {
		buffer = new StringBuffer();
		//: \
		buffer.append("<%@ Language=JavaScript Codepage=");
		buffer.append(ENCODING_SERVER);
		buffer.append("%><html><head></head><body>\r\n");
		//Response.CharSet=\"utf-8\";\r\n  
		buffer.append("<p><%var counter=");
		buffer.append(model.getStartParagraph().getNumber());
		buffer.append(";\r\n");
		buffer.append("if (typeof Request('");
		buffer.append(PARAGRAPH_NUMBER);
		buffer.append("') != 'undefined')\r\n");
		buffer.append("counter = 1*Request('");
		buffer.append(PARAGRAPH_NUMBER);
		buffer.append("');\r\n");
//		buffer.append("Select Case counter\r\n");
//		buffer.append("switch(counter){");
	}

	public byte[] toBytes() {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ZipOutputStream zos = new ZipOutputStream(outputStream);
	    try {
		    ZipEntry ze= new ZipEntry("output.opf");
			zos.putNextEntry(ze);
			zos.write(headerBuffer.toString().getBytes(ENCODING));
			zos.closeEntry();
			ze= new ZipEntry("default.htm");
			zos.putNextEntry(ze);
			zos.write(titleBuffer.toString().getBytes(ENCODING));
			zos.closeEntry();
			ze= new ZipEntry(SERVER_SIDE_FILE);
			zos.putNextEntry(ze);
			zos.write(buffer.toString().getBytes(ENCODING_SERVER_FULL));
			zos.closeEntry();
			zos.close();
			return outputStream.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public void addGreeting(Greeting greeting) {
//		buffer.append("<p class=\"greeting\">");
//		if (greeting.getImageUrl() != null && greeting.getImageUrl().length()>0) {
//			buffer.append("<img src=\"");
//			buffer.append(greeting.getImageUrl());
//			buffer.append("\"/>");
//		}
//		if (greeting.getUrl() != null && greeting.getUrl().length()>0) {
//			buffer.append("<a href=\"");
//			buffer.append(greeting.getUrl());
//			buffer.append("\">");
//			buffer.append(greeting.getName());
//			buffer.append("</a>");
//		} else {
//			buffer.append(greeting.getName());
//		}
//		if (greeting.getText() != null && greeting.getText().length()>0) {
//			buffer.append("<p class=\"greetingText\">");
//			buffer.append(greeting.getText());
//			buffer.append("</p>");
//		}
//		buffer.append("</p>");
	}

	public void endGreeting() {
//		buffer.append("</p>");
	}

	public void startGreeting() {
//		buffer.append("<p class=\"greetingBox\">");
//		buffer.append(appConstants.decoratorStartGreetings());
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
