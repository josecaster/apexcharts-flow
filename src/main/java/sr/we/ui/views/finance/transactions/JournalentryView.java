package sr.we.ui.views.finance.transactions;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.grid.FooterRow;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.apache.commons.lang3.StringUtils;
import sr.we.ContextProvider;
import sr.we.CustomNotificationHandler;
import sr.we.data.controller.BusinessService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Account;
import sr.we.shekelflowcore.entity.Business;
import sr.we.shekelflowcore.entity.Currency;
import sr.we.shekelflowcore.entity.helper.TransactionCategory;
import sr.we.shekelflowcore.entity.helper.vo.JournalsEntryVO;
import sr.we.shekelflowcore.entity.helper.vo.PaymentTransactionVO;
import sr.we.shekelflowcore.enums.DebCred;
import sr.we.shekelflowcore.enums.TransactionType;
import sr.we.shekelflowcore.enums.Reference;
import sr.we.shekelflowcore.exception.PrimaryThrowable;
import sr.we.shekelflowcore.exception.ValidationException;
import sr.we.shekelflowcore.settings.util.Constants;
import sr.we.ui.components.UIUtil;
import sr.we.ui.components.customer.CustomerButton;
import sr.we.ui.components.finance.AccountSelect;
import sr.we.ui.components.general.CurrencySelect;
import sr.we.ui.views.LineAwesomeIcon;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * A Designer generated component for the journalentry-view template.
 * <p>
 * Designer will add and remove fields with @Id mappings but
 * does not overwrite or otherwise change this file.
 */
@Tag("journalentry-view")
@JsModule("./src/views/finance/transactions/journalentry-view.ts")
public class JournalentryView extends LitTemplate {

    protected Grid<JournalsEntryVO> grid;
    @Id("transaction-date-picker")
    protected DatePicker transactionDatePicker;
    @Id("transaction-description")
    protected TextField transactionDescription;
    @Id("add-journal-entry-btn")
    protected Button addJournalEntryBtn;
    @Id("delete-btn")
    protected Button deleteBtn;
    @Id("copy-btn")
    protected Button copyBtn;
    @Id("last-updated-paragraph")
    protected Paragraph lastUpdatedParagraph;
    @Id("journal-table-layout")
    protected Div journalTableLayout;

    protected Long businessId;
    protected List<JournalsEntryVO> journalsEntryVOS;
    protected H3 sumDebitH3;
    protected H3 sumCreditH3;
    @Id("action-layout")
    protected HorizontalLayout actionLayout;
    protected H3 difference;
    @Id("currency-cmb")
    protected CurrencySelect currencyCmb;
    protected Business business;
    protected Currency currency;
    @Id("customer-btn")
    private CustomerButton customerBtn;
    private BigDecimal debit;
    private BigDecimal credit;
    private BigDecimal diff;

    /**
     * Creates a new JournalentryView.
     */
    public JournalentryView() {
        // You can initialise any data required for the connected UI components here.
        initButtons();


        initGrid();


        debit();
        credit();


        grid.getDataProvider().refreshAll();
        refresh();
        transactionDatePicker.setValue(LocalDate.now());

        actionLayout.setVisible(false);
    }

