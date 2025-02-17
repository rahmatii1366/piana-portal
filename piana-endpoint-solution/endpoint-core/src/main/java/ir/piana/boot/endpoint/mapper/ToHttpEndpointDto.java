package ir.piana.boot.endpoint.mapper;

import ir.piana.boot.endpoint.dto.EndpointDto;
import ir.piana.boot.utils.restclientconfigurable.HttpEndpointDto;

import java.util.List;

public class ToHttpEndpointDto {
    public static List<HttpEndpointDto> map(String servicePointName, List<EndpointDto> endpointDtoList) {
        return endpointDtoList.stream()
                .map(endpointDto -> ToHttpEndpointDto.map(servicePointName, endpointDto))
                .toList();
    }

    public static HttpEndpointDto map(String servicePointName, EndpointDto endpointDto) {
        return new HttpEndpointDto(
                endpointDto.id(),
                servicePointName + "_" + endpointDto.name(),
                endpointDto.isDebugMode(),
                endpointDto.isSecure(),
                endpointDto.host(),
                endpointDto.port(),
                endpointDto.baseUrl(),
                endpointDto.soTimeout(),
                endpointDto.connectionTimeout(),
                endpointDto.socketTimeout(),
                endpointDto.timeToLive(),
                endpointDto.poolReusePolicy(),
                endpointDto.poolConcurrencyPolicy(),
                endpointDto.trustStore(),
                endpointDto.trustStorePassword(),
                0,
                endpointDto.tlsVersions()
        );
    }
}
