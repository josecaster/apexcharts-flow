package sr.we.ui.views.business;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import sr.we.ContextProvider;
import sr.we.data.controller.BusinessService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Business;
import sr.we.shekelflowcore.entity.Role;
import sr.we.ui.views.TableLayout;
import sr.we.ui.views.settings.SettingsLayout;

import javax.annotation.security.RolesAllowed;
import java.util.List;

@Route(value = "my-businesses", layout = SettingsLayout.class)
@RolesAllowed({Role.user, Role.staff, Role.owner, Role.admin})
public class BusinessView extends TableLayout implements AfterNavigationObserver, BeforeEnterObserver, HasDynamicTitle, BeforeLeaveObserver {

    private final Grid<Business> grid;

    public BusinessView() {
        this.setSizeFull();
        layout.addClassName("loans-view");
        this.grid = new Grid<>();
//        this.grid.setMaxWidth("500px");

        this.grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_NO_ROW_BORDERS, GridVariant.LUMO_COMPACT, GridVariant.LUMO_NO_BORDER);
        add(grid);
        this.grid.removeAllColumns();
        this.grid.addComponentColumn(business -> createCard(business));
        this.grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        this.grid.addSelectionListener(f -> {
            if (f.getFirstSelectedItem().isPresent()) {
                Business business = f.getFirstSelectedItem().get();
                QueryParameters queryParameters = QueryParameters.fromString("id=" + business.getId());
                UI.getCurrent().navigate("edit-business", queryParameters);
            }
        });
//        this.grid.addComponentColumn(new ValueProvider<Business, Button>() {
//            @Override
//            public Button apply(Business business) {
//                Button button = new Button(new LineAwesomeIcon("la la-edit"));
//                button.addClickListener(f -> {
//                    QueryParameters queryParameters = QueryParameters.fromString("id=" + business.getId());
//                    UI.getCurrent().navigate("edit-business",queryParameters);
//                });
//                return button;
//            }
//        }).setHeader(getTranslation("sr.we.actions"));

    }

    private Component createCard(Business business) {
        HorizontalLayout card = new HorizontalLayout();
        card.addClassName("card");
        card.setSpacing(false);
        card.getThemeList().add("spacing-s");

        VerticalLayout header = new VerticalLayout();
        header.addClassName("header");
        header.setSpacing(false);
        header.getThemeList().add("spacing-s");

        Span name = new Span(business.getName());
        name.addClassName("name");
        Span date = new Span(business.getBusinessType().getName());
        date.addClassName("date");
        header.add(name, date);

        card.add(header);
        return card;
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
                List<Business> business = businessService.list(token).getResult();
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
