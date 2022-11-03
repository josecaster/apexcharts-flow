package sr.we.ui.components.general;

import com.vaadin.flow.component.select.Select;
import sr.we.ContextProvider;
import sr.we.data.controller.PojoService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Currency;

import java.util.List;
import java.util.Optional;

public class CurrencySelect extends Select<Currency> {

    private List<Currency> currencies;
    private boolean codeOnly;

    public CurrencySelect() {
        this(false);
    }

    public CurrencySelect(boolean codeOnly) {
        this.codeOnly = codeOnly;
        load();
    }

    public void load() {
        PojoService pojoService = ContextProvider.getBean(PojoService.class);
        String token = AuthenticatedUser.token();

        currencies = pojoService.listCurrency(token).getResult();
        setItems(currencies);

        setItemLabelGenerator((f) -> codeOnly ? f.getCode() : f.getName());
    }

    public String getTranslation() {
        return getTranslation("sr.we.business.currency");
    }

    public void setValueId(Long currency) {
        if(currency == null){
            setValue(null);
            return;
        }
        Optional<Currency> any = getCurrency(currency);
        if(any.isPresent()){
            setValue(any.get());
        } else {
            setValue(null);
        }
    }

    public Optional<Currency> getCurrency(Long currency) {
        Optional<Currency> any = currencies.stream().filter(f -> f.getId().compareTo(currency) == 0).findAny();
        return any;
    }
}
