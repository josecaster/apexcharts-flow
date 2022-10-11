package sr.we.ui.views.pos;

import java.util.Collection;
import java.util.Map;

public interface IFee {
    Collection<Item> getItemList();

    void addFeeMap(String title, String toString);

    Map<String, Object> getFeeMap();
}
