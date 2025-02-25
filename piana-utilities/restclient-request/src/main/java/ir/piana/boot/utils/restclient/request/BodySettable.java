package ir.piana.boot.utils.restclient.request;

import java.io.Serializable;

public interface BodySettable {
    HeaderSettable bodyLess();
    HeaderSettable setBodyAsString(String body);
    HeaderSettable setBodyAsBytes(byte[] body);
    HeaderSettable setBody(Serializable body);
}