    protected void initGrid() {
        grid = new Grid<>();
        grid.setAllRowsVisible(true);
        journalTableLayout.add(grid);
        grid.addComponentColumn(f -> {
            TextField description = new TextField();
            description.setPlaceholder("Write a description");
            description.setValue(StringUtils.isBlank(f.getMemo()) ? "" : f.getMemo());
            description.setWidthFull();
            description.addValueChangeListener(g -> {
                f.setMemo(g.getValue());
                grid.getDataProvider().refreshItem(f);
            });
            return description;
        }).setHeader("Description");
        Grid.Column<JournalsEntryVO> accountColumn = grid.addComponentColumn(f -> {
            AccountSelect account = new AccountSelect(businessId, Reference.JOURNAL_ENTRY);
            account.setValue(f.getAccount());
            account.setWidthFull();
            account.setPlaceholder("Select an account");
            account.addValueChangeListener(g -> {
                if (g.isFromClient()) {
                    Account value = g.getValue();
                    f.setAccount(value == null ? null : value.getId());
                    f.setAccountSelect(value);
                    grid.getDataProvider().refreshItem(f);
//                    grid.getDataProvider().refreshAll();
                    refresh();
                }
            });
            return account;
        }).setHeader("Account");

        Grid.Column<JournalsEntryVO> debitColumn = grid.addComponentColumn(f -> {
            BigDecimalField debit = new BigDecimalField();
            if (currency != null) {
                debit.setPrefixComponent(new Span(currency.getCode()));
            }
            if (f.getAmount() != null && f.getDebCred().compareTo(DebCred.DEB) == 0) {
                if (isNegative(f)) {
                    debit.setSuffixComponent(new Span("-"));
                } else {
                    debit.setSuffixComponent(new Span("+"));
                }
            }
            debit.setPlaceholder("Debit");
            debit.setValue(f.getDebCred().compareTo(DebCred.DEB) == 0 ? f.getAmount() : null);
            debit.setWidthFull();
            debit.addValueChangeListener(g -> {
                if (g.isFromClient()) {
                    f.setDebCred(DebCred.DEB);
                    f.setAmount(g.getValue() == null ? BigDecimal.ZERO : g.getValue());
                    grid.getDataProvider().refreshItem(f);
//                    grid.getDataProvider().refreshAll();
                    refresh();
                }
            });
            return debit;
        });
        debitColumn.setHeader("Debit");
        Grid.Column<JournalsEntryVO> arrowColumn = grid.addComponentColumn(f -> {
            Button arrow = new Button();
            arrow.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            String debit = "la la-arrow-left";
            String credit = "la la-arrow-right";
            LineAwesomeIcon lineAwesomeIcon = new LineAwesomeIcon(debit);
            lineAwesomeIcon.addClassName(LumoUtility.FontSize.MEDIUM);
            if (f.getDebCred().compareTo(DebCred.CRED) == 0) {
                lineAwesomeIcon.icon(debit);
            } else {
                lineAwesomeIcon.icon(credit);
            }
            lineAwesomeIcon.addClickListener(g -> {
                if (f.getDebCred().compareTo(DebCred.CRED) == 0) {
                    lineAwesomeIcon.icon(debit);
                    f.setDebCred(DebCred.DEB);
                } else {
                    lineAwesomeIcon.icon(credit);
                    f.setDebCred(DebCred.CRED);
                }
                grid.getDataProvider().refreshItem(f);
//                grid.getDataProvider().refreshAll();
                refresh();
            });
            arrow.setIcon(lineAwesomeIcon);
            arrow.setWidthFull();
            return arrow;
        });
        Grid.Column<JournalsEntryVO> creditColumn = grid.addComponentColumn(f -> {
            BigDecimalField credit = new BigDecimalField();
            if (currency != null) {
                credit.setPrefixComponent(new Span(currency.getCode()));
            }
            if (f.getAmount() != null && f.getDebCred().compareTo(DebCred.CRED) == 0) {
                if (isNegative(f)) {
                    credit.setSuffixComponent(new Span("-"));
                } else {
                    credit.setSuffixComponent(new Span("+"));
                }
            }
            credit.setPlaceholder("Credit");
            credit.setValue(f.getDebCred().compareTo(DebCred.CRED) == 0 ? f.getAmount() : null);
            credit.addValueChangeListener(g -> {
                if (g.isFromClient()) {
                    f.setDebCred(DebCred.CRED);
                    f.setAmount(g.getValue() == null ? BigDecimal.ZERO : g.getValue());
                    grid.getDataProvider().refreshItem(f);
//                    grid.getDataProvider().refreshAll();
                    refresh();
                }
            });
            credit.setWidthFull();
            return credit;
        });
        creditColumn.setHeader("Credit");


        FooterRow footerRow = grid.appendFooterRow();

        sumDebitH3 = new H3();
        sumCreditH3 = new H3();

        sumDebitH3.setClassName(LumoUtility.Margin.NONE);
        sumCreditH3.setClassName(LumoUtility.Margin.NONE);

        sumDebitH3.getElement().setAttribute("theme", UIUtil.Badge.PILL+" primary");
        sumCreditH3.getElement().setAttribute("theme", UIUtil.Badge.PILL+" primary");

        sumDebitH3.setText("Total " + Constants.CURRENCY_FORMAT.format(BigDecimal.ZERO));
        sumCreditH3.setText("Total " + Constants.CURRENCY_FORMAT.format(BigDecimal.ZERO));

        footerRow.getCell(debitColumn).setComponent(sumDebitH3);
        footerRow.getCell(creditColumn).setComponent(sumCreditH3);

        HorizontalLayout debit = new HorizontalLayout(new Span("Summary"));
        debit.setWidthFull();
        debit.setAlignItems(FlexComponent.Alignment.END);
        debit.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        footerRow.getCell(accountColumn).setComponent(debit);

        difference = new H3("Difference " + Constants.CURRENCY_FORMAT.format(BigDecimal.ZERO));
        difference.setClassName(LumoUtility.Margin.NONE);
        difference.getElement().setAttribute("theme", UIUtil.Badge.PILL+" primary");
        HorizontalLayout credit = new HorizontalLayout(difference);
        credit.setWidthFull();
        credit.setAlignItems(FlexComponent.Alignment.CENTER);
        credit.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        footerRow.getCell(arrowColumn).setComponent(credit);

        grid.getDataProvider().addDataProviderListener(g -> refresh());

        currencyCmb.addValueChangeListener(f -> {
            if (f.getValue() == null && business != null && business.getCurrency() != null) {
                currencyCmb.setValue(business.getCurrency());
            }
            currency = f.getValue();
            grid.getDataProvider().refreshAll();
        });

    }

