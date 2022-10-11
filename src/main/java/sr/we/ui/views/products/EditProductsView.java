package sr.we.ui.views.products;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import sr.we.ContextProvider;
import sr.we.data.controller.ProductService;
import sr.we.data.controller.UserAccessService;
import sr.we.demo.about.AboutView;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Product;
import sr.we.shekelflowcore.entity.Role;
import sr.we.shekelflowcore.exception.ValidationException;
import sr.we.shekelflowcore.security.Privileges;
import sr.we.shekelflowcore.security.privileges.ProductsPrivilege;
import sr.we.ui.views.MainLayout;
import sr.we.ui.views.ReRouteLayout;

import javax.annotation.security.RolesAllowed;
import java.util.List;
import java.util.Optional;

import static sr.we.ContextProvider.getBean;

@Deprecated
//@Route(value = "edit-products", layout = MainLayout.class)
//@RolesAllowed({Role.user, Role.staff, Role.owner, Role.admin})
public class EditProductsView extends AddProducts /*implements BeforeEnterObserver*/ {



    /**
     * Creates a new AddProducts.
     */
    public EditProductsView() {
    }

    public static String getLocation(String business) {
        return MainLayout.getLocation(business) + "/edit-products";
    }

    /*@Override
    public void beforeEnter(BeforeEnterEvent event) {
        UserAccessService userAccesService = ContextProvider.getBean(UserAccessService.class);
        boolean hasAccess = userAccesService.hasAccess(AuthenticatedUser.token(), new ProductsPrivilege(), Privileges.INSERT);
        if (!hasAccess) {
            UI.getCurrent().navigate(AboutView.class);
        }
        setBusiness(event);
        setProduct(event);
    }*/



}
