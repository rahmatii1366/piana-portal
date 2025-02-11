package ir.piana.dev.openidc.gateway.controller.domain;

import ir.piana.dev.openidc.core.service.domain.DomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${oidc-ui.controller.base-url:oidc-ui}/api/v1/piana/oidc/domain-permissions")
@RequiredArgsConstructor
public class DomainPermissionsController {
    private final DomainService domainService;


}
