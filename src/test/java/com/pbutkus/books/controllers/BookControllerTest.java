package com.pbutkus.books.controllers;

import com.pbutkus.books.dto.RatingRequest;
import com.pbutkus.books.models.Book;
import com.pbutkus.books.services.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BookControllerTest {

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookController bookController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("Test getAllBooks")
    class GetAllBooksTests {
        @Test
        @DisplayName("Should return empty List if there are no books")
        void getAllBooks_Empty() {
            when(bookService.findAll()).thenReturn(new ArrayList<>());

            ResponseEntity<List<Book>> response = bookController.getAllBooks();

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(0, response.getBody().size());

            verify(bookService).findAll();
        }

        @Test
        @DisplayName("Should return List of books when there are books")
        void getAllBooks_NotEmpty() {
            Book book1 = new Book(UUID.randomUUID(), "Harry Potter", "J.K. Rowling", "Fantasy", 1997, 0);
            Book book2 = new Book(UUID.randomUUID(), "Harry Potter and the Deathly Hallows", "J.K. Rowling", "Fantasy"
                    , 2007, 0);

            List<Book> books = Arrays.asList(book1, book2);

            when(bookService.findAll()).thenReturn(books);

            ResponseEntity<List<Book>> response = bookController.getAllBooks();

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(2, response.getBody().size());
            assertEquals("Harry Potter", response.getBody().get(0).getTitle());
            assertEquals("Harry Potter and the Deathly Hallows", response.getBody().get(1).getTitle());

            verify(bookService).findAll();
        }
    }

    @Nested
    @DisplayName("Test searchBooks")
    class SearchBooks {
        @Test
        @DisplayName("Should return NOT_FOUND when there are no books")
        void searchBooks_Empty() {
            when(bookService.searchBooks(null, null, null, null, null)).thenReturn(new ArrayList<>());

            ResponseEntity<List<Book>> response = bookController.searchBooks(null, null, null, null, null);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertNull(response.getBody());

            verify(bookService).searchBooks(null, null, null, null, null);
        }

        @Test
        @DisplayName("Should return all books when queried with no parameters")
        void searchBooks_AllBooks() {
            Book book1 = new Book(UUID.randomUUID(), "Harry Potter", "J.K. Rowling", "Fantasy", 1997, 0);
            Book book2 = new Book(UUID.randomUUID(), "Harry Potter and the Deathly Hallows", "J.K. Rowling", "Fantasy"
                    , 2007, 0);

            List<Book> books = Arrays.asList(book1, book2);

            when(bookService.searchBooks(null, null, null, null, null)).thenReturn(books);

            ResponseEntity<List<Book>> response = bookController.searchBooks(null, null, null, null, null);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(2, response.getBody().size());

            verify(bookService).searchBooks(null, null, null, null, null);
        }

        @Test
        @DisplayName("Should return a matching book when all parameters match")
        void searchBooks_AllParametersProvided() {
            String title = "Harry Potter";
            String author = "Rowling";
            String genre = "Fantasy";
            Integer yearFrom = 1997;
            Integer yearTo = 1997;

            Book book = new Book(UUID.randomUUID(), "Harry Potter", "J.K. Rowling", "Fantasy", 1997, 0);

            List<Book> books = List.of(book);

            when(bookService.searchBooks(title, author, genre, yearFrom, yearTo)).thenReturn(books);

            ResponseEntity<List<Book>> response = bookController.searchBooks(title, author, genre, yearFrom, yearTo);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(1, response.getBody().size());

            verify(bookService).searchBooks(title, author, genre, yearFrom, yearTo);
        }

        @Test
        @DisplayName("Should return matching books when title matches several books")
        void searchBooks_MatchingBook() {
            String title = "Harry Potter";

            Book book1 = new Book(UUID.randomUUID(), "Harry Potter", "J.K. Rowling", "Fantasy", 1997, 0);
            Book book2 = new Book(UUID.randomUUID(), "Harry Potter and the Deathly Hallows", "J.K. Rowling", "Fantasy"
                    , 2007, 0);

            List<Book> books = Arrays.asList(book1, book2);

            when(bookService.searchBooks(title, null, null, null, null)).thenReturn(books);

            ResponseEntity<List<Book>> response = bookController.searchBooks(title, null, null, null, null);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(2, response.getBody().size());

            verify(bookService).searchBooks(title, null, null, null, null);
        }
    }

    @Nested
    @DisplayName("Test rateBook")
    class RateBookTests {
        @Test
        @DisplayName("Should return book when rating is valid")
        void rateBook_ValidRating_ReturnsUpdatedBook() {
            String bookId = UUID.randomUUID().toString();
            RatingRequest ratingRequest = new RatingRequest();
            ratingRequest.setRating(4);

            Book updatedBook = new Book();
            updatedBook.setId(UUID.fromString(bookId));
            updatedBook.setTitle("Test Book");
            updatedBook.setRating(4);

            when(bookService.updateRating(bookId, 4)).thenReturn(updatedBook);

            ResponseEntity<Book> response = bookController.rateBook(bookId, ratingRequest);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(4, response.getBody().getRating());
            assertEquals("Test Book", response.getBody().getTitle());

            verify(bookService).updateRating(bookId, 4);
        }

        @Test
        @DisplayName("Should return BAD_REQUEST when rating is invalid")
        void rateBook_InvalidRating_ReturnsBadRequest() {
            String bookId = UUID.randomUUID().toString();
            RatingRequest ratingRequest = new RatingRequest();
            ratingRequest.setRating(6);

            ResponseEntity<Book> response = bookController.rateBook(bookId, ratingRequest);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertNull(response.getBody());

            verify(bookService, never()).updateRating(anyString(), anyInt());
        }

        @Test
        @DisplayName("Should return NOT_FOUND when book doesn't exist")
        void rateBook_BookNotFound_ReturnsNotFound() {
            String bookId = UUID.randomUUID().toString();
            RatingRequest ratingRequest = new RatingRequest();
            ratingRequest.setRating(4);

            when(bookService.updateRating(bookId, 4)).thenReturn(null);

            ResponseEntity<Book> response = bookController.rateBook(bookId, ratingRequest);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertNull(response.getBody());

            verify(bookService).updateRating(bookId, 4);
        }
    }
}
