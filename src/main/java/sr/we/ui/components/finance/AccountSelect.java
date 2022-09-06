package sr.we.ui.components.finance;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.AnchorTarget;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.provider.Query;
import sr.we.ContextProvider;
import sr.we.data.controller.AccountService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Account;
import sr.we.ui.views.account.CabAccountViewNew;

import java.util.List;
import java.util.stream.Collectors;

public class AccountSelect extends Select<Account> {


    public AccountSelect(Long businessId) {
        AccountService pojoService = ContextProvider.getBean(AccountService.class);
        String token = AuthenticatedUser.token();

        setItemLabelGenerator((f) -> f.getName());



        addOpenedChangeListener(f -> {
            if(f.isOpened()){
                List<Account> accounts = pojoService.list(token, businessId);
//                getDataProvider().fetch(new Query<>()).collect(Collectors.toList()).addAll(accounts1);
//                getDataProvider().refreshAll();
                setItems(accounts);
                Anchor routerLink = new Anchor(CabAccountViewNew.getLocation(businessId.toString()), "+ Add account", AnchorTarget.BLANK);
                add(routerLink);
            } else {
                getDataProvider().fetch(new Query<>()).collect(Collectors.toList()).clear();
            }
        });
//        setLabel(getTranslation("sr.we.payment.method"));
//        setHelperText(getTranslation("sr.we.type.of.business.info"));
    }

}
