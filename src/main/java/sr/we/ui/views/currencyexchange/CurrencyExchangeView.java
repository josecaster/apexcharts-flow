package sr.we.ui.views.currencyexchange;

import com.flowingcode.vaadin.addons.gridexporter.GridExporter;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import sr.we.ContextProvider;
import sr.we.CustomNotificationHandler;
import sr.we.data.controller.ExchangeRateService;
import sr.we.data.controller.UserAccessService;
import sr.we.demo.about.AboutView;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Currency;
import sr.we.shekelflowcore.entity.CurrencyExchange;
import sr.we.shekelflowcore.entity.Role;
import sr.we.shekelflowcore.entity.helper.adapter.CurrencyExchangeBody;
import sr.we.shekelflowcore.entity.helper.vo.CurrencyExchangeVO;
import sr.we.shekelflowcore.exception.SuccessThrowable;
import sr.we.shekelflowcore.exception.ValidationException;
import sr.we.shekelflowcore.security.Privileges;
import sr.we.shekelflowcore.security.privileges.CurrencyExchangePrivilege;
import sr.we.shekelflowcore.settings.util.Constants;
import sr.we.shekelflowcore.settings.util.DateUtil;
import sr.we.ui.components.BreadCrumb;
import sr.we.ui.components.GridUtil;
import sr.we.ui.components.general.CurrencySelect;
import sr.we.ui.views.MainLayout;

