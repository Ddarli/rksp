package com.example.pr7;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import lombok.Data;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

// Latyshev IKBO-16-21
@SpringBootApplication
public class Pr7Application {

	public static void main(String[] args) {
		SpringApplication.run(Pr7Application.class, args);
	}

}


@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("books")
class Book {
    @Id
    private Long id;
    private String title;
    private String author;
    private int year;
}

interface BookRepository extends ReactiveCrudRepository<Book, Long> {
	Flux<Book> findAll(Sort sort);
	Flux<Book> findByAuthorIgnoreCase(String author);
}

@Service
class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Flux<Book> getAllBooks() {
        return bookRepository.findAll();
    }

	public Flux<Book> getAllBooks(PageRequest pageRequest) {
        return bookRepository.findAll(pageRequest.getSort())
                .skip((long) pageRequest.getPageNumber() * pageRequest.getPageSize())
                .take(pageRequest.getPageSize());
    }

    public Mono<Book> getBookById(Long id) {
        return bookRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException
				(HttpStatus.NOT_FOUND, "Book not found")));
    }

    public Mono<Book> createBook(Book book) {
        return bookRepository.save(book);
    }

    public Mono<Book> updateBook(Long id, Book book) {
        return bookRepository.findById(id)
                .flatMap(existingBook -> {
                    existingBook.setTitle(book.getTitle());
                    existingBook.setAuthor(book.getAuthor());
                    existingBook.setYear(book.getYear());
                    return bookRepository.save(existingBook);
                })
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found")));
    }

    public Mono<Void> deleteBook(Long id) {
        return bookRepository.deleteById(id);
    }

    public Flux<Book> getBooksByAuthor(String author) {
        return bookRepository.findByAuthorIgnoreCase(author);
    }
}

@RestController
@RequestMapping("/api/books")
class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("path")
    public String getMethodName(@RequestParam String param) {
        return new String();
    }
    
    public Flux<Book> getAllBooks() {
        return bookService.getAllBooks();
    }


	@GetMapping
    public Flux<Book> getAllBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        return bookService.getAllBooks(pageRequest)
                .doOnNext(book -> System.out.println("Emitting book: " + book.getTitle()))  // для демонстрации
                .onErrorResume(e -> {
                    System.err.println("Error occurred: " + e.getMessage());
                    return Flux.empty();
                });
    }

    @GetMapping("/{id}")
    public Mono<Book> getBookById(@PathVariable Long id) {
        return bookService.getBookById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Book> createBook(@RequestBody Book book) {
        return bookService.createBook(book);
    }

    @PutMapping("/{id}")
    public Mono<Book> updateBook(@PathVariable Long id, @RequestBody Book book) {
        return bookService.updateBook(id, book);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteBook(@PathVariable Long id) {
        return bookService.deleteBook(id);
    }

    @GetMapping("/author/{author}")
    public Flux<Book> getBooksByAuthor(@PathVariable String author) {
        return bookService.getBooksByAuthor(author);
    }
}