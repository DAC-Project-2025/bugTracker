package com.user_service.config;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableAsync
@EnableScheduling
@Slf4j
public class AsyncConfig implements AsyncConfigurer{
	/**
     * Default async executor for @Async methods
     */
    @Override
    @Bean(name = "taskExecutor")
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        // Core pool size - minimum number of threads
        executor.setCorePoolSize(5);
        
        // Maximum pool size - max threads to create
        executor.setMaxPoolSize(10);
        
        // Queue capacity - tasks waiting for thread
        executor.setQueueCapacity(100);
        
        // Thread name prefix for identification in logs
        executor.setThreadNamePrefix("async-");
        
        // Rejection policy when queue is full
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        
        // Wait for tasks to complete on shutdown
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        
        executor.initialize();
        
        log.info("Async Executor initialized - Core: {}, Max: {}, Queue: {}", 
            executor.getCorePoolSize(), 
            executor.getMaxPoolSize(), 
            executor.getQueueCapacity());
        
        return executor;
    }

    /**
     * Dedicated executor for email sending
     * Isolates email operations from other async tasks
     */
    @Bean(name = "emailExecutor")
    public Executor emailExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("email-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        
        executor.initialize();
        
        log.info("Email Executor initialized - Core: {}, Max: {}", 
            executor.getCorePoolSize(), 
            executor.getMaxPoolSize());
        
        return executor;
    }

    /**
     * Dedicated executor for Kafka outbox polling
     */
    @Bean(name = "outboxExecutor")
    public Executor outboxExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(25);
        executor.setThreadNamePrefix("outbox-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        
        executor.initialize();
        
        log.info("Outbox Executor initialized - Core: {}, Max: {}", 
            executor.getCorePoolSize(), 
            executor.getMaxPoolSize());
        
        return executor;
    }

    /**
     * Exception handler for uncaught async exceptions
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new CustomAsyncExceptionHandler();
    }

    /**
     * Custom exception handler for async methods
     */
    public static class CustomAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {
        
        @Override
        public void handleUncaughtException(Throwable throwable, Method method, Object... params) {
            log.error("Async method '{}' threw exception: {}", 
                method.getName(), 
                throwable.getMessage(), 
                throwable);
            
            // Additional error handling logic here
            // e.g., send alert, write to error log, retry logic
            
            // Example: Log method parameters for debugging
            if (params != null && params.length > 0) {
                log.error("Method parameters: {}", (Object[]) params);
            }
        }
    }
}