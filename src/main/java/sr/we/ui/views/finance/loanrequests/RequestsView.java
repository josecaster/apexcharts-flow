package sr.we.ui.views.finance.loanrequests;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.router.*;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.apache.commons.lang3.StringUtils;
import sr.we.ContextProvider;
import sr.we.CustomNotificationHandler;
import sr.we.data.controller.LoanRequestService;
import sr.we.data.controller.PaymentTransactionService;
import sr.we.data.controller.UserAccessService;
import sr.we.demo.about.AboutView;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Customer;
import sr.we.shekelflowcore.entity.LoanRequest;
import sr.we.shekelflowcore.entity.Role;
import sr.we.shekelflowcore.entity.helper.MappedSuperClass;
import sr.we.shekelflowcore.entity.helper.vo.LoanRequestVO;
import sr.we.shekelflowcore.entity.helper.vo.PaymentTransactionVO;
import sr.we.shekelflowcore.exception.PrimaryThrowable;
import sr.we.shekelflowcore.security.PrivilegeModeAbstract;
import sr.we.shekelflowcore.security.Privileges;
import sr.we.shekelflowcore.security.privileges.LoanRequestPrivilege;
import sr.we.shekelflowcore.settings.util.Constants;
import sr.we.shekelflowcore.settings.util.DateUtil;
import sr.we.ui.components.UIUtil;
import sr.we.ui.components.buttons.DeleteButton;
import sr.we.ui.views.MainLayout;

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
public class RequestsView extends LitTemplate implements BeforeEnterObserver, AfterNavigationObserver {

    private Set<LoanRequest> allSelectedItems;
    Grid<LoanRequest> grid = new Grid<>();
    @Id("add-request-btn")
    private Button addRequestBtn;
    @Id("requests-grid-layout")
    private Div requestsGridLayout;
    private String business;

    /**
     * Creates a new RequestsView.
     */
    public RequestsView() {
        // You can initialise any data required for the connected UI components here.
        addRequestBtn.addClickListener(f -> UI.getCurrent().navigate(AddRequestsView.class, new RouteParameters(new RouteParam("business", business))));

//        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        Grid.Column<LoanRequest> customer = grid.addColumn(f -> f.getCustomer().getName() + (StringUtils.isBlank(f.getCustomer().getFirstName()) ? "" : " " + f.getCustomer().getFirstName())).setHeader("Customer").setSortable(true);
        Grid.Column<LoanRequest> loan_structure = grid.addColumn(f -> f.getLoan().getName()).setHeader("Loan Structure").setSortable(true);
        Grid.Column<LoanRequest> date = grid.addColumn(f -> Constants.SIMPLE_DATE_FORMAT.format(DateUtil.convertToDateViaInstant(f.getEstimatedDate()))).setHeader("Date").setSortable(true);
        grid.addColumn(f -> f.getCurrency().getCode()).setHeader("Currency").setSortable(true);
        grid.addColumn(f -> Constants.CURRENCY_FORMAT.format(f.getAmount())).setHeader("Amount").setSortable(true);
        grid.addColumn(f -> Constants.CURRENCY_FORMAT.format(f.getAmountDue())).setHeader("Amount due").setSortable(true);
        grid.addComponentColumn(f -> createCard(f, business, false)).setHeader("Status");
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        grid.addItemDoubleClickListener(get -> {
            LoanRequest loanRequest = get.getItem();
            List<String> strings = Arrays.asList(loanRequest.getId().toString());
            Map<String, List<String>> map = new HashMap<>();
            map.put("id", strings);
            QueryParameters queryParameters = new QueryParameters(map);
            UI.getCurrent().navigate(EditRequestsView.getLocation(business), queryParameters);
//                QueryParameters queryParameters = QueryParameters.fromString("id=" + loanRequest.getId());
//                UI.getCurrent().navigate(LRView.getLocation(business, loanId), queryParameters);
        });
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setClassName("resonate");
        requestsGridLayout.add(grid);
        grid.setAllRowsVisible(true);

        HeaderRow.HeaderCell join = grid.prependHeaderRow().join(customer, loan_structure, date);
        Div transactionToolbar = new Div();
        transactionToolbar.setWidthFull();
        join.setComponent(transactionToolbar);

        DeleteButton img = new DeleteButton();
        transactionToolbar.add(img);

        ConfirmDialog confirmDialog = new ConfirmDialog("Delete", "Do you wish to delete the selected loan requests?", "Yes", g -> {
            List<Long> longs = allSelectedItems.stream().map(MappedSuperClass::getId).toList();
            LoanRequestVO paymentTransactionVO = new LoanRequestVO();
            paymentTransactionVO.setIds(longs);
            LoanRequestService paymentTransactionService = ContextProvider.getBean(LoanRequestService.class);
            Long count = paymentTransactionService.delete(AuthenticatedUser.token(), paymentTransactionVO);
            CustomNotificationHandler.notify_(new PrimaryThrowable(count + " items deleted"));
            extracted();
        });
        confirmDialog.setConfirmButtonTheme(LumoUtility.Background.ERROR);
        confirmDialog.setCancelable(true);

        img.addClickListener(f -> {
            confirmDialog.open();
        });

        grid.addSelectionListener(get -> {
            allSelectedItems = get.getAllSelectedItems();
            if (allSelectedItems == null || allSelectedItems.isEmpty()) {
                img.setVisible(false);
            }
            img.setVisible(true);
        });
    }

    public static Span createCard(LoanRequest loanRequest, String business, boolean showContext) {
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

        Span name = new Span(customer.getName() + (StringUtils.isBlank(customer.getFirstName()) ? "" : " " + customer.getFirstName()));
//        name.addClassName("name");
        Span date = new Span(amount == null ? null : Constants.CURRENCY_FORMAT.format(amount));
//        date.addClassName("date");
        header.add(name, date);

        Span loanRequestStatusSpan = new Span();
        loanRequestStatusSpan.setWidth("85px");
        card.add(loanRequestStatusSpan);
        loanRequestStatusSpan.setVisible(true);
        loanRequestStatusSpan.setText(loanRequest.getStatus().name());
        ThemeList themeList = loanRequestStatusSpan.getElement().getThemeList();
        themeList.add(UIUtil.Badge.PILL);
        if (loanRequest.getStatus().compareTo(LoanRequest.Status.REQUESTED) == 0) {
            themeList.add("primary");
        } else if (loanRequest.getStatus().compareTo(LoanRequest.Status.APPROVED) == 0) {
            themeList.add("success");
        }
        if (loanRequest.getStatus().compareTo(LoanRequest.Status.CANCEL) == 0) {
            themeList.add("error");
        }
        if (loanRequest.getStatus().compareTo(LoanRequest.Status.ARCHIVE) == 0) {
            themeList.add("tertiary");
        }
        if (loanRequest.getStatus().compareTo(LoanRequest.Status.DONE) == 0) {
            themeList.add("primary success");
        }
//        }

        return loanRequestStatusSpan;
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
        extracted();
    }

    private void extracted() {
        LoanRequestService loanService = ContextProvider.getBean(LoanRequestService.class);
        grid.setItems(loanService.list(AuthenticatedUser.token(), Long.valueOf(business), null).getResult());
    }
}
