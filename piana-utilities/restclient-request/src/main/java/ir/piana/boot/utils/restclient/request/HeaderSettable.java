package ir.piana.boot.utils.restclient.request;

import java.util.List;

public interface HeaderSettable {
    HeaderSettable setHeader(String name, String value);
    HeaderSettable putHeader(String name, List<String> values);
    QueryParamSettable then();
}
