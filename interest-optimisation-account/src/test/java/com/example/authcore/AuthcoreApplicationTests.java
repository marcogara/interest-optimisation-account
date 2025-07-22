package com.example.authcore;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
class AuthcoreApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	public void testWeakPassword() {
		String password = "pass123";
		String pattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$";

		assertFalse(password.matches(pattern)); // ✅ should fail
	}

}
