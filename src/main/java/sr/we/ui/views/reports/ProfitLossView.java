package sr.we.ui.views.reports;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import sr.we.ContextProvider;
import sr.we.data.controller.BusinessService;
import sr.we.data.controller.JournalEntryService;
import sr.we.data.controller.UserAccessService;
import sr.we.demo.about.AboutView;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.*;
import sr.we.shekelflowcore.entity.helper.PagingResult;
import sr.we.shekelflowcore.entity.helper.vo.JournalsEntryVO;
import sr.we.shekelflowcore.enums.ChartOfAccountTypes;
import sr.we.shekelflowcore.enums.ChartOfAccounts;
import sr.we.shekelflowcore.enums.SystemAccounts;
import sr.we.shekelflowcore.enums.TransactionType;
import sr.we.shekelflowcore.security.Privileges;
import sr.we.shekelflowcore.security.privileges.AccountsPrivilege;
import sr.we.shekelflowcore.settings.util.Constants;
import sr.we.shekelflowcore.settings.util.DateUtil;
import sr.we.ui.components.BreadCrumb;
import sr.we.ui.views.MainLayout;

import javax.annotation.security.RolesAllowed;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A Designer generated component for the profit-loss-view template.
 * <p>
 * Designer will add and remove fields with @Id mappings but
 * does not overwrite or otherwise change this file.
 */
@Tag("profit-loss-view")
@JsModule("./src/views/reports/profit-loss-view.ts")
@BreadCrumb(titleKey = "sr.we.reports.profit.loss", parentNavigationTarget = ReportsView.class)
@Route(value = "profit-loss", layout = MainLayout.class)
@RolesAllowed({Role.user, Role.staff, Role.owner, Role.admin})
public class ProfitLossView extends LitTemplate implements BeforeEnterObserver {

    protected final Label dateHeaderLbl;
    private final Tab summary;
    private final Tab details;
    private final Tab usd;
    private final Tab srd;
    private final Tab euro;
    @Id("export-btn")
    protected Button exportBtn;
    @Id("table-layout")
    protected Div tableLayout;
    @Id("report-tab")
    protected Tabs reportTab;
    @Id("income-lbl")
    protected H2 incomeLbl;
    @Id("cgs-lbl")
    protected H2 cgsLbl;
    @Id("oe-lbl")
    protected H2 oeLbl;
    @Id("total")
    protected H2 total;
    @Id("update-report-btn")
    protected Button updateReportBtn;
    @Id("report-type-select")
    protected Select<JournalsEntryVO.ReportType> reportTypeSelect;
    @Id("as-of-date-picker")
    protected DatePicker asOfDatePicker;
    @Id("as-of-date-select")
    protected Select<Long> asOfDateSelect;
    protected String business;

    protected TreeGrid<BalanceSheetView.Item> treeGrid;
    @Id("currency-tabs")
    protected Tabs currencyTabs;
    private List<JournalsEntry> result;
    private List<BalanceSheetView.Item> last, first, second;
    private Map<Long, BigDecimal> fxMap;

