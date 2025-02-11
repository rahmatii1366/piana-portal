package ir.piana.dev.openidc.data.service;

import com.github.mfathi91.time.PersianDateTime;
import ir.piana.dev.openidc.core.dto.*;
import ir.piana.dev.openidc.core.service.domain.DomainService;
import ir.piana.dev.openidc.core.service.domain.dto.DomainDto;
import ir.piana.dev.openidc.core.service.domain.dto.DomainForUserDto;
import ir.piana.dev.openidc.core.service.domain.dto.GetDomainsForUserRequest;
import ir.piana.dev.openidc.core.service.permission.dto.PermissionDto;
import ir.piana.dev.openidc.core.service.role.dto.RoleDto;
import ir.piana.dev.openidc.data.tables.*;
import ir.piana.dev.openidc.data.tables.daos.DomainsDao;
import ir.piana.dev.openidc.data.tables.pojos.DomainsEntity;
import lombok.AllArgsConstructor;
import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class DomainServiceImpl implements DomainService {
    private final DomainsDao domainsDao;
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm:ss");

    @Override
    public PaginationResponse<DomainDto> domains(int offset, int size) {
        Integer count = domainsDao.ctx().selectCount().from(Domains.DOMAINS)
                .fetchOne(0, Integer.class);

        SelectSeekStep1<Record, Long> query = domainsDao.ctx().select()
                .from(Domains.DOMAINS)
                .orderBy(Domains.DOMAINS.ID);

        if (size > 0) {
            query.limit(size).offset(offset);
        }
        List<DomainDto> domains = query.fetch().stream().map(record ->
                new DomainDto(record.get(Domains.DOMAINS.ID),
                        record.get(Domains.DOMAINS.NAME),
                        record.get(Domains.DOMAINS.DESCRIPTION),
                        PersianDateTime.fromGregorian(
                                record.get(Domains.DOMAINS.CREATE_ON)).format(dateTimeFormatter))
        ).toList();
        return new PaginationResponse<>(count, domains);
    }

    public PaginationResponse<DomainForUserDto> domainsForUser(GetDomainsForUserRequest request) {
        Integer count = domainsDao.ctx().selectCount().from(Domains.DOMAINS)
                .fetchOne(0, Integer.class);

        /*Table<Record2<Long, Long>> udr = domainsDao.ctx().select(
                        UsersDomainsRoles.USERS_DOMAINS_ROLES.DOMAIN_ID,
                        UsersDomainsRoles.USERS_DOMAINS_ROLES.USER_ID)
                .from(UsersDomainsRoles.USERS_DOMAINS_ROLES)
                .where(UsersDomainsRoles.USERS_DOMAINS_ROLES.USER_ID.eq(request.getUserId())).asTable("udr");

        List<DomainForUserDto> domains = domainsDao.ctx()
                .select(Domains.DOMAINS.ID, Domains.DOMAINS.NAME, Domains.DOMAINS.DESCRIPTION,
                        DSL.field("USER_ID", Long.class))
                .from(Domains.DOMAINS)
                .leftJoin(udr)
                .on(Domains.DOMAINS.ID.eq(udr.field("domain_id", UsersDomainsRoles.USERS_DOMAINS_ROLES.DOMAIN_ID.getDataType())))
                .orderBy(Domains.DOMAINS.ID)
                .limit(request.getSize())
                .offset(request.getOffset())
                .fetch().stream().map(record ->
                        new DomainForUserDto(record.get(Domains.DOMAINS.ID),
                                record.get(Domains.DOMAINS.NAME),
                                record.get(Domains.DOMAINS.DESCRIPTION),
                                record.get(DSL.field("USER_ID", udr.field("user_id"))) != null)
                ).toList();*/

        Table<Record2<Long, Long>> ud = domainsDao.ctx().select(
                        DomainsUsers.DOMAINS_USERS.DOMAIN_ID,
                        DomainsUsers.DOMAINS_USERS.USER_ID)
                .from(DomainsUsers.DOMAINS_USERS)
                .where(DomainsUsers.DOMAINS_USERS.USER_ID.eq(request.getUserId())).asTable("ud");

        List<DomainForUserDto> domains = domainsDao.ctx()
                .select(Domains.DOMAINS.ID, Domains.DOMAINS.NAME, Domains.DOMAINS.DESCRIPTION,
                        DSL.field("USER_ID", Long.class))
                .from(Domains.DOMAINS)
                .leftJoin(ud)
                .on(Domains.DOMAINS.ID.eq(ud.field("domain_id", Long.class)))
                .orderBy(Domains.DOMAINS.ID)
                .limit(request.getSize())
                .offset(request.getOffset())
                .fetch().stream().map(record ->
                        new DomainForUserDto(record.get(Domains.DOMAINS.ID),
                                record.get(Domains.DOMAINS.NAME),
                                record.get(Domains.DOMAINS.DESCRIPTION),
                                record.get(DSL.field("USER_ID", ud.field("user_id"))) != null)
                ).toList();
        return new PaginationResponse<>(count, domains);
    }

    public PaginationResponse<RoleDto> domainRoles(int offset, int size, long domainId) {
        Integer count = domainsDao.ctx().selectCount().from(
                domainsDao.ctx().select(Roles.ROLES)
                        .from(DomainsRolesPermissions.DOMAINS_ROLES_PERMISSIONS).innerJoin(Roles.ROLES)
                        .on(DomainsRolesPermissions.DOMAINS_ROLES_PERMISSIONS.ROLE_ID.eq(Roles.ROLES.ID))
                        .where(DomainsRolesPermissions.DOMAINS_ROLES_PERMISSIONS.DOMAIN_ID.eq(domainId))
        ).fetchOne(0, Integer.class);

        List<RoleDto> map = domainsDao.ctx().select(
                        Roles.ROLES.ID, Roles.ROLES.NAME, Roles.ROLES.DESCRIPTION)
                .from(DomainsRolesPermissions.DOMAINS_ROLES_PERMISSIONS).innerJoin(Roles.ROLES)
                .on(DomainsRolesPermissions.DOMAINS_ROLES_PERMISSIONS.ROLE_ID.eq(Roles.ROLES.ID))
                .where(DomainsRolesPermissions.DOMAINS_ROLES_PERMISSIONS.DOMAIN_ID.eq(domainId))
                .orderBy(Roles.ROLES.ID)
                .limit(size)
                .offset(offset)
                .fetch().map(record -> new RoleDto(
                        record.get(Roles.ROLES.ID),
                        record.get(Roles.ROLES.NAME),
                        record.get(Roles.ROLES.DESCRIPTION)
                ));

        return new PaginationResponse<>(count.intValue(), map);
    }

    @Override
    public ApiResponse<?> create(String name, String description) {
        DomainsEntity domain = new DomainsEntity();
        domain.setName(name);
        domain.setDescription(description);
        domainsDao.insert(domain);
        return new ApiResponse<>("success", "ok");
    }

    @Override
    public PaginationResponse<PermissionDto> domainRolePermissions(
            int offset, int size, Long domainId, Long roleId) {
        Integer i = domainsDao.ctx()
                .selectCount()
                .from(domainsDao.ctx()
                        .select(Permissions.PERMISSIONS.ID, Permissions.PERMISSIONS.NAME, Permissions.PERMISSIONS.DISABLE)
                        .from(DomainsRolesPermissions.DOMAINS_ROLES_PERMISSIONS)
                        .innerJoin(Permissions.PERMISSIONS)
                        .on(DomainsRolesPermissions.DOMAINS_ROLES_PERMISSIONS.PERMISSION_ID.eq(Permissions.PERMISSIONS.ID))
                        .where(DomainsRolesPermissions.DOMAINS_ROLES_PERMISSIONS.DOMAIN_ID.eq(domainId))
                        .and(DomainsRolesPermissions.DOMAINS_ROLES_PERMISSIONS.ROLE_ID.eq(roleId)))
                .fetchOne(0, Integer.class);

        List<PermissionDto> permissions = domainsDao.ctx()
                .select(Permissions.PERMISSIONS.ID, Permissions.PERMISSIONS.NAME, Permissions.PERMISSIONS.DISABLE)
                .from(DomainsRolesPermissions.DOMAINS_ROLES_PERMISSIONS)
                .innerJoin(Permissions.PERMISSIONS)
                .on(DomainsRolesPermissions.DOMAINS_ROLES_PERMISSIONS.PERMISSION_ID.eq(Permissions.PERMISSIONS.ID))
                .where(DomainsRolesPermissions.DOMAINS_ROLES_PERMISSIONS.DOMAIN_ID.eq(domainId))
                .and(DomainsRolesPermissions.DOMAINS_ROLES_PERMISSIONS.ROLE_ID.eq(roleId))
                .orderBy(Permissions.PERMISSIONS.ID)
                .limit(size)
                .offset(offset)
                .fetch().map(mapper ->
                        new PermissionDto(
                                mapper.get(Permissions.PERMISSIONS.ID),
                                mapper.get(Permissions.PERMISSIONS.NAME),
                                mapper.get(Permissions.PERMISSIONS.DISABLE))
                );

        return new PaginationResponse<>(Optional.ofNullable(i).orElse(0), permissions);
    }

    @Override
    public List<Long> userDomainPermissionsByType(
            Long userId, Long domainId, String permissionTypeName) {
        SelectConditionStep<Record1<Long>> select = domainsDao.ctx()
                .select(Permissions.PERMISSIONS.ID)
                .from(UsersDomainsRoles.USERS_DOMAINS_ROLES)
                .innerJoin(DomainsRolesPermissions.DOMAINS_ROLES_PERMISSIONS)
                .on(UsersDomainsRoles.USERS_DOMAINS_ROLES.DOMAIN_ID.eq(DomainsRolesPermissions.DOMAINS_ROLES_PERMISSIONS.DOMAIN_ID))
                .and(UsersDomainsRoles.USERS_DOMAINS_ROLES.ROLE_ID.eq(DomainsRolesPermissions.DOMAINS_ROLES_PERMISSIONS.ROLE_ID))
                .innerJoin(Permissions.PERMISSIONS)
                .on(DomainsRolesPermissions.DOMAINS_ROLES_PERMISSIONS.PERMISSION_ID.eq(Permissions.PERMISSIONS.ID))
                .innerJoin(PermissionType.PERMISSION_TYPE)
                .on(Permissions.PERMISSIONS.PERMISSION_TYPE_ID.eq(PermissionType.PERMISSION_TYPE.ID))
                .where(UsersDomainsRoles.USERS_DOMAINS_ROLES.DOMAIN_ID.eq(domainId))
                .and(UsersDomainsRoles.USERS_DOMAINS_ROLES.USER_ID.eq(userId))
                .and(PermissionType.PERMISSION_TYPE.NAME.eq(permissionTypeName));

        Integer i = domainsDao.ctx()
                .selectCount()
                .from(select)
                .fetchOne(0, Integer.class);

        List<Long> permissions = select
                .orderBy(Permissions.PERMISSIONS.ID)
                .fetch().map(mapper ->
                        mapper.get(Permissions.PERMISSIONS.ID)
                );

        return permissions;
    }

    @Override
    public PaginationResponse<PermissionDto> domainPermissions(
            int offset, int size, Long domainId) {
        Integer i = domainsDao.ctx()
                .selectCount()
                .from(domainsDao.ctx()
                        .select(Permissions.PERMISSIONS.ID, Permissions.PERMISSIONS.NAME, Permissions.PERMISSIONS.DISABLE)
                        .from(DomainsRolesPermissions.DOMAINS_ROLES_PERMISSIONS)
                        .innerJoin(Permissions.PERMISSIONS)
                        .on(DomainsRolesPermissions.DOMAINS_ROLES_PERMISSIONS.PERMISSION_ID.eq(Permissions.PERMISSIONS.ID))
                        .where(DomainsRolesPermissions.DOMAINS_ROLES_PERMISSIONS.DOMAIN_ID.eq(domainId)))
                .fetchOne(0, Integer.class);

        List<PermissionDto> permissions = domainsDao.ctx()
                .select(Permissions.PERMISSIONS.ID, Permissions.PERMISSIONS.NAME, Permissions.PERMISSIONS.DISABLE)
                .from(DomainsRolesPermissions.DOMAINS_ROLES_PERMISSIONS)
                .innerJoin(Permissions.PERMISSIONS)
                .on(DomainsRolesPermissions.DOMAINS_ROLES_PERMISSIONS.PERMISSION_ID.eq(Permissions.PERMISSIONS.ID))
                .where(DomainsRolesPermissions.DOMAINS_ROLES_PERMISSIONS.DOMAIN_ID.eq(domainId))
                .orderBy(Permissions.PERMISSIONS.ID)
                .limit(size)
                .offset(offset)
                .fetch().map(mapper ->
                        new PermissionDto(
                                mapper.get(Permissions.PERMISSIONS.ID),
                                mapper.get(Permissions.PERMISSIONS.NAME),
                                mapper.get(Permissions.PERMISSIONS.DISABLE))
                );

        return new PaginationResponse<>(i, permissions);
    }
}
