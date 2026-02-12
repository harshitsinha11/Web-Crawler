package dev.harshit.crawler.model;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class PageData {

    private String url;
    private String title;
    private int wordCount;
    private String content;

    public void setUrl(String url){
        this.url = normalizer(url);
    }

    private String normalizer(String url){
        if(url == null) return null;
        String normalized = url.split("#")[0];

        if(normalized.endsWith("/")){
            normalized = normalized.substring(0,normalized.length()-1);
        }

        return normalized;
    }
}
