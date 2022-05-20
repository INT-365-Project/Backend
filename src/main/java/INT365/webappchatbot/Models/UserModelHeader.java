package INT365.webappchatbot.Models;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UserModelHeader implements UserDetails {
    private String username;
    private String password;
    private List<GrantedAuthority> authorities;
    private UserModelDetail userModelDetail;

    public UserModelHeader(String username, String password, List<GrantedAuthority> authorities, UserModelDetail userModelDetail) {
        this.username = username;
        this.password = password;
        this.authorities = authorities;
        this.userModelDetail = userModelDetail;
    }

    public UserModelDetail getUserModelDetail() {
        return userModelDetail;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
