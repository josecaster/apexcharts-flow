package sr.we.views.business;

import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.spring.SpringVaadinSession;
import sr.we.ContextProvider;
import sr.we.data.controller.PojoService;
import sr.we.shekelflowcore.entity.Country;
import sr.we.shekelflowcore.entity.Currency;

import java.util.List;

public class CurrencySelect extends Select<Currency> {

    public CurrencySelect() {
        PojoService pojoService = ContextProvider.getBean(PojoService.class);
        String token = (String) SpringVaadinSession.getCurrent().getAttribute("Token");

        List<Currency> currencies = pojoService.listCurrency(token);
        setItems(currencies);

        setItemLabelGenerator((f) -> f.getName());

        setLabel(getTranslation("sr.we.business.currency"));
        setHelperText(getTranslation("sr.we.business.currency.info"));
    }
}
