package ir.piana.boot.endpoint.core.manager;

import ir.piana.boot.endpoint.core.manager.dto.EndpointClientAuthMappable;
import ir.piana.boot.endpoint.core.manager.info.EndpointClientInfo;
import ir.piana.boot.utils.jedisutils.JedisPool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.LockConfiguration;
import net.javacrumbs.shedlock.core.LockingTaskExecutor;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;

//ToDo think about synchronize between multiple thread and multiple instance of app
@RequiredArgsConstructor
@Slf4j
public abstract class EndpointClientAuthEnable {
    private final JedisPool jedisPool;
    private final LockingTaskExecutor lockingTaskExecutor;

    public final EndpointClientAuthMappable authenticateOrLoad(
            RestClient restClient, EndpointClientInfo endpointClientInfo) {
        EndpointClientAuthMappable load = EndpointClientAuthMappable.load(
                jedisPool, endpointClientInfo.id());
        Map<String, EndpointClientAuthMappable> container = new LinkedHashMap<>();
        if (load == null) {
            lockingTaskExecutor.executeWithLock((Runnable) () -> {
                long start = System.currentTimeMillis();
                try {
                    container.put("load", authenticate(restClient, endpointClientInfo));
                } finally {
                    long end = System.currentTimeMillis();
                    log.info("Elapsed Time for {} in milli seconds: {}", (end - start));
                }
            }, new LockConfiguration(
                    Instant.now(),
                    "endpoint-" + endpointClientInfo.endpoint().name(),
                    Duration.of(5, ChronoUnit.SECONDS),
                    Duration.of(1, ChronoUnit.SECONDS))
            );
        }

        load = container.get("load");
        jedisPool.setRedisHashMappable(load);
        return load;
    }

    protected abstract EndpointClientAuthMappable authenticate(
            RestClient restClient, EndpointClientInfo endpointClientInfo);
}