import javax.annotation.security.RolesAllowed;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
    private final Tab vaadinTab;
    private final Tab vaadinTab2;
    //    private final RadioButtonGroup<String> buySellGrp;
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
    private Tab vaadinTab1;
    @Id("my-form-layout")
    private FormLayout myFormLayout;
    private CurrencyExchangeBody exchange;
    @Id("start-picker-fld")
    private DateTimePicker startPickerFld;
    @Id("end-picker-fld")
    private DateTimePicker endPickerFld;

    /**
     * Creates a new CurrencyExchangeView.
     */
    public CurrencyExchangeView() {
        startPickerFld.addValueChangeListener(f -> {
            if (f.getValue() != null) {
                endPickerFld.setMin(f.getValue());
                endPickerFld.setValue(f.getValue().plusDays(1));
            }
        });
        startPickerFld.setLabel("Start date");
        endPickerFld.setLabel("End date");
        filter = new CurrencyExchangeVO();
        // You can initialise any data required for the connected UI components here.
//        dateFld.setValue(LocalDate.now());
//        dateFld.addValueChangeListener(f -> {
//            exchange();
//        });


//        flipBtn.setText("Flip");
//        flipBtn.addClickListener(f -> {
//            fromAccountFld.setValue((Account) null);
//            toAccountFld.setValue((Account) null);
//            Currency value = fromCurFld.getValue();
//            BigDecimal value1 = amountFld.getValue();
//            Currency value2 = toCurFld.getValue();
//            BigDecimal value3 = convertedAmountFld.getValue();
//            toCurFld.setValue(value);
//            convertedAmountFld.setValue(value1);
//            fromCurFld.setValue(value2);
//            amountFld.setValue(value3);
//            exchange();
//        });

//        amountFld.addValueChangeListener(f -> {
//            if (f.getValue() == null) {
//                return;
//            }
//            if (f.isFromClient() && fxFld.getValue() != null) {
//                convertedAmountFld.setValue(f.getValue().divide(fxFld.getValue(), 6, RoundingMode.HALF_UP));
//            }
//        });
//        convertedAmountFld.addValueChangeListener(f -> {
//            if (f.getValue() == null) {
//                return;
//            }
//            if (f.isFromClient() && fxFld.getValue() != null) {
//                amountFld.setValue(f.getValue().multiply(fxFld.getValue()));
//            }
//        });

//        buySellGrp = new RadioButtonGroup<>();
//        radioDiv.add(buySellGrp);
//        buySellGrp.setItems("Buy", "Sell");
//        buySellGrp.setValue("Buy");
//        fromCurFld.addValueChangeListener(f -> {
//            if (f.getValue() == null) {
//                return;
//            }
//            fromAccountFld.setCurrency(f.getValue().getId());
//            amountFld.setPrefixComponent(new Span(f.getValue().getCode()));
//            exchange();
//        });
//        toCurFld.addValueChangeListener(f -> {
//            if (f.getValue() == null) {
//                return;
//            }
//            toAccountFld.setCurrency(f.getValue().getId());
//            convertedAmountFld.setPrefixComponent(new Span(f.getValue().getCode()));
//            exchange();
//        });
//        buySellGrp.addValueChangeListener(f -> {
//            exchange();
//        });
//        fxFld.addValueChangeListener(f -> {
//            if (f.getValue() == null) {
//                fxFld.setValue(BigDecimal.ONE);
//                return;
//            }
//            if (amountFld.getValue() != null && fxFld.getValue() != null) {
//                convertedAmountFld.setValue(amountFld.getValue().divide(fxFld.getValue(), 6, RoundingMode.HALF_UP));
//            }
//        });

        myFormLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0px", 1), new FormLayout.ResponsiveStep("500px", 4));

        vaadinFormLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0px", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP), new FormLayout.ResponsiveStep("500px", 3, FormLayout.ResponsiveStep.LabelsPosition.TOP));

        grid = new Grid<>();

        grid.addSortListener(f -> GridUtil.onComponentEvent(f, filter));
        grid.addColumn(f -> {
            return getFrom(f) + "-" + getTo(f) + " fx " + Constants.CURRENCY_FORMAT.format(f.getAmountFrom());
        }).setHeader("Buy").setResizable(true).setSortable(true).setId("c.amountFrom");
        grid.addColumn(f -> {
            return getFrom(f) + "-" + getTo(f) + " fx  " + Constants.CURRENCY_FORMAT.format(f.getAmountTo());
        }).setHeader("Sell").setResizable(true).setSortable(true).setId("c.amountTo");
        grid.addColumn(f -> {
            return Constants.SIMPLE_DATE_TIME_FORMAT_24H.format(DateUtil.convertToDateViaInstant(f.getStartDateTime()));
        }).setHeader("Started").setResizable(true).setSortable(true).setId("c.startDateTime");
        grid.addColumn(f -> {
            return f.getEndDateTime() == null ? "Active" : (Constants.SIMPLE_DATE_TIME_FORMAT_24H.format(DateUtil.convertToDateViaInstant(f.getEndDateTime())));
        }).setHeader("Live").setResizable(true).setSortable(true).setId("c.endDateTime");
        grid.setItems(CurrencyExchangeDataProvider.fetch(filter), CurrencyExchangeDataProvider.count(filter));

        GridExporter<CurrencyExchange> exporter = GridExporter.createFor(grid);
        GridUtil.exportButtons(exporter, grid);
        exporter.setTitle("Currency Exchange");
        exporter.setFileName("Currency_Exchange_" + new SimpleDateFormat("yyyyddMM").format(Calendar.getInstance().getTime()));
        rateGridLayout.add(grid);

        fromAmountFld.setRequiredIndicatorVisible(true);
        toAmountFld.setRequiredIndicatorVisible(true);

        fromAmountFld.addValueChangeListener(f -> {
            if (f.getValue() != null && f.getValue().compareTo(BigDecimal.ZERO) != 0) {
//                toAmountFld.setValue(BigDecimal.ONE.divide(f.getValue(), 4, RoundingMode.HALF_UP));
                toAmountFld.setValue(f.getValue());
            }
        });

        addRateBtn.addClickListener(f -> {
            ConfirmDialog confirmDialog = new ConfirmDialog();
            confirmDialog.setHeader("Confirm addition exchange rate");
            confirmDialog.setText("Are you sure that you want to add this exchange rate? ");
            confirmDialog.setCancelable(true);
            confirmDialog.addConfirmListener(g -> {
                ExchangeRateService productService = ContextProvider.getBean(ExchangeRateService.class);
                String token = AuthenticatedUser.token();

                CurrencyExchangeVO currencyExchangeVO = new CurrencyExchangeVO();
                currencyExchangeVO.setNew(true);
                currencyExchangeVO.setCurrencyToId(currencyToSelect.getValue() == null ? null : currencyToSelect.getValue().getId());
                currencyExchangeVO.setCurrencyFromId(currencyFromSelect.getValue() == null ? null : currencyFromSelect.getValue().getId());
                currencyExchangeVO.setStartDateTime(startPickerFld.getValue());
                currencyExchangeVO.setEndDateTime(endPickerFld.getValue());
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
                CustomNotificationHandler.notify_(new SuccessThrowable());
            });
            confirmDialog.open();

        });

//        exchangeBtn.addClickListener(f -> {
//            if (toCurFld.getValue() == null || fromCurFld.getValue() == null || amountFld.getValue() == null || convertedAmountFld.getValue() == null//
//                    || paymentMethodFrom.getValue() == null || paymentMethodTo.getValue() == null || dateFld.getValue() == null || fxFld.getValue() == null) {//
//                throw new ValidationException("Not all required fields are filled in");
//            }
//
//
//            ExchangeRateService productService = ContextProvider.getBean(ExchangeRateService.class);
//            CurrencyExchangeVO vo = new CurrencyExchangeVO();
//            vo.setCurrencyToId(toCurFld.getValue().getId());
//            vo.setCurrencyFromId(fromCurFld.getValue().getId());
//            vo.setAmountFrom(amountFld.getValue());
//            vo.setAmountTo(convertedAmountFld.getValue());
//            vo.setAccountTo(toAccountFld.getValue().getId());
//            vo.setAccountFrom(fromAccountFld.getValue().getId());
//            vo.setBuySell(buySellGrp.getValue().equalsIgnoreCase("Buy") ? "b" : "s");
//            vo.setRate(fxFld.getValue());
//            vo.setPrevRate(prevFxFld.getValue());
//            vo.setFxId(exchange.getId());
//            vo.setBusinessId(businessId);
//            vo.setLogDate(dateFld.getValue());
//            vo.setPaymentMethodFrom(paymentMethodFrom.getValue().getId());
//            vo.setPaymentMethodTo(paymentMethodTo.getValue().getId());
//            productService.buySell(AuthenticatedUser.token(), vo);
//
//            toCurFld.clear();
//            fromCurFld.clear();
//            amountFld.clear();
//            convertedAmountFld.clear();
//            toAccountFld.clear();
//            fromAccountFld.clear();
//            fxFld.clear();
//
//
//            CustomNotificationHandler.notify_(new SuccessThrowable());
//        });

        currencyFromSelect.addValueChangeListener(f -> {
            currencyToReplicaSelect.setValue(f.getValue());
        });
        currencyToSelect.addValueChangeListener(f -> {
            currencyFromReplicaSelect.setValue(f.getValue());
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

//    private void exchange() {
//        if (fromCurFld.getValue() != null && toCurFld.getValue() != null && dateFld.getValue() != null) {
//            ExchangeRateService exchangeRateService = ContextProvider.getBean(ExchangeRateService.class);
//            try {
//                exchange = exchangeRateService.exchangeResult(fromCurFld.getValue().getCode(), toCurFld.getValue().getCode(), businessId, buySellGrp.getValue().equalsIgnoreCase("Buy") ? "b" : "s", dateFld.getValue(), AuthenticatedUser.token());
//                fxFld.setValue(BigDecimal.valueOf(exchange.getRate()));
//                prevFxFld.setValue(BigDecimal.valueOf(exchange.getRate()));
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }
//    }

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
        String token = AuthenticatedUser.token();
        boolean hasAccess = userAccesService.hasAccess(token, new CurrencyExchangePrivilege(), Privileges.READ);
        if (!hasAccess) {
            UI.getCurrent().navigate(AboutView.class);
        }
        Optional<String> business1 = event.getRouteParameters().get("business");
        if (business1.isPresent()) {
            business = business1.get();
        }
        businessId = Long.valueOf(business);
        filter.setToken(token);
        filter.setBusinessId(businessId);
//        toAccountFld.load(businessId, Reference.EXCHANGE);
//        fromAccountFld.load(businessId, Reference.EXCHANGE);

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
