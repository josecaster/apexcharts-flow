package sr.we.ui.components.general;

import com.vaadin.flow.component.checkbox.CheckboxGroup;
import sr.we.ContextProvider;
import sr.we.data.controller.PojoService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.PaymentMethod;

import java.util.List;

public class PaymentMethodGroup extends CheckboxGroup<PaymentMethod> {

    public PaymentMethodGroup() {
        load();
        setItemLabelGenerator(PaymentMethod::getDescription);
        setLabel(getTranslation());
    }

    public void load() {
        PojoService pojoService = ContextProvider.getBean(PojoService.class);
        String token = AuthenticatedUser.token();
        List<PaymentMethod> paymentMethods = pojoService.listPaymentMethod(token).getResult();
        setItems(paymentMethods);
    }

    public String getTranslation() {
        return getTranslation("sr.we.payment.methods");
    }

}
