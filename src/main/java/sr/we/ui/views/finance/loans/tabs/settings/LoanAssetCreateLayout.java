package sr.we.ui.views.finance.loans.tabs.settings;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.RouteParam;
import com.vaadin.flow.router.RouteParameters;
import sr.we.ContextProvider;
import sr.we.data.controller.LoanAssetsService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Loan;
import sr.we.shekelflowcore.entity.LoanAssets;
import sr.we.shekelflowcore.entity.helper.vo.LoanAssetsVO;
import sr.we.ui.views.StateListenerLayout;
import sr.we.ui.views.finance.loans.tabs.LTabSettings;

public class LoanAssetCreateLayout extends StateListenerLayout {


    private final TextField nameFld;
    private final TextArea descriptionFld;
    private final Checkbox liquidChk;
    private final AssetTypeSelect assetTypeSelect;
    private final Checkbox depreciationChk;
    private final FormLayout layout;
    private String business;
    private Loan loan;


    public LoanAssetCreateLayout() {
        layout = new FormLayout();


        add(layout);
        layout.setMaxWidth("500px");

        nameFld = new TextField();
        descriptionFld = new TextArea();
        liquidChk = new Checkbox();
        assetTypeSelect = new AssetTypeSelect();
        depreciationChk = new Checkbox();

        nameFld.setWidthFull();
        descriptionFld.setWidthFull();
        liquidChk.setWidthFull();
        assetTypeSelect.setWidthFull();
        depreciationChk.setWidthFull();

        liquidChk.setLabel(null);
        depreciationChk.setLabel(null);
        assetTypeSelect.setLabel(null);
        assetTypeSelect.setHelperText(null);

        // info
        layout.addFormItem(nameFld, getTranslation("sr.we.name"));
        layout.addFormItem(descriptionFld, getTranslation("sr.we.description"));
        layout.addFormItem(assetTypeSelect, getTranslation("sr.we.asset.type"));
        layout.addFormItem(liquidChk, getTranslation("sr.we.liquid"));
        layout.addFormItem(depreciationChk, getTranslation("sr.we.depreciate"));


        nameFld.setRequired(true);
        nameFld.setRequiredIndicatorVisible(true);

        descriptionFld.setRequired(true);
        descriptionFld.setRequiredIndicatorVisible(true);

        liquidChk.setRequiredIndicatorVisible(true);

        assetTypeSelect.setRequiredIndicatorVisible(true);

        depreciationChk.setRequiredIndicatorVisible(true);


        state(nameFld, descriptionFld, assetTypeSelect, liquidChk, depreciationChk);

        layout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
    }

    @Override
    protected void onSave() {

        LoanAssetsVO loanAssetsBody = new LoanAssetsVO();
        loanAssetsBody.setLoan(loan == null ? null : loan.getId());
        loanAssetsBody.setName(nameFld.getValue());
        loanAssetsBody.setDescription(descriptionFld.getValue());
        loanAssetsBody.setLiquid(liquidChk.getValue());
        loanAssetsBody.setAssetType(assetTypeSelect.getValue() == null ? null : assetTypeSelect.getValue().getId());
        loanAssetsBody.setDepreciation(depreciationChk.getValue());
        LoanAssetsService bean = ContextProvider.getBean(LoanAssetsService.class);
        bean.create(AuthenticatedUser.token(), loanAssetsBody);
        redirectToParent();
    }

    private void redirectToParent() {
//        QueryParameters queryParameters = QueryParameters.fromString("id=" + loan.getId());
//        UI.getCurrent().navigate(LoansViewTabAssetss.getLocation(business), queryParameters);
        UI.getCurrent().navigate(LTabSettings.class, //
                new RouteParameters(//
                        new RouteParam("business", business),//
                        new RouteParam("loan", loan.getId().toString())));
    }

    @Override
    protected void onDiscard() {
        nameFld.clear();
        descriptionFld.clear();
        liquidChk.clear();
        assetTypeSelect.clear();
        depreciationChk.clear();
    }

    @Override
    protected boolean validate() {
        if (nameFld.isEmpty()) {
            return false;
        }
        if (descriptionFld.isEmpty()) {
            return false;
        }

        return !assetTypeSelect.isEmpty();
    }

    public void setBusiness(String business) {
        this.business = business;
    }

    public void setLoan(Loan loan) {
        this.loan = loan;
    }

    public void setLoanAssets(LoanAssets loanAssets) {
        nameFld.setValue(loanAssets.getName());
        descriptionFld.setValue(loanAssets.getDescription());
        liquidChk.setValue(loanAssets.getLiquid());
        assetTypeSelect.setValue(loanAssets.getAssetType());
        depreciationChk.setValue(loanAssets.getDepreciation());
        setReadOnly(nameFld, descriptionFld, liquidChk, assetTypeSelect, depreciationChk);
        actionLayout.setVisible(false);
    }

    private void setReadOnly(TextField nameFld, TextArea descriptionFld, Checkbox liquidChk, AssetTypeSelect assetTypeSelect, Checkbox depreciationChk) {
        nameFld.setReadOnly(true);
        assetTypeSelect.setReadOnly(true);
        descriptionFld.setReadOnly(true);
        liquidChk.setReadOnly(true);
        depreciationChk.setReadOnly(true);
    }


}
