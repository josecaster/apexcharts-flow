package sr.we.ui.views.pos;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import sr.we.ContextProvider;
import sr.we.data.controller.BusinessService;
import sr.we.data.controller.PosHeaderService;
import sr.we.data.controller.UserAccessService;
import sr.we.demo.about.AboutView;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Business;
import sr.we.shekelflowcore.entity.PosHeader;
import sr.we.shekelflowcore.entity.Role;
import sr.we.shekelflowcore.security.Privileges;
import sr.we.shekelflowcore.security.privileges.POSPrivilege;
import sr.we.ui.components.BreadCrumb;
import sr.we.ui.views.MainLayout;

import javax.annotation.security.RolesAllowed;
import java.util.List;
import java.util.Optional;

@BreadCrumb(titleKey = "sr.we.tickets")
@Route(value = "tickets", layout = MainLayout.class)
@RolesAllowed({Role.user, Role.staff, Role.owner, Role.admin})
public class TicketsGridView extends TicketsView implements BeforeEnterObserver {

    private String business;
    private Business business2;


    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        UserAccessService userAccesService = ContextProvider.getBean(UserAccessService.class);
        boolean hasAccess = userAccesService.hasAccess(AuthenticatedUser.token(), new POSPrivilege(), Privileges.READ);
        if (!hasAccess) {
            UI.getCurrent().navigate(AboutView.class);
        }
        Optional<String> business1 = event.getRouteParameters().get("business");
        business1.ifPresent(s -> business = s);
        BusinessService businessService = ContextProvider.getBean(BusinessService.class);
        business2 = businessService.get(Long.valueOf(business), AuthenticatedUser.token());

        refresh();
    }

    private void refresh() {
        PosHeaderService posHeaderService = ContextProvider.getBean(PosHeaderService.class);
        List<PosHeader> list = posHeaderService.list(business2.getId(), null, AuthenticatedUser.token()).getResult();
        setTickets(list, business2, onSave != null ? onSave : () -> {
            refresh();
            return null;
        }, onRefresh != null ? onRefresh : () -> {
            refresh();
            return null;
        });
    }
}
