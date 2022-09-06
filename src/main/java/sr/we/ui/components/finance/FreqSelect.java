package sr.we.ui.components.finance;

import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.select.SelectVariant;
import sr.we.shekelflowcore.entity.Loan;

public class FreqSelect extends Select<Loan.Freq> {

    public FreqSelect() {
        setItems(Loan.Freq.values());

        setItemLabelGenerator((f) -> f.getCaption());
        addThemeVariants(SelectVariant.LUMO_SMALL);

    }
}
