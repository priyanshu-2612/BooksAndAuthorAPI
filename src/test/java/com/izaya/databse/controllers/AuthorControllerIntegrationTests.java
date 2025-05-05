package com.izaya.databse.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.izaya.databse.TestDataUtil;
import com.izaya.databse.domain.dto.AuthorDto;
import com.izaya.databse.domain.entities.AuthorEntity;
import com.izaya.databse.services.AuthorService;
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
public class AuthorControllerIntegrationTests {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private AuthorService authorService;

    @Autowired
    public AuthorControllerIntegrationTests(MockMvc mockMvc, AuthorService authorService) {
        this.mockMvc = mockMvc;
        this.objectMapper = new ObjectMapper();
        this.authorService = authorService;
    }

    @Test
    public void testThatCreateAuthorSuccessfullyReturns201Created() throws Exception {
        AuthorEntity testAuthorAuthor = TestDataUtil.createTestAuthorA();
        testAuthorAuthor.setId(null);
        String authorJson = objectMapper.writeValueAsString(testAuthorAuthor);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authorJson)
        ).andExpect(
                MockMvcResultMatchers.status().isCreated()
        );
    }

    @Test
    public void testThatCreateAuthorSuccessfullyReturnsSavedAuthor() throws Exception {
        AuthorEntity testAuthorAuthor = TestDataUtil.createTestAuthorA();
        testAuthorAuthor.setId(null);
        String authorJson = objectMapper.writeValueAsString(testAuthorAuthor);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authorJson)
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.id").isNumber()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.name").value(testAuthorAuthor.getName())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.age").value(testAuthorAuthor.getAge())
        );
    }

    @Test
    public void testThatListAuthorsReturnsHttpStatus200() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/authors")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        );
    }

    @Test
    public void testThatListAuthorsReturnsListOfAuthors() throws Exception {
        //database is cleaned after each method is executed
        AuthorEntity author = TestDataUtil.createTestAuthorA();
        authorService.save(author);
        mockMvc.perform(
                MockMvcRequestBuilders.get("/authors")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].id").isNumber()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].name").value(author.getName())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].age").value(author.getAge())
        );
    }

    @Test
    public void testThatGetAuthorReturns200WhenAuthorExists() throws Exception {
        AuthorEntity author = TestDataUtil.createTestAuthorA();
        authorService.save(author);
        mockMvc.perform(
                MockMvcRequestBuilders.get("/authors/1")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        );
    }

    @Test
    public void testThatGetAuthorReturns404WhenNoAuthorExists() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/authors/1")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );
    }

    @Test
    public void testThatGetAuthorReturnsAuthorWhenAuthorExists() throws Exception {
        AuthorEntity author = TestDataUtil.createTestAuthorA();
        authorService.save(author);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/authors/1")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.id").value(author.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.name").value(author.getName())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.age").value(author.getAge())
        );
    }

    @Test
    public void testThatUpdateFullAuthorReturns404WhenNoAuthorExists() throws Exception {
        AuthorDto author = TestDataUtil.createTestAuthorDtoA();
        String authorDtoJson = objectMapper.writeValueAsString(author);
        mockMvc.perform(
                MockMvcRequestBuilders.get("/authors/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .contentType(authorDtoJson)
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );
    }

    @Test
    public void testThatUpdateFullAuthorReturns200WhenAuthorExists() throws Exception {
        AuthorEntity author = TestDataUtil.createTestAuthorA();
        AuthorEntity savedAuthor = authorService.save(author);
        String authorDtoJson = objectMapper.writeValueAsString(author);
        mockMvc.perform(
                MockMvcRequestBuilders.get("/authors/" + savedAuthor.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authorDtoJson)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        );
    }

    @Test
    public void testThatUpdateFullAuthorUpdatesExistingAuthor() throws Exception {
        AuthorEntity author = TestDataUtil.createTestAuthorA();
        AuthorEntity savedAuthor = authorService.save(author);

        AuthorEntity author2 = TestDataUtil.createTestAuthorB();
        author2.setId(savedAuthor.getId());

        String authorDtoUpdateJson = objectMapper.writeValueAsString(author2);
        mockMvc.perform(
                MockMvcRequestBuilders.put("/authors/" + savedAuthor.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authorDtoUpdateJson)
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.id").value(savedAuthor.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.name").value(author2.getName())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.age").value(author2.getAge())
        );
    }

    @Test
    public void testThatPartialUpdateExistingAuthorReturns200WhenAuthorExists() throws Exception {
        AuthorEntity author = TestDataUtil.createTestAuthorA();
        AuthorEntity savedAuthor = authorService.save(author);

        AuthorDto authorDto = TestDataUtil.createTestAuthorDtoA();
        authorDto.setId(savedAuthor.getId());
        authorDto.setName("UPDATED");
        String authorDtoJson = objectMapper.writeValueAsString(authorDto);

        mockMvc.perform(
                MockMvcRequestBuilders.patch("/authors/" + savedAuthor.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authorDtoJson)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        );
    }

    @Test
    public void testThatPartialUpdateExistingAuthorReturnsUpdatedAuthor() throws Exception {
        AuthorEntity author = TestDataUtil.createTestAuthorA();
        AuthorEntity savedAuthor = authorService.save(author);

        AuthorEntity author2 = TestDataUtil.createTestAuthorB();
        author2.setId(savedAuthor.getId());
        author2.setName("UPDATED");
        String authorDtoUpdateJson = objectMapper.writeValueAsString(author2);

        mockMvc.perform(
                MockMvcRequestBuilders.put("/authors/" + savedAuthor.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authorDtoUpdateJson)
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.id").value(savedAuthor.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.name").value(author2.getName())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.age").value(author2.getAge())
        );
    }

    @Test
    public void testThatDeleteAuthorReturnsHttpStatus204ForNonExistingAuthor() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/authors/999")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isNoContent()
        );
    }

    @Test
    public void testThatDeleteAuthorReturnsHttpStatus204ForExistingAuthor() throws Exception {
        AuthorEntity authorEntity = TestDataUtil.createTestAuthorA();
        AuthorEntity savedAuthor = authorService.save(authorEntity);

        mockMvc.perform(
                MockMvcRequestBuilders.delete("/authors/" + savedAuthor.getId())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isNoContent()
        );
    }
}
