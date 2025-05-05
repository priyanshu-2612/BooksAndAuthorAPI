package com.izaya.databse.Repositories;

import com.izaya.databse.TestDataUtil;
import com.izaya.databse.domain.entities.AuthorEntity;
import com.izaya.databse.domain.entities.BookEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookEntityRepositoryIntegrationTests {

    private BookRepository underTest;
    private AuthorRepository authorRepository;

    @Autowired
    public BookEntityRepositoryIntegrationTests(BookRepository underTest, AuthorRepository authorRepository){
        this.underTest = underTest;
        this.authorRepository = authorRepository;
    }

    @Test
    public void testThatBookCanBeCreatedAndRecalled(){
        AuthorEntity authorEntity = TestDataUtil.createTestAuthorA();
        BookEntity bookEntity = TestDataUtil.createTestBookA(authorEntity);
        underTest.save(bookEntity);
        Optional<BookEntity> result = underTest.findById(bookEntity.getIsbn());
        assertThat(result).isPresent();
        authorEntity.setId(result.get().getAuthorEntity().getId());
        assertThat(result.get()).isEqualTo(bookEntity);
    }

    @Test
    public void testThatMultipleBooksCanBeCreatedAndRecalled(){
        AuthorEntity authorEntityA = authorRepository.save(TestDataUtil.createTestAuthorA()); // first saving the author

        BookEntity bookEntityA = TestDataUtil.createTestBookA(authorEntityA);

        underTest.save(bookEntityA);

        BookEntity bookEntityB = TestDataUtil.createTestBookB(authorEntityA);

        underTest.save(bookEntityB);

        BookEntity bookEntityC = TestDataUtil.createTestBookC(authorEntityA);

        underTest.save(bookEntityC);

        Iterable<BookEntity> result = underTest.findAll();

        assertThat(result).hasSize(3).containsExactly(bookEntityA, bookEntityB, bookEntityC);

    }

    @Test
    public void testThatBookCanUpdated(){
        AuthorEntity authorEntity = authorRepository.save(TestDataUtil.createTestAuthorA());

        BookEntity bookEntity = TestDataUtil.createTestBookA(authorEntity);
        underTest.save(bookEntity);

        bookEntity.setTitle("UPDATED");
        underTest.save(bookEntity);

        Optional<BookEntity> result = underTest.findById(bookEntity.getIsbn());

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(bookEntity);
    }

    @Test
    public void testThatBookCanBeDeleted(){
        AuthorEntity authorEntity = authorRepository.save(TestDataUtil.createTestAuthorA());

        BookEntity bookEntity = TestDataUtil.createTestBookA(authorEntity);
        underTest.save(bookEntity);

        underTest.deleteById(bookEntity.getIsbn());

        Optional<BookEntity> result = underTest.findById(bookEntity.getIsbn());
        assertThat(result).isEmpty();
    }
}
