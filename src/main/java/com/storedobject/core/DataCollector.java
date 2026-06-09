package com.storedobject.core;

import java.io.Closeable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

/**
 * A utility class responsible for periodically collecting and analyzing data,
 * and notifying a {@code DataDistributor} of changes or activity.
 * <p>
 * The {@code DataCollector} runs a scheduled task that executes at a fixed interval.
 * This task evaluates whether the data has changed using a provided {@code Predicate}.
 * If a change is detected, the {@code dataUpdated} method of the {@code DataDistributor}
 * is invoked. Otherwise, the {@code pingReceived} method of the {@code DataDistributor}
 * is called to indicate that the periodic check occurred.
 * </p><p>
 * The frequency of data collection can be configured through the constructor.
 * If the frequency is less than or equal to zero, a default interval of 5 seconds is used.
 * </p>
 *
 * @param <D> The type of data being managed by the {@code DataCollector}.
 * @author Syam
 */
public class DataCollector<D> implements Closeable {

    private final ScheduledExecutorService scheduler;

    /**
     * Constructs a new {@code DataCollector} instance that periodically evaluates data changes
     * and notifies the {@code DataDistributor} of updates or activity.
     *
     * @param collector a {@code Predicate} used to determine if the data has changed. The predicate
     *                  should return {@code true} if the data is updated, or {@code false} otherwise.
     * @param distributor the {@code DataDistributor} which provides the data to be evaluated and
     *                    handles notifications for updates or periodic activity.
     * @param collectionFrequency the frequency, in seconds, at which the data collection and
     *                            evaluation task should run. If the value is less than or equal
     *                            to zero, a default interval of 5 seconds is used.
     */
    public DataCollector(Predicate<D> collector, DataDistributor<D> distributor, int collectionFrequency) {
        scheduler = Executors.newSingleThreadScheduledExecutor(r -> Thread.ofVirtual().name("DataCollector").unstarted(r));
        scheduler.scheduleAtFixedRate(() -> {
            boolean dataChanged = collector.test(distributor.getData());
            if(dataChanged) {
                distributor.dataUpdated();
            } else {
                distributor.pingReceived(System.currentTimeMillis());
            }
        }, 3000, (collectionFrequency <= 0 ? 5 : collectionFrequency) * 1000L, TimeUnit.MILLISECONDS);
    }

    /**
     * Closes the {@code DataCollector} and shuts down the associated scheduled task execution.
     * <p>
     * This method initiates a graceful shutdown of the scheduler, allowing any currently
     * executing tasks to complete within a timeout period of 5 seconds. If tasks do not
     * terminate within this timeframe, the scheduler is forcibly shut down. Interruptions
     * during this process are handled appropriately, and the thread's interrupt status is
     * restored.
     * </p>
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
}
