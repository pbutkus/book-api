package com.pbutkus.books.controllers;

import com.pbutkus.books.dto.RatingRequest;
import com.pbutkus.books.models.Book;
import com.pbutkus.books.services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping()
    public ResponseEntity<List<Book>> getAllBooks() {
        return ResponseEntity.ok(bookService.findAll());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Book>> searchBooks(@RequestParam(required = false) String title,
                                  @RequestParam(required = false) String author,
                                  @RequestParam(required = false) String genre,
                                  @RequestParam(required = false) Integer yearFrom,
                                  @RequestParam(required = false) Integer yearTo) {
        List<Book> books = bookService.searchBooks(title, author, genre, yearFrom, yearTo);

        if (books.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(books);
    }

    @PostMapping("/rate/{id}")
    public ResponseEntity<Book> rateBook(@PathVariable String id, @RequestBody RatingRequest rating) {
        if (rating.getRating() < 1 || rating.getRating() > 5) {
            return ResponseEntity.badRequest().build();
        }

        Book updatedBook = bookService.updateRating(id, rating.getRating());

        if (updatedBook == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(updatedBook);
    }

}
