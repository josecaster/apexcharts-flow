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
import sr.we.data.service.UserRepository;
import sr.we.shekelflowcore.entity.ThisUser;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ThisUser user = userRepository.findByUsername(username, "");
        if (user == null) {
            throw new UsernameNotFoundException("No user present with username: " + username);
        } else {
            return user;
        }
    }

    private static List<GrantedAuthority> getAuthorities(ThisUser user) {
        return user.getRoles().stream().map(role -> new SimpleGrantedAuthority( role.getName()))
                .collect(Collectors.toList());

    }

}
