package ir.piana.dev.openidc.data.service;

import com.github.mfathi91.time.PersianDateTime;
import ir.piana.dev.openidc.core.dto.ApiResponse;
import ir.piana.dev.openidc.core.dto.PaginationResponse;
import ir.piana.dev.openidc.core.service.permission.dto.PermissionDto;
import ir.piana.dev.openidc.core.service.permission.PermissionService;
import ir.piana.dev.openidc.core.service.permission.dto.PermissionsForRoleInDomainRequestDto;
import ir.piana.dev.openidc.core.service.permission.dto.UIPermissionDto;
import ir.piana.dev.openidc.data.tables.PermissionType;
import ir.piana.dev.openidc.data.tables.Permissions;
import ir.piana.dev.openidc.data.tables.daos.PermissionsDao;
import ir.piana.dev.openidc.data.tables.pojos.PermissionsEntity;
import lombok.AllArgsConstructor;
import org.jooq.Record;
import org.jooq.Record2;
import org.jooq.SelectConditionStep;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@AllArgsConstructor
public class PermissionServiceImpl implements PermissionService {
    private final PermissionsDao permissionsDao;
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm:ss");

    @Override
    public PaginationResponse<PermissionDto> permissions(int offset, int size) {
        Integer count = permissionsDao.ctx().selectCount().from(Permissions.PERMISSIONS)
                .fetchOne(0, Integer.class);

        List<PermissionDto> permissions = permissionsDao.ctx().select()
                .from(Permissions.PERMISSIONS)
                .orderBy(Permissions.PERMISSIONS.ID)
                .limit(size)
                .offset(offset)
                .fetch().stream().map(record ->
                        new PermissionDto(record.get(Permissions.PERMISSIONS.ID),
                                record.get(Permissions.PERMISSIONS.NAME),
                                record.get(Permissions.PERMISSIONS.DESCRIPTION),
                                record.get(Permissions.PERMISSIONS.START_HOURS),
                                record.get(Permissions.PERMISSIONS.END_HOURS),
                                record.get(Permissions.PERMISSIONS.DISABLE),
                                PersianDateTime.fromGregorian(
                                        record.get(Permissions.PERMISSIONS.CREATE_ON)).format(dateTimeFormatter))
                ).toList();
        return new PaginationResponse<>(count, permissions);
    }

    @Override
    public PaginationResponse<UIPermissionDto> uiPermissions(int offset, int size) {
        SelectConditionStep<Record2<Long, String>> query = permissionsDao.ctx().select(
                        Permissions.PERMISSIONS.ID, Permissions.PERMISSIONS.NAME
                )
                .from(Permissions.PERMISSIONS).innerJoin(PermissionType.PERMISSION_TYPE)
                .on(Permissions.PERMISSIONS.PERMISSION_TYPE_ID.eq(PermissionType.PERMISSION_TYPE.ID))
                .where(PermissionType.PERMISSION_TYPE.NAME.eq("UI"));

        List<UIPermissionDto> permissions = query.orderBy(Permissions.PERMISSIONS.ID)
                .limit(size)
                .offset(offset)
                .fetch().stream().map(record ->
                        new UIPermissionDto(record.get(Permissions.PERMISSIONS.ID),
                                record.get(Permissions.PERMISSIONS.NAME))
                ).toList();

        Integer count = permissionsDao.ctx().selectCount().from(query)
                .fetchOne(0, Integer.class);
        return new PaginationResponse<>(count, permissions);
    }

    @Override
    public PaginationResponse<PermissionDto> permissionsForRoleInDomain(PermissionsForRoleInDomainRequestDto dto) {
        SelectConditionStep<Record> where = permissionsDao.ctx().select()
                .from(Permissions.PERMISSIONS)
                .where(Permissions.PERMISSIONS.DISABLE.eq(false))
                .and(Permissions.PERMISSIONS.NAME.like((dto.getName() == null ? "" : dto.getName()) + "%"));

        Integer count = permissionsDao.ctx().selectCount().from(where)
                .fetchOne(0, Integer.class);

        List<PermissionDto> permissions = where
                .orderBy(Permissions.PERMISSIONS.ID)
                .limit(dto.getSize())
                .offset(dto.getOffset())
                .fetch().stream().map(record ->
                        new PermissionDto(record.get(Permissions.PERMISSIONS.ID),
                                record.get(Permissions.PERMISSIONS.NAME),
                                record.get(Permissions.PERMISSIONS.DESCRIPTION),
                                record.get(Permissions.PERMISSIONS.START_HOURS),
                                record.get(Permissions.PERMISSIONS.END_HOURS),
                                record.get(Permissions.PERMISSIONS.DISABLE),
                                PersianDateTime.fromGregorian(
                                        record.get(Permissions.PERMISSIONS.CREATE_ON)).format(dateTimeFormatter))
                ).toList();
        return new PaginationResponse<>(count, permissions);
    }

    @Override
    public ApiResponse<?> create(String name, String description) {
        PermissionsEntity permissionEntity = new PermissionsEntity();
        permissionEntity.setName(name);
        permissionEntity.setDescription(description);
        permissionsDao.insert(permissionEntity);
        return new ApiResponse<>("success", "ok");
    }
}
