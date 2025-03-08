package com.storedobject.ui.iot;

import com.storedobject.common.FilterProvider;
import com.storedobject.iot.Block;
import com.storedobject.iot.Site;
import com.storedobject.ui.ObjectComboField;

public class BlockComboField extends ObjectComboField<Block> {

    private final Filter filter = new Filter();

    public BlockComboField() {
        this(null);
    }

    public BlockComboField(String label) {
        super(label, Block.class);
        setFixedFilter(filter, true);
    }

    public void setSite(Site site) {
        if(site == null || (filter.site != site && site.equals(filter.site))) {
            return;
        }
        filter.site = site;
        applyFilter();
    }

    public Site getSite() {
        return filter.site;
    }

    public Block getBlock() {
        return getValue();
    }

    public void setBlock(Block block) {
        setValue(block);
    }

    private static class Filter implements FilterProvider {

        private Site site;

        @Override
        public String getFilterCondition() {
            return "Active" + (site == null ? "" : (" AND Site = " + site.getId()));
        }
    }
}
