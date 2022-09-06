package sr.we.ui.views.products;

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
import sr.we.data.controller.ProductService;
import sr.we.data.controller.UserAccessService;
import sr.we.demo.about.AboutView;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Product;
import sr.we.shekelflowcore.entity.Role;
import sr.we.shekelflowcore.security.Privileges;
import sr.we.shekelflowcore.security.privileges.LoanPrivilege;
import sr.we.shekelflowcore.security.privileges.ProductsPrivilege;
import sr.we.ui.views.MainLayout;

import javax.annotation.security.RolesAllowed;
import java.util.*;

/**
 * A Designer generated component for the product-view template.
 *
 * Designer will add and remove fields with @Id mappings but
 * does not overwrite or otherwise change this file.
 */
@Tag("product-view")
@JsModule("./src/views/products/product-view.ts")
@Route(value = "products", layout = MainLayout.class)
@RolesAllowed({Role.user, Role.staff, Role.owner, Role.admin})
public class ProductView extends LitTemplate implements BeforeEnterObserver {

    private final Grid<Product> grid;
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
    public ProductView() {
        // You can initialise any data required for the connected UI components here.
        addProductBtn.addClickListener(f -> UI.getCurrent().navigate(AddProductsView.class, new RouteParameters(new RouteParam("business", business))));

        grid = new Grid();
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        grid.addColumn(Product::getTitle).setHeader("Product");
        grid.addComponentColumn(f -> {
            if(f.getStatus().compareTo(Product.Status.Active) == 0){
                Span active = new Span("Active");
                active.getElement().getThemeList().add("badge success");
                active.getElement().getStyle().set("height","fit-content");
                return active;
            } else if (f.getStatus().compareTo(Product.Status.Draft) == 0){
                Span draft = new Span("Draft");
                draft.getElement().getThemeList().add("badge");
                draft.getElement().getStyle().set("height","fit-content");
                return draft;
            } else {
                Span archive = new Span("Archive");
                archive.getElement().getThemeList().add("badge contrast");
                archive.getElement().getStyle().set("height","fit-content");
                return archive;
            }
        }).setHeader("Status");
        grid.addColumn(Product::getInventory).setHeader("Inventory");
        grid.addColumn(Product::getType).setHeader("Type");
        grid.setAllRowsVisible(true);
        grid.addItemDoubleClickListener(f -> {
            Product product = f.getItem();
            if(product == null){
                return;
            }
            List<String> strings = Arrays.asList(product.getId().toString());
            Map<String, List<String>> map = new HashMap<>();
            map.put("id", strings);
            QueryParameters queryParameters = new QueryParameters(map);
            UI.getCurrent().navigate(EditProductsView.getLocation(Long.valueOf(business).toString()),queryParameters);
        });
        productGridLayout.add(grid);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        UserAccessService userAccesService = ContextProvider.getBean(UserAccessService.class);
        boolean hasAccess = userAccesService.hasAccess(AuthenticatedUser.token(), new ProductsPrivilege(), Privileges.READ);
        if(!hasAccess){
            UI.getCurrent().navigate(AboutView.class);
        }
        Optional<String> business1 = event.getRouteParameters().get("business");
        if (business1.isPresent()) {
            business = business1.get();
        }

        ProductService productService = ContextProvider.getBean(ProductService.class);
        List<Product> list = productService.list(AuthenticatedUser.token(), Long.valueOf(business));
        grid.setItems(list);
    }

}
