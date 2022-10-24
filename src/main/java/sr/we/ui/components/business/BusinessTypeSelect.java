package sr.we.ui.components.business;

import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.spring.SpringVaadinSession;
import sr.we.ContextProvider;
import sr.we.data.controller.PojoService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.BusinessType;
import sr.we.shekelflowcore.entity.Currency;

import java.util.List;

public class BusinessTypeSelect extends Select<BusinessType> {

    public BusinessTypeSelect() {
        PojoService pojoService = ContextProvider.getBean(PojoService.class);
        String token = AuthenticatedUser.token();

        List<BusinessType> businessTypes = pojoService.listBusinessType(token).getResult();
        setItems(businessTypes);

        setItemLabelGenerator((f) -> f.getName());

        setLabel(getTranslation("sr.we.type.of.business"));
        setHelperText(getTranslation("sr.we.type.of.business.info"));
    }
}
