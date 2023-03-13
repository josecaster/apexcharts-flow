package sr.we.ui.components.finance;

import com.vaadin.flow.component.select.Select;
import sr.we.ContextProvider;
import sr.we.data.controller.PojoService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.AccountPaymentMethod;
import sr.we.shekelflowcore.entity.PaymentMethod;

import java.util.List;
import java.util.Set;

public class PaymentMethodSelect extends Select<PaymentMethod> {


    public PaymentMethodSelect() {
        normal();

        setItemLabelGenerator((f) -> f.getDescription());


//        setLabel(getTranslation("sr.we.payment.method"));
//        setHelperText(getTranslation("sr.we.type.of.business.info"));
    }

    public void normal() {
        PojoService pojoService = ContextProvider.getBean(PojoService.class);
        String token = AuthenticatedUser.token();
//        addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
        List<PaymentMethod> paymentMethods = pojoService.listPaymentMethod(token).getResult();
        setItems(paymentMethods);
        setValue(paymentMethods.get(0));
    }

    public void barrier(Set<AccountPaymentMethod> accountPaymentMethods) {
        List<PaymentMethod> paymentMethods = accountPaymentMethods.stream().map(AccountPaymentMethod::getPaymentMethod).toList();
        setItems(paymentMethods);
        setValue(paymentMethods.get(0));
    }
}
