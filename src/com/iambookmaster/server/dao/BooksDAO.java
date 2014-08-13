package com.iambookmaster.server.dao;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.persistence.NonUniqueResultException;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;
import com.iambookmaster.client.common.XMLBuilder;
import com.iambookmaster.client.editor.ModelPersist;
import com.iambookmaster.client.model.Model;
import com.iambookmaster.server.beans.JPABook;
import com.iambookmaster.server.beans.JPABookVersion;
import com.iambookmaster.server.beans.JPAClob;
import com.iambookmaster.server.beans.JPAUser;

public class BooksDAO {
	
	private static final Logger log = Logger.getLogger(BooksDAO.class.getName());
	
	private static final String SELECT_BOOKS="SELECT from "+JPABook.class.getName();
	private static final String SELECT_BOOKS_WHERE=SELECT_BOOKS+" WHERE ";
	private static final String SELECT_BOOK_VERSIONS_WHERE = "SELECT from "+JPABookVersion.class.getName()+" WHERE ";
	private static final String SELECT_CLOB_WHERE_OWNER = "SELECT from "+JPAClob.class.getName()+" WHERE owner==_owner && version==_verson";
	private static final String SELECT_CLOB_WHERE_TYPE = "SELECT from "+JPAClob.class.getName()+" WHERE owner==_owner && type==_type && version==_verson ORDER BY ordering";
	
	@SuppressWarnings("unchecked")
	public List<JPABook> selectBooks(PersistenceManager em, BookCriteria criteria) {
		StringBuffer buffer = new StringBuffer(SELECT_BOOKS_WHERE);
		Query query;
		int par= applyCriteria(criteria,buffer,null,null);
		if (par>0) {
			Object[] params= new Object[par];
			query = em.newQuery(Query.JDOQL,buffer.toString());
			applyCriteria(criteria,null,query,params);
			return (List<JPABook>)query.executeWithArray(params);
		} else {
			return (List<JPABook>)em.newQuery(Query.JDOQL,SELECT_BOOKS).execute();
		}
	}
	
	private int applyCriteria(BookCriteria criteria, StringBuffer buffer,Query query,Object[] params) {
		StringBuffer importStr=null;
		StringBuffer paramStr=null;
		if (query != null) {
			importStr = new StringBuffer();
			paramStr = new StringBuffer();
		}
		int param=0;
		if (criteria.getUser() != null) {
			if (buffer != null) {
				buffer.append("owner==userKey");
			}
			if (query != null) {
				//append import and parameters
				importStr.append(Key.class.getName());
				paramStr.append("Key userKey");
				params[param]=criteria.getUser().getId();
			}
			param++;
		}
		if (criteria.getId() != null) {
			if (buffer != null) {
				if (param>0) {
					buffer.append(" && ");
				}
				buffer.append("id==_id");
			}
			if (query != null) {
				//append import and parameters
				if (importStr.indexOf(Key.class.getName())<0) {
					if (importStr.length()>0) {
						importStr.append(',');
					}
					importStr.append(Key.class.getName());
				}
				if (paramStr.length()>0) {
					paramStr.append(',');
				}
				paramStr.append("Key _id");
				params[param]=criteria.getId();
			}
			param++;
		}
		if (param>0 && query != null) {
			importStr.insert(0,"import ");
			query.declareImports(importStr.toString());
			query.declareParameters(paramStr.toString());
		}
		return param;
	}

	public JPABook findBook(PersistenceManager em, BookCriteria criteria) {
		List<JPABook> list = selectBooks(em,criteria);
		if (list.size()==0) {
			return null;
		} else if (list.size()==1) {
			return list.get(0);
		} else {
			throw new NonUniqueResultException(criteria.toString());
		}
	}

	public JPABook mergeBook(PersistenceManager em, ModelPersist model,	JPABook book, boolean published) {
		//assign unique Key to book
		model.setGameKey(KeyFactory.keyToString(book.getId()));
		//convert Model to XML
		XMLBuilder builder = XMLBuilder.getStartInstance();
		model.toJSON(Model.EXPORT_ALL, builder);
		String mod = builder.toXML();
		Date update = new Date();
		String modelVer = String.valueOf(model.getSettings().getGameVersion());
		//existed book - add new version
		JPABookVersion version = new JPABookVersion();
		version.setBook(book.getId());
		version.setDate(update);
		version.setPublished(published);
		version.setVersions(book.getVersion());
		version = em.makePersistent(version);
		setCLOB(em, version, JPAClob.TYPE_MODEL, mod);
		if (published || (book.isPublished()==false)) {
			//if model for publish - update everything
			//if mode for save - update version and unpublished book only
			book.setLastUpdate(update);
			book.setPublished(published);
			book.setVersion(modelVer);
			book.setDescription(model.getSettings().getBookDescription());
			book.setName(model.getSettings().getBookTitle());
			book.setAuthors(model.getSettings().getBookAuthors());
			book.clearLocals();
			setCLOB(em, book, JPAClob.TYPE_MODEL, mod);
		}
		log.log(Level.INFO,"Book '"+book.getName()+"' (id="+book.getId()+", extId="+book.getExternalId()+") was updated");
		return book;
	}


