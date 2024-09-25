package com.pbutkus.books.services;

import com.pbutkus.books.models.Book;
import com.pbutkus.books.repositories.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    private Book book1, book2;

    @BeforeEach
    void setUp() {
        book1 = new Book(UUID.randomUUID(), "The Great Gatsby", "F. Scott Fitzgerald", "Novel", 1925, 0);
        book2 = new Book(UUID.randomUUID(), "To Kill a Mockingbird", "Harper Lee", "Novel", 1960, 0);
    }

    @Nested
    @DisplayName("findAll tests")
    class FindAllTests {
        @Test
        @DisplayName("Should return all books when books exist")
        void findAll_WithExistingBooks_ReturnsAllBooks() {
            when(bookRepository.findAll()).thenReturn(Arrays.asList(book1, book2));

            List<Book> result = bookService.findAll();

            assertEquals(2, result.size());
            assertTrue(result.contains(book1));
            assertTrue(result.contains(book2));
            verify(bookRepository).findAll();
        }

        @Test
        @DisplayName("Should return empty list when no books exist")
        void findAll_WithNoBooks_ReturnsEmptyList() {
            when(bookRepository.findAll()).thenReturn(List.of());

            List<Book> result = bookService.findAll();

            assertTrue(result.isEmpty());
            verify(bookRepository).findAll();
        }
    }

    @Nested
    @DisplayName("searchBooks tests")
    class SearchBooksTests {
        @Test
        @DisplayName("Should return matching books when searching by title")
        void searchBooks_ByTitle_ReturnsMatchingBooks() {
            when(bookRepository.findAll(any(Specification.class))).thenReturn(List.of(book1));

            List<Book> result = bookService.searchBooks("Great", null, null, null, null);

            assertEquals(1, result.size());
            assertEquals(book1, result.get(0));
            verify(bookRepository).findAll(any(Specification.class));
        }

        @Test
        @DisplayName("Should return matching books when searching by author")
        void searchBooks_ByAuthor_ReturnsMatchingBooks() {
            when(bookRepository.findAll(any(Specification.class))).thenReturn(List.of(book2));

            List<Book> result = bookService.searchBooks(null, "Harper", null, null, null);

            assertEquals(1, result.size());
            assertEquals(book2, result.get(0));
            verify(bookRepository).findAll(any(Specification.class));
        }

        @Test
        @DisplayName("Should return matching books when searching by year range")
        void searchBooks_ByYearRange_ReturnsMatchingBooks() {
            when(bookRepository.findAll(any(Specification.class))).thenReturn(Arrays.asList(book1, book2));

            List<Book> result = bookService.searchBooks(null, null, null, 1920, 1960);

            assertEquals(2, result.size());
            assertTrue(result.contains(book1));
            assertTrue(result.contains(book2));
            verify(bookRepository).findAll(any(Specification.class));
        }

        @Test
        @DisplayName("Should return empty list when no books match criteria")
        void searchBooks_NoMatch_ReturnsEmptyList() {
            when(bookRepository.findAll(any(Specification.class))).thenReturn(List.of());

            List<Book> result = bookService.searchBooks("Nonexistent", null, null, null, null);

            assertTrue(result.isEmpty());
            verify(bookRepository).findAll(any(Specification.class));
        }
    }

    @Nested
    @DisplayName("updateRating tests")
    class UpdateRatingTests {
        @Test
        @DisplayName("Should update rating when book exists")
        void updateRating_ExistingBook_UpdatesRating() {
            UUID id = book1.getId();
            when(bookRepository.findById(id)).thenReturn(Optional.of(book1));
            when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Book result = bookService.updateRating(id.toString(), 5);

            assertNotNull(result);
            assertEquals(5, result.getRating());
            verify(bookRepository).findById(id);
            verify(bookRepository).save(book1);
        }

        @Test
        @DisplayName("Should return null when book doesn't exist")
        void updateRating_NonexistentBook_ReturnsNull() {
            UUID id = UUID.randomUUID();
            when(bookRepository.findById(id)).thenReturn(Optional.empty());

            Book result = bookService.updateRating(id.toString(), 5);

            assertNull(result);
            verify(bookRepository).findById(id);
            verify(bookRepository, never()).save(any(Book.class));
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException for invalid UUID")
        void updateRating_InvalidUUID_ThrowsException() {
            assertThrows(IllegalArgumentException.class, () -> bookService.updateRating("invalid-uuid", 5));
            verify(bookRepository, never()).findById(any());
            verify(bookRepository, never()).save(any(Book.class));
        }
    }
}
