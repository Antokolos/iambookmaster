package com.iambookmaster.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.iambookmaster.client.ServerExchangePanel;
import com.iambookmaster.client.beans.ObjectBean;
import com.iambookmaster.client.beans.Paragraph;
import com.iambookmaster.client.beans.ParagraphConnection;
import com.iambookmaster.client.common.JSONBuilder;
import com.iambookmaster.client.editor.ModelPersist;
import com.iambookmaster.client.exceptions.JSONException;
import com.iambookmaster.client.locale.AppConstants;
import com.iambookmaster.client.locale.AppMessages;
import com.iambookmaster.client.model.Model;
import com.iambookmaster.client.paragraph.BookCreator;
import com.iambookmaster.client.paragraph.BookCreatorListener;
import com.iambookmaster.client.paragraph.BookDecorator;
import com.iambookmaster.server.beans.JPABook;
import com.iambookmaster.server.beans.JPABookVersion;
import com.iambookmaster.server.beans.JPAClob;
import com.iambookmaster.server.beans.JPAUser;
import com.iambookmaster.server.dao.BooksDAO;
import com.iambookmaster.server.dao.DAO;
import com.iambookmaster.server.logic.QSPBookDecrator;
import com.iambookmaster.server.logic.URQBookDecrator;
import com.iambookmaster.server.tags.LoadModelTag;

public class LoadModelServlet extends AbstractServlet {
	
	private static final long serialVersionUID = 1L;

	private static final Logger log = Logger.getLogger(LoadModelServlet.class.getName());

