package org.example.service.fixedwindow;

public class Counter {
    private long windowInterval;

    private int count;

    public Counter(long now, int count) {
        this.windowInterval = now;
        this.count = count;
    }

    public long getWindowInterval() {
        return windowInterval;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int c) {
        this.count = c;
    }

    public void setWindowInterval(long now) {
        this.windowInterval =now;
    }
}