    /**
     * Creates a new BalanceSheetView.
     */
    public ProfitLossView() {
        // You can initialise any data required for the connected UI components here.

        reportTab.removeAll();
        summary = new Tab("Summary");
        details = new Tab("Details");
        reportTab.add(summary, details);

        currencyTabs.removeAll();
        usd = new Tab("USD");
        srd = new Tab("SRD");
        euro = new Tab("EURO");
        currencyTabs.add(usd, srd, euro);


        dateHeaderLbl = new Label();

        asOfDateSelect.setItems(2022L);
        asOfDatePicker.addValueChangeListener(f -> {
            if (f.getValue() == null) {
                asOfDatePicker.setValue(LocalDate.now());
                return;
            }
            dateHeaderLbl.setText(Constants.SIMPLE_DATE_FORMAT.format(DateUtil.convertToDateViaInstant(f.getValue())));
        });
        asOfDatePicker.setValue(LocalDate.now());
        reportTypeSelect.setItems(List.of(JournalsEntryVO.ReportType.values()));

        asOfDateSelect.setValue(2022L);
        reportTypeSelect.setValue(JournalsEntryVO.ReportType.ALL);
        treeGrid = new TreeGrid<>();
        treeGrid.addComponentHierarchyColumn(person -> {
            Span span = new Span(person.caption);
            if (person.getChartOfAccounts() != null) {
                span.addClassNames(LumoUtility.FontWeight.BOLD, LumoUtility.TextColor.PRIMARY);
            } else if (person.getChartOfAccountTypes() != null) {
                span.addClassNames(LumoUtility.FontWeight.BOLD, LumoUtility.TextColor.SECONDARY);
            }
            return span;
        }).setHeader("Accounts");

        treeGrid.addComponentColumn(person -> {
            HorizontalLayout horizontalLayout = new HorizontalLayout();
            List<JournalsEntry> values = person.getValues();
            if (values != null) {
                List<JournalsEntry> known = values.stream().filter(f -> f.getCurrencyTo().getCode().equalsIgnoreCase(getSelectedCurrency())).toList();
                List<JournalsEntry> unknown = values.stream().filter(f -> !f.getCurrencyTo().getCode().equalsIgnoreCase(getSelectedCurrency())).toList();
                if (!unknown.isEmpty()) {
                    JournalsEntry journalsEntry = unknown.get(0);
                    BigDecimal reduce = unknown.stream().map(getJournalsEntryBigDecimalFunction1()).reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal reduce1 = known.stream().map(getJournalsEntryBigDecimalFunction()).reduce(BigDecimal.ZERO, BigDecimal::add);
                    Span span = new Span("(" + journalsEntry.getCurrencyTo().getCode() + " " + Constants.CURRENCY_FORMAT.format(reduce) + "&" + getSelectedCurrency() + " " + Constants.CURRENCY_FORMAT.format(reduce1) + ")");
                    horizontalLayout.add(span);
                }
            }
            Span span = new Span(getSelectedCurrency() + " " + Constants.CURRENCY_FORMAT.format(person.getAmount()));
            if (person.getChartOfAccounts() != null) {
                span.addClassNames(LumoUtility.FontWeight.BOLD, LumoUtility.TextColor.PRIMARY);
            } else if (person.getChartOfAccountTypes() != null) {
                span.addClassNames(LumoUtility.FontWeight.BOLD, LumoUtility.TextColor.SECONDARY);
            }
            horizontalLayout.add(span);
            return horizontalLayout;
        }).setHeader(dateHeaderLbl).setTextAlign(ColumnTextAlign.END).setResizable(true);
        tableLayout.add(treeGrid);
    }

    public static String getLocation(String business) {
        return MainLayout.getLocation(business) + "/profit-loss";
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        UserAccessService userAccesService = ContextProvider.getBean(UserAccessService.class);
        boolean hasAccess = userAccesService.hasAccess(AuthenticatedUser.token(), new AccountsPrivilege(), Privileges.READ);
        if (!hasAccess) {
            UI.getCurrent().navigate(AboutView.class);
        }
        Optional<String> business1 = event.getRouteParameters().get("business");
        business1.ifPresent(s -> business = s);

        BusinessService businessService = ContextProvider.getBean(BusinessService.class);
        Business business2 = businessService.get(Long.valueOf(business), AuthenticatedUser.token());
        Currency currency = business2.getCurrency();
        if (currency.getCode().equalsIgnoreCase("SRD")) {
            currencyTabs.setSelectedTab(srd);
        } else if (currency.getCode().equalsIgnoreCase("USD")) {
            currencyTabs.setSelectedTab(usd);
        } else if (currency.getCode().equalsIgnoreCase("EURO")) {
            currencyTabs.setSelectedTab(euro);
        }

        refresh();

        currencyTabs.addSelectedChangeListener(f -> {
            refresh();
        });
        reportTab.addSelectedChangeListener(f -> {
            refresh();
            if (f.getSelectedTab().equals(details)) {
                if (first != null && !first.isEmpty()) treeGrid.expand(first);
                if (second != null && !second.isEmpty()) treeGrid.expand(second);
                if (last != null && !last.isEmpty()) treeGrid.expand(last);
            }
        });
        updateReportBtn.addClickListener(f -> {
            refresh();
        });
    }

