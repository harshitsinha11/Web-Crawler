package dev.harshit.crawler.core;

import dev.harshit.crawler.model.PageData;
import dev.harshit.crawler.utils.HtmlUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class PageCrawl {
    public static PageData crawlSinglePage(String url) {

        PageData pageData = new PageData();
        pageData.setUrl(url); //1

        try {
            Document doc = Jsoup.connect(pageData.getUrl())
                    .userAgent("Mozilla/5.0")
                    .timeout(10000)
                    .get();

            String title = HtmlUtils.getTitle(doc);
            pageData.setTitle(title); //2
            pageData.setContent(HtmlUtils.getTextContent(doc)); //3
            pageData.setWordCount(HtmlUtils.getWordCount(doc)); //4


        } catch (Exception e) {
            System.out.println("Failed to fetch :" + pageData.getUrl());
        }
        return pageData;
    }
}
