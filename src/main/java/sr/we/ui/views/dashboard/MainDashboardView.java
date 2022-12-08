package sr.we.ui.views.dashboard;


import com.github.appreciated.apexcharts.ApexChartsBuilder;
import com.github.appreciated.apexcharts.config.NoData;
import com.github.appreciated.apexcharts.config.XAxis;
import com.github.appreciated.apexcharts.config.builder.ChartBuilder;
import com.github.appreciated.apexcharts.config.builder.GridBuilder;
import com.github.appreciated.apexcharts.config.builder.StrokeBuilder;
import com.github.appreciated.apexcharts.config.builder.XAxisBuilder;
import com.github.appreciated.apexcharts.config.chart.Type;
import com.github.appreciated.apexcharts.config.chart.builder.ZoomBuilder;
import com.github.appreciated.apexcharts.config.grid.builder.RowBuilder;
import com.github.appreciated.apexcharts.config.stroke.Curve;
import com.github.appreciated.apexcharts.config.xaxis.Title;
import com.github.appreciated.apexcharts.helper.Series;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.vaadin.addons.yuri0x7c1.bslayout.BsColumn;
import org.vaadin.addons.yuri0x7c1.bslayout.BsLayout;
import org.vaadin.addons.yuri0x7c1.bslayout.BsRow;
import sr.we.ContextProvider;
import sr.we.data.controller.BusinessService;
import sr.we.data.controller.DashboardService;
import sr.we.data.controller.JournalEntryService;
import sr.we.data.controller.UserAccessService;
import sr.we.demo.about.AboutView;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Business;
import sr.we.shekelflowcore.entity.JournalsEntry;
import sr.we.shekelflowcore.entity.Role;
import sr.we.shekelflowcore.entity.helper.PagingResult;
import sr.we.shekelflowcore.entity.helper.adapter.DashboardAccounts;
import sr.we.shekelflowcore.entity.helper.vo.JournalsEntryVO;
import sr.we.shekelflowcore.enums.*;
import sr.we.shekelflowcore.security.Privileges;
import sr.we.shekelflowcore.security.privileges.TransactionsPrivilege;
import sr.we.shekelflowcore.settings.util.Constants;
import sr.we.ui.components.BreadCrumb;
import sr.we.ui.components.Highlight;
import sr.we.ui.components.UIUtil;
import sr.we.ui.views.MainLayout;

import javax.annotation.security.RolesAllowed;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@BreadCrumb(titleKey = "sr.we.dashboard", optimizedMobile = true)
@Route(value = "main-dashboard", layout = MainLayout.class)
@RolesAllowed({Role.user, Role.staff, Role.owner, Role.admin})
public class MainDashboardView extends Main implements BeforeEnterObserver {

    private final BsLayout board;
    private Long businessId;
    private ApexChartsBuilder apexChartsBuilder;
    private Div div;
    private Business business;

    public MainDashboardView() {
        addClassName("dashboard-view");


        board = new BsLayout();

        add(board);
    }

    public static Component createHighlight(String title, String value, Double percentage) {

        return new Highlight(title, () -> value, () -> percentage);
    }

    public static Component createResponseTimes() {
        HorizontalLayout header = createHeader("Profit And Loss", "Income and expenses only (includes unpaid invoices and bills).");

        // Chart
        /*Chart chart = new Chart();
        Configuration conf = chart.getConfiguration();
        conf.getChart().setStyledMode(true);

        DataSeries series = new DataSeries();
        series.add(new DataSeriesItem("System 1", 12.5));
        series.add(new DataSeriesItem("System 2", 12.5));
        series.add(new DataSeriesItem("System 3", 12.5));
        series.add(new DataSeriesItem("System 4", 12.5));
        series.add(new DataSeriesItem("System 5", 12.5));
        series.add(new DataSeriesItem("System 6", 12.5));
        conf.addSeries(series);*/

        // Add it all together
        VerticalLayout serviceHealth = new VerticalLayout(header/*, chart*/);
        serviceHealth.addClassName(LumoUtility.Padding.XSMALL);
        serviceHealth.setPadding(false);
        serviceHealth.setSpacing(false);
        serviceHealth.getElement().getThemeList().add("spacing-s");
        return serviceHealth;
    }

    private static HorizontalLayout createHeader(String title, String subtitle) {
        H2 h2 = new H2(title);
        h2.addClassNames("text-xl", "m-0");

        Span span = new Span(subtitle);
        span.addClassNames("text-secondary", "text-xs");

        VerticalLayout column = new VerticalLayout(h2, span);
        column.setPadding(false);
        column.setSpacing(false);

        HorizontalLayout header = new HorizontalLayout(column);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setSpacing(false);
        header.setWidthFull();
        return header;
    }

