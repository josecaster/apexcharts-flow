package sr.we.views.login;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.spring.SpringVaadinSession;
import org.apache.commons.lang3.StringUtils;
import sr.we.ContextProvider;
import sr.we.data.controller.BusinessService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.ThisUser;
import sr.we.shekelflowcore.entity.helper.Token;
import sr.we.views.LineAwesomeIcon;
import sr.we.views.MainLayout;
import sr.we.views.UserCompanyProfile;
import sr.we.views.about.AboutView;
import sr.we.views.athenticationauthorization.AthenticationAuthorizationView;
import sr.we.views.communication.CommunicationView;
import sr.we.views.customers.CustomersView;
import sr.we.views.dashboard.DashboardView;
import sr.we.views.flow.FlowView;
import sr.we.views.loans.LoansView;
import sr.we.views.overview.OverviewView;
import sr.we.views.partners.PartnersView;
import sr.we.views.products.ProductsView;
import sr.we.views.productscomponents.ProductsComponentsView;
import sr.we.views.purchases.PurchasesView;
import sr.we.views.reports.ReportsView;

import java.util.Optional;

/**
 * The main view is a top-level placeholder for other views.
 */
@PageTitle("Home")
@Route(value = "")
@AnonymousAllowed
public class IntroLayout extends VerticalLayout {



    public IntroLayout() {

        setSpacing(false);

        VerticalLayout verticalLayout = new VerticalLayout();
//        verticalLayout.getThemeList().add("dark");

        Image img = new Image("images/empty-plant.png", "placeholder plant");
        img.setWidth("200px");
        verticalLayout.add(img);




        verticalLayout.add(new H2("Register for FREE, today"));
        verticalLayout.add(new Paragraph("Simple bookkeeping and payment solutions for small business owners 🤗"));


        EmailField emailField = new EmailField();
        emailField.setPlaceholder("Email address");
        emailField.setWidthFull();
        PasswordField passwordField = new PasswordField();
        passwordField.setWidthFull();
        passwordField.setPlaceholder("Password");
        verticalLayout.add(new FormLayout(emailField,passwordField));
        Button button = new Button("Forgot password?");
        button.setWidthFull();
        verticalLayout.add(button);
        Button go_to_registration = new Button("Register");
        go_to_registration.setWidthFull();
        verticalLayout.add(go_to_registration);

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");

        verticalLayout. add(new Hr());


        Button go_to_login = new Button("Go To Login");
        go_to_login.setWidthFull();
        verticalLayout.add(go_to_login);
        go_to_login.addClickListener(f -> {
            UI.getCurrent().navigate(LoginView.class);
        });

        verticalLayout.setMaxWidth("350px");
        verticalLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        verticalLayout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        verticalLayout.getStyle().set("text-align", "center");
        add(verticalLayout);
    }
}
