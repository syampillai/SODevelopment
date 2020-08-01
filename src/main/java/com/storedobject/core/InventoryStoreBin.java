package com.storedobject.core;

/**
 * <p>The whole "inventory store" can be considered as on "huge" bin and this class represents that. For every
 * store, the system automatically generates an instance of this bin.</p>
 * <p>When an item is received in a store, it gets into this "bin" and only after inspection, it gets moved to
 * a proper "bin" within the store. So, an item located at this "bin" is not really "binned".</p>
 *
 * @author Syam
 */
public final class InventoryStoreBin extends InventoryBin {

    /**
     * Constructor.
     */
    public InventoryStoreBin() {
    }

    public static void columns(Columns columns) {
    }

    public static void indices(Indices indices) {
        indices.add("Store, T_Family",true);
    }

    public static String[] displayColumns() {
        return new String[] { "Name" };
    }

    public static String[] searchColumns() {
        return new String[] { "Name as Bin" };
    }

    public static String[] browseColumns() {
        return new String[] { "Name as Bin" };
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        super.validateData(tm);
        setName(getStore().getName());
        setParentLocation(Id.ZERO);
    }

    @Override
    public void validateDelete() throws Exception {
        super.validateDelete();
        if(!getStore().deleted()) {
            throw new Invalid_State("Deletion not allowed");
        }
    }

    /**
     * This method always returns <code>true</code>. (Means, anything can be stocked at this level).
     *
     * @param partNumber Item type to check.
     * @return True.
     */
    @Override
    public boolean canStore(InventoryItemType partNumber) {
        return true;
    }
}