    private static String getStatusDisplayName(DashboardAccounts serviceHealth) {
        if (serviceHealth.getIn().compareTo(serviceHealth.getOut()) == 0) {
            return "Ok";
        } else if (serviceHealth.getIn().compareTo(serviceHealth.getOut()) < 0) {
            return "Failing";
        } else if (serviceHealth.getIn().compareTo(serviceHealth.getOut()) > 0) {
            return "Excellent";
        }
        return null;
    }

    private static String getStatusTheme(DashboardAccounts serviceHealth) {
        String theme = UIUtil.Badge.PILL + " primary small";
        if (serviceHealth.getIn().compareTo(serviceHealth.getOut()) > 0) {
            theme += " success";
        } else if (serviceHealth.getIn().compareTo(serviceHealth.getOut()) < 0) {
            theme += " error";
        }
        return theme;
    }

    public Component createViewEvents() {
        // Header
        Select year = new Select();
        year.setItems("2011", "2012", "2013", "2014", "2015", "2016", "2017", "2018", "2019", "2020", "2021", "2022");
        year.setValue("2022");
        year.setWidth("100px");

        HorizontalLayout header = createHeader("Cash Flow", "Cash coming in and going out of your business.");
        header.add(year);

        // Chart
        apexChartsBuilder = ApexChartsBuilder.get();
//        Configuration conf = chart.getConfiguration();
//        conf.getChart().setStyledMode(true);
        NoData noData = new NoData();
        noData.setText("No data present at the moment");
        apexChartsBuilder = apexChartsBuilder.withChart(ChartBuilder.get().withType(Type.LINE).withHeight("400px").withZoom(ZoomBuilder.get().withEnabled(true).build()).build())//
                .withStroke(StrokeBuilder.get().withCurve(Curve.SMOOTH).build())//
//                .withTitle(TitleSubtitleBuilder.get().withText("Chart").withAlign(Align.right).build())//
                .withNoData(noData)//
                .withGrid(GridBuilder.get().withRow(RowBuilder.get().withColors("#f3f3f3", "transparent").withOpacity(0.5).build()).build());//

        XAxisBuilder xAxis = XAxisBuilder.get();
        XAxis axis = xAxis.withCategories("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec").build();
        apexChartsBuilder = apexChartsBuilder.withXaxis(axis);

        Title title = new Title();
        title.setText("Values");
        axis.setTitle(title);

//        PlotOptionsArea plotOptions = new PlotOptionsArea();
//        plotOptions.setPointPlacement(PointPlacement.ON);
//        conf.addPlotOptions(plotOptions);

        UI current = UI.getCurrent();
        String token = AuthenticatedUser.token();
        new Thread(new Runnable() {
            @Override
            public void run() {

                List<JournalsEntry> result = journalList(token);


                current.access(() -> {
                    Series<Number>[] listSeries = new Series[2];

                    Series<Number> flowin = new Series<>();
                    flowin.setName("Cash-in");
                    flowin.setData(getData(result, DebCred.DEB));
                    listSeries[0] = flowin;

                    Series<Number> flowout = new Series<>();
                    flowout.setName("Cash-out");
                    flowout.setData(getData(result, DebCred.CRED));
                    listSeries[1] = flowout;
                    apexChartsBuilder = apexChartsBuilder.withSeries(listSeries);
                    div.removeAll();
                    div.add(apexChartsBuilder.build());
                });
            }
        }).start();

        // Add it all together
        ProgressBar progressBar = new ProgressBar();
        progressBar.setWidthFull();
        progressBar.setIndeterminate(true);
        div = new Div(progressBar);
        div.setWidthFull();
        VerticalLayout viewEvents = new VerticalLayout(header, div);
        viewEvents.addClassName(LumoUtility.Padding.XSMALL);
        viewEvents.setPadding(false);
        viewEvents.setSpacing(false);
        viewEvents.getElement().getThemeList().add("spacing-s");
        return viewEvents;
    }

    private List<JournalsEntry> journalList(String token) {
        JournalEntryService journalEntryService = ContextProvider.getBean(JournalEntryService.class);
        JournalsEntryVO vo = new JournalsEntryVO();
        vo.setBusinessId(businessId);
        vo.setAsOfDate(LocalDate.now());
        PagingResult<JournalsEntry> list = journalEntryService.list(vo, token);

        List<JournalsEntry> result = list.getResult();
        return result;
    }

