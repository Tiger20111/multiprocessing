package readwritelock.interfaces;

import java.util.concurrent.locks.Lock;

public interface ReadWriteLock extends java.util.concurrent.locks.ReadWriteLock {
    Lock readLock();
    Lock writeLock();
}
