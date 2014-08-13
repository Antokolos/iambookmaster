package com.iambookmaster.server;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.iambookmaster.client.editor.ModelPersist;
import com.iambookmaster.client.locale.AppConstants;
import com.iambookmaster.client.locale.AppMessages;
import com.iambookmaster.server.beans.JPAUser;
import com.iambookmaster.server.dao.DAO;

public class SaveBookServlet extends AbstractServlet {
	
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(SaveBookServlet.class.getName());

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		perform(req,resp);
	}
	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)	throws ServletException, IOException {
		perform(req,resp);
	}

	private void perform(HttpServletRequest req, HttpServletResponse resp)throws IOException {
		req.setCharacterEncoding("UTF-8");
		AppConstants appConstants = LocalMessages.getInstance(AppConstants.class, LocalMessages.getLocale(req,resp));
		AppMessages appMessages = LocalMessages.getInstance(AppMessages.class, LocalMessages.getLocale(req,resp));
		try {
			PersistenceManager em = getPM(req);
			JPAUser user = getUser(req,resp);
			ModelPersist model = getModelFromRequest(em, user, req, resp,appConstants,appMessages);
			DAO.getBookDAO().mergeBook(em, model, user, false);
			user.setUpload(null);
			user.setUploadExternalId(null);
			success(req,resp);
		} catch (LogicException e) {
			replyError(appConstants.serverErrorsWereDetected(),e.getMessage(),req,resp);
		} catch (Exception e) {
			e.printStackTrace();
			log.log(Level.SEVERE,e.getMessage());
			replyError(e,req,resp);
		}
	}

	private void success(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String url = resp.encodeRedirectURL(req.getRequestURL().substring(0,req.getRequestURL().lastIndexOf("/"))+"/main.jsp");
		log.log(Level.INFO,"Success publiching. url"+url);
		replyOk(url, req, resp);
	}

}