    private List<JournalsEntry> journalList(String token, LocalDate localDate) {
        JournalEntryService journalEntryService = ContextProvider.getBean(JournalEntryService.class);
        JournalsEntryVO vo = new JournalsEntryVO();
        vo.setBusinessId(businessId);
        vo.setAsOfDate(localDate);
        PagingResult<JournalsEntry> list = journalEntryService.list(vo, token);

        List<JournalsEntry> result = list.getResult();
        return result;
    }

    private Number[] getData(List<JournalsEntry> result, DebCred debCred) {

        Map<Month, List<JournalsEntry>> debit = result.stream().filter(f -> f.getJournals().getLogDate().getYear() == 2022 && f.getDebCred().compareTo(debCred) == 0 && //
                        f.getAccount().getAccountType().getCode().compareTo(ChartOfAccountTypes.CAB) == 0 && //
                        f.getCurrencyFrom().getCode().equalsIgnoreCase(business.getCurrency().getCode()))//
                .collect(Collectors.groupingBy(f -> f.getJournals().getLogDate().getMonth()));

//        Map<Month, List<JournalsEntry>> credit = result.stream().filter(f -> f.getDebCred().compareTo(DebCred.CRED) == 0 && //
//                        f.getAccount().getAccountType().getCode().compareTo(ChartOfAccountTypes.CAB) == 0 && //
//                        f.getCurrencyFrom().getCode().equalsIgnoreCase(business.getCurrency().getCode()))//
//                .collect(Collectors.groupingBy(f -> f.getJournals().getLogDate().getMonth()));

        if (debCred.compareTo(DebCred.DEB) == 0) {

            return new Number[]{getMonthValue(debit, Month.JANUARY),//
                    getMonthValue(debit, Month.FEBRUARY),//
                    getMonthValue(debit, Month.MARCH),//
                    getMonthValue(debit, Month.APRIL),//
                    getMonthValue(debit, Month.MAY),//
                    getMonthValue(debit, Month.JUNE),//
                    getMonthValue(debit, Month.JULY),//
                    getMonthValue(debit, Month.AUGUST),//
                    getMonthValue(debit, Month.SEPTEMBER),//
                    getMonthValue(debit, Month.OCTOBER),//
                    getMonthValue(debit, Month.NOVEMBER),//
                    getMonthValue(debit, Month.DECEMBER)};
        } else {
            return new Number[]{getMonthValue(debit, Month.JANUARY).multiply(BigDecimal.valueOf(-1)),//
                    getMonthValue(debit, Month.FEBRUARY).multiply(BigDecimal.valueOf(-1)),//
                    getMonthValue(debit, Month.MARCH).multiply(BigDecimal.valueOf(-1)),//
                    getMonthValue(debit, Month.APRIL).multiply(BigDecimal.valueOf(-1)),//
                    getMonthValue(debit, Month.MAY).multiply(BigDecimal.valueOf(-1)),//
                    getMonthValue(debit, Month.JUNE).multiply(BigDecimal.valueOf(-1)),//
                    getMonthValue(debit, Month.JULY).multiply(BigDecimal.valueOf(-1)),//
                    getMonthValue(debit, Month.AUGUST).multiply(BigDecimal.valueOf(-1)),//
                    getMonthValue(debit, Month.SEPTEMBER).multiply(BigDecimal.valueOf(-1)),//
                    getMonthValue(debit, Month.OCTOBER).multiply(BigDecimal.valueOf(-1)),//
                    getMonthValue(debit, Month.NOVEMBER).multiply(BigDecimal.valueOf(-1)),//
                    getMonthValue(debit, Month.DECEMBER).multiply(BigDecimal.valueOf(-1))};
        }
    }

