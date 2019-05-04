import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class RestaurantOrdersUsingBlockingQueueTest {

    @Test
    void restaurantCanTakeOrders() {
        Restaurant restaurant = new Restaurant(10, 20);

        final Order pancake = new Order("Pancake");
        Stream.generate(() -> pancake)
                .limit(15)
                .forEach(restaurant::placeOrder);
        assertThat(Stream.generate(restaurant::serve)
                .limit(15)
                .collect(Collectors.toList()))
                .containsOnly(pancake);

        Order cake = new Order("Cakes");
        Stream.generate(() -> cake)
                .limit(5)
                .forEach(restaurant::placeOrder);
        assertThat(Stream.generate(restaurant::serve)
                .limit(5)
                .collect(Collectors.toList()))
                .containsOnly(cake);
    }

    @Test
    void restaurantCanTakeOrdersOnPriority() {

        Restaurant restaurant = new Restaurant(1, 4);
        restaurant.placeOrder(new Order("Sandwitch"));
        restaurant.placeOrder(new Order("Chees Cake"));
        restaurant.placeOrder(new Order("Waffles"));
        Order cofee = new Order(true, "Cofee");
        restaurant.placeOrder(cofee);
        assertThat(restaurant.serve())
                .isEqualTo(cofee);
    }

}
