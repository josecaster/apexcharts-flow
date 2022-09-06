package sr.we.ui.views.finance.loans.tabs.request.assets;

import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.IronIcon;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.dom.Element;
import org.apache.commons.lang3.StringUtils;
import sr.we.ContextProvider;
import sr.we.data.controller.LoanRequestAssetsService;
import sr.we.data.controller.UserAccessService;
import sr.we.demo.about.AboutView;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.LoanRequestAssets;
import sr.we.shekelflowcore.entity.LoanRequestAssetsFiles;
import sr.we.shekelflowcore.entity.helper.vo.LoanRequestAssetsVO;
import sr.we.shekelflowcore.security.Privileges;
import sr.we.shekelflowcore.security.privileges.LoanAssetsPrivilege;
import sr.we.shekelflowcore.security.privileges.LoanRequestAssetsPrivilege;
import sr.we.ui.components.Legend;
import sr.we.ui.components.ToolbarLayout;
import sr.we.ui.views.LineAwesomeIcon;

import javax.naming.Context;
import java.math.BigDecimal;

/**
 * A Designer generated component for the input-layout template.
 *
 * Designer will add and remove fields with @Id mappings but
 * does not overwrite or otherwise change this file.
 */
@Tag("input-layout")
@JsModule("./src/views/finance/loan/proces/input-layout.ts")
public class InputLayout<T> extends LitTemplate {

    @Id("fileGrid")
    private Grid<LoanRequestAssetsFiles> fileGrid;
    @Id("assetValidChk")
    private Checkbox assetValidChk;
    @Id("vaadinVerticalLayout")
    private VerticalLayout vaadinVerticalLayout;
    @Id("toolbarLayout")
    private ToolbarLayout toolbarLayout;
    @Id("saveBtn")
    private Button saveBtn;
    @Id("valueAmountFld")
    private BigDecimalField valueAmountFld;

    private LoanRequestAssets loanRequestAssets;
    @Id("memoLbl")
    private Label memoLbl;
    @Id("memoFld")
    private TextArea memoFld;

    /**
     * Creates a new InputLayout.
     */
    public InputLayout() {
        // You can initialise any data required for the connected UI components here.
//        fileGrid = new Grid<>();
        Column<LoanRequestAssetsFiles> name = fileGrid.addComponentColumn(f -> {
            TextField textField = new TextField();
            textField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
            textField.setValue(f.getName());
            textField.setWidthFull();
            return textField;
        }).setHeader("Name");
        name.getElement().getStyle().set("padding", "0px");
        fileGrid.addThemeVariants(GridVariant.LUMO_NO_ROW_BORDERS,GridVariant.LUMO_NO_ROW_BORDERS,GridVariant.LUMO_COMPACT);
        fileGrid.setSelectionMode(Grid.SelectionMode.MULTI);
        Column<LoanRequestAssetsFiles> loanRequestAssetsFilesColumn = fileGrid.addComponentColumn(f -> {
            return new Checkbox(f.getValid());
        });
        loanRequestAssetsFilesColumn.setWidth("50px");
        loanRequestAssetsFilesColumn.setHeader("Valid");
        saveBtn.getElement().removeAllChildren();
        saveBtn.setText("Save");
        saveBtn.setIcon(new LineAwesomeIcon("la la-save"));
        UserAccessService userAccesService = ContextProvider.getBean(UserAccessService.class);
        boolean hasAccess = userAccesService.hasAccess(AuthenticatedUser.token(), new LoanRequestAssetsPrivilege(), Privileges.UPDATE);
        saveBtn.setVisible(hasAccess);
        saveBtn.addClickListener(f -> {
            LoanRequestAssetsService loanRequestAssetsService = ContextProvider.getBean(LoanRequestAssetsService.class);
            LoanRequestAssetsVO vo = new LoanRequestAssetsVO();
            vo.setId(loanRequestAssets.getId());
            vo.setNew(false);
            vo.setAmount(valueAmountFld.getValue());
            vo.setValid(assetValidChk.getValue());
            vo.setMemo(memoFld.getValue());
            loanRequestAssetsService.edit(AuthenticatedUser.token(), vo);
            UI.getCurrent().getPage().reload();
        });
    }

    public Grid getFileGrid() {
        return fileGrid;
    }

    public void setLoanRequestAssets(LoanRequestAssets loanRequestAssets){
        this.loanRequestAssets = loanRequestAssets;
        valueAmountFld.setValue(loanRequestAssets.getAmount());
        memoFld.setValue(StringUtils.isBlank(loanRequestAssets.getMemo()) ? "" : loanRequestAssets.getMemo());
        assetValidChk.setValue(loanRequestAssets.getValid());
    }
}
