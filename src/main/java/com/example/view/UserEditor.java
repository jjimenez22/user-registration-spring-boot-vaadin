package com.example.view;

import com.example.service.User;
import com.example.service.UserRepository;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

@SpringComponent
@UIScope
public class UserEditor extends VerticalLayout implements KeyNotifier {

    private final UserRepository repository;

    private User user;

    private TextField firstName = new TextField("Nombre");
    private TextField lastName = new TextField("Apellido");
    private TextField email = new TextField("Email");
    //    private TextField password = new TextField("Contraseña");
    private Binder<User> binder = new Binder<>(User.class);
    private ChangeHandler changeHandler;
    private Button save = new Button("Guardar", VaadinIcon.CHECK.create(), e -> save());
    private Button delete = new Button("Eliminar", VaadinIcon.TRASH.create(), e -> delete());
    private Button cancel = new Button("Cancelar", VaadinIcon.CLOSE.create(), e -> save());

    @Autowired
    public UserEditor(UserRepository repository) {
        this.repository = repository;

        add(new HorizontalLayout(firstName, lastName), email, /*password,*/
                new HorizontalLayout(save, delete, cancel));
        binder.bindInstanceFields(this);

        setSpacing(true);
        save.getElement().getThemeList().add("primary");
        delete.getElement().getThemeList().add("error");

        addKeyPressListener(Key.ENTER, e -> save());

        setVisible(false);
    }

    private void save() {
        repository.save(user);
        changeHandler.onChange();
    }

    private void delete() {
        repository.delete(user);
        changeHandler.onChange();
    }

    public void editUser(User user) {
        if (Objects.isNull(user)) {
            setVisible(false);
            return;
        }

        boolean persisted = Objects.nonNull(user.getId());
        if (persisted) {
            this.user = repository.findById(user.getId()).get();
        } else {
            this.user = user;
        }
        cancel.setVisible(persisted);

        binder.setBean(this.user);
        setVisible(true);
        firstName.focus();
    }

    public void setChangeHandler(ChangeHandler changeHandler) {
        this.changeHandler = changeHandler;
    }

    @FunctionalInterface
    public interface ChangeHandler {
        void onChange();
    }
}