    protected void refresh() {
        if (journalsEntryVOS != null && !journalsEntryVOS.isEmpty()) {
            debit = journalsEntryVOS.stream().filter(f -> f.getAmount() != null && f.getDebCred().compareTo(DebCred.DEB) == 0).map(getJournalsEntryVOBigDecimalFunction()).reduce(BigDecimal.ZERO, BigDecimal::add);
            credit = journalsEntryVOS.stream().filter(f -> f.getAmount() != null && f.getDebCred().compareTo(DebCred.CRED) == 0).map(getJournalsEntryVOBigDecimalFunction1()).reduce(BigDecimal.ZERO, BigDecimal::add);
            sumDebitH3.setText("Total " + Constants.CURRENCY_FORMAT.format(debit));
            sumCreditH3.setText("Total " + Constants.CURRENCY_FORMAT.format(credit));
            diff = debit.subtract(credit);
            difference.setText("Difference " + Constants.CURRENCY_FORMAT.format(diff));
            if (debit.compareTo(credit) != 0) {
                sumDebitH3.getElement().setAttribute("theme", UIUtil.Badge.PILL+" error");
                sumCreditH3.getElement().setAttribute("theme", UIUtil.Badge.PILL+" error");
            } else if ((debit).compareTo(credit) == 0 && debit.compareTo(BigDecimal.ZERO) != 0) {
                sumDebitH3.getElement().setAttribute("theme", UIUtil.Badge.PILL+" success");
                sumCreditH3.getElement().setAttribute("theme", UIUtil.Badge.PILL+" success");
            } else {
                sumDebitH3.getElement().setAttribute("theme", UIUtil.Badge.PILL+" primary");
                sumCreditH3.getElement().setAttribute("theme", UIUtil.Badge.PILL+" primary");
            }
        }
    }

    protected Function<JournalsEntryVO, BigDecimal> getJournalsEntryVOBigDecimalFunction() {
        return f -> isNegative(f) ? f.getAmount().multiply(BigDecimal.valueOf(1)) : f.getAmount();
    }

