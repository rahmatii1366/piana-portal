package ir.piana.dev.openidc.gateway.controller.domain;

import ir.piana.dev.openidc.core.dto.PaginationResponse;
import ir.piana.dev.openidc.core.service.permission.dto.PermissionDto;
import ir.piana.dev.openidc.core.service.domain.DomainService;
import ir.piana.dev.openidc.gateway.controller.domain.dto.DomainRolePermissionsRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${oidc-ui.controller.base-url:oidc-ui}/api/v1/piana/oidc/domain-roles")
@RequiredArgsConstructor
public class DomainRolePermissionsController {
    private final DomainService domainService;

    @GetMapping(path = "permissions")
    public ResponseEntity<PaginationResponse<PermissionDto>> getClientGroups(
            @Valid @ModelAttribute DomainRolePermissionsRequestDto dto) {
        return ResponseEntity.ok(domainService.domainRolePermissions(
                dto.getOffset(), dto.getSize(), dto.getDomainId(), dto.getRoleId()));
    }
}
