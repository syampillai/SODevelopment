package com.storedobject.ui.util;

import com.storedobject.common.ArrayListSet;
import com.storedobject.common.Geolocation;
import com.storedobject.common.JSON;
import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.core.converter.DaysValueConverter;
import com.storedobject.core.converter.MinutesValueConverter;
import com.storedobject.core.converter.ValueConverter;
import com.storedobject.ui.*;
import com.storedobject.ui.inventory.UOMField;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.HasValue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

public class SOFieldCreator<T> implements ObjectFieldCreator<T> {

    private ObjectForm<T> form;
    private ClassAttribute<?> ca;
    private Class<T> objectClass;
    private StoredObject master;
    private Id objectId;
    private Map<String, UIFieldMetadata> mds;
    private ArrayList<StoredObjectUtility.Link<?>> links;
    private ArrayList<ContactType> contactTypes;
    private ContactData contactData;
    private boolean extraInfoLoaded = false;
    private ExtraInfo<?> extraInfo;
    private ArrayList<AttachmentDefinition> attachmentDefinitions;
    private StreamAttachmentData attachmentData;

    @Override
    public SOFieldCreator<T> create(ObjectForm<T> form) {
        SOFieldCreator<T> fc = new SOFieldCreator<>();
        fc.form = form;
        fc.setObjectClass(form.getObjectClass());
        fc.mds = new HashMap<>();
        return fc;
    }

    public SOFieldCreator<T> create(Class<T> objectClass) {
        SOFieldCreator<T> fc = new SOFieldCreator<>();
        fc.setObjectClass(objectClass);
        fc.mds = new HashMap<>();
        return fc;
    }

    private void setObjectClass(Class<T> c) {
        if(StoredObject.class.isAssignableFrom(c)) {
            @SuppressWarnings("unchecked") Class<? extends StoredObject> oClass = (Class<? extends StoredObject>) c;
            ca = ClassAttribute.get(oClass);
        }
        this.objectClass = c;
    }

    public Class<T> getObjectClass() {
        return objectClass;
    }

    @Override
    public Stream<Method> getFieldGetMethods() {
        if(ca != null) {
            ArrayList<Method> methods = new ArrayList<>();
            ca.getAttributes().minus(StoredObjectUtility.protectedColumns(ca.getObjectClass())).
                    forEach(n -> methods.add(ca.getMethod(n)));
            if(mds != null) {
                int order = 10000;
                for(Method m : methods) {
                    order = fieldMethod(m, order);
                }
            }
            return methods.stream();
        }
        return ObjectFieldCreator.super.getFieldGetMethods();
    }

    @Override
    public Method getFieldGetMethod(String fieldName) {
        if(ca == null) {
            ObjectFieldCreator.super.getFieldGetMethod(fieldName);
        }
        Method m = ca.getMethod(fieldName);
        if(m != null && mds != null) {
            int order = mds.values().stream().mapToInt(UIFieldMetadata::getFieldOrder).max().orElse(10001);
            fieldMethod(m, order);
        }
        return m;
    }

    private int fieldMethod(Method m, int order) {
        UIFieldMetadata md;
        String name = getFieldName(m);
        md = ca.getFieldMetadata(name);
        if(md != null && !md.isFieldOrderBuiltIn()) {
            md.setFieldOrder(md.getFieldOrder() + order);
            ++order;
        }
        if(md != null) {
            mds.put(name, md);
        }
        return order;
    }

    public StringList getAnchors() {
        return ca == null || form == null ? null : ca.getAnchors();
    }

    private ArrayList<AttachmentDefinition> listADs() {
        if(attachmentDefinitions != null) {
            return attachmentDefinitions;
        }
        attachmentDefinitions = new ArrayList<>();
        if(ca == null || form == null) {
            return attachmentDefinitions;
        }
        StoredObject.list(AttachmentDefinition.class,
                "lower(ClassName)='" + ca.getObjectClass().getName().toLowerCase() + "'",
                "DisplayOrder").collectAll(attachmentDefinitions);
        return attachmentDefinitions;
    }

