package com.iambookmaster.server;

import java.io.IOException;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * This filter strips all useless spaces and carrage returns from JSP output 
 * @author G.Gadyatskiy
 *
 */
public class TransactionInViewFilter implements Filter {

//    private static final EntityManagerFactory emfInstance = Persistence.createEntityManagerFactory("transactions-optional");
    private static final PersistenceManagerFactory pmfInstance = JDOHelper.getPersistenceManagerFactory("transactions-optional");
    
	private static final String ENTIYI_MANAGER = "com.iambookmaster.server.TransactionInViewFilter";
	
//	public static EntityManager getEM(ServletRequest request) {
//		return (EntityManager)request.getAttribute(ENTIYI_MANAGER);
//	}
	public static PersistenceManager getEM(ServletRequest request) {
		return (PersistenceManager)request.getAttribute(ENTIYI_MANAGER);
	}

	public void init(FilterConfig filterConfig) {
	}
	  
	public void destroy() {
	}
  
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
//		EntityManager em = emfInstance.createEntityManager();
		PersistenceManager em = pmfInstance.getPersistenceManager();
		request.setAttribute(ENTIYI_MANAGER, em);
		chain.doFilter(request, response);
		em.close();
		request.removeAttribute(ENTIYI_MANAGER);
    }
	
}

