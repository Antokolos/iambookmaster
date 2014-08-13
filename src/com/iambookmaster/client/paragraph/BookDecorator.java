package com.iambookmaster.client.paragraph;

import java.util.ArrayList;

import com.iambookmaster.client.beans.Greeting;
import com.iambookmaster.client.beans.Paragraph;
import com.iambookmaster.client.beans.ParagraphConnection;
import com.iambookmaster.client.model.ParagraphParsingHandler;

public interface BookDecorator {

	public void setStartParagraph(Paragraph paragraph);

	public String decorateNumber(int number,Paragraph from,Paragraph to, ParagraphConnection connection);

	public void appendParagraph(int number, String text, Paragraph paragraph, ArrayList<ParagraphConnection> outcomeConnections, ArrayList<ParagraphConnection> incomeConnections);

	public void startBook();

	public void endBook();

	public void addGreeting(Greeting greeting);

	public void startGreeting();

	public void endGreeting();

	public boolean isPlayerMode();

	public ParagraphParsingHandler getParagraphParsingHandler();

	public boolean isHideAbsoluteModificators();

	public byte[] toBytes();
	
}