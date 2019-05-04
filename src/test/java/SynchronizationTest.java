import org.assertj.core.api.Assumptions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class SynchronizationTest {

    public class Account {

        private int balance;

        public Account() {
            this.balance = 0;
        }

        void increament() {
            this.balance = this.balance + 1;
        }

        public int getBalance() {
            return balance;
        }
    }

    @Test
    void testConcurrentWriteMayFailWithoutSynchronization() throws InterruptedException {

        Account newAccount = new Account();
        ExecutorService exe = Executors.newFixedThreadPool(10);

        IntStream.range(0, 1000)
                .forEach(a -> exe.submit(() -> newAccount.increament()));

        exe.awaitTermination(1000, TimeUnit.MILLISECONDS);

        Assumptions.assumeThat(newAccount.getBalance())
                .isEqualTo(1000);
    }

    public class SynchronizedAccount {

        private int balance;

        public SynchronizedAccount() {
            this.balance = 0;
        }

        synchronized void increament() {
            this.balance = this.balance + 1;
        }

        synchronized int getBalance() {
            return this.balance;
        }
    }

    @Test
    void testConcurrentWriteSuccessfulWithSynchronization() throws InterruptedException {

        SynchronizedAccount newAccount = new SynchronizedAccount();
        ExecutorService exe = Executors.newCachedThreadPool();

        IntStream.range(0, 1000)
                .forEach(a -> exe.submit(newAccount::increament));

        exe.awaitTermination(100, TimeUnit.MILLISECONDS);

        Assumptions.assumeThat(newAccount.getBalance())
                .isEqualTo(1000);
    }


}
