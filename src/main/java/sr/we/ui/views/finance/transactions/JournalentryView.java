package sr.we.ui.views.finance.transactions;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.apache.commons.lang3.StringUtils;
import sr.we.shekelflowcore.entity.helper.vo.JournalsEntryVO;
import sr.we.shekelflowcore.entity.helper.vo.PaymentTransactionVO;
import sr.we.shekelflowcore.enums.DebCred;
import sr.we.shekelflowcore.enums.Reference;
import sr.we.ui.components.finance.AccountSelect;
import sr.we.ui.views.LineAwesomeIcon;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * A Designer generated component for the journalentry-view template.
 *
 * Designer will add and remove fields with @Id mappings but
 * does not overwrite or otherwise change this file.
 */
@Tag("journalentry-view")
@JsModule("./src/views/finance/transactions/journalentry-view.ts")
public class JournalentryView extends LitTemplate {

    private final Grid<JournalsEntryVO> grid;
    @Id("transaction-date-picker")
    private DatePicker transactionDatePicker;
    @Id("transaction-description")
    private TextField transactionDescription;
    @Id("add-journal-entry-btn")
    private Button addJournalEntryBtn;
    @Id("journal-entry-summary-span")
    private Span journalEntrySummarySpan;
    @Id("delete-btn")
    private Button deleteBtn;
    @Id("copy-btn")
    private Button copyBtn;
    @Id("last-updated-paragraph")
    private Paragraph lastUpdatedParagraph;
    @Id("journal-table-layout")
    private Div journalTableLayout;

    private Long businessId;
    private List<JournalsEntryVO> journalsEntryVOS;

    /**
     * Creates a new JournalentryView.
     */
    public JournalentryView() {
        // You can initialise any data required for the connected UI components here.
        grid = new Grid<>();
        journalTableLayout.add(grid);
        grid.addComponentColumn(f -> {
            TextField description = new TextField();
            description.setPlaceholder("Write a description");
            description.setValue(StringUtils.isBlank(f.getMemo()) ? "" : f.getMemo());
            description.setWidthFull();
            return description;
        }).setHeader("Description");
        grid.addComponentColumn(f -> {
            AccountSelect account = new AccountSelect(businessId, Reference.JOURNAL_ENTRY);
            account.setPlaceholder("Select an account");
            account.setValue(f.getAccount());
            account.setWidthFull();
            return account;
        }).setHeader("Account");

        grid.addComponentColumn(f -> {
            BigDecimalField debit = new BigDecimalField();
            debit.setValue(f.getDebCred().compareTo(DebCred.DEB) == 0 ? f.getAmount() : null);
            debit.setWidthFull();
            debit.addValueChangeListener(g -> {
                if(g.isFromClient()){
                    f.setDebCred(DebCred.DEB);
                    f.setAmount(g.getValue() == null ? BigDecimal.ZERO : g.getValue());
                    grid.getDataProvider().refreshItem(f);
                }
            });
            return debit;
        }).setHeader("Debit");
        grid.addComponentColumn(f -> {
            Button arrow = new Button();
            arrow.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            String debit = "la la-arrow-left";
            String credit = "la la-arrow-right";
            LineAwesomeIcon lineAwesomeIcon = new LineAwesomeIcon(debit);
            lineAwesomeIcon.addClassName(LumoUtility.IconSize.MEDIUM);
            if(f.getDebCred().compareTo(DebCred.CRED) == 0){
                lineAwesomeIcon.icon(debit);
            } else {
                lineAwesomeIcon.icon(credit);
            }
            lineAwesomeIcon.addClickListener(g -> {
                if(f.getDebCred().compareTo(DebCred.CRED) == 0){
                    lineAwesomeIcon.icon(debit);
                    f.setDebCred(DebCred.DEB);
                } else {
                    lineAwesomeIcon.icon(credit);
                    f.setDebCred(DebCred.CRED);
                }
                grid.getDataProvider().refreshItem(f);
            });
            arrow.setIcon(lineAwesomeIcon);
            arrow.setWidthFull();
            return arrow;
        });
        grid.addComponentColumn(f -> {
            BigDecimalField credit = new BigDecimalField();
            credit.setValue(f.getDebCred().compareTo(DebCred.CRED) == 0 ? f.getAmount() : null);
            credit.addValueChangeListener(g -> {
                if(g.isFromClient()){
                    f.setDebCred(DebCred.CRED);
                    f.setAmount(g.getValue() == null ? BigDecimal.ZERO : g.getValue());
                    grid.getDataProvider().refreshItem(f);
                }
            });
            credit.setWidthFull();
            return credit;
        }).setHeader("Credit");

        addJournalEntryBtn.addClickListener(f -> {
            JournalsEntryVO vo = new JournalsEntryVO();
            vo.setNew(true);
            vo.setDebCred(DebCred.DEB);
            vo.setAmount(BigDecimal.ZERO);
            if(journalsEntryVOS == null){
                journalsEntryVOS = new ArrayList<>();
            }
            journalsEntryVOS.add(vo);
            grid.setItems(journalsEntryVOS);
            grid.getDataProvider().refreshAll();

        });
    }

    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }

    public PaymentTransactionVO getVO(){
        PaymentTransactionVO vo = new PaymentTransactionVO();
        vo.setNew(true);
        vo.setPaymentDate(transactionDatePicker.getValue());
        vo.setMemo(transactionDescription.getValue());
        vo.setJournals(grid.getDataProvider().fetch(new Query<>()).toList());
        return vo;
    }
}
