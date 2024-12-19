package org.example.pr4;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.core.RSocketConnector;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;
import reactor.core.publisher.Flux;

import java.time.Duration;

public class LibraryClient {
    private final RSocket socket;

    public LibraryClient() {
        this.socket = RSocketConnector.create()
                .connect(TcpClientTransport.create("localhost", 7000))
                .block();
    }

    public void addBook(String title, String author) {
        socket.requestResponse(DefaultPayload.create(title + "," + author))
                .map(Payload::getDataUtf8)
                .doOnNext(System.out::println)
                .block();
    }

    public void getAllBooks() {
        socket.requestStream(DefaultPayload.create(""))
                .map(Payload::getDataUtf8)
                .doOnNext(System.out::println)
                .blockLast();
    }

    public void logAction(String action) {
        socket.fireAndForget(DefaultPayload.create(action)).block();
    }

    public void startRealtimeUpdates() {
        Flux<String> bookTitles = Flux.interval(Duration.ofSeconds(1))
                .map(i -> "New Book " + i);

        socket.requestChannel(bookTitles.map(DefaultPayload::create))
                .map(Payload::getDataUtf8)
                .doOnNext(System.out::println)
                .blockLast();
    }

    public static void main(String[] args) {
        LibraryClient client = new LibraryClient();

        client.addBook("1984", "George Orwell");
        client.addBook("To Kill a Mockingbird", "Harper Lee");

        client.getAllBooks();

        client.logAction("User viewed all books");

//        client.startRealtimeUpdates();
    }
}
