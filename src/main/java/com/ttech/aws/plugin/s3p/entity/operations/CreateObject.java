package com.ttech.aws.plugin.s3p.entity.operations;

import com.cloudops.mc.plugin.sdk.annotations.EntityOperation;
import com.cloudops.mc.plugin.sdk.contexts.CallerContext;
import com.cloudops.mc.plugin.sdk.operation.Operation;
import com.cloudops.mc.plugin.sdk.operation.OperationError;
import com.cloudops.mc.plugin.sdk.operation.OperationResult;
import com.ttech.aws.plugin.s3p.controller.S3pController;
import com.ttech.aws.plugin.s3p.entity.Object;
import com.ttech.aws.plugin.s3p.lib.utils.S3Errors;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.apache.axis2.transport.http.HTTPConstants.CONNECTION_TIMEOUT;

@EntityOperation(value = "create", defaultForType = EntityOperation.Type.CREATE)
public class CreateObject implements Operation<Object> {

    @Autowired
    private S3pController s3pController;

    @Override
    public List<OperationError> precondition(CallerContext callerContext, String id) {
        return null;
    }

    @Override
    public List<OperationError> validate(CallerContext callerContext, Object object) {
        ArrayList<OperationError> errors = new ArrayList<>();
        if(!s3pController.checkIfUrlValid(object.getUrl())){
            errors.add(Operation.error(S3Errors.URL_NOT_VALID)
                    .label("s3p.service.url-not-valid")
                    .build());
        }
        return errors;
    }

    @Override
    public OperationResult execute(CallerContext callerContext, Object object) {
        List extensionList = Arrays.asList(object.getUrl().split("[.]"));
        String extension = extensionList.get((extensionList.size())-1).toString();
        try {
            String bucketName = s3pController.toBucketName(callerContext.getEnvironment().getName());
            S3Client s3= s3pController.getClient(callerContext.getConnection());
            s3pController.createObject(s3, object, bucketName, extension);
            return new OperationResult.Builder(callerContext).build();
        } catch (Exception e) {
            e.printStackTrace();
            return new OperationResult.Builder(callerContext).withEntity(object).failed().build();
        }
    }



}
