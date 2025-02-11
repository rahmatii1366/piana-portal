package ir.piana.dev.openidc.core.filter;

import org.springframework.security.core.GrantedAuthority;

public final class SimpleGrantedAuthority implements GrantedAuthority {
    private static final long serialVersionUID = 620L;
    private final String authority;

    public SimpleGrantedAuthority(String authority) {
        //ToDo
        //Assert.hasText(role, "A granted authority textual representation is required");
        this.authority = authority;
    }

    @Override
    public String getAuthority() {
        return this.authority;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof org.springframework.security.core.authority.SimpleGrantedAuthority) {
            org.springframework.security.core.authority.SimpleGrantedAuthority sga = (org.springframework.security.core.authority.SimpleGrantedAuthority)obj;
            return this.authority.equals(sga.getAuthority());
        } else if (obj instanceof String) {
            return ((String) obj).equalsIgnoreCase(this.authority);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return this.authority.hashCode();
    }

    public String toString() {
        return this.authority;
    }
}
