package com.storedobject.core;

import com.storedobject.common.SORuntimeException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class ReferencePattern<O extends StoredObject> {

    private static final Set<ReferencePattern<?>> patterns = new HashSet<>();
    private final Map<String, String> serialPattern = new HashMap<>();
    private RP<O> rp; // Cache

    public ReferencePattern() {
        synchronized (patterns) {
            patterns.add(this);
        }
    }

    private RP<O> rp(StoredObject so) {
        if(rp != null && rp.objectClass == so.getClass()) {
            return rp;
        }
        synchronized (patterns) {
            //noinspection unchecked
            RP<O> rpo = (RP<O>) RP.get(so);
            if(rp == null) {
                rp = rpo;
            }
            return rpo;
        }
    }

    public String  getTag(O so) {
        return rp(so).getTag().apply(so) + suffix(so);
    }

    private Function<O, String> patternTag(StoredObject so) {
        return rp(so).getPatternTag();
    }

    private String suffix(StoredObject so) {
        if(so instanceof HasReference hr) {
            String s = hr.getTagSuffix();
            return s == null ? "" : ("-" + s);
        }
        return "";
    }

    public String get(StoredObject so) {
        if(!(so instanceof HasReference hr)) {
            return null;
        }
        int no = hr.getNo();
        if(no <= 0) {
            return null;
        }
        rp(so);
        @SuppressWarnings("unchecked") O o = (O) so;
        String tag = hr.getTagPrefix() + patternTag(so).apply(o) + suffix(o);
        String p = serialPattern.get(tag);
        Transaction t = so.getTransaction();
        if(p == null) {
            p = SerialPattern.getPatternFor(tag, hr.getTagPrefix() + "n", t);
            serialPattern.put(tag, p);
        }
        HasReference.Amend<O> a = hr.getAmend();
        String reference = SerialPattern.getNumber(t == null ? hr.getSystemEntity() : t.getManager().getEntity(),
                no, ((HasReference)(a.object())).getDate(), p);
        if(a.amendment() > 0) {
            reference += "/" + a.amendment();
        }
        return reference;
    }

    public static void clear() {
        RP.clear();
        synchronized (patterns) {
            patterns.forEach(rp -> {
                rp.serialPattern.clear();
                rp.rp = null;
            });
        }
    }

    public static void clearPatterns() {
        patterns.forEach(rp -> rp.serialPattern.clear());
    }

    private static class RP<T extends StoredObject> {

        private static final Map<Class<?>, RP<?>> patterns = new HashMap<>();
        private final Class<T> objectClass;
        private Function<T, String> tag, patternTag;
        private final BiFunction<Class<T>, Boolean, Function<T, String>> tagCreator;

        RP(Class<T> objectClass, BiFunction<Class<T>, Boolean, Function<T, String>> tagCreator) {
            this.objectClass = objectClass;
            this.tagCreator = tagCreator;
        }

        static void clear() {
                patterns.clear();
        }

        @SuppressWarnings("unchecked")
        public static <O extends StoredObject> RP<O> get(O object) {
            Class<O> objectClass = (Class<O>) object.getClass();
            RP<O> rp = (RP<O>) patterns.get(objectClass);
            if(rp != null) {
                return rp;
            }
            if(object instanceof HasReference hr && hr.serialTag(0) != null) {
                rp = new RP<>(objectClass, RP::tagSO);
            } else {
                rp = new RP<>(objectClass, tagGenerator(objectClass));
            }
            patterns.put(objectClass, rp);
            return rp;
        }

        public Function<T, String> getTag() {
            if(tag == null) {
                tag = tagCreator.apply(objectClass, Boolean.FALSE);
            }
            return tag;
        }

        public Function<T, String> getPatternTag() {
            if(patternTag == null) {
                patternTag = tagCreator.apply(objectClass, Boolean.TRUE);
            }
            return patternTag;
        }

        @SuppressWarnings("unchecked")
        private static <O extends StoredObject,
                PO extends InventoryPO,
                IT extends InventoryTransfer,
                IS extends InventorySale,
                MR extends MaterialRequest,
                JV extends JournalVoucher,
                C extends Consignment
                >
        BiFunction<Class<O>, Boolean, Function<O, String>> tagGenerator(Class<O> objectClass) {
            if(objectClass == InventoryGRN.class) {
                return (clazz, pattern) -> (Function<O, String>) tagGRN(pattern);
            }
            if(InventoryPO.class.isAssignableFrom(objectClass)) {
                Class<PO> poClass = (Class<PO>) objectClass;
                return (clazz, pattern) -> (Function<O, String>) tagPO(poClass, pattern);
            }
            if(InventorySale.class.isAssignableFrom(objectClass)) {
                Class<IS> isClass = (Class<IS>) objectClass;
                return (clazz, pattern) -> (Function<O, String>) tagIS(isClass, pattern);
            }
            if(InventoryTransfer.class.isAssignableFrom(objectClass)) {
                Class<IT> itClass = (Class<IT>) objectClass;
                return (clazz, pattern) -> (Function<O, String>) tagIT(itClass, pattern);
            }
            if(MaterialRequest.class.isAssignableFrom(objectClass)) {
                Class<MR> mrClass = (Class<MR>) objectClass;
                return (clazz, pattern) -> (Function<O, String>) tagMR(mrClass, pattern);
            }
            if(MaterialIssued.class == objectClass) {
                return (clazz, pattern) -> (Function<O, String>) tagMI(pattern);
            }
            if(Consignment.class.isAssignableFrom(objectClass)) {
                Class<C> conClass = (Class<C>) objectClass;
                return (clazz, pattern) -> (Function<O, String>) tagCON(conClass, pattern);
            }
            if(JournalVoucher.class.isAssignableFrom(objectClass)) {
                Class<JV> jvClass = (Class<JV>) objectClass;
                return (clazz, pattern) -> (Function<O, String>) tagJV(jvClass, pattern);
            }
            if (HasReference.class.isAssignableFrom(objectClass)) {
                return (clazz, pattern) -> (Function<O, String>) tagSO(objectClass, pattern);
            }
            throw new SORuntimeException("Reference Pattern: " + objectClass.getName());
        }

        private static Function<InventoryGRN, String> tagGRN(boolean pattern) {
            SerialConfigurator sc = SerialConfigurator.getFor(InventoryGRN.class);
            return switch(pattern ? sc.getPatternType() : sc.getType()) {
                case 3 -> grn -> grn.storeId + "-" + grn.type;
                case 2 -> grn -> grn.getStore().getSystemEntityId() + "-" + grn.type;
                case 1 -> grn -> String.valueOf(grn.storeId);
                default -> grn -> String.valueOf(grn.getStore().getSystemEntityId());
            };
        }

        private static <PO extends InventoryPO> Function<PO, String> tagPO(Class<PO> poClass, boolean pattern) {
            SerialConfigurator sc = SerialConfigurator.getFor(poClass);
            return switch(pattern ? sc.getPatternType() : sc.getType()) {
                case 7 -> po -> po.storeId + "-" + po.getGRNType() + "-" + po.getType();
                case 6 -> po -> po.storeId + "-" + po.getType();
                case 5 -> po -> po.storeId + "-" + po.getGRNType();
                case 4 -> po -> po.getSystemEntityId() + "-" + po.getGRNType() + "-" + po.getType();
                case 3 -> po -> po.getSystemEntityId() + "-" + po.getType();
                case 2 -> po -> po.getSystemEntityId() + "-" + po.getGRNType();
                case 1 -> po -> String.valueOf(po.storeId);
                default -> po -> String.valueOf(po.getSystemEntityId());
            };
        }

        private static <IS extends InventorySale> Function<IS, String> tagIS(Class<IS> isClass, boolean pattern) {
            SerialConfigurator sc = SerialConfigurator.getFor(isClass);
            return switch(pattern ? sc.getPatternType() : sc.getType()) {
                case 3 -> is -> is.fromLocationId + "-" + is.getType();
                case 2 -> is -> String.valueOf(is.getType());
                case 1 -> is -> String.valueOf(is.fromLocationId);
                default -> is -> String.valueOf(is.systemEntityId);
            };
        }

        private static <IT extends InventoryTransfer> Function<IT, String> tagIT(Class<IT> itClass, boolean pattern) {
            SerialConfigurator sc = SerialConfigurator.getFor(itClass);
            if((pattern ? sc.getPatternType() : sc.getType()) == 1) {
                return o -> String.valueOf(o.fromLocationId);
            }
            return o -> String.valueOf(o.systemEntityId);
        }

        private static <MR extends MaterialRequest> Function<MR, String> tagMR(Class<MR> mrClass, boolean pattern) {
            SerialConfigurator sc = SerialConfigurator.getFor(mrClass);
            switch(pattern ? sc.getPatternType() : sc.getType()) {
                case 0 -> {
                    return o -> String.valueOf(o.systemEntityId);
                }
                case 1 -> {
                    return o -> String.valueOf(o.fromLocationId);
                }
                case 2 -> {
                    return o -> String.valueOf(o.family());
                }
                case 3 -> {
                    return o -> o.systemEntityId + "-" + o.family();
                }
                case 4 -> {
                    return o -> o.fromLocationId + "-" + o.family();
                }
                case 5 -> {
                    return o -> o.systemEntityId + "-" + o.getType();
                }
                case 6 -> {
                    return o -> o.fromLocationId + "-" + o.getType();
                }
                case 7 -> {
                    return o -> o.family() + "-" + o.getType();
                }
                case 8 -> {
                    return o -> o.systemEntityId + "-" + o.family() + "-" + o.getType();
                }
                case 9 -> {
                    return o -> o.fromLocationId + "-" + o.family() + "-" + o.getType();
                }
                default -> {
                    return null;
                }
            }
        }

        private static Function<MaterialIssued, String> tagMI(boolean pattern) {
            SerialConfigurator sc = SerialConfigurator.getFor(MaterialIssued.class);
            if((pattern ? sc.getPatternType() : sc.getType()) == 1) {
                return o -> String.valueOf(o.getLocationId());
            }
            return o -> String.valueOf(o.getSystemEntityId());
        }

        private static <C extends Consignment> Function<C, String> tagCON(Class<C> conClass, boolean pattern) {
            SerialConfigurator sc = SerialConfigurator.getFor(conClass);
            return switch(pattern ? sc.getPatternType() : sc.getType()) {
                case 3 -> c -> c.loc() + "-" + c.getType();
                case 2 -> c -> String.valueOf(c.loc());
                case 1 -> c -> String.valueOf(c.orgId());
                default -> c -> String.valueOf(c.getType());
            };
        }

        private static <JV extends JournalVoucher> Function<JV, String> tagJV(Class<JV> jvClass, boolean pattern) {
            SerialConfigurator sc = SerialConfigurator.getFor(jvClass);
            if((pattern ? sc.getPatternType() : sc.getType()) == 1) {
                return o -> "0";
            }
            return o -> String.valueOf(o.getSystemEntityId());
        }

        private static <O extends StoredObject> Function<O, String> tagSO(Class<O> hrClass, boolean pattern) {
            SerialConfigurator sc = SerialConfigurator.getFor(hrClass);
            int index = pattern ? sc.getPatternType() : sc.getType();
            return hr -> ((HasReference)hr).serialTag(index);
        }
    }
}
