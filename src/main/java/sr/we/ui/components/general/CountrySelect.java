package sr.we.ui.components.general;

import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.spring.SpringVaadinSession;
import sr.we.ContextProvider;
import sr.we.data.controller.PojoService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Country;
import sr.we.shekelflowcore.entity.helper.MappedSuperClassReference;

import java.util.List;
import java.util.Optional;

public class CountrySelect extends Select<Country> {

    private final List<Country> countries;

    public CountrySelect() {
        PojoService pojoService = ContextProvider.getBean(PojoService.class);
        String token = AuthenticatedUser.token();

        countries = pojoService.listCountry(token);
        setItems(countries);

        setItemLabelGenerator(MappedSuperClassReference::getName);

        setLabel(getTranslation("sr.we.country"));
        setHelperText(getTranslation("sr.we.country.info"));
    }

    public Country getByCode(String code){
        Optional<Country> any = countries.stream().filter(f -> f.getCode().equalsIgnoreCase(code)).findAny();
        return any.orElse(null);
    }
}
