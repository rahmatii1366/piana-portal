package ir.piana.boot.utils.restclient.request;

import java.util.List;

class HeaderSettableImpl implements HeaderSettable {
    private final RestRequestBuilderImpl restClientRequesterBuilder;

    public HeaderSettableImpl(RestRequestBuilderImpl restClientRequesterBuilder) {
        this.restClientRequesterBuilder = restClientRequesterBuilder;
    }

    public HeaderSettableImpl setHeader(String name, String value) {
        restClientRequesterBuilder.headers.set(name, value);
        return this;
    }

    public HeaderSettableImpl putHeader(String name, List<String> values) {
        restClientRequesterBuilder.headers.put(name, values);
        return this;
    }

    public QueryParamSettable then() {
        return new QueryParamSettableImpl(restClientRequesterBuilder);
    }
}
