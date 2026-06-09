package com.storedobject.core;

import java.io.Closeable;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * A thread-safe utility class responsible for distributing data to registered consumers at a regular interval.
 * The data is distributed using a background scheduled task. Consumers are notified through their {@code accept} method.
 * Any consumer that throws an exception during data distribution is automatically unregistered.
 *
 * @param <D> The type of data being distributed.
 *
 * @author Syam
 */
public class DataDistributor<D> implements Closeable {

    private volatile D data;
    private final List<Consumer<D>> consumers = new CopyOnWriteArrayList<>();
    private final ScheduledExecutorService scheduler;
    private final AtomicLong lastUpdate = new AtomicLong(0), lastPing = new AtomicLong(0);
    private long distributedTime = -1;

    public DataDistributor() {
        this(null, 0);
    }

    public DataDistributor(int refreshRate) {
        this(null, refreshRate);
    }

    /**
     * Constructs a {@code DataDistributor} with the specified data and a default refresh rate.
     * The data is distributed to registered consumers periodically based on the preset interval.
     *
     * @param data The data to be distributed to consumers.
     */
    public DataDistributor(D data) {
        this(data, 0);
    }

    /**
     * Constructs a {@code DataDistributor} with the specified data and refresh rate.
     * The data is distributed to registered consumers periodically based on the refresh rate.
     *
     * @param data The data to be distributed to consumers.
     * @param refreshRate The interval, in seconds, at which data is distributed.
     *                    If the value is less than or equal to 0, a default interval of 5 seconds is used.
     */
    public DataDistributor(D data, int refreshRate) {
        this.data = data;
        scheduler = Executors.newSingleThreadScheduledExecutor(r -> Thread.ofVirtual().name("DataDistributor").unstarted(r));
        scheduler.scheduleAtFixedRate(this::distributeData, 3000, (refreshRate <= 0 ? 5 : refreshRate) * 1000L, TimeUnit.MILLISECONDS);
    }

    /**
     * Sets the data to be distributed by the {@code DataDistributor}.
     * This method updates the internal state of the {@code DataDistributor} to use
     * the specified data during the next distribution cycle.
     *
     * @param data The data to set. This data will be distributed to registered consumers.
     */
    public void setData(D data) {
        this.data = data;
    }

    /**
     * Retrieves the current data being managed by the {@code DataDistributor}.
     *
     * @return The data currently managed and distributed by this instance.
     */
    public D getData() {
        return data;
    }

    /**
     * Closes the {@code DataDistributor}, releasing any resources held by it.
     * This method cancels the timer used for periodic data distribution, stopping
     * any ongoing or scheduled tasks. After calling this method, the instance is
     * no longer usable for distributing data.
     */
    @Override
    public void close() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Registers a new consumer to receive data updates. The registered consumer
     * will be invoked whenever data is distributed by the {@code DataDistributor}.
     *
     * @param consumer The consumer to register. This consumer will process the
     *                 data upon distribution.
     */
    public void register(Consumer<D> consumer) {
        if (consumer != null && !consumers.contains(consumer)) {
            consumers.add(consumer);
        }
    }

    /**
     * Unregisters a previously registered consumer from receiving data updates.
     * Once unregistered, the consumer will no longer be invoked during data distribution.
     *
     * @param consumer The consumer to unregister. If null, the method does nothing.
     */
    public void unregister(Consumer<D> consumer) {
        if (consumer != null) {
            consumers.remove(consumer);
        }
    }

    /**
     * Updates the last recorded data update time if the current specified time
     * is more recent than the previously recorded update time.
     * This method ensures that the {@code lastUpdate} reflects the most recent
     * time at which a data update occurred, allowing tracking of the update
     * state within the {@code DataDistributor}.
     */
    public void dataUpdated() {
        long now = System.currentTimeMillis();
        lastUpdate.accumulateAndGet(now, Math::max);
    }

    /**
     * Updates the last recorded ping time if the specified time is more recent
     * than the previously recorded ping time. This method ensures that the
     * {@code lastPing} field reflects the most recent time at which a ping was received.
     *
     * @param time The timestamp of the received ping, in milliseconds.
     */
    public void pingReceived(long time) {
        lastPing.accumulateAndGet(time, Math::max);
    }

    /**
     * Get the last time data was updated (GMT).
     *
     * @return Time.
     */
    public long getTime() {
        return lastUpdate.get();
    }

    /**
     * Get the last time a ping was received (GMT).
     *
     * @return Ping-time.
     */
    public long getPingTime() {
        return Math.max(lastPing.get(), lastUpdate.get());
    }

    private void distributeData() {
        long updateTime = lastUpdate.get();
        if (distributedTime >= updateTime) {
            return;
        }
        D d = data;
        if (d == null) {
            return;
        }
        distributedTime = updateTime;
        for (Consumer<D> consumer : consumers) {
            try {
                consumer.accept(d);
            } catch (Throwable e) {
                unregister(consumer);
            }
        }
    }
}
