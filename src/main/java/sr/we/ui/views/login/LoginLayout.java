package sr.we.ui.views.login;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

/**
 * A Designer generated component for the login-layout template.
 *
 * Designer will add and remove fields with @Id mappings but
 * does not overwrite or otherwise change this file.
 */
//@PageTitle("Home")
//@Route(value = "")
//@AnonymousAllowed
@Tag("login-layout")
@JsModule("./src/views/authenticate/login-layout.ts")
//@JsModule("./themes/shekelflow/js/login-styles.js")
//@CssImport("./themes/shekelflow/views/login-style.css")

public class LoginLayout extends LitTemplate {

    @Id("container")
    private Div container;
    @Id("vaadinButton")
    private Button vaadinButton;

    /**
     * Creates a new LoginLayout.
     */
    public LoginLayout() {
        // You can initialise any data required for the connected UI components here.
        vaadinButton.addClickListener(f -> {

        });
    }

}
