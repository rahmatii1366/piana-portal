package ir.piana.boot.endpoint.servicepoint.restop;

public class TestRestOperation {
    void test() {
        Tes1 tes1 = new Tes1();
        tes1.call(restClient -> {
           return new RestClientResponse();
        });
    }

    static class Tes1 {

        public RestClientResponse call(
                RestClientOperator restClientOperator, Class<?> responseType) {
            // do some work then
            return restClientOperator.apply(null);
        }
    }
}
