package com.pbutkus.books.services;

import com.pbutkus.books.models.Book;
import com.pbutkus.books.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    public List<Book> searchBooks(String title, String author, String genre, Integer yearFrom, Integer yearTo) {
        Specification<Book> spec = Specification.where(null);

        if (title != null && !title.isEmpty()) {
            spec = spec.and(((root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get(
                    "title")), "%" + title.toLowerCase() + "%")));
        }

        if (author != null && !author.isEmpty()) {
            spec = spec.and(((root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get(
                    "author")), "%" + author.toLowerCase() + "%")));
        }

        if (genre != null && !genre.isEmpty()) {
            spec = spec.and(((root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get(
                    "genre")), "%" + genre.toLowerCase() + "%")));
        }

        if (yearFrom != null) {
            spec = spec.and(((root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get(
                    "publicationYear"), yearFrom)));
        }

        if (yearTo != null) {
            spec = spec.and(((root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get(
                    "publicationYear"), yearTo)));
        }

        return bookRepository.findAll(spec);
    }

    public Book updateRating(String id, Integer rating) {
        UUID uuid = UUID.fromString(id);
        Book book = bookRepository.findById(uuid).orElse(null);

        if (book == null) {
            return null;
        }

        book.setRating(rating);
        return bookRepository.save(book);
    }
}