	public static final String FIELD_BOOK_ID = "b";
	public static final String FIELD_BOOK_VERSION_ID = "v";
	public static final String FIELD_TYPE = "t";

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		UserService userService = UserServiceFactory.getUserService();
		AppConstants appConstants = LocalMessages.getInstance(AppConstants.class, LocalMessages.getLocale(req,resp));
		AppMessages appMessages = LocalMessages.getInstance(AppMessages.class, LocalMessages.getLocale(req,resp));
		if (userService.isUserLoggedIn()==false) {
			sendReplay(resp,ServerExchangePanel.ERROR_NO_LOGIN,appConstants.serverNotLoggedIn());
			return;
		}
		PersistenceManager em = getPM(req);
		JPABook book;
		JPABookVersion version;
		String type=req.getParameter(FIELD_TYPE);
		Object result = getBookOrVersionAndValidate(type,em,userService,req,resp,appConstants);
		if (result instanceof JPABook) {
			book = (JPABook) result;
			version = null;
		} else if (result instanceof JPABookVersion) {
			version = (JPABookVersion) result;
			book = null;
		} else {
			//error
			return;
		}
		BooksDAO booksDAO = DAO.getBookDAO();
		if (type==null || LoadModelTag.TYPE_EDITOR.equals(type)) {
			//Model for editor
			sendModelToEditor(book,version,req,resp,appConstants,appMessages);
		} else if (LoadModelTag.TYPE_HTML.equals(type)) {
			sendModelToHTML(book,version,req,resp);
		} else if (LoadModelTag.TYPE_TXT.equals(type)) {
			String data;
			if (version== null) {
				data = booksDAO.getCLOB(em, book, JPAClob.TYPE_TEXT);
			} else {
				data = booksDAO.getCLOB(em, version, JPAClob.TYPE_TEXT);
			}
			sendModelToFile(data,req,resp,Translit.toTranslit(book.getName().replace(' ', '_'))+".txt","UTF-8");
		} else if (LoadModelTag.TYPE_URQ.equals(type)) {
	    	try {
				ModelPersist model = loadModel(resp,em,book,version,booksDAO,appConstants,appMessages);
				BookDecorator decorator = new URQBookDecrator(model,appConstants,appMessages);
				sendModelToFile(model,decorator,req,resp,Translit.toTranslit(book.getName().replace(' ', '_'))+".qst",URQBookDecrator.ENCODING,appConstants,appMessages);
			} catch (Exception e) {
				e.printStackTrace();
	        	log.log(Level.SEVERE,e.getMessage(),e);
	        	resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			}
		} else if (LoadModelTag.TYPE_URQ_SHORT.equals(type)) {
	    	try {
				ModelPersist model = loadModel(resp,em,book,version,booksDAO,appConstants,appMessages);
				model.getSettings().setDisableAudio(true);
				model.getSettings().setDisableImages(true);
				BookDecorator decorator = new URQBookDecrator(model,appConstants,appMessages);
				sendModelToFile(model,decorator,req,resp,Translit.toTranslit(book.getName().replace(' ', '_'))+".qst",URQBookDecrator.ENCODING,appConstants,appMessages);
			} catch (Exception e) {
				e.printStackTrace();
	        	log.log(Level.SEVERE,e.getMessage(),e);
	        	resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			}
		} else if (LoadModelTag.TYPE_QSP.equals(type)) {
	    	try {
				ModelPersist model = loadModel(resp,em,book,version,booksDAO,appConstants,appMessages);
				BookDecorator decorator = new QSPBookDecrator(model,appConstants,appMessages);
				sendModelToFile(model,decorator,req,resp,Translit.toTranslit(book.getName().replace(' ', '_'))+".qsp",QSPBookDecrator.ENCODING,appConstants,appMessages);
			} catch (Exception e) {
				e.printStackTrace();
	        	log.log(Level.SEVERE,e.getMessage(),e);
	        	resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			}
		} else if (LoadModelTag.TYPE_PROJECT.equals(type)) {
			sendModelToProject(em,book,version,req,resp);
		}
	}

	private void sendModelToFile(Model model,BookDecorator decorator, HttpServletRequest req, HttpServletResponse resp, String fileName, String encoding,final AppConstants constants,final AppMessages messages) throws IOException {
		BookCreator creator = new BookCreator(model);
		List<Paragraph> list = model.getParagraphs();
		Paragraph[] book = new Paragraph[list.size()];
		for (Paragraph paragraph : list) {
//			assertNotSame(paragraph.getNumber(),0);
//			assertNull(book[paragraph.getNumber()-1]);
			book[paragraph.getNumber()-1]=paragraph;
		}
		creator.createText(book, new BookCreatorListener(){
			public void algorithmError(int code) {
				fail(messages.validatorAlgorithmError(code));
			}
			private void fail(String error) {
				// TODO Auto-generated method stub
			}
			public void allIterationsFailed() {
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
				fail(messages.validationObjectDoesNotHaveSecretKey(objectBean.getName()));
			}

			public void numberNotSet(Paragraph paragraph) {
				fail(messages.validationParagraphNumberNotSet(paragraph.getName()));
			}

			public void numberTooLarge(Paragraph paragraph, int max) {
				fail(messages.validationParagraphNumberOutOfRange(paragraph.getName(),max));
			}

			public void numbersDuplicated(Paragraph paragraph,Paragraph paragraph2) {
				fail(messages.validationParagraphNameDuplicated(paragraph.getName(),paragraph.getNumber(),paragraph2.getName()));
			}

			public void tooManyObjects() {
				fail("tooManyObjects");
			}

			public void wrongObjectSecretKey(ParagraphConnection connection) {
				fail(messages.validationObjectHasWrongSecretKey(connection.getObject().getName(),connection.getObject().getKey(),
						connection.getFrom().getNumber(),connection.getFrom().getName(),
						connection.getTo().getNumber(),connection.getTo().getName()));
			}
			
		}, decorator);
		String module = decorator.toString();
		sendModelToFile(module,req,resp,fileName,encoding);
		
	}

	private ModelPersist loadModel(HttpServletResponse resp,PersistenceManager em, JPABook book, JPABookVersion version, BooksDAO booksDAO, AppConstants appConstants,AppMessages appMessages) throws JSONException, ParserConfigurationException, SAXException, IOException {
		String data;
		if (version== null) {
			data = booksDAO.getCLOB(em, book, JPAClob.TYPE_MODEL);
		} else {
			data = booksDAO.getCLOB(em, version, JPAClob.TYPE_MODEL);
		}
		Document result;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(false);
        dbf.setNamespaceAware(false);
        DocumentBuilder db = dbf.newDocumentBuilder();
        InputSource inputSource = new InputSource(new StringReader(data));
        result = db.parse(inputSource);
    	ModelPersist model = new ModelPersist(appConstants,appMessages);
    	XMLModelParser parser = new XMLModelParser();
		model.restore(result, parser);
		return model;
	}

	protected Object getBookOrVersionAndValidate(String type, PersistenceManager em, UserService userService, HttpServletRequest req, HttpServletResponse resp, AppConstants appConstants) throws IOException{
		String bookId = req.getParameter(FIELD_BOOK_ID);
		BooksDAO booksDAO = DAO.getBookDAO();
		JPABook book;
		JPABookVersion version;
		if (bookId==null) {
			String bookVersionId = req.getParameter(FIELD_BOOK_VERSION_ID);
			Key bookVersionKey=null;
			if (bookVersionId == null) {
				sendError(type,resp,ServerExchangePanel.ERROR_NO_BOOK_ID,appConstants.serverNoBookID());
				return null;
			} 
			try {
				bookVersionKey = KeyFactory.stringToKey(bookVersionId);
			} catch (Exception e) {
				sendError(type,resp,ServerExchangePanel.ERROR_INVALID_BOOK_VERSION_ID,appConstants.serverInvalidBookVersionID());
				return null;
			}
			version = booksDAO.findBookVersion(em,bookVersionKey);
			if (version==null) {
				sendError(type,resp,ServerExchangePanel.ERROR_BOOK_VERSION_NOT_FOUND,appConstants.serverUnknownBookVersion());
				return null;
			}
			book = booksDAO.findBook(em, version.getBook());
		} else {
			version = null;
			Key bookKey;
			try {
				bookKey = KeyFactory.stringToKey(bookId);
			} catch (Exception e) {
				sendError(type,resp,ServerExchangePanel.ERROR_INVALID_BOOK_ID,appConstants.serverInvalidBookID());
				return null;
			}
			book = booksDAO.findBook(em, bookKey);
			if (book==null) {
				sendError(type,resp,ServerExchangePanel.ERROR_BOOK_NOT_FOUND,appConstants.serverBookNotFound());
				return null;
			}
		}
		
		if (userService.isUserAdmin()==false) {
			//administrator can load anything, other - owner only
			JPAUser user = DAO.getUsersDAO().findOrCreateUser(em, userService.getCurrentUser());
			if (user.getId().equals(book.getOwner())==false) {
				sendError(type,resp,ServerExchangePanel.ERROR_NOT_OWNER,appConstants.serverNotOwnerOfBook());
				return null;
			}
		}
		if (book==null) {
			return version;
		} else {
			return book;
		}
	}

	protected void sendError(String type,HttpServletResponse resp, int code,	String message) throws IOException {
		if (type==null || LoadModelTag.TYPE_EDITOR.equals(type)) {
			sendReplay(resp, code, message);
		} else {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
		}
	}

	private void sendModelToProject(PersistenceManager em, JPABook book, JPABookVersion version, HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String data;
		BooksDAO booksDAO = DAO.getBookDAO();
		if (version== null) {
			data = booksDAO.getCLOB(em, book, JPAClob.TYPE_MODEL);
		} else {
			data = booksDAO.getCLOB(em, version, JPAClob.TYPE_MODEL);
		}
		Document result;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setValidating(false);
            dbf.setNamespaceAware(false);
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource inputSource = new InputSource(new StringReader(data));
            result = db.parse(inputSource);
        }  catch (Exception e) {
        	log.log(Level.SEVERE,e.getMessage(),e);
    		resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,e.getMessage());
    		return;
        }
    	ModelPersist model = new ModelPersist(LocalMessages.getInstance(AppConstants.class, LocalMessages.getLocale(req,resp)),LocalMessages.getInstance(AppMessages.class, LocalMessages.getLocale(req,resp)));
    	XMLModelParser parser = new XMLModelParser();
    	try {
			model.restore(result, parser);
		} catch (Throwable e) {
        	log.log(Level.SEVERE,e.getMessage(),e);
    		resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,e.getMessage());
    		return;
		}
		JSONBuilder builder = JSONBuilder.getStartInstance();
		model.toJSON(Model.EXPORT_ALL, builder);
		sendModelToFile(builder.toString(),req,resp,Translit.toTranslit(book.getName().replace(' ', '_'))+".txt","UTF-8");
	}

