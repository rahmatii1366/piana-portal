package ir.piana.dev.openidc.gateway.controller.domain;

import ir.piana.dev.openidc.core.service.domain.DomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${oidc-ui.controller.base-url:oidc-ui}/api/v1/piana/oidc/domain-roles")
@RequiredArgsConstructor
public class DomainRolesController {
    private final DomainService domainService;


}
