package com.pbutkus.books.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Book {

    @Id
    private UUID id;
    private String title;
    private String author;
    private String genre;
    private Integer publicationYear;
    private Integer rating;

}
