package sr.we.ui.views.finance.loans.tabs.settings;

import com.vaadin.flow.component.select.Select;
import sr.we.ContextProvider;
import sr.we.data.controller.PojoService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.AssetType;
import sr.we.shekelflowcore.entity.Currency;

import java.util.List;

public class AssetTypeSelect extends Select<AssetType> {

    public AssetTypeSelect() {
        PojoService pojoService = ContextProvider.getBean(PojoService.class);
        String token = AuthenticatedUser.token();

        List<AssetType> currencies = pojoService.listAssetTypes(token);
        setItems(currencies);

        setItemLabelGenerator((f) -> f.getName());

        setLabel(getTranslation());
        setHelperText(getTranslation("sr.we.asset.type.info"));
    }

    public String getTranslation() {
        return getTranslation("sr.we.asset.type");
    }
}
