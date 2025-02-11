package ir.piana.dev.openidc.data.service;

import ir.piana.dev.openidc.core.dto.*;
import ir.piana.dev.openidc.core.service.permission.dto.PermissionDto;
import ir.piana.dev.openidc.core.service.user.UserService;
import ir.piana.dev.openidc.core.service.user.dto.CreateUserRequestDto;
import ir.piana.dev.openidc.core.service.user.dto.UserDto;
import ir.piana.dev.openidc.core.service.user.dto.UserPerDomainRequestDto;
import ir.piana.dev.openidc.data.tables.DomainsRolesPermissions;
import ir.piana.dev.openidc.data.tables.OidcUsers;
import ir.piana.dev.openidc.data.tables.Permissions;
import ir.piana.dev.openidc.data.tables.UsersDomainsRoles;
import ir.piana.dev.openidc.data.tables.daos.OidcUsersDao;
import ir.piana.dev.openidc.data.tables.daos.PasswordCredentialDao;
import ir.piana.dev.openidc.data.tables.pojos.OidcUsersEntity;
import ir.piana.dev.openidc.data.tables.pojos.PasswordCredentialEntity;
import lombok.AllArgsConstructor;
import org.jooq.Record2;
import org.jooq.Record5;
import org.jooq.SelectConditionStep;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final BCryptPasswordEncoder passwordEncoder;
    private final OidcUsersDao usersDao;
    private final PasswordCredentialDao passwordCredentialDao;
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm:ss");

    @Override
    public PaginationResponse<UserDto> users(int offset, int size) {
        Integer count = usersDao.ctx().selectCount().from(OidcUsers.OIDC_USERS)
                .fetchOne(0, Integer.class);

        List<UserDto> roles = usersDao.ctx().select()
                .from(OidcUsers.OIDC_USERS)
                .orderBy(OidcUsers.OIDC_USERS.ID)
                .limit(size)
                .offset(offset)
                .fetch().stream().map(record ->
                        new UserDto(record.get(OidcUsers.OIDC_USERS.ID),
                                record.get(OidcUsers.OIDC_USERS.FIRST_NAME),
                                record.get(OidcUsers.OIDC_USERS.LAST_NAME),
                                record.get(OidcUsers.OIDC_USERS.USERNAME))
                ).toList();
        return new PaginationResponse<>(count, roles);
    }

    @Override
    public PaginationResponse<UserDto> usersPerDomain(UserPerDomainRequestDto userPerDomainRequestDto) {
        Table<Record2<Long, Long>> udr = usersDao.ctx().selectDistinct(
                        UsersDomainsRoles.USERS_DOMAINS_ROLES.USER_ID,
                        UsersDomainsRoles.USERS_DOMAINS_ROLES.DOMAIN_ID)
                .from(UsersDomainsRoles.USERS_DOMAINS_ROLES)
                .where(UsersDomainsRoles.USERS_DOMAINS_ROLES.DOMAIN_ID
                        .eq(userPerDomainRequestDto.getDomainId())).asTable("udr");

        SelectConditionStep<Record5<Long, String, String, String, Object>> search = usersDao.ctx()
                .selectDistinct(OidcUsers.OIDC_USERS.ID, OidcUsers.OIDC_USERS.FIRST_NAME,
                        OidcUsers.OIDC_USERS.LAST_NAME, OidcUsers.OIDC_USERS.USERNAME,
                        DSL.field("DOMAIN_ID", udr.field("domain_id")))
                .from(OidcUsers.OIDC_USERS)
                .leftJoin(udr)
                .on(OidcUsers.OIDC_USERS.ID.eq(udr.field("user_id", UsersDomainsRoles.USERS_DOMAINS_ROLES.USER_ID.getDataType())))
                .where(OidcUsers.OIDC_USERS.FIRST_NAME.like(userPerDomainRequestDto.getFirstName() + "%"))
                .and(OidcUsers.OIDC_USERS.LAST_NAME.like(userPerDomainRequestDto.getLastName() + "%"))
                .and(OidcUsers.OIDC_USERS.USERNAME.like(userPerDomainRequestDto.getUsername() + "%"));

        if (userPerDomainRequestDto.isNoMemberOnly())
                    search.and(udr.field(
                            "domain_id", UsersDomainsRoles.USERS_DOMAINS_ROLES.DOMAIN_ID.getDataType()).isNull());

        /*if (userPerDomainRequestDto.isNoMemberOnly())
                    search.and(OidcUsers.OIDC_USERS.ID.notIn(udr.field(udr.field(
                            "user_id", UsersDomainsRoles.USERS_DOMAINS_ROLES.USER_ID.getDataType()))));*/

        /*SelectConditionStep<Record5<Long, String, String, String, Object>> target = usersDao.ctx()
                .selectDistinct(OidcUsers.OIDC_USERS.ID, OidcUsers.OIDC_USERS.FIRST_NAME,
                        OidcUsers.OIDC_USERS.LAST_NAME, OidcUsers.OIDC_USERS.USERNAME,
                        DSL.field("DOMAIN_ID", udr.field("domain_id")))
                .from(OidcUsers.OIDC_USERS)
                .leftJoin(udr)
                .on(OidcUsers.OIDC_USERS.ID.eq(udr.field("user_id", UsersDomainsRoles.USERS_DOMAINS_ROLES.USER_ID.getDataType())))
                .where(OidcUsers.OIDC_USERS.FIRST_NAME.like(userPerDomainRequestDto.getFirstName() + "%"))
                .and(OidcUsers.OIDC_USERS.LAST_NAME.like(userPerDomainRequestDto.getLastName() + "%"))
                .and(OidcUsers.OIDC_USERS.USERNAME.like(userPerDomainRequestDto.getUsername() + "%"));*/

        List<UserDto> roles = search.orderBy(OidcUsers.OIDC_USERS.ID)
                .limit(userPerDomainRequestDto.getSize())
                .offset(userPerDomainRequestDto.getOffset())
                .fetch().stream().map(record ->
                        new UserDto(record.get(OidcUsers.OIDC_USERS.ID),
                                record.get(OidcUsers.OIDC_USERS.FIRST_NAME),
                                record.get(OidcUsers.OIDC_USERS.LAST_NAME),
                                record.get(OidcUsers.OIDC_USERS.USERNAME),
                                record.get(DSL.field("DOMAIN_ID", UsersDomainsRoles.USERS_DOMAINS_ROLES.DOMAIN_ID.getDataType())))
                ).toList();

        Integer count = usersDao.ctx().selectCount().from(search)
                .fetchOne(0, Integer.class);
        return new PaginationResponse<>(count, roles);
    }

    @Override
    public ApiResponse<?> create(
            CreateUserRequestDto dto) {
        OidcUsersEntity entity = new OidcUsersEntity();
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setUsername(dto.getUsername());
        usersDao.insert(entity);

        entity = usersDao.fetchOneByUsername(entity.getUsername());

        PasswordCredentialEntity passwordCredential = new PasswordCredentialEntity();
        passwordCredential.setUserId(entity.getId());
        passwordCredential.setPassword(passwordEncoder.encode(dto.getPassword()));
        passwordCredential.setShouldBeChange(true);
        passwordCredentialDao.insert(passwordCredential);

        return new ApiResponse<>("success", "ok");
    }

    @Override
    public PaginationResponse<PermissionDto> userPermissions(
            Long userId, Long domainId, Long roleId, int size, int offset) {
        Integer i = usersDao.ctx()
                .selectCount()
                .from(usersDao.ctx().select().from(UsersDomainsRoles.USERS_DOMAINS_ROLES)
                        .innerJoin(DomainsRolesPermissions.DOMAINS_ROLES_PERMISSIONS)
                        .on(UsersDomainsRoles.USERS_DOMAINS_ROLES.DOMAIN_ID.eq(DomainsRolesPermissions.DOMAINS_ROLES_PERMISSIONS.DOMAIN_ID))
                        .and(UsersDomainsRoles.USERS_DOMAINS_ROLES.ROLE_ID.eq(DomainsRolesPermissions.DOMAINS_ROLES_PERMISSIONS.ROLE_ID))
                        .innerJoin(Permissions.PERMISSIONS)
                        .on(DomainsRolesPermissions.DOMAINS_ROLES_PERMISSIONS.PERMISSION_ID.eq(Permissions.PERMISSIONS.ID))
                        .where(UsersDomainsRoles.USERS_DOMAINS_ROLES.USER_ID.eq(userId)))
                .fetchOne(0, Integer.class);

        List<PermissionDto> permissions = usersDao.ctx()
                .select(Permissions.PERMISSIONS.ID, Permissions.PERMISSIONS.NAME, Permissions.PERMISSIONS.DISABLE)
                .from(UsersDomainsRoles.USERS_DOMAINS_ROLES)
                .innerJoin(DomainsRolesPermissions.DOMAINS_ROLES_PERMISSIONS)
                .on(UsersDomainsRoles.USERS_DOMAINS_ROLES.DOMAIN_ID.eq(DomainsRolesPermissions.DOMAINS_ROLES_PERMISSIONS.DOMAIN_ID))
                .and(UsersDomainsRoles.USERS_DOMAINS_ROLES.ROLE_ID.eq(DomainsRolesPermissions.DOMAINS_ROLES_PERMISSIONS.ROLE_ID))
                .innerJoin(Permissions.PERMISSIONS)
                .on(DomainsRolesPermissions.DOMAINS_ROLES_PERMISSIONS.PERMISSION_ID.eq(Permissions.PERMISSIONS.ID))
                .where(UsersDomainsRoles.USERS_DOMAINS_ROLES.USER_ID.eq(userId))
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
