package ir.piana.boot.endpoint.manager;

import ir.piana.boot.utils.scheduler.FixedIntervalScheduler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
@EnableScheduling
@Slf4j
@ConditionalOnProperty(name = "piana.scheduler.enabled", matchIfMissing = false)
public class SchedulerConfig implements SchedulingConfigurer {
    private final ApplicationContext applicationContext;

    public SchedulerConfig(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Bean
    public TaskScheduler taskScheduler() {
        int allScheduledTasks = applicationContext.getBeansOfType(FixedIntervalScheduler.class).size();
        ExecutorService executorService = Executors.newFixedThreadPool(Math.max(allScheduledTasks, 5));

        executorService.submit(() -> log.info("taskScheduler thread pool known as {} initialized by {} thread.",
                Thread.currentThread().getName(), Math.max(allScheduledTasks, 5)));

        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(
                Math.max(allScheduledTasks, 5));

        scheduledExecutorService.submit(() -> log.info("taskScheduler thread pool known as {} initialized by {} thread.",
                Thread.currentThread().getName(), Math.max(allScheduledTasks, 5)));


        var concurrentTaskScheduler = new ConcurrentTaskScheduler();
        concurrentTaskScheduler.setScheduledExecutor(scheduledExecutorService);
        concurrentTaskScheduler.setConcurrentExecutor(executorService);
        return concurrentTaskScheduler;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setTaskScheduler(taskScheduler());
    }
}
