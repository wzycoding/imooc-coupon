package com.imooc.coupon.config;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * <h1>自定义生成优惠券异步线程池配置</h1>
 * <p>
 * *@EnableAsync这个注解也可以放到配置类，也可以放到启动入口中
 *
 * @author wzy
 * @version V1.0
 * @date 2020/6/26 11:50
 **/
@Slf4j
@Configuration
@EnableAsync
public class AsyncPoolConfig implements AsyncConfigurer {

    /**
     * 配置异步任务线程池
     *
     * @return 异步任务线程池对象
     */
    @Bean
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(20);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("ImoocAsync_");

        // 任务关闭线程池是否退出
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // 线程池关闭时最长等待时间
        executor.setAwaitTerminationSeconds(60);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        executor.initialize();
        return executor;
    }

    /**
     * 返回捕获异常处理类
     *
     * @return 异步任务异常处理对象
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new AsyncExceptionHandler();
    }

    /**
     * 捕获异常处理类
     */
    @SuppressWarnings("all")
    class AsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

        @Override
        public void handleUncaughtException(Throwable throwable,
                                            Method method,
                                            Object... objects) {
            // 打印异常堆栈
            throwable.printStackTrace();
            log.error("AsyncError: {}, Method: {}, Param:{}",
                    throwable.getMessage(), method.getName(), JSON.toJSONString(objects));
            //Todo: 发送邮件或发送短信，做进一步的处理
        }
    }
}
