package com.example.graphqlserver.model;


import java.util.ArrayList;
import java.util.List;

public class Author {

    private int id;
    private String firstName;
    private final String lastName;
    private List<Book> books = new ArrayList<>();

    public Author(int id, String firstName, String lastName, List<Book> books) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.books = books;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public List<Book> getBooks() { return books; }
    public void setBooks(List<Book> books) { this.books = books; }
}
