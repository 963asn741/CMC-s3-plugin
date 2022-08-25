package com.ttech.aws.plugin.s3p.controller;

import com.cloudops.mc.plugin.sdk.models.Connection;
import com.ttech.aws.plugin.s3p.Credentials;
import com.ttech.aws.plugin.s3p.S3pManagementController;
import com.ttech.aws.plugin.s3p.entity.Object;
import io.ebean.enhance.common.SysoutMessageOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.waiters.S3Waiter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Component
public class S3pController {

    private static final Logger logger = LoggerFactory.getLogger(S3pController.class);
    private Region region = Region.AP_SOUTH_1;

    public void testConnectionToApi(String accessId, String secretKey) {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessId, secretKey);
        S3Client s3 = S3Client.builder()
                .region(region)
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();
        ListBucketsRequest listBucketsRequest = ListBucketsRequest.builder().build();
        s3.listBuckets(listBucketsRequest);
        s3.close();
    }

    public boolean checkIfBucketExists(String accessId, String secretKey, String bucketName) {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessId, secretKey);
        S3Client s3 = S3Client.builder()
                .region(region)
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();
        HeadBucketRequest headBucketRequest = HeadBucketRequest.builder()
                .bucket(bucketName)
                .build();
        try {
            s3.headBucket(headBucketRequest);
            s3.close();
            return true;
        } catch (NoSuchBucketException e) {
            s3.close();
            return false;
        }
    }

    public void createBucket(String accessId, String secretKey, String bucketName) {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessId, secretKey);
        S3Client s3 = S3Client.builder()
                .region(region)
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();
        S3Waiter s3Waiter = s3.waiter();
        CreateBucketRequest bucketRequest = CreateBucketRequest.builder()
                .bucket(bucketName)
                .build();
        s3.createBucket(bucketRequest);
        HeadBucketRequest bucketRequestWait = HeadBucketRequest.builder()
                .bucket(bucketName)
                .build();
        WaiterResponse<HeadBucketResponse> waiterResponse = s3Waiter.waitUntilBucketExists(bucketRequestWait);
        waiterResponse.matched().response().ifPresent(System.out::println);
        logger.info("created bucket with name " + bucketName);
        s3.close();
    }

    public void deleteBucket(String accessId, String secretKey, String bucketName) {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessId, secretKey);
        S3Client s3 = S3Client.builder()
                .region(region)
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();
        // To delete a bucket, all the objects in the bucket must be deleted first.
        ListObjectsV2Request listObjectsV2Request = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .build();
        ListObjectsV2Response listObjectsV2Response;

        do {
            listObjectsV2Response = s3.listObjectsV2(listObjectsV2Request);
            for (S3Object s3Object : listObjectsV2Response.contents()) {
                DeleteObjectRequest request = DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(s3Object.key())
                        .build();
                s3.deleteObject(request);
            }
        } while (listObjectsV2Response.isTruncated());

        DeleteBucketRequest deleteBucketRequest = DeleteBucketRequest.builder()
                .bucket(bucketName)
                .build();

        s3.deleteBucket(deleteBucketRequest);
        s3.close();
    }

    public List<S3Object> filesInBucket(String accessId, String secretKey, String bucketName){
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessId, secretKey);
        S3Client s3 = S3Client.builder()
                .region(region)
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();
        try {
            ListObjectsRequest listObjects = ListObjectsRequest
                    .builder()
                    .bucket(bucketName)
                    .build();
            ListObjectsResponse res = s3.listObjects(listObjects);
            List<S3Object> objects = res.contents();
            return objects;
        } catch (S3Exception e) {
            logger.error(e.awsErrorDetails().errorMessage());
        }
        return Collections.emptyList();
    }

    public S3Client getClient(Connection connection){
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(connection.getParameter(Credentials.ACCESS_ID), connection.getParameter(Credentials.SECRET_KEY));
        return S3Client.builder()
                .region(region)
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();
    }

    public S3Object getObject(S3Client s3, String bucketName, String objectETag){
            ListObjectsRequest listObjects = ListObjectsRequest
                    .builder()
                    .bucket(bucketName)
                    .build();
            ListObjectsResponse res = s3.listObjects(listObjects);
            List<S3Object> objects = res.contents();
            for(S3Object o : objects) if(o.eTag().equals(objectETag)) return o;
            return null;
    }

    public void deleteObject(S3Client s3, String bucketName, Object object){
        ArrayList<ObjectIdentifier> toDelete = new ArrayList<>();
        toDelete.add(ObjectIdentifier.builder()
                .key(object.getName())
                .build());
        DeleteObjectsRequest dor = DeleteObjectsRequest.builder()
                .bucket(bucketName)
                .delete(Delete.builder()
                        .objects(toDelete).build())
                .build();
        s3.deleteObjects(dor);
        s3.close();
    }
}