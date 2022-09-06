package sr.we.ui.views.pos;

import java.util.Objects;
import java.util.UUID;

public class ProductOrServiceGrid {

    private ProductOrService one, two, three, four;
    private UUID uuid;

    public ProductOrServiceGrid() {
        this.uuid = UUID.randomUUID();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProductOrServiceGrid that = (ProductOrServiceGrid) o;

        return Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return uuid != null ? uuid.hashCode() : 0;
    }

    public ProductOrService getOne() {
        return one;
    }

    public void setOne(ProductOrService one) {
        this.one = one;
    }

    public ProductOrService getTwo() {
        return two;
    }

    public void setTwo(ProductOrService two) {
        this.two = two;
    }

    public ProductOrService getThree() {
        return three;
    }

    public void setThree(ProductOrService three) {
        this.three = three;
    }

    public ProductOrService getFour() {
        return four;
    }

    public void setFour(ProductOrService four) {
        this.four = four;
    }
}
