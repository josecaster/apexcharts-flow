package sr.we.ui.views.customers;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import sr.we.ContextProvider;
import sr.we.data.controller.CustomerService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Customer;
import sr.we.shekelflowcore.entity.Role;
import sr.we.ui.components.BreadCrumb;
import sr.we.ui.views.MainLayout;
import sr.we.ui.views.TableLayout;

import javax.annotation.security.RolesAllowed;
import java.util.List;
import java.util.Optional;
import java.util.Random;

//@PageTitle("Customers")
@BreadCrumb(titleKey = "sr.we.customers")
@Route(value = "customers", layout = MainLayout.class)
@RolesAllowed({Role.user, Role.staff, Role.owner, Role.admin})
public class CustomersView extends TableLayout implements AfterNavigationObserver, BeforeEnterObserver, HasDynamicTitle, BeforeLeaveObserver {

    private final Grid<Customer> grid;
    private String businessString;

    public CustomersView() {
        layout.addClassName("loans-view");
        this.grid = new Grid<>();
        this.grid.setAllRowsVisible(true);
//        this.grid.setMaxWidth("500px");

        this.grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_NO_ROW_BORDERS, GridVariant.LUMO_NO_BORDER);
        add(grid);
//        this.grid.addColumn("name");
//
//        this.grid.addColumn(new ValueProvider<Customer, String>() {
//            @Override
//            public String apply(Customer customer) {
//                return customer.getPrimaryCustomerContacts().getEmail();
//            }
//        }).setHeader(getTranslation("sr.we.email"));
//
//        this.grid.addColumn(new ValueProvider<Customer, String>() {
//            @Override
//            public String apply(Customer customer) {
//                return customer.getPrimaryCustomerContacts().getPhone();
//            }
//        }).setHeader(getTranslation("sr.we.phone"));
//
//        this.grid.addColumn(new ValueProvider<Customer, String>() {
//            @Override
//            public String apply(Customer customer) {
//                return customer.getPrimaryCustomerContacts().getMobile();
//            }
//        }).setHeader(getTranslation("sr.we.mobile"));
        this.grid.addComponentColumn(customer -> createCard(customer));
        this.grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        this.grid.addSelectionListener(f -> {
            Optional<Customer> firstSelectedItem = f.getFirstSelectedItem();
            if (firstSelectedItem.isEmpty()) {
                return;
            }
            Notification.show("selected " + firstSelectedItem.get().getName());
        });
//        this.grid.addComponentColumn(new ValueProvider<Customer, Button>() {
//            @Override
//            public Button apply(Customer Customer) {
//                Button button = new Button(new LineAwesomeIcon("la la-edit"));
//                button.addClickListener(f -> {
//                    QueryParameters queryParameters = QueryParameters.fromString("id=" + Customer.getId());
//                    UI.getCurrent().navigate("editCustomer", queryParameters);
//                });
//                return button;
//            }
//        }).setHeader(getTranslation("sr.we.actions"));

    }

    private Component createCard(Customer customer) {
        HorizontalLayout card = new HorizontalLayout();
        card.addClassName("card");
        card.setSpacing(true);
        card.setPadding(false);
//        card.getThemeList().add("spacing-s");

        VerticalLayout header = new VerticalLayout();
        header.addClassName("header");
        header.setSpacing(true);
//        header.getThemeList().add("spacing-s");
        header.setPadding(false);

        Span name = new Span(customer.getName());
        name.addClassName("name");
        Span date = new Span(customer.getAccount());
        date.addClassName("date");
        header.add(name, date);


        Avatar avatar = new Avatar(customer.getName());
        avatar.setColorIndex(new Random().nextInt(7 - 1 + 1) + 1);
        card.add(avatar, header);

        return card;
    }

    @Override
    protected void onCreateClick() {
        UI.getCurrent().navigate(CustomerViewCreate.class, new RouteParameters(new RouteParam("business", businessString)));
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {

    }

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
                        grid.setItems(Customer);
                        grid.getDataProvider().refreshAll();
                    });
                }
            }).start();
        }
    }

    @Override
    public String getPageTitle() {
        return getTranslation("sr.we.customers");
    }

    @Override
    public void beforeLeave(BeforeLeaveEvent event) {
    }
}
