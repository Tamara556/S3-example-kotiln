package com.codersee.s3.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.CreateBucketRequest
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.ListObjectsRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import kotlin.text.Charsets.UTF_8

typealias PutObjectRequestBody = software.amazon.awssdk.core.sync.RequestBody

@RestController
@RequestMapping("/buckets")
class BucketController(
    private val s3Client: S3Client
) {

    @GetMapping
    fun listBuckets(): List<String> {
        val buckets = s3Client.listBuckets()
        return buckets.buckets()
            .map { it.name() }
    }

    @PostMapping
    fun createBucket(@RequestBody bucketRequest: BucketRequest) {
        val request = CreateBucketRequest.builder()
            .bucket(bucketRequest.bucketName)
            .build()

        s3Client.createBucket(request)

    }

    @PostMapping("/{bucketName}/objects")
    fun createObject(
        @PathVariable bucketName: String,
        @RequestBody objectRequest: ObjectRequest
    ) {
        val createObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(objectRequest.objectName)
            .build()

        val fileContent = PutObjectRequestBody.fromString(objectRequest.content)

        s3Client.putObject(createObjectRequest, fileContent)
    }

    @GetMapping("/{bucketName}/objects")
    fun listObjects(@PathVariable bucketName: String): List<String> {
        val listObjectsRequest = ListObjectsRequest.builder()
            .bucket(bucketName)
            .build()

        val response = try {
            s3Client.listObjects(listObjectsRequest)
        } catch (ex: Exception) {
            println("Error listing objects in bucket $bucketName: ${ex.message}")
            return emptyList()
        }

        val keys = response.contents().mapNotNull { obj ->
            val key = obj.key()
            if (key == null) println("Found null key in bucket $bucketName. Skipping...")
            key
        }

        println("Keys found in bucket '$bucketName': $keys")
        return keys
    }

    @GetMapping("/{bucketName}/objects/{objectName:.+}")
    fun getObject(
        @PathVariable bucketName: String,
        @PathVariable objectName: String
    ): String {
        val getObjectRequest = GetObjectRequest.builder()
            .bucket(bucketName)
            .key(objectName)
            .build()

        s3Client.getObject(getObjectRequest).use {
            return it.readAllBytes().toString(Charsets.UTF_8)
        }
    }

    data class BucketRequest(val bucketName: String)

    data class ObjectRequest(
        val objectName: String,
        val content: String
    )

}