    private void refresh() {
        first = new ArrayList<>();
        second = new ArrayList<>();
        last = new ArrayList<>();
        JournalEntryService journalEntryService = ContextProvider.getBean(JournalEntryService.class);
        JournalsEntryVO vo = new JournalsEntryVO();
        vo.setBusinessId(Long.valueOf(business));
        vo.setAsOfDate(asOfDatePicker.getValue());
        PagingResult<JournalsEntry> list = journalEntryService.list(vo, AuthenticatedUser.token());

        result = list.getResult();
        if (treeGrid.getDataProvider() != null) {
            treeGrid.setItems(new ArrayList<BalanceSheetView.Item>(), this::getChildren);
        }
        if (result != null && !result.isEmpty()) {
            Map<ChartOfAccounts, List<JournalsEntry>> collect = result.stream().filter(f -> f.getCurrencyFrom().getCode().equalsIgnoreCase(getSelectedCurrency()) && //
                            (f.getAccount().getAccountType().getType().compareTo(ChartOfAccounts.INC) == 0 || //
                                    f.getAccount().getAccountType().getCode().compareTo(ChartOfAccountTypes.CGS) == 0 || //
                                    f.getAccount().getAccountType().getCode().compareTo(ChartOfAccountTypes.OE) == 0))//
                    .collect(Collectors.groupingBy(f -> f.getAccount().getAccountType().getType()));
            List<BalanceSheetView.Item> items = collect.entrySet().stream().map(f -> {
                ChartOfAccounts key = f.getKey();
                List<JournalsEntry> value = f.getValue();
                BigDecimal reduce = value.stream().map(getJournalsEntryBigDecimalFunction()).reduce(BigDecimal.ZERO, BigDecimal::add);
                return new BalanceSheetView.Item("Total " + key.getCaption(), reduce, key);
            }).toList();
            first.addAll(items);
            treeGrid.setItems(items, this::getChildren);
        }

        BigDecimal income = result.stream().filter(f -> f.getAccount().getAccountType().getType().compareTo(ChartOfAccounts.INC) == 0 && f.getCurrencyFrom().getCode().equalsIgnoreCase(getSelectedCurrency()))//
                .map(getJournalsEntryBigDecimalFunction()).reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal cgs = result.stream().filter(f -> f.getAccount().getAccountType().getCode().compareTo(ChartOfAccountTypes.CGS) == 0 && f.getCurrencyFrom().getCode().equalsIgnoreCase(getSelectedCurrency()))//
                .map(getJournalsEntryBigDecimalFunction()).reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal oe = result.stream().filter(f -> (f.getAccount().getAccountType().getCode().compareTo(ChartOfAccountTypes.OE) == 0
                        || f.getAccount().getAccountType().getCode().compareTo(ChartOfAccountTypes.PE) == 0
                ||f.getAccount().getAccountType().getCode().compareTo(ChartOfAccountTypes.UE) == 0
                )
                        && f.getCurrencyFrom().getCode().equalsIgnoreCase(getSelectedCurrency()))//
                .map(getJournalsEntryBigDecimalFunction()).reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal gofe = result.stream().filter(f -> f.getAccount().getSystemId() != null && f.getAccount().getSystemId().compareTo(SystemAccounts.GOFE.getId()) == 0 && f.getCurrencyFrom().getCode().equalsIgnoreCase(getSelectedCurrency()))//
                .map(getJournalsEntryBigDecimalFunction()).reduce(BigDecimal.ZERO, BigDecimal::add);

        income = income.subtract(gofe);


        BigDecimal sum = income.subtract(cgs).subtract(oe);


        incomeLbl.setText(getSelectedCurrency() + " " + Constants.CURRENCY_FORMAT.format(income));
        cgsLbl.setText(getSelectedCurrency() + " " + Constants.CURRENCY_FORMAT.format(cgs));
        oeLbl.setText(getSelectedCurrency() + " " + Constants.CURRENCY_FORMAT.format(oe));
        total.setText(getSelectedCurrency() + " " + Constants.CURRENCY_FORMAT.format(sum));
    }

