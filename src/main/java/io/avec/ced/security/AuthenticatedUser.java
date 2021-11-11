package io.avec.ced.security;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinServletRequest;
import io.avec.ced.data.entity.Manager;
import io.avec.ced.data.service.ManagerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class AuthenticatedUser {

    private final ManagerRepository managerRepository;
    private final AuthenticationManager authenticationManager;

    private UserDetails getAuthenticatedUser() {
        SecurityContext context = SecurityContextHolder.getContext();
        Object principal = context.getAuthentication().getPrincipal();
        if (principal instanceof UserDetails userDetails) { // Java 17
//        if (principal instanceof UserDetails) { // Java < 17
//            UserDetails userDetails = (UserDetails) context.getAuthentication().getPrincipal();
            return userDetails;
        }
        return null;
    }

    public Optional<Manager> get() {
        UserDetails details = getAuthenticatedUser();
        if (details == null) {
            return Optional.empty();
        }
        return Optional.of(managerRepository.findByUsername(details.getUsername()));
    }

    public void logout() {
        UI.getCurrent().getPage().setLocation(SecurityConfiguration.LOGOUT_URL);
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(VaadinServletRequest.getCurrent().getHttpServletRequest(), null, null);
    }

    public void reauthenticate(String password) {
        final Optional<Manager> optionalManager = get();
        Manager manager = optionalManager.orElseThrow(() -> new IllegalStateException("You must sign in to the application."));
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(manager.getUsername(), password);

        final Authentication authentication = authenticationManager.authenticate(token);
        if(!authentication.isAuthenticated()) {
            throw new BadCredentialsException("Could not authenticate principal " + manager.getUsername() + " with given credentials");
        }

    }

    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && !(authentication instanceof AnonymousAuthenticationToken) && authentication.isAuthenticated();
    }

}
