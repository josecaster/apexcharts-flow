package sr.we.ui.views.invoice;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import sr.we.ContextProvider;
import sr.we.data.controller.BusinessService;
import sr.we.data.controller.InvoiceService;
import sr.we.data.controller.LoanRequestService;
import sr.we.data.controller.UserAccessService;
import sr.we.demo.about.AboutView;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Role;
import sr.we.shekelflowcore.exception.ValidationException;
import sr.we.shekelflowcore.security.Privileges;
import sr.we.shekelflowcore.security.privileges.POSPrivilege;
import sr.we.ui.views.MainLayout;
import sr.we.ui.views.ReRouteLayout;
import sr.we.ui.views.pos.DataProviders;
import sr.we.ui.views.pos.ProductOrService;

import javax.annotation.security.RolesAllowed;
import java.util.List;
import java.util.Optional;

import static sr.we.ContextProvider.getBean;

/**
 * A Designer generated component for the create-invoice-view template.
 * <p>
 * Designer will add and remove fields with @Id mappings but
 * does not overwrite or otherwise change this file.
 */
@Route(value = "invoice-edit", layout = MainLayout.class)
@RolesAllowed({Role.user, Role.staff, Role.owner, Role.admin})
public class EditInvoiceView extends CreateInvoiceView implements BeforeEnterObserver {

    public static String getLocation(String business) {
        return MainLayout.getLocation(business) + "/invoice-edit";
    }


    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        UserAccessService userAccesService = ContextProvider.getBean(UserAccessService.class);
        boolean hasAccess = userAccesService.hasAccess(AuthenticatedUser.token(), new POSPrivilege(), Privileges.INSERT);
        if (!hasAccess) {
            UI.getCurrent().navigate(AboutView.class);
        }
        Optional<String> business1 = event.getRouteParameters().get("business");
        business1.ifPresent(s -> business = s);
        BusinessService businessService = ContextProvider.getBean(BusinessService.class);
        setBusiness2(businessService.get(Long.valueOf(business), AuthenticatedUser.token()));

        CallbackDataProvider<ProductOrService, String> dataProvider = null;
        filterCmb.setPlaceholder("choose an item");
        dataProvider = DataProviders.getServices(business);
        filterCmb.setItems(dataProvider);

        QueryParameters queryParams = event.getLocation().getQueryParameters();
        List<String> id1 = queryParams.getParameters().get("id");
        Optional<String> id = id1.stream().findAny();
        if (id.isEmpty()) {
            event.forwardTo(ReRouteLayout.class);
            throw new ValidationException("Invalid Link");
        }
        Long posHeaderId = Long.valueOf(id.get());
        setByPosHeaderId(event, posHeaderId);

    }



}
