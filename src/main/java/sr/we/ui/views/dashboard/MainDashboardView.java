package sr.we.ui.views.dashboard;


import com.github.appreciated.apexcharts.ApexChartsBuilder;
import com.github.appreciated.apexcharts.config.DataLabels;
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
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.*;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.vaadin.addons.yuri0x7c1.bslayout.BsColumn;
import org.vaadin.addons.yuri0x7c1.bslayout.BsLayout;
import org.vaadin.addons.yuri0x7c1.bslayout.BsRow;
import sr.we.ContextProvider;
import sr.we.data.controller.BusinessService;
import sr.we.data.controller.JournalEntryService;
import sr.we.data.controller.UserAccessService;
import sr.we.demo.about.AboutView;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Account;
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
import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

@BreadCrumb(titleKey = "sr.we.dashboard", optimizedMobile = true)
@Route(value = "main-dashboard", layout = MainLayout.class)
@RolesAllowed({Role.user, Role.staff, Role.owner, Role.admin})
public class MainDashboardView extends Main implements BeforeEnterObserver, BeforeLeaveObserver {

    private static Div divProfitLoss;
    private final BsLayout board;
    private Long businessId;
    private ApexChartsBuilder cashFlowChartsBuilder, profitLossChartBuilder, breakDownChartBuilder;
    private Div div;
    private Business business;
    private Select<Year> year;
    private Future<?> submit, submit1, submit2, submit3, submit4, submit5, submit6, submit7, submit8;
    private ExecutorService executorService;
    private Select<Year> yearProfitLoss;
    private Div divBreakDown;
    private Select<Year> yearBreakDown;

    public MainDashboardView() {
        executorService = Executors.newFixedThreadPool(5);
        addClassName("dashboard-view");


        board = new BsLayout();

        add(board);
    }

//    public static Component createHighlight(String title, String value, Double percentage) {
//
//        return new Highlight(title, () -> value, () -> percentage, executorService);
//    }

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

    public Component expenseBreakDownChart() {
        // Header
        yearBreakDown = new Select<>();
        Year now = Year.now();
        yearBreakDown.setItems(now.minusYears(5), now.minusYears(4), now.minusYears(3), now.minusYears(2), now.minusYears(1), now);
        yearBreakDown.setValue(now);
        yearBreakDown.setWidth("100px");
        yearBreakDown.addValueChangeListener(f -> {
            UI current = UI.getCurrent();
            String token = AuthenticatedUser.token();
            submit6 = executorService.submit(() -> {
                submit7 = current.access(() -> {
                    ProgressBar progressBar = new ProgressBar();
                    progressBar.setWidthFull();
                    progressBar.setIndeterminate(true);
                    divBreakDown.removeAll();
                    divBreakDown.add(progressBar);
                });
            });
            refreshBreakDownChart(current, token);
        });

        HorizontalLayout header = createHeader("Expense Breakdown", "expenses only (includes unpaid bills).");
        header.add(yearBreakDown);

        // Chart
        breakDownChartBuilder = new PieChartExample();
//        breakDownChartBuilder = breakDownChartBuilder.withChart(ChartBuilder.get().withType(Type.PIE).withHeight("400px").withZoom(ZoomBuilder.get().withEnabled(true).build()).build())//
//                .withPlotOptions(PlotOptionsBuilder.get().withPie(PieBuilder.get()
//                .withDonut(DonutBuilder.get()
//                        .withSize("70%")
//                        .withLabels(LabelsBuilder.get()
//                                .withShow(false)
//                                .withName(NameBuilder.get().withShow(true)
//                                        .withOffsetY(-16d)
//                                        .withColor("#adad44")
//                                        .build())
//                                .withValue(ValueBuilder.get().withFormatter(
//                                                " function (val) { " +
//                                                        "    return val " +
//                                                        "   } ")
//                                        .build()).build()
//                        )
//                        .build()).build()).build());//

//                .withStroke(StrokeBuilder.get().withCurve(Curve.SMOOTH).build())//
//                .withNoData(noData)//e
//                .withGrid(GridBuilder.get().withRow(RowBuilder.get().withColors("#f3f3f3", "transparent").withOpacity(0.5).build()
//                        )
//                        .build());//

        UI current = UI.getCurrent();
        String token = AuthenticatedUser.token();
        refreshBreakDownChart(current, token);

        // Add it all together
        ProgressBar progressBar = new ProgressBar();
        progressBar.setWidthFull();
        progressBar.setIndeterminate(true);
        divBreakDown = new Div(progressBar);
        divBreakDown.setWidthFull();
        VerticalLayout viewEvents = new VerticalLayout(header, divBreakDown);
        viewEvents.addClassName(LumoUtility.Padding.XSMALL);
        viewEvents.setPadding(false);
        viewEvents.setSpacing(false);
        viewEvents.getElement().getThemeList().add("spacing-s");
        return viewEvents;
    }

