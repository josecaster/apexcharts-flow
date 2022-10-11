package sr.we.ui.views.finance.loans.tabs.request.assets;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.FileData;
import sr.we.ContextProvider;
import sr.we.data.controller.LoanRequestAssetsService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.LoanRequest;
import sr.we.shekelflowcore.entity.helper.Executable;
import sr.we.shekelflowcore.entity.helper.vo.LoanRequestAssetsFilesVO;
import sr.we.shekelflowcore.entity.helper.vo.LoanRequestAssetsVO;
import sr.we.ui.components.EmailAddress;
import sr.we.ui.components.finance.FrequencyField;
import sr.we.ui.components.finance.LoanAssetsSelect;
import sr.we.ui.components.general.CurrencySelect;
import sr.we.ui.views.StateListenerLayout;
import sr.we.util.FileBuffer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Set;

public class LRAssetsLayout extends StateListenerLayout {


    private final TextArea memoFld;
    private final LoanAssetsSelect loanAssetsSelect;

    private final FormLayout layout;
    private final FileBuffer multiFileMemoryBuffer;
    private final Upload upload;

    private String business;
    private final LoanRequest loanRequest;

    private Executable executable;


    public LRAssetsLayout(LoanRequest loanRequest) {
        this.loanRequest = loanRequest;
        layout = new FormLayout();


        add(layout);
        layout.setMaxWidth("500px");

        int maxFileSizeInBytes = 1 * 1024 * 1024; // 1.0 MB
        upload = new Upload();
        upload.setDropAllowed(true);
        upload.setAutoUpload(true);
        upload.setMaxFileSize(maxFileSizeInBytes);
        multiFileMemoryBuffer = new FileBuffer();
        upload.setReceiver(multiFileMemoryBuffer);

        layout.add(upload);

        loanAssetsSelect = new LoanAssetsSelect(loanRequest.getLoan().getId());
        loanAssetsSelect.setRequiredIndicatorVisible(true);
        loanAssetsSelect.setWidthFull();
        layout.addFormItem(loanAssetsSelect, "Asset Type");

        memoFld = new TextArea();
        layout.addFormItem(memoFld, "Memo");

        state(loanAssetsSelect);
        memoFld.setWidthFull();
        upload.addSucceededListener(f -> {
            listenToState();
        });
        upload.addFileRejectedListener(f -> {
            listenToState();
        });
        upload.getElement().addEventListener("file-remove", event -> {
            if (!multiFileMemoryBuffer.getFiles().isEmpty()) {
                upload.clearFileList();
            }
            listenToState();
        }).addEventData("event.detail.file.name");

        layout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
    }

    @Override
    protected void onSave() {
        try {
            LoanRequestAssetsVO loanRequestAssetsVO = new LoanRequestAssetsVO();
            loanRequestAssetsVO.setLoanAssets(loanAssetsSelect.getValue().getId());
            loanRequestAssetsVO.setLoanRequest(loanRequest.getId());
            loanRequestAssetsVO.setMemo(memoFld.getValue());
            loanRequestAssetsVO.setAmount(BigDecimal.ZERO);
            loanRequestAssetsVO.setValid(false);

            Set<String> files = multiFileMemoryBuffer.getFiles();
            for (String filename : files) {
                FileData fileData = multiFileMemoryBuffer.getFileData(filename);
                OutputStream outputBuffer = fileData.getOutputBuffer();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                baos.writeTo(outputBuffer);
                byte[] bytes = baos.toByteArray();
                baos.close();
                LoanRequestAssetsFilesVO loanRequestAssetsFilesVO = new LoanRequestAssetsFilesVO();
                loanRequestAssetsFilesVO.setLoanAssets(loanRequestAssetsVO.getLoanAssets());
                loanRequestAssetsFilesVO.setLoanRequest(loanRequestAssetsVO.getLoanRequest());
                loanRequestAssetsFilesVO.setDoc(bytes);
                loanRequestAssetsFilesVO.setValid(true);
                loanRequestAssetsFilesVO.setName(fileData.getFileName());
                loanRequestAssetsFilesVO.setExtension(fileData.getFileName().substring(fileData.getFileName().lastIndexOf(".")+1));
                loanRequestAssetsVO.addFile(loanRequestAssetsFilesVO);
            }
            LoanRequestAssetsService loanRequestAssetsService = ContextProvider.getBean(LoanRequestAssetsService.class);
            loanRequestAssetsService.create(AuthenticatedUser.token(), loanRequestAssetsVO);
            if(executable != null){
                executable.build();
            }
            UI.getCurrent().getPage().reload();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    protected void onDiscard() {
        memoFld.clear();
        loanAssetsSelect.clear();
        upload.clearFileList();
    }

    @Override
    protected boolean validate() {
        if (loanAssetsSelect.isEmpty()) {
            return false;
        }
        Set<String> files = multiFileMemoryBuffer.getFiles();
        return !files.isEmpty();
    }

    public void setBusiness(String business) {
        this.business = business;
    }

    public void setLoanRequest(LoanRequest loanRequest) {
        actionLayout.setVisible(false);
    }

    private void setReadOnly(TextField nameFld, TextField mobileFld, EmailAddress emailAddress, CurrencySelect currencyFld, FrequencyField frequencyFld, BigDecimalField requestedAmountFld, DatePicker datePicker) {
        nameFld.setReadOnly(true);
        mobileFld.setReadOnly(true);
        emailAddress.setReadOnly(true);
        currencyFld.setReadOnly(true);
        frequencyFld.setReadOnly(true);
        requestedAmountFld.setReadOnly(true);
        datePicker.setReadOnly(true);
    }

    public void setBuild(Executable executable) {
        this.executable = executable;
    }
}
