package ir.piana.boot.endpoint.manager;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class RestClientManagerHolder {
    private final List<RestClientManagerImpl> restClientManagerList;
}