    protected Function<JournalsEntryVO, BigDecimal> getJournalsEntryVOBigDecimalFunction1() {
        return f -> !isNegative(f) ? f.getAmount().multiply(BigDecimal.valueOf(1)) : f.getAmount();
    }

    protected boolean isNegative(JournalsEntryVO f) {
        return f.getAccountSelect() != null && Objects.requireNonNull(f.getAccountSelect().getAccountType().getType().getPlusMin(f.getDebCred())).compareTo(TransactionType.WITHDRAWAL) == 0;
    }

    protected void initButtons() {
        LineAwesomeIcon icon = new LineAwesomeIcon("la la-trash-alt");
        LineAwesomeIcon icon1 = new LineAwesomeIcon("la la-copy");

        icon.addClassNames(LumoUtility.Margin.NONE, LumoUtility.FontSize.MEDIUM);
        icon1.addClassNames(LumoUtility.Margin.NONE, LumoUtility.FontSize.MEDIUM);

        icon.getElement().getStyle().set("margin", "0px");
        icon1.getElement().getStyle().set("margin", "0px");

        deleteBtn.setIcon(icon);
        copyBtn.setIcon(icon1);

        deleteBtn.setText(null);
        copyBtn.setText(null);

        deleteBtn.addThemeVariants(ButtonVariant.LUMO_ICON);
        copyBtn.addThemeVariants(ButtonVariant.LUMO_ICON);

        addJournalEntryBtn.addThemeVariants(ButtonVariant.LUMO_SMALL);

        addJournalEntryBtn.addClickListener(f -> {
            debit();
            grid.getDataProvider().refreshAll();

        });
    }

    protected void credit() {
        JournalsEntryVO vo = new JournalsEntryVO();
        vo.setNew(true);
        vo.setDebCred(DebCred.CRED);
        vo.setAmount(BigDecimal.ZERO);
        addJournal(vo);
    }

    protected void debit() {
        JournalsEntryVO vo = new JournalsEntryVO();
        vo.setNew(true);
        vo.setDebCred(DebCred.DEB);
        vo.setAmount(BigDecimal.ZERO);
        addJournal(vo);
    }

    protected void addJournal(JournalsEntryVO vo) {
        if (journalsEntryVOS == null) {
            journalsEntryVOS = new ArrayList<>();
        }
        journalsEntryVOS.add(vo);
        grid.setItems(journalsEntryVOS);
    }

    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
        customerBtn.setBusinessId(businessId);
        BusinessService businessService = ContextProvider.getBean(BusinessService.class);
        business = businessService.get(businessId, AuthenticatedUser.token());
        currencyCmb.setValue(business.getCurrency());
    }

    public PaymentTransactionVO getVO() {
        if(diff.compareTo(BigDecimal.ZERO) != 0){
            throw new ValidationException("This entry is not balanced. Difference needs to be 0.00");
        }
        PaymentTransactionVO paymentTransactionVO = new PaymentTransactionVO();
        paymentTransactionVO.setNew(true);
        paymentTransactionVO.setBusiness(businessId);
        paymentTransactionVO.setPaymentDate(transactionDatePicker.getValue());
        paymentTransactionVO.setMemo(transactionDescription.getValue());
        paymentTransactionVO.setJournals(grid.getDataProvider().fetch(new Query<>()).toList());
        paymentTransactionVO.setCustomerId(customerBtn.getCustomerId());
        paymentTransactionVO.setAmount(debit);
        paymentTransactionVO.setExchangeRate(BigDecimal.ONE);
        paymentTransactionVO.setConvertedAmount(debit);
        paymentTransactionVO.setReceived(credit);
        paymentTransactionVO.setChange(BigDecimal.ZERO);
        paymentTransactionVO.setReference(Reference.JOURNAL_ENTRY);
        paymentTransactionVO.setCurrencyFrom(currencyCmb.getValue().getId());
        paymentTransactionVO.setCurrencyTo(currencyCmb.getValue().getId());
        paymentTransactionVO.setTransactionType(TransactionType.UNKNOWN);

        return paymentTransactionVO;
    }
}