    private Function<JournalsEntry, BigDecimal> getJournalsEntryBigDecimalFunction1() {
        return g -> g.getAccount().getAccountType().getType().getPlusMin(g.getDebCred()).compareTo(TransactionType.WITHDRAWAL) == 0//
                ? g.getConvertedAmount().multiply(BigDecimal.valueOf(-1)) : g.getConvertedAmount();
    }

    private Function<JournalsEntry, BigDecimal> getJournalsEntryBigDecimalFunction() {
        return g -> {
//            if (g.getAccount().getAccountType().getType().compareTo(ChartOfAccounts.ASSETS) == 0 || g.getAccount().getAccountType().getType().compareTo(ChartOfAccounts.INC) == 0 || g.getAccount().getAccountType().getType().compareTo(ChartOfAccounts.EXP) == 0 && g.getCurrencyTo().getId().compareTo(g.getCurrencyFrom().getId()) != 0) {
//                if (fxMap == null) {
//                    fxMap = new HashMap<>();
//                }
//                BigDecimal rate = fxMap.get(g.getCurrencyTo().getId());
//                if (rate == null) {
//                    ExchangeRateService exchangeRateService = ContextProvider.getBean(ExchangeRateService.class);
//                    try {
//                        rate = exchangeRateService.exchange( g.getCurrencyFrom().getCode(),g.getCurrencyTo().getCode(), Long.valueOf(business), "b",LocalDate.now(), AuthenticatedUser.token());
//                        fxMap.put(g.getCurrencyTo().getId(), rate);
//
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//                BigDecimal multiply = rate.multiply(g.getAccount().getAccountType().getType().getPlusMin(g.getDebCred()).compareTo(TransactionType.WITHDRAWAL) == 0//
//                        ? g.getConvertedAmount().multiply(BigDecimal.valueOf(-1)) : g.getConvertedAmount());
//                return multiply;
//            }
            BigDecimal bigDecimal = g.getAccount().getAccountType().getType().getPlusMin(g.getDebCred()).compareTo(TransactionType.WITHDRAWAL) == 0//
                    ? g.getAmount().multiply(BigDecimal.valueOf(-1)) : g.getAmount();
            return bigDecimal;
        };
    }

    private String getSelectedCurrency() {
        if (currencyTabs.getSelectedTab().equals(srd)) {
            return "SRD";
        } else if (currencyTabs.getSelectedTab().equals(usd)) {
            return "USD";
        }
        return "EURO";
    }

