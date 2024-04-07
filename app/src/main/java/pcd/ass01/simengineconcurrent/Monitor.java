package pcd.ass01.simengineconcurrent;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Monitor{
    private int readers = 0;
    private int writers = 0;
    private int activeWriters = 0;

    private Lock lock = new ReentrantLock();
    private Condition noWriters = lock.newCondition();
    private Condition noActiveWriters = lock.newCondition();

    public void startRead() throws InterruptedException {
        lock.lock();
        try {
            while (writers > 0 || activeWriters > 0) {
                noWriters.await();
            }
            readers++;
        } finally {
            lock.unlock();
        }
    }

    public void endRead() {
        lock.lock();
        try {
            readers--;
            if (readers == 0) {
                noActiveWriters.signal();
            }
        } finally {
            lock.unlock();
        }
    }

    public void startWrite() throws InterruptedException {
        lock.lock();
        try {
            writers++;
            while (readers > 0 || activeWriters > 0) {
                noActiveWriters.await();
            }
            writers--;
            activeWriters++;
        } finally {
            lock.unlock();
        }
    }

    public void endWrite() {
        lock.lock();
        try {
            activeWriters--;
            if (activeWriters == 0) {
                noWriters.signalAll();
            }
        } finally {
            lock.unlock();
        }
    }
}
