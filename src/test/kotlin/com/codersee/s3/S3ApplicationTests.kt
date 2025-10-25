package com.codersee.s3

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@Import(TestcontainersConfiguration::class)
@SpringBootTest
class S3ApplicationTests {

	@Test
	fun contextLoads() {
	}

}
