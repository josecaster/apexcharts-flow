package sr.we.ui.views.finance.loanrequests;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.board.Board;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.RouteParam;
import com.vaadin.flow.router.RouteParameters;
import sr.we.ContextProvider;
import sr.we.data.controller.LoanRequestService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.LoanRequest;
import sr.we.shekelflowcore.entity.LoanRequestPlan;
import sr.we.shekelflowcore.entity.helper.Build;
import sr.we.shekelflowcore.entity.helper.adapter.LoanRequestBody;
import sr.we.shekelflowcore.entity.helper.vo.LoanRequestAssetsVO;
import sr.we.shekelflowcore.entity.helper.vo.LoanRequestVO;
import sr.we.shekelflowcore.settings.util.Constants;
import sr.we.ui.components.Highlight;
import sr.we.ui.views.LineAwesomeIcon;

import java.util.*;

/**
 * A Designer generated component for the add-requests template.
 * <p>
 * Designer will add and remove fields with @Id mappings but
 * does not overwrite or otherwise change this file.
 */
@Tag("add-requests")
@JsModule("./src/views/finance/loanrequests/add-requests.ts")
public class AddRequests extends LitTemplate {

    private final Build refresh;
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
    private Button backButton;
    @Id("save-btn")
    private Button saveBtn;
    private String business;
    @Id("contract-layout")
    private Div contractLayout;
    @Id("generate-contract-btn")
    private Button generateContractBtn;
    private LoanRequest loanRequest;

    /**
     * Creates a new AddRequests.
     */
    public AddRequests() {
        // You can initialise any data required for the connected UI components here.
        backButton.addClickListener(f -> {
            UI.getCurrent().navigate(RequestsView.class, new RouteParameters(new RouteParam("business", business)));
        });
        backButton.setIcon(new LineAwesomeIcon("la la-arrow-left"));
        backButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_ICON);


        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0px", 1), new FormLayout.ResponsiveStep("500px", 3));

        saveBtn.addClickListener(f -> onSave());


        refresh = new Build<>() {

            @Override
            public Object build() {
                board();
                return null;
            }
        };
        contractLayout.setVisible(false);
        generateContractBtn.addClickListener(f -> {
            /*try {
                createNewContent();
            } catch (IOException | Docx4JException | JAXBException | ParserConfigurationException |
                     TransformerException e) {
                throw new RuntimeException(e);
            }*/
        });
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
    }

    private void board() {
        UI current = UI.getCurrent();
        String token = AuthenticatedUser.token();
        new Thread(new Runnable() {
            @Override
            public void run() {

                LoanRequestService loanRequestService = ContextProvider.getBean(LoanRequestService.class);
                loanRequest = loanRequestService.get(loanRequest.getId(), token);
                current.access(() -> {
                    boardLayout.removeAll();
                    Board board = new Board();
                    boardLayout.add(board);

                    Long reduce = 0l;
                    List<LoanRequestPlan> loanRequestPlans = new ArrayList<>(AddRequests.this.loanRequest.getLoanRequestPlans());
                    if (loanRequestPlans != null && !loanRequestPlans.isEmpty()) {
                        reduce = loanRequestPlans.stream().map(f -> f.getFreqAmount()).reduce(0l, (subtotal, element) -> subtotal + element);
                    }

                    Highlight principal = new Highlight("Principal", () -> Constants.CURRENCY_FORMAT.format(loanRequest.getAmount()), () -> null);
                    Long finalReduce = reduce;
                    Highlight initial_frequency = new Highlight("Initial Frequency", () -> loanRequest.getFreq().getCaption() + " " + loanRequest.getFreqVal().doubleValue(), () -> finalReduce.doubleValue());
                    Highlight intrest = new Highlight("Intrest", () -> loanRequest.getIntrest() == null ? Constants.CURRENCY_FORMAT.format(0) : Constants.CURRENCY_FORMAT.format(loanRequest.getIntrest()), () -> null);
                    Highlight balance = new Highlight("Balance", () -> loanRequest.getBalance() == null ? Constants.CURRENCY_FORMAT.format(0) : Constants.CURRENCY_FORMAT.format(loanRequest.getBalance()), () -> loanRequest.getTransactionBalance() == null ? null : loanRequest.getTransactionBalance().doubleValue());
                    board.addRow(principal, initial_frequency, intrest, balance);
                });
            }
        }).start();

    }

    protected void onSave() {

        LoanRequestBody loanRequestBody = requestorForm.getLoanRequestBody();

        LoanRequestService bean = ContextProvider.getBean(LoanRequestService.class);
        LoanRequest loanRequest = null;
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
        List<String> strings = Arrays.asList(loanRequest.getId().toString());
        Map<String, List<String>> map = new HashMap<>();
        map.put("id", strings);
        QueryParameters queryParameters = new QueryParameters(map);
        UI.getCurrent().navigate(EditRequestsView.getLocation(business), queryParameters);
    }


}
