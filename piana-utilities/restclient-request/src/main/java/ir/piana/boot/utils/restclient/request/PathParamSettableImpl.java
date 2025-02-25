package ir.piana.boot.utils.restclient.request;

class PathParamSettableImpl implements PathParamSettable {
    private final RestRequestBuilderImpl restClientRequesterBuilder;

    public PathParamSettableImpl(RestRequestBuilderImpl restClientRequesterBuilder) {
        this.restClientRequesterBuilder = restClientRequesterBuilder;
    }

    public RestRequestBuilder noPathParam() {
        return restClientRequesterBuilder;
    }

    public PathParamSettableImpl setPathParam(String name, String value) {
        restClientRequesterBuilder.pathParam.put(name, value);
    return this;
    }

    public RestRequestBuilder then() {
        return restClientRequesterBuilder;
    }
}
