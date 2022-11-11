package sr.we.ui.components.finance;

import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.theme.lumo.LumoUtility;
import sr.we.ContextProvider;
import sr.we.data.controller.AccountService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Account;
import sr.we.shekelflowcore.entity.helper.vo.AccountVO;
import sr.we.shekelflowcore.enums.ChartOfAccountTypes;
import sr.we.shekelflowcore.enums.ChartOfAccounts;
import sr.we.shekelflowcore.enums.Reference;
import sr.we.shekelflowcore.enums.TransactionType;
import sr.we.ui.views.account.CabAccountViewNew;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class AccountSelect extends Select<Account> {


    private List<Account> accounts1;
    private AccountVO accountVO;

    public AccountSelect() {
    }

    public AccountSelect(Long businessId, TransactionType transactionType) {
        this(businessId,Reference.ALL_ASSETS);
    }
    public AccountSelect(Long businessId, Reference reference) {
        load(businessId, reference);
//        setLabel(getTranslation("sr.we.payment.method"));
//        setHelperText(getTranslation("sr.we.type.of.business.info"));
    }

    public void load(Long businessId, Reference reference) {
        AccountService pojoService = ContextProvider.getBean(AccountService.class);
        String token = AuthenticatedUser.token();

        setItemLabelGenerator((f) -> f.getAccountType().getType().getCaption() +" > "+f.getName());

        accountVO = new AccountVO();

        accountVO.setBusiness(businessId);
        accountVO.setAccountCodes(reference.getAccountCodes());
        if(accountVO.getAccountCodes().size() == 31){
            accountVO.setCurrency(0L);
        }
        accounts1 = pojoService.list(token, accountVO).getResult();
        setItems(accounts1);
        addOpenedChangeListener(f -> {
            if(f.isOpened()){


                List<Account> accounts = pojoService.list(token, accountVO).getResult();
                List<Account> sorted = accounts.stream().sorted(Comparator.comparing(l -> l.getAccountType().getType().ordinal())).toList();

//                getDataProvider().fetch(new Query<>()).collect(Collectors.toList()).addAll(accounts1);
//                getDataProvider().refreshAll();
//                if(accounts != null && !accounts.isEmpty()){
//                    Map<ChartOfAccounts, Account> collect = accounts.stream().collect(Collectors.toMap(g -> g.getAccountType().getType(), l -> l));
//                    collect.entrySet().stream().forEachOrdered(d -> {
//                        add(d.getKey().getCaption());
//                        add(new Hr());
//                    });
//                }

                Map<ChartOfAccountTypes, List<Account>> collect = sorted.stream().collect(Collectors.groupingBy(l -> l.getAccountType().getCode()));
                accounts1 = new ArrayList<>();
                collect.entrySet().stream().sorted(Comparator.comparingInt(d -> d.getKey().ordinal())).forEachOrdered(d -> {
                    if(d.getValue() != null && !d.getValue().isEmpty()) {
                        accounts1.addAll(d.getValue());
                    }
                });
                setItems(accounts1);
                AtomicInteger l = new AtomicInteger();
                int i2 = l.decrementAndGet();
                collect.entrySet().stream().sorted(Comparator.comparingInt(d -> d.getKey().ordinal())).forEachOrdered(d -> {
                    List<Account> value = d.getValue();
                    if(value != null && !value.isEmpty()) {
                        Account afterItem = value.get(0);
                        int i = accounts1.indexOf(afterItem);
                        Span label = new Span(afterItem.getAccountType().getCode().getCaption());
                        label.setClassName(LumoUtility.FontWeight.BOLD);
                        VerticalLayout component = new VerticalLayout(label, new Hr());
                        component.setMargin(false);
                        component.setPadding(false);
                        component.setSpacing(false);
                        int i1 = l.addAndGet(1);
                        addComponentAtIndex(i+ i1, component);

                    }
                });
                Anchor routerLink = new Anchor(CabAccountViewNew.getLocation(businessId.toString()), "+ Add account", AnchorTarget.BLANK);
                add(routerLink);
            } else {
                getDataProvider().fetch(new Query<>()).collect(Collectors.toList()).clear();
            }
        });
    }

    public void setCurrency(Long currency) {
        accountVO.setCurrency(currency);
    }

    public void setValue(Long account) {
        if(account != null && accounts1 != null && !accounts1.isEmpty()){
            Optional<Account> any = accounts1.stream().filter(f -> f.getId().compareTo(account) == 0).findAny();
            if(any.isPresent()){
                Account account1 = any.get();
                setValue(account1);
            }
        } else {
            setValue((Account) null);
        }
    }
}
