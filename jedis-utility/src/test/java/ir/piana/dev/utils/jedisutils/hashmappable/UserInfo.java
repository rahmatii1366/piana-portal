package ir.piana.dev.utils.jedisutils.hashmappable;

import ir.piana.dev.utils.jedisutils.RedisHashMappable;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author m.rahmati on 12/29/2024
 */
public class UserInfo implements RedisHashMappable {
    private String token;
    private long userId;
    private long nationalCode;
    private String username;
    private String firstName;
    private String lastName;
    private int serviceCode;
    private String userProperties;
    private String lastLogin;

    public UserInfo() {
    }

    public UserInfo(
            String token, long userId, long nationalCode,
            String username, String firstName, String lastName,
            int serviceCode, String userProperties, String lastLogin) {
        this.token = token;
        this.userId = userId;
        this.nationalCode = nationalCode;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.serviceCode = serviceCode;
        this.userProperties = userProperties;
        this.lastLogin = lastLogin;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getNationalCode() {
        return nationalCode;
    }

    public void setNationalCode(long nationalCode) {
        this.nationalCode = nationalCode;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(int serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getUserProperties() {
        return userProperties;
    }

    public void setUserProperties(String userProperties) {
        this.userProperties = userProperties;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }

    public boolean isMultiLogin() {
        return false;
/*        return userProperties != null &&
                !userProperties.isEmpty() &&
                ((ByteUtility.hexStringToByteArray(userProperties)[0] & (1 << 7)) > 0);
    */
    }

    @Override
    public Map<String, String> toRedisHashMap() {
        return new LinkedHashMap<>() {{
            put("token", token);
            put("userId", String.valueOf(userId));
            put("nationalCode", String.valueOf(nationalCode));
            put("username", username);
            put("firstName", firstName);
            put("lastName", lastName);
            put("serviceCode", String.valueOf(serviceCode));
            put("userProperties", userProperties);
            put("lastLogin", lastLogin);
        }};
    }

    @Override
    public void reloadFromRedisHashMap(Map<String, String> redisHashMap) {
        UserInfo other = fromRedisHashMap(redisHashMap);
        this.token = other.token;
        this.userId = other.userId;
        this.nationalCode = other.nationalCode;
        this.username = other.username;
        this.firstName = other.firstName;
        this.lastName = other.lastName;
        this.serviceCode = other.serviceCode;
        this.userProperties = other.userProperties;
        this.lastLogin = other.lastLogin;
    }

    @Override
    public List<String> redisKeys() {
        return Arrays.asList(
                "token",
                "userId",
                "nationalCode",
                "username",
                "firstName",
                "lastName",
                "serviceCode",
                "userProperties",
                "lastLogin"
        );
    }

    @Override
    public String redisHashKey(String... hashKeys) {
        if (hashKeys != null && hashKeys.length > 0) {
            return "userInfo." + String.join(".", hashKeys);
        }
        return "userInfo." + token;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof UserInfo))
            return false;
        UserInfo other = (UserInfo) obj;
        return other.token.equals(this.token) &&
                other.userId == this.userId &&
                other.nationalCode == this.nationalCode &&
                other.username.equals(this.username) &&
                other.firstName.equals(this.firstName) &&
                other.lastName.equals(this.lastName) &&
                other.serviceCode == this.serviceCode &&
                other.userProperties.equals(this.userProperties) &&
                other.lastLogin.equals(this.lastLogin);

    }

    public static UserInfo fromRedisHashMap(Map<String, String> hashMap) {
        return new UserInfo(
                hashMap.get("token"),
                Long.parseLong(hashMap.get("userId")),
                Long.parseLong(hashMap.get("nationalCode")),
                hashMap.get("username"),
                hashMap.get("firstName"),
                hashMap.get("lastName"),
                Integer.parseInt(hashMap.get("serviceCode")),
                hashMap.get("userProperties"),
                hashMap.get("lastLogin")
        );
    }
}
