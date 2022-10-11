package sr.we.ui.views.settings.users;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.dom.Element;

/**
 * A Designer generated component for the staff-form template.
 *
 * Designer will add and remove fields with @Id mappings but
 * does not overwrite or otherwise change this file.
 */
@Tag("staff-form")
@JsModule("./src/views/settings/staff-form.ts")
public class StaffForm extends LitTemplate {

    @Id("first-name")
    private TextField firstName;
    @Id("email")
    private TextField email;
    @Id("permission-state-txt")
    private Paragraph permissionStateTxt;
    @Id("permission-state-btn")
    private Button permissionStateBtn;
    @Id("permission-form")
    private VerticalLayout permissionForm;
    @Id("last-name")
    private TextField lastName;

    /**
     * Creates a new StaffForm.
     */
    public StaffForm() {
        // You can initialise any data required for the connected UI components here.
    }

    public TextField getFirstName() {
        return firstName;
    }

    public TextField getEmail() {
        return email;
    }

    public Paragraph getPermissionStateTxt() {
        return permissionStateTxt;
    }

    public Button getPermissionStateBtn() {
        return permissionStateBtn;
    }

    public VerticalLayout getPermissionForm() {
        return permissionForm;
    }

    public TextField getLastName() {
        return lastName;
    }
}