    public Component createProfitLossChart() {
        // Header
        yearProfitLoss = new Select<>();
        Year now = Year.now();
        yearProfitLoss.setItems(now.minusYears(5), now.minusYears(4), now.minusYears(3), now.minusYears(2), now.minusYears(1), now);
        yearProfitLoss.setValue(now);
        yearProfitLoss.setWidth("100px");
        yearProfitLoss.addValueChangeListener(f -> {
            UI current = UI.getCurrent();
            String token = AuthenticatedUser.token();
            submit2 = executorService.submit(() -> {
                submit4 = current.access(() -> {
                    ProgressBar progressBar = new ProgressBar();
                    progressBar.setWidthFull();
                    progressBar.setIndeterminate(true);
                    divProfitLoss.removeAll();
                    divProfitLoss.add(progressBar);
                });
            });
            refreshProfitLossChart(current, token);
        });

        HorizontalLayout header = createHeader("Profit And Loss", "Income and expenses only (includes unpaid invoices and bills).");
        header.add(yearProfitLoss);

        // Chart
        profitLossChartBuilder = ApexChartsBuilder.get();
//        Configuration conf = chart.getConfiguration();
//        conf.getChart().setStyledMode(true);
        NoData noData = new NoData();
        noData.setText("No data present at the moment");
        DataLabels dataLabels = new DataLabels();
        dataLabels.setEnabled(false);
        profitLossChartBuilder = profitLossChartBuilder.withChart(ChartBuilder.get().withType(Type.BAR).withHeight("400px").withZoom(ZoomBuilder.get().withEnabled(true).build()).build())//
                .withStroke(StrokeBuilder.get().withCurve(Curve.SMOOTH).build())//
                .withDataLabels(dataLabels)//
                .withNoData(noData)//e
                .withGrid(GridBuilder.get().withRow(RowBuilder.get().withColors("#f3f3f3", "transparent").withOpacity(0.5).build()).build());//

        XAxisBuilder xAxis = XAxisBuilder.get();
        XAxis axis = xAxis.withCategories("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec").build();
        profitLossChartBuilder = profitLossChartBuilder.withXaxis(axis);

        Title title = new Title();
        title.setText("Values");
        axis.setTitle(title);

//        PlotOptionsArea plotOptions = new PlotOptionsArea();
//        plotOptions.setPointPlacement(PointPlacement.ON);
//        conf.addPlotOptions(plotOptions);

        UI current = UI.getCurrent();
        String token = AuthenticatedUser.token();
        refreshProfitLossChart(current, token);

        // Add it all together
        ProgressBar progressBar = new ProgressBar();
        progressBar.setWidthFull();
        progressBar.setIndeterminate(true);
        divProfitLoss = new Div(progressBar);
        divProfitLoss.setWidthFull();
        VerticalLayout viewEvents = new VerticalLayout(header, divProfitLoss);
        viewEvents.addClassName(LumoUtility.Padding.XSMALL);
        viewEvents.setPadding(false);
        viewEvents.setSpacing(false);
        viewEvents.getElement().getThemeList().add("spacing-s");
        return viewEvents;
    }


