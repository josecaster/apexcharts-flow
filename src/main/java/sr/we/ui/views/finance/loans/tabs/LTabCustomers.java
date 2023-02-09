package sr.we.ui.views.finance.loans.tabs;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import sr.we.ContextProvider;
import sr.we.data.controller.CustomerService;
import sr.we.data.controller.UserAccessService;
import sr.we.demo.about.AboutView;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Customer;
import sr.we.shekelflowcore.entity.Role;
import sr.we.shekelflowcore.entity.helper.vo.CustomerVO;
import sr.we.shekelflowcore.exception.ValidationException;
import sr.we.shekelflowcore.security.Privileges;
import sr.we.shekelflowcore.security.privileges.CustomerPrivilege;
import sr.we.ui.views.business.BusinessView;
import sr.we.ui.views.finance.loans.LoansView;

import javax.annotation.security.RolesAllowed;
import java.util.List;
import java.util.Optional;

@Deprecated
@Route(value = "customers", layout = LoansView.class)
@RolesAllowed({Role.user, Role.staff, Role.owner, Role.admin})
//@PreserveOnRefresh
public class LTabCustomers extends VerticalLayout implements AfterNavigationObserver, HasDynamicTitle, BeforeEnterObserver {

    public static String getLocation(String business, String loan) {
        return LoansView.getLocation(business, loan) + "/customers";
    }

    private String loanId;

    Grid<Customer> grid = new Grid<>();
    private String business;

    public LTabCustomers() {
        addClassName("loans-view");
        setSizeFull();
        grid.setHeight("100%");
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_NO_ROW_BORDERS);
        grid.addComponentColumn(person -> createCard(person, business, true)).setResizable(true);
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.addSelectionListener(get -> {
            Optional<Customer> firstSelectedItem = get.getFirstSelectedItem();
            if (firstSelectedItem.isPresent()) {
                Customer loan = firstSelectedItem.get();
                QueryParameters queryParameters = QueryParameters.fromString("id=" + loan.getId());
//                UI.getCurrent().navigate(LTabDashboard.getLocation(business, loan.getId().toString()), queryParameters);
            }
        });
        add(grid);
    }

    public static HorizontalLayout createCard(Customer loan, String business, boolean showContext) {
        UI current = UI.getCurrent();

        HorizontalLayout card = new HorizontalLayout();
        card.addClassName("card");
        card.setSpacing(false);
//        card.getThemeList().add("spacing-s");
        card.setPadding(false);

        VerticalLayout header = new VerticalLayout();
        header.addClassName("header");
        header.setSpacing(false);
//        header.getThemeList().add("spacing-s");
        header.setPadding(false);

        Span name = new Span(loan.getName());
        name.addClassName("name");
        Span date = new Span(loan.getAccount());
        date.addClassName("date");
        header.add(name, date);

        card.add(header);
        return card;
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        CustomerService CustomerService = ContextProvider.getBean(CustomerService.class);
        String token = AuthenticatedUser.token();
        CustomerVO customerVO = new CustomerVO();customerVO.setBusiness(Long.valueOf(business));
        List<Customer> customer = CustomerService.list(customerVO, token).getResult();
        grid.setItems(customer);
    }


    @Override
    public String getPageTitle() {
        return getTranslation("sr.we.loan.customers");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        UserAccessService userAccesService = ContextProvider.getBean(UserAccessService.class);
        boolean hasAccess = userAccesService.hasAccess(AuthenticatedUser.token(), new CustomerPrivilege(), Privileges.READ);
        if(!hasAccess){
            UI.getCurrent().navigate(AboutView.class);
        }
        Optional<String> business1 = event.getRouteParameters().get("business");
        if (business1.isPresent()) {
            business = business1.get();
        }
        Optional<String> loan = event.getRouteParameters().get("loan");
//        QueryParameters queryParams = event.getLocation().getQueryParameters();
//        List<String> id1 = queryParams.getParameters().get("id");
//        Optional<String> id = id1.stream().findAny();
        if (loan.isEmpty()) {
            event.forwardTo(BusinessView.class);
            throw new ValidationException("Invalid Authentication");
        } else {
            loanId = loan.get();
        }
    }
}
