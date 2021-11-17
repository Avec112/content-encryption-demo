package io.avec.ced.views.superhero;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.function.SerializableRunnable;
import io.avec.ced.security.AuthenticatedUser;
import org.springframework.security.authentication.BadCredentialsException;

public class AuthenticationDialog extends Dialog {

    public AuthenticationDialog(AuthenticatedUser authenticatedUser, SerializableRunnable callWhenAuthenticated) {

        VerticalLayout loginLayout = new VerticalLayout();
        PasswordField password = new PasswordField();
        password.setPlaceholder("User password");
        password.focus();

        password.addKeyPressListener(Key.ENTER, e -> {
            authenticate(authenticatedUser, callWhenAuthenticated, password);
        });

        loginLayout.add(new Paragraph("Authentication required"), password);
        add(loginLayout);
    }

    private void authenticate(AuthenticatedUser authenticatedUser, SerializableRunnable callWhenAuthenticated, PasswordField password) {
        try {
            authenticatedUser.reauthenticate(password.getValue());
            close();
            callWhenAuthenticated.run();
        } catch (BadCredentialsException e) {
            passwordErrorException(password, e);
        }
    }

    private void passwordErrorException(PasswordField password, BadCredentialsException e) {
        password.setValue("");
        password.setErrorMessage(e.getMessage());
        password.setInvalid(true);
        password.focus();
    }

}
