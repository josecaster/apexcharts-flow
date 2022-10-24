package sr.we.ui.views;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.RouterLink;
import sr.we.ContextProvider;
import sr.we.data.controller.BusinessService;
import sr.we.security.AuthenticatedUser;
import sr.we.shekelflowcore.entity.Business;
import sr.we.shekelflowcore.entity.ThisUser;
import sr.we.shekelflowcore.entity.helper.MappedSuperClass;
import sr.we.shekelflowcore.entity.helper.Token;
import sr.we.shekelflowcore.exception.ValidationException;
import sr.we.ui.views.person.GeneralView;
import sr.we.ui.views.business.BusinessViewCreate;

import java.util.*;
import java.util.stream.Collectors;

public class UserCompanyProfile extends Button {

    private final ListBox<Long> listBox;
    private List<Business> businesses;
    private boolean clear, clicked;

    public UserCompanyProfile(Dialog dialog) {
        super();
        dialog.setModal(false);
        dialog.setDraggable(true);
        dialog.setHeaderTitle("Your account");
        dialog.getElement().getStyle().set("position", "absolute");
        dialog.getElement().getStyle().set("top", "0px");
        dialog.getElement().getStyle().set("left", "0px");

        Image img = new Image("images/seaqns-icon.png", "seaqns icon");
        img.setWidth("36px");
        setIcon(img);

//        margin-left: var(--lumo-space-s);
//        margin-right: var(--lumo-space-s);
//        border-radius: 100px;
        getElement().getStyle().set("margin-left", "var(--lumo-space-s)");
        getElement().getStyle().set("margin-right", "var(--lumo-space-s)");
        getElement().getStyle().set("border-radius", "100px");


        addThemeVariants(ButtonVariant.LUMO_LARGE,ButtonVariant.LUMO_PRIMARY);

        listBox = new ListBox<>();
        listBox.setRenderer(new ComponentRenderer<>(id -> {
            Optional<Business> any = businesses.stream().filter(f -> f.getId().compareTo(id) == 0).findAny();
            boolean present = any.isPresent();
            Business business = !present ? null : any.get();
            HorizontalLayout row = new HorizontalLayout();
            row.setAlignItems(FlexComponent.Alignment.CENTER);

            Avatar avatar = new Avatar();
            avatar.setColorIndex(new Random().nextInt(7 - 1 + 1) + 1);
            avatar.setName(present ? business.getName() : getTranslation("sr.we.personal"));
//            avatar.setImage(person.getPictureUrl());

            Span name = new Span(present ? business.getName() : getTranslation("sr.we.personal"));
            Span profession = new Span(present ? business.getBusinessType().getName() : getTranslation("sr.we.personal.info"));
            profession.getStyle().set("color", "var(--lumo-secondary-text-color)").set("font-size", "var(--lumo-font-size-s)");

            VerticalLayout column = new VerticalLayout(name, profession);
            column.setPadding(false);
            column.setSpacing(false);

            row.add(avatar, column);
            row.getStyle().set("line-height", "var(--lumo-line-height-m)");
            return row;
        }));

        dialog.add(listBox);


        RouterLink createBusinessLink = new RouterLink("Create a new business", BusinessViewCreate.class);
        HorizontalLayout businessLayout = new HorizontalLayout(new LineAwesomeIcon("las la-feather"), createBusinessLink);
        businessLayout.setWidthFull();
        dialog.add(businessLayout);

        dialog.add(new Hr());

        new Text("You are singed in as ");

        RouterLink manageProfileLink = new RouterLink("Manage your profile", GeneralView.class);
        HorizontalLayout profileLayout = new HorizontalLayout(new LineAwesomeIcon("la la-user-circle"), manageProfileLink);
        profileLayout.setWidthFull();
        dialog.add(profileLayout);

        AuthenticatedUser authenticatedUser = ContextProvider.getBean(AuthenticatedUser.class);
        Optional<ThisUser> maybeUser = authenticatedUser.get();
        if (maybeUser.isEmpty()) {
            authenticatedUser.logout();
            throw new ValidationException("Invalid Authentication");
        }
        ThisUser thisUser = maybeUser.get();
        Avatar avatar = new Avatar(thisUser.getUsername()/*, user.getProfilePictureUrl()*/);
        avatar.setColorIndex(new Random().nextInt(7 - 1 + 1) + 1);
        avatar.addClassNames("me-xs");

        Span name = new Span(thisUser.getUsername());
        name.addClassNames("font-medium", "text-s", "text-secondary");


        Footer footer = new Footer(avatar, name);
        footer.addClassNames("footer");
//        dialog.add(footer);
        ContextMenu userMenu = new ContextMenu(footer);
        userMenu.setOpenOnClick(true);
        userMenu.addItem("Logout", e -> authenticatedUser.logout());

        LanguageSelect languageSelect = new LanguageSelect();
        languageSelect.setWidthFull();
        dialog.add(languageSelect);

        dialog.add(new Hr());

        com.vaadin.flow.component.button.Button cancelButton = new com.vaadin.flow.component.button.Button("Cancel", e -> dialog.close());
        dialog.getFooter().add(cancelButton);
        clear = false;
        addClickListener(e -> {
            clicked = true;
            clear = true;
            listBox.clear();
            clear = false;

            setValues(listBox);
            dialog.open();
            clicked = false;
        });

        listBox.addValueChangeListener(id -> {
            if (id.getValue() == null) {
//                setText(getTranslation("sr.we.personal"));
                return;
            }
            Optional<Business> any = businesses.stream().filter(f -> f.getId().compareTo(id.getValue()) == 0).findAny();
            if (any.isPresent()) {
                Business business = any.get();
                setText(business.getName());
            } else {
                setText(getTranslation("sr.we.personal"));

            }
        });


        listBox.addValueChangeListener(id -> {
            BusinessService businessService = ContextProvider.getBean(BusinessService.class);
            Token token1 = thisUser.getToken();
            String token = token1.getToken();
            if (id.getValue() == null) {
                if (!clear) {
                    businessService.unselectAll(token);
                }
                return;
            }
            Optional<Business> any = businesses.stream().filter(f -> f.getId().compareTo(id.getValue()) == 0).findAny();

            if (any.isPresent()) {
                businessService.select(any.get().getId(), token);
            } else {
                businessService.unselectAll(token);
            }
            dialog.close();

        });

        setValues(listBox);
    }

    private void setValues(ListBox<Long> listBox) {
        String token = AuthenticatedUser.token();
        BusinessService businessService = ContextProvider.getBean(BusinessService.class);
        businesses = businessService.listFromUserRoles(token).getResult();
        if (businesses == null) {
            businesses = new ArrayList<>();
        }
        List<Long> collect = businesses.stream().map(MappedSuperClass::getId).collect(Collectors.toList());
        collect.add(0, 0L);
        listBox.setItems(collect);
        Business business = businessService.get(null, AuthenticatedUser.token());
//        Optional<Business> max = businesses.stream().filter(f -> f.getCounter().compareTo(0L) != 0).max(Comparator.comparingLong(Business::getCounter));
        if (business != null) {
            listBox.setValue(business.getId());
        } else {
            listBox.setValue(0L);
        }
        listBox.addValueChangeListener(f -> {
            if(!clicked) {
                UI.getCurrent().navigate(ReRouteLayout.class);
            }
        });
    }

    public Long getListBox() {
        return listBox.getValue();
    }
}
