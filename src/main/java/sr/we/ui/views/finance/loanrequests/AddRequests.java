package sr.we.ui.views.finance.loanrequests;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.RouteParam;
import com.vaadin.flow.router.RouteParameters;
import org.vaadin.addons.yuri0x7c1.bslayout.BsColumn;
import org.vaadin.addons.yuri0x7c1.bslayout.BsLayout;
import org.vaadin.addons.yuri0x7c1.bslayout.BsRow;
import sr.we.ContextProvider;
import sr.we.data.controller.LoanRequestService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.LoanRequest;
import sr.we.shekelflowcore.entity.LoanRequestPlan;
import sr.we.shekelflowcore.entity.helper.Executable;
import sr.we.shekelflowcore.entity.helper.adapter.LoanRequestBody;
import sr.we.shekelflowcore.entity.helper.vo.LoanRequestAssetsVO;
import sr.we.shekelflowcore.entity.helper.vo.LoanRequestVO;
import sr.we.shekelflowcore.settings.util.Constants;
import sr.we.ui.components.Highlight;
import sr.we.ui.components.UIUtil;
import sr.we.ui.views.LineAwesomeIcon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * A Designer generated component for the add-requests template.
 * <p>
 * Designer will add and remove fields with @Id mappings but
 * does not overwrite or otherwise change this file.
 */
@Tag("add-requests")
@JsModule("./src/views/finance/loanrequests/add-requests.ts")
public class AddRequests extends LitTemplate {

    private final Executable<?> refresh;
    @Id("form-layout")
    protected FormLayout formLayout;
    @Id("assets-form")
    protected AssetsForm assetsForm;
    @Id("provision-form")
    protected ProvisionForm provisionForm;
    @Id("repayment-form")
    protected RepaymentForm repaymentForm;
    @Id("requestor-form")
    protected RequestorForm requestorForm;
    @Id("board-layout")
    protected Div boardLayout;
    @Id("back-button")
    protected Button backButton;
    @Id("save-btn")
    protected Button saveBtn;
    @Id("contract-layout")
    protected Div contractLayout;
    @Id("generate-contract-btn")
    protected Button generateContractBtn;
    @Id("new-request-lbl")
    protected H3 newRequestLbl;
    @Id("new-request-paragraph")
    protected Paragraph newRequestParagraph;
    @Id("loan-request-cancel-btn")
    protected Button loanRequestCancelBtn;
    @Id("loan-request-archive-btn")
    protected Button loanRequestArchiveBtn;
    private String business;
    private LoanRequest loanRequest;
    @Id("loan-request-status-span")
    private Span loanRequestStatusSpan;
    @Id("loan-request-approve-btn")
    private Button loanRequestApproveBtn;
    @Id("loan-request-done-btn")
    private Button loanRequestDoneBtn;

    /**
     * Creates a new AddRequests.
     */
    public AddRequests() {
        // You can initialise any data required for the connected UI components here.


        backButton.addClickListener(f -> UI.getCurrent().navigate(RequestsView.class, new RouteParameters(new RouteParam("business", business))));
        backButton.setIcon(new LineAwesomeIcon("la la-arrow-left"));
        backButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_ICON);


        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0px", 1), new FormLayout.ResponsiveStep("500px", 3));

        saveBtn.addClickListener(f -> onSave());


