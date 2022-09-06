package sr.we.ui.views.dashboard;


import com.vaadin.flow.component.Component;
//import com.vaadin.flow.component.board.Board;
//import com.vaadin.flow.component.charts.Chart;
//import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.board.Board;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.*;

import javax.annotation.security.RolesAllowed;

import sr.we.ContextProvider;
import sr.we.data.controller.DashboardService;
import sr.we.data.controller.UserAccessService;
import sr.we.demo.about.AboutView;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Role;
import sr.we.shekelflowcore.entity.helper.adapter.DashboardAccounts;
import sr.we.shekelflowcore.entity.helper.adapter.DashboardTransactionEvents;
import sr.we.shekelflowcore.security.Privileges;
import sr.we.shekelflowcore.security.privileges.LoanReportPrivilege;
import sr.we.shekelflowcore.security.privileges.LoanRequestPlanPrivilege;
import sr.we.shekelflowcore.settings.util.Constants;
import sr.we.ui.components.Highlight;
import sr.we.ui.views.MainLayout;

import java.time.Month;
import java.util.List;
import java.util.Optional;

@PageTitle("Dashboard")
@Route(value = "dashboard", layout = MainLayout.class)
@RolesAllowed({Role.user, Role.staff, Role.owner, Role.admin})
public class DashboardView extends Main implements BeforeEnterObserver {

    private final Board board;
    private Long businessId;

    public DashboardView() {
        addClassName("dashboard-view");


        board = new Board();

        add(board);
    }

    public static Component createHighlight(String title, String value, Double percentage) {

        return new Highlight(title, () -> value, () -> percentage);
    }

    public Component createViewEvents() {
        // Header
        Select year = new Select();
        year.setItems("2011", "2012", "2013", "2014", "2015", "2016", "2017", "2018", "2019", "2020", "2021");
        year.setValue("2021");
        year.setWidth("100px");

        HorizontalLayout header = createHeader("Transaction events", "Cumulative ((IN/OUT)/month)");
        header.add(year);

        // Chart
        Chart chart = new Chart();
        Configuration conf = chart.getConfiguration();
        conf.getChart().setStyledMode(true);

        XAxis xAxis = new XAxis();
        xAxis.setCategories("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");
        conf.addxAxis(xAxis);

        conf.getyAxis().setTitle("Values");

        PlotOptionsArea plotOptions = new PlotOptionsArea();
        plotOptions.setPointPlacement(PointPlacement.ON);
        conf.addPlotOptions(plotOptions);

        UI current = UI.getCurrent();
        String token = AuthenticatedUser.token();
        new Thread(new Runnable() {
            @Override
            public void run() {

                DashboardService dashboardService = ContextProvider.getBean(DashboardService.class);
                List<DashboardTransactionEvents> transactionEvents = dashboardService.getTransactionEvents(token, businessId);
                current.access(() -> {
                    for (DashboardTransactionEvents dashboardTransactionEvents : transactionEvents) {
                        ListSeries listSeries = new ListSeries();
                        listSeries.setName(dashboardTransactionEvents.getSeries());
                        listSeries.setData(dashboardTransactionEvents.get(Month.JANUARY), //
                                dashboardTransactionEvents.get(Month.FEBRUARY), //
                                dashboardTransactionEvents.get(Month.MARCH), //
                                dashboardTransactionEvents.get(Month.APRIL), //
                                dashboardTransactionEvents.get(Month.MAY), //
                                dashboardTransactionEvents.get(Month.JUNE), //
                                dashboardTransactionEvents.get(Month.JULY), //
                                dashboardTransactionEvents.get(Month.AUGUST), //
                                dashboardTransactionEvents.get(Month.SEPTEMBER), //
                                dashboardTransactionEvents.get(Month.OCTOBER), //
                                dashboardTransactionEvents.get(Month.NOVEMBER),//
                                dashboardTransactionEvents.get(Month.DECEMBER) //
                        );
                        conf.addSeries(listSeries);
                    }
                });
            }
        }).start();





//        conf.addSeries(new ListSeries("Berlin", 189, 191, 191, 196, 201, 203, 209, 212, 229, 242, 244, 247));
//        conf.addSeries(new ListSeries("London", 138, 146, 148, 148, 152, 153, 163, 173, 178, 179, 185, 187));
//        conf.addSeries(new ListSeries("New York", 65, 65, 66, 71, 93, 102, 108, 117, 127, 129, 135, 136));
//        conf.addSeries(new ListSeries("Tokyo", 0, 11, 17, 23, 30, 42, 48, 49, 52, 54, 58, 62));

        // Add it all together
        VerticalLayout viewEvents = new VerticalLayout(header, chart);
        viewEvents.addClassName("p-l");
        viewEvents.setPadding(false);
        viewEvents.setSpacing(false);
        viewEvents.getElement().getThemeList().add("spacing-l");
        return viewEvents;
    }

