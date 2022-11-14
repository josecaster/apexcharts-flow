package sr.we.ui.views.customers;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.router.*;
import sr.we.ContextProvider;
import sr.we.data.controller.CustomerService;
import sr.we.data.controller.UserAccessService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Customer;
import sr.we.shekelflowcore.entity.Role;
import sr.we.shekelflowcore.security.PrivilegeModeAbstract;
import sr.we.shekelflowcore.security.Privileges;
import sr.we.shekelflowcore.security.privileges.CustomerPrivilege;
import sr.we.ui.components.BreadCrumb;
import sr.we.ui.views.MainLayout;

import javax.annotation.security.RolesAllowed;
import java.util.List;
import java.util.Optional;

/**
 * A Designer generated component for the customer-view template.
 *
 * Designer will add and remove fields with @Id mappings but
 * does not overwrite or otherwise change this file.
 */
@Tag("customer-view")
@JsModule("./src/views/customer/customer-view.ts")
@BreadCrumb(titleKey = "sr.we.customers")
@Route(value = "customers", layout = MainLayout.class)
@RolesAllowed({Role.user, Role.staff, Role.owner, Role.admin})
public class CustomerView extends LitTemplate implements BeforeEnterObserver {

    @Id("add-customer-btn")
    private Button addCustomerBtn;
    private CustomersGrid customersGrid;
    @Id("customer-grid-layout")
    private Div customerGridLayout;

    /**
     * Creates a new CustomerView.
     */
    public CustomerView() {
        // You can initialise any data required for the connected UI components here.

        addCustomerBtn.addClickListener(f -> {
            UI.getCurrent().navigate(CustomerViewCreate.class, new RouteParameters(new RouteParam("business", businessString)));
        });
    }

    private String businessString;

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<String> business = event.getRouteParameters().get("business");
        String token = AuthenticatedUser.token();
        if (business.isPresent()) {
            businessString = business.get();
            UI current = UI.getCurrent();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    CustomerService CustomerService = ContextProvider.getBean(CustomerService.class);

                    List<Customer> Customer = CustomerService.list(Long.valueOf(businessString), token).getResult();
                    current.access(() -> {
                        customersGrid = new CustomersGrid();
                        customersGrid.setBusinessString(businessString);
                        customerGridLayout.removeAll();
                        customerGridLayout.add(customersGrid);
                        customersGrid.setItems(Customer);
                    });
                }
            }).start();
            UserAccessService userAccessService = ContextProvider.getBean(UserAccessService.class);
            boolean hasAccess = userAccessService.hasAccess(token, PrivilegeModeAbstract.getInstance(CustomerPrivilege.class), Privileges.INSERT);
            addCustomerBtn.setVisible(hasAccess);
        }

    }

    public static String getLocation(String business) {
        return MainLayout.getLocation(business) + "/customers";
    }

}
