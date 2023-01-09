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
import sr.we.data.controller.UserAccessService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Role;
import sr.we.shekelflowcore.entity.helper.vo.CustomerVO;
import sr.we.shekelflowcore.security.PrivilegeModeAbstract;
import sr.we.shekelflowcore.security.Privileges;
import sr.we.shekelflowcore.security.privileges.CustomerPrivilege;
import sr.we.ui.components.BreadCrumb;
import sr.we.ui.components.GridUtil;
import sr.we.ui.components.MySearchField;
import sr.we.ui.views.MainLayout;

import javax.annotation.security.RolesAllowed;
import java.util.Optional;

/**
 * A Designer generated component for the customer-view template.
 * <p>
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
    @Id("filter-field")
    private MySearchField filterField;
    private String businessString;
    private CustomerVO filter;

    /**
     * Creates a new CustomerView.
     */
    public CustomerView() {
        // You can initialise any data required for the connected UI components here.

        addCustomerBtn.addClickListener(f -> {
            UI.getCurrent().navigate(CustomerViewCreate.class, new RouteParameters(new RouteParam("business", businessString)));
        });

        customersGrid = new CustomersGrid();
        customerGridLayout.add(customersGrid);
        customerGridLayout.setHeightFull();
        customersGrid.setHeightFull();

    }

    public static String getLocation(String business) {
        return MainLayout.getLocation(business) + "/customers";
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        filter = new CustomerVO();
        Optional<String> business = event.getRouteParameters().get("business");
        String token = AuthenticatedUser.token();
        if (business.isPresent()) {
            businessString = business.get();
            customersGrid.setBusinessString(businessString);
            filter.setBusiness(Long.valueOf(businessString));
            customersGrid.addSortListener(f -> GridUtil.onComponentEvent(f,filter));
            filter.setToken(AuthenticatedUser.token());
            customersGrid.setItems(CustomerDataProvider.fetch(filter), CustomerDataProvider.count(filter));
            customersGrid.refreshAll();
            UI current = UI.getCurrent();
//            Executors.newSingleThreadExecutor().execute(new Runnable() {
//                @Override
//                public void run() {
//                    CustomerService customerService = ContextProvider.getBean(CustomerService.class);
//
//                    List<Customer> Customer = customerService.list(vo, token).getResult();
//                    current.access(() -> {
//                        customersGrid = new CustomersGrid();
//                        customersGrid.setBusinessString(businessString);
//                        customerGridLayout.removeAll();
//                        customerGridLayout.add(customersGrid);
//                        customersGrid.setItems(Customer);
//                    });
//                }
//            });
            UserAccessService userAccessService = ContextProvider.getBean(UserAccessService.class);
            boolean hasAccess = userAccessService.hasAccess(token, PrivilegeModeAbstract.getInstance(CustomerPrivilege.class), Privileges.INSERT);
            addCustomerBtn.setVisible(hasAccess);
        }



    }

}
