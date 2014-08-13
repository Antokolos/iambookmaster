package com.iambookmaster.client.importer;

import com.iambookmaster.client.beans.Paragraph;

public interface TextBookImporterListener {
	
	public void error(String text);
	public void warning(String text);
	public void info(String text);
	public void startStage(String text);
	public void endStage(String text);
	public void startParseParagraph(Paragraph paragraph);
	public void endParseParagraph(Paragraph paragraph);
	public void parseParagraphAddText(String text);
	public void parseParagraphAddLink(String link, Paragraph paragraph);
	public void parseParagraphAddError(String text);

}
