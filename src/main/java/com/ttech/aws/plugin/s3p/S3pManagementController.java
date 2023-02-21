package com.ttech.aws.plugin.s3p;

import java.security.Provider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.cloudops.mc.plugin.sdk.components.environmentmetrics.EnvironmentMetricsException;
import com.cloudops.mc.plugin.sdk.exception.ServiceRuntimeException;
import com.cloudops.mc.plugin.sdk.models.*;
import com.ttech.aws.plugin.s3p.controller.S3pController;
import com.ttech.aws.plugin.s3p.lib.utils.S3Errors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cloudops.mc.plugin.sdk.annotations.PluginComponent;
import com.cloudops.mc.plugin.sdk.components.management.EnvironmentController;
import com.cloudops.mc.plugin.sdk.components.management.EnvironmentServiceDetail;
import com.cloudops.mc.plugin.sdk.components.management.OrganizationController;
import com.cloudops.mc.plugin.sdk.components.management.UserController;
import com.cloudops.mc.plugin.sdk.contexts.EnvironmentContext;
import com.cloudops.mc.plugin.sdk.contexts.OrganizationContext;
import com.cloudops.mc.plugin.sdk.exception.ServiceException;
import com.cloudops.mc.plugin.sdk.contexts.UserContext;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The S3pManagementController handles all org, env and user management on the service
 */
@PluginComponent
public class S3pManagementController implements OrganizationController, UserController, EnvironmentController {
    private static final Logger logger = LoggerFactory.getLogger(S3pManagementController.class);

    @Autowired
    private S3pController s3pController;

    @Override
    public List<Credential> createOrganization(OrganizationContext organizationContext, Organization organization) {
        return new ArrayList<>();
    }

    @Override
    public void deleteOrganization(OrganizationContext organizationContext) {

    }

    @Override
    public List<Credential> createUser(UserContext userContext, User user) {
        return new ArrayList<>();
    }

    @Override
    public void deleteUser(UserContext userContext) {

    }

    @Override
    public List<Credential> createEnvironment(EnvironmentContext environmentContext, Environment environment) throws ServiceException {
        try {
            Connection connection = environmentContext.getConnection();
            String bucketName = s3pController.toBucketName(environment.getName());
            if(s3pController.checkIfBucketExists(connection.getParameter(Credentials.ACCESS_ID), connection.getParameter(Credentials.SECRET_KEY), bucketName)){
                throw new ServiceException(S3Errors.ENVIRONMENT_NAME_NOT_UNIQUE);
            }
            else s3pController.createBucket(connection.getParameter(Credentials.ACCESS_ID), connection.getParameter(Credentials.SECRET_KEY), bucketName);
            return new ArrayList<>();
        } catch (Exception e) {
            logger.info("------------SECOND LOGGER------------");
            logger.error(e.getMessage());
            throw e;
        }
    }

    @Override
    public void updateEnvironment(EnvironmentContext environmentContext, Environment updatedEnvironment) {

    }

    @Override
    public void deleteEnvironment(EnvironmentContext environmentContext) {
        try {
            Connection connection = environmentContext.getConnection();
            String bucketName = s3pController.toBucketName(environmentContext.getEnvironment().getName());
            s3pController.deleteBucket(connection.getParameter(Credentials.ACCESS_ID), connection.getParameter(Credentials.SECRET_KEY), bucketName);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public List<Credential> addUserToEnvironment(EnvironmentContext environmentContext, User user) throws ServiceException {
        return new ArrayList<>();
    }

    @Override
    public void removeUserFromEnvironment(EnvironmentContext environmentContext, User user) {

    }

    @Override
    public List<EnvironmentServiceDetail> getEnvironmentServiceDetails(EnvironmentContext environmentContext) {
        return new ArrayList<>();
    }
}
