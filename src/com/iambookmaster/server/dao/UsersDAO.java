package com.iambookmaster.server.dao;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.users.User;
import com.iambookmaster.server.beans.JPAUser;

public class UsersDAO {

	private static final Logger log = Logger.getLogger(UsersDAO.class.getName());
	
	private static final String SELECT_USERS="SELECT from "+JPAUser.class.getName();
	private static final String SELECT_USERS_WHERE=SELECT_USERS+" WHERE ";
	
	@SuppressWarnings("unchecked")
	public JPAUser findUserByEmail(PersistenceManager em, String email) {
		Query query =  em.newQuery(Query.JDOQL,"SELECT from "+JPAUser.class.getName()+" WHERE email==_email");
		query.declareParameters("String _email");
		List<JPAUser> list = (List<JPAUser>)query.execute(email);
		if (list.size()>0) {
			return list.get(0);
		} else {
			return null;
		}
	}

	public JPAUser findOrCreateUser(PersistenceManager em, User usr) {
		JPAUser user = findUserByEmail(em, usr.getEmail());
		if (user==null) {
			em.currentTransaction().begin();
			user = em.newInstance(JPAUser.class);
			user.setEmail(usr.getEmail());
			user.setNick(usr.getNickname());
			em.makePersistent(user);
			em.currentTransaction().commit();
			em.flush();
			log.log(Level.INFO,"New user "+usr.getEmail());
		}
		return user;
	}

	@SuppressWarnings("unchecked")
	public List<JPAUser> selectUsers(PersistenceManager em, UserCriteria criteria) {
		StringBuffer buffer = new StringBuffer(SELECT_USERS_WHERE);
		Query query;
		int par= applyCriteria(criteria,buffer,null,null);
		if (par>0) {
			Object[] params= new Object[par];
			query = em.newQuery(Query.JDOQL,buffer.toString());
			applyCriteria(criteria,null,query,params);
			return (List<JPAUser>)query.executeWithArray(params);
		} else {
			return (List<JPAUser>)em.newQuery(Query.JDOQL,SELECT_USERS).execute();
		}
	}
	
	private int applyCriteria(UserCriteria criteria, StringBuffer buffer,Query query,Object[] params) {
		StringBuffer importStr=null;
		StringBuffer paramStr=null;
		if (query != null) {
			importStr = new StringBuffer();
			paramStr = new StringBuffer();
		}
		int param=0;
		if (criteria.getEmail() != null) {
			if (buffer != null) {
				buffer.append("email==_email");
			}
			if (query != null) {
				//append import and parameters
				paramStr.append("String _email");
				params[param]=criteria.getEmail();
			}
			param++;
		}
		if (criteria.getName() != null) {
			if (buffer != null) {
				if (param>0) {
					buffer.append(" && ");
				}
				buffer.append("nick==_nick");
			}
			if (query != null) {
				//append import and parameters
				if (paramStr.length()>0) {
					paramStr.append(',');
				}
				paramStr.append("String _nick");
				params[param]=criteria.getName();
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
				if (paramStr.length()>0) {
					paramStr.append(',');
				}
				paramStr.append("Key _id");
				if (importStr.length()>0) {
					importStr.append(',');
				}
				importStr.append(Key.class.getName());
				params[param]=criteria.getId();
			}
			param++;
		}
		if (param>0 && query != null) {
			if (importStr.length()>0) {
				importStr.insert(0,"import ");
				query.declareImports(importStr.toString());
			}
			query.declareParameters(paramStr.toString());
		}
		return param;
	}

	public void remove(PersistenceManager em, JPAUser user) {
		em.currentTransaction().begin();
		DAO.getBookDAO().removeAllUserBooks(em, user);
		em.deletePersistent(user);
		em.currentTransaction().commit();
		em.flush();
	}

	public void lock(PersistenceManager em, JPAUser user, boolean lock) {
		user.setLocked(lock);
	}

}
