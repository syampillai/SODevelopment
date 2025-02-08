package com.storedobject.ui.iot;

import com.storedobject.iot.Block;
import com.storedobject.iot.Site;
import com.storedobject.ui.ObjectComboField;
import com.storedobject.ui.Transactional;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.View;

public abstract class BlockSelector extends DataForm implements Transactional {

    private final BlockComboField blockField = new BlockComboField("Building/Block");
    private final ObjectComboField<Site> siteField = new ObjectComboField<>("Site", Site.class);
    private Block block;

    public BlockSelector(String caption) {
        this(caption, null);
    }

    public BlockSelector(String caption, Block block) {
        super(caption);
        siteField.setLoadFilter(Site::getActive);
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
        if(GUI.isFixedSite()) {
            setFieldReadOnly(siteField);
        }
        if(GUI.site() != null) {
            siteField.setValue(GUI.site());
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
