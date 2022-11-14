package sr.we.ui.views.customers;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.router.*;
import sr.we.shekelflowcore.entity.Customer;
import sr.we.ui.views.TableLayout;

import java.util.*;

//@PageTitle("Customers")

public class CustomersGrid extends TableLayout{

    private final Grid<Customer> grid;
    private String businessString;

    public CustomersGrid() {

        this.grid = new Grid<>();
        this.grid.addClassNames("resonate"/*, LumoUtility.BoxShadow.SMALL, "my-cart-white", LumoUtility.Margin.MEDIUM*/);
        this.grid.setAllRowsVisible(true);
        this.grid.setSelectionMode(Grid.SelectionMode.MULTI);
//        this.grid.setMaxWidth("500px");

//        this.grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_NO_ROW_BORDERS, GridVariant.LUMO_NO_BORDER);
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
        this.grid.addColumn(Customer::getName).setHeader("Name");
        this.grid.addColumn(Customer::getFirstName).setHeader("Firstname");
        this.grid.addColumn(f -> f.getPrimaryBillingAddress() == null ? null : (f.getPrimaryBillingAddress().getCountry() == null ? null : f.getPrimaryBillingAddress().getCountry().getName())).setHeader("Country");
        this.grid.addColumn(f -> f.getPrimaryBillingAddress() == null ? null : f.getPrimaryBillingAddress().getState()).setHeader("State");
        this.grid.addColumn(f -> f.getPrimaryBillingAddress() == null ? null : f.getPrimaryBillingAddress().getAddress()).setHeader("Address");
        this.grid.addColumn(f -> f.getPrimaryCustomerContacts() == null ? null : f.getPrimaryCustomerContacts().getEmail()).setHeader("Email");
        this.grid.addColumn(f -> f.getPrimaryCustomerContacts() == null ? null : f.getPrimaryCustomerContacts().getMobile()).setHeader("Mobile number");
        this.grid.addColumn(f -> f.getPrimaryCustomerContacts() == null ? null : f.getPrimaryCustomerContacts().getPhone()).setHeader("Phone number");
//        this.grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        this.grid.addItemDoubleClickListener(f -> {
            Customer loanRequest = f.getItem();
            if (loanRequest== null) {
                return;
            }
            List<String> strings = Arrays.asList(loanRequest.getId().toString());
            Map<String, List<String>> map = new HashMap<>();
            map.put("id", strings);
            QueryParameters queryParameters = new QueryParameters(map);
            UI.getCurrent().navigate(CustomerViewCreate.getLocation(businessString), queryParameters);
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

    public void setBusinessString(String businessString) {
        this.businessString = businessString;
    }

    private Component createCard(Customer customer) {
//        HorizontalLayout card = new HorizontalLayout();
//        card.addClassName("card");
//        card.setSpacing(true);
//        card.setPadding(false);
////        card.getThemeList().add("spacing-s");
//
//        VerticalLayout header = new VerticalLayout();
//        header.addClassName("header");
//        header.setSpacing(true);
////        header.getThemeList().add("spacing-s");
//        header.setPadding(false);
//
//        Span name = new Span(customer.getName());
//        name.addClassName("name");
//        Span date = new Span(customer.getAccount());
//        date.addClassName("date");
//        header.add(name, date);


        Avatar avatar = new Avatar(customer.getName());
        avatar.setColorIndex(new Random().nextInt(7 - 1 + 1) + 1);
//        card.add(avatar, header);

        return avatar;
    }

    @Override
    protected void onCreateClick() {
        UI.getCurrent().navigate(CustomerViewCreate.class, new RouteParameters(new RouteParam("business", businessString)));
    }

    public void setItems(List<Customer> customer) {
        grid.setItems(customer);
        grid.getDataProvider().refreshAll();
    }
}
