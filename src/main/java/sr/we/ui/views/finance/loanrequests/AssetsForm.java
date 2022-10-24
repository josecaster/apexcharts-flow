package sr.we.ui.views.finance.loanrequests;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.FileData;
import sr.we.ContextProvider;
import sr.we.data.controller.LoanRequestAssetsService;
import sr.we.data.controller.LoanRequestService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.LoanRequest;
import sr.we.shekelflowcore.entity.LoanRequestAssets;
import sr.we.shekelflowcore.entity.helper.Executable;
import sr.we.shekelflowcore.entity.helper.vo.LoanRequestAssetsFilesVO;
import sr.we.shekelflowcore.entity.helper.vo.LoanRequestAssetsVO;
import sr.we.ui.components.finance.LoanAssetsSelect;
import sr.we.ui.components.general.BusinessCurrencySelect;
import sr.we.util.FileBuffer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * A Designer generated component for the assets-form template.
 * <p>
 * Designer will add and remove fields with @Id mappings but
 * does not overwrite or otherwise change this file.
 */
@Tag("assets-form")
@JsModule("./src/views/finance/loanrequests/assets-form.ts")
public class AssetsForm extends LitTemplate {

    private final List<LoanRequestAssetsVO> list;
    private final Grid<LoanRequestAssetsVO> grid;
    private final FileBuffer multiFileMemoryBuffer;
    @Id("assets-grid-layout")
    private Div assetsGridLayout;
    //    @Id("asset-value-fld")
//    private NumberField assetValueFld;
    @Id("asset-memo-fld")
    private TextArea assetMemoFld;
    @Id("upload-btn")
    private Upload uploadBtn;
    //    @Id("asset-currency-cmb")
//    private CurrencySelect assetCurrencyCmb;
    @Id("asset-type-cmb")
    private LoanAssetsSelect assetTypeCmb;
    @Id("asset-currency-cmb1")
    private BusinessCurrencySelect assetCurrencyCmb1;
    @Id("asset-value-fld1")
    private NumberField assetValueFld1;
    @Id("add-assets-detail")
    private Details addAssetsDetail;
    @Id("view-assets-detail")
    private Details viewAssetsDetail;
    @Id("discard-assets-btn")
    private Button discardAssetsBtn;
    @Id("add-assets-btn")
    private Button addAssetsBtn;
    @Id("total-asset-lbl")
    private H3 totalAssetLbl;
    private LoanRequest loanRequest;
    private Executable refresh;

    /**
     * Creates a new AssetsForm.
     */
    public AssetsForm() {
        // You can initialise any data required for the connected UI components here.
//        Animated.animate(this, Animated.Animation.SLIDE_IN_RIGHT);

//        assetCurrencyCmb.setLabel("Appraisal currency");
//        assetCurrencyCmb.setHelperText(null);

        assetCurrencyCmb1.setLabel("Appraisal currency");
        assetCurrencyCmb1.setHelperText(null);

//        assetValueFld.setReadOnly(true);

        addAssetsDetail.setSummaryText("Add assets");
        viewAssetsDetail.setSummaryText("View assets");


        int maxFileSizeInBytes = 1 * 1024 * 1024; // 1.0 MB
        uploadBtn.setDropAllowed(true);
        uploadBtn.setAutoUpload(true);
        uploadBtn.setMaxFileSize(maxFileSizeInBytes);
        multiFileMemoryBuffer = new FileBuffer();
        uploadBtn.setReceiver(multiFileMemoryBuffer);


        discardAssetsBtn.addClickListener(f -> {
            assetValueFld1.clear();
            assetMemoFld.clear();
            assetCurrencyCmb1.clear();
            uploadBtn.clearFileList();
            assetTypeCmb.clear();
        });

        grid = new Grid<>();
        assetsGridLayout.add(grid);
        list = new ArrayList<>();
        grid.setItems(list);
        grid.getDataProvider().refreshAll();
        grid.addColumn(LoanRequestAssetsVO::getLoanAssets).setHeader("Asset type").setFlexGrow(0);
        grid.addColumn(f -> f.getCurrencyCode()).setHeader("Currency").setFlexGrow(0);
        grid.addColumn(LoanRequestAssetsVO::getAmount).setHeader("Amount").setFlexGrow(0);
        grid.addColumn(LoanRequestAssetsVO::getMemo).setHeader("Memo").setFlexGrow(1);
        grid.setAllRowsVisible(true);

        addAssetsBtn.addClickListener(f -> {
            LoanRequestAssetsVO vo = getVO();
            LoanRequestAssetsService loanRequestAssetsService = ContextProvider.getBean(LoanRequestAssetsService.class);
            if (vo.isNew()) {
                loanRequestAssetsService.create(AuthenticatedUser.token(), vo);
            } else {
                loanRequestAssetsService.edit(AuthenticatedUser.token(), vo);
            }
            LoanRequestService loanRequestService = ContextProvider.getBean(LoanRequestService.class);
            loanRequest = loanRequestService.get(loanRequest.getId(), AuthenticatedUser.token());
            setLoanRequest(loanRequest, refresh);
            discardAssetsBtn.click();
            if (!viewAssetsDetail.isOpened()) {
                viewAssetsDetail.setOpened(true);
            }
            grid.scrollIntoView();

        });

        totalAssetLbl.setVisible(false);
    }

