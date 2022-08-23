package com.ttech.aws.plugin.s3p.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class S3pController {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Autowired
    private RestTemplate restTemplate;

    private ResponseEntity<String> getRequestResponseEntity(String baseUrl, String host, HttpMethod method) throws Exception{
        var httpHeaders = new HttpHeaders();
        httpHeaders.add("Host", host);
        HttpEntity httpEntity = new HttpEntity<>(httpHeaders);
        return restTemplate.exchange(baseUrl, method, httpEntity, String.class);
    }

    public void testConnectionToApi(String baseUrl) throws Exception {
        var response = getRequestResponseEntity(baseUrl, "s3.amazonaws.com", HttpMethod.GET);
        System.out.println(response);
    }
}
