package com.example.graphqlserver.controller;

import com.example.graphqlserver.dto.input.AddAuthorInput;
import com.example.graphqlserver.dto.input.UpdateAuthorFirstNameInput;
import com.example.graphqlserver.dto.output.AddAuthorPayload;
import com.example.graphqlserver.dto.output.UpdateAuthorPayload;
import com.example.graphqlserver.model.Author;
import com.example.graphqlserver.model.Book;
import com.example.graphqlserver.repository.AuthorRepository;
import com.example.graphqlserver.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class AuthorController {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

    @Autowired
    public AuthorController(AuthorRepository authorRepository, BookRepository bookRepository) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
    }

    @QueryMapping
    public List<Author> authors() {
        return authorRepository.findAll();
    }

    @QueryMapping
    public Author authorById(@Argument Long id) {
        return authorRepository.findById(id).orElse(null);
    }

    @QueryMapping
    public List<Author> authorBylastName(@Argument String lastName) {
        return authorRepository.findByLastNameIgnoreCase(lastName);
    }

    @MutationMapping
    public AddAuthorPayload addAuthor(@Argument AddAuthorInput input) {
        Author author = new Author(input.firstName(), input.lastName());
        author = authorRepository.save(author);
        return new AddAuthorPayload(author);
    }

    @MutationMapping
    public UpdateAuthorPayload updateAuthorFirstName(@Argument UpdateAuthorFirstNameInput input) {
        return authorRepository.findById((long) input.authorId())
            .map(author -> {
                String oldName = author.getFirstName();
                author.setFirstName(input.newFirstName());
                authorRepository.save(author);
                return new UpdateAuthorPayload(oldName);
            })
            .orElse(new UpdateAuthorPayload(null));
    }

    @SchemaMapping(typeName = "Author", field = "books")
    public List<Book> books(Author author) {
        return bookRepository.findByAuthorId(author.getId());
    }
}
