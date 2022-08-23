package com.ttech.aws.plugin.s3p;

import com.cloudops.mc.plugin.sdk.annotations.ViewBuilder;
import com.cloudops.mc.plugin.sdk.contexts.ConnectionContext;
import com.cloudops.mc.plugin.sdk.viewbuilders.WorkspaceViewBuilder;
import com.cloudops.mc.plugin.sdk.viewbuilders.response.WorkspaceView;

@ViewBuilder(isDefault = true)
public class S3pWorkspace implements WorkspaceViewBuilder {
   @Override
   public WorkspaceView buildView(ConnectionContext connectionContext) {
      return WorkspaceView.create()
              .tab("s3p.service.instances.name", "instances")
              .build();
   }
}