    private ArrayList<ContactType> listCTs() {
        if(contactTypes != null) {
            return contactTypes;
        }
        contactTypes = new ArrayList<>();
        if(ca == null || form == null) {
            return contactTypes;
        }
        try {
            StoredObject dummy = ca.getObjectClass().getDeclaredConstructor().newInstance();
            if(dummy instanceof HasContacts) {
                ((HasContacts) dummy).listContactTypes().forEach(contactTypes::add);
            }
        } catch(InstantiationException | IllegalAccessException | NoSuchMethodException
                | InvocationTargetException ignored) {
        }
        return contactTypes;
    }

    private ExtraInfo<?> extraInfo() {
        if(extraInfoLoaded) {
            return extraInfo;
        }
        extraInfoLoaded = true;
        extraInfo = null;
        if(ca == null || form == null) {
            return null;
        }
        ExtraInfoDefinition def = StoredObject.get(ExtraInfoDefinition.class, "ClassName='"
                + ca.getObjectClass().getName() + "'");
        if(def != null) {
            extraInfo = new ExtraInfo<>(def);
        }
        return extraInfo;
    }

    @Override
    public Stream<String> getFieldNames() {
        if(ca != null && form != null) {
            links = StoredObjectUtility.linkDetails(ca.getObjectClass());
            StringList protectedColumns = StoredObjectUtility.protectedColumns(ca.getObjectClass());
            links.removeIf(link -> protectedColumns.contains(link.getName() + ".l"));
            Stream<String> names = links.stream().map(link -> link.getName() + ".l");
            listADs().removeIf(ad -> protectedColumns.contains(ad.getName() + ".a"));
            if(!attachmentDefinitions.isEmpty()) {
                names = Stream.concat(names, listADs().stream().map(ad -> ad.getName() + ".a"));
            }
            listCTs().removeIf(ct -> protectedColumns.contains(ct.getName() + ".c"));
            if(!contactTypes.isEmpty()) {
                names = Stream.concat(names, listCTs().stream().map(ct -> ct.getName() + ".c"));
            }
            if(extraInfo() != null) {
                if(protectedColumns.contains(ExtraInfo.getName() + ".e")) {
                    extraInfo = null;
                } else {
                    names = Stream.concat(names, Stream.of(ExtraInfo.getName() + ".e"));
                }
            }
            Optional<StoredObjectUtility.Link<?>> child = links.stream().filter(link -> link.getType() == 0 &&
                    link.getObjectClass() == ca.getObjectClass()).findAny();
            if(child.isPresent()) {
                names = Stream.concat(names, Stream.of(".p"));
            }
            return names;
        }
        return ObjectFieldCreator.super.getFieldNames();
    }

    @Override
    public String getFieldName(Method getMethod) {
        String name = ObjectFieldCreator.super.getFieldName(getMethod);
        if(name != null && name.endsWith("Id") && name.length() > 2) {
            return name.substring(0, name.lastIndexOf("Id"));
        }
        return name;
    }

    @Override
    public int getFieldOrder(String fieldName) {
        if(form == null) {
            return 0;
        }
        if(fieldName.startsWith("_")) {
            return Integer.MAX_VALUE - 100;
        }
        if(fieldName.equals(".p")) {
            return Integer.MIN_VALUE;
        }
        if(fieldName.endsWith(".c")) {
            ContactType ct = ct(fieldName);
            if(ct != null) {
                return (Integer.MAX_VALUE - 1000) + ct.getDisplayOrder();
            }
        }
        if(fieldName.endsWith(".e")) {
            if(extraInfo != null) {
                return (Integer.MAX_VALUE - 1000) + extraInfo.getDisplayOrder();
            }
        }
        if(fieldName.endsWith(".a")) {
            AttachmentDefinition ad = ad(fieldName);
            if(ad != null) {
                return ad.getDisplayOrder();
            }
        }
        if(ca != null && fieldName.endsWith(".l")) {
            fieldName = fieldName.substring(0, fieldName.length() - 2);
            int n = links.size() - 1;
            while(n >= 0) {
                if(links.get(n).getName().equals(fieldName)) {
                    break;
                }
                --n;
            }
            return Integer.MAX_VALUE - (links.size() - n);
        }
        return md(fieldName).getFieldOrder();
    }

