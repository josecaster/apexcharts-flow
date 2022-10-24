package sr.we.ui.views;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import sr.we.ContextProvider;
import sr.we.data.controller.BusinessService;
import sr.we.data.controller.UserAccessService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Business;
import sr.we.shekelflowcore.entity.Role;
import sr.we.shekelflowcore.entity.ThisUser;
import sr.we.shekelflowcore.entity.helper.Token;
import sr.we.shekelflowcore.security.Privileges;
import sr.we.shekelflowcore.security.privileges.LoanReportPrivilege;
import sr.we.ui.views.dashboard.DashboardView;
import sr.we.ui.views.login.NotActiveDialog;
import sr.we.ui.views.person.PersonView;
import sr.we.ui.views.personform.PersonFormView;

import javax.annotation.security.RolesAllowed;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Route("u")
@RolesAllowed({Role.user, Role.staff, Role.owner, Role.admin})
public class ReRouteLayout extends VerticalLayout implements BeforeEnterObserver {


//    @Override
//    public void beforeEnter(BeforeEnterEvent event) {
////        RouteConfiguration routeConfiguration = RouteConfiguration.forSessionScope();
////        routeConfiguration.setRoute("en_US", MainLayout.class);
////        routeConfiguration.setRoute("nl_NL", MainLayout.class);
////        routeConfiguration.setRoute("en_US/dashboard", DashboardView.class, MainLayout.class);
////        routeConfiguration.setRoute("nl_NL/dashboard", DashboardView.class, MainLayout.class);
////        routeConfiguration.setRoute("nl_NL/about", AboutView.class, MainLayout.class);
////        routeConfiguration.setRoute("en_US/about", AboutView.class, MainLayout.class);
//
//        Cookie[] cookies = VaadinRequest.getCurrent().getCookies();
//        if (cookies != null && cookies.length >= 1) {
//            Optional<Cookie> any = Arrays.stream(cookies).filter(f -> f.getName().equalsIgnoreCase("my-lang")).findAny();
//            if (any.isPresent()) {
//                Cookie cookie = any.get();
//                if (TranslationProvider.LOCALE_EN.getLanguage().equalsIgnoreCase(cookie.getValue())) {
//                    event.forwardTo(MainLayout.class,new RouteParameters( new RouteParam("lang","en_US")));
//                } else if (TranslationProvider.LOCALE_NL.getLanguage().equalsIgnoreCase(cookie.getValue())) {
//                    event.forwardTo(MainLayout.class,new RouteParameters( new RouteParam("lang","nl_NL")));
//                }
//            } else {
//                event.forwardTo(MainLayout.class,new RouteParameters( new RouteParam("lang","en_US")));
//            }
//        }
//    }


    @Override
    public void beforeEnter(BeforeEnterEvent event) {

        AuthenticatedUser bean = ContextProvider.getBean(AuthenticatedUser.class);
        Optional<ThisUser> thisUser = bean.get();
        if (!thisUser.isPresent()) {
            bean.logout();
        }

        ThisUser thisUser1 = thisUser.get();
        // basic details
        if (thisUser1.getPerson() == null) {
            event.forwardTo(PersonView.class);
            return;
        }

        // basic details
        if (thisUser1.getPerson().getDefaultForms() == null) {
            event.forwardTo(PersonFormView.class);
            return;
        }

        // inactive user
        if (thisUser1.getActive() == null || !thisUser1.getActive()) {
            new NotActiveDialog().open();
            return;
        }


        Token token = thisUser1.getToken();
        BusinessService businessService = ContextProvider.getBean(BusinessService.class);

        List<Business> businesses = businessService.list(token.getToken()).getResult();
        if (businesses == null) {
            businesses = new ArrayList<>();
        }
        Business business = businessService.get(null, AuthenticatedUser.token());
        Optional<Business> max = businesses.stream().filter(f -> f.getCounter().compareTo(0L) != 0).max(Comparator.comparingLong(Business::getCounter));
        UserAccessService userAccessService = ContextProvider.getBean(UserAccessService.class);
        if(userAccessService.hasAccess(AuthenticatedUser.token(),new LoanReportPrivilege(), Privileges.READ)) {
            if (business != null) {
                event.forwardTo(DashboardView.class, new RouteParameters(new RouteParam("business", business.getId().toString())));
            } else {
                event.forwardTo(DashboardView.class, new RouteParameters(new RouteParam("business", "0")));
            }
        } else {
            if (business != null) {
                event.forwardTo(MainLayout.class, new RouteParameters(new RouteParam("business", business.getId().toString())));
            } else {
                event.forwardTo(MainLayout.class, new RouteParameters(new RouteParam("business", "0")));
            }
        }

    }
}
