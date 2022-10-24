package sr.we.ui.views.finance.transactions;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.AnchorTarget;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.lumo.LumoUtility;
import net.sf.jasperreports.engine.JRException;
import sr.we.ContextProvider;
import sr.we.data.controller.InvoiceService;
import sr.we.data.controller.PosHeaderService;
import sr.we.data.controller.UserAccessService;
import sr.we.data.report.MyReportEngine;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Currency;
import sr.we.shekelflowcore.entity.Invoice;
import sr.we.shekelflowcore.entity.PaymentTransaction;
import sr.we.shekelflowcore.entity.PosHeader;
import sr.we.shekelflowcore.entity.helper.Executable;
import sr.we.shekelflowcore.enums.Reference;
import sr.we.shekelflowcore.exception.ValidationException;
import sr.we.shekelflowcore.security.Privileges;
import sr.we.shekelflowcore.security.privileges.TransactionsPrivilege;
import sr.we.ui.components.MyDialog;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TransactionDialog extends MyDialog {


    private final TransactionForm transactionForm;
    private Button downloadReceipt;
    private Anchor anchor;
    private Button saveButton = null;
    private PaymentTransaction save;

    public TransactionDialog(BigDecimal rest, LocalDate initDate, Long businessId, Currency fromCurrency, Currency selectedCurrency, Reference reference, Long referenceId) {

        setHeaderTitle("Record a payment");
        Button closeButton = new Button(new Icon("lumo", "cross"), (e) -> {
            close();
        });
        setCloseOnOutsideClick(false);
        setCloseOnEsc(false);
        setModal(true);
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        getHeader().add(closeButton);

        Button cancelButton = new Button("Cancel", (e) -> close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelButton.getStyle().set("margin-right", "auto");
        getFooter().add(cancelButton);

        transactionForm = new TransactionForm(rest, initDate, businessId, fromCurrency, selectedCurrency, reference, referenceId);
        add(transactionForm);

        saveButton = new Button("Save");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        getFooter().add(saveButton);
        UserAccessService userAccesService = ContextProvider.getBean(UserAccessService.class);
        boolean hasAccess = userAccesService.hasAccess(AuthenticatedUser.token(), new TransactionsPrivilege(), Privileges.INSERT);
        saveButton.setVisible(hasAccess);




        saveButton.addClickListener(g -> {
            save = transactionForm.save();

            if(save != null && onSave != null){
                onSave.build();
            }
            String token = AuthenticatedUser.token();
            if(reference.compareTo(Reference.POS) == 0||reference.compareTo(Reference.INVOICE) == 0||reference.compareTo(Reference.BILL) == 0){
                remove(transactionForm);
                if(anchor != null){
                    remove(anchor);
                }
                if(downloadReceipt != null){
                    getFooter().remove(downloadReceipt);
                }
                setHeaderTitle(null);
                getHeader().remove(closeButton);
                saveButton.setVisible(false);
                downloadReceipt = new Button("Download receipt");
//                getFooter().add(downloadReceipt);
                anchor = new Anchor("", downloadReceipt);
                getFooter().add(anchor);
                H1 h1 = new H1("Successfully created Transaction");
                h1.addClassName(LumoUtility.TextColor.PRIMARY);
                VerticalLayout layout = new VerticalLayout(h1, new Image("icons/icons8_ok_48px.png","SeaQns Ok image"));

                layout.setAlignItems(FlexComponent.Alignment.CENTER);
                layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
                downloadReceipt.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
                anchor.setTarget(AnchorTarget.BLANK);
                anchor.setHref(new StreamResource("Receipt" + UUID.randomUUID() + ".pdf", new InputStreamFactory() {

                    @Override
                    public InputStream createInputStream() {

                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("TRANSACTION_ID", save.getId());
                        map.put("TRANSACTION_RECEIVED", save.getReceived());
                        map.put("TRANSACTION_CHANGE", save.getChange());
                        map.put("CUR_FROM", save.getCurrencyFrom().getCode());
                        map.put("CUR_TO", save.getCurrencyTo().getCode());
                        if(save.getReference().compareTo(Reference.INVOICE) == 0){
                            InvoiceService invoiceService = ContextProvider.getBean(InvoiceService.class);

                            Invoice invoice = invoiceService.get(save.getReferenceId(), token);
                            if(invoice.isFullyPayed()){
                                map.put("TRANSACTION_STATUS", "Fully paid");
                            } else {
                                map.put("TRANSACTION_STATUS", "Partially paid");
                            }
                            map.put("POS_PAID", invoice.getTransactionsAmount());
                            map.put("POS_DUE", invoice.getRest());
                            map.put("POS_ID", invoice.getPosHeader().getId());
                        } else if(save.getReference().compareTo(Reference.POS) == 0){
                            PosHeaderService posHeaderService = ContextProvider.getBean(PosHeaderService.class);
                            PosHeader posHeader = posHeaderService.get(save.getReferenceId(), token);
                            if(posHeader.getRest().compareTo(BigDecimal.ZERO) == 0){
                                map.put("TRANSACTION_STATUS", "Fully paid");
                            } else {
                                map.put("TRANSACTION_STATUS", "Partially paid");
                            }

                            map.put("POS_PAID", posHeader.getTransactionsAmount());
                            map.put("POS_DUE", posHeader.getRest());
                            map.put("POS_ID", posHeader.getId());
                        } else {
                            throw new ValidationException("No report for this transaction");
                        }





                        try {
                            byte[] exportReportMap = ((MyReportEngine) ContextProvider.getBean(MyReportEngine.class)).exportReceipt(map);
                            return new ByteArrayInputStream(exportReportMap);
                        } catch (JRException e) {
                            throw new RuntimeException(e);
                        }
                    }

                }));
                add(layout);
            } else {
                close();
            }
        });

    }

    public void disableExchange() {
        transactionForm.disableExchange();
    }

    public void disableAmount() {
        transactionForm.disableAmount();
    }

    public void setNextReferenceId(Long id) {
        transactionForm.setNextReferenceId(id);
    }

    private Executable refresh, onSave;

    public void setRefresh(Executable refresh) {
        this.refresh = refresh;
        transactionForm.setRefresh(refresh);
    }

    public void setOnSave(Executable onSave) {
        this.onSave = onSave;
    }
}
