package sr.we.ui.components.finance;

import com.vaadin.flow.component.select.Select;
import sr.we.ContextProvider;
import sr.we.data.controller.LoanAssetsService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.LoanAssets;

public class LoanAssetsSelect extends Select<LoanAssets> {

    public LoanAssetsSelect() {
    }

    public LoanAssetsSelect(Long loanId) {
        load(loanId);
    }

    public void load(Long loanId) {
        LoanAssetsService loanService = ContextProvider.getBean(LoanAssetsService.class);
        setItems(loanService.list(AuthenticatedUser.token(), Long.valueOf(loanId)).getResult());
        setItemLabelGenerator((f) -> f.getName());
    }

}
