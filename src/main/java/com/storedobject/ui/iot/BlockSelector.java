package com.storedobject.ui.iot;

import com.storedobject.iot.Block;
import com.storedobject.iot.Site;
import com.storedobject.ui.ObjectComboField;
import com.storedobject.ui.Transactional;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.View;

public abstract class BlockSelector extends DataForm implements Transactional {

    private final ObjectComboField<Site> siteField = new ObjectComboField<>("Site", Site.class, "Active");
    private final BlockComboField blockField = new BlockComboField("Building/Block");
    private Block block;

    public BlockSelector(String caption) {
        this(caption, null);
    }

    public BlockSelector(String caption, Block block) {
        this(caption, block, null);
    }

    public BlockSelector(String caption, Block block, Site site) {
        super(caption);
        addField(siteField, blockField);
        siteField.addValueChangeListener(e -> {
            Site s = e.getValue();
            blockField.setSite(s);
            siteChanged(s);
        });
        blockField.addValueChangeListener(e -> {
            this.block = blockField.getBlock();
            if(this.block == null) {
                siteField.focus();
            }
            blockChanged(this.block);
        });
        if(site == null && block != null) {
            site = block.getSite();
        }
        if(site == null) {
            site = Site.list(Site.class).single(false);
        }
        if(site != null) {
            siteField.setValue(site);
            setFirstFocus(blockField);
        }
        if(block != null) {
            blockField.setValue(block);
        }
    }

    protected void siteChanged(Site newSite) {
    }

    protected void blockChanged(Block newBlock) {
    }

    public void setBlock(Block block) {
        if(block == null) {
            return;
        }
        Site site = siteField.getObject();
        if(site == null) {
            siteField.setValue(block.getSite());
        }
        blockField.setValue(block);
    }

    public void setSite(Site site) {
        siteField.setValue(site);
        siteField.setReadOnly(site != null);
        if(site != null) {
            setFirstFocus(blockField);
        }
    }

    @Override
    protected void execute(View parent, boolean doNotLock) {
        if(block != null && siteField.isReadOnly()) {
            process();
            return;
        }
        super.execute(parent, doNotLock);
    }

    @Override
    protected boolean process() {
        clearAlerts();
        Block block = blockField.getObject();
        if(block == null) {
            warning("Select a building/block");
            return false;
        }
        try {
            if(!accept(block)) {
                return false;
            }
        } catch (Exception e) {
            error(e);
        }
        return true;
    }

    protected abstract boolean accept(Block block) throws Exception;
}
