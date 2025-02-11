package ir.piana.dev.openidc.data.service;

import ir.piana.dev.openidc.core.dto.ApiResponse;
import ir.piana.dev.openidc.core.dto.PaginationResponse;
import ir.piana.dev.openidc.core.service.role.dto.RoleDto;
import ir.piana.dev.openidc.core.service.role.RoleService;
import ir.piana.dev.openidc.core.service.role.dto.RolePerUserOnDomainRequestDto;
import ir.piana.dev.openidc.core.service.role.dto.RolesForDomainDto;
import ir.piana.dev.openidc.core.service.role.dto.RolesForDomainRequestDto;
import ir.piana.dev.openidc.data.tables.DomainsRolesPermissions;
import ir.piana.dev.openidc.data.tables.Roles;
import ir.piana.dev.openidc.data.tables.daos.RolesDao;
import ir.piana.dev.openidc.data.tables.pojos.RolesEntity;
import lombok.AllArgsConstructor;
import org.jooq.Record;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@AllArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RolesDao rolesDao;
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm:ss");

    @Override
    public PaginationResponse<RoleDto> roles(int offset, int size) {
        Integer count = rolesDao.ctx().selectCount().from(Roles.ROLES)
                .fetchOne(0, Integer.class);

        List<RoleDto> roles = rolesDao.ctx().select()
                .from(Roles.ROLES)
                .orderBy(Roles.ROLES.ID)
                .limit(size)
                .offset(offset)
                .fetch().stream().map(record ->
                        new RoleDto(record.get(Roles.ROLES.ID),
                                record.get(Roles.ROLES.NAME),
                                record.get(Roles.ROLES.DESCRIPTION)/*,
                                PersianDateTime.fromGregorian(
                                        record.get(Roles.ROLES.CREATE_ON)).format(dateTimeFormatter)*/)
                ).toList();
        return new PaginationResponse<>(count, roles);
    }

    @Override
    public PaginationResponse<RoleDto> roles(RolePerUserOnDomainRequestDto dto) {
        SelectConditionStep<Record> search = rolesDao.ctx().select()
                .from(Roles.ROLES)
                .where(Roles.ROLES.NAME.like(dto.getName() + "%"));

        List<RoleDto> roles = search
                .orderBy(Roles.ROLES.ID)
                .limit(dto.getSize())
                .offset(dto.getOffset())
                .fetch().stream().map(record ->
                        new RoleDto(record.get(Roles.ROLES.ID),
                                record.get(Roles.ROLES.NAME),
                                record.get(Roles.ROLES.DESCRIPTION)/*,
                                PersianDateTime.fromGregorian(
                                        record.get(Roles.ROLES.CREATE_ON)).format(dateTimeFormatter)*/)
                ).toList();

        Integer count = rolesDao.ctx().selectCount().from(search)
                .fetchOne(0, Integer.class);
        return new PaginationResponse<>(count, roles);
    }

    /*@Override
    public PaginationResponse<RolesForDomainDto> rolesForDomain(RolesForDomainRequestDto dto) {
        Table<Record2<Long, Long>> drp = rolesDao.ctx().selectDistinct(
                        DomainsRolesPermissions.DOMAINS_ROLES_PERMISSIONS.ROLE_ID,
                        DomainsRolesPermissions.DOMAINS_ROLES_PERMISSIONS.DOMAIN_ID)
                .from(DomainsRolesPermissions.DOMAINS_ROLES_PERMISSIONS)
                .where(DomainsRolesPermissions.DOMAINS_ROLES_PERMISSIONS.DOMAIN_ID
                        .eq(dto.getDomainId())).asTable("drp");

        SelectConditionStep<Record4<Long, String, String, Object>> search = rolesDao.ctx()
                .select(Roles.ROLES.ID, Roles.ROLES.NAME, Roles.ROLES.DESCRIPTION,
                        DSL.field("DOMAIN_ID", drp.field("domain_id")))
                .from(Roles.ROLES).leftOuterJoin(drp)
                .on(Roles.ROLES.ID.eq(drp.field("role_id", DomainsRolesPermissions.DOMAINS_ROLES_PERMISSIONS.DOMAIN_ID.getDataType())))
                .where(Roles.ROLES.NAME.like((dto.getName() == null ? "" : dto.getName()) + "%"));

        if (dto.getMemberOnly() != null) {
            if (!dto.getMemberOnly())
                search.and(drp.field("domain_id",
                        DomainsRolesPermissions.DOMAINS_ROLES_PERMISSIONS.DOMAIN_ID.getDataType()).isNull());
            else
                search.and(drp.field(
                        "domain_id",
                        DomainsRolesPermissions.DOMAINS_ROLES_PERMISSIONS.DOMAIN_ID.getDataType()).isNotNull());

        }

        List<RolesForDomainDto> roles = search
                .orderBy(Roles.ROLES.ID)
                .limit(dto.getSize())
                .offset(dto.getOffset())
                .fetch().stream().map(record ->
                        new RolesForDomainDto(record.get(Roles.ROLES.ID),
                                record.get(Roles.ROLES.NAME),
                                record.get(Roles.ROLES.DESCRIPTION),
                                record.get(DSL.field("DOMAIN_ID",
                                        DomainsRolesPermissions.DOMAINS_ROLES_PERMISSIONS.DOMAIN_ID.getDataType())) != null)
                ).toList();

        Integer count = rolesDao.ctx().selectCount().from(search)
                .fetchOne(0, Integer.class);
        return new PaginationResponse<>(count, roles);
    }*/


    @Override
    public PaginationResponse<RolesForDomainDto> rolesForDomain(RolesForDomainRequestDto dto) {
        Table<Record2<Long, Long>> drp = rolesDao.ctx().selectDistinct(
                        DomainsRolesPermissions.DOMAINS_ROLES_PERMISSIONS.ROLE_ID,
                        DomainsRolesPermissions.DOMAINS_ROLES_PERMISSIONS.DOMAIN_ID.as("domainId"))
                .from(DomainsRolesPermissions.DOMAINS_ROLES_PERMISSIONS)
                .where(DomainsRolesPermissions.DOMAINS_ROLES_PERMISSIONS.DOMAIN_ID
                        .eq(dto.getDomainId())).asTable("drp");

        SelectConditionStep<? extends Record4<Long, String, String, ?>> search = rolesDao.ctx()
                .select(Roles.ROLES.ID, Roles.ROLES.NAME, Roles.ROLES.DESCRIPTION, drp.field("domainId"))
                .from(Roles.ROLES).leftOuterJoin(drp)
                .on(Roles.ROLES.ID.eq(drp.field("role_id", DomainsRolesPermissions.DOMAINS_ROLES_PERMISSIONS.DOMAIN_ID.getDataType())))
                .where(drp.field("domainId").isNull()
                        .or(drp.field("domainId", Long.class).eq(dto.getDomainId())))
                .and(Roles.ROLES.NAME.like((dto.getName() == null ? "" : dto.getName()) + "%"));

        if (dto.getMemberOnly() != null) {
            if (!dto.getMemberOnly())
                search.and(drp.field("domainId").isNull());
            else
                search.and(drp.field(
                        "domainId").isNotNull());

        }

        List<RolesForDomainDto> roles = search
                .orderBy(Roles.ROLES.ID)
                .limit(dto.getSize())
                .offset(dto.getOffset())
                .fetch().stream().map(record ->
                        new RolesForDomainDto(record.get(Roles.ROLES.ID),
                                record.get(Roles.ROLES.NAME),
                                record.get(Roles.ROLES.DESCRIPTION),
                                record.get(drp.field("domainId")) != null)
                ).toList();

        Integer count = rolesDao.ctx().selectCount().from(search)
                .fetchOne(0, Integer.class);
        return new PaginationResponse<>(count, roles);
    }

    @Override
    public ApiResponse<?> create(String name, String description) {
        RolesEntity role = new RolesEntity();
        role.setName(name);
        role.setDescription(description);
        rolesDao.insert(role);
        return new ApiResponse<>("success", "ok");
    }
}
