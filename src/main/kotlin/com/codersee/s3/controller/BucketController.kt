package com.codersee.s3.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.CreateBucketRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest

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

    data class BucketRequest(val bucketName: String)

    data class ObjectRequest(
        val objectName: String,
        val content: String
    )

}