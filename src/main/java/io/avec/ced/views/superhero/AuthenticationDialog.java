package io.avec.ced.views.superhero;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.function.SerializableRunnable;
import io.avec.ced.security.AuthenticatedUser;
import org.springframework.security.authentication.BadCredentialsException;

public class AuthenticationDialog extends Dialog {

    public AuthenticationDialog(AuthenticatedUser authenticatedUser, SerializableRunnable callWhenAuthenticated) {

        VerticalLayout loginLayout = new VerticalLayout();
        PasswordField password = new PasswordField("Password");
        password.focus();

//        Button button = new Button("Authenticate", e -> authenticate(authenticatedUser, callWhenAuthenticated, password));

        password.addKeyUpListener(Key.ENTER, upEvent -> {
            authenticate(authenticatedUser, callWhenAuthenticated, password);
        });

//        loginLayout.add(password, button);
        loginLayout.add(password);
        add(loginLayout);
    }

    private void authenticate(AuthenticatedUser authenticatedUser, SerializableRunnable callWhenAuthenticated, PasswordField password) {
        try {
            authenticatedUser.reauthenticate(password.getValue());
            close();
            callWhenAuthenticated.run();
        } catch (BadCredentialsException e) {
            password.setValue("");
            password.setErrorMessage(e.getMessage());
            password.setInvalid(true);
            password.focus();
            //                    Notification.show(e.getMessage());
        }
    }

}
