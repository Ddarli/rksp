package org.example.pr4;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.SocketAcceptor;
import io.rsocket.core.RSocketServer;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.util.DefaultPayload;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

public class LibraryServer {
    private LibraryDatabase db;
    private List<Book> books = new ArrayList<>();

    public LibraryServer(LibraryDatabase db) {
        this.db = db;
    }
    public LibraryDatabase getDb() {
        return db;
    }

    public void start() {
        RSocketServer.create(SocketAcceptor.with(new LibrarySocketAcceptor()))
                .bindNow(TcpServerTransport.create("localhost", 7000));

        System.out.println("Server started on port 7000");
    }

    public class LibrarySocketAcceptor implements RSocket {
        // Request-Response: Add a book
        @Override
        public Mono<Payload> requestResponse(Payload payload) {
            String bookData = payload.getDataUtf8();
            String[] parts = bookData.split(",");
            Book book = new Book(parts[0], parts[1]);
            db.addBookToDatabase(book);
            return Mono.just(DefaultPayload.create("Book added: " + book))
                    .doFinally(signalType -> System.out.println("Finished adding book."));
        }

        // Request-Stream: Get all books
        @Override
        public Flux<Payload> requestStream(Payload payload) {
            return Flux.fromIterable(db.getAllBooksFromDatabase())
                    .map(book -> DefaultPayload.create(book.toString()))
                    .doFinally(signalType -> System.out.println("Finished fetching books."));
        }

        // Fire-and-Forget: Log an action
        @Override
        public Mono<Void> fireAndForget(Payload payload) {
            System.out.println("Action logged: " + payload.getDataUtf8());
            return Mono.empty();
        }

        // Channel: Real-time book updates
        @Override
        public Flux<Payload> requestChannel(Publisher<Payload> payloads) {
            return Flux.from(payloads)
                    .map(Payload::getDataUtf8)
                    .map(data -> {
                        Book book = new Book(data, "Unknown");
                        books.add(book);
                        db.addBookToDatabase(book);  // Сохраняем книгу в базе данных
                        return DefaultPayload.create("Book added: " + data);
                    })
                    .doFinally(signalType -> System.out.println("Finished processing channel."));
        }
    }

    public static void main(String[] args) {
        LibraryDatabase db = new LibraryDatabase();
        LibraryServer server = new LibraryServer(db);
        server.start();
        try {
            Thread.sleep(100000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
