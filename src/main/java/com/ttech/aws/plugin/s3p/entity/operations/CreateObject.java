package com.ttech.aws.plugin.s3p.entity.operations;

import com.cloudops.mc.plugin.sdk.annotations.EntityOperation;
import com.cloudops.mc.plugin.sdk.contexts.CallerContext;
import com.cloudops.mc.plugin.sdk.operation.Operation;
import com.cloudops.mc.plugin.sdk.operation.OperationError;
import com.cloudops.mc.plugin.sdk.operation.OperationResult;
import com.ttech.aws.plugin.s3p.entity.Object;

import java.util.List;

@EntityOperation(value = "create", defaultForType = EntityOperation.Type.CREATE)
public class CreateObject implements Operation<Object> {
    @Override
    public List<OperationError> precondition(CallerContext callerContext, String s) {
        return null;
    }

    @Override
    public List<OperationError> validate(CallerContext callerContext, Object object) {
        return null;
    }

    @Override
    public OperationResult execute(CallerContext callerContext, Object object) {
        return null;
    }
}
