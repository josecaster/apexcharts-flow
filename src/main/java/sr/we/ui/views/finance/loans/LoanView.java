package sr.we.ui.views.finance.loans;

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
import sr.we.ContextProvider;
import sr.we.data.controller.LoanService;
import sr.we.data.controller.UserAccessService;
import sr.we.demo.about.AboutView;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Loan;
import sr.we.shekelflowcore.entity.Role;
import sr.we.shekelflowcore.security.Privileges;
import sr.we.shekelflowcore.security.privileges.LoanPrivilege;
import sr.we.ui.components.UIUtil;
import sr.we.ui.views.MainLayout;
import sr.we.ui.views.finance.loans.tabs.LTabDashboard;

import javax.annotation.security.RolesAllowed;
import java.util.Optional;

/**
 * A Designer generated component for the loan-view template.
 * <p>
 * Designer will add and remove fields with @Id mappings but
 * does not overwrite or otherwise change this file.
 */
@Tag("loan-view")
@JsModule("./src/views/finance/loan/loan-view.ts")
@Route(value = "loans", layout = MainLayout.class)
@RolesAllowed({Role.user, Role.staff, Role.owner, Role.admin})
public class LoanView extends LitTemplate implements AfterNavigationObserver, HasDynamicTitle, BeforeEnterObserver {

    Grid<Loan> grid = new Grid<>();
    private String business;
    @Id("loans-grid-layout")
    private Div loansGridLayout;
    @Id("add-loan-btn")
    private Button addLoanBtn;
    private boolean hasInsertAccess;


    /**
     * Creates a new LoanView.
     */
    public LoanView() {
        // You can initialise any data required for the connected UI components here.

        grid.setAllRowsVisible(true);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_NO_ROW_BORDERS);
        grid.addComponentColumn(person -> createCard(person, business, true));
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.addSelectionListener(get -> {
            Optional<Loan> firstSelectedItem = get.getFirstSelectedItem();
            if (firstSelectedItem.isPresent()) {
                Loan loan = firstSelectedItem.get();
//                QueryParameters queryParameters = QueryParameters.fromString("id=" + loan.getId());
//                UI.getCurrent().navigate(LoansViewTabDashboard.getLocation(business, loan.getId().toString()), queryParameters);
                UI.getCurrent().navigate(LTabDashboard.class, //
                        new RouteParameters(//
                                new RouteParam("business", business),//
                                new RouteParam("loan", loan.getId().toString())));
            }
        });
        loansGridLayout.add(grid);

        addLoanBtn.addClickListener(f -> {
            if (hasInsertAccess) {
                UI.getCurrent().navigate(LoansCreateView.class, new RouteParameters(new RouteParam("business", business)));
            }
        });
    }

    public static HorizontalLayout createCard(Loan loan, String business, boolean showContext) {
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
        Span date = new Span(loan.getCurrency().getName());
        date.addClassName("date");
        header.add(name, date);

        card.add(header);
        Span pending = new Span("Balanced");
        pending.getElement().getStyle().set("height", "fit-content");
        pending.getElement().getThemeList().add(UIUtil.Badge.PILL+" contrast");

        Span confirmed = new Span("Fixed");
        confirmed.getElement().getStyle().set("height", "fit-content");
        confirmed.getElement().getThemeList().add(UIUtil.Badge.PILL+" success");
        if (loan.getFixed()) {
            card.add(confirmed);
        } else {
            card.add(pending);
        }
        return card;
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        LoanService loanService = ContextProvider.getBean(LoanService.class);
        grid.setItems(loanService.list(AuthenticatedUser.token(), Long.valueOf(business)).getResult());

    }


    @Override
    public String getPageTitle() {
        return getTranslation("sr.we.loans");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        UserAccessService userAccesService = ContextProvider.getBean(UserAccessService.class);
        boolean hasAccess = userAccesService.hasAccess(AuthenticatedUser.token(), new LoanPrivilege(), Privileges.READ);
        if (!hasAccess) {
            UI.getCurrent().navigate(AboutView.class);
        }
        Optional<String> business1 = event.getRouteParameters().get("business");
        if (business1.isPresent()) {
            business = business1.get();
        }

        hasInsertAccess = userAccesService.hasAccess(AuthenticatedUser.token(), new LoanPrivilege(), Privileges.INSERT);
        if (!hasInsertAccess) {
            addLoanBtn.setVisible(false);
        }
    }

}
