package com.iambookmaster.server;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.UserServiceFactory;
import com.iambookmaster.client.ServerExchangePanel;
import com.iambookmaster.client.editor.ModelPersist;
import com.iambookmaster.client.exceptions.TimeoutException;
import com.iambookmaster.client.locale.AppConstants;
import com.iambookmaster.client.locale.AppMessages;
import com.iambookmaster.server.beans.JPABook;
import com.iambookmaster.server.beans.JPABookVersion;
import com.iambookmaster.server.beans.JPAClob;
import com.iambookmaster.server.beans.JPAUser;
import com.iambookmaster.server.dao.BooksDAO;
import com.iambookmaster.server.dao.DAO;

public class WebPublishServlet extends PublishServlet {
	
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(WebPublishServlet.class.getName());

	protected void startPublishing(PersistenceManager em, PublishTaskListener listener, String operation, long time, HttpServletRequest req, HttpServletResponse resp) throws LogicException,TimeoutException,IOException {
		JPAUser user = getUser(req,resp);
		//no errors in the model
		AppConstants appConstants = LocalMessages.getInstance(AppConstants.class, LocalMessages.getLocale(req,resp));
		AppMessages appMessages = LocalMessages.getInstance(AppMessages.class, LocalMessages.getLocale(req,resp));
		Object object = getBookOrVersionAndValidate(null, em, UserServiceFactory.getUserService(), req, resp,appConstants);
		String modelXML;
		JPABook book;
		PublishTask task;
		BooksDAO bookDAO = DAO.getBookDAO();
		if (object instanceof JPABook) {
			book = (JPABook) object;
			task = new PublishTask(book,appConstants,appMessages);
			modelXML = bookDAO.getCLOB(em, book, JPAClob.TYPE_MODEL);
		} else if (object instanceof JPABookVersion) {
			JPABookVersion version = (JPABookVersion) object;
			task = new PublishTask(version,LocalMessages.getLocale(req,resp));
			book = DAO.getBookDAO().findBook(em, version.getBook());
			modelXML = bookDAO.getCLOB(em, version, JPAClob.TYPE_MODEL);
		} else {
			//error
			replyError("Unknown object: "+object,req,resp);
			return;
		}
		if (book != null && book.isLocked()) {
			//this book is locked
			throw new LogicException(appConstants.serverBookIsLocked());
		}
		ModelPersist model = getModel(modelXML,req, resp, appConstants, appMessages);
		
		log.log(Level.INFO,"Publish book '"+model.getSettings().getBookTitle()+"' id="+KeyFactory.keyToString(book.getId())+", externalId="+model.getGameId()+", user="+user.getEmail());
		
		if (ServerExchangePanel.OPERATION_RE_EXPORT.equals(operation)) {
			req.getSession().setAttribute(PUBLISH_TASK,task);
			task.recreate(model,user,em,listener);
		} else if (ServerExchangePanel.OPERATION_CREATE.equals(operation)) {
			req.getSession().setAttribute(PUBLISH_TASK,task);
			task.create(model,user,em,listener);
		} else {
			replyError("Unknown operation",req,resp);
			return;
		}
		success(req,resp,KeyFactory.keyToString(book.getId()));
	}
	
	@Override
	protected void success(HttpServletRequest req, HttpServletResponse resp, String bookKey) throws IOException {
		//for web-page
		String url = resp.encodeRedirectURL(req.getRequestURL().substring(0,req.getRequestURL().lastIndexOf("/"))+"/main.jsp");
		resp.sendRedirect(url);
	}
	
	protected void replyError(String error, String errorDescription, HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setCharacterEncoding("UTF-8");
		resp.sendError(HttpServletResponse.SC_BAD_REQUEST,error+'\n'+errorDescription);
	}
	
	protected void replyError(String error, HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setCharacterEncoding("UTF-8");
		resp.sendError(HttpServletResponse.SC_BAD_REQUEST,error);
	}
	

}
