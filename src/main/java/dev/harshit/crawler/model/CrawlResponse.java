package dev.harshit.crawler.model;

import lombok.Data;
import java.util.List;

@Data
public class CrawlResponse {
    private int totalPages;
    private long crawlTime;
    private List<PageData> result;
}