    protected LoanRequestAssetsVO getVO() {
        LoanRequestAssetsVO loanRequestAssetsVO = new LoanRequestAssetsVO();
        loanRequestAssetsVO.setNew(true);
        loanRequestAssetsVO.setLoanAssets(assetTypeCmb.getValue().getId());
        loanRequestAssetsVO.setLoanRequest(loanRequest.getId());
        loanRequestAssetsVO.setMemo(assetMemoFld.getValue());
        loanRequestAssetsVO.setAmount(assetValueFld1.getValue() == null ? BigDecimal.ZERO : BigDecimal.valueOf(assetValueFld1.getValue()));
        loanRequestAssetsVO.setCurrency(assetCurrencyCmb1.getValue() == null ? null : assetCurrencyCmb1.getValue().getId());
        loanRequestAssetsVO.setValid(true);
        try {

            Set<String> files = multiFileMemoryBuffer.getFiles();
            for (String filename : files) {
                FileData fileData = multiFileMemoryBuffer.getFileData(filename);
                OutputStream outputBuffer = fileData.getOutputBuffer();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                baos.writeTo(outputBuffer);
                byte[] bytes = baos.toByteArray();
                baos.close();
                LoanRequestAssetsFilesVO loanRequestAssetsFilesVO = new LoanRequestAssetsFilesVO();
                loanRequestAssetsFilesVO.setNew(true);
                loanRequestAssetsFilesVO.setLoanAssets(loanRequestAssetsVO.getLoanAssets());
                loanRequestAssetsFilesVO.setLoanRequest(loanRequestAssetsVO.getLoanRequest());
                loanRequestAssetsFilesVO.setDoc(bytes);
                loanRequestAssetsFilesVO.setValid(true);
                loanRequestAssetsFilesVO.setName(fileData.getFileName());
                loanRequestAssetsFilesVO.setExtension(fileData.getFileName().substring(fileData.getFileName().lastIndexOf(".") + 1));
                loanRequestAssetsVO.addFile(loanRequestAssetsFilesVO);
            }
//            LoanRequestAssetsService loanRequestAssetsService = ContextProvider.getBean(LoanRequestAssetsService.class);
//            loanRequestAssetsService.create(AuthenticatedUser.token(), loanRequestAssetsVO);
//            if(build != null){
//                build.build();
//            }
//            UI.getCurrent().getPage().reload();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        return loanRequestAssetsVO;
    }

    protected void setLoanRequest(LoanRequest loanRequest, Executable refresh) {
        list.clear();
        this.refresh = refresh;
        this.loanRequest = loanRequest;
        assetTypeCmb.load(loanRequest.getLoan().getId());
        Set<LoanRequestAssets> loanRequestAssets = loanRequest.getLoanRequestAssets();
        for (LoanRequestAssets requestAssets : loanRequestAssets) {
            LoanRequestAssetsVO loanRequestAssetsVO = new LoanRequestAssetsVO();
            loanRequestAssetsVO.setNew(false);
            loanRequestAssetsVO.setId(requestAssets.getId());
            loanRequestAssetsVO.setLoanRequest(loanRequest.getId());
            loanRequestAssetsVO.setLoanAssets(requestAssets.getLoanAssets().getId());
            loanRequestAssetsVO.setMemo(requestAssets.getMemo());
            loanRequestAssetsVO.setValid(requestAssets.getValid());
            loanRequestAssetsVO.setAmount(requestAssets.getAmount());
            loanRequestAssetsVO.setCurrency(requestAssets.getCurrency() == null ? loanRequest.getCurrency().getId() : requestAssets.getCurrency().getId());
            loanRequestAssetsVO.setCurrencyCode(requestAssets.getCurrency() == null ? loanRequest.getCurrency().getCode() : requestAssets.getCurrency().getCode());
//            loanRequestAssetsVO.setFiles(requestAssets.getLoanRequestAssetsFiles());
            list.add(loanRequestAssetsVO);
        }
        grid.getDataProvider().refreshAll();
        if (list.size() > 0) {
            if (viewAssetsDetail.isOpened()) {
                viewAssetsDetail.setOpened(false);
            }
            if (addAssetsDetail.isOpened()) {
                addAssetsDetail.setOpened(false);
            }
        }

        addAssetsBtn.setVisible(false);
        discardAssetsBtn.setVisible(false);
        uploadBtn.setVisible(false);
        if (loanRequest.getStatus().compareTo(LoanRequest.Status.REQUESTED) == 0 || loanRequest.getStatus().compareTo(LoanRequest.Status.APPROVED) == 0) {
            addAssetsBtn.setVisible(true);
            discardAssetsBtn.setVisible(true);
            uploadBtn.setVisible(true);
        }
//        BigDecimal reduce = list.stream().map(f -> f.getAmount()).reduce(BigDecimal.ZERO, BigDecimal::add);
//        totalAssetLbl.setText("Total asset value " + loanRequest.getCurrency().getCode() + " " + reduce);
    }

    public List<LoanRequestAssetsVO> getList() {
        return list;
    }
}
