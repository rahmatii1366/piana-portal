/*
package ir.piana.boot.inquiry.servicepoint.vehiclethirdpartyinsurance;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ir.piana.boot.inquiry.servicepoint.vehiclethirdpartyinsurance.dto.TabanTokenHashMappable;
import ir.piana.boot.inquiry.servicepoint.vehiclethirdpartyinsurance.dto.VehicleThirdPartyInsuranceRequest;
import ir.piana.boot.utils.endpointlimiter.operation.RestClientOperationHandleable;
import ir.piana.boot.utils.errorprocessor.InternalServerErrorTypes;
import ir.piana.boot.utils.jedisutils.JedisPool;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
class TabanHandleable<R>
        extends RestClientOperationHandleable<VehicleThirdPartyInsuranceRequest, R> {
    private final ObjectMapper objectMapper;
    private final JedisPool jedisPool;

    @Value("${piana.config.third-parties.taban.token}")
    private String token;

    public TabanHandleable(
            ApplicationContext applicationContext,
            JedisPool jedisPool,
            ObjectMapper objectMapper) {
        super(applicationContext);
        this.jedisPool = jedisPool;
        this.objectMapper = objectMapper;
    }

    private TabanTokenHashMappable getAuthToken() {
        try {
            TabanTokenHashMappable redisHashMappable = jedisPool.getRedisHashMappable(TabanTokenHashMappable.class);
            if (redisHashMappable == null) {
                redisHashMappable = getRestClient().post()
                        .uri("token?grant_type=client_credentials")
//                    .header("Postman-Token", "7c15e34c-00e2-4303-b69a-35308b3e2bae")
//                    .header("User-Agent", "PostmanRuntime/7.37.3")
                        .header(
                                "Authorization",
                                "Basic " + token)
                        .exchange((clientRequest, clientResponse) -> {
                            if (clientResponse.getStatusCode().is2xxSuccessful()) {
                                JsonNode jsonNode = objectMapper.readTree(clientResponse.getBody());
                                TabanTokenHashMappable tabanTokenHashMappable = new TabanTokenHashMappable(
                                        jsonNode.get("access_token").asText(),
                                        jsonNode.get("scope").asText(),
                                        jsonNode.get("token_type").asText(),
                                        jsonNode.get("expires_in").asLong()
                                );
                                jedisPool.setRedisHashMappable(tabanTokenHashMappable);
                                return tabanTokenHashMappable;
                            }
                            return null;
                        });

                return redisHashMappable;
            }
            return redisHashMappable;
        } catch (Exception e) {
            throw InternalServerErrorTypes.INTERNAL_SERVER_ERROR.newException(e);
        }
    }

    public JsonNode getByVin(String vin, String productionYear) {
        try {
            TabanTokenHashMappable authToken = getAuthToken();
            return getRestClient().post()
                    .uri("VSBV/1/api/ThirdPartyInsurance/SearchByVin")
                    .header("token", "Bearer " + authToken.getAccessToken())
                    .header(
                            "Authorization",
                            "Basic " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(objectMapper.createObjectNode()
                            .put("vin", vin)
                            .put("productionYear", productionYear))
                    .exchange((clientRequest, clientResponse) ->
                            objectMapper.readTree(clientResponse.getBody().readAllBytes()));
        } catch (NoSuchBeanDefinitionException e) {
            throw e;
        } catch (Throwable t) {
            throw InternalServerErrorTypes.INTERNAL_SERVER_ERROR.newException(t);
        }
    }

    @Override
    protected String servicePointName() {
        return "vehicle_third_party_insurance";
    }

    @Override
    protected String endpointName() {
        return "taban";
    }

    @Override
    protected R doRequest(VehicleThirdPartyInsuranceRequest requestDto) {
        return (R) getByVin(requestDto.vin(), requestDto.productionYear());
    }
}
*/
