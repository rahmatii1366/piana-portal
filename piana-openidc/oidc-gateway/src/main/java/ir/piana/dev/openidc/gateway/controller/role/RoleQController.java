package ir.piana.dev.openidc.gateway.controller.role;

import ir.piana.dev.openidc.core.dto.ApiResponse;
import ir.piana.dev.openidc.core.dto.PaginationRequest;
import ir.piana.dev.openidc.core.dto.PaginationResponse;
import ir.piana.dev.openidc.core.service.role.RoleService;
import ir.piana.dev.openidc.core.service.role.dto.RoleDto;
import ir.piana.dev.openidc.core.service.role.dto.RolePerUserOnDomainRequestDto;
import ir.piana.dev.openidc.core.service.role.dto.RolesForDomainDto;
import ir.piana.dev.openidc.core.service.role.dto.RolesForDomainRequestDto;
import ir.piana.dev.openidc.gateway.controller.role.dto.CreateRoleRequestDto;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${oidc-ui.controller.base-url:oidc-ui}/api/v1/piana/oidc/role")
@RequiredArgsConstructor
public class RoleQController {
    private final RoleService roleService;

    @GetMapping(path = "all")
    public ResponseEntity<PaginationResponse<RoleDto>> getRoles(
            HttpServletRequest request, @ModelAttribute PaginationRequest paginationRequest) {
        return ResponseEntity.ok(roleService.roles(
                paginationRequest.getOffset(), paginationRequest.getSize()));
    }

    @GetMapping(path = "per-user-on-domain")
    public ResponseEntity<PaginationResponse<RoleDto>> getRolesPerUserOnDomain(
            @Valid @ModelAttribute RolePerUserOnDomainRequestDto userPerDomainRequestDto) {
        return ResponseEntity.ok(roleService.roles(userPerDomainRequestDto));
    }

    @GetMapping(path = "for-domain")
    public ResponseEntity<PaginationResponse<RolesForDomainDto>> getRolesForDomain(
            @Valid @ModelAttribute RolesForDomainRequestDto dto) {
        return ResponseEntity.ok(roleService.rolesForDomain(dto));
    }
}
