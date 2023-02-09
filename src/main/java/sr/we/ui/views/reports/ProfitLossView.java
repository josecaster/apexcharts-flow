package sr.we.ui.views.reports;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.FormItem;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.FooterRow;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
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
import org.apache.commons.lang3.StringUtils;
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
import java.time.Month;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    private final Tab summary;
    private final Tab details;
    private final Tab usd;
    private final Tab srd;
    private final Tab euro;
    private final Label grossProfitLbl,grossProfitCompareLbl,grossProfitDiffLbl, netProfitLbl,netProfitCompareLbl,netProfitDiffLbl;
    protected Label dateHeaderLbl,compareDateHeaderLbl;
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
    protected Select<Year> year;
    protected String business;
    protected TreeGrid<ProfitLossView.Item> grossProfitGrid, netProfitGrid;
    @Id("currency-tabs")
    protected Tabs currencyTabs;
    @Id("compare-date-select")
    private Select<Year> yearCompare;
    private List<JournalsEntry> result;
    private List<ProfitLossView.Item> grossLast, grossFirst, grossSecond;
    private List<ProfitLossView.Item> netLast, netFirst, netSecond;
    private Map<Long, BigDecimal> fxMap;
    @Id("vaadinFormLayout")
    private FormLayout vaadinFormLayout;
    @Id("start-from-date-picker")
    private DatePicker startFromDatePicker;

    @Id("compare-start-from-date-picker")
    private DatePicker compareStartFromDatePicker;
    @Id("compare-as-of-date-picker")
    private DatePicker compareAsOfDatePicker;
    @Id("compare-item")
    private FormItem compareItem;
    @Id("compare-btn")
    private Button compareBtn;
    @Id("single-summary")
    private HorizontalLayout singleSummary;

    /**
     * Creates a new ProfitLossView.
     */
    public ProfitLossView() {
        // You can initialise any data required for the connected UI components here.

        vaadinFormLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0px", 1), new FormLayout.ResponsiveStep("100px", 4));


        UI current = UI.getCurrent();
        exportBtn.addClickListener(f -> {
//            CompletableFuture<String> completableFuture = HTML2CANVAS.takeScreenShot(ProfitLossView.this.tableLayout.getElement());
//            completableFuture.thenRun(() -> {
//                try {
//                    String src = completableFuture.get();
////                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(src.getBytes());
//                    current.access(() -> {
//
//                        Image image = new Image(src, "adfs");
//                        image.setWidth("1000px");
//                        Anchor docLink = new Anchor(/*new StreamResource("screenshot.png", () -> {
//                            return byteArrayInputStream;
//                        })*/src, "Download");
//                        docLink.getElement().setAttribute("download", "Screenshot_Profit_Loss_" + dateHeaderLbl.getText());
//
//                        Dialog dialog = new Dialog(image, docLink);
////                        dialog.setSizeFull();
//                        dialog.open();
//                    });
//                } catch (InterruptedException | ExecutionException ignored) {
//                }
//            });
        });

        reportTab.removeAll();
        summary = new Tab("Summary");
        details = new Tab("Details");
        reportTab.add(summary, details);

        currencyTabs.removeAll();
        usd = new Tab("USD");
        srd = new Tab("SRD");
        euro = new Tab("EURO");
        currencyTabs.add(usd, srd, euro);
        currencyTabs.setVisible(false);


        dateHeaderLbl = new Label();
        dateHeaderLbl.addClassNames(LumoUtility.FontWeight.BOLD);
        compareDateHeaderLbl = new Label();
        compareDateHeaderLbl.addClassNames(LumoUtility.FontWeight.BOLD);

        Year now = Year.now();
        year.setItems(now.minusYears(5), now.minusYears(4), now.minusYears(3), now.minusYears(2), now.minusYears(1), now);
        yearCompare.setItems(now.minusYears(5), now.minusYears(4), now.minusYears(3), now.minusYears(2), now.minusYears(1), now);
        asOfDatePicker.addValueChangeListener(f -> {
            if (asOfDatePicker.getValue() == null || startFromDatePicker.getValue() == null) {
                year.setValue(now);
                return;
            }
            dateHeaderLbl.setText(Constants.SIMPLE_DATE_FORMAT.format(DateUtil.convertToDateViaInstant(startFromDatePicker.getValue())) + " | " + //
                    Constants.SIMPLE_DATE_FORMAT.format(DateUtil.convertToDateViaInstant(f.getValue())));//
        });
        startFromDatePicker.addValueChangeListener(f -> {
            if (asOfDatePicker.getValue() == null || startFromDatePicker.getValue() == null) {
                year.setValue(now);
                return;
            }
            dateHeaderLbl.setText(Constants.SIMPLE_DATE_FORMAT.format(DateUtil.convertToDateViaInstant(startFromDatePicker.getValue())) + " | " + //
                    Constants.SIMPLE_DATE_FORMAT.format(DateUtil.convertToDateViaInstant(asOfDatePicker.getValue())));//
        });
        compareAsOfDatePicker.addValueChangeListener(f -> {
            if (compareAsOfDatePicker.getValue() == null || compareStartFromDatePicker.getValue() == null) {
                yearCompare.setValue(now.minusYears(1));
                return;
            }
            compareDateHeaderLbl.setText(Constants.SIMPLE_DATE_FORMAT.format(DateUtil.convertToDateViaInstant(compareStartFromDatePicker.getValue())) + " | " + //
                    Constants.SIMPLE_DATE_FORMAT.format(DateUtil.convertToDateViaInstant(f.getValue())));//
        });
        compareStartFromDatePicker.addValueChangeListener(f -> {
            if (compareAsOfDatePicker.getValue() == null || compareStartFromDatePicker.getValue() == null) {
                yearCompare.setValue(now.minusYears(1));
                return;
            }
            compareDateHeaderLbl.setText(Constants.SIMPLE_DATE_FORMAT.format(DateUtil.convertToDateViaInstant(compareStartFromDatePicker.getValue())) + " | " + //
                    Constants.SIMPLE_DATE_FORMAT.format(DateUtil.convertToDateViaInstant(compareAsOfDatePicker.getValue())));//
        });

        compareItem.setVisible(false);
        compareBtn.addClickListener(f -> {
            boolean visible = compareItem.isVisible();
            compareItem.setVisible(!visible);
            grossProfitGrid.getColumnByKey("compare").setVisible(!visible);
            netProfitGrid.getColumnByKey("compare").setVisible(!visible);
            grossProfitGrid.getColumnByKey("difference").setVisible(!visible);
            netProfitGrid.getColumnByKey("difference").setVisible(!visible);
            if (!visible) {
                compareBtn.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
                compareBtn.addThemeVariants(ButtonVariant.LUMO_ERROR);
                compareBtn.setText("Remove date range");
            } else {
                compareBtn.setText("Compare date range");
                compareBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
                compareBtn.removeThemeVariants(ButtonVariant.LUMO_ERROR);
            }
        });

//        startFromDatePicker.setValue(Year.now().atMonth(Month.JANUARY).atDay(1));
        reportTypeSelect.setItems(List.of(JournalsEntryVO.ReportType.values()));


        year.addValueChangeListener(f -> {
//            refresh();
            LocalDate localDate = year.getValue().atMonth(Month.DECEMBER).atEndOfMonth();
            LocalDate startDate = year.getValue().atMonth(Month.JANUARY).atDay(1);
            asOfDatePicker.setValue(localDate);
            startFromDatePicker.setValue(startDate);
//            dateHeaderLbl.setText(Constants.SIMPLE_DATE_FORMAT.format(DateUtil.convertToDateViaInstant(localDate)));
        });
        yearCompare.addValueChangeListener(f -> {
//            refresh();
            LocalDate localDate = yearCompare.getValue().atMonth(Month.DECEMBER).atEndOfMonth();
            LocalDate startDate = yearCompare.getValue().atMonth(Month.JANUARY).atDay(1);
            compareAsOfDatePicker.setValue(localDate);
            compareStartFromDatePicker.setValue(startDate);
//            dateHeaderLbl.setText(Constants.SIMPLE_DATE_FORMAT.format(DateUtil.convertToDateViaInstant(localDate)));
        });
        year.setValue(now);
        yearCompare.setValue(now.minusYears(1));
        reportTypeSelect.setValue(JournalsEntryVO.ReportType.ALL);

//
        Span account = new Span("Account");
        account.addClassNames(LumoUtility.FontWeight.BOLD);
        Span diff = new Span("Difference");
        diff.addClassNames(LumoUtility.FontWeight.BOLD);
//        HorizontalLayout acc = new HorizontalLayout(account, dateHeaderLbl);
//        acc.setWidthFull();
//        acc.addClassNames(LumoUtility.Background.BASE);
//        acc.setPadding(true);
//        dateHeaderLbl.getElement().getStyle().set("margin-left", "auto");

        tableLayout.add(grossProfitGrid = itemTreeGrid());


        grossProfitGrid.getColumnByKey("account").setHeader(account);
        grossProfitGrid.getColumnByKey("main").setHeader(dateHeaderLbl);
        grossProfitGrid.getColumnByKey("compare").setHeader(compareDateHeaderLbl);
        grossProfitGrid.getColumnByKey("difference").setHeader(diff);

        grossProfitLbl = new Label("-");
        grossProfitCompareLbl = new Label("-");
        grossProfitDiffLbl = new Label("-");
        grossProfitLbl.addClassNames(LumoUtility.FontWeight.BOLD,LumoUtility.TextColor.PRIMARY, LumoUtility.FontSize.MEDIUM);
        grossProfitCompareLbl.addClassNames(LumoUtility.FontWeight.BOLD,LumoUtility.TextColor.PRIMARY, LumoUtility.FontSize.MEDIUM);
        grossProfitDiffLbl.addClassNames(LumoUtility.FontWeight.BOLD,LumoUtility.TextColor.PRIMARY, LumoUtility.FontSize.MEDIUM);
        Span span = new Span("Gross Profit");
        span.addClassNames(LumoUtility.FontWeight.BOLD);
        FooterRow footerRow1 = grossProfitGrid.appendFooterRow();
        footerRow1.getCell(grossProfitGrid.getColumnByKey("account")).setComponent(span);
        footerRow1.getCell(grossProfitGrid.getColumnByKey("main")).setComponent(aligntRight(grossProfitLbl));
        footerRow1.getCell(grossProfitGrid.getColumnByKey("compare")).setComponent(aligntRight(grossProfitCompareLbl));
        footerRow1.getCell(grossProfitGrid.getColumnByKey("difference")).setComponent(aligntRight(grossProfitDiffLbl));
//        HorizontalLayout gross_profit = new HorizontalLayout(span, grossProfitLbl);
//        gross_profit.setWidthFull();
//        gross_profit.addClassNames(LumoUtility.Background.BASE);
//        gross_profit.setPadding(true);
//        grossProfitLbl.getElement().getStyle().set("margin-left", "auto");
        tableLayout.add(/*gross_profit,*/ netProfitGrid = itemTreeGrid());

        netProfitLbl = new Label("-");
        netProfitCompareLbl = new Label("-");
        netProfitDiffLbl = new Label("-");
        netProfitLbl.addClassNames(LumoUtility.FontWeight.BOLD,LumoUtility.TextColor.PRIMARY, LumoUtility.FontSize.MEDIUM);
        netProfitCompareLbl.addClassNames(LumoUtility.FontWeight.BOLD,LumoUtility.TextColor.PRIMARY, LumoUtility.FontSize.MEDIUM);
        netProfitDiffLbl.addClassNames(LumoUtility.FontWeight.BOLD,LumoUtility.TextColor.PRIMARY, LumoUtility.FontSize.MEDIUM);
        Span net = new Span("Net Profit");
        net.addClassNames(LumoUtility.FontWeight.BOLD);
        FooterRow footerRow = netProfitGrid.appendFooterRow();
        footerRow.getCell(netProfitGrid.getColumnByKey("account")).setComponent(net);
        footerRow.getCell(netProfitGrid.getColumnByKey("main")).setComponent(aligntRight(netProfitLbl));
        footerRow.getCell(netProfitGrid.getColumnByKey("compare")).setComponent(aligntRight(netProfitCompareLbl));
        footerRow.getCell(netProfitGrid.getColumnByKey("difference")).setComponent(aligntRight(netProfitDiffLbl));
//        HorizontalLayout net_profit = new HorizontalLayout(net, netProfitLbl);
//        net_profit.setWidthFull();
//        net_profit.setPadding(true);
//        net_profit.addClassNames(LumoUtility.Background.BASE);
//        netProfitLbl.getElement().getStyle().set("margin-left", "auto");
//        tableLayout.add(net_profit);
    }

    private HorizontalLayout aligntRight(Label label) {
        HorizontalLayout component = new HorizontalLayout(label);
        component.setAlignItems(FlexComponent.Alignment.END);
        component.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        return component;
    }

    public static String getLocation(String business) {
        return MainLayout.getLocation(business) + "/profit-loss";
    }

    static Predicate<JournalsEntry> cgsFilter() {
        return f -> (f.getAccount().getAccountType().getType().compareTo(ChartOfAccounts.EXP) == 0 && f.getAccount().getAccountType().getCode().compareTo(ChartOfAccountTypes.CGS) == 0);
    }

    static Predicate<JournalsEntry> expensesFilter() {
        return f -> (f.getAccount().getAccountType().getType().compareTo(ChartOfAccounts.EXP) == 0 && f.getAccount().getAccountType().getCode().compareTo(ChartOfAccountTypes.CGS) != 0);
    }

    static Predicate<JournalsEntry> incomeFilter() {
        return f -> (f.getAccount().getAccountType().getType().compareTo(ChartOfAccounts.INC) == 0);
    }

    private TreeGrid<Item> itemTreeGrid() {
        TreeGrid<Item> grid = new TreeGrid<>();
        grid.setAllRowsVisible(true);
        grid.addClassName("resonate");
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER,GridVariant.LUMO_WRAP_CELL_CONTENT);
        grid.addComponentHierarchyColumn(item -> {
            Span span = new Span(item.caption);
            if (item.getChartOfAccounts() != null) {
                span.addClassNames(LumoUtility.FontWeight.BOLD, LumoUtility.TextColor.PRIMARY);
            } else if (item.getChartOfAccountTypes() != null) {
                span.addClassNames(LumoUtility.FontWeight.BOLD, LumoUtility.TextColor.SECONDARY);
            }
            return span;
        }).setKey("account");

        grid.addComponentColumn(item -> {
            HorizontalLayout horizontalLayout = new HorizontalLayout();
            List<JournalsEntry> values = item.getValues();
            if (values != null) {
                values = values.stream().filter(filterMainPeriod()).toList();
                List<JournalsEntry> known = values.stream().filter(f -> f.getCurrencyTo().getCode().equalsIgnoreCase(getSelectedCurrency())).toList();
                List<JournalsEntry> unknown = values.stream().filter(f -> !f.getCurrencyTo().getCode().equalsIgnoreCase(getSelectedCurrency())).toList();
                if (!unknown.isEmpty()) {
                    JournalsEntry journalsEntry = unknown.get(0);
                    BigDecimal reduce = unknown.stream().map(getJournalsEntryBigDecimalFunction1()).reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal reduce1 = known.stream().map(getJournalsEntryBigDecimalFunction()).reduce(BigDecimal.ZERO, BigDecimal::add);
                    Span span = new Span("(" + journalsEntry.getCurrencyTo().getCode() + " " + Constants.CURRENCY_FORMAT.format(reduce) + "&" + getSelectedCurrency() + " " + Constants.CURRENCY_FORMAT.format(reduce1) + ")");
                    if (journalsEntry.getAccount().getCurrency() != null) {
                        horizontalLayout.add(span);
                    }
                }
            }
            Span span = new Span();
            span.add(getSelectedCurrency() + " ");
            span.add(Constants.CURRENCY_FORMAT.format(item.getAmount()));
            if (item.getChartOfAccounts() != null) {
                span.addClassNames(LumoUtility.FontWeight.BOLD, LumoUtility.TextColor.PRIMARY);
            } else if (item.getChartOfAccountTypes() != null) {
                span.addClassNames(LumoUtility.FontWeight.BOLD, LumoUtility.TextColor.SECONDARY);
            }
            horizontalLayout.setAlignItems(FlexComponent.Alignment.END);
            horizontalLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
            horizontalLayout.add(span);
            return horizontalLayout;
        }).setTextAlign(ColumnTextAlign.END).setResizable(true).setKey("main");
        Grid.Column<Item> compare = grid.addComponentColumn(item -> {
            HorizontalLayout horizontalLayout = new HorizontalLayout();
            List<JournalsEntry> values = item.getValues();
            if (values != null) {
                values = values.stream().filter(filterComparePeriod()).toList();
                List<JournalsEntry> known = values.stream().filter(f -> f.getCurrencyTo().getCode().equalsIgnoreCase(getSelectedCurrency())).toList();
                List<JournalsEntry> unknown = values.stream().filter(f -> !f.getCurrencyTo().getCode().equalsIgnoreCase(getSelectedCurrency())).toList();
                if (!unknown.isEmpty()) {
                    JournalsEntry journalsEntry = unknown.get(0);
                    BigDecimal reduce = unknown.stream().map(getJournalsEntryBigDecimalFunction1()).reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal reduce1 = known.stream().map(getJournalsEntryBigDecimalFunction()).reduce(BigDecimal.ZERO, BigDecimal::add);
                    Span span = new Span("(" + journalsEntry.getCurrencyTo().getCode() + " " + Constants.CURRENCY_FORMAT.format(reduce) + "&" + getSelectedCurrency() + " " + Constants.CURRENCY_FORMAT.format(reduce1) + ")");
                    if (journalsEntry.getAccount().getCurrency() != null) {
                        horizontalLayout.add(span);
                    }
                }
            }
            Span span = new Span();
            span.add(getSelectedCurrency() + " ");
            span.add(Constants.CURRENCY_FORMAT.format(item.getAmountCompare()));
            if (item.getChartOfAccounts() != null) {
                span.addClassNames(LumoUtility.FontWeight.BOLD, LumoUtility.TextColor.PRIMARY);
            } else if (item.getChartOfAccountTypes() != null) {
                span.addClassNames(LumoUtility.FontWeight.BOLD, LumoUtility.TextColor.SECONDARY);
            }
            horizontalLayout.setAlignItems(FlexComponent.Alignment.END);
            horizontalLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
            horizontalLayout.add(span);
            return horizontalLayout;
        })/*.setHeader("Compare")*/;
        compare.setKey("compare");
        compare.setVisible(false);
        Grid.Column<Item> difference = grid.addComponentColumn(item -> {
            HorizontalLayout horizontalLayout = new HorizontalLayout();
            Span span = new Span();
            span.add(getSelectedCurrency() + " ");
            BigDecimal subtract = item.getAmount().subtract(item.getAmountCompare());
            span.add(Constants.CURRENCY_FORMAT.format(subtract));
            if (subtract.compareTo(BigDecimal.ZERO) == 0) {
                span.addClassNames(LumoUtility.FontWeight.BOLD, LumoUtility.TextColor.PRIMARY);
            } else if (subtract.compareTo(BigDecimal.ZERO) > 0) {
                if((item.getChartOfAccounts() != null && item.getChartOfAccounts().compareTo(ChartOfAccounts.INC) == 0) || (item.getChartOfAccountTypes() != null && item.getChartOfAccountTypes().getChartOfAccounts().compareTo(ChartOfAccounts.INC) == 0) || (item.getAccount() != null && item.getAccount().getAccountType().getType().compareTo(ChartOfAccounts.INC) == 0)) {
                    span.addClassNames(LumoUtility.FontWeight.BOLD, LumoUtility.TextColor.SUCCESS);
                } else {
                    span.addClassNames(LumoUtility.FontWeight.BOLD, LumoUtility.TextColor.ERROR);
                }
            } else {
                if((item.getChartOfAccounts() != null && item.getChartOfAccounts().compareTo(ChartOfAccounts.INC) == 0) || (item.getChartOfAccountTypes() != null && item.getChartOfAccountTypes().getChartOfAccounts().compareTo(ChartOfAccounts.INC) == 0) || (item.getAccount() != null && item.getAccount().getAccountType().getType().compareTo(ChartOfAccounts.INC) == 0)) {
                    span.addClassNames(LumoUtility.FontWeight.BOLD, LumoUtility.TextColor.ERROR);
                } else {
                    span.addClassNames(LumoUtility.FontWeight.BOLD, LumoUtility.TextColor.SUCCESS);
                }
            }
            horizontalLayout.setAlignItems(FlexComponent.Alignment.END);
            horizontalLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
            horizontalLayout.add(span);
            return horizontalLayout;
        })/*.setHeader("Difference")*/;
        difference.setKey("difference");
        difference.setVisible(false);
        return grid;
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
                if (grossFirst != null && !grossFirst.isEmpty()) grossProfitGrid.expand(grossFirst);
                if (grossSecond != null && !grossSecond.isEmpty()) grossProfitGrid.expand(grossSecond);
                if (grossLast != null && !grossLast.isEmpty()) grossProfitGrid.expand(grossLast);
                if (netFirst != null && !netFirst.isEmpty()) netProfitGrid.expand(netFirst);
                if (netSecond != null && !netSecond.isEmpty()) netProfitGrid.expand(netSecond);
                if (netLast != null && !netLast.isEmpty()) netProfitGrid.expand(netLast);
            }
        });
        updateReportBtn.addClickListener(f -> {
            refresh();
        });
    }

    private void refresh() {
        grossFirst = new ArrayList<>();
        grossSecond = new ArrayList<>();
        grossLast = new ArrayList<>();

        netFirst = new ArrayList<>();
        netSecond = new ArrayList<>();
        netLast = new ArrayList<>();

        JournalEntryService journalEntryService = ContextProvider.getBean(JournalEntryService.class);
        JournalsEntryVO vo = new JournalsEntryVO();
        vo.setBusinessId(Long.valueOf(business));
        vo.setAsOfDate(asOfDatePicker.getValue());
        vo.setStartDate(startFromDatePicker.getValue());
        if (compareItem.isVisible()) {
            vo.setCompareAsOfDate(compareAsOfDatePicker.getValue());
            vo.setCompareStartDate(compareStartFromDatePicker.getValue());
        }
        PagingResult<JournalsEntry> list = journalEntryService.list(vo, AuthenticatedUser.token());
        result = list.getResult();


        if (grossProfitGrid.getDataProvider() != null) {
            grossProfitGrid.setItems(new ArrayList<ProfitLossView.Item>(), f -> getChildren(f, grossSecond, grossLast));
        }
        if (netProfitGrid.getDataProvider() != null) {
            netProfitGrid.setItems(new ArrayList<ProfitLossView.Item>(), f -> getChildren(f, netSecond, netLast));
        }
        if (result != null && !result.isEmpty()) {
            // income
            Stream<JournalsEntry> incomeStream = result.stream().filter(incomeFilter());
            Map<ChartOfAccounts, List<JournalsEntry>> income = incomeStream//
                    .collect(Collectors.groupingBy(f -> f.getAccount().getAccountType().getType()));
            List<ProfitLossView.Item> items = income.entrySet().stream().map(map(null)).toList();
            grossFirst.addAll(items);

            //expenses
            Stream<JournalsEntry> expensesStream = result.stream().filter(expensesFilter());
            Map<ChartOfAccounts, List<JournalsEntry>> expenses = expensesStream//
                    .collect(Collectors.groupingBy(f -> f.getAccount().getAccountType().getType()));
            List<ProfitLossView.Item> expenseItems = expenses.entrySet().stream().map(map(null)).toList();
            netFirst.addAll(expenseItems);

            // cgs
            Stream<JournalsEntry> expensesCgsStream = result.stream().filter(cgsFilter());
            Map<ChartOfAccounts, List<JournalsEntry>> expensesCgs = expensesCgsStream//
                    .collect(Collectors.groupingBy(f -> f.getAccount().getAccountType().getType()));
            List<ProfitLossView.Item> expenseCgsItems = expensesCgs.entrySet().stream().map(map("Cost of Goods Sold")).toList();
            grossFirst.addAll(expenseCgsItems);
            grossProfitGrid.setItems(grossFirst, f -> getChildren(f, grossSecond, grossLast));
            netProfitGrid.setItems(netFirst, f -> getChildren(f, netSecond, netLast));
        }

        BigDecimal income = result.stream().filter(filterMainPeriod()).filter(incomeFilter())//
                .map(getJournalsEntryBigDecimalFunction()).reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal cgs = result.stream().filter(filterMainPeriod()).filter(cgsFilter())//
                .map(getJournalsEntryBigDecimalFunction()).reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal oe = result.stream().filter(filterMainPeriod()).filter(expensesFilter())//
                .map(getJournalsEntryBigDecimalFunction()).reduce(BigDecimal.ZERO, BigDecimal::add);


//        BigDecimal gofe = result.stream().filter(f -> f.getAccount().getSystemId() != null && f.getAccount().getSystemId().compareTo(SystemAccounts.GOFE.getId()) == 0 && f.getCurrencyFrom().getCode().equalsIgnoreCase(getSelectedCurrency()))//
//                .map(getJournalsEntryBigDecimalFunction()).reduce(BigDecimal.ZERO, BigDecimal::add);
//
//        income = income.subtract(gofe);


        BigDecimal grossProfit = income.subtract(cgs);
        BigDecimal sum = grossProfit.subtract(oe);


        grossProfitLbl.setText(getSelectedCurrency() + " " + Constants.CURRENCY_FORMAT.format(grossProfit));
        incomeLbl.setText(getSelectedCurrency() + " " + Constants.CURRENCY_FORMAT.format(income));
        cgsLbl.setText(getSelectedCurrency() + " " + Constants.CURRENCY_FORMAT.format(cgs));
        oeLbl.setText(getSelectedCurrency() + " " + Constants.CURRENCY_FORMAT.format(oe));
        total.setText(getSelectedCurrency() + " " + Constants.CURRENCY_FORMAT.format(sum));
        netProfitLbl.setText(getSelectedCurrency() + " " + Constants.CURRENCY_FORMAT.format(sum));


        if(compareItem.isVisible()){
            BigDecimal incomeCompare = result.stream().filter(filterComparePeriod()).filter(incomeFilter())//
                    .map(getJournalsEntryBigDecimalFunction()).reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal cgsCompare = result.stream().filter(filterComparePeriod()).filter(cgsFilter())//
                    .map(getJournalsEntryBigDecimalFunction()).reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal oeCompare = result.stream().filter(filterComparePeriod()).filter(expensesFilter())//
                    .map(getJournalsEntryBigDecimalFunction()).reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal grossProfitCompare = incomeCompare.subtract(cgsCompare);
            grossProfitCompareLbl.setText(getSelectedCurrency() + " " + Constants.CURRENCY_FORMAT.format(grossProfitCompare));
            BigDecimal subtract = grossProfit.subtract(grossProfitCompare);
            grossProfitDiffLbl.setText(getSelectedCurrency() + " " + Constants.CURRENCY_FORMAT.format(subtract));
            if(subtract.compareTo(BigDecimal.ZERO) == 0){
                grossProfitDiffLbl.addClassNames(LumoUtility.FontWeight.BOLD, LumoUtility.TextColor.PRIMARY);
            } else if (subtract.compareTo(BigDecimal.ZERO) > 0){
                grossProfitDiffLbl.addClassNames(LumoUtility.FontWeight.BOLD, LumoUtility.TextColor.SUCCESS);
            } else {
                grossProfitDiffLbl.addClassNames(LumoUtility.FontWeight.BOLD, LumoUtility.TextColor.ERROR);
            }

            BigDecimal sumCompare = grossProfitCompare.subtract(oeCompare);
            netProfitCompareLbl.setText(getSelectedCurrency() + " " + Constants.CURRENCY_FORMAT.format(sumCompare));
            BigDecimal subtract1 = sum.subtract(sumCompare);
            netProfitDiffLbl.setText(getSelectedCurrency() + " " + Constants.CURRENCY_FORMAT.format(subtract1));
            if(subtract1.compareTo(BigDecimal.ZERO) == 0){
                netProfitDiffLbl.addClassNames(LumoUtility.FontWeight.BOLD, LumoUtility.TextColor.PRIMARY);
            } else if (subtract1.compareTo(BigDecimal.ZERO) > 0){
                netProfitDiffLbl.addClassNames(LumoUtility.FontWeight.BOLD, LumoUtility.TextColor.SUCCESS);
            } else {
                netProfitDiffLbl.addClassNames(LumoUtility.FontWeight.BOLD, LumoUtility.TextColor.ERROR);
            }
        }
    }

    private Predicate<JournalsEntry> filterMainPeriod() {
        return f -> f.getJournals().getLogDate().isBefore(asOfDatePicker.getValue().plusDays(1)) && f.getJournals().getLogDate().isAfter(startFromDatePicker.getValue().minusDays(1));
    }

    private Predicate<JournalsEntry> filterComparePeriod() {
        return f -> compareItem.isVisible() && f.getJournals().getLogDate().isBefore(compareAsOfDatePicker.getValue().plusDays(1)) && f.getJournals().getLogDate().isAfter(compareStartFromDatePicker.getValue().minusDays(1));
    }

    private Function<Map.Entry<ChartOfAccounts, List<JournalsEntry>>, ProfitLossView.Item> map(String caption) {
        return f -> {
            ChartOfAccounts key = f.getKey();
            List<JournalsEntry> value = f.getValue();
            BigDecimal reduce = value.stream().filter(filterMainPeriod()).map(getJournalsEntryBigDecimalFunction()).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal reduce2 = value.stream().filter(filterComparePeriod()).map(getJournalsEntryBigDecimalFunction()).reduce(BigDecimal.ZERO, BigDecimal::add);
            return new ProfitLossView.Item("Total " + (StringUtils.isBlank(caption) ? key.getCaption() : caption), reduce, reduce2, key, value);
        };
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

    protected List<ProfitLossView.Item> getChildren(ProfitLossView.Item journalsEntry, List<ProfitLossView.Item> second, List<ProfitLossView.Item> last) {
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
        List<JournalsEntry> result1 = journalsEntry.getValues();
//        if(journalsEntry.getChartOfAccountTypes() != null && journalsEntry.getChartOfAccountTypes().compareTo(ChartOfAccountTypes.CGS) == 0){
//            result1 = result1.stream().filter(cgsFilter()).collect(Collectors.toList());
//        } else if(journalsEntry.getChartOfAccounts() != null && journalsEntry.getChartOfAccounts().compareTo(ChartOfAccounts.EXP) == 0){
//            result1 = result1.stream().filter(expensesFilter()).collect(Collectors.toList());
//        }else if(journalsEntry.getChartOfAccounts() != null && journalsEntry.getChartOfAccounts().compareTo(ChartOfAccounts.INC) == 0){
//            result1 = result1.stream().filter(incomeFilter()).collect(Collectors.toList());
//        }
        if (journalsEntry.getChartOfAccounts() != null) {
            result = result1.stream().filter(g -> g.getAccount().getAccountType().getType().compareTo(journalsEntry.getChartOfAccounts()) == 0).toList();
            Map<ChartOfAccountTypes, List<JournalsEntry>> collect = result.stream().filter(f -> f.getCurrencyFrom().getCode().equalsIgnoreCase(getSelectedCurrency())).collect(Collectors.groupingBy(f -> f.getAccount().getAccountType().getCode()));
            List<ProfitLossView.Item> items = collect.entrySet().stream().map(f -> {
                ChartOfAccountTypes key = f.getKey();
                List<JournalsEntry> value = f.getValue();
                BigDecimal reduce = value.stream().filter(filterMainPeriod()).map(getJournalsEntryBigDecimalFunction()).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal reduce2 = value.stream().filter(filterComparePeriod()).map(getJournalsEntryBigDecimalFunction()).reduce(BigDecimal.ZERO, BigDecimal::add);
                return new ProfitLossView.Item("Total " + key.getCaption(), reduce, reduce2, key, value);
            }).toList();
            second.addAll(items);
            return items;
        } else {
            result = result1.stream().filter(g -> g.getAccount().getAccountType().getCode().compareTo(journalsEntry.getChartOfAccountTypes()) == 0).toList();
            Map<Account, List<JournalsEntry>> collect = result.stream()/*.filter(f -> f.getCurrencyFrom().getCode().equalsIgnoreCase(getSelectedCurrency()))*/.collect(Collectors.groupingBy(JournalsEntry::getAccount));
            List<ProfitLossView.Item> items = collect.entrySet().stream().map(f -> {
                Account key = f.getKey();
                List<JournalsEntry> value = f.getValue();
                BigDecimal reduce = value.stream().filter(filterMainPeriod()).map(getJournalsEntryBigDecimalFunction()).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal reduce2 = value.stream().filter(filterComparePeriod()).map(getJournalsEntryBigDecimalFunction()).reduce(BigDecimal.ZERO, BigDecimal::add);
                return new ProfitLossView.Item("Total " + key.getName(), reduce, reduce2, value,key);
            }).toList();
            last.addAll(items);
            return items;
        }


    }

    public static class Item {
        protected String caption;
        protected BigDecimal amount, amountCompare;
        protected ChartOfAccounts chartOfAccounts;
        protected ChartOfAccountTypes chartOfAccountTypes;
        private List<JournalsEntry> values;
        private Account account;

        public Item(String caption, BigDecimal amount, BigDecimal amountCompare, ChartOfAccountTypes chartOfAccountTypes, List<JournalsEntry> values) {
            this.caption = caption;
            this.amount = amount;
            this.amountCompare = amountCompare;
            this.chartOfAccountTypes = chartOfAccountTypes;
            this.values = values;
        }

        public Item(String caption, BigDecimal amount, BigDecimal amountCompare, List<JournalsEntry> values, Account account) {
            this.caption = caption;
            this.amount = amount;
            this.account = account;
            this.amountCompare = amountCompare;
            this.values = values;
        }

        public Item(String caption, BigDecimal amount, BigDecimal amountCompare, ChartOfAccounts chartOfAccounts, List<JournalsEntry> values) {
            this.caption = caption;
            this.amount = amount;
            this.amountCompare = amountCompare;
            this.chartOfAccounts = chartOfAccounts;
            this.values = values;
        }

        public Account getAccount() {
            return account;
        }

        public void setAccount(Account account) {
            this.account = account;
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

        public BigDecimal getAmountCompare() {
            return amountCompare;
        }

        public void setAmountCompare(BigDecimal amountCompare) {
            this.amountCompare = amountCompare;
        }
    }

}
