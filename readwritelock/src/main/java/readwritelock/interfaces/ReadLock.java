package readwritelock.interfaces;

import java.util.concurrent.locks.Lock;

public interface ReadLock extends Lock {
    @Override
    void lock();

    @Override
    void unlock();
}
