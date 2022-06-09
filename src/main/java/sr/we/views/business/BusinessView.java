package sr.we.views.business;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.SpringVaadinSession;
import sr.we.ContextProvider;
import sr.we.data.controller.BusinessService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Business;
import sr.we.shekelflowcore.entity.Role;
import sr.we.views.LineAwesomeIcon;
import sr.we.views.MainLayout;
import sr.we.views.TableLayout;
import sr.we.views.settings.SettingsLayout;

import javax.annotation.security.RolesAllowed;
import java.util.Arrays;
import java.util.List;

@Route(value = "my-businesses", layout = SettingsLayout.class)
@RolesAllowed({Role.user,Role.staff,Role.owner,Role.admin})
public class BusinessView extends TableLayout implements AfterNavigationObserver, BeforeEnterObserver, HasDynamicTitle, BeforeLeaveObserver {

    private Grid<Business> grid;

    public BusinessView() {
        this.setSizeFull();
        this.grid = new Grid<>(Business.class);
        this.grid.setMaxWidth("500px");

        this.grid.addThemeVariants(GridVariant.LUMO_NO_BORDER,GridVariant.LUMO_NO_ROW_BORDERS,GridVariant.LUMO_COMPACT,GridVariant.LUMO_NO_BORDER);
        add(grid);
        this.grid.removeAllColumns();
        this.grid.addColumn("name");
        this.grid.addComponentColumn(new ValueProvider<Business, Button>() {
            @Override
            public Button apply(Business business) {
                Button button = new Button(new LineAwesomeIcon("la la-edit"));
                button.addClickListener(f -> {
                    QueryParameters queryParameters = QueryParameters.fromString("id=" + business.getId());
                    UI.getCurrent().navigate("editbusiness",queryParameters);
                });
                return button;
            }
        }).setHeader(getTranslation("sr.we.actions"));

    }

    @Override
    protected void onCreateClick() {
        UI.getCurrent().navigate(BusinessViewCreate.class);
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String token = AuthenticatedUser.token();
        UI current = UI.getCurrent();
        new Thread(new Runnable() {
            @Override
            public void run() {
                BusinessService businessService = ContextProvider.getBean(BusinessService.class);
                List<Business> business = businessService.list(token);
                current.access(() -> {
                    grid.setItems(business);
                    grid.getDataProvider().refreshAll();
                });
            }
        }).start();
    }

    @Override
    public String getPageTitle() {
        return getTranslation("sr.we.your.businesses");
    }

    @Override
    public void beforeLeave(BeforeLeaveEvent event) {
    }
}
