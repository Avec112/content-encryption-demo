package io.avec.ced.views.superhero;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.Shortcuts;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.function.SerializableRunnable;

public class ConfirmDialog extends Dialog {

    public ConfirmDialog(SerializableRunnable callWhenConfirmed) {
        Button confirmButton = new Button("Confirm", e -> {
            close();
            callWhenConfirmed.run();
        });

        confirmButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        Shortcuts.addShortcutListener(confirmButton, confirmButton::click, Key.ENTER, KeyModifier.CONTROL);

        Button cancelButton = new Button("Cancel", e -> close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        HorizontalLayout buttons = new HorizontalLayout(confirmButton, cancelButton);
        buttons.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        VerticalLayout layout = new VerticalLayout(
                new Paragraph("Are you sure nobody is looking?"),
                buttons
        );

        add(layout);
    }
}
