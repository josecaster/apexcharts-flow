package sr.we.ui.components.general;

import com.vaadin.flow.component.select.Select;
import sr.we.ContextProvider;
import sr.we.data.controller.BankService;
import sr.we.data.controller.PojoService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Bank;
import sr.we.shekelflowcore.entity.Currency;

import java.util.List;

public class BankSelect extends Select<Bank> {

    public BankSelect(Long businessId) {
        load(businessId);
        setItemLabelGenerator((f) -> f.getName());

        setLabel(getTranslation());
    }

    public void load(Long businessId) {
        if(businessId != null){
            BankService pojoService = ContextProvider.getBean(BankService.class);
            String token = AuthenticatedUser.token();

            List<Bank> currencies = pojoService.list(token, businessId);
            setItems(currencies);
        }
    }

    public String getTranslation() {
        return getTranslation("sr.we.bank");
    }
}
