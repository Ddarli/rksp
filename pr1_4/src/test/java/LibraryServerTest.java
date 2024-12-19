import io.rsocket.Payload;
import io.rsocket.util.DefaultPayload;
import org.example.pr4.Book;
import org.example.pr4.LibraryDatabase;
import org.example.pr4.LibraryServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LibraryServerTest {

    private LibraryDatabaseStub dbStub;
    private LibraryServer server;

    @BeforeEach
    public void setUp() {
        dbStub = new LibraryDatabaseStub();
        server = new LibraryServer(dbStub);
    }

    @Test
    public void testAddBookToDatabase() {
        // Act
        Mono<Payload> result = server.new LibrarySocketAcceptor().requestResponse(DefaultPayload.create("New Book,Unknown"));

        // Assert
        assertEquals("Book added: New Book by Unknown", result.block().getDataUtf8());

        assertEquals(1, dbStub.getAllBooksFromDatabase().size());
        assertEquals("New Book", dbStub.getAllBooksFromDatabase().get(0).getTitle());
        assertEquals("Unknown", dbStub.getAllBooksFromDatabase().get(0).getAuthor());
    }

    @Test
    public void testGetAllBooksFromDatabase() {
        // Arrange
        dbStub.addBookToDatabase(new Book("Book 1", "Author 1"));
        dbStub.addBookToDatabase(new Book("Book 2", "Author 2"));

        // Act
        Flux<Payload> result = server.new LibrarySocketAcceptor().requestStream(DefaultPayload.create(""));

        // Assert
        List<Payload> payloads = result.collectList().block();
        assertEquals(2, payloads.size());
        assertEquals("Book 1 by Author 1", payloads.get(0).getDataUtf8());
        assertEquals("Book 2 by Author 2", payloads.get(1).getDataUtf8());
    }

    public class LibraryDatabaseStub extends LibraryDatabase {
        private List<Book> books = new ArrayList<>();

        @Override
        public void addBookToDatabase(Book book) {
            // Просто добавляем книгу в список, ничего не делая с реальной базой данных
            books.add(book);
        }

        @Override
        public List<Book> getAllBooksFromDatabase() {
            // Возвращаем фиксированный список книг для тестирования
            return new ArrayList<>(books);
        }
    }
}
