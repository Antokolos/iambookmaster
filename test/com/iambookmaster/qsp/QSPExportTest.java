package com.iambookmaster.qsp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.TestCase;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.iambookmaster.client.beans.ObjectBean;
import com.iambookmaster.client.beans.Paragraph;
import com.iambookmaster.client.beans.ParagraphConnection;
import com.iambookmaster.client.common.JSONParser;
import com.iambookmaster.client.editor.ModelPersist;
import com.iambookmaster.client.exceptions.JSONException;
import com.iambookmaster.client.locale.AppConstants;
import com.iambookmaster.client.locale.AppMessages;
import com.iambookmaster.client.model.Model;
import com.iambookmaster.client.paragraph.BookCreator;
import com.iambookmaster.client.paragraph.BookCreatorListener;
import com.iambookmaster.client.paragraph.BookDecorator;
import com.iambookmaster.server.LocalMessages;
import com.iambookmaster.server.XMLModelParser;
import com.iambookmaster.server.logic.QSPBookDecrator;

public class QSPExportTest extends TestCase {

	protected AppConstants appConstants;
	protected AppMessages appMessages;
	private static String locale="ru";
	
	private int contentCounter;

	public void test1() throws Exception {
		
//		perform("attack2attack");
//		perform("attack2defence");
//		perform("attack2attackWeak");
//		perform("attack2defenceFatal");
//		perform("attack2defenceFatalMax");
//		perform("1round");
//		perform("3npc");
		
//		perform("Sprites");
//		perform("Kererleys");
		perform("Dragon");
		
	}
	
	@Override
	protected void setUp() throws Exception {
		LocalMessages messages = new LocalMessages();
		messages.initialize(locale);
		appConstants = LocalMessages.getInstance(AppConstants.class, locale);
		appMessages = LocalMessages.getInstance(AppMessages.class, locale);
	}
	
	public void perform(String name) throws Exception {
		Model model = createModelFromJSON(name+".xml");
		ArrayList<Paragraph> list = model.getParagraphs();
		Paragraph[] book = new Paragraph[list.size()];
		for (Paragraph paragraph : list) {
			assertNotSame(paragraph.getNumber(),0);
			assertNull(book[paragraph.getNumber()-1]);
			book[paragraph.getNumber()-1]=paragraph;
		}
		BookCreator creator = new BookCreator(model);
		BookDecorator decrator = getBookDecorator(model);
		creator.createText(book, new BookCreatorListener(){
			public void algorithmError(int code) {
				fail("algorithmError "+code);
				
			}
			public void allIterationsFailed() {
				fail("allIterationsFailed");
			}

			public boolean checkTimiout() {
				return false;
			}

			public void iterationFailed(int fail, int total) {
			}

			public void noSupported() {
				fail("noSupported");
			}

			public void numberNotSet(ObjectBean objectBean) {
				fail("numberNotSet in object: "+objectBean.getName());
			}

			public void numberNotSet(Paragraph paragraph) {
				fail("numberNotSet in paragraph: "+paragraph.getName());
			}

			public void numberTooLarge(Paragraph paragraph, int max) {
				fail("numberTooLarge in paragraph: "+paragraph.getName());
			}

			public void numbersDuplicated(Paragraph paragraph,Paragraph paragraph2) {
				fail("numbersDuplicated in paragraphs: "+paragraph.getName()+" and "+paragraph2.getName());
			}

			public void tooManyObjects() {
				fail("tooManyObjects");
			}

			public void wrongObjectSecretKey(ParagraphConnection connection) {
				fail("wrongObjectSecretKey, object:"+connection.getObject().getName()+
						" ("+connection.getObject().getKey()+") for connection from '"+
						connection.getFrom().getName()+"' to '"+connection.getTo().getName()+"'");
			}
			
		}, decrator);
		FileOutputStream fileWriter = getOutputStream();
		try {
			fileWriter.write(decrator.toBytes());
		} finally {
			fileWriter.close();
		}
	}
	
	protected BookDecorator getBookDecorator(Model model) {
		return new QSPBookDecrator(model,appConstants,appMessages);
	}

	protected FileOutputStream getOutputStream() throws IOException {
		return new FileOutputStream("qsp/module.qsp");
	}

	protected ModelPersist createModelFromJSON(String file) throws JSONException, ParserConfigurationException, SAXException, IOException {
		InputStream stream = getClass().getResourceAsStream(file);
		assertNotNull("Cannot load file "+file,stream);
		ModelPersist model;
		try {
			model = new ModelPersist(appConstants,appMessages);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setValidating(false);
			dbf.setNamespaceAware(false);
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource inputSource = new InputSource(stream);
			Document result = db.parse(inputSource);
			JSONParser parser = new XMLModelParser();
			model.restore(result,parser);
		} finally {
			if (stream != null) {
				stream.close();
			}
		}
		return model;
	}
	
	public String copyContent(String name, String source, String outpath) throws Exception {
		StringBuilder builder = new StringBuilder(source);
		builder.append(File.separator);
		builder.append(name);
		File file = new File(builder.toString());
		if (file.exists()==false) {
			assertTrue(builder.append(" does not exist").toString(),file.exists());
		}
		if (file.isFile()==false) {
			assertTrue(builder.append(" is not a file").toString(),file.isFile());
		}
		File out = new File(outpath);
		if (out.exists()==false) {
			assertTrue("File does not exist "+outpath,out.exists());
		}
		if (out.isDirectory()==false) {
			assertTrue("No a folder "+outpath,out.isDirectory());
		}
		builder.setLength(0);
		builder.append(out.getAbsolutePath());
		builder.append(File.separator);
		int l = builder.length();
		builder.append('r');
		builder.append(String.valueOf(contentCounter++));
		int ext = name.lastIndexOf('.');
		if (ext>0) {
			builder.append(name.substring(ext));
		}
		out = new File(builder.toString());
		copyFile(file, out);
		return builder.substring(l);
	}
	
	  public static void copyFile(File in, File out) throws Exception {
		    FileInputStream fis  = new FileInputStream(in);
		    FileOutputStream fos = new FileOutputStream(out);
		    try {
		        byte[] buf = new byte[1024];
		        int i = 0;
		        while ((i = fis.read(buf)) != -1) {
		            fos.write(buf, 0, i);
		        }
		    } 
		    catch (Exception e) {
		        throw e;
		    }
		    finally {
		        if (fis != null) fis.close();
		        if (fos != null) fos.close();
		    }
	  }
	
	public static void clearFolder(String outputPath) {
		File folder = new File(outputPath);
		for (File file : folder.listFiles()) {
			file.delete();
		}
	}
		
}