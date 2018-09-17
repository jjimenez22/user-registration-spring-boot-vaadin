package com.example.view;

import com.example.service.User;
import com.example.service.UserRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import org.springframework.util.StringUtils;

@Route
public class MainView extends VerticalLayout {

    private final UserRepository repository;

    private final UserEditor editor;
    private final Grid<User> grid = new Grid<>(User.class);
    private final TextField filter = new TextField();
    private final Button add = new Button("Nuevo", VaadinIcon.PLUS.create());

    public MainView(UserRepository repository, UserEditor editor) {
        this.repository = repository;
        this.editor = editor;
        add(new HorizontalLayout(filter, add),
                grid, editor);

        grid.setHeight("300px");
        grid.setColumns("id");
        grid.getColumnByKey("id").setWidth("50px").setFlexGrow(0).setHeader("ID");
        grid.addColumn("firstName").setHeader("Nombre");
        grid.addColumn("lastName").setHeader("Apellido");
        grid.addColumn("email").setHeader("Correo");

        filter.setPlaceholder("Filtrar por nombre");
        filter.setValueChangeMode(ValueChangeMode.EAGER);
        filter.addValueChangeListener(e -> listUser(e.getValue()));

        grid.asSingleSelect().addValueChangeListener(e -> editor.editUser(e.getValue()));

        add.addClickListener(e -> editor.editUser(new User()));

        editor.setChangeHandler(() -> {
            editor.setVisible(false);
            listUser(filter.getValue());
        });

        listUser(null);
    }

    private void listUser(String text) {
        if (StringUtils.isEmpty(text)) {
            grid.setItems(repository.findAll());
        } else {
            grid.setItems(repository.findByFirstNameStartsWithIgnoreCase(text));
        }
    }
}
