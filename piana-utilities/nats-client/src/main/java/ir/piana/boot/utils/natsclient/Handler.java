package ir.piana.boot.utils.natsclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.nats.client.Message;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import ir.piana.boot.utils.errorprocessor.ApiException;
import ir.piana.boot.utils.errorprocessor.BadRequestTypes;
import ir.piana.boot.utils.errorprocessor.InternalServerErrorTypes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.SmartValidator;

@Component
@Slf4j
public class Handler {
    protected final MessageSource messageSource;
    protected final SmartValidator validator;
    protected final ObjectMapper objectMapper;

    public Handler(
            MessageSource messageSource, SmartValidator validator, ObjectMapper objectMapper) {
        this.messageSource = messageSource;
        this.validator = validator;
        this.objectMapper = objectMapper;
    }

    public void handle(MessageHandler messageHandler, Message message) {
        try {
            var buffer = Buffer.buffer().appendBytes(message.getData());
            log.debug("Message: {}, {}", message.getSubject(), buffer.toString());

            var request = buffer.length() != 0 ? buffer.toJsonObject().mapTo(messageHandler.dtoType()) : new Object();
            var errors = validator.validateObject(request);
            if (errors.hasErrors()) {
                var error = errors.getFieldErrors().getFirst();
                error(message, BadRequestTypes.REQUEST_BODY_NOT_VALID.newException());
                return;
            }

            var response = messageHandler.apply(request);
            publish(message, response);
        } catch (ApiException e) {
            log.error("Unable to handle nats request in {}, errKey : {}, {}",
                    messageHandler.subject(), e.getApiError().getCode(), e.getMessage());
            error(message, e);
        } catch (Exception e) {
            log.error("Unable to handle nats request in {}, {}", messageHandler.subject(), e.getMessage());
            error(message, InternalServerErrorTypes.INTERNAL_SERVER_ERROR.newException(e));
        }
    }

    protected void publish(Message message, Object response) {
        if (message.getReplyTo() != null) {
            if (response != null) {
                message.getConnection().publish(message.getReplyTo(), JsonObject.mapFrom(response).toBuffer().getBytes());
            } else {
                message.getConnection().publish(message.getReplyTo(), new byte[]{});
            }
        }
    }

    protected void error(Message message, ApiException apiException) {
//    protected void error(Message message, String code, String errorMessage, int httpStatusCode) {
        if (message.getReplyTo() != null) {
            var json = new JsonObject()
                    .put("code", apiException.getApiError().getCode())
                    .put("message", apiException.getApiError().getMessage())
                    .put("httpStatusCode", apiException.getStatus().value());

            message.getConnection().publish(message.getReplyTo(), json.toBuffer().getBytes());
        }
    }
}
