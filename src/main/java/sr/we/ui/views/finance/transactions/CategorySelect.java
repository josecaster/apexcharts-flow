package sr.we.ui.views.finance.transactions;

import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.theme.lumo.LumoUtility;
import sr.we.ContextProvider;
import sr.we.data.controller.AccountService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Account;
import sr.we.shekelflowcore.entity.helper.TransactionCategory;
import sr.we.shekelflowcore.entity.helper.vo.AccountVO;
import sr.we.shekelflowcore.enums.ChartOfAccounts;
import sr.we.shekelflowcore.enums.Reference;
import sr.we.shekelflowcore.enums.TransactionType;

import java.util.ArrayList;
import java.util.List;

public class CategorySelect extends Select<TransactionCategory> {

    /**
     * This constructor indicates weather it is a deposit or a withdrawal which will affect the behavior of the Select
     **/
    public CategorySelect(TransactionType transactionType, Long businessId) {

        setItemLabelGenerator(TransactionCategory::getCaption);

        if (transactionType.compareTo(TransactionType.DEPOSIT) == 0) {
            deposit(businessId);
        } else {
            withdrawal(businessId);
        }
    }

    private void deposit(Long businessId) {

        List<TransactionCategory> accounts = new ArrayList<>();

        AccountVO accountVO = new AccountVO();
        accountVO.setBusiness(businessId);
//        accountVO.setCurrency(0L);

        List<TransactionCategory> incomeAccounts = accounts(Reference.INC, accountVO);
        List<TransactionCategory> assetAccounts = accounts(Reference.ASSETS, accountVO);
        List<TransactionCategory> liabilityAccounts = accounts(Reference.LCC, accountVO);
        List<TransactionCategory> equityAccounts = accounts(Reference.EQ, accountVO);

        int incomeAccountsSize = incomeAccounts.size();
        int assetAccountsSize = assetAccounts.size();
        int liabilityAccountsSize = liabilityAccounts.size();
        int equityAccountsSize = equityAccounts.size();

        accounts.addAll(incomeAccounts);
        accounts.addAll(assetAccounts);
        accounts.addAll(liabilityAccounts);
        accounts.addAll(equityAccounts);

        setItems(accounts);

        // Income
        addComponentAsFirst(header(ChartOfAccounts.INC));
        int last = 1;

        // Assets
        if (assetAccountsSize > 0) {
            last += incomeAccountsSize;
            addComponentAtIndex(last, header(ChartOfAccounts.ASSETS));
            last++;
        }

        if (liabilityAccountsSize > 0) {
            last += assetAccountsSize;
            addComponentAtIndex(last, header(ChartOfAccounts.LCC));
            last++;
        }

        if (equityAccountsSize > 0) {
            last += liabilityAccountsSize;
            addComponentAtIndex(last, header(ChartOfAccounts.EQ));
            last++;
        }
    }

    private void withdrawal(Long businessId) {

        List<TransactionCategory> accounts = new ArrayList<>();

        AccountVO accountVO = new AccountVO();
        accountVO.setBusiness(businessId);
        accountVO.setCurrency(0L);

        List<TransactionCategory> expenseAccounts = accounts(Reference.EXP, accountVO);
        List<TransactionCategory> assetAccounts = accounts(Reference.ASSETS, accountVO);
        List<TransactionCategory> liabilityAccounts = accounts(Reference.LCC, accountVO);
        List<TransactionCategory> equityAccounts = accounts(Reference.EQ, accountVO);

        int expenseAccountsSize = expenseAccounts.size();
        int assetAccountsSize = assetAccounts.size();
        int liabilityAccountsSize = liabilityAccounts.size();
        int equityAccountsSize = equityAccounts.size();

        accounts.addAll(expenseAccounts);
        accounts.addAll(assetAccounts);
        accounts.addAll(liabilityAccounts);
        accounts.addAll(equityAccounts);

        setItems(accounts);

        // Expense
        addComponentAsFirst(header(ChartOfAccounts.EXP));
        int last = 1;

        // Assets
        if (assetAccountsSize > 0) {
            last += expenseAccountsSize;
            addComponentAtIndex(last, header(ChartOfAccounts.ASSETS));
            last++;
        }

        if (liabilityAccountsSize > 0) {
            last += assetAccountsSize;
            addComponentAtIndex(last, header(ChartOfAccounts.LCC));
            last++;
        }

        if (equityAccountsSize > 0) {
            last += liabilityAccountsSize;
            addComponentAtIndex(last, header(ChartOfAccounts.EQ));
            last++;
        }
    }

    private VerticalLayout header(ChartOfAccounts inc) {
        Span span = new Span(inc.getCaption());
        span.addClassNames(LumoUtility.Margin.NONE, LumoUtility.FontWeight.BOLD);
        VerticalLayout component = new VerticalLayout(span, new Hr());
        component.setPadding(false);
        component.setSpacing(false);
        return component;
    }

    private List<TransactionCategory> accounts(Reference reference, AccountVO accountVO) {
        AccountService pojoService = ContextProvider.getBean(AccountService.class);
        String token = AuthenticatedUser.token();
        accountVO.setAccountCodes(reference.getAccountCodes());
        List<Account> incomeAccounts = pojoService.list(token, accountVO).getResult();
        return incomeAccounts.stream().map(f -> new TransactionCategory(reference, f.getId(), f.getName())).toList();
    }
}
