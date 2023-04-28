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
 * The logic really doesn't have to be UI logic. Instead, you could pass any {@link Runnable}, too.
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

    /**
     * Get the current {@link Application} instance.
     *
     * @return Current {@link Application} instance.
     */
    public Application getApplication() {
        return application;
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
     * @param uiLogic Class name of the UI logic to be added. (For example: "com.storedobject.ui.inventory.LocateItem").
     *                If the logic is an instance of {@link HasRefresh}, its refresh() method will be invoked at the
     *                refresh rate specified. The logic could be just a {@link Runnable} class name.
     * @param time Time duration for which this logic should stay on the screen.
     */
    public void addScreen(String uiLogic, int time) {
        screens.add(new Screen(uiLogic, time));
    }

    /**
     * Add a new UI logic to the presentation.
     *
     * @param uiLogicClass Class of the UI logic to be added.
     *                     (For example: com.storedobject.ui.inventory.LocateItem.class).
     *                If the logic is an instance of {@link HasRefresh}, its refresh() method will be invoked at the
     *                refresh rate specified. The logic class could be any {@link Runnable} class.
     * @param time Time duration for which this logic should stay on the screen.
     */
    public void addScreen(Class<? extends Runnable> uiLogicClass, int time) {
        screens.add(new Screen(uiLogicClass, time));
    }

    /**
     * Add a new UI logic to the presentation.
     *
     * @param logic Any {@link Runnable} logic.
     * @param time Time duration for which this logic should stay on the screen.
     */
    public void addScreen(Runnable logic, int time) {
        screens.add(new Screen(logic, time));
    }

    private class Screen {

        private final int time;
        private Runnable view;
        private final String logic;
        private final Class<? extends Runnable> logicClass;
        private boolean refresh;

        private Screen(String logic, int time) {
            this.time = (time <= 0 ? 10 : time) * 1000;
            this.logic = logic;
            logicClass = null;
        }

        private Screen(Class<? extends Runnable> logicClass, int time) {
            this.time = (time <= 0 ? 10 : time) * 1000;
            this.logicClass = logicClass;
            logic = null;
        }

        private Screen(Runnable view, int time) {
            this.time = (time <= 0 ? 10 : time) * 1000;
            this.view = view;
            this.logicClass = null;
            logic = null;
        }

        private void execute() {
            if(view == null) {
                refresh = false;
                try {
                    if(logicClass != null) {
                        view = logicClass.getConstructor().newInstance();
                    } else {
                        view = (Runnable) JavaClassLoader.getLogic(logic).getConstructor().newInstance();
                    }
                } catch(Throwable e) {
                    CenteredLayout layout = new CenteredLayout(
                            new ELabel("Unable to create " + logic,
                                    "color:red;font-weight:bold;font-size:2vw")
                    );
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
