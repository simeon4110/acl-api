package com.sonnets.sonnet.controllers;

import com.sonnets.sonnet.services.prose.BookService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class BookControllerIntegrationTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private BookService bookService;

    @Test
    public void get() {
    }

    @Test
    public void getByTitle() {
    }

    @Test
    public void getTitle() {
    }

    @Test
    public void getAll() {
    }

    @Test
    public void getAllSimple() {
    }

    @Test
    public void add() {
    }

    @Test
    public void modify() {
    }

    @Test
    public void delete() {
    }

    @Test
    public void getBookCharacters() {
    }
}