    @Override
    public void close() {
        if(mds == null) {
            return;
        }
        mds.clear();
        mds = null;
        ca = null;
        links = null;
        attachmentDefinitions = null;
    }

    private ObjectEditor<?> oe() {
        View v = form.getView();
        return v instanceof ObjectEditor ? (ObjectEditor<?>) v : null;
    }

    @Override
    public Function<T, ?> getValueGetter(String fieldName) {
        if(form == null) {
            return null;
        }
        if(ca != null && fieldName.equals(".p")) {
            return o -> {
                ObjectEditor<?> oe = oe();
                if(oe != null && oe.getParentLinkType() == 0 && oe.getParentObject() != null) {
                    return oe.getParentObject().toDisplay();
                }
                if(o == null) {
                    return "";
                }
                @SuppressWarnings("unchecked") Class<? extends StoredObject> objectClass =
                        (Class<? extends StoredObject>) o.getClass();
                if(master != null) {
                    if(objectId == null || !objectId.equals(o)) {
                        master = null;
                    }
                }
                if(master == null) {
                    master = ((StoredObject) o).getMaster(objectClass);
                    objectId = ((StoredObject) o).getId();
                }
                return master == null ? "" : master.toDisplay();
            };
        }
        if(ca != null && fieldName.endsWith(".a")) {
            AttachmentDefinition ad = ad(fieldName);
            if(ad != null) {
                return o -> createAD((StoredObject) o).getAttachment(fieldName);
            }
        }
        if(ca != null && fieldName.endsWith(".e") && extraInfo != null) {
            ObjectEditor<?> oe = oe();
            if(oe == null) {
                return null;
            }
            oe.setExtraInfo(extraInfo);
            return o -> {
                extraInfo.setMaster((StoredObject) o);
                return extraInfo.getValue();
            };
        }
        if(ca != null && fieldName.endsWith(".c")) {
            ContactType ct = ct(fieldName);
            ObjectEditor<?> oe = oe();
            if(oe == null) {
                return null;
            }
            if(ct != null) {
                return o -> {
                    oe.setContactData(createCD((StoredObject) o));
                    return contactData.getContactValue(ct);
                };
            }
        }
        if(ca == null || !fieldName.endsWith(".l")) {
            return null;
        }
        StoredObjectUtility.Link<?> link = link(fieldName);
        if(link == null) {
            return null;
        }
        return o -> {
            StoredObject so = (StoredObject) o;
            StoredObjectLink<?> soLink = so.objectLink(link.getName(), false);
            return soLink == null ? StoredObjectLink.create(link, so) : soLink;
        };
    }

    private StreamAttachmentData createAD(StoredObject master) {
        if(attachmentData == null) {
            attachmentData = new StreamAttachmentData();
        }
        attachmentData.setMaster(master);
        return attachmentData;
    }

    private ContactData createCD(StoredObject master) {
        if(contactData == null) {
            contactData = new ContactData(contactTypes);
        }
        contactData.setMaster(master);
        return contactData;
    }

