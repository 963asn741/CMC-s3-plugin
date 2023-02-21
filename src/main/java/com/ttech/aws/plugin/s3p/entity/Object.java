    package com.ttech.aws.plugin.s3p.entity;

    import com.cloudops.mc.plugin.sdk.annotations.Entity;
    import com.cloudops.mc.plugin.sdk.entity.ServiceEntity;
    import lombok.Data;

    import java.net.URL;

    @Data
    @Entity(value = "object")
    public class Object implements ServiceEntity {
        private String id;
        private String name;
        private String url;

        public Object(String id, String name, String url) {
            this.id = id;
            this.name = name;
            this.url = url;
        }

        @Override
        public String getId() {
            return this.id;
        }
    }
