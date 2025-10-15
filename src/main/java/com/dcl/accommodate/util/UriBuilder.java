package com.dcl.accommodate.util;

import com.dcl.accommodate.config.AppEnv;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
public class UriBuilder {
    private String baseURI;

    public UriBuilder(AppEnv env){
        this.baseURI = env.getBaseURI();
    }

    public String buildPattern(String path){
        return baseURI + path;
    }

    public String buildPublicPattern(String path){
        return baseURI + "/public" + path;
    }

    public URI buildURI(String path){
        return URI.create(buildPattern(path));
    }

    public URI buildPublicURI(String path){
        return URI.create(buildPublicPattern(path));
    }
}
