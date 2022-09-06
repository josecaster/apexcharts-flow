package sr.we.ui.components;

import com.vaadin.flow.component.textfield.EmailField;

public class EmailAddress extends EmailField {
    public EmailAddress() {
        setLabel("Email address");
        getElement().setAttribute("name", "email");
        setPlaceholder("username@example.com");
        setErrorMessage("Please enter a valid email address");
        setClearButtonVisible(true);
        setPattern("^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");
    }
}
