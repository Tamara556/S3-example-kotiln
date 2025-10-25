package com.codersee.s3

import org.springframework.boot.fromApplication
import org.springframework.boot.with


fun main(args: Array<String>) {
	fromApplication<S3Application>().with(TestcontainersConfiguration::class).run(*args)
}
