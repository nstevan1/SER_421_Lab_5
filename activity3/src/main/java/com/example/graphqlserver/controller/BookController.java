package com.example.graphqlserver.controller;

import com.example.graphqlserver.dto.input.AddBookInput;
import com.example.graphqlserver.dto.input.DeleteBookInput;
import com.example.graphqlserver.dto.output.AddBookPayload;
import com.example.graphqlserver.dto.output.DeleteBookPayload;
import com.example.graphqlserver.model.Book;
import com.example.graphqlserver.repository.AuthorRepository;
import com.example.graphqlserver.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.stream.Collectors;

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
        return bookRepository.findAll();
    }

    @QueryMapping
    public Book bookByISBN(@Argument("isbn") String isbn) {
        return bookRepository.findById(isbn).orElse(null);
    }

    @QueryMapping
    public List<Book> bookByAuthorId(@Argument Long authorId) {
        return bookRepository.findByAuthorId(authorId);
    }

    @QueryMapping
    public List<String> bookTitlesByAuthorFirstName(@Argument String firstName) {
        return authorRepository.findByFirstNameIgnoreCase(firstName).stream()
            .flatMap(a -> bookRepository.findByAuthorId(a.getId()).stream())
            .map(Book::getTitle)
            .collect(Collectors.toList());
    }

    @MutationMapping
    public AddBookPayload addBook(@Argument AddBookInput input) {
        authorRepository.findById((long) input.authorId())
            .orElseThrow(() -> new IllegalArgumentException("Author with ID " + input.authorId() + " does not exist"));
        Book book = new Book(input.isbn(), input.title(), (long) input.authorId());
        bookRepository.save(book);
        return new AddBookPayload(book);
    }

    @MutationMapping
    public DeleteBookPayload deleteBookByISBN(@Argument DeleteBookInput input) {
        if (bookRepository.existsById(input.isbn())) {
            bookRepository.deleteById(input.isbn());
            return new DeleteBookPayload(input.isbn());
        }
        return new DeleteBookPayload(null);
    }
}
