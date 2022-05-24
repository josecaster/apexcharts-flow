package sr.we.security;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sr.we.data.controller.UserService;
import sr.we.shekelflowcore.entity.ThisUser;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserService userService;

    @Autowired
    public UserDetailsServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ThisUser user = userService.authenticate(username, "");
        if (user == null) {
            throw new UsernameNotFoundException("No user present with username: " + username);
        } else {
            return user;
        }
    }

    public static List<GrantedAuthority> getAuthorities(ThisUser user) {
        return user.getRoles().stream().map(role -> new SimpleGrantedAuthority( role.getName()))
                .collect(Collectors.toList());

    }

}
