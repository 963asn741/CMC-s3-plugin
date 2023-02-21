package com.ttech.aws.plugin.s3p.entity.operations;

import com.cloudops.mc.plugin.sdk.annotations.ConfirmDialog;
import com.cloudops.mc.plugin.sdk.annotations.ViewBuilder;
import com.cloudops.mc.plugin.sdk.ui.form.Form;
import com.cloudops.mc.plugin.sdk.ui.form.MessageElement;
import com.cloudops.mc.plugin.sdk.viewbuilders.ConfirmationOperationViewBuilder;
import com.ttech.aws.plugin.s3p.entity.Object;

@ConfirmDialog
@ViewBuilder(name = "delete")
public class DeleteObjectView extends ConfirmationOperationViewBuilder<Object> {

    @Override
    protected MessageElement getMessage(Object object) {
        // labels added here must be updated in the label.json file
        return Form.message("s3p.service.files.operations.delete.confirm")
                .interpolate("name", object.getName())
                .build();
    }
}
