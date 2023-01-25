package com.img.resource.web.controller;

import com.img.resource.error.CustomAsyncExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.sleuth.instrument.async.LazyTraceExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
@ComponentScan("com.img.resource")
//@EnableScheduling
@Slf4j
public class SpringAsyncConfig implements AsyncConfigurer {
    @Value("${NUM_THREADS}")
    private Integer parallelism;

    @Autowired
    private BeanFactory beanFactory;

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor scheduler = new ThreadPoolTaskExecutor();
        scheduler.setCorePoolSize(parallelism);
        scheduler.setMaxPoolSize(parallelism * 200);
        scheduler.setThreadNamePrefix("ResourceAsyncThread-");
        scheduler.initialize();
        log.debug("\n\n\nscheduler thread name prefix:" + scheduler.getThreadNamePrefix());
        System.out.println("\n\n\nscheduler thread name prefix:" + scheduler.getThreadNamePrefix());
//        return scheduler;
        return new LazyTraceExecutor(beanFactory, scheduler);
    }

    @Bean(name = "executorOne")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(200);
        executor.setQueueCapacity(500);
        executor.setKeepAliveSeconds(200);
        executor.setThreadNamePrefix("Browser-");
        log.debug("\n\n\nscheduler thread name prefix:" + executor.getThreadNamePrefix());
        System.out.println("\n\n\nscheduler thread name prefix:" + executor.getThreadNamePrefix());
        executor.initialize();
        return executor;
    }

    @Bean(name = "execFilter")
    public Executor taskExecutorF() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        int numOfCores = Runtime.getRuntime().availableProcessors();
        log.debug("number of cores: "+ numOfCores);
        executor.setCorePoolSize(numOfCores * 20);
        executor.setMaxPoolSize(numOfCores * 20);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("execFilter-");
        executor.setKeepAliveSeconds(200);
        executor.initialize();
        log.debug("schedule execFilter");
        log.debug("\n\n\nscheduler thread name prefix:" + executor.getThreadNamePrefix());
        log.debug("scheduler threads core:" + executor.getCorePoolSize());
        log.debug("scheduler pool size:" + executor.getPoolSize());
        log.debug("scheduler max pool size:" + executor.getMaxPoolSize());
        return executor;
    }


    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new CustomAsyncExceptionHandler();
    }
}
