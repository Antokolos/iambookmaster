package com.iambookmaster.server;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.KeyFactory;
import com.iambookmaster.client.ServerExchangePanel;
import com.iambookmaster.client.editor.ModelPersist;
import com.iambookmaster.client.exceptions.TimeoutException;
import com.iambookmaster.client.locale.AppConstants;
import com.iambookmaster.client.locale.AppMessages;
import com.iambookmaster.server.beans.JPABook;
import com.iambookmaster.server.beans.JPAUser;
import com.iambookmaster.server.dao.DAO;

public class PublishServlet extends LoadModelServlet {
	
	private static final long serialVersionUID = 1L;

	private static final Logger log = Logger.getLogger(PublishServlet.class.getName());

	protected static final String PUBLISH_TASK = "com.iambookmaster.server.PublishServlet";
	
	protected long timeoutLimit=20*1000;
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		perform(req,resp);
	}
	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)	throws ServletException, IOException {
		perform(req,resp);
	}

	protected void perform(HttpServletRequest req, HttpServletResponse resp)throws IOException {
		final long time = System.currentTimeMillis();
		req.setCharacterEncoding("UTF-8");
		String operation = req.getParameter(ServerExchangePanel.FIELD_OPERATION);
		AppConstants appConstants = LocalMessages.getInstance(AppConstants.class, LocalMessages.getLocale(req,resp));
		AppMessages appMessages = LocalMessages.getInstance(AppMessages.class, LocalMessages.getLocale(req,resp));
		try {
			PersistenceManager em = TransactionInViewFilter.getEM(req);
			PublishTaskListener listener = new PublishTaskListener() {
				public boolean checkTimeout() {
					return System.currentTimeMillis()-time > timeoutLimit; 
				}
			};
			if (operation==null) {
				PublishTask publishTask = (PublishTask)req.getSession().getAttribute(PUBLISH_TASK);
				if (publishTask==null) {
					throw new LogicException("Operation is not set");
				} else {
					//continue publishing, just to be sure - check for login
					JPAUser user = getUser(req,resp);
					publishTask.continueTask(user,em,listener);
					success(req,resp,KeyFactory.keyToString(publishTask.getBookKey()));
				}
			} else {
				startPublishing(em,listener,operation,time,req,resp,appConstants,appMessages);
			}
		} catch (TimeoutException e) {
			resp.setContentType("text/html; charset=UTF-8");
			resp.getWriter().println("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/>");
			resp.getWriter().println("<title>I am Book Master</title>");
			resp.getWriter().println("<script language='javascript'>function onloadEvent() {");
			resp.getWriter().print("window.location.href='");
			resp.getWriter().print(req.getRequestURI());
			resp.getWriter().print("';}");
			resp.getWriter().println("</script></head><body onload=\"onloadEvent();\">Processing, do not close browser...");
			resp.getWriter().println("</body></html>");
			resp.flushBuffer();
		} catch (LogicException e) {
			replyError("Errors were detected",e.getMessage(),req,resp);
		} catch (Exception e) {
			e.printStackTrace();
			log.log(Level.SEVERE,e.getMessage());
			replyError(e,req,resp);
		}
	}

	protected void startPublishing(PersistenceManager em, PublishTaskListener listener, String operation, long time, HttpServletRequest req, HttpServletResponse resp,AppConstants appConstants,AppMessages appMessages) throws LogicException,TimeoutException,IOException {
		JPAUser user = getUser(req,resp);
		//no errors in the model
		ModelPersist model = getModelFromRequest(em, user, req, resp,appConstants,appMessages);
		if (model==null) {
			return;
		}
		JPABook book = DAO.getBookDAO().findBook(em, user,model);
		if (book != null && book.isLocked()) {
			//this book is locked
			throw new LogicException("This Book is locked. You cannot change it.");
		}
		PublishTask task = new PublishTask(book,appConstants,appMessages);
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
		success(req,resp,KeyFactory.keyToString(task.getBookKey()));
	}

	protected void success(HttpServletRequest req, HttpServletResponse resp, String bookKey) throws IOException {
		//for Editor
//		String url = resp.encodeRedirectURL(req.getRequestURL().substring(0,req.getRequestURL().lastIndexOf("/"))+"/main.jsp");
		log.log(Level.INFO,"Success publiching, key="+bookKey);
		replyLoad(bookKey,null, req, resp);
	}

}
