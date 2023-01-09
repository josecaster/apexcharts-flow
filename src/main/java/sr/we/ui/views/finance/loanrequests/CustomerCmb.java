package sr.we.ui.views.finance.loanrequests;

import com.vaadin.flow.component.combobox.ComboBox;
import org.apache.commons.lang3.StringUtils;
import sr.we.ContextProvider;
import sr.we.data.controller.CustomerService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Customer;
import sr.we.shekelflowcore.entity.Loan;
import sr.we.shekelflowcore.entity.helper.vo.CustomerVO;

import java.util.List;

public class CustomerCmb extends ComboBox<Customer> {


    public CustomerCmb() {
        setItemLabelGenerator(f -> {
            return f.getName() + (StringUtils.isBlank(f.getFirstName()) ? "" : " "+ f.getFirstName());
        });
    }

    public void load(Long businessId){
        CustomerService customerService = ContextProvider.getBean(CustomerService.class);
        CustomerVO customerVO = new CustomerVO();customerVO.setBusiness(businessId);
        List<Customer> list = customerService.list(customerVO, AuthenticatedUser.token()).getResult();
        setItems(list);
    }
}
