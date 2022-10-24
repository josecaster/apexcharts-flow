package sr.we.ui.views.finance.transactions;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.AnchorTarget;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;
import net.sf.jasperreports.engine.JRException;
import sr.we.ContextProvider;
import sr.we.data.controller.InvoiceService;
import sr.we.data.controller.PosHeaderService;
import sr.we.data.report.MyReportEngine;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Invoice;
import sr.we.shekelflowcore.entity.PaymentTransaction;
import sr.we.shekelflowcore.entity.PosHeader;
import sr.we.shekelflowcore.enums.Reference;
import sr.we.shekelflowcore.exception.ValidationException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class TransactionsCmb extends HorizontalLayout {

    private final ComboBox<Object> paymentTransactionComboBox;
    private final Anchor anchor;

    public TransactionsCmb() {

        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        anchor = new Anchor();
        paymentTransactionComboBox = new ComboBox<>();
        add(paymentTransactionComboBox, anchor);

        paymentTransactionComboBox.setPlaceholder("Transactions");
        paymentTransactionComboBox.setVisible(false);
        paymentTransactionComboBox.setClearButtonVisible(true);
        paymentTransactionComboBox.setItemLabelGenerator(f -> {
            return "Transaction # " + ((PaymentTransaction) f).getId();
        });
        paymentTransactionComboBox.addValueChangeListener(f -> {
            anchor.setVisible(f.getValue() != null);
        });

        anchor.setText("Download receipt");
        anchor.setVisible(false);
        anchor.setTarget(AnchorTarget.BLANK);
        String token = AuthenticatedUser.token();
        anchor.setHref(new StreamResource("Receipt" + UUID.randomUUID() + ".pdf", new InputStreamFactory() {

            @Override
            public InputStream createInputStream() {
                PaymentTransaction save = (PaymentTransaction) paymentTransactionComboBox.getValue();
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("TRANSACTION_ID", save.getId());
                map.put("TRANSACTION_RECEIVED", save.getReceived());
                map.put("TRANSACTION_CHANGE", save.getChange());
                map.put("CUR_FROM", save.getCurrencyFrom().getCode());
                map.put("CUR_TO", save.getCurrencyTo().getCode());

                if (save.getReference().compareTo(Reference.INVOICE) == 0) {
                    InvoiceService invoiceService = ContextProvider.getBean(InvoiceService.class);

                    Invoice invoice = invoiceService.get(save.getReferenceId(), token);
                    if (invoice.isFullyPayed()) {
                        map.put("TRANSACTION_STATUS", "Fully paid");
                    } else {
                        map.put("TRANSACTION_STATUS", "Partially paid");
                    }
                    map.put("POS_PAID", invoice.getTransactionsAmount());
                    map.put("POS_DUE", invoice.getRest());
                    map.put("POS_ID", invoice.getPosHeader().getId());
                } else if (save.getReference().compareTo(Reference.POS) == 0) {
                    PosHeaderService posHeaderService = ContextProvider.getBean(PosHeaderService.class);
                    PosHeader posHeader = posHeaderService.get(save.getReferenceId(), token);
                    if (posHeader.getRest().compareTo(BigDecimal.ZERO) == 0) {
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
    }

    public void loadCmb(Set<? extends PaymentTransaction> collection) {
        paymentTransactionComboBox.setItems(collection.toArray(new PaymentTransaction[collection.size()]));
        paymentTransactionComboBox.setVisible(!collection.isEmpty());
    }
}