    protected List<BalanceSheetView.Item> getChildren(BalanceSheetView.Item journalsEntry) {
        boolean isSummary = reportTab.getSelectedTab().equals(summary);
        if (journalsEntry.getChartOfAccountTypes() == null && journalsEntry.getChartOfAccounts() == null && isSummary) {
            return new ArrayList<>();
        }
        boolean isDetail = reportTab.getSelectedTab().equals(details);
        if (journalsEntry.getChartOfAccounts() == null && journalsEntry.getChartOfAccountTypes() == null && isDetail) {
            return new ArrayList<>();
        }
//        JournalEntryService journalEntryService = ContextProvider.getBean(JournalEntryService.class);
//        JournalsEntryVO vo = new JournalsEntryVO();
//        vo.setBusinessId(Long.valueOf(business));
//        vo.setAsOfDate(asOfDatePicker.getValue());
//        vo.setChartOfAccounts(journalsEntry.getChartOfAccounts());
//        PagingResult<JournalsEntry> list =
        List<JournalsEntry> result = null;
        if (journalsEntry.getChartOfAccounts() != null) {
            result = this.result.stream().filter(g -> g.getAccount().getAccountType().getType().compareTo(journalsEntry.getChartOfAccounts()) == 0).toList();
            Map<ChartOfAccountTypes, List<JournalsEntry>> collect = result.stream().filter(f -> f.getCurrencyFrom().getCode().equalsIgnoreCase(getSelectedCurrency())).collect(Collectors.groupingBy(f -> f.getAccount().getAccountType().getCode()));
            List<BalanceSheetView.Item> items = collect.entrySet().stream().map(f -> {
                ChartOfAccountTypes key = f.getKey();
                List<JournalsEntry> value = f.getValue();
                BigDecimal reduce = value.stream().map(getJournalsEntryBigDecimalFunction()).reduce(BigDecimal.ZERO, BigDecimal::add);
                return new BalanceSheetView.Item("Total " + key.getCaption(), reduce, key);
            }).toList();
            second.addAll(items);
            return items;
        } else {
            result = this.result.stream().filter(g -> g.getAccount().getAccountType().getCode().compareTo(journalsEntry.getChartOfAccountTypes()) == 0).toList();
            Map<Account, List<JournalsEntry>> collect = result.stream()/*.filter(f -> f.getCurrencyFrom().getCode().equalsIgnoreCase(getSelectedCurrency()))*/.collect(Collectors.groupingBy(JournalsEntry::getAccount));
            List<BalanceSheetView.Item> items = collect.entrySet().stream().map(f -> {
                Account key = f.getKey();
                List<JournalsEntry> value = f.getValue();
                BigDecimal reduce = value.stream().map(getJournalsEntryBigDecimalFunction()).reduce(BigDecimal.ZERO, BigDecimal::add);
                return new BalanceSheetView.Item("Total " + key.getName(), reduce, value);
            }).toList();
            last.addAll(items);
            return items;
        }


    }

    public static class Item {
        protected String caption;
        protected BigDecimal amount;
        protected ChartOfAccounts chartOfAccounts;
        protected ChartOfAccountTypes chartOfAccountTypes;
        private List<JournalsEntry> values;

        public Item(String caption, BigDecimal amount, ChartOfAccountTypes chartOfAccountTypes) {
            this.caption = caption;
            this.amount = amount;
            this.chartOfAccountTypes = chartOfAccountTypes;
        }

        public Item(String caption, BigDecimal amount, List<JournalsEntry> values) {
            this.caption = caption;
            this.amount = amount;
            this.values = values;
        }

        public Item(String caption, BigDecimal amount, ChartOfAccounts chartOfAccounts) {
            this.caption = caption;
            this.amount = amount;
            this.chartOfAccounts = chartOfAccounts;
        }

        public ChartOfAccounts getChartOfAccounts() {
            return chartOfAccounts;
        }

        public void setChartOfAccounts(ChartOfAccounts chartOfAccounts) {
            this.chartOfAccounts = chartOfAccounts;
        }

        public String getCaption() {
            return caption;
        }

        public void setCaption(String caption) {
            this.caption = caption;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public ChartOfAccountTypes getChartOfAccountTypes() {
            return chartOfAccountTypes;
        }

        public void setChartOfAccountTypes(ChartOfAccountTypes chartOfAccountTypes) {
            this.chartOfAccountTypes = chartOfAccountTypes;
        }

        public List<JournalsEntry> getValues() {
            return values;
        }

        public void setValues(List<JournalsEntry> values) {
            this.values = values;
        }
    }

}
