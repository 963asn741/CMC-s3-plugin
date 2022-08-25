package com.ttech.aws.plugin.s3p.entity.operations;

import com.cloudops.mc.plugin.sdk.annotations.ViewBuilder;
import com.cloudops.mc.plugin.sdk.contexts.CallerContext;
import com.cloudops.mc.plugin.sdk.operation.OperationSection;
import com.cloudops.mc.plugin.sdk.operation.StepResult;
import com.cloudops.mc.plugin.sdk.ui.form.Form;
import com.cloudops.mc.plugin.sdk.ui.form.FormElement;
import com.cloudops.mc.plugin.sdk.viewbuilders.OperationViewBuilder;
import com.ttech.aws.plugin.s3p.entity.Object;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.singletonList;

@ViewBuilder(name = "create")
public class CreateObjectView extends OperationViewBuilder<Object> {
    @Override
    protected List<OperationSection<Object>> getSections() {
        return singletonList(section("TaskInfo", this::details));
    }

    private StepResult<Object> details(CallerContext callerContext, Object object) {
        // a list of Form elements in the details section
        List<FormElement> elements = new ArrayList<>();
        // Adding a `text input` form element
        elements.add(Form.text("name")
                .label("todoist.fields.name")
                .required()
                .build());

        elements.add(Form.text("due")
                .label("todoist.fields.due")
                .required()
                .description("todoist.fields.due_description")
                .build());

        elements.add(Form.text("dateString")
                .label("todoist.fields.dateString")
                .description("todoist.fields.dateString_description")
                .build());
        return new StepResult<>(elements, object);
    }

}
