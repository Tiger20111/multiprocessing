package readwritelock;

import readwritelock.interfaces.ReadLock;
import readwritelock.interfaces.ReadWriteLock;
import readwritelock.interfaces.WriteLock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class SimpleReadWriteLock implements ReadWriteLock {
    private int readers;
    private boolean writer;
    private Object lock;
    private Lock readLock, writeLock;

    public SimpleReadWriteLock() {
        writer = false;
        readers = 0;
        lock = new Object();
        readLock = new SimpleReadLock();
        writeLock = new SimpleWriteLock();
    }

    @Override
    public Lock readLock() {
        return readLock;
    }

    @Override
    public Lock writeLock() {
        return writeLock;
    }

    class SimpleReadLock implements ReadLock {
        @Override
        public void lock() {
            synchronized (lock) {
                try {
                    while (writer) {
                        lock.wait();
                    }
                    readers++;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void unlock() {
            synchronized (lock) {
                readers--;
                if (readers == 0)
                    lock.notifyAll();
            }
        }

        @Override
        public void lockInterruptibly() {

        }

        @Override
        public boolean tryLock() {
            return false;
        }

        @Override
        public boolean tryLock(long time, TimeUnit unit) {
            return false;
        }

        @Override
        public Condition newCondition() {
            return null;
        }
    }

    protected class SimpleWriteLock implements WriteLock {
        @Override
        public void lock() {
            synchronized (lock) {
                try {
                    while (readers > 0 || writer) {
                        lock.wait();
                    }
                    writer = true;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void unlock() {
            synchronized (lock) {
                writer = false;
                lock.notifyAll();
            }
        }

        @Override
        public Condition newCondition() {
            return null;
        }

        @Override
        public void lockInterruptibly() {

        }

        @Override
        public boolean tryLock() {
            return false;
        }

        @Override
        public boolean tryLock(long time, TimeUnit unit) {
            return false;
        }
    }
}