    public Component createServiceHealth() {
        // Header
        HorizontalLayout header = createHeader("Ledgers", "Input / output");

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
        grid.addColumn(f -> f.getName()+"("+f.getAccountNumber()+")").setHeader("Account").setFlexGrow(1);
        grid.addColumn(f -> Constants.CURRENCY_FORMAT.format(f.getIn())).setHeader("IN").setAutoWidth(true).setTextAlign(ColumnTextAlign.END);
        grid.addColumn(f -> Constants.CURRENCY_FORMAT.format(f.getOut())).setHeader("OUT").setAutoWidth(true).setTextAlign(ColumnTextAlign.END);


        UI current = UI.getCurrent();
        String token = AuthenticatedUser.token();
        new Thread(new Runnable() {
            @Override
            public void run() {

                DashboardService dashboardService = ContextProvider.getBean(DashboardService.class);

                List<DashboardAccounts> accounts = dashboardService.getAccounts(token, businessId);
                current.access(() -> {
                    grid.setItems(accounts);
                });
            }
        }).start();



        // Add it all together
        VerticalLayout serviceHealth = new VerticalLayout(header, grid);
        serviceHealth.addClassName("p-l");
        serviceHealth.setPadding(false);
        serviceHealth.setSpacing(false);
        serviceHealth.getElement().getThemeList().add("spacing-l");
        return serviceHealth;
    }

    public static Component createResponseTimes() {
        HorizontalLayout header = createHeader("Response times", "Average across all systems");

        // Chart
        Chart chart = new Chart();
        Configuration conf = chart.getConfiguration();
        conf.getChart().setStyledMode(true);

        DataSeries series = new DataSeries();
        series.add(new DataSeriesItem("System 1", 12.5));
        series.add(new DataSeriesItem("System 2", 12.5));
        series.add(new DataSeriesItem("System 3", 12.5));
        series.add(new DataSeriesItem("System 4", 12.5));
        series.add(new DataSeriesItem("System 5", 12.5));
        series.add(new DataSeriesItem("System 6", 12.5));
        conf.addSeries(series);

        // Add it all together
        VerticalLayout serviceHealth = new VerticalLayout(header, chart);
        serviceHealth.addClassName("p-l");
        serviceHealth.setPadding(false);
        serviceHealth.setSpacing(false);
        serviceHealth.getElement().getThemeList().add("spacing-l");
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
        String theme = "badge primary small";
        if (serviceHealth.getIn().compareTo(serviceHealth.getOut()) > 0) {
            theme += " success";
        } else if (serviceHealth.getIn().compareTo(serviceHealth.getOut()) < 0) {
            theme += " error";
        }
        return theme;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        UserAccessService userAccesService = ContextProvider.getBean(UserAccessService.class);
        boolean hasAccess = userAccesService.hasAccess(AuthenticatedUser.token(), new LoanReportPrivilege(), Privileges.READ);
        if(!hasAccess){
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
            board.addRow(new Highlight("Active Loan Requests", () -> dashboardService.getActiveLoanRequests(token, businessId).getValue(), () -> dashboardService.getActiveLoanRequests(token, businessId).getPercentage()), //
                    new Highlight("Overdue", () -> /*"54.6k"*/dashboardService.getOverdue(token, businessId).getValue(), () -> /*-112.45*/dashboardService.getOverdue(token, businessId).getPercentage()), //
                    new Highlight("Next Payments ", () -> /*"54.6k"*/dashboardService.getMonthsPayments(token, businessId).getValue(), () -> /*-112.45*/dashboardService.getMonthsPayments(token, businessId).getPercentage()), //
                    new Highlight("Transactions YTD", () -> /*"54.6k"*/dashboardService.getProfits(token, businessId).getValue(), () -> /*-112.45*/dashboardService.getProfits(token, businessId).getPercentage()));
            board.addRow(createViewEvents());
            board.addRow(createServiceHealth(), createResponseTimes());
        }
    }
}
