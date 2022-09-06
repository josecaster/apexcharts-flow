package sr.we.ui.components.finance;

import com.vaadin.flow.component.select.Select;
import sr.we.ContextProvider;
import sr.we.data.controller.PojoService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.BusinessType;
import sr.we.shekelflowcore.entity.Loan;

import java.util.List;

public class FactorTypeSelect extends Select<Loan.FactorType> {

    public FactorTypeSelect() {
        setItems(Loan.FactorType.values());

        setItemLabelGenerator((f) -> f.getCaption());
    }

}
