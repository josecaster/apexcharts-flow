package sr.we.ui.views.finance.loans.tabs;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import sr.we.ContextProvider;
import sr.we.data.controller.LoanAssetsService;
import sr.we.data.controller.UserAccessService;
import sr.we.demo.about.AboutView;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.LoanAssets;
import sr.we.shekelflowcore.entity.Role;
import sr.we.shekelflowcore.exception.ValidationException;
import sr.we.shekelflowcore.security.Privileges;
import sr.we.shekelflowcore.security.privileges.LoanAssetsPrivilege;
import sr.we.shekelflowcore.security.privileges.TransactionsPrivilege;
import sr.we.ui.views.TableLayout;
import sr.we.ui.views.business.BusinessView;
import sr.we.ui.views.finance.loans.tabs.settings.LoanAssetCreateView;
import sr.we.ui.views.finance.loans.LoansView;

import javax.annotation.security.RolesAllowed;
import java.util.Optional;

@Route(value = "settings", layout = LoansView.class)
@RolesAllowed({Role.user, Role.staff, Role.owner, Role.admin})
//@PreserveOnRefresh
public class LTabSettings extends TableLayout implements AfterNavigationObserver, HasDynamicTitle, BeforeEnterObserver {

    public static String getLocation(String business, String loan) {
        return LoansView.getLocation(business, loan) + "/settings";
    }

    private String loanId;

    Grid<LoanAssets> grid = new Grid<>();
    private String business;

    public LTabSettings() {
        addClassName("loans-view");

        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_NO_ROW_BORDERS);
        grid.addComponentColumn(person -> createCard(person, business, true)).setResizable(true);
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.addSelectionListener(get -> {
            Optional<LoanAssets> firstSelectedItem = get.getFirstSelectedItem();
            if (firstSelectedItem.isPresent()) {
                LoanAssets loanAssets = firstSelectedItem.get();
                QueryParameters queryParameters = QueryParameters.fromString("id=" + loanAssets.getId());
//                UI.getCurrent().navigate(LoanAssetssViewOverview.getLocation(business, loanId), queryParameters);
                new ValidationException("Not yet implemented");
            }
        });
        add(grid);
        grid.setAllRowsVisible(true);
    }

    @Override
    protected void createVisible(boolean visible) {
        UserAccessService userAccesService = ContextProvider.getBean(UserAccessService.class);
        boolean hasAccess = userAccesService.hasAccess(AuthenticatedUser.token(), new LoanAssetsPrivilege(), Privileges.INSERT);
        super.createVisible(hasAccess);
    }

    @Override
    protected void onCreateClick() {
//        QueryParameters queryParameters = QueryParameters.fromString("id=" + loanId);
//        UI.getCurrent().navigate(LoanAssetsViewNew.getLocation(business, loanId), queryParameters);
        UI.getCurrent().navigate(LoanAssetCreateView.class, //
                new RouteParameters(//
                        new RouteParam("business", business),//
                        new RouteParam("loan", loanId)));
    }

    public static HorizontalLayout createCard(LoanAssets loan, String business, boolean showContext) {
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
        Span date = new Span(loan.getAssetType() == null ? null : loan.getAssetType().getName());
        date.addClassName("date");
        header.add(name, date);

        card.add(header);

        return card;
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        LoanAssetsService loanService = ContextProvider.getBean(LoanAssetsService.class);
        grid.setItems(loanService.list(AuthenticatedUser.token(), Long.valueOf(loanId)).getResult());

    }


    @Override
    public String getPageTitle() {
        return getTranslation("sr.we.loan.assets");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        UserAccessService userAccesService = ContextProvider.getBean(UserAccessService.class);
        boolean hasAccess = userAccesService.hasAccess(AuthenticatedUser.token(), new LoanAssetsPrivilege(), Privileges.READ);
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
