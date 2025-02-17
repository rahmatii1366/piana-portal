//package ir.piana.boot.utils.endpointlimiter.operation;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import ir.piana.boot.endpoint.dto.ServicePointCollectionDto;
//import ir.piana.boot.endpoint.mapper.ToHttpEndpointDto;
//import ir.piana.boot.utils.jedisutils.JedisPool;
//import ir.piana.boot.utils.natsclient.NatsConfig;
//import ir.piana.boot.utils.restclientconfigurable.HttpEndpointDto;
//import ir.piana.boot.utils.restclientconfigurable.RestClientBuilderUtils;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.autoconfigure.AutoConfigureBefore;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.support.GenericApplicationContext;
//
//import java.util.List;
//
//@Configuration
//@AutoConfigureBefore(NatsConfig.class)
//@Slf4j
//public class RestClientOperationHandleableBeanConfiguration {
//    @Bean
//    RestClientOperationHandleableProvider restClientOperationHandleableBeanCreator(
//            JedisPool jedisPool,
//            ObjectMapper objectMapper,
//            GenericApplicationContext applicationContext,
//            RestClientBuilderUtils restClientBuilderUtils,
//            List<RestClientOperationHandleable> restClientOperationHandleables
//    ) throws JsonProcessingException {
//        RestClientOperationHandleableBeanCreator creator = new RestClientOperationHandleableBeanCreator(
//                jedisPool, objectMapper, applicationContext, restClientBuilderUtils, restClientOperationHandleables
//        );
//        String endpoints = jedisPool.getKey("endpoints");
//        if (endpoints != null && !endpoints.isEmpty()) {
//            ServicePointCollectionDto servicePointCollectionDto = objectMapper.readValue(
//                    endpoints, ServicePointCollectionDto.class);
//            creator.registerServicePointCollectionBean(servicePointCollectionDto);
//            List<HttpEndpointDto> list = servicePointCollectionDto.servicePoints().stream()
//                    .map(servicePointDto -> ToHttpEndpointDto.map(
//                            servicePointDto.name(), servicePointDto.endpoints()))
//                    .flatMap(List::stream)
//                    .toList();
//            creator.refreshRestClients(list);
//            restClientOperationHandleables.forEach(
//                    restClientOperationHandleable -> {
//                        try {
//                            restClientOperationHandleable.refreshLimitation(servicePointCollectionDto);
//                        } catch (Exception e) {
//                            //ToDo check properly behavior
//                            log.error("refresh limitation failed", e);
//                        }
//                    });
//        }
//        return creator;
//    }
//}
