package sr.we.ui.components.general;

import com.vaadin.flow.component.select.Select;
import sr.we.ContextProvider;
import sr.we.data.controller.PojoService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Currency;

import java.util.List;
import java.util.Optional;

public class BusinessCurrencySelect extends CurrencySelect {

    public BusinessCurrencySelect() {
        super();
        setLabel(getTranslation());
        setHelperText(getTranslation("sr.we.business.currency.info"));
    }


    public String getTranslation() {
        return getTranslation("sr.we.business.currency");
    }
}
