package com.izaya.databse.services;

import com.izaya.databse.domain.entities.AuthorEntity;

import java.util.List;
import java.util.Optional;

public interface AuthorService {

    public AuthorEntity save(AuthorEntity author);
    public List<AuthorEntity> findAll();
    public Optional<AuthorEntity> findOne(Long id);
    public boolean isExists(Long id);
    AuthorEntity partialUpdate(Long id, AuthorEntity authorEntity);

    void delete(Long id);
}
