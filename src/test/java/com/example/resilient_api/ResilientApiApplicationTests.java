package com.example.resilient_api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(classes = ResilientApiApplication.class)
class ResilientApiApplicationTests {

	@MockBean
	private UserPersistencePort userPersistencePort;

	@Autowired
	private UserUseCase userUseCase;

	@Test
	void contextLoads() {
	}

}
