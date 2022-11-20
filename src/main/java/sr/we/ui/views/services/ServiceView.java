package sr.we.ui.views.services;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import sr.we.ContextProvider;
import sr.we.data.controller.ItemsService;
import sr.we.data.controller.UserAccessService;
import sr.we.demo.about.AboutView;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Items;
import sr.we.shekelflowcore.entity.Role;
import sr.we.shekelflowcore.security.Privileges;
import sr.we.shekelflowcore.security.privileges.ServicesPrivilege;
import sr.we.ui.components.BreadCrumb;
import sr.we.ui.components.UIUtil;
import sr.we.ui.views.MainLayout;
import sr.we.ui.views.currencyexchange.CurrencyExchangeDataProvider;
import sr.we.ui.views.pos.DataProviders;

import javax.annotation.security.RolesAllowed;
import java.util.*;

/**
 * A Designer generated component for the service-view template.
 * <p>
 * Designer will add and remove fields with @Id mappings but
 * does not overwrite or otherwise change this file.
 */
@Tag("service-view")
@JsModule("./src/views/services/service-view.ts")
@BreadCrumb(titleKey = "sr.we.products.services")
@Route(value = "services", layout = MainLayout.class)
@RolesAllowed({Role.user, Role.staff, Role.owner, Role.admin})
public class ServiceView extends LitTemplate implements BeforeEnterObserver {

    private final Grid<Items> grid;
    @Id("product-grid-layout")
    private Div productGridLayout;
    @Id("filter-field")
    private TextField filterField;
    @Id("export-btn")
    private Button exportBtn;
    @Id("import-btn")
    private Button importBtn;
    @Id("add-product-btn")
    private Button addProductBtn;
    private String business;

    public static String getLocation(String business) {
        return MainLayout.getLocation(business) + "/view-loan/";
    }

    /**
     * Creates a new ProductView.
     */
    public ServiceView() {
        // You can initialise any data required for the connected UI components here.
        addProductBtn.addClickListener(f -> UI.getCurrent().navigate(AddServiceView.class, new RouteParameters(new RouteParam("business", business))));

        grid = new Grid();
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        grid.addColumn(Items::getName).setHeader("Service name");
        grid.addComponentColumn(f -> {
            if (f.getActive()) {
                Span active = new Span("Active");
                active.getElement().getThemeList().add(UIUtil.Badge.PILL+" success");
                active.getElement().getStyle().set("height", "fit-content");
                return active;
            } else {
                Span archive = new Span("Inactive");
                archive.getElement().getThemeList().add(UIUtil.Badge.PILL+" contrast");
                archive.getElement().getStyle().set("height", "fit-content");
                return archive;
            }
        }).setHeader("Status");
        grid.addColumn(Items::getType).setHeader("Type");
        grid.setAllRowsVisible(true);
        grid.addItemDoubleClickListener(f -> {
            Items service = f.getItem();
            if (service == null) {
                return;
            }
            List<String> strings = Arrays.asList(service.getId().toString());
            Map<String, List<String>> map = new HashMap<>();
            map.put("id", strings);
            QueryParameters queryParameters = new QueryParameters(map);
            UI.getCurrent().navigate(EditServiceView.getLocation(Long.valueOf(business).toString()), queryParameters);
        });
        productGridLayout.add(grid);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        UserAccessService userAccesService = ContextProvider.getBean(UserAccessService.class);
        boolean hasAccess = userAccesService.hasAccess(AuthenticatedUser.token(), new ServicesPrivilege(), Privileges.READ);
        if (!hasAccess) {
            UI.getCurrent().navigate(AboutView.class);
        }
        Optional<String> business1 = event.getRouteParameters().get("business");
        business1.ifPresent(s -> business = s);

        ItemsService itemsService = ContextProvider.getBean(ItemsService.class);
//        List<Items> list = itemsService.list(AuthenticatedUser.token(), Long.valueOf(business)).getResult();
        grid.setItems(DataProviders.getItems(business));
    }

}
