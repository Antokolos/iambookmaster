package com.iambookmaster.server;

import java.io.IOException;
import java.io.StringReader;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.iambookmaster.client.ServerExchangePanel;
import com.iambookmaster.client.common.Base64Coder;
import com.iambookmaster.client.common.JSONBuilder;
import com.iambookmaster.client.editor.ModelPersist;
import com.iambookmaster.client.locale.AppConstants;
import com.iambookmaster.client.locale.AppMessages;
import com.iambookmaster.client.remote.RemotePanel;
import com.iambookmaster.server.beans.JPABook;
import com.iambookmaster.server.beans.JPAUser;
import com.iambookmaster.server.dao.DAO;

public abstract class AbstractServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	public JPAUser getUser(HttpServletRequest req,HttpServletResponse resp) throws LogicException {
		//for Google App Engines
		UserService userService = UserServiceFactory.getUserService();
		if (userService.isUserLoggedIn()) {
			User usr = userService.getCurrentUser();
			PersistenceManager em = getPM(req);
			JPAUser user = DAO.getUsersDAO().findOrCreateUser(em, usr);
			if (user != null && user.isLocked()) {
				//this book is locked
				throw new LogicException(getAppConstants(req,resp).serverAccountLocked());
			}
			return user;
 		} else {
 			throw new LogicException(getAppConstants(req,resp).serverNotLoggedIn());
 		}
	}

	protected AppConstants getAppConstants(HttpServletRequest req,HttpServletResponse resp) {
		return LocalMessages.getInstance(AppConstants.class, LocalMessages.getLocale(req,resp));
	}

	protected AppMessages getAppMessages(HttpServletRequest req,HttpServletResponse resp) {
		return LocalMessages.getInstance(AppMessages.class, LocalMessages.getLocale(req,resp));
	}
	
	protected PersistenceManager getPM(ServletRequest req) {
		return TransactionInViewFilter.getEM(req);
	}

	protected void replyError(String error, HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String callback = extractCallback(req);
		if (callback==null) {
			//no callback, just print it
			printCallbackError(resp);
			return;
		}
		JSONBuilder builder = JSONBuilder.getStartInstance();
		builder.newRow();
		builder.field(RemotePanel.FIELD_ANSWER_CODE, RemotePanel.CODE_ERROR);
		builder.field(RemotePanel.FIELD_ERROR_SHORT, error);
		sendReply(req,resp,callback,builder);
	}
	
	protected void replyError(String error, String errorDescription, HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String callback = extractCallback(req);
		if (callback==null) {
			//no callback, just print it
			printCallbackError(resp);
			return;
		}
		JSONBuilder builder = JSONBuilder.getStartInstance();
		builder.newRow();
		builder.field(RemotePanel.FIELD_ANSWER_CODE, RemotePanel.CODE_ERROR);
		builder.field(RemotePanel.FIELD_ERROR_SHORT, error);
		builder.field(RemotePanel.FIELD_DESCRIPTION, errorDescription.replace("\n", "<br/>"));
		sendReply(req,resp,callback,builder);
	}
	
	protected void replyError(Throwable error, HttpServletRequest req, HttpServletResponse resp) throws IOException {
		StringBuffer buffer = new StringBuffer();
		buffer.append(error.getMessage());
		buffer.append('\n');
		StackTraceElement[] elements = error.getStackTrace();
		for (int i = 0; i < elements.length; i++) {
			buffer.append(elements[i].toString());
			buffer.append('\n');
		}
		replyError(error.getMessage(),buffer.toString(),req,resp);
	}
	
	private void sendReply(HttpServletRequest req,HttpServletResponse resp, String callback, JSONBuilder builder) throws IOException{
		resp.setCharacterEncoding("UTF-8");
		resp.setContentType("text/html; charset=UTF-8");
		resp.getWriter().println("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/>");
		resp.getWriter().println("<title>I am Book Master</title>");
		resp.getWriter().println("<script language='javascript'>function onloadEvent() {");
		resp.getWriter().print("window.name='");
		resp.getWriter().print(Base64Coder.encodeString(builder.toString()));
		resp.getWriter().println("';");
		resp.getWriter().print("try{");
		resp.getWriter().print("window.location.href='");
		resp.getWriter().print(callback);
		resp.getWriter().print("';}catch(e){history.back();return false;}}");
		resp.getWriter().println("</script></head><body onload=\"onloadEvent();\">&nbsp;</body></html>");
		resp.flushBuffer();
	}

	private void printCallbackError(HttpServletResponse resp) throws IOException {
		resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "No callback to the client provided");
	}

	private String extractCallback(HttpServletRequest req) {
		String cl = req.getParameter(RemotePanel.FIELD_CALLBACK);
		if (cl==null) {
			return null;
		} else {
			try {
				return Base64Coder.decodeString(cl);
			} catch (Exception e) {
				//wrong Base64
				return null;
			}
		}
	}

	protected void replyOk(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		String callback = extractCallback(req);
		if (callback==null) {
			//no callback, just print it
			printCallbackError(resp);
			return;
		}
		JSONBuilder builder = JSONBuilder.getStartInstance();
		builder.newRow();
		builder.field(RemotePanel.FIELD_ANSWER_CODE, RemotePanel.CODE_SUCCESS);
		sendReply(req,resp,callback,builder);
	}
	
	protected void replyOk(String url,HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String callback = extractCallback(req);
		if (callback==null) {
			//no callback, just print it
			printCallbackError(resp);
			return;
		}
		JSONBuilder builder = JSONBuilder.getStartInstance();
		builder.newRow();
		builder.field(RemotePanel.FIELD_ANSWER_CODE, RemotePanel.CODE_SUCCESS);
		builder.field(RemotePanel.FIELD_URL, url);
		sendReply(req,resp,callback,builder);
	}

	protected void replyLoad(String bookKey,String versionKey,HttpServletRequest request, HttpServletResponse resp) throws IOException {
		String callback = extractCallback(request);
		if (callback==null) {
			//no callback, just print it
			printCallbackError(resp);
			return;
		}
		JSONBuilder builder = JSONBuilder.getStartInstance();
		builder.newRow();
		builder.field(RemotePanel.FIELD_ANSWER_CODE, RemotePanel.CODE_LOAD);
		String req = request.getRequestURL().toString();
		StringBuffer url = new StringBuffer(req.substring(0,req.lastIndexOf("/")));
		url.append("/loadModel.js?");
		if (versionKey==null) {
			url.append(LoadModelServlet.FIELD_BOOK_ID);
			url.append('=');
			url.append(bookKey);
		} else {
			url.append(LoadModelServlet.FIELD_BOOK_VERSION_ID);
			url.append('=');
			url.append(versionKey);
		}
		builder.field(RemotePanel.FIELD_URL, url.toString());
		sendReply(request,resp,callback,builder);
	}

	protected ModelPersist getModel(String mod, HttpServletRequest req, HttpServletResponse resp,AppConstants appConstants,AppMessages appMessages) throws LogicException  {
		Document result;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setValidating(false);
            dbf.setNamespaceAware(false);
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource inputSource = new InputSource(new StringReader(mod));
            result = db.parse(inputSource);
        }  catch (Exception e) {
			throw new LogicException(e);
        }
    	ModelPersist model = new ModelPersist(appConstants,appMessages);
    	XMLModelParser parser = new XMLModelParser();
    	try {
			model.restore(result, parser);
		} catch (Throwable e) {
			throw new LogicException(e);
		}
		return model;
	}

	protected ModelPersist getModelFromRequest(PersistenceManager em, JPAUser user, HttpServletRequest req, HttpServletResponse resp,AppConstants appConstants,AppMessages appMessages) throws LogicException {
		if (user.getUpload()==null || user.getUpload().length()==0) {
			throw new LogicException(appConstants.serverUploadModelBefore());
		}
		String id = req.getParameter(ServerExchangePanel.FIELD_DATA);
		if (id==null) {
			throw new LogicException(appConstants.serverNoBookID());
		}
		if (id.equals(user.getUploadExternalId())==false) {
			throw new LogicException(appConstants.serverBookIDAndModelAreDifferent());
		}
		ModelPersist model = getModel(user.getUpload(),req, resp, appConstants, appMessages);
		if (id.equals(model.getGameId())==false) {
			throw new LogicException(appConstants.serverBookIDAndModelAreDifferent());
		}
		JPABook book = DAO.getBookDAO().findBook(em, user,model);
		if (book != null && book.isLocked()) {
			//this book is locked
			throw new LogicException(appConstants.serverBookIsLocked());
		}
		return model;
	}
}
