package org.example.pr4;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LibraryDatabase {
    private Connection connection;

    public LibraryDatabase() {
        try {
            String url = "jdbc:postgresql://localhost:5432/postgres";
            String username = "postgres";
            String password = "password";
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Соединение с базой данных установлено.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addBookToDatabase(Book book) {
        try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO books (title, author) VALUES (?, ?)")) {
            stmt.setString(1, book.title);
            stmt.setString(2, book.author);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Book> getAllBooksFromDatabase() {
        List<Book> books = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement("SELECT title, author FROM books")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                books.add(new Book(rs.getString("title"), rs.getString("author")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return books;
    }
}