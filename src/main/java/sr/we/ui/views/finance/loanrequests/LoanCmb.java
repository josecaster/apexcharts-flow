package sr.we.ui.views.finance.loanrequests;

import com.vaadin.flow.component.combobox.ComboBox;
import sr.we.ContextProvider;
import sr.we.data.controller.CustomerService;
import sr.we.data.controller.LoanService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Customer;
import sr.we.shekelflowcore.entity.Loan;

import java.util.List;

public class LoanCmb extends ComboBox<Loan> {

    public LoanCmb() {

        setItemLabelGenerator(f -> {
            return f.getName();
        });

    }

    public void load(Long businessId) {
        LoanService loanService = ContextProvider.getBean(LoanService.class);
        List<Loan> list = loanService.list(AuthenticatedUser.token(), businessId);
        setItems(list);
    }
}
