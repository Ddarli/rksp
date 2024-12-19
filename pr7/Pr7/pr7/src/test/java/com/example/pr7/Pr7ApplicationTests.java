package com.example.pr7;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class Pr7ApplicationTests {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @Test
    void getAllBooks() {
        Book book1 = new Book(1L, "Title1", "Author1", 2021);
        Book book2 = new Book(2L, "Title2", "Author2", 2022);
        when(bookRepository.findAll()).thenReturn(Flux.just(book1, book2));

        Flux<Book> result = bookService.getAllBooks();

        StepVerifier.create(result)
                .expectNext(book1)
                .expectNext(book2)
                .verifyComplete();
    }

    @Test
    void getBookById() {
        Long id = 1L;
        Book book = new Book(id, "Title", "Author", 2021);
        when(bookRepository.findById(id)).thenReturn(Mono.just(book));

        Mono<Book> result = bookService.getBookById(id);

        StepVerifier.create(result)
                .expectNext(book)
                .verifyComplete();
    }

    @Test
    void createBook() {
        Book book = new Book(null, "Title", "Author", 2021);
        Book savedBook = new Book(1L, "Title", "Author", 2021);
        when(bookRepository.save(book)).thenReturn(Mono.just(savedBook));

        Mono<Book> result = bookService.createBook(book);

        StepVerifier.create(result)
                .expectNext(savedBook)
                .verifyComplete();
    }

    @Test
    void updateBook() {
        Long id = 1L;
    Book existingBook = new Book(id, "OldTitle", "OldAuthor", 2020);
    Book updatedBook = new Book(id, "NewTitle", "NewAuthor", 2021);
    
    when(bookRepository.findById(id)).thenReturn(Mono.just(existingBook));
    when(bookRepository.save(any(Book.class))).thenReturn(Mono.just(updatedBook));

    Mono<Book> result = bookService.updateBook(id, updatedBook);

    StepVerifier.create(result)
            .expectNext(updatedBook)
            .verifyComplete();
	}
}