//	private void sendModelToTXT(PersistenceManager em, JPABook book, JPABookVersion version, HttpServletRequest req, HttpServletResponse resp) throws IOException {
//		resp.setContentType("text/plain");
//		resp.setCharacterEncoding("UTF-8");
//		PrintWriter writer = resp.getWriter();
//		String data;
//		BooksDAO booksDAO = DAO.getBookDAO();
//		if (version== null) {
//			data = booksDAO.getCLOB(em, book, JPAClob.TYPE_MODEL);
//		} else {
//			data = booksDAO.getCLOB(em, version, JPAClob.TYPE_MODEL);
//		}
//		writer.append(data);
//		writer.flush();
//		
//	}

	private void sendModelToFile(String content,HttpServletRequest req, HttpServletResponse resp, String fileName,String enconding) throws IOException {
		resp.setContentType("text/plain");
		resp.setCharacterEncoding(enconding);
		resp.setHeader("Content-Disposition", "attachment;filename=" + fileName);
		PrintWriter writer = resp.getWriter();
		writer.write(content);
		writer.flush();
		writer.close();
	}

	private void sendModelToHTML(JPABook book, JPABookVersion version, HttpServletRequest req, HttpServletResponse resp)throws IOException  {
		resp.setContentType("text/html");
		resp.setCharacterEncoding("UTF-8");
		PrintWriter writer = resp.getWriter();
		BooksDAO booksDAO = DAO.getBookDAO();
		PersistenceManager em = getPM(req);
		if (version == null) {
			writer.append(booksDAO.getCLOB(em, book, JPAClob.TYPE_HTML));
		} else {
			writer.append(booksDAO.getCLOB(em, version, JPAClob.TYPE_HTML));
		}
		writer.flush();
	}

	private void sendModelToEditor(JPABook book, JPABookVersion version, HttpServletRequest req, HttpServletResponse resp,AppConstants appConstants,AppMessages appMessages) throws IOException {
		String modelXML;
		BooksDAO booksDAO = DAO.getBookDAO();
		PersistenceManager em = getPM(req);
		if (version == null) {
			modelXML = booksDAO.getCLOB(em, book, JPAClob.TYPE_MODEL);
		} else {
			modelXML = booksDAO.getCLOB(em, version, JPAClob.TYPE_MODEL);
		}
		Document result;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setValidating(false);
            dbf.setNamespaceAware(false);
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource inputSource = new InputSource(new StringReader(modelXML));
            result = db.parse(inputSource);
        }  catch (Exception e) {
        	log.log(Level.SEVERE,e.getMessage(),e);
    		resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,e.getMessage());
