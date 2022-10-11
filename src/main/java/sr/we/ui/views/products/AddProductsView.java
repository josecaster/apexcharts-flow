package sr.we.ui.views.products;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.router.*;
import sr.we.ContextProvider;
import sr.we.data.controller.BusinessService;
import sr.we.data.controller.UserAccessService;
import sr.we.demo.about.AboutView;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Business;
import sr.we.shekelflowcore.entity.Currency;
import sr.we.shekelflowcore.entity.Role;
import sr.we.shekelflowcore.security.Privileges;
import sr.we.shekelflowcore.security.privileges.ProductsPrivilege;
import sr.we.ui.views.LineAwesomeIcon;
import sr.we.ui.views.MainLayout;

import javax.annotation.security.RolesAllowed;
import java.util.Optional;

@Deprecated
//@Route(value = "add-products", layout = MainLayout.class)
//@RolesAllowed({Role.user, Role.staff, Role.owner, Role.admin})
public class AddProductsView extends AddProducts /*implements BeforeEnterObserver*/ {


    /**
     * Creates a new AddProducts.
     */
    public AddProductsView() {
    }

    /*@Override
    public void beforeEnter(BeforeEnterEvent event) {
        UserAccessService userAccesService = ContextProvider.getBean(UserAccessService.class);
        boolean hasAccess = userAccesService.hasAccess(AuthenticatedUser.token(), new ProductsPrivilege(), Privileges.INSERT);
        if (!hasAccess) {
            UI.getCurrent().navigate(AboutView.class);
        }
        setBusiness(event);
    }*/

}
