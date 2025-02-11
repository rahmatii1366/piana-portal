package ir.piana.boot.inquiry.common.scheduler;

import lombok.*;
import net.javacrumbs.shedlock.core.LockConfiguration;
import net.javacrumbs.shedlock.core.LockingTaskExecutor;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.AtomicInteger;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class FixedIntervalScheduler implements SchedulingConfigurer, Runnable {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    private boolean shouldBeTransactional = false;
    private boolean readyToStart = false;
    private int initialDelay = 0;
    private String initialDelayUnit = "Seconds";
    private int period = 30;
    private String periodUnit = "Minutes";
    private String activeFrom = "00:00";
    private String activeTo = "23:59";
    private String schedulerLockName = null;
    private Duration lockAtMostFor = null;
    private Duration lockAtLeastFor = null;

    @Autowired
    private LockingTaskExecutor lockingTaskExecutor;

    @Autowired
    private ApplicationContext applicationContext;

    public static DateTimeFormatter dateTimeFormatterForLog = DateTimeFormatter.ofPattern("yyyy/MM/dd-HH:mm:ss");

    @Setter(AccessLevel.NONE)
    private AtomicInteger counter = new AtomicInteger(0);

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        logger.info("{} scheduler try to start", getSchedulerName());
        if (!readyToStart) {
            logger.info("{} scheduler not ready to start", getSchedulerName());
            return;
        }

        try {
            taskRegistrar.addTriggerTask(() -> {
                var nowInTehran = LocalDateTime.now(ZoneId.of("Asia/Tehran"));
                var activeFrom = LocalTime.parse(this.activeFrom);
                var activeTo = LocalTime.parse(this.activeTo);

                if (nowInTehran.toLocalTime().isAfter(activeFrom) && nowInTehran.toLocalTime().isBefore(activeTo)) {
                    logger.info("{} scheduler start running for the {} time at {}",
                            getSchedulerName(), counter.incrementAndGet(),
                            LocalDateTime.now().format(dateTimeFormatterForLog));
                    try {
                        if (!Strings.isEmpty(schedulerLockName)) {
                            lockingTaskExecutor.executeWithLock((Runnable) () -> {
                                long start = System.currentTimeMillis();
                                try {
                                    if (shouldBeTransactional) {
                                        applicationContext.getBean(TransactionalTask.class)
                                                .run(applicationContext.getBean(this.getClass()));
                                    } else {
                                        applicationContext.getBean(this.getClass()).run();
                                    }
                                } finally {
                                    long end = System.currentTimeMillis();
                                    logger.info("Elapsed Time for {} in milli seconds: {}",
                                            this.getSchedulerName(), (end - start));
                                }
                            }, new LockConfiguration(
                                    Instant.now(),
                                    schedulerLockName,
                                    lockAtMostFor == null ? Duration.of(60, ChronoUnit.MINUTES) : lockAtMostFor,
                                    lockAtLeastFor == null ? Duration.of(1, ChronoUnit.SECONDS): lockAtLeastFor));
                        } else {
                            long start = System.currentTimeMillis();
                            try {
                                if (shouldBeTransactional) {
                                    applicationContext.getBean(TransactionalTask.class)
                                            .run(applicationContext.getBean(this.getClass()));
                                } else {
                                    applicationContext.getBean(this.getClass()).run();
                                }
                            } finally {
                                long end = System.currentTimeMillis();
                                logger.info("Elapsed Time for {} in milli seconds: {}",
                                        this.getSchedulerName(), (end - start));
                            }
                        }
                    } catch (Exception e) {
                        logger.error("{} scheduler faced by an error at {} => \"{}\"",
                                getSchedulerName(),
                                LocalDateTime.now().format(dateTimeFormatterForLog),
                                e.getMessage(), e);
                    }
                }
            }, SchedulerInstantUtil.trigger(
                    initialDelay, initialDelayUnit,
                    period, periodUnit));
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        logger.info("{} scheduler is running ", getSchedulerName());
    }

    public String getSchedulerName() {
        return this.getClass().getSimpleName();
    }
}
