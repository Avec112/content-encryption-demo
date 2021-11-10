package io.avec.ced.security;

import io.avec.ced.data.entity.Manager;
import io.avec.ced.data.service.ManagerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final ManagerRepository managerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Manager manager = managerRepository.findByUsername(username);
        if (manager == null) {
            throw new UsernameNotFoundException("No manager present with username: " + username);
        } else {
            return new org.springframework.security.core.userdetails.User(manager.getUsername(), manager.getHashedPassword(),
                    getAuthorities(manager));
        }
    }

    private static List<SimpleGrantedAuthority> getAuthorities(Manager manager) {
        return manager.getRoles().stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleName()))
                .toList(); // Java 17
//                .collect(Collectors.toList()); // java < 17

    }

}
