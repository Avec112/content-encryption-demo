package io.avec.ced.views.superhero;

import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import io.avec.ced.data.dto.SuperheroDTO;

public class SuperheroDialog extends Dialog {

    public SuperheroDialog(SuperheroDTO superheroDTO) {

        HorizontalLayout titleLayout = new HorizontalLayout(new H2(superheroDTO.getNickname()));
        titleLayout.setWidthFull();
        titleLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        VerticalLayout layout = new VerticalLayout(titleLayout);
        layout.add(createSuperheroLayout(superheroDTO));

        getElement().setAttribute("theme", "secret-dialog");
        add(layout);
    }

    private VerticalLayout createSuperheroLayout(SuperheroDTO dto) {
        VerticalLayout form = new VerticalLayout();

        final TextField firstname = new TextField("First name");
        firstname.setValue(dto.getFirstname());
        form.add(firstname);

        final TextField lastname = new TextField("Last name");
        lastname.setValue(dto.getLastname());
        form.add(lastname);

        final DatePicker dob = new DatePicker("Date of birth");
        dob.setValue(dto.getDateOfBirth());
        form.add(dob);

        final TextField country = new TextField("Nationality");
        country.setValue(dto.getCountry());
        form.add(country);

        return form;
    }
}
