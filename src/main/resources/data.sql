CREATE TABLE IF NOT EXISTS BOOK (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    genre VARCHAR(255),
    publication_year INT,
    rating INT
);

INSERT INTO BOOK (id, title, author, genre, publication_year, rating) VALUES
(RANDOM_UUID(), 'To Kill a Mockingbird', 'Harper Lee', 'Fiction', 1960, 0),
(RANDOM_UUID(), '1984', 'George Orwell', 'Science Fiction', 1949, 0),
(RANDOM_UUID(), 'Pride and Prejudice', 'Jane Austen', 'Romance', 1813, 0),
(RANDOM_UUID(), 'The Great Gatsby', 'F. Scott Fitzgerald', 'Fiction', 1925, 0),
(RANDOM_UUID(), 'The Hobbit', 'J.R.R. Tolkien', 'Fantasy', 1937, 0);