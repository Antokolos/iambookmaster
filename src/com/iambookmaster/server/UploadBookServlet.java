package com.iambookmaster.server;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.iambookmaster.client.ServerExchangePanel;
import com.iambookmaster.client.locale.AppConstants;
import com.iambookmaster.server.beans.JPABook;
import com.iambookmaster.server.beans.JPAUser;
import com.iambookmaster.server.dao.DAO;

public class UploadBookServlet extends AbstractServlet {
	
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(UploadBookServlet.class.getName());

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		perform(req,resp);
	}
	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)	throws ServletException, IOException {
		perform(req,resp);
	}

	private void perform(HttpServletRequest req, HttpServletResponse resp)throws IOException {
		req.setCharacterEncoding("UTF-8");
		AppConstants appConstants = LocalMessages.getInstance(AppConstants.class, LocalMessages.getLocale(req,resp));
		try {
			PersistenceManager em = getPM(req);
			JPAUser user = getUser(req,resp);
			if (user.isLocked()) {
				//this user is locked
				throw new LogicException(appConstants.serverAccountLocked());
			}
			String mod = req.getParameter(ServerExchangePanel.FIELD_MODEL);
			if (mod==null) {
				throw new LogicException(appConstants.serverModelWasNotSent());
			}
			String externalId = req.getParameter(ServerExchangePanel.FIELD_DATA);
			if (externalId==null) {
				throw new LogicException(appConstants.serverNoBookID());
			}
			JPABook book = DAO.getBookDAO().findBook(em, user,externalId);
			if (book != null && book.isLocked()) {
				//this book is locked
				throw new LogicException(appConstants.serverBookIsLocked());
			}
			if (req.getParameter(ServerExchangePanel.FIELD_CODE) != null) {
				//start from zero
				user.setUploadExternalId(externalId);
				user.setUpload(mod);
			} else if (externalId.equals(user.getUploadExternalId())) {
				//the same
				if (mod.length()+user.getUpload().length()>10000000) {
					//not set, error
					throw new LogicException(appConstants.serverModelIsTooBig());
				}
				user.setUpload(user.getUpload()+mod);
			} else {
				//start from zero
				user.setUploadExternalId(externalId);
				user.setUpload(mod);
			}
			log.log(Level.INFO,"Success uploaded");
			replyOk(req, resp);
		} catch (LogicException e) {
			replyError(appConstants.serverErrorsWereDetected(),e.getMessage(),req,resp);
		} catch (Exception e) {
			e.printStackTrace();
			log.log(Level.SEVERE,e.getMessage());
			replyError(e,req,resp);
		}
	}

}
