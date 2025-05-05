package com.izaya.databse.Repositories;

import com.izaya.databse.domain.entities.AuthorEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends CrudRepository<AuthorEntity, Long> {
    Iterable<AuthorEntity> ageLessThan(int i);

//    @Query("SELECT a from Author a where a.age > ?1")
//    Iterable<AuthorEntity> getAuthorsWithAgeGreaterThan(int age);
}
