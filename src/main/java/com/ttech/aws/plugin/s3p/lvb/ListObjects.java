package com.ttech.aws.plugin.s3p.lvb;

import com.cloudops.mc.plugin.sdk.annotations.ViewBuilder;
import com.cloudops.mc.plugin.sdk.contexts.CallerContext;
import com.cloudops.mc.plugin.sdk.fetcher.EntityFetcher;
import com.cloudops.mc.plugin.sdk.ui.Icon;
import com.cloudops.mc.plugin.sdk.ui.metadata.FieldMetadata;
import com.cloudops.mc.plugin.sdk.ui.metadata.FieldValue;
import com.cloudops.mc.plugin.sdk.ui.metadata.Metadata;
import com.cloudops.mc.plugin.sdk.ui.metadata.OperationMetadata;
import com.cloudops.mc.plugin.sdk.viewbuilders.ListViewBuilder;

import com.ttech.aws.plugin.s3p.entity.Object;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ViewBuilder
public class ListObjects extends ListViewBuilder<Object> {
    public ListObjects(EntityFetcher<Object> fetcher) {
        super(fetcher);
        System.out.println("--------------------------------CONSTRUCTOR FETCHER");
    }

    @Override
    protected List<FieldMetadata> getFields(CallerContext callerContext) {
        System.out.println("--------------------------------GET FIELDS IN LSIT OBJECTS");
        List<FieldMetadata> objectListColumns = new ArrayList<>();
        objectListColumns.add(new FieldValue.Builder("name", "s3p.fields.name").build());
        objectListColumns.add(new FieldValue.Builder("url", "s3p.fields.url").build());
        return objectListColumns;
    }

    @Override
    protected List<OperationMetadata> getGeneralOperations() {
        System.out.println("--------------------------------GET GENERAL OPERATIONS IN LSIT OBJECTS");
        return Collections.singletonList(
                // Use the same identifier used for the operation name - 'create' and provide an ICON
                Metadata.operation("create", Icon.PLUS).build());
    }

    @Override
    protected List<OperationMetadata> getSpecificOperations() {
        System.out.println("--------------------------------GET SPECIIFIC OPERATIONS IN LSIT OBJECTS");
        return Collections.singletonList(Metadata.operation("delete", Icon.MINUS).build());
    }
}