        refresh = (Executable<Object>) () -> {
            board();
            return null;
        };
        loanRequestApproveBtn.setVisible(false);
        loanRequestCancelBtn.setVisible(false);
        loanRequestArchiveBtn.setVisible(false);
        loanRequestStatusSpan.setVisible(false);
        loanRequestDoneBtn.setVisible(false);
        loanRequestDoneBtn.addClickListener(f -> {
            LoanRequestService loanRequestService = ContextProvider.getBean(LoanRequestService.class);
            loanRequest = loanRequestService.procesNextStep(AuthenticatedUser.token(), loanRequest.getId(), LoanRequest.Status.DONE);
            setLoanRequest(loanRequest);
        });
        loanRequestApproveBtn.addClickListener(f -> {
            ConfirmDialog confirmDialog = new ConfirmDialog();
            confirmDialog.setHeader("Approve Loan request?");
            confirmDialog.add("Are you sure that you want to approve this loan request");
            confirmDialog.setCancelable(true);
            BigDecimalField bigDecimalField = new BigDecimalField();
            bigDecimalField.setPlaceholder("Exchange rate");
            bigDecimalField.setHelperText("Please provide a base exchange rate");
            bigDecimalField.setWidthFull();
            bigDecimalField.setRequiredIndicatorVisible(true);
            boolean exchangeRate = loanRequest.getCurrency().getId().compareTo(loanRequest.getBusiness().getCurrency().getId()) != 0;
            if (exchangeRate) {
                confirmDialog.add(bigDecimalField);
            }
            confirmDialog.addConfirmListener(g -> {
                LoanRequestService loanRequestService = ContextProvider.getBean(LoanRequestService.class);
                loanRequest = loanRequestService.procesNextStep(AuthenticatedUser.token(), loanRequest.getId(),bigDecimalField.getValue(), LoanRequest.Status.APPROVED);
                setLoanRequest(loanRequest);
            });
            confirmDialog.open();
        });

        loanRequestCancelBtn.addClickListener(f -> {
            ConfirmDialog confirmDialog = new ConfirmDialog();
            confirmDialog.setHeader("Cancel Loan request?");
            confirmDialog.setText("Are you sure that you want to cancel this loan request");
            confirmDialog.setCancelable(true);
            confirmDialog.addConfirmListener(g -> {
                LoanRequestService loanRequestService = ContextProvider.getBean(LoanRequestService.class);
                loanRequest = loanRequestService.procesNextStep(AuthenticatedUser.token(), loanRequest.getId(), LoanRequest.Status.CANCEL);
                setLoanRequest(loanRequest);
            });
            confirmDialog.open();
        });

