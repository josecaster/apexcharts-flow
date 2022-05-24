package sr.we.views.business;

import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.spring.SpringVaadinSession;
import sr.we.ContextProvider;
import sr.we.data.controller.PojoService;
import sr.we.shekelflowcore.entity.Country;

import java.util.List;

public class CountrySelect extends Select<Country> {

    public CountrySelect() {
        PojoService pojoService = ContextProvider.getBean(PojoService.class);
        String token = (String) SpringVaadinSession.getCurrent().getAttribute("Token");

        List<Country> countries = pojoService.listCountry(token);
        setItems(countries);

        setItemLabelGenerator((f) -> f.getName());

        setLabel(getTranslation("sr.we.country"));
        setHelperText(getTranslation("sr.we.country.info"));
    }
}