    public Component createCashflowChart() {
        // Header
        year = new Select<>();
        Year now = Year.now();
        year.setItems(now.minusYears(5), now.minusYears(4), now.minusYears(3), now.minusYears(2), now.minusYears(1), now);
        year.setValue(now);
        year.setWidth("100px");
        year.addValueChangeListener(f -> {
            UI current = UI.getCurrent();
            String token = AuthenticatedUser.token();
            submit3 = executorService.submit(() -> {
                submit = current.access(() -> {
                    ProgressBar progressBar = new ProgressBar();
                    progressBar.setWidthFull();
                    progressBar.setIndeterminate(true);
                    MainDashboardView.this.div.removeAll();
                    MainDashboardView.this.div.add(progressBar);
                });
            });
            refreshCashFlowChart(current, token);
        });

        HorizontalLayout header = createHeader("Cash Flow", "Cash coming in and going out of your business.");
        header.add(year);

        // Chart
        cashFlowChartsBuilder = ApexChartsBuilder.get();
//        Configuration conf = chart.getConfiguration();
//        conf.getChart().setStyledMode(true);
        NoData noData = new NoData();
        noData.setText("No data present at the moment");
        cashFlowChartsBuilder = cashFlowChartsBuilder.withChart(ChartBuilder.get().withType(Type.LINE).withHeight("400px").withZoom(ZoomBuilder.get().withEnabled(true).build()).build())//
                .withStroke(StrokeBuilder.get().withCurve(Curve.SMOOTH).build())//
//                .withTitle(TitleSubtitleBuilder.get().withText("Chart").withAlign(Align.right).build())//
                .withNoData(noData)//e
                .withGrid(GridBuilder.get().withRow(RowBuilder.get().withColors("#f3f3f3", "transparent").withOpacity(0.5).build()).build());//

        XAxisBuilder xAxis = XAxisBuilder.get();
        XAxis axis = xAxis.withCategories("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec").build();
        cashFlowChartsBuilder = cashFlowChartsBuilder.withXaxis(axis);

        Title title = new Title();
        title.setText("Values");
        axis.setTitle(title);

//        PlotOptionsArea plotOptions = new PlotOptionsArea();
//        plotOptions.setPointPlacement(PointPlacement.ON);
//        conf.addPlotOptions(plotOptions);

        UI current = UI.getCurrent();
        String token = AuthenticatedUser.token();
        refreshCashFlowChart(current, token);

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

    private void refreshCashFlowChart(UI current, String token) {

        submit3 = executorService.submit(() -> {

            List<JournalsEntry> result = journalList(token, year);
            Number[] data = getData(result, DebCred.DEB);
            Number[] data1 = getData(result, DebCred.CRED);

            Series<Number>[] listSeries = new Series[2];

            Series<Number> flowIn = new Series<>();
            flowIn.setName("Cash-in");
            flowIn.setData(data);
            listSeries[0] = flowIn;

            Series<Number> flowOut = new Series<>();
            flowOut.setName("Cash-out");
            flowOut.setData(data1);
            listSeries[1] = flowOut;

            submit1 = current.access(() -> {
                cashFlowChartsBuilder = cashFlowChartsBuilder.withSeries(listSeries);
                div.removeAll();
                div.add(cashFlowChartsBuilder.build());
            });
        });
    }

    private void refreshProfitLossChart(UI current, String token) {

        submit2 = executorService.submit(() -> {

            List<JournalsEntry> result = journalList(token, yearProfitLoss);
            Number[] data = getData(result, ChartOfAccounts.INC);
            Number[] data1 = getData(result, ChartOfAccounts.EXP);

            Series<Number>[] listSeries = new Series[2];

            Series<Number> flowIn = new Series<>();
            flowIn.setName("Income");
            flowIn.setData(data);
            listSeries[0] = flowIn;

            Series<Number> flowOut = new Series<>();
            flowOut.setName("Expense");
            flowOut.setData(data1);
            listSeries[1] = flowOut;

            submit5 = current.access(() -> {
                profitLossChartBuilder = profitLossChartBuilder.withSeries(listSeries);
                divProfitLoss.removeAll();
                divProfitLoss.add(profitLossChartBuilder.build());
            });
        });
    }

    private void refreshBreakDownChart(UI current, String token) {

        submit6 = executorService.submit(() -> {

            List<JournalsEntry> result = journalList(token, yearBreakDown);

            Map<Account, List<JournalsEntry>> collect = result.stream().filter(f -> f.getAccount().getAccountType().getType().compareTo(ChartOfAccounts.EXP) == 0 && //
                            f.getCurrencyFrom().getCode().equalsIgnoreCase(business.getCurrency().getCode()))//
                    .collect(Collectors.groupingBy(JournalsEntry::getAccount));
            Double[] listSeries = new Double[collect.size()];
            String[] listLabels = new String[collect.size()];
            AtomicInteger i = new AtomicInteger();
            collect.entrySet().forEach(f -> {
                int i1 = i.get();
                listSeries[i1] = getAccountValue(f.getValue(), f.getKey().getId()).doubleValue();
                listLabels[i1] = f.getKey().getName();
                i.getAndIncrement();
            });


            submit8 = current.access(() -> {
                breakDownChartBuilder = breakDownChartBuilder.withSeries(listSeries).withLabels(listLabels);
                divBreakDown.removeAll();
                if (listSeries.length == 0) {
                    H2 h2 = new H2("No data present at the moment");
                    h2.setClassName(LumoUtility.TextColor.HEADER);
                    divBreakDown.add(h2);
                } else {
                    divBreakDown.add(breakDownChartBuilder.build());
                }
            });
        });
    }

    private List<JournalsEntry> journalList(String token, Select<Year> year) {
        JournalEntryService journalEntryService = ContextProvider.getBean(JournalEntryService.class);
        JournalsEntryVO vo = new JournalsEntryVO();
        vo.setBusinessId(businessId);
        if (year != null) {
            vo.setStartDate(year.getValue().atMonth(Month.JANUARY).atDay(1));
            if (year.getValue().compareTo(Year.now()) == 0) {
                vo.setAsOfDate(LocalDate.now());
            } else {
                vo.setAsOfDate(year.getValue().atMonth(Month.DECEMBER).atEndOfMonth());
            }
        } else {
            vo.setAsOfDate(LocalDate.now());
        }
        PagingResult<JournalsEntry> list = journalEntryService.list(vo, token);

        return list.getResult();
    }

    private List<JournalsEntry> journalList(String token, LocalDate localDate) {
        JournalEntryService journalEntryService = ContextProvider.getBean(JournalEntryService.class);
        JournalsEntryVO vo = new JournalsEntryVO();
        vo.setBusinessId(businessId);
        vo.setAsOfDate(localDate);
        PagingResult<JournalsEntry> list = journalEntryService.list(vo, token);

        return list.getResult();
    }

    private Number[] getData(List<JournalsEntry> result, DebCred debCred) {

        Map<Month, List<JournalsEntry>> debit = result.stream().filter(f -> f.getDebCred().compareTo(debCred) == 0 && //
                        f.getAccount().getAccountType().getCode().compareTo(ChartOfAccountTypes.CAB) == 0 && //
                        f.getCurrencyFrom().getCode().equalsIgnoreCase(business.getCurrency().getCode()))//
                .collect(Collectors.groupingBy(f -> f.getJournals().getLogDate().getMonth()));

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

    private Number[] getData(List<JournalsEntry> result, ChartOfAccounts chartOfAccounts) {

        Map<Month, List<JournalsEntry>> debit = result.stream().filter(f -> f.getAccount().getAccountType().getType().compareTo(chartOfAccounts) == 0 && //
                        f.getCurrencyFrom().getCode().equalsIgnoreCase(business.getCurrency().getCode()))//
                .collect(Collectors.groupingBy(f -> f.getJournals().getLogDate().getMonth()));


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

    }

    private BigDecimal getMonthValue(Map<Month, List<JournalsEntry>> collect, Month month) {
        return collect == null ? BigDecimal.ZERO : collect.get(month) == null ? BigDecimal.ZERO : collect.get(month).stream().map(getJournalsEntryBigDecimalFunction()).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal getAccountValue(List<JournalsEntry> collect, Long accountId) {
        return collect == null ? BigDecimal.ZERO : collect.stream().map(getJournalsEntryBigDecimalFunction()).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Function<JournalsEntry, BigDecimal> getJournalsEntryBigDecimalFunction() {
        return g -> Objects.requireNonNull(g.getAccount().getAccountType().getType().getPlusMin(g.getDebCred())).compareTo(TransactionType.WITHDRAWAL) == 0//
                ? g.getAmount().multiply(BigDecimal.valueOf(-1)) : g.getAmount();
    }

//    public Component createPayableAndOwingReport() {
//        // Header
//        HorizontalLayout header = createHeader("Payable & Owing", "");
//
//        // Grid
//        Grid<DashboardAccounts> grid = new Grid<>();
//        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
//        grid.setAllRowsVisible(true);
//
//        grid.addColumn(new ComponentRenderer<>(serviceHealth -> {
//            Span status = new Span();
//            String statusText = getStatusDisplayName(serviceHealth);
//            status.getElement().setAttribute("aria-label", "Status: " + statusText);
//            status.getElement().setAttribute("title", "Status: " + statusText);
//            status.getElement().getThemeList().add(getStatusTheme(serviceHealth));
//            return status;
//        })).setHeader("").setFlexGrow(0).setAutoWidth(true).setResizable(true).setSortable(true);
//        grid.addColumn(f -> f.getName() + "(" + f.getAccountNumber() + ")").setHeader("Account").setFlexGrow(1).setResizable(true).setSortable(true);
//        grid.addColumn(f -> Constants.CURRENCY_FORMAT.format(f.getIn())).setHeader("IN").setAutoWidth(true).setTextAlign(ColumnTextAlign.END).setResizable(true).setSortable(true);
//        grid.addColumn(f -> Constants.CURRENCY_FORMAT.format(f.getOut())).setHeader("OUT").setAutoWidth(true).setTextAlign(ColumnTextAlign.END).setResizable(true).setSortable(true);
//
//
//        //        new Thread(new Runnable() {
////            @Override
////            public void run() {
////
////                DashboardService dashboardService = ContextProvider.getBean(DashboardService.class);
////
////                List<DashboardAccounts> accounts = dashboardService.getAccounts(token, businessId);
////                current.access(() -> {
////                    grid.setItems(accounts);
////                });
////            }
////        }).start();
//
//
//        // Add it all together
//        VerticalLayout serviceHealth = new VerticalLayout(header, grid);
//        serviceHealth.addClassName(LumoUtility.Padding.XSMALL);
//        serviceHealth.setPadding(false);
//        serviceHealth.setSpacing(false);
//        serviceHealth.getElement().getThemeList().add("spacing-s");
//        return serviceHealth;
//    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {

        if (executorService.isShutdown()) {
            executorService = Executors.newFixedThreadPool(5);
        }

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


            String format = Constants.CURRENCY_FORMAT.format(BigDecimal.ZERO);
            Highlight outstanding_payments = new Highlight("Outstanding payments", () -> {
                List<JournalsEntry> result = journalList(token, (Select<Year>) null);
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
                return Constants.CURRENCY_FORMAT.format(otherAssets);
            }, () -> {
                List<JournalsEntry> result = journalList(token, (Select<Year>) null);
                List<JournalsEntry> result2 = journalList(token, LocalDate.now().minusMonths(1));
                BigDecimal reduce = result.stream().filter(f -> f.getAccount().getSystemId() != null && f.getAccount().getSystemId().compareTo(SystemAccounts.AR.getId()) == 0 && //
                                f.getCurrencyFrom().getCode().equalsIgnoreCase(business.getCurrency().getCode()))//
                        .map(f -> f.getAmount() == null ? BigDecimal.ZERO : f.getDebCred().compareTo(DebCred.DEB) == 0 ? f.getAmount() : f.getAmount().multiply(BigDecimal.valueOf(-1L))).reduce(BigDecimal.ZERO, BigDecimal::add);
//                reduce = reduce == null ? BigDecimal.ZERO : reduce;
                BigDecimal reduce2 = result2.stream().filter(f -> f.getAccount().getSystemId() != null && f.getAccount().getSystemId().compareTo(SystemAccounts.AR.getId()) == 0 && //
                                f.getCurrencyFrom().getCode().equalsIgnoreCase(business.getCurrency().getCode()))//
                        .map(f -> f.getAmount() == null ? BigDecimal.ZERO : f.getDebCred().compareTo(DebCred.DEB) == 0 ? f.getAmount() : f.getAmount().multiply(BigDecimal.valueOf(-1L))).reduce(BigDecimal.ZERO, BigDecimal::add);
//                reduce2 = reduce2 == null ? BigDecimal.ZERO : reduce2;
                reduce = reduce2.subtract(reduce);
                return reduce.doubleValue();

            }, executorService);
            Highlight outstanding_bills = new Highlight("Outstanding Bills", () -> {
                List<JournalsEntry> result = journalList(token, (Select<Year>) null);
                BigDecimal liabilities = result.stream().filter(f -> f.getAccount().getAccountType().getType().compareTo(ChartOfAccounts.LCC) == 0 && f.getCurrencyFrom().getCode().equalsIgnoreCase(business.getCurrency().getCode()))//
                        .map(getJournalsEntryBigDecimalFunction()).reduce(BigDecimal.ZERO, BigDecimal::add);
                return Constants.CURRENCY_FORMAT.format(liabilities);
            }, () -> {
                List<JournalsEntry> result = journalList(token, (Select<Year>) null);
                List<JournalsEntry> result2 = journalList(token, LocalDate.now().minusMonths(1));
                BigDecimal reduce = result.stream().filter(f -> f.getAccount().getSystemId() != null && f.getAccount().getSystemId().compareTo(SystemAccounts.AP.getId()) == 0 && //
                                f.getCurrencyFrom().getCode().equalsIgnoreCase(business.getCurrency().getCode()))//
                        .map(f -> f.getAmount() == null ? BigDecimal.ZERO : f.getDebCred().compareTo(DebCred.DEB) == 0 ? f.getAmount() : f.getAmount().multiply(BigDecimal.valueOf(-1L))).reduce(BigDecimal.ZERO, BigDecimal::add);
//                reduce = reduce == null ? BigDecimal.ZERO : reduce;
                BigDecimal reduce2 = result2.stream().filter(f -> f.getAccount().getSystemId() != null && f.getAccount().getSystemId().compareTo(SystemAccounts.AP.getId()) == 0 && //
                                f.getCurrencyFrom().getCode().equalsIgnoreCase(business.getCurrency().getCode()))//
                        .map(f -> f.getAmount() == null ? BigDecimal.ZERO : f.getDebCred().compareTo(DebCred.DEB) == 0 ? f.getAmount() : f.getAmount().multiply(BigDecimal.valueOf(-1L))).reduce(BigDecimal.ZERO, BigDecimal::add);
//                reduce2 = reduce2 == null ? BigDecimal.ZERO : reduce2;
                reduce = reduce2.subtract(reduce);
                return reduce.doubleValue();
            }, executorService);
            board.withRows(new BsRow().withColumns(//
                            new BsColumn(outstanding_payments).withSize(BsColumn.Size.XS), //
                            new BsColumn(outstanding_bills).withSize(BsColumn.Size.XS), //
                            new BsColumn(new Highlight("Next Payments ", () -> /*"54.6k"*/format, () -> /*-112.45*/0d, executorService)).withSize(BsColumn.Size.XS), //
                            new BsColumn(new Highlight("Transactions YTD", () -> /*"54.6k"*/format, () -> /*-112.45*/0d, executorService)).withSize(BsColumn.Size.XS)),
                    //
                    new BsRow().withColumns(//
                            new BsColumn(createCashflowChart()).withSize(BsColumn.Size.XS)), //
                    new BsRow().withColumns(//
                            new BsColumn(expenseBreakDownChart()).withSize(BsColumn.Size.XS), //
                            new BsColumn(createProfitLossChart()).withSize(BsColumn.Size.XS)));//
        }
    }


    @Override
    protected void onDetach(DetachEvent detachEvent) {
        shut();
        if (!executorService.isShutdown()) {
            executorService.shutdownNow();
        }
        super.onDetach(detachEvent);

    }

    private void shut() {
        if (submit != null) {
            submit.cancel(true);
        }
        if (submit1 != null) {
            submit1.cancel(true);
        }
        if (submit2 != null) {
            submit2.cancel(true);
        }
        if (submit4 != null) {
            submit4.cancel(true);
        }
        if (submit3 != null) {
            submit3.cancel(true);
        }
        if (submit5 != null) {
            submit5.cancel(true);
        }

    }

    @Override
    public void beforeLeave(BeforeLeaveEvent beforeLeaveEvent) {
        shut();
    }
}
