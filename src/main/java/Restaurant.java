import java.util.concurrent.*;
import java.util.stream.Stream;

public class Restaurant {

    private final BlockingQueue<Order> orderQueue;
    private final BlockingQueue<Order> completedOrderQueue;

    public Restaurant(int numOfCooks, int maxOrders) {
        orderQueue = new PriorityBlockingQueue<>(maxOrders, Order::compare);
        completedOrderQueue = new LinkedBlockingQueue <>();
        ExecutorService kitchen = Executors.newFixedThreadPool(numOfCooks);
        Stream.generate(() -> new Cook(orderQueue, completedOrderQueue))
                .limit(numOfCooks)
                .forEach(kitchen::submit);
    }

    private class Cook implements Runnable {

        protected final BlockingQueue <Order> completedOrderQueue;
        private final BlockingQueue <Order> orderQueue;

        public Cook(BlockingQueue <Order> orderQueue, BlockingQueue <Order> completedOrderQueue) {
            this.orderQueue = orderQueue;
            this.completedOrderQueue = completedOrderQueue;
        }

        private void processOrder() throws InterruptedException {
            Order take = orderQueue.take();
            completedOrderQueue.add(take);
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(1000);
                    processOrder();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    void placeOrder(Order order) {
        this.orderQueue.add(order);
    }

    Order serve() {
        try {
            return completedOrderQueue.take();
        } catch (InterruptedException e) {
            return null;
        }
    }

}
