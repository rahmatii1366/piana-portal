package ir.piana.dev.openidc.utility;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
public class MessageProvider {
    private static MessageSource messageSource;

    public MessageProvider(MessageSource messageSource) {
        MessageProvider.messageSource = messageSource;
    }

    public static MessageSource messageSource() {
        return messageSource;
    }
}
