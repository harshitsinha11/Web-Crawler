package dev.harshit.crawler.service;

import dev.harshit.crawler.core.PageCrawl;
import dev.harshit.crawler.model.CrawlResponse;
import dev.harshit.crawler.model.PageData;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static dev.harshit.crawler.utils.HtmlUtils.extractLinks;

@Service
public class CrawlService {

    private static final int MAX_PAGES = 30;
    private static final int MAX_DEPTH = 5;
    private static final int THREAD_COUNT = 5;

    record CrawlData(String url,int depth){}

    public CrawlResponse start(List<String> seedUrls) {


        ConcurrentHashMap<String,PageData> resultMap = new ConcurrentHashMap<>();
        BlockingQueue<CrawlData> listOfUrls = new LinkedBlockingDeque<>();
        AtomicInteger pagesCount = new AtomicInteger(0);

        for(String url : seedUrls){
            listOfUrls.add(new CrawlData(url,0));
        }

        long startTime = System.currentTimeMillis();
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);


        for(int i=1;i<=THREAD_COUNT;i++){

            executorService.submit(()->{

                try{
                    while(pagesCount.get() < MAX_PAGES){

                        CrawlData task = listOfUrls.poll(500,TimeUnit.MILLISECONDS);

                        //Boundary Conditions
                        if(task == null) break;
                        if(task.depth > MAX_DEPTH) continue;
                        if(resultMap.containsKey(task.url)) continue;

                        System.out.println("Crawling URL: " + task.url());

                        PageData pageData = PageCrawl.crawlSinglePage(task.url());

                        System.out.println("PageData returned: " + pageData);


//                        if(pageData == null) continue;

                        if(resultMap.putIfAbsent(pageData.getUrl(),pageData)==null){

                            if(pagesCount.incrementAndGet() >= MAX_PAGES) break;

                            for (String child : extractLinks(pageData.getUrl())) {
                                if(!resultMap.containsKey(child)){
                                    listOfUrls.add(new CrawlData(child, task.depth + 1));
                                }
                            }

                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        executorService.shutdown();

        try {
            boolean finished = executorService.awaitTermination(2, TimeUnit.MINUTES);
            if (!finished) {
                System.out.println("Executor did not terminate in time");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }


        long endTime = System.currentTimeMillis();
        CrawlResponse response = new CrawlResponse();
        response.setTotalPages(resultMap.size());
        response.setCrawlTime(endTime-startTime);
        response.setResult(new ArrayList<>(resultMap.values()));
        return response;

    }
}
