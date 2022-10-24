package sr.we.ui.views.currencyexchange;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import sr.we.ContextProvider;
import sr.we.data.controller.ExchangeRateService;
import sr.we.data.controller.UserAccessService;
import sr.we.demo.about.AboutView;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Currency;
import sr.we.shekelflowcore.entity.CurrencyExchange;
import sr.we.shekelflowcore.entity.Role;
import sr.we.shekelflowcore.entity.helper.vo.CurrencyExchangeVO;
import sr.we.shekelflowcore.exception.ValidationException;
import sr.we.shekelflowcore.security.Privileges;
import sr.we.shekelflowcore.security.privileges.CurrencyExchangePrivilege;
import sr.we.shekelflowcore.settings.util.Constants;
import sr.we.shekelflowcore.settings.util.DateUtil;
import sr.we.ui.components.BreadCrumb;
import sr.we.ui.components.general.CurrencySelect;
import sr.we.ui.views.MainLayout;

import javax.annotation.security.RolesAllowed;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

/**
 * A Designer generated component for the currency-exchange-view template.
 * <p>
 * Designer will add and remove fields with @Id mappings but
 * does not overwrite or otherwise change this file.
 */
@BreadCrumb(titleKey = "sr.we.currency.exchange.rates")
@Tag("currency-exchange-view")
@JsModule("./src/views/currencyexchange/currency-exchange-view.ts")
@Route(value = "currency_exchange", layout = MainLayout.class)
@RolesAllowed({Role.user, Role.staff, Role.owner, Role.admin})
public class CurrencyExchangeView extends LitTemplate implements BeforeEnterObserver {

    private final Grid<CurrencyExchange> grid;
    private final CurrencyExchangeVO filter;
    @Id("rate-grid-layout")
    private Div rateGridLayout;
    @Id("add-rate-btn")
    private Button addRateBtn;
    @Id("currency-from-select")
    private CurrencySelect currencyFromSelect;
    @Id("currency-to-select")
    private CurrencySelect currencyToSelect;
    @Id("from-amount-fld")
    private BigDecimalField fromAmountFld;
    @Id("to-amount-fld")
    private BigDecimalField toAmountFld;
    private Long businessId;
    private String business;
    @Id("vaadinFormLayout")
    private FormLayout vaadinFormLayout;
    @Id("currency-to--replica-select")
    private CurrencySelect currencyToReplicaSelect;
    @Id("currency-from--replica-select")
    private CurrencySelect currencyFromReplicaSelect;
    @Id("vaadinTabs")
    private Tabs vaadinTabs;
    private final Tab vaadinTab;
    private Tab vaadinTab1;
    private final Tab vaadinTab2;

