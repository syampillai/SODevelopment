package com.storedobject.ui;

import com.storedobject.common.Executable;
import com.storedobject.core.JavaClassLoader;
import com.storedobject.vaadin.CenteredLayout;
import com.storedobject.vaadin.Viewer;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Presentation runner to present multiple UI logic in sequence. Each UI logic will stay for the specified time.
 *
 * @author Syam
 */
public abstract class PresentationRunner implements Executable {

    private final Application application;
    private final int refresh;
    private final Timer refreshTimer = new Timer();
    private final ArrayList<Screen> screens = new ArrayList<>();
    private int index = -1;

    /**
     * Constructor with default (10 seconds) refresh rate.
     */
    public PresentationRunner() {
        this(0);
    }

    /**
     * Constructor.
     *
     * @param refresh Data refresh rate in seconds.
     */
    public PresentationRunner(int refresh) {
        this.refresh = (refresh <= 0 ? 10 : refresh) * 1000;
        this.application = Application.get();
    }

    @Override
    public void execute() {
        this.refreshTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                refresh();
            }
        }, refresh, refresh);
        next();
    }

    private void next() {
        int n = screens.size();
        if(n == 0) {
            refreshTimer.cancel();
            return;
        }
        if(++index >= n) {
            index = 0;
        }
        screens.get(index).execute();
    }

    private void refresh() {
        screens.forEach(s -> s.refresh = true);
    }

    /**
     * Add a new UI logic to the presentation.
     *
     * @param uiLogic Class name of the UI logic to be added. (For example: "com.storedobject.ui.inventory.LocateItem).
     *                If the logic is an instance of {@link HasRefresh}, its refresh() method will be invoked at the
     *                refresh rate specified.
     * @param time Time duration for which this logic should stay on the screen.
     */
    public void addScreen(String uiLogic, int time) {
        screens.add(new Screen(uiLogic, time));
    }

    private class Screen {

        private final int time;
        private Runnable view;
        private final String logic;
        private boolean refresh;

        private Screen(String logic, int time) {
            this.time = (time <= 0 ? 10 : time) * 1000;
            this.logic = logic;
        }

        private void execute() {
            if(view == null) {
                refresh = false;
                try {
                    view = (Runnable) JavaClassLoader.getLogic(logic).getConstructor().newInstance();
                } catch(Throwable e) {
                    CenteredLayout layout = new CenteredLayout(new ELabel("Unable to create " + logic));
                    view = new Viewer(layout, "Error", false);
                }
            }
            if(refresh) {
                refresh =false;
                if(view instanceof HasRefresh r) {
                    application.access(r::refresh);
                }
            }
            new ScreenRunner(this);
        }
    }

    private class ScreenRunner extends TimerTask {

        private final Timer timer;

        private ScreenRunner(Screen screen) {
            this.timer = new Timer();
            timer.schedule(this, screen.time);
            application.access(() -> {
                screen.view.run();
                application.closeMenu();
            });
        }

        @Override
        public void run() {
            timer.cancel();
            next();
        }
    }
}
