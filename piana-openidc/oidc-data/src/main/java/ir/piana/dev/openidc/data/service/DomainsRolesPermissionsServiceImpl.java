package ir.piana.dev.openidc.data.service;

import ir.piana.dev.openidc.core.dto.ApiResponse;
import ir.piana.dev.openidc.core.service.drp.DomainsRolesPermissionsService;
import ir.piana.dev.openidc.core.service.drp.dto.RoleByPermissionsAddToDomainRequestDto;
import ir.piana.dev.openidc.data.tables.daos.DomainsRolesPermissionsDao;
import ir.piana.dev.openidc.data.tables.pojos.DomainsRolesPermissionsEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class DomainsRolesPermissionsServiceImpl implements DomainsRolesPermissionsService {
    private final DomainsRolesPermissionsDao dao;

    public DomainsRolesPermissionsServiceImpl(DomainsRolesPermissionsDao dao) {
        this.dao = dao;
    }

    @Override
    public ApiResponse<?> addRoleByPermissionsIntoDomain(RoleByPermissionsAddToDomainRequestDto dto) {
        List<DomainsRolesPermissionsEntity> entities = dto.getPermissionIds().stream().map(
                permissionId -> new DomainsRolesPermissionsEntity(
                        dto.getDomainId(), dto.getRoleId(), permissionId, LocalDateTime.now()
                )).toList();

        dao.insert(entities);
        return new ApiResponse<>("success", "success");
    }
}
