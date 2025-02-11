package ir.piana.dev.inquiry.publisher.controller;

import ir.piana.boot.utils.errorprocessor.ApiAdviceHandler;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class PublisherAdviceHandler extends ApiAdviceHandler {
    public PublisherAdviceHandler(MessageSource messageSource) {
        super(messageSource);
    }
}
