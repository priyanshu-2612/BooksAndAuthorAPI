package com.izaya.databse;

import lombok.extern.java.Log;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Log
public class BooksAndAuthorAPI {
	public static void main(String[] args) {
		SpringApplication.run(BooksAndAuthorAPI.class, args);
	}
}
