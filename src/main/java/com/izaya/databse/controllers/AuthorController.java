package com.izaya.databse.controllers;

import com.izaya.databse.domain.dto.AuthorDto;
import com.izaya.databse.domain.entities.AuthorEntity;
import com.izaya.databse.mappers.Mapper;
import com.izaya.databse.services.AuthorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class AuthorController {

    private AuthorService authorService;
    private Mapper<AuthorEntity, AuthorDto> authorMapper;

    public AuthorController(AuthorService authorService, Mapper<AuthorEntity, AuthorDto> authorMapper){
        this.authorService = authorService;
        this.authorMapper = authorMapper;
    }

    @DeleteMapping("/authors/{id}")
    public ResponseEntity deleteAuthor(@PathVariable("id") Long id){
        authorService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/authors")
    public ResponseEntity<AuthorDto> createAuthor(@RequestBody AuthorDto author){
        AuthorEntity authorEntity = authorMapper.mapFrom(author);
        AuthorEntity savedAuthor = authorService.save(authorEntity);
//        return new ResponseEntity<>(authorMapper.mapTo(savedAuthor), HttpStatus.CREATED);
        return ResponseEntity.status(HttpStatus.CREATED).body(authorMapper.mapTo(savedAuthor));
    }

    @GetMapping("/authors")
    public List<AuthorDto> listAuthors(){
        List<AuthorEntity> authors = authorService.findAll();
        return authors.stream().map(authorMapper::mapTo).collect(Collectors.toList());
    }

    @GetMapping("/authors/{id}")
    public ResponseEntity<AuthorDto> getAuthor(@PathVariable("id") Long id){
        Optional<AuthorEntity> foundAuthor = authorService.findOne(id);
        return foundAuthor.map(authorEntity -> {
                    AuthorDto authorDto = authorMapper.mapTo(authorEntity);
                    return new ResponseEntity<AuthorDto>(authorDto, HttpStatus.OK);
        }).orElse(new ResponseEntity<AuthorDto>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/authors/{id}")
    public ResponseEntity<AuthorDto> fullUpdateAuthor(@PathVariable("id") Long id, @RequestBody AuthorDto authorDto){
        if(!authorService.isExists(id)){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        authorDto.setId(id);
        AuthorEntity authorEntity = authorMapper.mapFrom(authorDto);
        AuthorEntity savedAuthor = authorService.save(authorEntity);
        return new ResponseEntity<AuthorDto>(authorMapper.mapTo(savedAuthor), HttpStatus.CREATED);
    }

    @PatchMapping("/authors/{id}")
    public ResponseEntity<AuthorDto> partialUpdate(@PathVariable("id") Long id, @RequestBody AuthorDto authorDto){
        if(!authorService.isExists(id)){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        AuthorEntity authorEntity = authorMapper.mapFrom(authorDto);
        AuthorEntity updatedAuthor = authorService.partialUpdate(id, authorEntity);
        return  new ResponseEntity<>(authorMapper.mapTo(updatedAuthor) , HttpStatus.OK);
    }
}
