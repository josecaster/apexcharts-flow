package sr.we.ui.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import org.apache.commons.lang3.StringUtils;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Business;
import sr.we.shekelflowcore.entity.ThisUser;
import sr.we.shekelflowcore.entity.helper.InterExecutable;
import sr.we.shekelflowcore.entity.helper.vo.ApplicationMailQueueVO;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MailDialog extends MyDialog {


    private final Select<String> replyToCmb;
    private final TextField toFld;
    private final TextField subjectFld;
    private final TextArea messageFld;
    private final Checkbox sendCopyChk;
    private String username = "N.A";

    public MailDialog(String header, InterExecutable<Object, ApplicationMailQueueVO> executable, Business business) {

        setMaxWidth("500px");

        setHeaderTitle(header);
        this.setCloseOnOutsideClick(false);
        this.setCloseOnEsc(false);
        this.setModal(true);

        replyToCmb = new Select<>();
        toFld = new TextField();

        subjectFld = new TextField();
        messageFld = new TextArea();

        sendCopyChk = new Checkbox();

        replyToCmb.setWidthFull();
        toFld.setWidthFull();
        subjectFld.setWidthFull();
        messageFld.setWidthFull();

        replyToCmb.addValueChangeListener(f -> {
            if (f.getValue() == null) {
                if (StringUtils.isNotBlank(username)) {
                    replyToCmb.setValue(username);
                }
                return;
            }
            sendCopyChk.setLabel("Send a copy to myself at " + f.getValue());
        });

        Optional<ThisUser> thisUserOptional = AuthenticatedUser.get();
        boolean present = thisUserOptional.isPresent();
        sendCopyChk.setVisible(present);
        if (present) {
            ThisUser thisUser = thisUserOptional.get();
            List<String> items = new ArrayList<>();
            username = thisUser.getUsername();
            String base = username;

            if (business != null && StringUtils.isNotBlank(business.getEmailAddress()) && !items.contains(business.getEmailAddress())) {
                items.add(business.getEmailAddress());
                base = business.getEmailAddress();
            }
            items.addAll((thisUser.getPerson() == null || //
                    thisUser.getPerson().getDefaultForms() == null || //
                    StringUtils.isBlank(thisUser.getPerson().getDefaultForms().getEmailAddress())) //
                    ? List.of(username) : List.of(username, thisUser.getPerson().getDefaultForms().getEmailAddress()));

            replyToCmb.setItems(items);
            replyToCmb.setValue(base);
        }


        FormLayout formLayout = new FormLayout();
        formLayout.addFormItem(replyToCmb, "From");
        formLayout.addFormItem(toFld, "To");
        formLayout.addFormItem(subjectFld, "Subject");
        formLayout.addFormItem(messageFld, "Message");
        formLayout.addFormItem(sendCopyChk, "");
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0px", 1));
        add(formLayout);

        Button closeButton = new Button(new Icon("lumo", "cross"), (e) -> {
            this.close();
        });
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        this.getHeader().add(closeButton);

        Button cancelButton = new Button("Cancel", (e) -> this.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelButton.getStyle().set("margin-right", "auto");
        this.getFooter().add(cancelButton);

        Button continueBtn = new Button("Send", (e) -> {
            executable.build(getVO());
            this.close();
        });
        continueBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        this.getFooter().add(continueBtn);


    }

    public ApplicationMailQueueVO getVO() {
        ApplicationMailQueueVO applicationMailQueueVO = new ApplicationMailQueueVO();
        applicationMailQueueVO.setMailReplyTo(replyToCmb.getValue());
        applicationMailQueueVO.setMailTo(toFld.getValue());
        applicationMailQueueVO.setMailSubj(subjectFld.getValue());
        applicationMailQueueVO.setMailBody(messageFld.getValue());
        applicationMailQueueVO.setMailToSelf(sendCopyChk.getValue() ? replyToCmb.getValue() : null);
        return applicationMailQueueVO;
    }

    public void setValues(String to, String subj, String message) {
        toFld.setValue(to);
        subjectFld.setValue(subj);
        messageFld.setValue(message);
    }


}
