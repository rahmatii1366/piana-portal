package ir.piana.boot.utils.restclient.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;

class BodySettableImpl implements BodySettable {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public HeaderSettable bodyLess() {
        return new HeaderSettableImpl(null);
    }

    public HeaderSettable setBodyAsString(String body) {
        return new HeaderSettableImpl(new RestRequestBuilderImpl(body.getBytes()));
    }

    public HeaderSettable setBodyAsBytes(byte[] body) {
        return new HeaderSettableImpl(new RestRequestBuilderImpl(body));
    }

    public HeaderSettable setBody(Serializable body) {
        try {
            return new HeaderSettableImpl(new RestRequestBuilderImpl(
                    objectMapper.writeValueAsBytes(body)));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
