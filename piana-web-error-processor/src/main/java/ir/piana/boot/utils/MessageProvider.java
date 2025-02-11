package ir.piana.boot.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;

//ToDo should be inspect
public class MessageProvider implements ApplicationContextAware {
    private final MessageSource messageSource;
    private static ApplicationContext applicationContext;

    public MessageProvider(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public MessageSource getMessageSource() {
        return messageSource;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        MessageProvider.applicationContext = applicationContext;
    }

    public static MessageProvider thisBean() {
        return applicationContext.getBean(MessageProvider.class);
    }

    public static MessageSource messageSource() {
        return thisBean().messageSource;
    }
}
