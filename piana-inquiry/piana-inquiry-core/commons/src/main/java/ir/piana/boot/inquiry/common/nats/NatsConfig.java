package ir.piana.boot.inquiry.common.nats;

import io.nats.client.Connection;
import io.nats.client.Nats;
import io.nats.client.Options;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.support.GenericWebApplicationContext;

import java.io.IOException;
import java.util.List;

@Configuration
public class NatsConfig {

    @Value("${piana.tools.nats.url}")
    private String natsUrl;
    @Value("${piana.tools.nats.status:disable}")
    private String natsStatus;
    private final Handler handler;
    private final List<MessageHandler<?>> messageHandlers;
    private final GenericWebApplicationContext applicationContext;


    @Autowired
    public NatsConfig(
            GenericWebApplicationContext applicationContext,
            Handler handler, List<MessageHandler<?>> messageHandlers) {
        this.applicationContext = applicationContext;
        this.handler = handler;
        this.messageHandlers = messageHandlers;
    }

    //ToDo should be enabled
    @PostConstruct
    public void natsConnection() throws IOException, InterruptedException {
        if (natsStatus.equals("enable")) {
            Options options = new Options.Builder().server(natsUrl).build();
            var connection = Nats.connect(options);
            messageHandlers.forEach(messageHandler -> {
                        if (messageHandler.queue() == null || messageHandler.queue().isEmpty()) {
                            connection.createDispatcher().subscribe(messageHandler.subject(),
                                    msg -> handler.handle(messageHandler, msg));
                        } else {
                            connection.createDispatcher().subscribe(messageHandler.subject(),
                                    messageHandler.subject().concat("." + messageHandler.queue()),
                                    msg -> handler.handle(messageHandler, msg));
                        }
                    }
            );
            applicationContext.registerBean("piana-nats", Connection.class, () -> connection);
        }
    }
}
