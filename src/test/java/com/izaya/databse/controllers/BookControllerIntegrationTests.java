package com.izaya.databse.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.izaya.databse.TestDataUtil;
import com.izaya.databse.domain.dto.BookDto;
import com.izaya.databse.domain.entities.BookEntity;
import com.izaya.databse.services.BookService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class BookControllerIntegrationTests {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private BookService bookService;

    @Autowired
    public BookControllerIntegrationTests(MockMvc mockMvc, BookService bookService) {
        this.mockMvc = mockMvc;
        this.objectMapper = new ObjectMapper();
        this.bookService = bookService;
    }

    @Test
    public void testThatCreateBookReturnsHttpStatus201Created() throws Exception{
        BookDto bookDto = TestDataUtil.createTestBookDtoA(null);
        String createBookJson = objectMapper.writeValueAsString(bookDto);

        mockMvc.perform(
                MockMvcRequestBuilders.put("/books/" + bookDto.getIsbn())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBookJson)
        ).andExpect(
                MockMvcResultMatchers.status().isCreated()
        );
    }

    @Test
    public void testThatCreateBookReturnsCreatedBook() throws Exception{
        BookDto bookDto = TestDataUtil.createTestBookDtoA(null);
        String createBookJson = objectMapper.writeValueAsString(bookDto);

        mockMvc.perform(
                MockMvcRequestBuilders.put("/books/" + bookDto.getIsbn())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBookJson)
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.isbn").value(bookDto.getIsbn())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.title").value(bookDto.getTitle())
        );
    }

    @Test
        public void testThatListBookReturnsHttpStatus200Ok() throws Exception{
        mockMvc.perform(
                MockMvcRequestBuilders.get("/books")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        );
    }

    @Test
    public void testThatListBookReturnsListOfBooks() throws Exception{
        BookEntity book = TestDataUtil.createTestBookA(null);
        bookService.createUpdateBook(book.getIsbn(), book);
        mockMvc.perform(
                MockMvcRequestBuilders.get("/books")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].isbn").value(book.getIsbn())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].title").value(book.getTitle())
        );
    }

    @Test
    public void testThatGetBookReturnsHttpStatus200WhenBookExists() throws Exception{
        BookEntity book = TestDataUtil.createTestBookA(null);
        bookService.createUpdateBook(book.getIsbn(), book);
        //do book = bookService.createBook(book.getIsbn(), book); for learning purposes
        mockMvc.perform(
                MockMvcRequestBuilders.get("/books/"+book.getIsbn())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        );
    }

    @Test
    public void testThatGetBookReturnsHttpStatus404WhenBookDoesNotExists() throws Exception{
        BookEntity book = TestDataUtil.createTestBookA(null);
        //do book = bookService.createBook(book.getIsbn(), book); for learning purposes
        mockMvc.perform(
                MockMvcRequestBuilders.get("/books/"+book.getIsbn())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );
    }

    @Test
    public void testThatUpdateBookReturnsHttpStatus200Ok() throws Exception{
        BookEntity testBookA = TestDataUtil.createTestBookA(null);
        BookEntity savedBookA = bookService.createUpdateBook(testBookA.getIsbn(), testBookA);

        BookDto bookDto = TestDataUtil.createTestBookDtoA(null);
        bookDto.setIsbn(savedBookA.getIsbn());//for documentation
        String createBookJson = objectMapper.writeValueAsString(bookDto);

        mockMvc.perform(
                MockMvcRequestBuilders.put("/books/" + bookDto.getIsbn())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBookJson)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        );
    }

    @Test
    public void testThatUpdateBookReturnsUpdatedBook() throws Exception{
        BookEntity testBookA = TestDataUtil.createTestBookA(null);
        BookEntity savedBookA = bookService.createUpdateBook(testBookA.getIsbn(), testBookA);

        BookDto bookDto = TestDataUtil.createTestBookDtoA(null);
        bookDto.setIsbn(savedBookA.getIsbn());
        bookDto.setTitle("UPDATED");
        String createBookJson = objectMapper.writeValueAsString(bookDto);

        mockMvc.perform(
                MockMvcRequestBuilders.put("/books/" + bookDto.getIsbn())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBookJson)
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.isbn").value(bookDto.getIsbn())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.title").value("UPDATED")
        );
    }

    @Test
    public void testThatParialUpdateBookReturnsHttpStatus200Ok() throws Exception{
        BookEntity testBookEntityA = TestDataUtil.createTestBookA(null);
        BookEntity savedBookA = bookService.createUpdateBook(testBookEntityA.getIsbn(), testBookEntityA);

        BookDto testBookA = TestDataUtil.createTestBookDtoA(null);
        testBookA.setIsbn(savedBookA.getIsbn());
        testBookA.setTitle("UPDATED");
        String testBookAjson = objectMapper.writeValueAsString(testBookA);

        mockMvc.perform(
                MockMvcRequestBuilders.patch("/books/"+testBookA.getIsbn())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(testBookAjson)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        );
    }

    @Test
    public void testThatPartialUpdateBookReturnsUpdatedBook() throws Exception{
        BookEntity testBookA = TestDataUtil.createTestBookA(null);
        BookEntity savedBookA = bookService.createUpdateBook(testBookA.getIsbn(), testBookA);

        BookDto bookDto = TestDataUtil.createTestBookDtoA(null);
        bookDto.setIsbn(savedBookA.getIsbn());
        bookDto.setTitle("UPDATED");
        String createBookJson = objectMapper.writeValueAsString(bookDto);

        mockMvc.perform(
                MockMvcRequestBuilders.patch("/books/" + bookDto.getIsbn())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBookJson)
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.isbn").value(bookDto.getIsbn())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.title").value("UPDATED")
        );
    }

    @Test
    public void testThatDeleteNonExistingBookReturnsStatus204NoContent() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/books/999-886-000")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isNoContent()
        );
    }

    @Test
    public void testThatDeleteExistingBookReturnsStatus204NoContent() throws Exception {
        BookEntity bookEntity = TestDataUtil.createTestBookA(null);
        BookEntity savedBook = bookService.createUpdateBook(bookEntity.getIsbn(), bookEntity);

        mockMvc.perform(
                MockMvcRequestBuilders.delete("/books/" + savedBook.getIsbn())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isNoContent()
        );
    }
}