//    		sendReplay(resp,ServerExchangePanel.ERROR_INVALID_MODEL,e.getMessage());
    		return;
        }
    	ModelPersist model = new ModelPersist(appConstants,appMessages);
    	XMLModelParser parser = new XMLModelParser();
    	try {
			model.restore(result, parser);
		} catch (Throwable e) {
        	log.log(Level.SEVERE,e.getMessage(),e);
    		resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,e.getMessage());
//    		sendReplay(resp,ServerExchangePanel.ERROR_INVALID_MODEL,e.getMessage());
    		return;
		}
		JSONBuilder builder = JSONBuilder.getStartInstance();
		model.toJSON(Model.EXPORT_ALL, builder);
		sendReplay(resp,ServerExchangePanel.LOAD_OK,"loaded",builder.toString());
	}

	private void sendReplay(HttpServletResponse resp, int code, String message) {
		sendReplay(resp,code,message,null);
	}

	private void sendReplay(HttpServletResponse resp, int code, String message, String data) {
		try {
//			content-type: 
			resp.setContentType("application/x-javascript");
			resp.setCharacterEncoding("UTF-8");
			PrintWriter writer = resp.getWriter();
			writer.write("//Loading model of Game-Book\n");
			writer.write("var result = {};\nresult.");
			if (data != null) {
				writer.write(ServerExchangePanel.FIELD_DATA);
				writer.write('=');
				writer.write(data);
				writer.write(";\n");
			}
			if (message != null) {
				writer.write("result.");
				writer.write(ServerExchangePanel.FIELD_MESSAGE);
				writer.write("='");
				writer.write(message);
				writer.write("';\n");
			}
			writer.write("result.");
			writer.write(ServerExchangePanel.FIELD_CODE);
			writer.write('=');
			writer.write(String.valueOf(code));
			writer.write(";\ndocument.iambookmasterLoad(result);\n");
			resp.flushBuffer();
		} catch (IOException e) {
			log.log(Level.WARNING,e.getMessage());
		}
	}

}
