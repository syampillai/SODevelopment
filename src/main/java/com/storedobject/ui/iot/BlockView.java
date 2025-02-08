package com.storedobject.ui.iot;

import com.storedobject.core.DateUtility;
import com.storedobject.core.Id;
import com.storedobject.core.StoredObject;
import com.storedobject.iot.*;
import com.storedobject.ui.Application;
import com.storedobject.ui.TemplateView;
import com.storedobject.ui.Transactional;
import com.storedobject.vaadin.CloseableView;
import com.storedobject.vaadin.View;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.shared.Registration;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

public class BlockView extends TemplateView implements Transactional, CloseableView {

    private Consumer<Id> refresher;
    private final Registration size;
    private final Application application;
    private final List<Unit> units = new ArrayList<>();
    private Block block;
    private Date lastUpdateTime;

    @com.vaadin.flow.component.template.Id("block")
    private BlockComboField blockField;

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
            application.setPollInterval(this, 30000);
            refresher = this::refreshStatus;
            DataSet.register(refresher);
        }
        if(block == null) {
            findBlock();
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
        if(isCreated()) {
            paint(block);
        }
        reload();
    }

    public void setSite(Site site) {
        if(blockField == null || site == null || (this.block != null && this.block.getSiteId().equals(site.getId()))) {
            return;
        }
        blockField.setSite(site);
        reload();
    }

    public Site getSite() {
        if(block != null) {
            return block.getSite();
        }
        return blockField == null ? null : blockField.getSite();
    }

    public void reload() {
        updateTime();
        if(block != null) {
            buildTree();
        }
    }

    private void updateTime() {
        lastUpdateTime = application.getTransactionManager().date(new Date());
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public String getLastUpdate() {
        if(lastUpdateTime == null) {
            return "UNKNOWN";
        }
        return DateUtility.formatWithTimeHHMM(lastUpdateTime);
    }

    private void findBlock() {
        Block block = this.block;
        if(block != null) {
            setBlock(block);
            return;
        }
        if(blockField != null) {
            block = blockField.getBlock();
        }
        if(block != null) {
            setBlock(block);
            return;
        }
        block = StoredObject.get(Block.class, "Active");
        if(block != null) {
            if(blockField != null) {
                blockField.setValue(block);
            } else {
                setBlock(block);
            }
        }
    }

    private void refreshStatus(Id blockId) {
        if(block == null || blockId == null || !blockId.equals(this.block.getId())) {
            return;
        }
        reload();
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
        if(!isCreated()) {
            return;
        }
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

    protected void paint(Block block) {
    }

    protected void paint(Unit unit) {
    }

    protected void paint(DataSet.LimitStatus limitStatus) {
    }

    protected void paint(DataSet.AlarmStatus alarmStatus) {
    }

    @Override
    protected Component createComponentForId(String id) {
        if("block".equals(id)) {
            BlockComboField blockField = new BlockComboField();
            blockField.addValueChangeListener(e -> setBlock(e.getValue()));
            return blockField;
        }
        return super.createComponentForId(id);
    }
}