    @Override
    public BiConsumer<T, ?> getValueSetter(String fieldName) {
        if(form == null) {
            return null;
        }
        if(ca != null && (fieldName.equals(".p") || fieldName.endsWith(".a"))) {
            return (o, v) -> {
            };
        }
        if(ca != null && fieldName.endsWith(".e")) {
            return (o, v) -> extraInfo.setMaster((StoredObject) o);
        }
        if(ca != null && fieldName.endsWith(".c")) {
            return (o, v) -> {
                createCD((StoredObject) o);
                contactData.setContactValue(ct(fieldName), (String) v);
            };
        }
        if(ca == null || !fieldName.endsWith(".l")) {
            return null;
        }
        StoredObjectUtility.Link<?> link = link(fieldName);
        if(link == null) {
            return null;
        }
        return (o, v) -> {
        };
    }

    private StoredObjectUtility.Link<?> link(String fieldName) {
        return links.stream().filter(l -> fieldName.equals(l.getName() + ".l")).findAny().orElse(null);
    }

    private AttachmentDefinition ad(String fieldName) {
        return listADs().stream().filter(a -> fieldName.equals(a.getName() + ".a")).findAny().orElse(null);
    }

    private ContactType ct(String fieldName) {
        return listCTs().stream().filter(a -> fieldName.equals(a.getName() + ".c")).findAny().orElse(null);
    }

    @Override
    public String getLabel(String fieldName) {
        if(form == null) {
            return null;
        }
        if(fieldName.equals(".p")) {
            return "Previous in the Hierarchy";
        }
        if(fieldName.endsWith(".a")) {
            AttachmentDefinition ad = ad(fieldName);
            if(ad != null) {
                String label = ad.getCaption();
                return label.isEmpty() ? fieldName.substring(0, fieldName.length() - 2) : label;
            }
        }
        if(fieldName.endsWith(".e") && extraInfo != null) {
            return null;
        }
        if(fieldName.endsWith(".c")) {
            ContactType ct = ct(fieldName);
            if(ct != null) {
                return ct.getName();
            }
        }
        String label = md(fieldName).getCaption();
        return StringUtility.isWhite(label) ? ObjectFieldCreator.super.getLabel(fieldName) : label;
    }

    @Override
    public void customizeField(String fieldName, HasValue<?, ?> field) {
        if(field instanceof SOFieldCreator.RO) {
            return;
        }
        UIFieldMetadata md = md(fieldName);
        if(form != null && field != null && md.isRequired()) {
            boolean required = !fieldName.endsWith(".c");
            if(required) {
                if(field instanceof ValueRequired) {
                    ((ValueRequired) field).setRequired(true);
                    required = ((ValueRequired) field).isRequired();
                }
            }
            if(required) {
                form.setRequired(field);
            }
        }
        if(form != null && !md.isSetAllowed()) {
            ObjectEditor<?> oe = oe();
            if(oe != null) {
                if(!isFF(field)) {
                    oe.setSetNotAllowed(fieldName);
                }
            } else if(field != null) {
                field.setReadOnly(true);
            }
        }
    }

    private static boolean isFF(HasValue<?, ?> field) {
        return field instanceof ObjectFormField ||
                (field instanceof ObjectField<?> of && of.getField() instanceof ObjectFormField);
    }

    @Override
    public HasValue<?, ?> createField(String fieldName, Class<?> fieldType, String label) {
        System.err.println("SOF: " + fieldName);
        HasValue<?, ?> f = createFieldX(fieldName, fieldType, label);
        if(f == null) {
            System.err.println("It's NULL");
        }
        return f;
    }