	public JPABook mergeBook(PersistenceManager em, ModelPersist model, JPAUser user, boolean published) {
		JPABook book = findBook(em,user,model);
		if (book==null) {
			Date update = new Date();
			String modelVer = String.valueOf(model.getSettings().getGameVersion());
			//new book - add everywhere
			book = new JPABook();
			book.setName(model.getSettings().getBookTitle());
			book.setAuthors(model.getSettings().getBookAuthors());
			book.setExternalId(model.getGameId());
			book.setOwner(user.getId());
			book.setPublished(published);
			book.setVersion(modelVer);
			book.setDescription(model.getSettings().getBookDescription());
			book.setLastUpdate(update);
			
			book = em.makePersistent(book);
//			if (em.currentTransaction().isActive()) {
//				em.currentTransaction().commit();
//				em.currentTransaction().begin();
//			} else {
//				em.flush();
//			} 
//			em.refresh(book);
//			em.refresh(user);
			//assign unique Key to book
			model.setGameKey(KeyFactory.keyToString(book.getId()));
			//convert book to XML
			XMLBuilder builder = XMLBuilder.getStartInstance();
			model.toJSON(Model.EXPORT_ALL, builder);
			String mod = builder.toXML();
			setCLOB(em, book, JPAClob.TYPE_MODEL, mod);
			//add first version
			JPABookVersion version = new JPABookVersion();
			version.setBook(book.getId());
			version.setDate(update);
			version.setPublished(published);
			version.setVersions(modelVer);
			version = em.makePersistent(version);
			setCLOB(em, version, JPAClob.TYPE_MODEL, mod);
			log.log(Level.INFO,"Book '"+book.getName()+"' (extId="+book.getExternalId()+") was added");
			return book;
		} else {
			return mergeBook(em, model, book, published);
		}
	}

	public JPABook findBook(PersistenceManager em, JPAUser user, ModelPersist model) {
		return findBook(em,user,model.getGameId());
	}

	@SuppressWarnings("unchecked")
	public JPABook findBook(PersistenceManager em, JPAUser user, String externalId) {
		Query query =  em.newQuery(Query.JDOQL,SELECT_BOOKS_WHERE+"externalId==_externalId && owner==_user");
		query.declareImports("import "+Key.class.getName());
		query.declareParameters("Key _user, java.lang.String _externalId");
		List<JPABook> list = (List<JPABook>)query.execute(user.getId(),externalId);
		if (list.size()>0) {
			return list.get(0);
		} else {
			return null;
		}
	}

//	@SuppressWarnings("unchecked")
//	public JPABook findBook(PersistenceManager em, String id) {
//		Query query =  em.newQuery(Query.JDOQL,SELECT_BOOKS_WHERE+"externalId==_externalId && owner==_user");
//		query.declareImports("import "+Key.class.getName());
//		query.declareParameters("Key _user, java.lang.String _externalId");
//		List<JPABook> list = (List<JPABook>)query.execute(user.getId(),externalId);
//		if (list.size()>0) {
//			return list.get(0);
//		} else {
//			return null;
//		}
//	}

	public void remove(PersistenceManager em, JPABook book) {
		em.currentTransaction().begin();
		removeAllBookVersions(em, book);
		em.deletePersistent(book);
		em.currentTransaction().commit();
		em.flush();
	}

	void removeAllBookVersions(PersistenceManager em, JPABook book) {
		List<JPABookVersion> list = selectBookVersions(em,book);
		for (JPABookVersion version : list) {
			em.currentTransaction().begin();
			List<JPAClob> listClob = selectAllCLOBs(em,version.getId(),true);
			em.deletePersistentAll(listClob);
			em.currentTransaction().commit();
			em.flush();
		}
		em.deletePersistentAll(list);
		List<JPAClob> listClob = selectAllCLOBs(em,book.getId(),false);
		em.deletePersistentAll(listClob);
	}
	
	@SuppressWarnings("unchecked")
	public List<JPABookVersion> selectBookVersions(PersistenceManager em, JPABook book) {
		Query query = em.newQuery(Query.JDOQL,SELECT_BOOK_VERSIONS_WHERE+"book==_book");
		query.declareImports("import "+Key.class.getName());
		query.declareParameters("Key _book");
		return (List<JPABookVersion>)query.execute(book.getId());
	}

	@SuppressWarnings("unchecked")
	public List<JPAClob> selectAllCLOBs(PersistenceManager em, Key owner, boolean version) {
		Query query = em.newQuery(Query.JDOQL,SELECT_CLOB_WHERE_OWNER);
		query.declareImports("import "+Key.class.getName());
		query.declareParameters("Key _owner,int _version");
		return (List<JPAClob>)query.execute(owner,version ? 1:0);
	}
	
	@SuppressWarnings("unchecked")
	public List<JPAClob> selectCLOBs(PersistenceManager em, Key owner, int version, int type) {
		Query query = em.newQuery(Query.JDOQL,SELECT_CLOB_WHERE_TYPE);
		query.declareImports("import "+Key.class.getName());
		query.declareParameters("Key _owner,int _version,int _type");
		return (List<JPAClob>)query.execute(owner,version,type);
	}
	
