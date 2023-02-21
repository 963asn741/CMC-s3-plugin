package com.ttech.aws.plugin.s3p.entity.operations;

import com.cloudops.mc.plugin.sdk.annotations.EntityOperation;
import com.cloudops.mc.plugin.sdk.components.quota.MetricDelta;
import com.cloudops.mc.plugin.sdk.contexts.CallerContext;
import com.cloudops.mc.plugin.sdk.operation.Operation;
import com.cloudops.mc.plugin.sdk.operation.OperationError;
import com.cloudops.mc.plugin.sdk.operation.OperationResult;
import com.ttech.aws.plugin.s3p.S3pConnector;
import com.ttech.aws.plugin.s3p.controller.S3pController;
import com.ttech.aws.plugin.s3p.entity.Object;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.List;

@EntityOperation(value="delete")
public class DeleteObject implements Operation<Object> {
    @Autowired
    private S3pController s3pController;

    private static final Logger logger = LoggerFactory.getLogger(DeleteObject.class);

    @Override
    public List<OperationError> precondition(CallerContext callerContext, String s) {
        logger.info("PRE CONDITION IN DELETE");
        return null;
    }

    @Override
    public List<OperationError> validate(CallerContext callerContext, Object object) {
        logger.info("VALIDATE IN DELETE");
        return null;
    }

    @Override
    public OperationResult execute(CallerContext callerContext, Object object) {
        try {
            logger.info("INITING EXECUTE IN DELETE OBJECT");
            S3Client s3 = s3pController.getClient(callerContext.getConnection());
            logger.info("GONNA CALL DELETE OBJECT METHOD IN CONTROLLER");
            s3pController.deleteObject(s3,callerContext.getEnvironment().getName(), object);
            return new OperationResult.Builder(callerContext).build();
        }catch (Exception e) {
            logger.error(e.getMessage());
            return new OperationResult.Builder(callerContext).withEntity(object).failed().build();
        }
    }

    @Override
    public List<MetricDelta> computeMetricDeltas(CallerContext callerContext) {
        return Operation.super.computeMetricDeltas(callerContext);
    }
}
