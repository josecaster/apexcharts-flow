package sr.we.ui.views.settings.users;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.template.Id;

/**
 * A Designer generated component for the add-staff template.
 * <p>
 * Designer will add and remove fields with @Id mappings but
 * does not overwrite or otherwise change this file.
 */
@Tag("add-staff")
@JsModule("./src/views/settings/add-staff.ts")
public class AddStaff extends LitTemplate {

    @Id("staff-count")
    private H3 staffCount;
    @Id("add-staff")
    private Button addStaff;
    @Id("staff-layout")
    private VerticalLayout staffLayout;

    /**
     * Creates a new AddStaff.
     */
    public AddStaff() {
        // You can initialise any data required for the connected UI components here.

    }


    public H3 getStaffCount() {
        return staffCount;
    }

    public Button getAddStaff() {
        return addStaff;
    }

    public VerticalLayout getStaffLayout() {
        return staffLayout;
    }

    public void setCount(int size) {
        staffCount.setText("Staff (" + size + " of 2)");
        if (size > 1) {
            staffCount.setText("Staff");
            addStaff.setVisible(false);
        }
    }
}