    /**
     * Creates a new CurrencyExchangeView.
     */
    public CurrencyExchangeView() {
        // You can initialise any data required for the connected UI components here.

        vaadinFormLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0px", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP), new FormLayout.ResponsiveStep("500px", 3, FormLayout.ResponsiveStep.LabelsPosition.TOP));

        grid = new Grid<>();
        grid.addColumn(f -> {
            return getFrom(f) + "-" + getTo(f) + " xf  " + Constants.CURRENCY_FORMAT.format(f.getAmountFrom());
        }).setHeader("Exchange rate (1)");
        grid.addColumn(f -> {
            return getTo(f) + "-" + getFrom(f) + " xf  " + Constants.CURRENCY_FORMAT.format(f.getAmountTo());
        }).setHeader("Exchange rate (2)");
        grid.addColumn(f -> {
            return Constants.SIMPLE_DATE_TIME_FORMAT_24H.format(DateUtil.convertToDateViaInstant(f.getStartDateTime()));
        }).setHeader("Started");
        grid.addColumn(f -> {
            return f.getEndDateTime() == null ? "Active" : (Constants.SIMPLE_DATE_TIME_FORMAT_24H.format(DateUtil.convertToDateViaInstant(f.getEndDateTime())));
        }).setHeader("Live");
        filter = new CurrencyExchangeVO();
        grid.setItems(CurrencyExchangeDataProvider.fetch(filter), CurrencyExchangeDataProvider.count(filter));

        rateGridLayout.add(grid);

        fromAmountFld.setRequiredIndicatorVisible(true);
        toAmountFld.setRequiredIndicatorVisible(true);

        fromAmountFld.addValueChangeListener(f -> {
            if (f.getValue() != null && f.getValue().compareTo(BigDecimal.ZERO) != 0) {
                toAmountFld.setValue(BigDecimal.ONE.divide(f.getValue(), 4, RoundingMode.HALF_UP));
            }
        });

        addRateBtn.addClickListener(f -> {
            ExchangeRateService productService = ContextProvider.getBean(ExchangeRateService.class);
            String token = AuthenticatedUser.token();

            CurrencyExchangeVO currencyExchangeVO = new CurrencyExchangeVO();
            currencyExchangeVO.setNew(true);
            currencyExchangeVO.setCurrencyToId(currencyToSelect.getValue() == null ? null : currencyToSelect.getValue().getId());
            currencyExchangeVO.setCurrencyFromId(currencyFromSelect.getValue() == null ? null : currencyFromSelect.getValue().getId());


            currencyExchangeVO.setAmountFrom(fromAmountFld.getValue());
            currencyExchangeVO.setAmountTo(toAmountFld.getValue());
            currencyExchangeVO.setBusinessId(businessId);

            validate(currencyExchangeVO);

            if (currencyExchangeVO.getCurrencyToId() == null || currencyExchangeVO.getCurrencyFromId() == null || currencyExchangeVO.getAmountTo() == null || currencyExchangeVO.getAmountFrom() == null) {
                throw new ValidationException("Please fill in all the fields");
            }

            if (currencyExchangeVO.getCurrencyToId().compareTo(currencyExchangeVO.getCurrencyFromId()) == 0) {
                throw new ValidationException("Currencies cannot be the same");
            }
            productService.create(token, currencyExchangeVO);

            vaadinTabs.setSelectedTab(vaadinTab1);
            refresh(token);
            Notification notification = new Notification();
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            notification.setText(getTranslation("sr.we.success"));
            notification.setDuration(5000);
            notification.setPosition(Notification.Position.MIDDLE);
            notification.open();
        });

        currencyFromSelect.addValueChangeListener(f -> {
            currencyFromReplicaSelect.setValue(f.getValue());
        });
        currencyToSelect.addValueChangeListener(f -> {
            currencyToReplicaSelect.setValue(f.getValue());
        });
        vaadinTab = new Tab("All");
        vaadinTab1 = new Tab("Active");
        vaadinTab2 = new Tab("Archived");
        vaadinTabs.add(vaadinTab, vaadinTab1, vaadinTab2);
        vaadinTabs.addSelectedChangeListener(f -> {
            Tab selectedTab = f.getSelectedTab();
            if (selectedTab.equals(vaadinTab)) {
                filter.setActive(null);
            } else filter.setActive(selectedTab.equals(vaadinTab1));
            refresh(AuthenticatedUser.token());
        });
    }

    private void validate(CurrencyExchangeVO currencyExchangeVO) {
        boolean valid = currencyExchangeVO.getCurrencyToId() != null;
        if (currencyExchangeVO.getCurrencyFromId() == null) {
            valid = false;
        }
        if (currencyExchangeVO.getAmountFrom() == null) {
            valid = false;
        }
        if (currencyExchangeVO.getAmountTo() == null) {
            valid = false;
        }
        if (!valid) {
            throw new ValidationException("Please fill in all required fields");
        }
    }

    private String getTo(CurrencyExchange f) {
        Long currencyToId = f.getCurrencyToId();
        Optional<Currency> currency = currencyFromSelect.getCurrency(currencyToId);
        if (currency.isPresent()) {
            Currency currency1 = currency.get();
            return currency1.getCode();
        }
        return "Error!";
    }

    private String getFrom(CurrencyExchange f) {
        Long currencyFromId = f.getCurrencyFromId();
        Optional<Currency> currency = currencyFromSelect.getCurrency(currencyFromId);
        if (currency.isPresent()) {
            Currency currency1 = currency.get();
            return currency1.getCode();
        }
        return "Error!";
    }

    private void refresh(String token) {
        grid.getDataProvider().refreshAll();
    }

    public void beforeEnter(BeforeEnterEvent event) {
        UserAccessService userAccesService = ContextProvider.getBean(UserAccessService.class);
        boolean hasAccess = userAccesService.hasAccess(AuthenticatedUser.token(), new CurrencyExchangePrivilege(), Privileges.READ);
        if (!hasAccess) {
            UI.getCurrent().navigate(AboutView.class);
        }
        Optional<String> business1 = event.getRouteParameters().get("business");
        if (business1.isPresent()) {
            business = business1.get();
        }
        businessId = Long.valueOf(business);
        filter.setBusinessId(businessId);

        refresh(AuthenticatedUser.token());
    }

    public class EmployeeFilter {
        private String filterText;
        private Object department;

        public EmployeeFilter(String filterText, Object object) {
            this.filterText = filterText;
        }

        public String getFilterText() {
            return filterText;
        }

        public void setFilterText(String filterText) {
            this.filterText = filterText;
        }

        public Object getObject() {
            return department;
        }

        public void setObject(Object department) {
            this.department = department;
        }
    }

}
