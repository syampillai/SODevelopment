package com.storedobject.ui.inventory;

import com.storedobject.common.SORuntimeException;
import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.ui.HTMLText;
import com.storedobject.ui.Transactional;
import com.storedobject.vaadin.ChoiceField;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.View;

/**
 * Receive material from another location.
 *
 * @author Syam
 */
public class ReceiveMaterial extends DataForm implements Transactional {

    private static final String[] TYPE_4_STORES = new String[] {
            "GRN - Purchases",
            "Returned from Other Locations",
            "Transferred from Other Stores",
            "Transferred from Other Stores (Against Requests)",
            "Returned from Repair/Maintenance Organizations",
            "Lease/Loan/Rental Returns",
            "GRN - From External Owners",
            "Tools Returned",
            "GRN - Loan from Lender",
    };
    private static final String[] TYPE_4_LOCATIONS = new String[] {
            "Issued from Stores",
            "Transferred from Stores",
    };
    private boolean valuesSet = false;
    private final LocationField toField;
    private final ChoiceField typeFieldForStores = new ChoiceField("Type", TYPE_4_STORES);
    private final ChoiceField typeFieldForLocations = new ChoiceField("Type", TYPE_4_LOCATIONS);

    /**
     * Constructor.
     */
    public ReceiveMaterial() {
        this(null, null, -1);
    }

    /**
     * Constructor.
     *
     * @param locationName Name of the location.
     */
    public ReceiveMaterial(String locationName) {
        this(ParameterParser.location(locationName, true, 0, 4, 5, 11),
                ParameterParser.number(locationName));
    }

    /**
     * Constructor.
     *
     * @param to Location to.
     */
    public ReceiveMaterial(InventoryLocation to) {
        this(to, -1);
    }

    /**
     * Constructor.
     *
     * @param to Location to.
     * @param type Type of receipt.
     */
    public ReceiveMaterial(InventoryLocation to, int type) {
        this(null, to, type);
    }

    private ReceiveMaterial(LocationField toField, InventoryLocation to, int type) {
        super("Receive Materials/Tools");
        if(toField == null) {
            if(to == null) {
                this.toField = LocationField.create("x", 0, 4, 5, 10, 11);
                this.toField.setValue((Id)null);
            } else {
                this.toField = LocationField.create("x", to);
                setFieldReadOnly(this.toField);
            }
            this.toField.setLabel("Receive to");
        } else {
            this.toField = toField;
            if(to != null) {
                this.toField.setValue(to);
                setFieldReadOnly(this.toField);
            }
        }
        addField(this.toField, typeFieldForStores, typeFieldForLocations);
        setRequired(this.toField);
        toChanged(this.toField.getValue());
        this.toField.addValueChangeListener(e -> toChanged(e.getValue()));
        if(type >= 0 && to != null) {
            if(to instanceof InventoryStoreBin) {
                if(type < TYPE_4_STORES.length) {
                    typeFieldForStores.setValue(type);
                    typeFieldForStores.setReadOnly(true);
                    valuesSet = true;
                } else {
                    showTypeError(type, TYPE_4_STORES);
                }
            } else {
                if(type < TYPE_4_LOCATIONS.length) {
                    typeFieldForLocations.setValue(type);
                    typeFieldForLocations.setReadOnly(true);
                    valuesSet = true;
                } else {
                    showTypeError(type, TYPE_4_LOCATIONS);
                }
            }
        }
    }

    private void showTypeError(int type, String[] values) {
        HTMLText h = new HTMLText("Incorrect type value: " + type);
        h.newLine().append("Please set one of the following values.");
        for(int i = 0; i < values.length; i++) {
            h.newLine().append(i + ": " + values[i]);
        }
        throw new SORuntimeException(h.getHTML());
    }

    private void toChanged(InventoryLocation location) {
        boolean isStore = location instanceof InventoryStoreBin;
        typeFieldForStores.setVisible(isStore);
        typeFieldForLocations.setVisible(!isStore);
    }

