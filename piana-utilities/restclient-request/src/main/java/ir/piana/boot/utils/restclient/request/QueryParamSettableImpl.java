package ir.piana.boot.utils.restclient.request;

class QueryParamSettableImpl implements QueryParamSettable {
    private final RestRequestBuilderImpl restClientRequesterBuilder;

    public QueryParamSettableImpl(RestRequestBuilderImpl restClientRequesterBuilder) {
        this.restClientRequesterBuilder = restClientRequesterBuilder;
    }

    public PathParamSettable noQueryParam() {
        return new PathParamSettableImpl(restClientRequesterBuilder);
    }

    public QueryParamSettableImpl setQueryParam(String name, String value) {
        restClientRequesterBuilder.queryParam.put(name, value);
        return this;
    }

    public PathParamSettable then() {
        return new PathParamSettableImpl(restClientRequesterBuilder);
    }
}
