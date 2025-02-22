package ir.piana.boot.endpoint.manager;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class EndpointManager {
    private final List<RestClientManager> restClientManagers;


}
