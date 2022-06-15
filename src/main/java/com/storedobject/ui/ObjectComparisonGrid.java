package com.storedobject.ui;

import com.storedobject.common.StringList;
import com.storedobject.core.ClassAttribute;
import com.storedobject.core.StoredObject;
import com.storedobject.core.StoredObjectUtility;
import com.storedobject.core.StringUtility;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.ButtonLayout;
import com.storedobject.vaadin.CloseableView;
import com.storedobject.vaadin.ListGrid;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;

import java.util.Objects;
import java.util.function.Function;

@SuppressWarnings("unchecked")
public class ObjectComparisonGrid<T extends StoredObject> extends ListGrid<ObjectComparisonGrid.Value>
        implements CloseableView {

    private final ELabel doneBy;
    public ObjectComparisonGrid(T current, T previous) {
        super(Value.class, StringList.EMPTY);
        setCaption("Changes");
        createColumn("Attribute", Value::attribute);
        createHTMLColumn("Changed", Value::changed);
        createColumn("PreviousValue", Value::previousValue);
        createColumn("CurrentValue", Value::currentValue);
        load(current, previous);
        setViewFilter(Value::isChanged);
        doneBy = new ELabel("(", Application.COLOR_INFO);
        doneBy.append("Changes done by ").append(current.person(), Application.COLOR_SUCCESS).append(")",
                Application.COLOR_INFO).update();
    }

    @Override
    public Component createHeader() {
        Checkbox cb = new Checkbox("Show All Details", false);
        cb.addValueChangeListener(e -> {
            if(e.getValue()) {
                setViewFilter(v -> true);
            } else {
                setViewFilter(Value::isChanged);
            }
        });
        return new ButtonLayout(cb, doneBy, new Button("Exit", e -> close()));
    }

    private void load(T current, T previous) {
        ClassAttribute<T> ca;
        ca = ClassAttribute.get(current.getClass().isAssignableFrom(previous.getClass()) ? current : previous);
        StoredObjectUtility.MethodList[] methods = StoredObjectUtility.createMethodLists(ca.getObjectClass(),
                StringList.create(ca.getAllAttributes()));
        boolean real;
        String a;
        Function<Object, String> func;
        Application application = Application.get();
        for(StoredObjectUtility.MethodList m: methods) {
            if(m.getReturnType() == Class.class) {
                continue;
            }
            func = m.display(application);
            a = m.getAttributeName();
            real = ca.getAttributes().contains(a);
            add(new Value(caption(ca.getFieldMetadata(a).getCaption(), a), func.apply(previous), func.apply(current),
                    real));
        }
    }

    private static String caption(String caption, String attribute) {
        if(caption != null && !caption.isEmpty()) {
            return caption;
        }
        return StringUtility.makeLabel(attribute);
    }

    record Value(String attribute, String previousValue, String currentValue, boolean real) {

        boolean isChanged() {
            return !Objects.equals(previousValue, currentValue);
        }

        HTMLText changed() {
            HTMLText html = new HTMLText();
            if(isChanged()) {
                html.append("Yes", real ? Application.COLOR_ERROR : "#860D9B");
            } else {
                html.append("No", Application.COLOR_SUCCESS);
            }
            html.update();
            return html;
        }
    }
}
