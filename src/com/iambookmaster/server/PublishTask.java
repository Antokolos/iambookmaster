package com.iambookmaster.server;

import java.io.Serializable;

import javax.jdo.PersistenceManager;

import com.google.appengine.api.datastore.Key;
import com.iambookmaster.client.beans.Paragraph;
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
import com.iambookmaster.server.logic.ServerBookCreator;
import com.iambookmaster.server.logic.ServerBookCreatorListener;
import com.iambookmaster.server.logic.ServerModelValidator;
import com.iambookmaster.server.logic.ServerModelValidatorListener;

public class PublishTask implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private static final int TASK_RECREATE = 0;
	private static final int TASK_CREATE = 1;
	
	private int task=0; 
	private ModelPersist model;
	private ServerModelValidator validator;
	private ServerBookCreator bookCreator;
	private ServerModelValidatorListener validatorListener;
	private ServerBookCreatorListener bookCreatorListener;
	private PublishTaskListener taskListener;
	private final AppConstants appConstants;
	private final AppMessages appMessages;
	
	private Key bookKey;
	
	public Key getBookKey() {
		return bookKey;
	}

	public PublishTask(JPABook book,AppConstants appConstants,AppMessages appMessages) {
		if (book != null) {
			bookKey = book.getId();
		}
		this.appConstants = appConstants;
		this.appMessages = appMessages;
	}

	public PublishTask(JPABookVersion version,String locale) {
		if (version != null) {
			bookKey = version.getBook();
		}
		appConstants = LocalMessages.getInstance(AppConstants.class, locale);
		appMessages = LocalMessages.getInstance(AppMessages.class, locale);
	}

	public void continueTask(JPAUser user, PersistenceManager em, PublishTaskListener listener) throws TimeoutException,LogicException {
		this.taskListener = listener;
		if (validator != null) {
			if (validator.continueValidation(validatorListener)==false) {
				throw new LogicException(validator.getErrors());
			}
			successulValidataion(user,em);
		} else {
			//bookCreator can be interrupted only during creation
			bookCreator.continueCreation(bookCreatorListener);
			BooksDAO booksDAO = DAO.getBookDAO();
			JPABook book = booksDAO.findBook(em, bookKey);
			book = booksDAO.mergeBook(em,model,book,true);
			publishBook(em,book);
		}
	}

	private void successulValidataion(JPAUser user, PersistenceManager em) throws LogicException,TimeoutException {
		BooksDAO booksDAO = DAO.getBookDAO();
		JPABook book;
		if (bookKey==null) {
			//new book
			book = booksDAO.mergeBook(em,model,user,true);
			bookKey = book.getId();
		} else {
			book = booksDAO.findBook(em, bookKey);
			if (book==null) {
				throw new LogicException(appConstants.serverUnknownBook());
			}
			//TODO check for locked book or locked user
		}
		book = booksDAO.mergeBook(em,model,book,true);
		bookCreator = new ServerBookCreator(model,appConstants,appMessages); 
		bookCreatorListener = new ServerBookCreatorListener() {
			public boolean checkTimiout() {
				return taskListener.checkTimeout();
			}
		};
		if (task==TASK_CREATE) {
			//generate numbers for book
			bookCreator.create(bookCreatorListener);
			//book is ready, save and publish
			booksDAO.mergeBook(em, model, book, true);
			publishBook(em,book);
		} else  {
			//book is ready, publish
			publishBook(em,book);
		}
	}

	/**
	 * This method is called when current model is generated and validated
	 * so from now Text, HTML or other version of book can be created
	 * @param em
	 * @param book
	 */
	private void publishBook(PersistenceManager em, JPABook book) throws LogicException{
		//step 1 - validate numbers
		Paragraph[] paragraphs = bookCreator.recreationValidate(); 
		//step 2 - generate
		BooksDAO booksDAO = DAO.getBookDAO();
		String text = bookCreator.getText(paragraphs);
		booksDAO.setCLOB(em, book, JPAClob.TYPE_TEXT, text);
		text = bookCreator.getHTML(paragraphs);
		booksDAO.setCLOB(em, book, JPAClob.TYPE_HTML, text);
		text = bookCreator.getURQ(paragraphs);
		booksDAO.setCLOB(em, book, JPAClob.TYPE_URQ, text);
	}

	public void recreate(ModelPersist model, JPAUser user, PersistenceManager em, PublishTaskListener listener) throws TimeoutException,LogicException{
		validate(TASK_RECREATE,model,user,em,listener);
	}

	public void create(ModelPersist model, JPAUser user, PersistenceManager em, PublishTaskListener listener) throws TimeoutException,LogicException {
		validate(TASK_CREATE,model,user,em,listener);
	}

	private void validate(int task, ModelPersist model, JPAUser user, PersistenceManager em, PublishTaskListener listener) throws TimeoutException,LogicException {
		this.model = model;
		this.task = task;
		this.taskListener = listener;
		validatorListener = new ServerModelValidatorListener() {
			public boolean checkTimeout() {
				return taskListener.checkTimeout();
			}
		};
		validator = new ServerModelValidator(model,appConstants,appMessages);
		if (validator.validate(validatorListener)==false) {
			throw new LogicException(validator.getErrors());
		}
		successulValidataion(user,em);
	}

}
