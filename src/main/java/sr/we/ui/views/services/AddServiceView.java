package sr.we.ui.views.services;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import sr.we.ContextProvider;
import sr.we.data.controller.UserAccessService;
import sr.we.demo.about.AboutView;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Role;
import sr.we.shekelflowcore.security.Privileges;
import sr.we.shekelflowcore.security.privileges.ProductsPrivilege;
import sr.we.shekelflowcore.security.privileges.ServicesPrivilege;
import sr.we.ui.components.BreadCrumb;
import sr.we.ui.views.MainLayout;
import sr.we.ui.views.products.AddProducts;

import javax.annotation.security.RolesAllowed;

@BreadCrumb(titleKey = "sr.we.products.services.create",parentNavigationTarget = ServiceView.class)
@Route(value = "add-service", layout = MainLayout.class)
@RolesAllowed({Role.user, Role.staff, Role.owner, Role.admin})
public class AddServiceView extends AddService implements BeforeEnterObserver {


    /**
     * Creates a new AddProducts.
     */
    public AddServiceView() {
        super();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        UserAccessService userAccesService = ContextProvider.getBean(UserAccessService.class);
        boolean hasAccess = userAccesService.hasAccess(AuthenticatedUser.token(), new ServicesPrivilege(), Privileges.INSERT);
        if (!hasAccess) {
            UI.getCurrent().navigate(AboutView.class);
        }

        setServices(event);
    }



}
