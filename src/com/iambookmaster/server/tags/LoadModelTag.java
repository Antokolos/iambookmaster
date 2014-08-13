package com.iambookmaster.server.tags;

import java.io.IOException;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

import com.google.appengine.api.datastore.KeyFactory;
import com.iambookmaster.client.common.Base64Coder;
import com.iambookmaster.client.common.JSONBuilder;
import com.iambookmaster.client.remote.RemotePanel;
import com.iambookmaster.server.LoadModelServlet;
import com.iambookmaster.server.beans.JPABook;
import com.iambookmaster.server.beans.JPABookVersion;
import com.iambookmaster.server.beans.JPAClob;
import com.iambookmaster.server.dao.BooksDAO;
import com.iambookmaster.server.dao.DAO;

@SuppressWarnings("serial")
public class LoadModelTag extends RememberCallbackTag {
	
	public static final String TYPE_EDITOR = "editor";
	public static final String TYPE_PROJECT = "project";
	public static final String TYPE_TXT = "text";
	public static final String TYPE_HTML = "html";
	public static final String TYPE_URQ = "urq";
	public static final String TYPE_URQ_SHORT = "urqShort";
	public static final String TYPE_QSP = "qsp";
	
	private String type;
	private String target;
	
	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public int doStartTag() throws JspException {
		if (pageContext.getRequest() instanceof HttpServletRequest) {
			HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
			Object object = getObjectByName(false,getName());
			String bookKey;
			String versionKey;
			if (object instanceof JPABook) {
				JPABook book = (JPABook)object;
				bookKey = KeyFactory.keyToString(book.getId());
				versionKey = null;
			} else if (object instanceof JPABookVersion) {
				JPABookVersion bookVersion = (JPABookVersion)object;
				bookKey = KeyFactory.keyToString(bookVersion.getBook());
				versionKey = KeyFactory.keyToString(bookVersion.getId());
			} else {
				throw new JspException(getName()+" is not a JPABook or JPABookVersion");
			}
			try {
				if (TYPE_EDITOR.equals(type)) {
					editorModelLink(request,bookKey,versionKey);
				} else if (TYPE_PROJECT.equals(type)) {
					projectLink(request,bookKey,versionKey,type);
				} else if (TYPE_TXT.equals(type)) {
					projectLink(request,bookKey,versionKey,type);
				} else if (TYPE_HTML.equals(type)) {
					projectLink(request,bookKey,versionKey,type);
				} else if (TYPE_URQ.equals(type)) {
					projectLink(request,bookKey,versionKey,type);
				} else if (TYPE_URQ_SHORT.equals(type)) {
					projectLink(request,bookKey,versionKey,type);
				} else if (TYPE_QSP.equals(type)) {
					projectLink(request,bookKey,versionKey,type);
				} else {
					throw new JspException("Unknow type "+type);
				}
			} catch (IOException e) {
				throw new JspException(e);
			}
		} else {
			throw new JspException("Only HTTP request can be used");
		}
		return EVAL_BODY_INCLUDE;
	}

	private void projectLink(HttpServletRequest request, String bookKey, String versionKey,String type) throws JspException,IOException {
		pageContext.getOut().append("<a href=\"");
		String req = request.getRequestURL().toString();
		pageContext.getOut().append(req.substring(0,req.lastIndexOf("/")));
		pageContext.getOut().append("/getbook.do?");
		pageContext.getOut().append(LoadModelServlet.FIELD_TYPE);
		pageContext.getOut().append('=');
		pageContext.getOut().append(type);
		pageContext.getOut().append('&');
		if (versionKey==null) {
			pageContext.getOut().append(LoadModelServlet.FIELD_BOOK_ID);
			pageContext.getOut().append('=');
			pageContext.getOut().append(bookKey);
		} else {
			pageContext.getOut().append(LoadModelServlet.FIELD_BOOK_VERSION_ID);
			pageContext.getOut().append('=');
			pageContext.getOut().append(versionKey);
		}
		pageContext.getOut().append('"');
		if (target != null) {
			pageContext.getOut().append(" target=\"");
			pageContext.getOut().append(target);
			pageContext.getOut().append('"');
		}
		pageContext.getOut().append('>');
	}

	private void editorModelLink(HttpServletRequest request, String bookKey, String versionKey) throws JspException,IOException {
		if (isCallbackPresent()==false) {
			throw new JspException("Cannot obtain callback to the editor");
		}
		pageContext.getOut().append("<a href=\"");
		pageContext.getOut().append(getCallback());
		pageContext.getOut().append("\" onclick=\"");
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
		pageContext.getOut().append("window.name='");
		pageContext.getOut().append(Base64Coder.encodeString(builder.toString()));
		pageContext.getOut().append("';try{window.location.href='");
		pageContext.getOut().append(getCallback());
		//Mozilla - Links to local pages do not work
		//see http://kb.mozillazine.org/Links_to_local_pages_don't_work
		pageContext.getOut().append("';}catch(e){history.back();return false;}\">");
	}

	protected boolean isTypePresent() throws JspException {
		if (LoadModelTag.TYPE_EDITOR.equals(type)) {
			return isCallbackPresent();
		} else if (LoadModelTag.TYPE_PROJECT.equals(type)) {
			return true;
		}
		Object object = getObjectByName();
		BooksDAO booksDAO = DAO.getBookDAO();
		PersistenceManager em = getPM();
		if (object instanceof JPABook) {
			JPABook book = (JPABook) object;
			int typeData;
			if (LoadModelTag.TYPE_TXT.equals(type)) {
				typeData = JPAClob.TYPE_TEXT;
			} else if (LoadModelTag.TYPE_HTML.equals(type)) {
				typeData = JPAClob.TYPE_TEXT;
			} else if (LoadModelTag.TYPE_URQ.equals(type)) {
				typeData = JPAClob.TYPE_TEXT;
			} else if (LoadModelTag.TYPE_URQ_SHORT.equals(type)) {
				typeData = JPAClob.TYPE_TEXT;
			} else if (LoadModelTag.TYPE_QSP.equals(type)) {
				typeData = JPAClob.TYPE_TEXT;
			} else {
				throw new JspException("Uknown type "+type);
			}
			String data = booksDAO.getCLOB(em, book, typeData);
			return data!=null && data.length()>0;
		} else if (object instanceof JPABookVersion) {
			JPABookVersion version = (JPABookVersion) object;
			int typeData;
			if (LoadModelTag.TYPE_TXT.equals(type)) {
				typeData = JPAClob.TYPE_TEXT;
			} else if (LoadModelTag.TYPE_HTML.equals(type)) {
				typeData = JPAClob.TYPE_HTML;
			} else if (LoadModelTag.TYPE_URQ.equals(type)) {
				typeData = JPAClob.TYPE_TEXT;
			} else if (LoadModelTag.TYPE_URQ_SHORT.equals(type)) {
				typeData = JPAClob.TYPE_TEXT;
			} else if (LoadModelTag.TYPE_QSP.equals(type)) {
				typeData = JPAClob.TYPE_TEXT;
			} else {
				throw new JspException("Uknown type "+type);
			}
			String data = booksDAO.getCLOB(em, version, typeData);
			return data!=null && data.length()>0;
		} else {
			throw new JspException(getName()+" is not JSPBook or JPAVersion");
		}
	}

	@Override
	public int doEndTag() throws JspException {
		try {
			pageContext.getOut().append("</a>");
			return EVAL_PAGE;
		} catch (IOException e) {
			throw new JspException(e);
		}
	}





}