    @Override
    protected void execute(View parent, boolean doNotLock) {
        if(valuesSet) {
            process();
            return;
        }
        super.execute(parent, doNotLock);
    }

    @Override
    protected boolean process() {
        InventoryLocation to = toField.getValue();
        if(to == null) {
            return false;
        }
        close();
        if(to instanceof InventoryStoreBin) {
            switch(typeFieldForStores.getValue()) {
                case 0 -> grn(0, to);
                case 1 -> new ReceiveMaterialReturned(to).execute();
                case 2 -> new ReceiveMaterialTransferred(to).execute();
                case 3 -> new ReceiveMaterialRequested(to).execute();
                case 4 -> new ReceiveReturnedItems(3, (InventoryStoreBin) to, null, false).execute();
                case 5 -> new ReceiveReturnedItems(8, (InventoryStoreBin) to, null, false).execute();
                case 6 -> grn(1, to);
                case 7 -> new ReceiveReturnedItems(18, (InventoryStoreBin) to, null, false).execute();
                case 8 -> grn(2, to);
            }
        } else {
            switch(typeFieldForLocations.getValue()) {
                case 0 -> new ReceiveMaterialRequested(to).execute();
                case 1 -> new ReceiveMaterialTransferred(to).execute();
            }
        }
        return true;
    }

    private void grn(int type, InventoryLocation to) {
        new GRNSourceType(type, to).execute();
    }

    private void grn(int type, InventoryLocation to, int category) {
        GRN grn = new GRN(type, ((InventoryStoreBin) to).getStore());
        try {
            String label = grnSourceLabel(type);
            String prefix = grnSourcePrefix(type);
            String property = prefix + "-BROWSER-LOGIC-" + category;
            Class<?> browserClass = JavaClassLoader.createClassFromProperty(property);
            if(browserClass == null) {
                m(label, property);
            } else {
                property = prefix + "-CLASS-" + category;
                Class<?> dataClass = JavaClassLoader.createClassFromProperty(property);
                if(dataClass == null) {
                    String className = browserClass.getName().replace(".logic.", ".").replace("Browser", "");
                    try {
                        dataClass = JavaClassLoader.getLogic(className);
                    } catch (ClassNotFoundException ignored) {
                    }
                }
                if(dataClass == null) {
                    m(label, property);
                } else {
                    //noinspection unchecked
                    grn.setSource(label, browserClass, (Class<? extends StoredObject>) dataClass);
                }
            }
        } catch(SOException e) {
            log(e);
        } finally {
            grn.execute();
        }
    }

    private void m(String label, String property) {
        warning("Unable to determine the class for '" + label + "'. Please contact technical support to set global property: " + property);
    }

    private static String grnSourcePrefix(int type) {
        return switch(type) {
            case 0 -> "PO";
            case 1 -> "FROM-EXTERNAL-OWNERS";
            case 2 -> "LOANS-IN";
            default -> "UNKNOWN";
        };
    }

    private static String grnSourceLabel(int type) {
        return switch(type) {
            case 0 -> "POs";
            case 1 -> "From External Owners";
            case 2 -> "Loans";
            default -> "GRN Source";
        };
    }

    private class GRNSourceType extends DataForm {

        private final int type;
        private final InventoryLocation to;
        private final ChoiceField choice;
        private final int choiceCount;

        public GRNSourceType(int type, InventoryLocation location) {
            super(grnSourceLabel(type) + " - Choose Type");
            this.type = type;
            to = location;
            StringList choices = StringList.create(
                    ApplicationServer.getGlobalProperty(grnSourcePrefix(type) + "-TYPES", "None", true).
                    replace('\n', ','));
            choiceCount = choices.size();
            choice = new ChoiceField("Type", choices);
            addField(choice);
            choice.setValue(0);
        }

        @Override
        protected void execute(View parent, boolean doNotLock) {
            if(choiceCount == 1) {
                process();
                return;
            }
            super.execute(parent, doNotLock);
        }

        @Override
        protected boolean process() {
            close();
            grn(type, to, choice.getValue());
            return true;
        }
    }
}
