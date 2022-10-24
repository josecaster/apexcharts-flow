package sr.we.ui.views.finance.loans.tabs;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
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
import sr.we.shekelflowcore.exception.ValidationException;
import sr.we.shekelflowcore.security.Privileges;
import sr.we.shekelflowcore.security.privileges.LoanRequestPrivilege;
import sr.we.shekelflowcore.settings.util.Constants;
import sr.we.ui.views.TableLayout;
import sr.we.ui.views.finance.loans.tabs.request.LRCreateView;
import sr.we.ui.views.business.BusinessView;
import sr.we.ui.views.finance.loans.LoansView;
import sr.we.ui.views.finance.loans.tabs.request.LRView;

import javax.annotation.security.RolesAllowed;
import java.math.BigDecimal;
import java.util.Optional;

@Route(value = "requests", layout = LoansView.class)
@RolesAllowed({Role.user, Role.staff, Role.owner, Role.admin})
//@PreserveOnRefresh
public class LTabRequests extends TableLayout implements AfterNavigationObserver, HasDynamicTitle, BeforeEnterObserver {


    public static String getLocation(String business, String loan) {
        return LoansView.getLocation(business, loan) + "/requests";
    }

    private String loanId;

    Grid<LoanRequest> grid = new Grid<>();
    private String business;

    public LTabRequests() {
        addClassName("loans-view");

        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_NO_ROW_BORDERS);
        grid.addComponentColumn(person -> createCard(person, business, true));
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.addSelectionListener(get -> {
            Optional<LoanRequest> firstSelectedItem = get.getFirstSelectedItem();
            if (firstSelectedItem.isPresent()) {
                LoanRequest loanRequest = firstSelectedItem.get();
                QueryParameters queryParameters = QueryParameters.fromString("id=" + loanRequest.getId());
                UI.getCurrent().navigate(LRView.getLocation(business, loanId), queryParameters);
            }
        });
        add(grid);
        grid.setAllRowsVisible(true);

    }

    @Override
    protected void createVisible(boolean visible) {
        UserAccessService userAccesService = ContextProvider.getBean(UserAccessService.class);
        boolean hasAccess = userAccesService.hasAccess(AuthenticatedUser.token(), new LoanRequestPrivilege(), Privileges.INSERT);
        super.createVisible(hasAccess);
    }

    @Override
    protected void onCreateClick() {
//        QueryParameters queryParameters = QueryParameters.fromString("id=" + loanId);
//        UI.getCurrent().navigate(LoanRequestViewNew.getLocation(business, loanId), queryParameters);
        UI.getCurrent().navigate(LRCreateView.class, //
                new RouteParameters(//
                        new RouteParam("business", business),//
                        new RouteParam("loan", loanId)));
    }

    public static HorizontalLayout createCard(LoanRequest loanRequest, String business, boolean showContext) {
        UI current = UI.getCurrent();

        Customer customer = loanRequest.getCustomer();
        BigDecimal amount = loanRequest.getAmount();
        Boolean eligible = loanRequest.getEligible();
        LoanRequest.Status status = loanRequest.getStatus();

        HorizontalLayout card = new HorizontalLayout();
        card.addClassName("card");
        card.setSpacing(false);
        card.setPadding(false);

        VerticalLayout header = new VerticalLayout();
        card.add(header);
        header.addClassName("header");
        header.setSpacing(false);
        header.setPadding(false);

        Span name = new Span(customer.getName() + (StringUtils.isBlank(customer.getFirstName()) ? "" : " "+ customer.getFirstName()));
        name.addClassName("name");
        Span date = new Span(amount == null ? null : Constants.CURRENCY_FORMAT.format(amount));
        date.addClassName("date");
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
                /*case ELIGIBLE -> {
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
                }*/
                case APPROVED -> {
                    Span approved = new Span("Approved");
                    approved.getElement().getThemeList().add("badge success");
                    approved.getElement().getStyle().set("height","fit-content");
                    card.add(approved);
                }
                /*case REPAYMENT -> {
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
                }*/
            }
//        }

        return card;
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        LoanRequestService loanService = ContextProvider.getBean(LoanRequestService.class);
        grid.setItems(loanService.list(AuthenticatedUser.token(), Long.valueOf(business), Long.valueOf(loanId)).getResult());

    }


    @Override
    public String getPageTitle() {
        return getTranslation("sr.we.loan.requests");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        UserAccessService userAccesService = ContextProvider.getBean(UserAccessService.class);
        boolean hasAccess = userAccesService.hasAccess(AuthenticatedUser.token(), new LoanRequestPrivilege(), Privileges.READ);
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
