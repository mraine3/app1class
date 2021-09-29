package com.lessons.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.*;

@Service("com.lessons.services.AsyncService")
public class AsyncService {
    private static final Logger logger = LoggerFactory.getLogger(AsyncService.class);

    private ExecutorService executorService = null;
    private ExecutorCompletionService<?> completionService = null;


    @PostConstruct
    public void init() {
        final int TOTAL_THREAD_COUNT = 50;
        final int TOTAL_ACTIVE_THREAD_COUNT = 40;

        // Create a thread pool that can hold 50 threads total
        this.executorService = Executors.newFixedThreadPool(TOTAL_THREAD_COUNT);

        // Create a threadpool with a fixed number of threads operating off a shared unbounded queue.
        ExecutorService executorService = Executors.newFixedThreadPool(TOTAL_ACTIVE_THREAD_COUNT);
        this.completionService = new ExecutorCompletionService<>(executorService);
    }


    @PreDestroy
    public void destroy() {
        if (this.executorService != null) {
            logger.debug("destroy() Shutting down executorService started.");
            try {
                executorService.shutdown();

                // Wait 2 seconds threads to finish
                executorService.awaitTermination(2, TimeUnit.SECONDS);
            }
            catch (Exception e) {
                logger.warn("Ignoring exception raised shutting down the executor service.", e);
            }

            logger.debug("destroy() Shutting down executorService finished.");
        }
    }

    public void submit(Callable<?> aCallableOperation) {
        if (aCallableOperation == null) {
            throw new RuntimeException("Error in submit():  The passed-in callable reference is null.");
        }

        this.executorService.submit(aCallableOperation);
    }
}
