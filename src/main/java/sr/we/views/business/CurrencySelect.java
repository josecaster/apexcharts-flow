package sr.we.views.business;

import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.spring.SpringVaadinSession;
import sr.we.ContextProvider;
import sr.we.data.controller.PojoService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Country;
import sr.we.shekelflowcore.entity.Currency;

import java.util.List;

public class CurrencySelect extends Select<Currency> {

    public CurrencySelect() {
        PojoService pojoService = ContextProvider.getBean(PojoService.class);
        String token = AuthenticatedUser.token();

        List<Currency> currencies = pojoService.listCurrency(token);
        setItems(currencies);

        setItemLabelGenerator((f) -> f.getName());

        setLabel(getTranslation());
        setHelperText(getTranslation("sr.we.business.currency.info"));
    }

    public String getTranslation() {
        return getTranslation("sr.we.business.currency");
    }
}