    public HasValue<?, ?> createFieldX(String fieldName, Class<?> fieldType, String label) {
        if(form == null && fieldName.contains(".")) {
            return null;
        }
        HasValue<?, ?> field;
        if(ca != null) {
            if(fieldName.equals(".p")) {
                return new ROTextField(label);
            }
            if(fieldName.endsWith(".a")) {
                AttachmentDefinition ad = ad(fieldName);
                if(ad != null) {
                    attachmentDefinitions.remove(ad);
                    return new AttachmentField(label, createAD(null).addAttachment(ad));
                }
            }
            if(fieldName.endsWith(".e") && extraInfo != null) {
                return new ExtraInfoField<>(extraInfo);
            }
            if(fieldName.endsWith(".c")) {
                ContactType ct = ct(fieldName);
                if(ct != null) {
                    switch(ct.getType()) {
                        case 0:
                            return new PhoneField(label);
                        case 1:
                            return new EmailField(label);
                        case 2:
                            return new AddressField(label);
                        case 3:
                            return new TextField(label);
                    }
                }
            }
            if(fieldName.endsWith(".l")) {
                StoredObjectUtility.Link<?> link = link(fieldName);
                if(link != null) {
                    links.remove(link);
                }
                return new ObjectLinkField<>(label, link);
            }
        }
        field = createSOField(fieldName, fieldType, label);
        if(field == null) {
            field = ObjectFieldCreator.super.createField(fieldName, fieldType, label);
        }
        return field;
    }

    private Method getMethod(String fieldName) {
        try {
            return objectClass.getMethod("get" + fieldName);
        } catch(NoSuchMethodException e1) {
            try {
                Method m = objectClass.getMethod("is" + fieldName);
                return m.getReturnType() == boolean.class ? m : null;
            } catch(NoSuchMethodException e2) {
                return null;
            }
        }
    }

    private UIFieldMetadata md(String fieldName) {
        UIFieldMetadata md = mds.get(fieldName);
        if(md == null) {
            if(ca != null) {
                md = ca.getFieldMetadata(fieldName);
            }
            if(md == null) {
                md = new UIFieldMetadata(fieldName, getMethod(fieldName));
            }
            mds.put(fieldName, md);
        }
        return md;
    }

    public UIFieldMetadata getMD(String fieldName) {
        return mds == null ? null : mds.get(fieldName);
    }

