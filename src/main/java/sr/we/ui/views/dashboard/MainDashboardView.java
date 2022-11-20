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
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.vaadin.addons.yuri0x7c1.bslayout.BsColumn;
import org.vaadin.addons.yuri0x7c1.bslayout.BsLayout;
import org.vaadin.addons.yuri0x7c1.bslayout.BsRow;
import sr.we.ContextProvider;
import sr.we.data.controller.DashboardService;
import sr.we.data.controller.UserAccessService;
import sr.we.demo.about.AboutView;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Role;
import sr.we.shekelflowcore.entity.helper.adapter.DashboardAccounts;
import sr.we.shekelflowcore.entity.helper.adapter.DashboardTransactionEvents;
import sr.we.shekelflowcore.security.Privileges;
import sr.we.shekelflowcore.security.privileges.TransactionsPrivilege;
import sr.we.shekelflowcore.settings.util.Constants;
import sr.we.ui.components.BreadCrumb;
import sr.we.ui.components.Highlight;
import sr.we.ui.components.UIUtil;
import sr.we.ui.views.MainLayout;

import javax.annotation.security.RolesAllowed;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@BreadCrumb(titleKey = "sr.we.dashboard",optimizedMobile = true)
@Route(value = "main-dashboard", layout = MainLayout.class)
@RolesAllowed({Role.user, Role.staff, Role.owner, Role.admin})
public class MainDashboardView extends Main implements BeforeEnterObserver {

    private final BsLayout board;
    private Long businessId;
    private ApexChartsBuilder apexChartsBuilder;
    private Div div;

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
        String theme = UIUtil.Badge.PILL+" primary small";
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
        year.setItems("2011", "2012", "2013", "2014", "2015", "2016", "2017", "2018", "2019", "2020", "2021");
        year.setValue("2021");
        year.setWidth("100px");

        HorizontalLayout header = createHeader("Cash Flow", "Cash coming in and going out of your business.");
        header.add(year);

        // Chart
        apexChartsBuilder = ApexChartsBuilder.get();
//        Configuration conf = chart.getConfiguration();
//        conf.getChart().setStyledMode(true);
        NoData noData = new NoData();
        noData.setText("No data present at the moment");
        apexChartsBuilder = apexChartsBuilder.withChart(ChartBuilder.get().withType(Type.line).withHeight("400px").withZoom(ZoomBuilder.get().withEnabled(true).build()).build())//
                .withStroke(StrokeBuilder.get().withCurve(Curve.smooth).build())//
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

                DashboardService dashboardService = ContextProvider.getBean(DashboardService.class);
//                List<DashboardTransactionEvents> transactionEvents = dashboardService.getTransactionEvents(token, businessId);
                current.access(() -> {
                    Series<Number>[] listSeries = new Series[2/*transactionEvents.size()*/];
//                    for (DashboardTransactionEvents dashboardTransactionEvents : transactionEvents) {

                    Series<Number> flowin = new Series<>();
                    flowin.setName("Cash-in");
                    flowin.setData(new Number[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
                    listSeries[0] = flowin;

                    Series<Number> flowout = new Series<>();
                    flowout.setName("Cash-out");
                    flowout.setData(new Number[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
                    listSeries[1] = flowout;
//                    }
                    apexChartsBuilder = apexChartsBuilder.withSeries(listSeries);
                    div.removeAll();
                    div.add(apexChartsBuilder.build());
                });
            }
        }).start();


//        conf.addSeries(new ListSeries("Berlin", 189, 191, 191, 196, 201, 203, 209, 212, 229, 242, 244, 247));
//        conf.addSeries(new ListSeries("London", 138, 146, 148, 148, 152, 153, 163, 173, 178, 179, 185, 187));
//        conf.addSeries(new ListSeries("New York", 65, 65, 66, 71, 93, 102, 108, 117, 127, 129, 135, 136));
//        conf.addSeries(new ListSeries("Tokyo", 0, 11, 17, 23, 30, 42, 48, 49, 52, 54, 58, 62));

        // Add it all together
        div = new Div();
        div.setWidthFull();
        VerticalLayout viewEvents = new VerticalLayout(header, div);
        viewEvents.addClassName(LumoUtility.Padding.XSMALL);
        viewEvents.setPadding(false);
        viewEvents.setSpacing(false);
        viewEvents.getElement().getThemeList().add("spacing-s");
        return viewEvents;
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
        })).setHeader("").setFlexGrow(0).setAutoWidth(true);
        grid.addColumn(f -> f.getName() + "(" + f.getAccountNumber() + ")").setHeader("Account").setFlexGrow(1);
        grid.addColumn(f -> Constants.CURRENCY_FORMAT.format(f.getIn())).setHeader("IN").setAutoWidth(true).setTextAlign(ColumnTextAlign.END);
        grid.addColumn(f -> Constants.CURRENCY_FORMAT.format(f.getOut())).setHeader("OUT").setAutoWidth(true).setTextAlign(ColumnTextAlign.END);


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
        Optional<String> business = event.getRouteParameters().get("business");
        String token = AuthenticatedUser.token();
        if (business.isPresent()) {
            String businessString = business.get();
            businessId = Long.valueOf(businessString);
        }
        DashboardService dashboardService = ContextProvider.getBean(DashboardService.class);
        if (business != null) {
            String format = Constants.CURRENCY_FORMAT.format(BigDecimal.ZERO);
            board.withRows(new BsRow().withColumns(//
                            new BsColumn(new Highlight("Overdue payments", () -> format, () -> 0d)).withSize(BsColumn.Size.XS), //
                            new BsColumn(new Highlight("Overdue Bills", () -> /*"54.6k"*/format, () -> /*-112.45*/0d)).withSize(BsColumn.Size.XS), //
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
