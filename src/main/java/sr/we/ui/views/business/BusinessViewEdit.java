package sr.we.ui.views.business;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;
import sr.we.ContextProvider;
import sr.we.data.controller.BusinessService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Business;
import sr.we.shekelflowcore.entity.Role;
import sr.we.shekelflowcore.entity.helper.vo.BusinessVO;
import sr.we.shekelflowcore.exception.ValidationException;
import sr.we.ui.components.business.BusinessOrganisationTypeSelect;
import sr.we.ui.components.business.BusinessTypeSelect;
import sr.we.ui.components.general.BusinessCurrencySelect;
import sr.we.ui.components.general.CountrySelect;
import sr.we.ui.views.ReRouteLayout;
import sr.we.ui.views.StateListenerLayout;
import sr.we.ui.views.settings.SettingsLayout;
import sr.we.util.FileBuffer;

import javax.annotation.security.RolesAllowed;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Route(value = "edit-business", layout = SettingsLayout.class)
@RolesAllowed({Role.user, Role.staff, Role.owner, Role.admin})
public class BusinessViewEdit extends StateListenerLayout implements HasDynamicTitle, BeforeEnterObserver {

    private final TextField companyName;
    private final BusinessTypeSelect typeOfBusiness;
    private final CountrySelect country;
    private final BusinessCurrencySelect businessCurrency;
    private final BusinessOrganisationTypeSelect typeOfOrganization;
    private final Upload upload;
    private final FileBuffer multiFileMemoryBuffer;
    private final Image image;
    private Business business;

    public BusinessViewEdit() {
        companyName = new TextField(getTranslation("sr.we.company.name"));
        companyName.setRequired(true);
        companyName.setRequiredIndicatorVisible(true);

        typeOfBusiness = new BusinessTypeSelect();
        typeOfBusiness.setRequiredIndicatorVisible(true);

        country = new CountrySelect();
        country.setRequiredIndicatorVisible(true);
        country.setReadOnly(true);

        businessCurrency = new BusinessCurrencySelect();
        businessCurrency.setRequiredIndicatorVisible(true);
        businessCurrency.setReadOnly(true);

        typeOfOrganization = new BusinessOrganisationTypeSelect();
        typeOfOrganization.setRequiredIndicatorVisible(true);

        int maxFileSizeInBytes = 1 * 1024 * 1024; // 1.0 MB
        upload = new Upload();
        upload.setDropLabel(new Span("Add a company ICON"));
        upload.setDropAllowed(true);
        upload.setAutoUpload(true);
        upload.setMaxFileSize(maxFileSizeInBytes);
        upload.setMaxFiles(1);
        multiFileMemoryBuffer = new FileBuffer();
        upload.setReceiver(multiFileMemoryBuffer);

        image = new Image();
        image.setVisible(false);


        FormLayout formLayout = new FormLayout();
        formLayout.add(companyName, typeOfBusiness, country, businessCurrency, typeOfOrganization, upload, image);
        state(companyName, typeOfBusiness, country, businessCurrency, typeOfOrganization);
        formLayout.getElement().getStyle().set("align-self", "center");

        formLayout.setWidth("500px");
        add(formLayout);
        formLayout.setResponsiveSteps(
                // Use one column by default
                new FormLayout.ResponsiveStep("0", 1)/*,
                // Use two columns, if layout's width exceeds 500px
                new ResponsiveStep("500px", 2)*/);
//// Stretch the username field over 2 columns
//        formLayout.setColspan(username, 2);
    }

    @Override
    protected void onSave() {
        BusinessService businessService = ContextProvider.getBean(BusinessService.class);
        String token = AuthenticatedUser.token();
        BusinessVO vo = new BusinessVO();
        vo.setBusinessType(typeOfBusiness.getValue().getId());
        vo.setBusinessOrganisationType(typeOfOrganization.getValue().getId());
        vo.setCountry(country.getValue().getId());
        vo.setCurrency(businessCurrency.getValue().getId());
        vo.setName(companyName.getValue());
        vo.setId(business.getId());
//        for (String filename : multiFileMemoryBuffer.getFiles()) {
//            FileData fileData = multiFileMemoryBuffer.getFileData(filename);
//            try {
//                FileInputStream fileInputStream = new FileInputStream(fileData.getFile());
        if (multiFileMemoryBuffer.getFile() != null) {
            vo.setImage(multiFileMemoryBuffer.getContentAsByte());
        }
        if (vo.getImage() == null) {
            vo.setImage(business.getImage());
        }
//                fileInputStream.close();
//            } catch (final IOException e) {
//                throw new RuntimeException(e);
//            }
//        }
        Business business = businessService.edit(token, vo);

//        Notification notification = new Notification();
//        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
//        notification.setText(getTranslation("sr.we.success"));
//        notification.setDuration(5000);
//        notification.setPosition(Notification.Position.MIDDLE);
//        notification.open();

        UI.getCurrent().navigate(ReRouteLayout.class);


    }

    @Override
    protected void onDiscard() {
        companyName.clear();
        typeOfBusiness.clear();
        country.clear();
        businessCurrency.clear();
        typeOfOrganization.clear();
        stateChanged(false, false);
    }

    @Override
    protected boolean validate() {
        if (companyName.isEmpty()) {
            return false;
        }
        if (typeOfBusiness.getOptionalValue().isEmpty()) {
            return false;
        }
        if (country.getOptionalValue().isEmpty()) {
            return false;
        }
        if (businessCurrency.getOptionalValue().isEmpty()) {
            return false;
        }
        return typeOfOrganization.getOptionalValue().isPresent();
    }

    @Override
    public String getPageTitle() {
        return getTranslation("sr.we.update.business");
    }


    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        QueryParameters routeParameters = event.getLocation().getQueryParameters();
        List<String> id1 = routeParameters.getParameters().get("id");
        Optional<String> id = id1.stream().findAny();
        if (id.isEmpty()) {
            event.forwardTo(BusinessView.class);
            throw new ValidationException("Invalid Authentication");
        }
        String token = AuthenticatedUser.token();
        UI current = UI.getCurrent();
        new Thread(() -> {
            BusinessService businessService = ContextProvider.getBean(BusinessService.class);
            business = businessService.get(Long.valueOf(id.get()), token);
            current.access(() -> {
                typeOfBusiness.setValue(business.getBusinessType());
                typeOfOrganization.setValue(business.getBusinessOrganisationType());
                country.setValue(business.getCountry());
                businessCurrency.setValue(business.getCurrency());
                companyName.setValue(business.getName());
                if (business.getImage() != null) {
                    image.setVisible(true);
                    StreamResource streamResource = new StreamResource("", new InputStreamFactory() {
                        @Override
                        public InputStream createInputStream() {
                            return new ByteArrayInputStream(business.getImage());
                        }
                    });
                    image.setSrc(streamResource);
                }
            });
        }).start();


//        vo.setBusinessType(typeOfBusiness.getValue().getId());
//        vo.setBusinessOrganisationType(typeOfOrganization.getValue().getId());
//        vo.setCountry(country.getValue().getId());
//        vo.setCurrency(businessCurrency.getValue().getId());
//        vo.setName(companyName.getValue());
    }
}
