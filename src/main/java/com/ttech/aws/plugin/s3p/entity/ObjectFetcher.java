package com.ttech.aws.plugin.s3p.entity;

import com.cloudops.mc.plugin.sdk.annotations.Fetcher;
import com.cloudops.mc.plugin.sdk.contexts.CallerContext;
import com.cloudops.mc.plugin.sdk.fetcher.AbstractFetcher;
import com.cloudops.mc.plugin.sdk.fetcher.FetchOptions;
import com.cloudops.mc.plugin.sdk.fetcher.FetcherError;
import com.cloudops.mc.plugin.sdk.models.Connection;
import com.cloudops.mc.plugin.sdk.models.Environment;
import com.ttech.aws.plugin.s3p.Credentials;
import com.ttech.aws.plugin.s3p.controller.S3pController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Fetcher
@Component
public class ObjectFetcher extends AbstractFetcher<Object> {
    @Autowired
    private S3pController s3pController;

    @Override
    protected List<Object> fetchEntities(CallerContext callerContext, FetchOptions fetchOptions) {
        System.out.println("----------------------------FETCH ENTITIES IN OBJECT FETCHER");
        List<Object> ObjectList= new ArrayList<>();
        Connection connection = callerContext.getConnection();
        Environment environment = callerContext.getEnvironment();
        S3Client s3 = s3pController.getClient(connection);
        List<S3Object> listOfFS3Objects = s3pController.filesInBucket(connection.getParameter(Credentials.ACCESS_ID), connection.getParameter(Credentials.SECRET_KEY), environment.getName());
        for(S3Object x : listOfFS3Objects){
            System.out.println(x.key());
            GetUrlRequest request = GetUrlRequest.builder()
                    .bucket(environment.getName())
                    .key(x.key())
                    .build();
            URL url = s3.utilities().getUrl(request);
            System.out.println(x.eTag()+" "+ x.key()+" "+ url);
            ObjectList.add(new Object(x.eTag(), x.key(), url));
        }
        return ObjectList;
    }

    @Override
    protected Object fetchEntity(CallerContext callerContext, String id, FetchOptions fetchOptions) {
        System.out.println("----------------------------FETCH ENTITY IN OBJECT FETCHER");
        System.out.println("--------------           -----------------         ---------- key : "+id);
        Connection connection = callerContext.getConnection();
        Environment environment = callerContext.getEnvironment();
        S3Client s3 = s3pController.getClient(connection);
        S3Object s3object = s3pController.getObject(s3, environment.getName(), id);
        if (s3object.equals(null)) return null;
        else {
            GetUrlRequest request = GetUrlRequest.builder()
                    .bucket(environment.getName())
                    .key(s3object.key())
                    .build();
            URL url = s3.utilities().getUrl(request);
            return new Object(s3object.eTag(), s3object.key(), url);
        }
    }

    @Override
    protected List<FetcherError> validateListFetchOptions(CallerContext callerContext, FetchOptions fetchOptions) {
        System.out.println("----------------------------ERROR THING 1 IN OBJECT FETCHER");
        return Collections.emptyList();
    }

    @Override
    protected List<FetcherError> validateDetailFetchOptions(CallerContext callerContext, FetchOptions fetchOptions) {
        System.out.println("----------------------------ERROR THING 2 IN OBJECT FETCHER");
        return Collections.emptyList();
    }
}