    @SuppressWarnings("unchecked")
    private HasValue<?, ?> createSOField(String fieldName, Class<?> type, String label) {
        Method getMethod = null;
        if(type == null) {
            getMethod = getMethod(fieldName);
            if(getMethod == null) {
                return null;
            }
            type = getMethod.getReturnType();
        }
        UIFieldMetadata md = md(fieldName);
        if(type == ObjectText.class) {
            Method m;
            try {
                m = objectClass.getMethod("get" + fieldName + "Object");
            } catch(NoSuchMethodException error) {
                return null;
            }
            //noinspection rawtypes
            return new ObjectTextField(label, m.getReturnType(), md.isAny());
        }
        if(md.isSerial()) {
            if(type == int.class) {
                return new ROIntegerField(label);
            }
            if(type == long.class) {
                return new ROLongField(label);
            }
        }
        if(type == Id.class) {
            if(getMethod == null) {
                getMethod = getMethod(fieldName);
            }
            if(getMethod == null) {
                return null;
            }
            Class<?> objectType = getMethod.getReturnType();
            if(!StoredObject.class.isAssignableFrom(objectType) || objectType == StoredObject.class) {
                return null;
            }
            fieldName = getFieldName(getMethod);
            if(fieldName == null) {
                return null;
            }
            Class<? extends StoredObject> realObjectType = (Class<? extends StoredObject>) objectType;
            realObjectType = md.getParameterAsClass(realObjectType);
            if(realObjectType == StreamData.class) {
                if(form == null) {
                    return null;
                }
                List<ObjectField.Type> types = objectFieldTypes(md);
                try {
                    String[] mimes = (String[]) objectClass.getMethod("contentTypes", String.class)
                            .invoke(null, new Object[]{fieldName});
                    for(String mt: mimes) {
                        if(mt.contains("image/")) {
                            types.add(ObjectField.Type.IMAGE);
                        }
                        if(mt.contains("video/")) {
                            types.add(ObjectField.Type.VIDEO);
                        }
                        if(mt.contains("audio/")) {
                            types.add(ObjectField.Type.AUDIO);
                        }
                    }
                } catch(Throwable ignore) {
                }
                return new ObjectField<>(label, new FileField(types.toArray(new ObjectField.Type[0])));
            }
            return new ObjectField<>(label, realObjectType, md.isAny(), objectFieldType(md),
                    md.isAddAllowed());
        }
        if(JSON.class == type) {
            return new JSONField(label);
        }
        if(Money.class == type) {
            return new MoneyField(label, md.getIntParameter(0, 0));
        }
        if(DecimalNumber.class == type || Rate.class == type) {
            String style = md.getStyle();
            while(style != null && style.indexOf(' ') >= 0) {
                style = style.replace(" ", "");
            }
            int w = 0, d = 0;
            if(style != null && (d = style.indexOf("(d:")) >= 0) {
                style = style.substring(d + 3);
                try {
                    d = style.indexOf(',');
                    w = Integer.parseInt(style.substring(0, d));
                    style = style.substring(d + 1);
                    d = Integer.parseInt(style.substring(0, style.indexOf(')')));
                } catch(Throwable t) {
                    style = null;
                }
            } else {
                style = null;
            }
            if(Rate.class == type) {
                if(style == null) {
                    return new RateField(label);
                } else {
                    RateField field = new RateField(label, d);
                    field.setLength(w);
                    return field;
                }
            }
            if(style == null) {
                return new DecimalNumberField(label);
            }
            DecimalNumberField field = new DecimalNumberField(label, d);
            field.setLength(w);
            return field;
        }
        if(Quantity.class == type) {
            return switch(fieldName) {
                case "UnitOfMeasurement", "UnitOfIssue" ->
                        new UOMField(objectClass.getName().equals("com.storedobject.iot.ValueLimit"));
                default -> new QuantityField(label, md.getIntParameter(0, 0),
                        md.getIntParameter(6, 1));
            };
        }
        if(Area.class == type) {
            return new AreaField(label, md.getIntParameter(0, 0),
                    md.getIntParameter(6, 1));
        }
        if(Count.class == type) {
            return new CountField(label, md.getIntParameter(0, 0));
        }
        if(Distance.class == type) {
            return new DistanceField(label, md.getIntParameter(0, 0),
                    md.getIntParameter(6, 1));
        }
        if(Volume.class == type) {
            return new VolumeField(label, md.getIntParameter(0, 0),
                    md.getIntParameter(6, 1));
        }
        if(Weight.class == type) {
            return new WeightField(label, md.getIntParameter(0, 0),
                    md.getIntParameter(6, 1));
        }
        if(FractionalCount.class == type) {
            return new FractionalCountField(label, md.getIntParameter(0, 0),
                    md.getIntParameter(6, 1));
        }
        if(WeightOrVolume.class == type) {
            return new WeightOrVolumeField(label, md.getIntParameter(0, 0),
                    md.getIntParameter(6, 1));
        }
        if(Temperature.class == type) {
            return new TemperatureField(label, md.getIntParameter(0, 0),
                    md.getIntParameter(6, 1));
        }
        if(Pressure.class == type) {
            return new PressureField(label, md.getIntParameter(0, 0),
                    md.getIntParameter(6, 1));
        }
        if(Speed.class == type) {
            return new SpeedField(label, md.getIntParameter(0, 0),
                    md.getIntParameter(6, 1));
        }
        if(Percentage.class == type) {
            return new PercentageField(label, md.getIntParameter(5, 0),
                    md.getIntParameter(2, 1));
        }
        if(Angle.class == type) {
            return new AngleField(label, md.getIntParameter(6, 0),
                    md.getIntParameter(2, 1));
        }
        if(WeightRate.class == type) {
            return new WeightRateField(label, md.getIntParameter(0, 0),
                    md.getIntParameter(6, 1));
        }
        if(VolumeRate.class == type) {
            return new VolumeRateField(label, md.getIntParameter(0, 0),
                    md.getIntParameter(6, 1));
        }
        if(TimeDuration.class == type) {
            return new TimeDurationField(label, md.getIntParameter(0, 0),
                    md.getIntParameter(6, 1));
        }
        if(ComputedDate.class == type) {
            return new ComputedDateField(label);
        }
        if(ComputedMinute.class == type) {
            ComputedMinutesField mf = new ComputedMinutesField(label);
            mf.setLength(md.getIntParameter(9, 0));
            String p = md.getParameter(1);
            if(p != null && p.length() == 0) {
                p = null;
            }
            if(p != null) {
                mf.setPlaceholder(p);
            }
            p = md.getParameter("", 2);
            if(p.indexOf('D') >= 0) {
                mf.setAllowDays(true);
            }
            return mf;
        }
        if(Geolocation.class == type) {
            return new GeolocationField(label);
        }
        if(ComputedDouble.class == type) {
            return new ComputedDoubleField(label, 0.0, md.getIntParameter(-1, 0),
                    md.getIntParameter(2, 1));
        }
        if(ComputedInteger.class == type) {
            return new ComputedIntegerField(label, md.getIntParameter(8, 0));
        }
        if(ComputedLong.class == type) {
            return new ComputedLongField(label, md.getIntParameter(8, 0));
        }
        if(java.sql.Time.class == type) {
            return new TimeField(label);
        }
        if(java.sql.Timestamp.class == type) {
            return new TimestampField(label);
        }
        if(double.class == type) {
            DoubleField field = new DoubleField(label, 0.0, md.getIntParameter(-1, 0),
                    md.getIntParameter(6, 1));
            field.setPlaceholder(md.getParameter(2));
            return field;
        }
        if(long.class == type) {
            LongField field = new LongField(label, 0L, md.getIntParameter(-1, 0));
            field.setPlaceholder(md.getParameter(1));
            return field;
        }
        if(int.class == type) {
            try {
                Method listM = objectClass.getMethod("get" + fieldName + "Values");
                if(Modifier.isStatic(listM.getModifiers()) && listM.getReturnType() == String[].class) {
                    return new ChoiceField(label, (String[]) listM.invoke(null));
                }
            } catch(Exception ignore) {
            }
            try {
                Method listM = objectClass.getMethod("get" + fieldName + "BitValues");
                if(Modifier.isStatic(listM.getModifiers()) && listM.getReturnType() == String[].class) {
                    return new TokenChoicesField(label, (String[]) listM.invoke(null));
                }
            } catch(Exception ignore) {
            }
            HasValue<?, ?> f = null;
            try {
                Method cM = objectClass.getMethod("get" + fieldName + "Converter");
                if(Modifier.isStatic(cM.getModifiers()) && ValueConverter.class.isAssignableFrom(cM.getReturnType())) {
                    ValueConverter<?> vc = (ValueConverter<?>) cM.invoke(null);
                    if(vc instanceof MinutesValueConverter) {
                        MinutesField field = new MinutesField(label);
                        field.setPlaceholder(vc.getEmptyTextValue());
                        f = field;
                    }
                    if(vc instanceof DaysValueConverter) {
                        f = new DaysField(label);
                    }
                }
            } catch(Exception ignore) {
            }
            if(f == null) {
                if(md.isMinutes()) {
                    f = new MinutesField(label);
                } else if(md.isDays()) {
                    f = new DaysField(label);
                } else {
                    int w = md.getIntParameter(0, 0);
                    boolean negative = w < 0;
                    if(negative) {
                        w = -w;
                    } else if(w == 0) {
                        w = -1;
                    }
                    f = new IntegerField(label, 0, w, false, negative);
                    ((IntegerField) f).setPlaceholder(md.getParameter(1));
                }
            }
            if(f instanceof MinutesField mf) {
                mf.setLength(md.getIntParameter(9, 0));
                String p = md.getParameter(1);
                if(p != null && p.length() == 0) {
                    p = null;
                }
                if(p != null) {
                    mf.setPlaceholder(p);
                }
                p = md.getParameter("", 2);
                if(p.indexOf('D') >= 0) {
                    mf.setAllowDays(true);
                }
            }
            return f;
        }
        if(type != String.class) {
            return null;
        }
        int p1 = md.getIntParameter(-1, 0);
        if(md.isSecret()) {
            PasswordField field = new PasswordField(label);
            if(p1 > 0) {
                field.setMaxLength(p1);
                field.setMinLength(p1);
            }
            return field;
        }
        if(md.isCountry()) {
            return new CountryField(label);
        }
        if(md.isPhone()) {
            return new PhoneField(label);
        }
        if(md.isAddress()) {
            return new AddressField(label);
        }
        if(md.isTimeZone()) {
            return new TimeZoneField(label);
        }
        if(md.isCurrency()) {
            return new CurrencyField(label);
        }
        if(md.isMultiline() || md.isPopupText()) {
            if(md.isPopupText()) {
                return new PopupTextField(label);
            } else {
                TextArea field = new TextArea(label);
                int cols = md.getColumnSpan();
                if(cols > 1) {
                    field.getElement().setAttribute("colspan", "" + cols);
                }
                return field;
            }
        }
        TextField field = new TextField(label);
        if(md.isCode() || md.isStyle("upper") || md.isStyle("uppercase")) {
            field.uppercase();
        } else if(md.isStyle("lower") || md.isStyle("lowercase")) {
            field.lowercase();
        }
        if(p1 <= 0) {
            field.setMinWidth("15em");
            return field;
        }
        field.setMinLength(p1);
        return field;
    }

