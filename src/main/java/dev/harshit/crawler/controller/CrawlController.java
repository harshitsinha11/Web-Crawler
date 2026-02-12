package dev.harshit.crawler.controller;


import dev.harshit.crawler.model.CrawlResponse;
import dev.harshit.crawler.service.CrawlService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@RestController
@RequestMapping("/crawl")
public class CrawlController {

    private final CrawlService crawlService;

    public CrawlController(CrawlService crawlService) {
        this.crawlService = crawlService;
    }

    @PostMapping("/start")
    @ResponseBody
    public ResponseEntity<CrawlResponse> startCrawl(@RequestParam(required = false) String url1,
                                                    @RequestParam(required = false) String url2,
                                                    @RequestParam(required = false) String url3,
                                                    @RequestParam(required = false) String url4,
                                                    @RequestParam(required = false) String url5){
        List<String> seedUrls = Stream.of(url1,url2,url3,url4,url5)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        if (seedUrls.isEmpty()){
            throw new IllegalArgumentException("At least one url is required");
        }
        return ResponseEntity.ok(crawlService.start(seedUrls));
    }

}
