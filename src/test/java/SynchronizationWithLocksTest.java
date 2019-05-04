import org.assertj.core.api.Assumptions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.IntStream;

public class SynchronizationWithLocksTest {

    public class Account {

        private int balance;

        public Account() {
            this.balance = 0;
        }

        void increment() {
            this.balance = getBalance() + 1;
        }

        void decrement() {
            this.balance = getBalance() - 1;
        }

        public int getBalance() {
            return balance;
        }
    }

    @Test
    void testConcurrentWriteMayFailWithoutSynchronization() throws InterruptedException {

        Account newAccount = new Account();
        ExecutorService exe = Executors.newCachedThreadPool();

        IntStream.range(0, 100)
                .peek(System.out::println)
                .forEach(a -> exe.submit(newAccount::increment));

        IntStream.range(0, 100)
                .peek(System.out::println)
                .forEach(a -> exe.submit(newAccount::decrement));


        exe.awaitTermination(1000, TimeUnit.MILLISECONDS);

        Assumptions.assumeThat(newAccount.getBalance())
                .isEqualTo(0);
    }

    public class SynchronizedAccount {

        private int balance;

        private ReadWriteLock reentrantLock;

        public SynchronizedAccount() {
            this.balance = 0;
            this.reentrantLock = new ReentrantReadWriteLock();
        }

        void increment()
        {
            Lock writeLock = reentrantLock.writeLock();
            try {
                writeLock.lock();
                setBalance(this.balance+1);
            } finally {
                writeLock.unlock();
            }
        }

        void decrement()
        {
            Lock writeLock = reentrantLock.writeLock();
            try {
                writeLock.lock();
                setBalance(this.balance-1);
            } finally {
                writeLock.unlock();
            }
        }

        private void setBalance(int balance) {
           this.balance = balance;
        }

        int getBalance() {
            Lock readLock = reentrantLock.readLock();
            try {
                readLock.lock();
                return this.balance;
            } finally {
                readLock.unlock();
            }
        }

    }

    @Test
    void testConcurrentWriteSuccessfulWithSynchronization() throws InterruptedException {

        SynchronizedAccount newAccount = new SynchronizedAccount();
        ExecutorService exe = Executors.newCachedThreadPool();

        IntStream.range(0, 100)
                .peek(System.out::println)
                .forEach(a -> exe.submit(newAccount::increment));

        IntStream.range(0, 100)
                .peek(System.out::println)
                .forEach(a -> exe.submit(newAccount::decrement));


        exe.awaitTermination(1000, TimeUnit.MILLISECONDS);

        Assumptions.assumeThat(newAccount.getBalance())
                .isEqualTo(0);
    }


}