    private static ObjectField.Type objectFieldType(UIFieldMetadata md) {
        if(md.getMetadata() == null) {
            return ObjectField.Type.AUTO;
        }
        for(ObjectField.Type type : ObjectField.Type.values()) {
            if(md.isStyle("" + type)) {
                return type;
            }
        }
        return ObjectField.Type.AUTO;
    }

    private static List<ObjectField.Type> objectFieldTypes(UIFieldMetadata md) {
        List<ObjectField.Type> types = new ArrayListSet<>();
        if(md.getMetadata() == null) {
            return types;
        }
        for(ObjectField.Type type : ObjectField.Type.values()) {
            if(md.isStyle("" + type)) {
                types.add(type);
            }
        }
        return types;
    }

    interface RO {
    }

    private static class ROTextField extends TextField implements ValueRequired, RO {

        private ROTextField(String label) {
            super(label);
        }

        @Override
        public void setReadOnly(boolean readOnly) {
            super.setReadOnly(true);
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public void setRequired(boolean required) {
            super.setRequired(false);
        }

        @Override
        public boolean isRequired() {
            return false;
        }

        @Override
        public boolean isRequiredIndicatorVisible() {
            return false;
        }
    }

    private static class ROIntegerField extends IntegerField implements ValueRequired, RO {

        private ROIntegerField(String label) {
            super(label);
            setEmptyDisplay("");
        }

        @Override
        public void setReadOnly(boolean readOnly) {
            super.setReadOnly(true);
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public void setRequired(boolean required) {
        }

        @Override
        public boolean isRequired() {
            return false;
        }

        @Override
        public boolean isRequiredIndicatorVisible() {
            return false;
        }
    }

    private static class ROLongField extends LongField implements ValueRequired, RO {

        private ROLongField(String label) {
            super(label);
            setEmptyDisplay("");
        }

        @Override
        public void setReadOnly(boolean readOnly) {
            super.setReadOnly(true);
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public void setRequired(boolean required) {
        }

        @Override
        public boolean isRequired() {
            return false;
        }

        @Override
        public boolean isRequiredIndicatorVisible() {
            return false;
        }
    }
}