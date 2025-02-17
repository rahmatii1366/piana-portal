package ir.piana.boot.utils.scheduler;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TransactionalTask {

    @Transactional
    public void run(Runnable runnable) {
        runnable.run();
    }

    @Transactional
    public AdjustableIntervalScheduler.IntervalByUnit exec(AdjustableIntervalScheduler adjustableIntervalScheduler) {
        return adjustableIntervalScheduler.exec();
    }
}
