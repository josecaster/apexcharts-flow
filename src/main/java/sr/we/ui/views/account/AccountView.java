package sr.we.ui.views.account;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.theme.lumo.LumoUtility;
import sr.we.ContextProvider;
import sr.we.CustomNotificationHandler;
import sr.we.data.controller.AccountService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Account;
import sr.we.shekelflowcore.entity.helper.vo.AccountVO;
import sr.we.shekelflowcore.enums.ChartOfAccountTypes;
import sr.we.shekelflowcore.enums.SystemAccounts;
import sr.we.shekelflowcore.exception.PrimaryThrowable;
import sr.we.ui.components.MyDialog;
import sr.we.ui.views.LineAwesomeIcon;

import java.util.List;
import java.util.concurrent.Executors;

/**
 * A Designer generated component for the account-view template.
 * <p>
 * Designer will add and remove fields with @Id mappings but
 * does not overwrite or otherwise change this file.
 */
@Tag("account-view")
@JsModule("./src/views/accounts/account-view.ts")
public class AccountView extends LitTemplate {

    private final ContextMenu infoMenu;
    private final AccountNewLayout accountCreateEditView;
    private final MyDialog dialog;
    @Id("account-type")
    private H3 accountType;
    @Id("account-type-desc")
    private Icon accountTypeDesc;
    @Id("records-layout")
    private VerticalLayout recordsLayout;
    @Id("add-account-btn")
    private Button addAccountBtn;
    private Grid<Account> grid;
    private AccountVO vo;
    private ChartOfAccountTypes accountCodes;
    private String token;

    /**
     * Creates a new AccountView.
     */
    public AccountView() {
        // You can initialise any data required for the connected UI components here.
        infoMenu = new ContextMenu(accountTypeDesc);
        infoMenu.setOpenOnClick(true);
//        recordsLayout.removeAll();

        accountCreateEditView = new AccountNewLayout();
        dialog = new MyDialog(new Hr(), accountCreateEditView);
        dialog.setHeaderTitle("Add account");
        Button closeButton = new Button(new Icon("lumo", "cross"), (e) -> {
            dialog.close();
            refresh();
        });
        accountCreateEditView.setBuild(() -> {
            dialog.close();
            refresh();
            return null;
        });
        dialog.setCloseOnOutsideClick(false);
        dialog.setCloseOnEsc(false);
        dialog.setModal(true);
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        dialog.getHeader().add(closeButton);

        addAccountBtn.addClickListener(f -> {

            accountCreateEditView.setAccountTypeCode(accountCodes.name());
            dialog.open();
        });
    }

    public void build(ChartOfAccountTypes accountCodes, Long businessId) {
        accountCreateEditView.setBusiness(businessId.toString());
        this.accountCodes = accountCodes;
        accountType.setText(accountCodes.getCaption());
        infoMenu.add("Description comes here");
        vo = new AccountVO();
        vo.setBusiness(businessId);
        vo.setAccountCodes(List.of(accountCodes));
        vo.setCurrency(0L);

        grid = new Grid<>();
        grid.addColumn(Account::getAccountId).setResizable(true).setSortable(true);
        grid.addColumn(Account::getName).setResizable(true).setSortable(true);
        grid.addColumn(Account::getDescription).setResizable(true).setSortable(true);
        grid.addComponentColumn(f -> {
            LineAwesomeIcon lineAwesomeIcon = new LineAwesomeIcon("la la-pencil");
            if (!SystemAccounts.isSystemAccount(f.getSystemId())) {
                lineAwesomeIcon.addClassName(LumoUtility.TextColor.PRIMARY);
                lineAwesomeIcon.addClickListener(g -> {
                    accountCreateEditView.setValue(f);
                    dialog.open();
                });
            } else {
                lineAwesomeIcon.icon("la la-lock");
            }
            return lineAwesomeIcon;
        }).setResizable(true);
        grid.addComponentColumn(f -> {
            LineAwesomeIcon lineAwesomeIcon = new LineAwesomeIcon("la la-times");
            if (!SystemAccounts.isSystemAccount(f.getSystemId())) {
                lineAwesomeIcon.addClassName(LumoUtility.TextColor.ERROR);
                lineAwesomeIcon.addClickListener(g -> {
                    ConfirmDialog confirmDialog = new ConfirmDialog("Delete", "Do you wish to delete this account [" + f.getName() + "]?", "Yes", l -> {
                        AccountService accountService = ContextProvider.getBean(AccountService.class);
                        AccountVO vo1 = new AccountVO();
                        vo1.setId(f.getId());
                        Long count = accountService.delete(token, vo1);
                        CustomNotificationHandler.notify_(new PrimaryThrowable(count + " item deleted"));
                        refresh();
                    });
                    confirmDialog.setConfirmButtonTheme(LumoUtility.Background.ERROR);
                    confirmDialog.setCancelable(true);

                    confirmDialog.open();

                });
            } else {
                lineAwesomeIcon.icon("la la-lock");
            }
            return lineAwesomeIcon;
        }).setResizable(true);
        grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT, GridVariant.LUMO_NO_BORDER);
        grid.setAllRowsVisible(true);
        refresh();
    }

    private void refresh() {
        UI current = UI.getCurrent();

        token = AuthenticatedUser.token();
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                AccountService accountService = ContextProvider.getBean(AccountService.class);
                List<Account> list = accountService.list(token, vo).getResult();
                current.access(() -> {
                    if (list != null && !list.isEmpty()) {
                        grid.setItems(list);
                        grid.getDataProvider().refreshAll();
                        recordsLayout.removeAll();
                        recordsLayout.add(grid);
                    } else {
                        recordsLayout.removeAll();
                        recordsLayout.add(new Paragraph("No records found"));
                    }
                });

            }
        });
    }

}
