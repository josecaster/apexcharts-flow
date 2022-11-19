package sr.we.ui.components;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.*;
import sr.we.ui.views.LineAwesomeIcon;

//@CssImport("./styles/components/navi-item.css")
public class NaviItem extends HorizontalLayout {

    private String CLASS_NAME = "navi-item";

    private int level = 0;
    private Component link;
    private Class<? extends Component> navigationTarget;
    private String text;
    protected Button expandCollapse;
    private NaviItem superItem;
    private List<NaviItem> subItems;
    private boolean subItemsVisible = false;
    private String businessId;

    public NaviItem(String text, Class<? extends Component> navigationTarget, String businessId) {
        this(text, null, navigationTarget,businessId);


    }

    public NaviItem(String text, String iconClass, Class<? extends Component> navigationTarget, String businessId) {

        setClassName(CLASS_NAME);
        setLevel(0);
        setPadding(false);
        setMargin(false);
        setWidthFull();
        this.businessId = businessId;
        this.text = text;
        this.navigationTarget = navigationTarget;

        if (navigationTarget != null) {
            RouterLink link = new RouterLink();
            link.addClassNames("menu-item-link");
            link.setRoute(navigationTarget, new RouteParameters(new RouteParam("business",businessId)));

            Span span = new Span(this.text);
            span.addClassNames("menu-item-text");

            if (iconClass != null) {
                Image img = new Image(iconClass, "icon by Icons8");
                img.setWidth("18px");
                img.setHeight("18px");
                img.getElement().getStyle().set("margin-inline-end", "var(--lumo-space-s)");
                img.getElement().getStyle().set("margin-top", "calc(var(--lumo-space-xs) * 0.5)");
                link.add(iconClass.startsWith("icons/menus/") ? img : new LineAwesomeIcon(iconClass));
            }
            link.add(span);

            link.setHighlightCondition(HighlightConditions.sameLocation());
            this.link = link;
        } else {
            Div div = new Div();
            div.addClickListener(e -> {
                expandCollapse.click();
            });
            div.addClassNames(CLASS_NAME + "__link","menu-item-link");
            Span span = new Span(this.text);
            span.addClassNames("menu-item-text");

            if (iconClass != null) {
                Image img = new Image(iconClass, "icon by Icons8");
                img.setWidth("18px");
                img.setHeight("18px");
                img.getElement().getStyle().set("margin-inline-end", "var(--lumo-space-s)");
                img.getElement().getStyle().set("margin-top", "calc(var(--lumo-space-xs) * 0.5)");
                div.add(iconClass.startsWith("icons/menus/") ? img : new LineAwesomeIcon(iconClass));
            }
            div.add(span);
            this.link = div;
            getElement().getStyle().set("border-bottom", "solid 1px var(--lumo-contrast-10pct)");
        }
        Icon i = new Icon(VaadinIcon.CARET_DOWN);
        i.getElement().setAttribute("slot", "prefix");
        expandCollapse = new Button();
        expandCollapse.setIcon(i);
        expandCollapse.addThemeVariants(ButtonVariant.LUMO_SMALL,
                ButtonVariant.LUMO_TERTIARY);
        expandCollapse.addClickListener(event -> setSubItemsVisible(!subItemsVisible));
        expandCollapse.setVisible(false);
        expandCollapse.getElement().getStyle().set("margin-left","auto");

        subItems = new ArrayList<>();
        add(link, expandCollapse);

        link.getElement().getStyle().set("width","100%");
    }

    public boolean isHighlighted(AfterNavigationEvent e) {
        return link instanceof RouterLink
                && ((RouterLink) link).getHighlightCondition().shouldHighlight((RouterLink) link, e);
    }

    public void setLevel(int level) {
        this.level = level;
        if (level > 0) {
            getElement().setAttribute("level", Integer.toString(level));
        }
    }

    public int getLevel() {
        return level;
    }

    public Class<? extends Component> getNavigationTarget() {
        return navigationTarget;
    }

    public void addSubItem(NaviItem item) {
        if (!expandCollapse.isVisible()) {
            expandCollapse.setVisible(true);
        }
        item.setLevel(getLevel() + 1);
        subItems.add(item);
    }

    private void setSubItemsVisible(boolean visible) {
        if (level == 0) {
            expandCollapse.setIcon(new Icon(visible ? VaadinIcon.CARET_UP : VaadinIcon.CARET_DOWN));
        }
        subItems.forEach(item -> item.setVisible(visible));
        subItemsVisible = visible;
    }

    public String getText() {
        return text;
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);

        // If true, we only update the icon. If false, we hide all the sub items
        if (visible) {
            if (level == 0) {
                expandCollapse.setIcon(new Icon(VaadinIcon.CARET_DOWN));
            }
        } else {
            setSubItemsVisible(visible);
        }
    }

    public void setSuperItem(NaviItem superItem) {
        this.superItem = superItem;
    }

    public NaviItem getSuperItem() {
        return superItem;
    }

    public List<NaviItem> getSubItems() {
        return subItems;
    }

}