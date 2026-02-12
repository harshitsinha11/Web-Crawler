package dev.harshit.crawler.utils;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class HtmlUtils {

    public static String getTitle(Document doc){
        String title = doc.title();
        return title != null ? title.trim() : "";
    }

    public static int getWordCount(Document doc){
        String text = doc.body().text();
        if(text.isBlank()) return 0;
        return text.trim().split("\\s+").length;
    }

    public static String getTextContent(Document doc){
        String content = doc.body().text().trim();
        if(content.length() <= 1000) return content;
        return truncateSafely(content);
    }

    public static List<String> extractLinks(String url){
        Document doc = fetch(url);
        List<String> children = new ArrayList<>();
        Elements elements = doc.select("a[href]");
        for(Element elem : elements){
            String absHref = elem.attr("abs:href");
            if (absHref.isBlank()) continue;

            String normalized = normalizeUrl(absHref);
            if (normalized != null) {
                children.add(normalized);
            }
        }
        return children;
    }

    public static String truncateSafely(String content){
        int lastSpace = content.lastIndexOf(' ',1000);
        if (lastSpace <= 0) {
            return content.substring(0, 1000) + "...";
        }
        return content.substring(0,lastSpace) + "...";
    }

    public static String normalizeUrl(String url){
        if(url == null) return null;
        String normalized = url.split("#")[0];

        if(normalized.endsWith("/")){
            normalized = normalized.substring(0,normalized.length()-1);
        }

        return normalized;
    }

    public static Document fetch(String url){
        try {
            return Jsoup.connect(url)
                    .userAgent("Mozilla/5.0")
                    .timeout(10000)
                    .get();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch :" + url);
        }
    }
}
