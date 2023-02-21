package com.ttech.aws.plugin.s3p.controller;

import com.cloudops.mc.plugin.sdk.models.Connection;
import com.google.gson.JsonSyntaxException;
import com.ttech.aws.plugin.s3p.Credentials;
import com.ttech.aws.plugin.s3p.entity.Object;
import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.waiters.S3Waiter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

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

    public void deleteObject(S3Client s3, String bucketName, Object object)throws JsonSyntaxException {
        try {
            System.out.println(object.getName());
            System.out.println(bucketName);
            if(object.getName().equals("Antelope-Canyon-Wallpapers.jpg")) System.out.println("NAMES MATCH CONFIRMED");
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(object.getName())
                    .build();
            s3.deleteObject(deleteObjectRequest);
            s3.close();
        }catch (JsonSyntaxException e){
            e.printStackTrace();
        }
    }

    public boolean checkIfUrlValid(String url){
        System.out.println(url);
        UrlValidator urlValidator = new UrlValidator();
        if(!urlValidator.isValid(url)) return false;
        List extensionList = Arrays.asList(url.split("[.]"));
        System.out.println(extensionList);
        String extension = extensionList.get((extensionList.size())-1).toString();
        System.out.println(extension);
        System.out.println(extension.equals("jpg"));
        if(!extension.equals("jpg")) return false;
        return true;
    }

    public String toBucketName(String envName){
        return "envnameprefix-"+envName+"-ennamesuffix";
    }

    public String toEnvironmentName(String bucketName){
        return Arrays.asList(bucketName.split("-")).get(1);
    }

    public void createObject(S3Client s3, Object object, String bucketName, String extension) {
        try {
            URL url = new URL(object.getUrl());
            InputStream is = getImageInputStream(url);
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(object.getName() + "." + extension)
                    .acl("public-read")
                    .build();
            s3.putObject(putObjectRequest, RequestBody.fromInputStream(is, getContentLength(object.getUrl())));
            is.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static Long getContentLength(String urlStr) {
        Long contentLength = null;
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.62 Safari/537.36");
            conn.setRequestMethod("HEAD");
            contentLength = conn.getContentLengthLong();
        } catch (Exception e) {
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return contentLength;
    }

    private static InputStream getImageInputStream(URL url) throws IOException {
        // This user agent is for if the server wants real humans to visit
        String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36";
        // This socket type will allow to set user_agent
        URLConnection con = url.openConnection();
        // Setting the user agent
        con.setRequestProperty("User-Agent", USER_AGENT);
        //Getting content Length
        int contentLength = con.getContentLength();
        // Requesting input data from server
        return con.getInputStream();
    }
}