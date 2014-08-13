package com.iambookmaster.server.dao;


public final class DAO {
    private static UsersDAO usersDAO = new UsersDAO();
    public static UsersDAO getUsersDAO() {
		return usersDAO;
	}

    private static BooksDAO booksDAO = new BooksDAO();
	public static BooksDAO getBookDAO() {
		return booksDAO;
	}
 
}