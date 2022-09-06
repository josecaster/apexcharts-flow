package sr.we.ui.views.account;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.RouteParam;
import com.vaadin.flow.router.RouteParameters;
import org.apache.commons.lang3.StringUtils;
import sr.we.ContextProvider;
import sr.we.data.controller.AccountService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.helper.vo.AccountVO;
import sr.we.ui.components.general.BankSelect;
import sr.we.ui.views.StateListenerLayout;
import sr.we.ui.components.general.CurrencySelect;
import sr.we.ui.views.dashboard.DashboardView;

public class AccountNewLayout extends StateListenerLayout {


    private final TextField nameFld;
    private final TextField accountId;
    private final CurrencySelect currencyFld;
    private final BankSelect bankSelect;

    private final FormLayout layout;

    private String business;

    private Long accountType;


    public AccountNewLayout() {
        layout = new FormLayout();

        add(layout);
        layout.setMaxWidth("500px");

        nameFld = new TextField();
        accountId = new TextField();
        currencyFld = new CurrencySelect();
        bankSelect = new BankSelect(null);

        bankSelect.setWidthFull();
        nameFld.setWidthFull();
        accountId.setWidthFull();
        currencyFld.setWidthFull();

        bankSelect.setLabel(null);
        bankSelect.setHelperText(null);

        currencyFld.setLabel(null);
        currencyFld.setHelperText(null);

        // info
        layout.addFormItem(nameFld, getTranslation("sr.we.name"));
        layout.addFormItem(accountId, getTranslation("sr.we.account.id"));

        // start at

        // amount
        layout.addFormItem(bankSelect, getTranslation("sr.we.bank"));
        layout.addFormItem(currencyFld, getTranslation("sr.we.currency"));


        nameFld.setRequired(true);
        nameFld.setRequiredIndicatorVisible(true);


        currencyFld.setRequiredIndicatorVisible(true);

        state(nameFld, accountId, currencyFld,bankSelect);

        layout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
    }

    @Override
    protected void onSave() {

        AccountVO accountVO = new AccountVO();
        accountVO.setName(nameFld.getValue());
        accountVO.setCurrency(currencyFld.getValue().getId());
        accountVO.setAccountId(accountId.getValue());
        accountVO.setAccountType(accountType);
        accountVO.setBusiness(Long.valueOf(business));
        accountVO.setBank(bankSelect.getValue() == null ? null : bankSelect.getValue().getId());

        AccountService bean = ContextProvider.getBean(AccountService.class);
        bean.create(AuthenticatedUser.token(), accountVO);
        redirectToParent();
    }

    private void redirectToParent() {
//        QueryParameters queryParameters = QueryParameters.fromString("id=" + loan.getId());
//        UI.getCurrent().navigate(LoansViewTabRequests.getLocation(business), queryParameters);
        UI.getCurrent().navigate(DashboardView.class, new RouteParameters(new RouteParam("business", business)));
    }

    @Override
    protected void onDiscard() {
        nameFld.clear();
        accountId.clear();
        currencyFld.clear();
        bankSelect.clear();
    }

    @Override
    protected boolean validate() {
        if (nameFld.isEmpty()) {
            return false;
        }
//        if (accountId.isEmpty()) {
//            return false;
//        }
        if (currencyFld.isEmpty()) {
            return false;
        }
        return true;
    }

    public void setBusiness(String business) {
        this.business = business;
        if(StringUtils.isNotBlank(business)){
            bankSelect.load(Long.valueOf(business));
        }
    }

    public void setAccountType(Long accountType) {
        this.accountType = accountType;
    }

    //    public void setLoanRequest(LoanRequest loanRequest) {
//        nameFld.setValue(loanRequest.getCustomer().getName());
//        accountId.setValue(loanRequest.getCustomer().getPrimaryCustomerContacts().getMobile());
//        currencyFld.setValue(loanRequest.getCurrency());
//        setReadOnly(nameFld, accountId, currencyFld);
//        actionLayout.setVisible(false);
//    }
//
//    private void setReadOnly(TextField nameFld, TextField mobileFld, CurrencySelect currencyFld) {
//    nameFld.setReadOnly(true);
//    mobileFld.setReadOnly(true);
//    currencyFld.setReadOnly(true);
//    }

}
