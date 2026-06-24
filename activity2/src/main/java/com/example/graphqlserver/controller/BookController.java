package com.example.graphqlserver.controller;

import com.example.graphqlserver.dto.input.AddBookInput;
import com.example.graphqlserver.dto.input.DeleteBookInput;
import com.example.graphqlserver.dto.output.AddBookPayload;
import com.example.graphqlserver.dto.output.DeleteBookPayload;
import com.example.graphqlserver.model.Author;
import com.example.graphqlserver.model.Book;
import com.example.graphqlserver.repository.AuthorRepository;
import com.example.graphqlserver.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

@Controller
public class BookController {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    @Autowired
    public BookController(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }

    @QueryMapping
    public List<Book> books() {
        return bookRepository.getBooks();
    }

    @QueryMapping
    public Book bookByISBN(@Argument("isbn") String isbn) {
        return bookRepository.getBookByISBN(isbn);
    }

    @QueryMapping
    public List<Book> bookByAuthorId(@Argument int authorId) {
        return BookRepository.getBooksByAuthorId(authorId);
    }

    @QueryMapping
    public List<String> bookTitlesByAuthorFirstName(@Argument String firstName) {
        List<String> titles = new ArrayList<>();
        for (Author author : authorRepository.getAuthors()) {
            if (author.getFirstName().equalsIgnoreCase(firstName)) {
                for (Book book : author.getBooks()) {
                    titles.add(book.getTitle());
                }
            }
        }
        return titles;
    }

    @MutationMapping
    public AddBookPayload addBook(@Argument AddBookInput input) {
        Author author = authorRepository.getAuthorById(input.authorId());
        if (author == null) {
            throw new IllegalArgumentException("Author with ID " + input.authorId() + " does not exist");
        }
        var book = bookRepository.save(input.isbn(), input.title(), input.authorId());
        author.getBooks().add(book);
        return new AddBookPayload(book);
    }

    @MutationMapping
    public DeleteBookPayload deleteBookByISBN(@Argument DeleteBookInput input) {
        Book book = bookRepository.getBookByISBN(input.isbn());
        if (book == null) {
            return new DeleteBookPayload(null);
        }
        Author author = authorRepository.getAuthorById(book.getAuthorId());
        if (author != null) {
            author.getBooks().remove(book);
        }
        String isbn = bookRepository.deleteByISBN(input.isbn());
        return new DeleteBookPayload(isbn);
    }
}
