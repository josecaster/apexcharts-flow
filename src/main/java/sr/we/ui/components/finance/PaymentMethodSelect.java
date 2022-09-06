package sr.we.ui.components.finance;

import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.select.Select;
import sr.we.ContextProvider;
import sr.we.data.controller.PojoService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.BusinessType;
import sr.we.shekelflowcore.entity.PaymentMethod;

import java.util.List;

public class PaymentMethodSelect extends RadioButtonGroup<PaymentMethod> {


    public PaymentMethodSelect() {
        PojoService pojoService = ContextProvider.getBean(PojoService.class);
        String token = AuthenticatedUser.token();

        List<PaymentMethod> paymentMethods = pojoService.listPaymentMethod(token);
        setItems(paymentMethods);

        setItemLabelGenerator((f) -> f.getDescription());

        setValue(paymentMethods.get(0));

//        setLabel(getTranslation("sr.we.payment.method"));
//        setHelperText(getTranslation("sr.we.type.of.business.info"));
    }

}
