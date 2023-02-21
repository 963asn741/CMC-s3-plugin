package com.ttech.aws.plugin.s3p.entity.operations;

import com.cloudops.mc.plugin.sdk.annotations.ViewBuilder;
import com.cloudops.mc.plugin.sdk.contexts.CallerContext;
import com.cloudops.mc.plugin.sdk.operation.OperationSection;
import com.cloudops.mc.plugin.sdk.operation.OperationStep;
import com.cloudops.mc.plugin.sdk.operation.StepResult;
import com.cloudops.mc.plugin.sdk.ui.form.Form;
import com.cloudops.mc.plugin.sdk.ui.form.FormElement;
import com.cloudops.mc.plugin.sdk.viewbuilders.OperationViewBuilder;
import com.ttech.aws.plugin.s3p.entity.Object;

import java.util.ArrayList;
import java.util.Arrays;
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
                .label("s3p.fields.name")
                .required()
                .build());

        elements.add(Form.text("url")
                .label("s3p.fields.due")
                .required()
                .description("s3p.fields.url_description")
                .build());
        return new StepResult<>(elements, object);
    }

    @Override
    protected List<OperationStep<Object>> getSteps() {
        OperationStep titleOfObject = stepBuilder("Name of the file",
                "Enter the name of the image that you want it to be saved as",
                singletonList(section("Name", this::itemName))).build();
        OperationStep urlOfObject = stepBuilder("URL of the file",
                "Enter the URL of the image that you want to save",
                singletonList(section("URL", this::itemUrl))).build();
        return Arrays.asList(titleOfObject, urlOfObject);
    }

    private StepResult<Object> itemName(CallerContext callerContext, Object object) {
        // a list of Form elements in the details section
        List<FormElement> elements = new ArrayList<>();
        // Adding a `text input` form element
        elements.add(Form.text("name")
                .label("s3p.fields.name")
                .required()
                .build());
        return new StepResult<>(elements, object);
    }

    private StepResult<Object> itemUrl(CallerContext callerContext, Object object){
        List<FormElement> elements = new ArrayList<>();
        elements.add(Form.text("url")
                .label("s3p.fields.url")
                .required()
                .build());
        return new StepResult<>(elements, object);
    }
}