        loanRequestArchiveBtn.addClickListener(f -> {
            ConfirmDialog confirmDialog = new ConfirmDialog();
            confirmDialog.setHeader("Archive Loan request?");
            confirmDialog.setText("Are you sure that you want to archive this loan request");
            confirmDialog.setCancelable(true);
            confirmDialog.addConfirmListener(g -> {
                LoanRequestService loanRequestService = ContextProvider.getBean(LoanRequestService.class);
                loanRequest = loanRequestService.procesNextStep(AuthenticatedUser.token(), loanRequest.getId(), LoanRequest.Status.ARCHIVE);
                setLoanRequest(loanRequest);
            });
            confirmDialog.open();
        });
        contractLayout.setVisible(false);
        generateContractBtn.setVisible(false);
        generateContractBtn.addClickListener(f -> {
            /*try {
                createNewContent();
            } catch (IOException | Docx4JException | JAXBException | ParserConfigurationException |
                     TransformerException e) {
                throw new RuntimeException(e);
            }*/
        });
        loanRequestStatusSpan.setWidth("85px");
    }

    /*public void createNewContent() throws IOException, FrameworkException, Docx4JException, JAXBException, ParserConfigurationException, TransformerException {
            OfficeDocDocxParser officeDocDocxParser = new OfficeDocDocxParser(UI.getCurrent().getLocale());
            Map<String, String> placeholders = new HashMap<>();
        File file1 = ResourceUtils.getFile(
                "classpath:contracts/contract-new-hire-6-maanden.docx");
        String letterDocname = "contract-new-hire-6-maanden.docx";
            String outputFilename = Constants.REPORT_TEMP_HOME + Constants.FILE_SEPERATOR + Calendar.getInstance().getTime().getTime() + "_" + letterDocname;
            if (file1 != null) {
                officeDocDocxParser.getFileFromTemplate(file1, outputFilename, placeholders);
                final File file = new File(outputFilename);

                Anchor anchor = new Anchor(new StreamResource(letterDocname, new InputStreamFactory() {

                    @Override
                    public InputStream createInputStream() {
//                        UploadTemplateGenerator generator = new UploadTemplateGenerator(value, locale);
//                        byte[] array = generator.createExcel();
                        try {
                            return new FileInputStream(file);//new ByteArrayInputStream("new byte[]{}".getBytes());
                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        }

                    }
                }), "Download Contract");
                contractLayout.add(anchor);
            }
    }*/

    public void setBusiness(String business) {
        this.business = business;
        requestorForm.setBusiness(business);
    }

    protected void setLoanRequest(LoanRequest loanRequest) {
        this.loanRequest = loanRequest;
        requestorForm.setLoanRequest(loanRequest, refresh);
        assetsForm.setLoanRequest(loanRequest, refresh);
        provisionForm.setLoanRequest(loanRequest, refresh);
        repaymentForm.setLoanRequest(loanRequest, refresh);
        refresh.build();
        contractLayout.setVisible(true);
        newRequestLbl.setVisible(false);
        newRequestParagraph.setVisible(false);
        loanRequestApproveBtn.setVisible(false);
        loanRequestCancelBtn.setVisible(false);
        loanRequestArchiveBtn.setVisible(false);
        loanRequestDoneBtn.setVisible(false);
        if (loanRequest.getStatus().compareTo(LoanRequest.Status.REQUESTED) == 0) {
            loanRequestApproveBtn.setVisible(true);
            loanRequestArchiveBtn.setVisible(true);
        }
        if (loanRequest.getStatus().compareTo(LoanRequest.Status.APPROVED) == 0 || loanRequest.getStatus().compareTo(LoanRequest.Status.CANCEL) == 0) {
            loanRequestArchiveBtn.setVisible(true);
        }
        if (loanRequest.getStatus().compareTo(LoanRequest.Status.REQUESTED) == 0 || loanRequest.getStatus().compareTo(LoanRequest.Status.APPROVED) == 0) {
            boolean b = loanRequest.getPaymentTransactions() != null && !loanRequest.getPaymentTransactions().isEmpty();
            boolean present = loanRequest.getLoanRequestPlans().stream().map(f -> f.getLoanRequestPlanDetails().stream().anyMatch(g -> f.getLoanRequestPlanDetails().stream().anyMatch(l -> l.getPaymentTransactions() != null && !l.getPaymentTransactions().isEmpty()))).findAny().isPresent();
            if (b || present) {
                loanRequestCancelBtn.setVisible(true);
            }


        }
        if (loanRequest.getStatus().compareTo(LoanRequest.Status.APPROVED) == 0) {
            boolean allDone = loanRequest.getNextPaymentReferenceId() == null;
            if (allDone) {
                loanRequestDoneBtn.setVisible(true);
            }
        }
        if (loanRequest.getStatus().compareTo(LoanRequest.Status.DONE) == 0) {
            loanRequestApproveBtn.setVisible(false);
            loanRequestCancelBtn.setVisible(false);
            loanRequestArchiveBtn.setVisible(false);
            loanRequestDoneBtn.setVisible(false);
        }
        loanRequestStatusSpan.setVisible(true);
        loanRequestStatusSpan.setText(loanRequest.getStatus().name());
        ThemeList themeList = loanRequestStatusSpan.getElement().getThemeList();
        themeList.add(UIUtil.Badge.PILL);
        if (loanRequest.getStatus().compareTo(LoanRequest.Status.REQUESTED) == 0) {
            themeList.add("primary");
        } else if (loanRequest.getStatus().compareTo(LoanRequest.Status.APPROVED) == 0) {
            themeList.add("success");
        }
        if (loanRequest.getStatus().compareTo(LoanRequest.Status.CANCEL) == 0) {
            themeList.add("error");
        }
        if (loanRequest.getStatus().compareTo(LoanRequest.Status.ARCHIVE) == 0) {
            themeList.add("tertiary");
        }
        if (loanRequest.getStatus().compareTo(LoanRequest.Status.DONE) == 0) {
            themeList.add("primary success");
        }
        saveBtn.setVisible(false);
    }

    private void board() {
        UI current = UI.getCurrent();
        String token = AuthenticatedUser.token();
        Executors.newSingleThreadExecutor().execute(() -> {

            LoanRequestService loanRequestService = ContextProvider.getBean(LoanRequestService.class);
            loanRequest = loanRequestService.get(loanRequest.getId(), token);
            current.access(() -> {
                boardLayout.removeAll();
                BsLayout board = new BsLayout();
                boardLayout.add(board);

                long reduce = 0L;
                List<LoanRequestPlan> loanRequestPlans = new ArrayList<>(AddRequests.this.loanRequest.getLoanRequestPlans());
                if (!loanRequestPlans.isEmpty()) {
                    reduce = loanRequestPlans.stream().map(LoanRequestPlan::getFreqAmount).reduce(0L, Long::sum);
                }

                Highlight principal = new Highlight("Principal", () -> Constants.CURRENCY_FORMAT.format(loanRequest.getAmount()), () -> null, Executors.newSingleThreadExecutor());
                long finalReduce = reduce;
                Highlight initial_frequency = new Highlight("Initial Frequency", () -> loanRequest.getFreq().getCaption() + " " + loanRequest.getFreqVal().doubleValue(), () -> (double) finalReduce, Executors.newSingleThreadExecutor());
                Highlight intrest = new Highlight("Intrest", () -> loanRequest.getIntrest() == null ? Constants.CURRENCY_FORMAT.format(0) : Constants.CURRENCY_FORMAT.format(loanRequest.getIntrest()), () -> null, Executors.newSingleThreadExecutor());
                Highlight balance = new Highlight("Balance", () -> loanRequest.getBalance() == null ? Constants.CURRENCY_FORMAT.format(0) : Constants.CURRENCY_FORMAT.format(loanRequest.getBalance()), () -> loanRequest.getTransactionBalance() == null ? null : loanRequest.getTransactionBalance().doubleValue(), Executors.newSingleThreadExecutor());
                board.withRows(new BsRow().withColumns(new BsColumn(principal).withSize(BsColumn.Size.XS), new BsColumn(initial_frequency).withSize(BsColumn.Size.XS), new BsColumn(intrest).withSize(BsColumn.Size.XS), new BsColumn(balance).withSize(BsColumn.Size.XS)));
            });
        });

    }

    protected void onSave() {

        LoanRequestBody loanRequestBody = requestorForm.getLoanRequestBody();

        LoanRequestService bean = ContextProvider.getBean(LoanRequestService.class);
        LoanRequest loanRequest;
        if (this.loanRequest == null) {
            loanRequest = bean.create(AuthenticatedUser.token(), loanRequestBody);
        } else {
            LoanRequestVO vo = new LoanRequestVO();
            List<LoanRequestAssetsVO> list = assetsForm.getList();
            vo.setAssets(list);
            vo.setId(this.loanRequest.getId());
            vo.setStatus(this.loanRequest.getStatus());
            vo.setBalance(this.loanRequest.getBalance());
            vo.setCurrency(this.loanRequest.getCurrency().getId());
            vo.setCustomer(this.loanRequest.getCustomer().getId());
            vo.setEligible(this.loanRequest.getEligible());
            vo.setFactor(this.loanRequest.getLoan().getFactor());
            vo.setFactorType(this.loanRequest.getLoan().getFactorType());
            vo.setFreq(this.loanRequest.getFreq());
            vo.setFreqVal(this.loanRequest.getFreqVal());
            vo.setLoan(this.loanRequest.getLoan().getId());
            vo.setName(this.loanRequest.getLoan().getName());
            vo.setNew(false);
            loanRequest = bean.edit(AuthenticatedUser.token(), vo);
        }
        List<String> strings = List.of(loanRequest.getId().toString());
        Map<String, List<String>> map = new HashMap<>();
        map.put("id", strings);
        QueryParameters queryParameters = new QueryParameters(map);
        UI.getCurrent().navigate(EditRequestsView.getLocation(business), queryParameters);
    }


}