	public String getCLOB(PersistenceManager em, JPABook book, int type) {
		switch (type) {
		case JPAClob.TYPE_HTML:
			if (book.getLocalHtml() != null) {
				return book.getLocalHtml();
			}
			break;
		case JPAClob.TYPE_TEXT:
			if (book.getLocalText() != null) {
				return book.getLocalText();
			}
			break;
		case JPAClob.TYPE_URQ:
			if (book.getLocalURQ() != null) {
				return book.getLocalURQ();
			}
			break;
		default:
			if (book.getLocalModel() != null) {
				return book.getLocalModel();
			}
		}
		return getCLOB(em,book.getId(),0,type);
	}
	public String getCLOB(PersistenceManager em, JPABookVersion version, int type) {
		switch (type) {
		case JPAClob.TYPE_HTML:
			if (version.getLocalHtml() != null) {
				return version.getLocalHtml();
			}
			break;
		case JPAClob.TYPE_TEXT:
			if (version.getLocalText() != null) {
				return version.getLocalText();
			}
			break;
		case JPAClob.TYPE_URQ:
			if (version.getLocalURQ() != null) {
				return version.getLocalURQ();
			}
			break;
		default:
			if (version.getLocalModel() != null) {
				return version.getLocalModel();
			}
		}
		return getCLOB(em,version.getId(),1,type);
	}
	
	private String getCLOB(PersistenceManager em, Key owner, int version, int type) {
		List<JPAClob> list = selectCLOBs(em, owner, version, type);
		if (list.isEmpty()) {
			return "";
		} else {
			StringBuilder builder = new StringBuilder();
			for (JPAClob clob : list) {
				builder.append(clob.getData().getValue());
			}
			return builder.toString();
		}
	}
	
	public void setCLOB(PersistenceManager em, JPABook book, int type,String data) {
		setCLOB(em,book.getId(),0,type,data);
	}
	public void setCLOB(PersistenceManager em, JPABookVersion version, int type,String data) {
		setCLOB(em,version.getId(),1,type,data);
	}
	
	private void setCLOB(PersistenceManager em, Key owner, int version, int type,String value) {
		List<JPAClob> list = selectCLOBs(em, owner, version, type);
		if (value==null || value.length()==0) {
			if (list.size()>0) { 
				em.deletePersistentAll(list);
			}
		} else {
			int pos=0;
			int order=0;
			for (JPAClob clob : list) {
				if (pos>=value.length()) {
					em.deletePersistent(clob);
				} else {
					clob.setOrder(order++);
					int last = pos+JPAClob.CLOB_SIZE;
					if (last>value.length()) {
						clob.setData(new Text(value.substring(pos)));
						pos = value.length(); 
					} else {
						clob.setData(new Text(value.substring(pos,last)));
						pos = last;
					}
				}
			}
			while (pos<value.length()) {
				//extend data
				int last = pos+JPAClob.CLOB_SIZE;
				JPAClob clob = new JPAClob();
				clob.setOwner(owner);
				clob.setOrder(order++);
				clob.setType(type);
				clob.setVersion(version);
				if (last>value.length()) {
					clob.setData(new Text(value.substring(pos)));
					pos = value.length(); 
				} else {
					clob.setData(new Text(value.substring(pos,last)));
					pos = last;
				}
				clob = em.makePersistent(clob);
			}
				
		}
	}
	
	void removeAllUserBooks(PersistenceManager em, JPAUser user) {
		BookCriteria criteria = new BookCriteria();
		criteria.setUser(user);
		List<JPABook> books = selectBooks(em, criteria);
		for (Iterator<JPABook> iterator = books.iterator(); iterator.hasNext();) {
			JPABook book = iterator.next();
			removeAllBookVersions(em, book);
		}
		em.deletePersistentAll(books);
	}

	public void lock(PersistenceManager em, JPABook book, boolean lock) {
		book.setLocked(lock);
	}

	@SuppressWarnings("unchecked")
	public JPABook findBook(PersistenceManager em, Key key) {
		Query query = em.newQuery(Query.JDOQL,SELECT_BOOKS_WHERE+"id==_id");
		query.declareImports("import "+Key.class.getName());
		query.declareParameters("Key _id");
		List<JPABook> list = (List<JPABook>)query.execute(key);
		if (list.size()==1) {
			return list.get(0);
		} else if (list.size()==0) {
			return null;
		} else {
			throw new NonUniqueResultException();
		}
	}

	@SuppressWarnings("unchecked")
	public JPABookVersion findBookVersion(PersistenceManager em, Key key) {
		Query query = em.newQuery(Query.JDOQL,SELECT_BOOK_VERSIONS_WHERE+"id==_id");
		query.declareImports("import "+Key.class.getName());
		query.declareParameters("Key _id");
		List<JPABookVersion> list = (List<JPABookVersion>)query.execute(key);
		if (list.size()==1) {
			return list.get(0);
		} else if (list.size()==0) {
			return null;
		} else {
			throw new NonUniqueResultException();
		}
	}

}
