package com.izaya.databse.services;

import com.izaya.databse.domain.entities.BookEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface BookService {

    BookEntity createUpdateBook(String isbn, BookEntity book);
    List<BookEntity> findAll();

    Page<BookEntity> findAll(Pageable pageable);

    Optional<BookEntity> findOne(String isbn);
    boolean isExists(String isbn);
    BookEntity partialUpdate(String isbn, BookEntity book);

    void delete(String isbn);
}
