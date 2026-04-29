package com.storedobject.ui;

import com.storedobject.vaadin.Clickable;
import com.vaadin.flow.component.ClickNotifier;
import com.vaadin.flow.component.Component;

import java.util.concurrent.locks.ReentrantLock;

/**
 * A thread-safe utility class for managing click events with concurrent control.
 * This class ensures that only one click event is processed at a time by acquiring
 * and releasing a lock during the event execution.
 *
 * @author Syam
 */
public final class ConcurrentClick {

    private final ReentrantLock lock = new ReentrantLock();

    /**
     * Registers a click event listener for the given component. If the component is an instance
     * of {@code ClickNotifier}, the provided {@code Runnable} is executed when the component is
     * clicked. For other components, a wrapper ensures the given {@code Runnable} is executed
     * through the click pipeline.
     *
     * @param c the component to register the click event listener for
     * @param runnable the action to execute when the component is clicked
     */
    public void registerClick(Component c, Runnable runnable) {
        if(c instanceof ClickNotifier<?>) {
            ((ClickNotifier<?>) c).addClickListener(e -> clicked(runnable));
        } else {
            Runnable r = () -> clicked(runnable); // Run it through the click pipeline.
            new Clickable<>(c, t -> r.run());
        }
    }

    private void clicked(Runnable runnable) {
        if(!lock.tryLock()) {
            return; // Only one click at a time. So, ignore this one.
        }
        try {
            Application a = Application.get();
            a.access(runnable::run); // Run it after acquiring the application lock.
        } finally {
            lock.unlock();
        }
    }
}
