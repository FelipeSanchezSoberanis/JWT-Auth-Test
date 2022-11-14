package com.felipe.jwtAuthTest.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.felipe.jwtAuthTest.entities.Permission;
import com.felipe.jwtAuthTest.entities.Role;
import com.felipe.jwtAuthTest.entities.User;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserDetailsImpl implements UserDetails {

    private Long id;
    private String username;
    private Collection<? extends GrantedAuthority> authorities;

    @JsonIgnore
    private String password;

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

    public static UserDetailsImpl buildFromUser(User user) {
        UserDetailsImpl userDetails = new UserDetailsImpl();

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        List<Role> userRoles = user.getRoles();
        List<Permission> userPermissions = new ArrayList<>();

        userRoles.forEach(role -> userPermissions.addAll(role.getPermissions()));

        authorities.addAll(
            userRoles
                .stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .collect(Collectors.toList())
        );

        authorities.addAll(
            userPermissions
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getName()))
                .collect(Collectors.toList())
        );

        userDetails.setId(user.getId());
        userDetails.setUsername(user.getUsername());
        userDetails.setPassword(user.getPassword());
        userDetails.setAuthorities(authorities);

        return userDetails;
    }
}
