package ir.piana.dev.openidc.data.service;

import ir.piana.dev.openidc.core.dto.*;
import ir.piana.dev.openidc.core.service.udr.UsersDomainsRolesService;
import ir.piana.dev.openidc.core.service.domain.dto.DomainDto;
import ir.piana.dev.openidc.core.service.role.dto.RoleDto;
import ir.piana.dev.openidc.core.service.udr.dto.SimpleDomainAndItsRolesDto;
import ir.piana.dev.openidc.core.service.udr.dto.SimpleRoleDto;
import ir.piana.dev.openidc.core.service.udr.dto.UserDomainsAndDomainRolesResponseDto;
import ir.piana.dev.openidc.core.service.udr.dto.UsersByRolesAddToDomainRequestDto;
import ir.piana.dev.openidc.data.tables.Domains;
import ir.piana.dev.openidc.data.tables.DomainsUsers;
import ir.piana.dev.openidc.data.tables.Roles;
import ir.piana.dev.openidc.data.tables.UsersDomainsRoles;
import ir.piana.dev.openidc.data.tables.daos.DomainsUsersDao;
import ir.piana.dev.openidc.data.tables.daos.UsersDomainsRolesDao;
import ir.piana.dev.openidc.data.tables.pojos.DomainsUsersEntity;
import ir.piana.dev.openidc.data.tables.pojos.UsersDomainsRolesEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class UsersDomainsRolesServiceImpl implements UsersDomainsRolesService {
    private final UsersDomainsRolesDao dao;
    private final DomainsUsersDao domainsUsersDao;

    public UsersDomainsRolesServiceImpl(
            UsersDomainsRolesDao dao,
            DomainsUsersDao domainsUsersDao) {
        this.dao = dao;
        this.domainsUsersDao = domainsUsersDao;
    }

    @Transactional
    @Override
    public ApiResponse<?> addUsersByRolesIntoDomain(UsersByRolesAddToDomainRequestDto dto) {
        List<DomainsUsersEntity> usersDomainsList = dto.getUserIds().stream()
                .map(userId -> new DomainsUsersEntity(
                userId, dto.getDomainId(), null
        )).toList();
        domainsUsersDao.insert(usersDomainsList);
        List<UsersDomainsRolesEntity> entities = dto.getUserIds().stream().map(
                userId -> {
                    List<UsersDomainsRolesEntity> list = dto.getRoleIds().stream().map(roleId -> new UsersDomainsRolesEntity(
                            userId, dto.getDomainId(), roleId, LocalDateTime.now()
                    )).toList();
                    return list;
                }
        ).flatMap(List::stream).toList();
        dao.insert(entities);
        return new ApiResponse<>("success", "success");
    }

    public ApiResponse<UserDomainsAndDomainRolesResponseDto> getUserDomainsAndDomainRoles(long userId) {
        /*List<DomainDto> userDomains = dao.ctx().selectDistinct(Domains.DOMAINS.ID, Domains.DOMAINS.NAME)
                .from(Domains.DOMAINS).innerJoin(UsersDomainsRoles.USERS_DOMAINS_ROLES)
                .on(Domains.DOMAINS.ID.eq(UsersDomainsRoles.USERS_DOMAINS_ROLES.DOMAIN_ID))
                .where(UsersDomainsRoles.USERS_DOMAINS_ROLES.USER_ID.eq(userId))
                .fetch().map(rec -> new DomainDto(
                        rec.get(Domains.DOMAINS.ID),
                        rec.get(Domains.DOMAINS.NAME))
                );*/

        List<DomainDto> userDomains = dao.ctx().selectDistinct(Domains.DOMAINS.ID, Domains.DOMAINS.NAME)
                .from(Domains.DOMAINS).innerJoin(DomainsUsers.DOMAINS_USERS)
                .on(Domains.DOMAINS.ID.eq(DomainsUsers.DOMAINS_USERS.DOMAIN_ID))
                .where(DomainsUsers.DOMAINS_USERS.USER_ID.eq(userId))
                .fetch().map(rec -> new DomainDto(
                        rec.get(Domains.DOMAINS.ID),
                        rec.get(Domains.DOMAINS.NAME))
                );

        List<SimpleDomainAndItsRolesDto> list = userDomains.stream().map(domainDto -> {
            List<RoleDto> roles = dao.ctx().select(Roles.ROLES.ID, Roles.ROLES.NAME)
                    .from(Roles.ROLES)
                    .innerJoin(UsersDomainsRoles.USERS_DOMAINS_ROLES)
                    .on(Roles.ROLES.ID.eq(UsersDomainsRoles.USERS_DOMAINS_ROLES.ROLE_ID))
                    .where(UsersDomainsRoles.USERS_DOMAINS_ROLES.USER_ID.eq(userId))
                    .and(UsersDomainsRoles.USERS_DOMAINS_ROLES.DOMAIN_ID.eq(domainDto.getId()))
                    .fetch().map(rec -> new RoleDto(
                            rec.get(Roles.ROLES.ID),
                            rec.get(Roles.ROLES.NAME))
                    );

            return new SimpleDomainAndItsRolesDto(domainDto.getId(), domainDto.getName(),
                    roles.stream().map(role -> new SimpleRoleDto(role.getId(), role.getName())).toList());
        }).toList();

        return new ApiResponse<>("success", new UserDomainsAndDomainRolesResponseDto(userId, list));
    }
}
