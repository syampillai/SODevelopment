package com.storedobject.ui;

import com.storedobject.common.Executable;
import com.storedobject.core.JavaClassLoader;
import com.storedobject.vaadin.CenteredLayout;
import com.storedobject.vaadin.TimerComponent;
import com.storedobject.vaadin.View;
import com.storedobject.vaadin.Viewer;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

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
    private long time;

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
        application.closeMenu();
        time = System.currentTimeMillis();
        this.refreshTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                refresh();
            }
        }, refresh, refresh);
        next();
    }

    private void next() {
        if((System.currentTimeMillis() - time) > 60000L) {
            time = System.currentTimeMillis();
            application.access(() -> new Monitor().execute());
            return;
        }
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

    private void previous() {
        --index;
        next();
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
        private String logicName, logic;
        private Class<? extends Runnable> logicClass;
        private boolean refresh;

        private Screen(String logic, int time) {
            this.time = (time <= 0 ? 10 : time) * 1000;
            this.logic = logic;
            logicName = logic;
        }

        private Screen(Class<? extends Runnable> logicClass, int time) {
            this.time = (time <= 0 ? 10 : time) * 1000;
            this.logicClass = logicClass;
            logicName = logicClass.getName();
        }

        private Screen(Runnable view, int time) {
            this.time = (time <= 0 ? 10 : time) * 1000;
            this.view = view;
        }

        @SuppressWarnings("unchecked")
        private void execute() {
            if(view == null && logicClass == null && logic == null) {
                previous();
                return;
            }
            if(view == null) {
                refresh = false;
                try {
                    if(logic != null) {
                        logicClass = (Class<? extends Runnable>) JavaClassLoader.getLogic(logic);
                        logic = null;
                    }
                    if(logicClass != null) {
                        Class<? extends Runnable> lc = logicClass;
                        logicClass = null;
                        application.access(() -> {
                            try {
                                view = lc.getConstructor().newInstance();
                            } catch(Throwable e) {
                                errorView(e);
                            }
                        });
                    }
                    previous();
                    return;
                } catch(Throwable e) {
                    errorView(e);
                }
            }
            if(refresh) {
                refresh = false;
                if(view instanceof HasRefresh r) {
                    application.access(r::refresh);
                }
            }
            new ScreenRunner(this);
        }

        private void errorView(Throwable error) {
            logic = null;
            logicClass = null;
            application.log(error);
            CenteredLayout layout = new CenteredLayout(
                    new ELabel("Unable to create " + logicName,
                            "color:red;font-weight:bold;font-size:2vw")
            );
            view = new Viewer(layout, "Error", false);
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

    private class Monitor extends View {

        private final Timer timer = new Timer();
        private boolean live;
        private final TimerComponent heartbeat = new TimerComponent();

        private Monitor() {
            super("Presentation Monitor");
            heartbeat.setPrefix("Reconnecting ");
            heartbeat.addListener(l -> beat());
            VerticalLayout v = new VerticalLayout(heartbeat);
            setComponent(v);
        }

        @Override
        protected void execute(View parent, boolean doNotLock) {
            live = false;
            super.execute(parent, doNotLock);
            heartbeat.countDown(1);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    notLive();
                }
            }, 10000, 10000);
        }

        private void beat() {
            live = true;
            heartbeat.abort();
            timer.cancel();
            application.access(this::close);
            next();
        }

        @Override
        public void clean() {
            super.clean();
            timer.cancel();
            heartbeat.abort();
        }

        private void notLive() {
            timer.cancel();
            if(live) {
                live = false;
                return;
            }
            application.access(application::close);
        }
    }
}
