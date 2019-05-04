import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Order {

    private final boolean highPriority;
    private final List<String> dishes;

    public Order(String ... dishes) {
        this(false, dishes);
    }

    public Order(boolean highPriority, String ... dishes) {
        this.highPriority = highPriority;
        this.dishes = Arrays.stream(dishes).collect(Collectors.toList());
    }

    static int compare(Order a, Order b) {
        return Boolean.compare(b.isHighPriority(), a.isHighPriority());
    }

    public boolean isHighPriority() {
        return highPriority;
    }

    @Override
    public String toString() {
        return "Order{" +
                "highPriority=" + highPriority +
                ", dishes=" + dishes +
                '}';
    }
}
