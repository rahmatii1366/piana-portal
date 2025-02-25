package ir.piana.boot.utils.restclient.request;

public class RequesterBuilderTest {
    public void test() {
        RestRequest restRequestImpl = RestRequest.builder()
                .bodyLess()
                .setHeader("k", "1")
                .then()
                .setQueryParam("k", "1")
                .then()
                .noPathParam().build();
    }
}
