package sr.we.ui.views.finance.loanrequests;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.router.*;
import org.apache.commons.lang3.StringUtils;
import sr.we.ContextProvider;
import sr.we.data.controller.LoanRequestService;
import sr.we.data.controller.UserAccessService;
import sr.we.demo.about.AboutView;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Customer;
import sr.we.shekelflowcore.entity.LoanRequest;
import sr.we.shekelflowcore.entity.Role;
import sr.we.shekelflowcore.security.PrivilegeModeAbstract;
import sr.we.shekelflowcore.security.Privileges;
import sr.we.shekelflowcore.security.privileges.LoanPrivilege;
import sr.we.shekelflowcore.security.privileges.LoanRequestPrivilege;
import sr.we.shekelflowcore.settings.util.Constants;
import sr.we.ui.views.MainLayout;
import sr.we.ui.views.finance.loans.tabs.request.LRView;
import sr.we.ui.views.products.AddProductsView;

import javax.annotation.security.RolesAllowed;
import java.math.BigDecimal;
import java.util.*;

/**
 * A Designer generated component for the requests-view template.
 * <p>
 * Designer will add and remove fields with @Id mappings but
 * does not overwrite or otherwise change this file.
 */
@Tag("requests-view")
@JsModule("./src/views/finance/loanrequests/requests-view.ts")
@Route(value = "loan-requests", layout = MainLayout.class)
@RolesAllowed({Role.user, Role.staff, Role.owner, Role.admin})
public class RequestsView extends LitTemplate implements BeforeEnterObserver , AfterNavigationObserver{

    @Id("add-request-btn")
    private Button addRequestBtn;
    @Id("requests-grid-layout")
    private Div requestsGridLayout;
    private String business;

    Grid<LoanRequest> grid = new Grid<>();

    /**
     * Creates a new RequestsView.
     */
    public RequestsView() {
        // You can initialise any data required for the connected UI components here.
        addRequestBtn.addClickListener(f -> UI.getCurrent().navigate(AddRequestsView.class, new RouteParameters(new RouteParam("business", business))));

//        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.addComponentColumn(person -> createCard(person, business, true));
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.addSelectionListener(get -> {
            Optional<LoanRequest> firstSelectedItem = get.getFirstSelectedItem();
            if (firstSelectedItem.isPresent()) {
                LoanRequest loanRequest = firstSelectedItem.get();
                List<String> strings = Arrays.asList(loanRequest.getId().toString());
                Map<String, List<String>> map = new HashMap<>();
                map.put("id", strings);
                QueryParameters queryParameters = new QueryParameters(map);
                UI.getCurrent().navigate(EditRequestsView.getLocation(business),queryParameters);
//                QueryParameters queryParameters = QueryParameters.fromString("id=" + loanRequest.getId());
//                UI.getCurrent().navigate(LRView.getLocation(business, loanId), queryParameters);
            }
        });
        requestsGridLayout.add(grid);
        grid.setAllRowsVisible(true);
    }

    public static HorizontalLayout createCard(LoanRequest loanRequest, String business, boolean showContext) {
        UI current = UI.getCurrent();

        Customer customer = loanRequest.getCustomer();
        BigDecimal amount = loanRequest.getAmount();
        Boolean eligible = loanRequest.getEligible();
        LoanRequest.Status status = loanRequest.getStatus();

        HorizontalLayout card = new HorizontalLayout();
//        card.addClassName("card");
        card.setSpacing(false);
        card.setPadding(false);

        VerticalLayout header = new VerticalLayout();
        card.add(header);
//        header.addClassName("header");
        header.setSpacing(false);
        header.setPadding(false);

        Span name = new Span(customer.getName() + (StringUtils.isBlank(customer.getFirstName()) ? "" : " "+ customer.getFirstName()));
//        name.addClassName("name");
        Span date = new Span(amount == null ? null : Constants.CURRENCY_FORMAT.format(amount));
//        date.addClassName("date");
        header.add(name, date);

        if (eligible != null) {
            Span pending = new Span("Not Eligible");
            pending.getElement().getThemeList().add("badge error");
            pending.getElement().getStyle().set("height","fit-content");

            Span confirmed = new Span("Eligible");
            confirmed.getElement().getThemeList().add("badge success");
            confirmed.getElement().getStyle().set("height","fit-content");

            if (eligible) {
                card.add(confirmed);
            } else {
                card.add(pending);
            }
        } //else {
        switch (status) {
            case REQUESTED -> {
                Span pending = new Span("Requested");
                pending.getElement().getThemeList().add("badge");
                pending.getElement().getStyle().set("height","fit-content");
                card.add(pending);
            }
            case ELIGIBLE -> {
                Span approved = new Span("Eligible");
                approved.getElement().getThemeList().add("badge success");
                approved.getElement().getStyle().set("height","fit-content");
                card.add(approved);
            }
            case REVIEW -> {
                Span review = new Span("Review");
                review.getElement().getThemeList().add("badge contrast");
                review.getElement().getStyle().set("height","fit-content");
                card.add(review);
            }
            case INPUT -> {
                Span input = new Span("Waiting for input");
                input.getElement().getThemeList().add("badge error");
                input.getElement().getStyle().set("height","fit-content");
                card.add(input);
            }
            case CLOSED -> {
                Span approved = new Span("Closed");
                approved.getElement().getThemeList().add("badge success");
                approved.getElement().getStyle().set("height","fit-content");
                card.add(approved);
            }
            case APPROVED -> {
                Span approved = new Span("Approved");
                approved.getElement().getThemeList().add("badge success");
                approved.getElement().getStyle().set("height","fit-content");
                card.add(approved);
            }
            case REPAYMENT -> {
                Span review = new Span("Repayment");
                review.getElement().getThemeList().add("badge contrast");
                review.getElement().getStyle().set("height","fit-content");
                card.add(review);
            }
            case PROVIDE -> {
                Span review = new Span("Provision");
                review.getElement().getThemeList().add("badge contrast");
                review.getElement().getStyle().set("height","fit-content");
                card.add(review);
            }
        }
//        }

        return card;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        UserAccessService userAccessService = ContextProvider.getBean(UserAccessService.class);
        String token = AuthenticatedUser.token();
        boolean hasAccess = userAccessService.hasAccess(token, PrivilegeModeAbstract.getInstance(LoanRequestPrivilege.class), Privileges.READ);
        if (!hasAccess) {
            UI.getCurrent().navigate(AboutView.class);
        }
        Optional<String> business1 = event.getRouteParameters().get("business");
        if (business1.isPresent()) {
            business = business1.get();
        }
    }

    @Override
    public void afterNavigation(AfterNavigationEvent afterNavigationEvent) {
        LoanRequestService loanService = ContextProvider.getBean(LoanRequestService.class);
        grid.setItems(loanService.list(AuthenticatedUser.token(), Long.valueOf(business), null));
    }
}
