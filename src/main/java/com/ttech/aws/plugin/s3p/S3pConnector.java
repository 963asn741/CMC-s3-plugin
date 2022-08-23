package com.ttech.aws.plugin.s3p;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.ttech.aws.plugin.s3p.controller.S3pController;
import com.ttech.aws.plugin.s3p.lib.utils.S3Errors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cloudops.mc.plugin.sdk.annotations.Plugin;
import com.cloudops.mc.plugin.sdk.connector.ConnectionTest;
import com.cloudops.mc.plugin.sdk.connector.Connector;
import com.cloudops.mc.plugin.sdk.contexts.ConnectionContext;
import com.cloudops.mc.plugin.sdk.models.Connection;
import com.cloudops.mc.plugin.sdk.models.Credential;
import com.cloudops.mc.plugin.sdk.plugin.Extension;
import com.cloudops.mc.plugin.sdk.ui.form.Form;
import com.cloudops.mc.plugin.sdk.ui.form.FormElement;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * S3pConnector validates access to the s3p service
 */
@Plugin("s3p")
public class S3pConnector implements Connector {

   @Autowired
   private S3pController s3pController;
   private static final Logger logger = LoggerFactory.getLogger(S3pConnector.class);

   @Override
   public List<Credential> createConnection(ConnectionContext context, Connection connection) {
      return getConnectionCredentials(connection);
   }

   @Override
   public List<Credential> updateConnection(ConnectionContext context, Connection connection) {
      return getConnectionCredentials(connection);
   }

   private List<Credential> getConnectionCredentials(Connection connection) {
      return Arrays.asList(
              Credential.from(Credentials.ACCESS_ID, connection.getParameter(Credentials.ACCESS_ID)),
              Credential.from(Credentials.SECRET_KEY, connection.getParameter(Credentials.SECRET_KEY))
              );
   }

   @Override
   public ConnectionTest testConnection(Connection connection) {
      try {

         if (connection.getParameter(Credentials.ACCESS_ID) == null) {
            return new ConnectionTest.Builder()
                    .withErrorCode(S3Errors.MISSING_ACCESS_ID_PARAMETER)
                    .addToContext("parameter", Credentials.ACCESS_ID)
                    .build();
         } else if (connection.getParameter(Credentials.ACCESS_ID) == null) {
            return new ConnectionTest.Builder()
                    .withErrorCode(S3Errors.MISSING_SECRET_KEY_PARAMETER)
                    .addToContext("parameter", Credentials.SECRET_KEY)
                    .build();

         } else {
            s3pController.testConnectionToApi(connection.getParameter(Credentials.ACCESS_ID), connection.getParameter(Credentials.SECRET_KEY));
            return new ConnectionTest.Builder()
                    .addToContext("labelKey", "s3p.connection.success")
                    .build();
         }
      }
      catch(Exception e){
         System.out.println(e.getMessage());
         logger.error(String.valueOf(S3Errors.CONNECTION_TEST_FAILED));
         return new ConnectionTest.Builder()
                 .withErrorCode(S3Errors.CONNECTION_TEST_FAILED)
                 .addToContext("labelKey", "s3p.connection.success")
                 .build();
      }
   }

   @Override
   public List<FormElement> getParameterFormElements() {
      List<FormElement> els = new ArrayList<>();
      els.add(Form.text(Credentials.ACCESS_ID)
              .label("s3p.service_configuration.parameters.accessid.label")
              .description("s3p.service_configuration.parameters.accessid.description")
              .required()
              .build());
      els.add(Form.text(Credentials.SECRET_KEY)
              .label("s3p.service_configuration.parameters.secretkey.label")
              .description("s3p.service_configuration.parameters.secretkey.description")
              .required()
              .build());
      return els;
   }

   @Override
   public List<String> getParameters() {
      List<String> parameters = new ArrayList<>();
      parameters.add(Credentials.ACCESS_ID);
      parameters.add(Credentials.SECRET_KEY);
      return parameters;
   }

   @Override
   public List<Extension> getExtensions() {
      return Collections.emptyList();
   }

  @Override
   public String getDependentPlugin() {
      return null;
   }
}