    private BigDecimal getMonthValue(Map<Month, List<JournalsEntry>> collect, Month month) {
        return collect == null ? BigDecimal.ZERO : collect.get(month) == null ? BigDecimal.ZERO : collect.get(month).stream().map(getJournalsEntryBigDecimalFunction()).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Function<JournalsEntry, BigDecimal> getJournalsEntryBigDecimalFunction() {
        return g -> {
            BigDecimal bigDecimal = g.getAccount().getAccountType().getType().getPlusMin(g.getDebCred()).compareTo(TransactionType.WITHDRAWAL) == 0//
                    ? g.getAmount().multiply(BigDecimal.valueOf(-1)) : g.getAmount();
            return bigDecimal;
        };
    }

    public Component createServiceHealth() {
        // Header
        HorizontalLayout header = createHeader("Payable & Owing", "");

        // Grid
        Grid<DashboardAccounts> grid = new Grid();
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setAllRowsVisible(true);

        grid.addColumn(new ComponentRenderer<>(serviceHealth -> {
            Span status = new Span();
            String statusText = getStatusDisplayName(serviceHealth);
            status.getElement().setAttribute("aria-label", "Status: " + statusText);
            status.getElement().setAttribute("title", "Status: " + statusText);
            status.getElement().getThemeList().add(getStatusTheme(serviceHealth));
            return status;
        })).setHeader("").setFlexGrow(0).setAutoWidth(true).setResizable(true).setSortable(true);
        grid.addColumn(f -> f.getName() + "(" + f.getAccountNumber() + ")").setHeader("Account").setFlexGrow(1).setResizable(true).setSortable(true);
        grid.addColumn(f -> Constants.CURRENCY_FORMAT.format(f.getIn())).setHeader("IN").setAutoWidth(true).setTextAlign(ColumnTextAlign.END).setResizable(true).setSortable(true);
        grid.addColumn(f -> Constants.CURRENCY_FORMAT.format(f.getOut())).setHeader("OUT").setAutoWidth(true).setTextAlign(ColumnTextAlign.END).setResizable(true).setSortable(true);


        UI current = UI.getCurrent();
        String token = AuthenticatedUser.token();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//                DashboardService dashboardService = ContextProvider.getBean(DashboardService.class);
//
//                List<DashboardAccounts> accounts = dashboardService.getAccounts(token, businessId);
//                current.access(() -> {
//                    grid.setItems(accounts);
//                });
//            }
//        }).start();


        // Add it all together
        VerticalLayout serviceHealth = new VerticalLayout(header, grid);
        serviceHealth.addClassName(LumoUtility.Padding.XSMALL);
        serviceHealth.setPadding(false);
        serviceHealth.setSpacing(false);
        serviceHealth.getElement().getThemeList().add("spacing-s");
        return serviceHealth;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        UserAccessService userAccesService = ContextProvider.getBean(UserAccessService.class);
        boolean hasAccess = userAccesService.hasAccess(AuthenticatedUser.token(), new TransactionsPrivilege(), Privileges.READ);
        if (!hasAccess) {
            UI.getCurrent().navigate(AboutView.class);
        }
        board.removeAll();
        Optional<String> businessOptional = event.getRouteParameters().get("business");
        String token = AuthenticatedUser.token();
        if (businessOptional.isPresent()) {
            String businessString = businessOptional.get();
            businessId = Long.valueOf(businessString);
            BusinessService businessService = ContextProvider.getBean(BusinessService.class);
            business = businessService.get(businessId, AuthenticatedUser.token());
        }
        DashboardService dashboardService = ContextProvider.getBean(DashboardService.class);
        if (businessOptional != null) {


            String format = Constants.CURRENCY_FORMAT.format(BigDecimal.ZERO);
            board.withRows(new BsRow().withColumns(//
                            new BsColumn(new Highlight("Outstanding payments", () -> {
                                List<JournalsEntry> result = journalList(token);
                                BigDecimal otherAssets = result.stream().filter(f -> f.getAccount().getAccountType().getType().compareTo(ChartOfAccounts.ASSETS) == 0 && !(

                                                f.getAccount().getAccountType().getCode().compareTo(ChartOfAccountTypes.CAB) == 0 ||//
                                                        f.getAccount().getAccountType().getCode().compareTo(ChartOfAccountTypes.INV) == 0 ||//
                                                        f.getAccount().getAccountType().getCode().compareTo(ChartOfAccountTypes.PPE) == 0 ||//
                                                        f.getAccount().getAccountType().getCode().compareTo(ChartOfAccountTypes.DA) == 0 ||//
                                                        f.getAccount().getAccountType().getCode().compareTo(ChartOfAccountTypes.VPVC) == 0 ||//
                                                        f.getAccount().getAccountType().getCode().compareTo(ChartOfAccountTypes.OSTA) == 0 ||//
                                                        f.getAccount().getAccountType().getCode().compareTo(ChartOfAccountTypes.OLTA) == 0 //
                                        )

                                                && f.getCurrencyFrom().getCode().equalsIgnoreCase(business.getCurrency().getCode()))//
                                        .map(getJournalsEntryBigDecimalFunction()).reduce(BigDecimal.ZERO, BigDecimal::add);
                                return Constants.CURRENCY_FORMAT.format(otherAssets == null ? BigDecimal.ZERO : otherAssets);
                            }, () -> {
                                List<JournalsEntry> result = journalList(token);
                                List<JournalsEntry> result2 = journalList(token, LocalDate.now().minusMonths(1));
                                BigDecimal reduce = result.stream().filter(f -> f.getAccount().getSystemId() != null && f.getAccount().getSystemId().compareTo(SystemAccounts.AR.getId()) == 0 && //
                                                f.getCurrencyFrom().getCode().equalsIgnoreCase(business.getCurrency().getCode()))//
                                        .map(f -> f.getAmount() == null ? BigDecimal.ZERO : f.getDebCred().compareTo(DebCred.DEB) == 0 ? f.getAmount() : f.getAmount().multiply(BigDecimal.valueOf(-1L))).reduce(BigDecimal.ZERO, BigDecimal::add);
                                reduce = reduce == null ? BigDecimal.ZERO : reduce;
                                BigDecimal reduce2 = result2.stream().filter(f -> f.getAccount().getSystemId() != null && f.getAccount().getSystemId().compareTo(SystemAccounts.AR.getId()) == 0 && //
                                                f.getCurrencyFrom().getCode().equalsIgnoreCase(business.getCurrency().getCode()))//
                                        .map(f -> f.getAmount() == null ? BigDecimal.ZERO : f.getDebCred().compareTo(DebCred.DEB) == 0 ? f.getAmount() : f.getAmount().multiply(BigDecimal.valueOf(-1L))).reduce(BigDecimal.ZERO, BigDecimal::add);
                                reduce2 = reduce2 == null ? BigDecimal.ZERO : reduce2;
                                reduce = reduce2.subtract(reduce);
                                return reduce == null ? 0d : reduce.doubleValue();

                            })).withSize(BsColumn.Size.XS), //
                            new BsColumn(new Highlight("Outstanding Bills", () -> {
                                List<JournalsEntry> result = journalList(token);
                                BigDecimal liabilities = result.stream().filter(f -> f.getAccount().getAccountType().getType().compareTo(ChartOfAccounts.LCC) == 0 && f.getCurrencyFrom().getCode().equalsIgnoreCase(business.getCurrency().getCode()))//
                                        .map(getJournalsEntryBigDecimalFunction()).reduce(BigDecimal.ZERO, BigDecimal::add);
                                return Constants.CURRENCY_FORMAT.format(liabilities == null ? BigDecimal.ZERO : liabilities);
                            }, () -> {
                                List<JournalsEntry> result = journalList(token);
                                List<JournalsEntry> result2 = journalList(token, LocalDate.now().minusMonths(1));
                                BigDecimal reduce = result.stream().filter(f -> f.getAccount().getSystemId() != null && f.getAccount().getSystemId().compareTo(SystemAccounts.AP.getId()) == 0 && //
                                                f.getCurrencyFrom().getCode().equalsIgnoreCase(business.getCurrency().getCode()))//
                                        .map(f -> f.getAmount() == null ? BigDecimal.ZERO : f.getDebCred().compareTo(DebCred.DEB) == 0 ? f.getAmount() : f.getAmount().multiply(BigDecimal.valueOf(-1L))).reduce(BigDecimal.ZERO, BigDecimal::add);
                                reduce = reduce == null ? BigDecimal.ZERO : reduce;
                                BigDecimal reduce2 = result2.stream().filter(f -> f.getAccount().getSystemId() != null && f.getAccount().getSystemId().compareTo(SystemAccounts.AP.getId()) == 0 && //
                                                f.getCurrencyFrom().getCode().equalsIgnoreCase(business.getCurrency().getCode()))//
                                        .map(f -> f.getAmount() == null ? BigDecimal.ZERO : f.getDebCred().compareTo(DebCred.DEB) == 0 ? f.getAmount() : f.getAmount().multiply(BigDecimal.valueOf(-1L))).reduce(BigDecimal.ZERO, BigDecimal::add);
                                reduce2 = reduce2 == null ? BigDecimal.ZERO : reduce2;
                                reduce = reduce2.subtract(reduce);
                                return reduce == null ? 0d : reduce.doubleValue();
                            })).withSize(BsColumn.Size.XS), //
                            new BsColumn(new Highlight("Next Payments ", () -> /*"54.6k"*/format, () -> /*-112.45*/0d)).withSize(BsColumn.Size.XS), //
                            new BsColumn(new Highlight("Transactions YTD", () -> /*"54.6k"*/format, () -> /*-112.45*/0d)).withSize(BsColumn.Size.XS)),
                    //
                    new BsRow().withColumns(//
                            new BsColumn(createViewEvents()).withSize(BsColumn.Size.XS)), //
                    new BsRow().withColumns(//
                            new BsColumn(createServiceHealth()).withSize(BsColumn.Size.XS), //
                            new BsColumn(createResponseTimes()).withSize(BsColumn.Size.XS)));//
        }
    }
}
