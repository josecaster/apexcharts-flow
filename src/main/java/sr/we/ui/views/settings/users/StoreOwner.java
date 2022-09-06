package sr.we.ui.views.settings.users;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.dom.Element;

/**
 * A Designer generated component for the store-owner template.
 *
 * Designer will add and remove fields with @Id mappings but
 * does not overwrite or otherwise change this file.
 */
@Tag("store-owner")
@JsModule("./src/views/settings/store-owner.ts")
public class StoreOwner extends LitTemplate {

    @Id("avatar")
    private Avatar avatar;
    @Id("store-owner-link")
    private Element storeOwnerLink;
    @Id("last-login")
    private Paragraph lastLogin;
    @Id("store-owner-permissions")
    private Element storeOwnerPermissions;
    @Id("transfer-ownership")
    private Element transferOwnership;

    /**
     * Creates a new StoreOwner.
     */
    public StoreOwner() {
        // You can initialise any data required for the connected UI components here.
    }

    public Avatar getAvatar() {
        return avatar;
    }

    public void setAvatar(Avatar avatar) {
        this.avatar = avatar;
    }

    public Element getStoreOwnerLink() {
        return storeOwnerLink;
    }

    public void setStoreOwnerLink(Element storeOwnerLink) {
        this.storeOwnerLink = storeOwnerLink;
    }

    public Paragraph getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Paragraph lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Element getStoreOwnerPermissions() {
        return storeOwnerPermissions;
    }

    public void setStoreOwnerPermissions(Element storeOwnerPermissions) {
        this.storeOwnerPermissions = storeOwnerPermissions;
    }

    public Element getTransferOwnership() {
        return transferOwnership;
    }

    public void setTransferOwnership(Element transferOwnership) {
        this.transferOwnership = transferOwnership;
    }
}
