package ir.piana.boot.utils.scheduler;

import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class AdjustableIntervalScheduler implements Runnable {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    private int initialDelay = 0;
    private String initialDelayUnit = "Seconds";

    private String activeFrom;
    private String activeTo;

    private int defaultInterval = 120;
    private String defaultUnit = "Seconds";

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private TaskScheduler scheduler;

    @Setter(AccessLevel.NONE)
    private AtomicInteger counter = new AtomicInteger(0);

    @Setter(AccessLevel.NONE)
    private FinalContainer<ScheduledFuture<?>> container = new FinalContainer<>();
    private FinalContainer<IntervalByUnit> lastInterval = new FinalContainer<>();

    @EventListener(ApplicationReadyEvent.class)
    void createScheduler() {
        if (initialDelayUnit == null)
            initialDelayUnit = "SECONDS";
        addTaskToScheduler(initialDelay, ChronoUnit.valueOf(initialDelayUnit.toUpperCase()));
    }

    protected abstract IntervalByUnit exec();

    @Override
    public final void run() {
        logger.info("{} scheduler start running for the {} time at {}",
                getSchedulerName(), counter.incrementAndGet(),
                LocalDateTime.now().format(FixedIntervalScheduler.dateTimeFormatterForLog));
        if (Objects.nonNull(this.activeFrom) && Objects.nonNull(this.activeTo)) {
            var nowInTehran = LocalDateTime.now(ZoneId.of("Asia/Tehran"));
            var activeFrom = LocalTime.parse(this.activeFrom);
            var activeTo = LocalTime.parse(this.activeTo);
            if (nowInTehran.toLocalTime().isAfter(activeFrom) && nowInTehran.toLocalTime().isBefore(activeTo)) {
                executeAndReset();
            }
        } else {
            executeAndReset();
        }
    }

    private void executeAndReset() {
        try {
            long start = System.currentTimeMillis();
            IntervalByUnit interval = null;
            try {
                interval = applicationContext.getBean(TransactionalTask.class)
                        .exec(applicationContext.getBean(this.getClass()));
            } finally {
                long end = System.currentTimeMillis();
                logger.info("Elapsed Time for {} in milli seconds: {}",
                        this.getSchedulerName(), (end - start));
            }
            if (interval != null &&
                    (lastInterval.get().interval != interval.interval || lastInterval.get().unit != interval.unit)) {
                removeTaskFromScheduler();
                addTaskToScheduler(interval.interval, interval.unit);
                logger.info("{} next start time is {} - {}",
                        getSchedulerName(),
                        interval.interval,
                        interval.unit);
            } else {
                removeTaskFromScheduler();
                addTaskToScheduler(defaultInterval, ChronoUnit.valueOf(defaultUnit.toUpperCase()));
                logger.info("{} next start time is {} - {}",
                        getSchedulerName(),
                        defaultInterval,
                        defaultUnit);
            }
        } catch (Throwable t) {
            logger.error("{} scheduler faced by an error at {} => \"{}\"",
                    getSchedulerName(),
                    LocalDateTime.now().format(FixedIntervalScheduler.dateTimeFormatterForLog),
                    t.getMessage(), t);
        }

    }

    /*Instant getInstant(int sec) {
        return LocalDateTime.now().plusSeconds(sec).atZone(ZoneId.systemDefault()).toInstant();
    }*/

    private Instant getInstant(int period, ChronoUnit periodUnit) {
        return LocalDateTime.now().plus(period, periodUnit).atZone(ZoneId.systemDefault()).toInstant();
    }

    private void addTaskToScheduler(int period, ChronoUnit periodUnit) {
        logger.info("{} scheduler try to reset with {}({})", getSchedulerName(), periodUnit.name(), period);
        ScheduledFuture<?> scheduledTask = scheduler.schedule(this, getInstant(period, periodUnit));
        container.set(scheduledTask);
        lastInterval.set(new IntervalByUnit(period, periodUnit));
        logger.info("{} scheduler reseted to {}({})", getSchedulerName(), periodUnit.name(), period);
    }

    private void removeTaskFromScheduler() {
        ScheduledFuture<?> scheduledTask = container.get();
        if (scheduledTask != null) {
            scheduledTask.cancel(true);
            container.set(null);
            lastInterval.set(null);
        }
    }

    public abstract String getSchedulerName();

    public static class IntervalByUnit {
        int interval;
        ChronoUnit unit;

        public IntervalByUnit(int interval, ChronoUnit unit) {
            this.interval = interval;
            this.unit = unit;
        }
    }
}
