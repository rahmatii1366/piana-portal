package ir.piana.dev.openidc.data.service;

import ir.piana.boot.utils.errorprocessor.ApiException;
import ir.piana.boot.utils.errorprocessor.AuthenticationFailedTypes;
import ir.piana.dev.openidc.core.dto.ApiResponse;
import ir.piana.dev.openidc.core.filter.SimpleGrantedAuthority;
import ir.piana.dev.openidc.core.service.TokenManagementService;
import ir.piana.dev.openidc.core.service.auth.AuthService;
import ir.piana.dev.openidc.core.service.auth.dto.AuthDto;
import ir.piana.dev.openidc.core.service.auth.redisobj.LoginIpsModel;
import ir.piana.dev.openidc.core.service.auth.redisobj.TokenRelatedModel;
import ir.piana.dev.openidc.core.service.auth.redisobj.UserEntranceModel;
import ir.piana.dev.openidc.core.service.domain.DomainService;
import ir.piana.dev.openidc.core.service.user.dto.UserDetails;
import ir.piana.dev.openidc.core.types.LeakyBucketConstants;
import ir.piana.dev.openidc.data.tables.DomainsRolesPermissions;
import ir.piana.dev.openidc.data.tables.PasswordCredential;
import ir.piana.dev.openidc.data.tables.Permissions;
import ir.piana.dev.openidc.data.tables.UserEntrance;
import ir.piana.dev.openidc.data.tables.daos.*;
import ir.piana.dev.openidc.data.tables.pojos.*;
import ir.piana.dev.utils.jedisutils.JedisPool;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private static Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final OidcUsersDao usersDao;
    private final PasswordCredentialDao passwordCredentialDao;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtTokenServiceImpl jwtTokenServiceImpl;
    private final UsersDomainsRolesDao usersDomainsRolesDao;
    private final DomainsRolesPermissionsDao domainsRolesPermissionsDao;
    private final UserEntranceDao userEntranceDao;
    private final ConnectivityChannelDao connectivityChannelDao;
    private final DomainService domainService;
    private final JedisPool jedisPool;
    private final TokenManagementService tokenManagementService;


    @Override
    public UserDetails byUsernameAndToken(String username, String token) {
        OidcUsersEntity user = usersDao.fetchOneByUsername(username);
        List<UsersDomainsRolesEntity> usersDomainsRolesEntities = usersDomainsRolesDao.fetchByUserId(
                user.getId());
        Set<String> permissions = new HashSet<>();
        Set<SimpleGrantedAuthority> collect = usersDomainsRolesEntities.stream().map(entity ->
                domainsRolesPermissionsDao.ctx()
                        .select(Permissions.PERMISSIONS.NAME)
                        .from(DomainsRolesPermissions.DOMAINS_ROLES_PERMISSIONS)
                        .innerJoin(Permissions.PERMISSIONS)
                        .on(DomainsRolesPermissions.DOMAINS_ROLES_PERMISSIONS.PERMISSION_ID.eq(Permissions.PERMISSIONS.ID))
                        .where(DomainsRolesPermissions.DOMAINS_ROLES_PERMISSIONS.DOMAIN_ID.eq(entity.getDomainId()))
                        .and(DomainsRolesPermissions.DOMAINS_ROLES_PERMISSIONS.ROLE_ID.eq(entity.getRoleId()))
                        .fetch(Permissions.PERMISSIONS.NAME)
        ).flatMap(List::stream).map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
        return new UserDetails(user.getUsername(), user.getId(), token, collect);
    }

    @Override
    public ApiResponse<AuthDto> loginWithPasswordCredential(
            String username, String password,
            String connectivityChannel, long domainId, String ip) {
        UserEntranceModel userEntranceModel = checkLoginLimitThresholdBasedUser(
                username, LeakyBucketConstants.TOKEN_LOGIN_WEIGHT);

        OidcUsersEntity userEntity = usersDao.fetchOneByUsername(username);
        if (userEntity != null) {
            PasswordCredentialEntity passwordCredentialEntity = passwordCredentialDao.fetchOne(
                    PasswordCredential.PASSWORD_CREDENTIAL.USER_ID, userEntity.getId());

            if (Objects.isNull(passwordCredentialEntity) ||
                    !bCryptPasswordEncoder.matches(password, passwordCredentialEntity.getPassword())) {
                userEntranceModel.incrementFailedTryCount(jedisPool);
                throw AuthenticationFailedTypes.UsernameOrPasswordIsIncorrect.newException();
            }

            checkAllowedLoginBasedOnChannelAndIp(username, userEntity.getMultiLoginEnabled(),
                    connectivityChannel, domainId, ip);

            /*String jwtToken = jwtTokenServiceImpl.generateToken(
                    userEntity.getUsername(), 20000);*/
            String token = UUID.randomUUID().toString();

            userEntranceModel.addToken(jedisPool, token);

            List<Long> uiPermissions = domainService.userDomainPermissionsByType(
                    userEntity.getId(), domainId, "UI");

            TokenRelatedModel tokenRelatedModel = new TokenRelatedModel(
                    token, username, userEntity.getId(),
                    connectivityChannel, domainId, ip, System.currentTimeMillis(),
                    uiPermissions
            );

            jedisPool.setRedisHashMappable(tokenRelatedModel);

            return ApiResponse.<AuthDto>builder()
                    .code("login.success")
                    .message("login.success")
                    .data(new AuthDto(
                            token,
                            passwordCredentialEntity.getShouldBeChange(),
                            userEntity.getUsername(),
                            uiPermissions))
                    .build();
        }
        throw AuthenticationFailedTypes.UsernameOrPasswordIsIncorrect.newException();
    }

    @Transactional
    public ApiResponse<?> changePassword(String password, String newPassword) {
        if (password.equalsIgnoreCase(newPassword)) {
            throw AuthenticationFailedTypes.NewPasswordIsEqualToOldPassword.newException();
        }
        UserDetails userDetails = (UserDetails)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        OidcUsersEntity user = usersDao.fetchOneByUsername(userDetails.username());
        if (user != null) {
            PasswordCredentialEntity passwordCredentialEntity = passwordCredentialDao.fetchOne(
                    PasswordCredential.PASSWORD_CREDENTIAL.USER_ID, user.getId());
            if (Objects.nonNull(passwordCredentialEntity) &&
                    bCryptPasswordEncoder.matches(password, passwordCredentialEntity.getPassword())) {
                String encodedNewPassword = bCryptPasswordEncoder.encode(newPassword);

                passwordCredentialEntity.setPassword(encodedNewPassword);
                passwordCredentialEntity.setShouldBeChange(false);
                passwordCredentialDao.update(passwordCredentialEntity);

                return ApiResponse.builder()
                        .code("change-password.success")
                        .message("change-password.success")
                        .build();
            }
        }
        throw AuthenticationFailedTypes.UsernameOrPasswordIsIncorrect.newException();
    }

    @Override
    public ApiResponse<AuthDto> refresh(String token) {
        UserDetails userDetails = (UserDetails)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        PasswordCredentialEntity passwordCredentialEntity = passwordCredentialDao.fetchOne(
                PasswordCredential.PASSWORD_CREDENTIAL.USER_ID, userDetails.id());
        TokenRelatedModel redisHashMappable = jedisPool.getRedisHashMappable(TokenRelatedModel.class, token);

        return ApiResponse.<AuthDto>builder()
                .code("refresh.success")
                .message("refresh.success")
                .data(new AuthDto(
                        token,
                        passwordCredentialEntity.getShouldBeChange(),
                        userDetails.username(),
                        redisHashMappable == null ? new ArrayList<>() : redisHashMappable.getUiPermissions()))
                .build();
    }

    public ApiResponse<Boolean> hasPermission(String perm) {
        UserDetails userDetails = (UserDetails)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ApiResponse.<Boolean>builder()
                .data(userDetails.authorities().stream()
                        .anyMatch(authority -> authority.equals(perm)))
                .build();
    }

    private synchronized UserEntranceModel checkLoginLimitThresholdBasedUser(String username, int weight) {
        try {
            UserEntranceModel userEntrance = jedisPool.getRedisHashMappable(UserEntranceModel.class, username);
            if (userEntrance != null) {
                boolean entrance = userEntrance.entrance(jedisPool, weight);
//                jedisPool.setRedisHashMappable(userEntrance);
                if (!entrance) {
                    throw AuthenticationFailedTypes.MaximumNumberOfLoginInHourExceeded.newException();
                }
                checkFailedTryCount(userEntrance);
                return userEntrance;
            }

            UserEntranceEntity userEntranceEntity = userEntranceDao.fetchOneByUsername(username);
            if (userEntranceEntity == null) {
                userEntranceEntity = new UserEntranceEntity(
                        username, 0,
                        LeakyBucketConstants.LOGIN_LEAK_RATE, LeakyBucketConstants.LOGIN_BUCKET_VOLUME,
                        null
                );
                userEntranceDao.insert(userEntranceEntity);
            }
//                throw AuthenticationFailedTypes.MaximumNumberOfLoginInHourExceeded.newException();

            userEntrance = (userEntranceEntity.getVolume() == 0 || userEntranceEntity.getRate() == 0) ?
                    new UserEntranceModel(
                            username,
                            userEntranceEntity.getFailedTryCount(),
                            userEntranceEntity.getFailedTryCount() == 3,
                            LeakyBucketConstants.LOGIN_BUCKET_VOLUME,
                            LeakyBucketConstants.LOGIN_LEAK_RATE) :
                    new UserEntranceModel(
                            username,
                            userEntranceEntity.getFailedTryCount(),
                            userEntranceEntity.getFailedTryCount() == 3,
                            userEntranceEntity.getVolume(),
                            userEntranceEntity.getRate());

            jedisPool.setRedisHashMappable(userEntrance);
            checkFailedTryCount(userEntrance);
            return userEntrance;
        } catch (Exception e) {
            if (e instanceof ApiException)
                throw e;
//            log.error("UserName:{}, Weight: {}, Message: {}", username, weight, e.getMessage());
            throw AuthenticationFailedTypes.UnknownError.newException();
        }
    }

    private void checkFailedTryCount(UserEntranceModel userEntrance) {
        if (userEntrance.getFailedTryCount() >= 3) {
            if (!userEntrance.isFailedTryCountUpstream()) {
                userEntranceDao.ctx().update(UserEntrance.USER_ENTRANCE)
                        .set(UserEntrance.USER_ENTRANCE.FAILED_TRY_COUNT, 3)
                        .where(UserEntrance.USER_ENTRANCE.USERNAME.eq(userEntrance.getUsername())).execute();
                userEntrance.sendFailedTryCountUpstream(jedisPool);
            }
            throw AuthenticationFailedTypes.MaximumNumberOfFailedCredential.newException();
        }
    }

    private synchronized void checkAllowedLoginBasedOnChannelAndIp(
            String username, boolean multiLoginEnabled,
            String connectivityChannel, long domainId, String ipAddress) {
        ConnectivityChannelEntity connectivityChannelEntity = connectivityChannelDao.fetchOneByName(connectivityChannel);
        if (connectivityChannelEntity == null)
            throw AuthenticationFailedTypes.ChannelUnknown.newException();

        LoginIpsModel loginIpsModel = jedisPool.getRedisHashMappable(
                LoginIpsModel.class, username, connectivityChannel, String.valueOf(domainId));
        if (loginIpsModel == null) {
            loginIpsModel = new LoginIpsModel(username, connectivityChannel, domainId, new ArrayList<>() {{
                add(ipAddress);
            }});
            jedisPool.setRedisHashMappable(loginIpsModel);
            return;
        }

        if (!multiLoginEnabled) {
            if (loginIpsModel.getIps().size() > 1 || (
                    loginIpsModel.getIps().size() == 1 &&
                            loginIpsModel.getIps().getFirst() != null &&
                            !loginIpsModel.getIps().getFirst().equalsIgnoreCase(ipAddress))) {
                log.error("already logged in => username: {}, channel: {}, domainId: {}, ip: {}",
                        username, connectivityChannel, domainId, ipAddress);
                throw AuthenticationFailedTypes.MaximumNumberOfLoginInHourExceeded.newException();
            }
        } else {
            loginIpsModel.addIp(jedisPool, ipAddress);
        }
    }
}
