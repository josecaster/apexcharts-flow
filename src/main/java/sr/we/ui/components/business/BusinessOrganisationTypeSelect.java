package sr.we.ui.components.business;

import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.spring.SpringVaadinSession;
import sr.we.ContextProvider;
import sr.we.data.controller.PojoService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.BusinessOrganisationType;
import sr.we.shekelflowcore.entity.BusinessType;

import java.util.List;

public class BusinessOrganisationTypeSelect extends Select<BusinessOrganisationType> {

    public BusinessOrganisationTypeSelect() {
        PojoService pojoService = ContextProvider.getBean(PojoService.class);
        String token = AuthenticatedUser.token();

        List<BusinessOrganisationType> businessOrganisationTypes = pojoService.listBusinessOrganisationType(token);
        setItems(businessOrganisationTypes);

        setItemLabelGenerator((f) -> f.getName());

        setLabel(getTranslation("sr.we.type.of.organization"));
        setHelperText(getTranslation("sr.we.type.of.organization.info"));
    }
}
