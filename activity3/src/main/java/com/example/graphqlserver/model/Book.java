package com.example.graphqlserver.model;

import jakarta.persistence.*;

@Entity
@Table(name = "book")
public class Book {

    @Id
    private String isbn;

    private String title;

    @Column(name = "author_id")
    private Long authorId;

    public Book() {}

    public Book(String isbn, String title, Long authorId) {
        this.isbn = isbn;
        this.title = title;
        this.authorId = authorId;
    }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }
}
