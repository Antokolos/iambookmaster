package com.iambookmaster.server;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.iambookmaster.client.player.FeedbackPanel;
import com.iambookmaster.server.beans.JPAFeedback;

public class FeedbackServlet extends AbstractServlet {
	
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(FeedbackServlet.class.getName());
	
	private static final String COOKIE_KEY="iamboomaster_feedback"; 

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		perform(req,resp);
	}
	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)	throws ServletException, IOException {
		perform(req,resp);
	}

	private void perform(HttpServletRequest req, HttpServletResponse resp)throws IOException {
		req.setCharacterEncoding("UTF-8");
//		AppConstants appConstants = LocalMessages.getInstance(AppConstants.class, LocalMessages.getLocale(req,resp));
		Cookie[] cookies = req.getCookies();
		String key = null;
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (COOKIE_KEY.equals(cookie.getName())) {
					//found
					key = cookie.getValue();
					break;
				}
			}
		}
		String paragraphId = req.getParameter(FeedbackPanel.PARAGRAPH);
		String gameId = req.getParameter(FeedbackPanel.GAME_ID);
		String gameKey = req.getParameter(FeedbackPanel.GAME_KEY);
		String title = req.getParameter(FeedbackPanel.GAME_TITLE);
		String authors = req.getParameter(FeedbackPanel.GAME_AUTHORS);
		String rating = req.getParameter(FeedbackPanel.STORY_RATING);
		String complexity = req.getParameter(FeedbackPanel.COMPLEXITY_RATING);
		String appearence = req.getParameter(FeedbackPanel.APPEARENCE_RATING);
		String note = req.getParameter(FeedbackPanel.NOTE);
		StringBuilder builder = new StringBuilder("Feedback\n");
		builder.append(title);
		builder.append('\n');
		builder.append(authors);
		builder.append('\n');
		builder.append("id=");
		builder.append(gameId);
		builder.append(" key=");
		builder.append(gameKey);
		builder.append('\n');
		if (paragraphId != null) {
			builder.append("paragraph ID ");
			builder.append(paragraphId);
			builder.append('\n');
		}
		builder.append("Rating: ");
		builder.append(rating);
		builder.append('/');
		builder.append(complexity);
		builder.append('/');
		builder.append(appearence);
		builder.append('\n');
		builder.append(note);
		log.log(Level.INFO,builder.toString());
		if (gameId != null && title != null && authors != null && rating != null && complexity != null && appearence != null) {
			try {
				PersistenceManager em = getPM(req);
				JPAFeedback feedback=null;
				boolean create=false;
				if (key!=null) {
					Key feebackKey;
					try {
						feebackKey = KeyFactory.stringToKey(key);
					} catch (Exception e) {
						feebackKey = null;
					}
					if (feebackKey != null) {
						Query query =  em.newQuery(Query.JDOQL,"SELECT from "+JPAFeedback.class.getName()+" WHERE id==_key");
						query.declareImports("import "+Key.class.getName());
						query.declareParameters("Key _key");
						List<JPAFeedback> list = (List<JPAFeedback>)query.execute(feebackKey);
						if (list.size()>0) {
							feedback = list.get(0);
							if (gameId.equals(feedback.getBookId())==false) {
								//other book
								feedback = null;
							}
						}
					}
				}
				if (feedback==null) {
					create = true;
					feedback = new JPAFeedback();
					feedback.setBookId(gameId);
				}
				feedback.setParagraph(paragraphId);
				if (feedback.getBookKey()==null && gameKey != null) {
					try {
						feedback.setBookKey(KeyFactory.stringToKey(gameKey));
					} catch (Exception e) {
					}
				}
				feedback.setAuthor(authors);
				feedback.setTitle(title);
				feedback.setRating(Integer.parseInt(rating));
				feedback.setComplexity(Integer.parseInt(complexity));
				feedback.setAppearance(Integer.parseInt(appearence));
				feedback.setNote(note);
				feedback.setDate(new Date());
				if (create) {
					feedback = em.makePersistent(feedback);
				}
				Cookie cookie = new Cookie(COOKIE_KEY,KeyFactory.keyToString(feedback.getId()));
				cookie.setMaxAge(60*60*24*30);//30 days
				resp.addCookie(cookie);
				resp.getWriter().write("OK");
				resp.flushBuffer();
			} catch (Exception e) {
				e.printStackTrace();
				log.log(Level.SEVERE,e.getMessage());
				resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		}
	}

}
