package com.izaya.databse.controllers;

import com.izaya.databse.domain.dto.BookDto;
import com.izaya.databse.domain.entities.BookEntity;
import com.izaya.databse.mappers.Mapper;
import com.izaya.databse.services.BookService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class BookController {

    private Mapper<BookEntity, BookDto> bookMapper;
    private BookService bookService;

    public BookController(Mapper<BookEntity, BookDto> bookMapper, BookService bookService) {
        this.bookMapper = bookMapper;
        this.bookService = bookService;
    }

    @DeleteMapping("/books/{isbn}")
    public ResponseEntity deleteBook(@PathVariable("isbn") String isbn){
        bookService.delete(isbn);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/books/{isbn}")
    public ResponseEntity<BookDto> partialUpdate(@PathVariable("isbn") String isbn, @RequestBody BookDto bookDto){
        if(!bookService.isExists(isbn)){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        BookEntity bookEntity = bookMapper.mapFrom(bookDto);
        BookEntity updatedBook = bookService.partialUpdate(isbn, bookEntity);
        return new ResponseEntity<>(bookMapper.mapTo(updatedBook), HttpStatus.OK);
    }

    @PutMapping("/books/{isbn}")
    public ResponseEntity<BookDto> createUpdateBook(@PathVariable("isbn") String isbn, @RequestBody BookDto bookDto){
        BookEntity bookEntity = bookMapper.mapFrom(bookDto);
        boolean bookExists = bookService.isExists(isbn);
        BookEntity savedBook = bookService.createUpdateBook(isbn,bookEntity);
        BookDto savedBookDto = bookMapper.mapTo(savedBook);

        if(bookExists){
            return new ResponseEntity<>(savedBookDto, HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(savedBookDto, HttpStatus.CREATED);
        }
    }

//    @GetMapping("/books")
//    public List<BookDto> listBooks(){
//        List<BookEntity> books = bookService.findAll();
//        return books.stream().map(bookMapper::mapTo).collect(Collectors.toList());
//    }

    @GetMapping("/books")
    public Page<BookDto> listBooks(Pageable pageable){
        Page<BookEntity> books = bookService.findAll(pageable);
        return books.map(bookMapper::mapTo);
    }

    @GetMapping("/books/{isbn}")
    public ResponseEntity<BookDto> getBook(@PathVariable("isbn") String isbn){
        Optional<BookEntity> foundBook = bookService.findOne(isbn);
        return foundBook.map(bookEntity -> {
            BookDto bookDto = bookMapper.mapTo(bookEntity);
            return new ResponseEntity<>(bookDto, HttpStatus.OK);
        }).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
