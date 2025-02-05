package com.storedobject.ui.iot;

import com.storedobject.core.DateUtility;
import com.storedobject.core.Id;
import com.storedobject.iot.*;
import com.storedobject.ui.Application;
import com.storedobject.ui.TemplateView;
import com.storedobject.ui.Transactional;
import com.storedobject.vaadin.View;
import com.vaadin.flow.shared.Registration;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

public class BlockView extends TemplateView implements Transactional {

    private Consumer<Id> refresher;
    private final Registration size;
    private final Application application;
    private final List<Unit> units = new ArrayList<>();
    private Block block;
    protected String lastUpdateTime = "UNKNOWN";

    public BlockView() {
        super();
        setCaption("Block View");
        Application.get().closeMenu();
        application = Application.get();
        application.getContentWidth();
        size = application.addContentResizedListener((w, h) -> reload());
    }

    @Override
    public void clean() {
        size.remove();
        if(refresher != null) {
            DataSet.unregister(refresher);
            refresher = null;
        }
        application.stopPolling(this);
        super.clean();
    }

    @Override
    public void execute(View lock) {
        if(refresher == null) {
            updateTime();
            loadBlockData();
            application.setPollInterval(this, 30000);
            refresher = this::refreshStatus;
            DataSet.register(refresher);
        }
        if(block == null) {
            loadBlocks();
        }
        super.execute(lock);
    }

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        if(block == null || (this.block != null && this.block.getId().equals(block.getId()))) {
            return;
        }
        this.block = block;
        units.clear();
        block.listUnits().collectAll(units);
        reload();
    }

    public void reload() {
        updateTime();
        if(block != null) {
            buildTree();
        }
    }

    private void loadBlocks() {
    }

    private void loadBlockData() {
    }

    private void refreshStatus(Id blockId) {
        if(block == null || blockId == null || !blockId.equals(this.block.getId())) {
            return;
        }
        reload();
    }

    private void updateTime() {
        Date date = new Date();
        date.setTime(DataSet.getTime());
        lastUpdateTime = DateUtility.formatWithTimeHHMM(application.getTransactionManager().date(date));
    }

    private void buildTree() {
        Id siteId = block.getSiteId();
        List<Unit> unprocessed = new ArrayList<>(units);
        DataSet.getSites().stream().filter(s -> s.getSite().getId().equals(siteId))
                .forEach(s -> buildBranches(s, unprocessed));
    }

    private void buildBranches(DataSet.AbstractData parent, List<Unit> unprocessed) {
        parent.children().forEach(row -> {
            if(row instanceof DataSet.UnitData ud) {
                if(units.contains(ud.getUnit())) {
                    ud.getDataStatus().forEach(ds -> process(ds, unprocessed));
                }
            }
            buildBranches(row, unprocessed);
        });
    }

    private void process(DataSet.DataStatus<?> ds, List<Unit> unprocessed) {
        Unit unit = ds.getUnit();
        if(unprocessed.contains(unit)) {
            unprocessed.remove(unit);
            paint(unit);
        }
        if(ds instanceof DataSet.LimitStatus ls) {
            paint(ls);
        } else if(ds instanceof DataSet.AlarmStatus as) {
            paint(as);
        }
    }

    protected void paint(Unit unit) {
    }

    protected void paint(DataSet.LimitStatus limitStatus) {
    }

    protected void paint(DataSet.AlarmStatus alarmStatus) {
    }
}
