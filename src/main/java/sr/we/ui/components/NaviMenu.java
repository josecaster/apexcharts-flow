package sr.we.ui.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;

import java.util.List;
import java.util.stream.Collectors;

public class NaviMenu extends Div {

    private final String CLASS_NAME = "navi-menu";
    private String businessId;

    public NaviMenu(String businessId) {
        this.businessId = businessId;

        setClassName(CLASS_NAME);
    }

    protected void addNaviItem(NaviItem item) {
        add(item);
    }

    protected void addNaviItem(NaviItem parent, NaviItem item) {
        parent.addSubItem(item);
        addNaviItem(item);
    }

    public void filter(String filter) {
        getNaviItems().forEach(naviItem -> {
            boolean matches = naviItem.getText().toLowerCase().contains(filter.toLowerCase());
            naviItem.setVisible(matches);
        });
    }

    public NaviItem addNaviItem(String menuTitle, String iconClass, Class<? extends Component> navigationTarget) {
        NaviItem item = new NaviItem(menuTitle, iconClass, navigationTarget,businessId);
        addNaviItem(item);
        return item;
    }

    public NaviItem addNaviItem(NaviItem parent, String text,String iconClass, Class<? extends Component> navigationTarget) {
        NaviItem item = new NaviItem(text,iconClass, navigationTarget,businessId);
        item.setSuperItem(parent);
        addNaviItem(parent, item);
        if(parent != null){
            item.setVisible(false);
        }
        if(parent != null && navigationTarget != null){
            item.getStyle().set("padding-left", "1em");
        }
        return item;
    }

    public List<NaviItem> getNaviItems() {
        List<NaviItem> items = (List) getChildren().collect(Collectors.toList());
        return items;
    }

}