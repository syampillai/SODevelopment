package com.storedobject.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasText;

/**
 * Represents a section of a form, designed as a horizontal row layout for
 * arranging components. A {@code FormSection} typically includes a name
 * component and a styled line, which can also be customized in terms of span
 * and height.
 *
 * @author Syam
 */
public class FormSection extends Card.Row {

    private Component name;
    private final Card.Line line = new Card.Line();

    /**
     * Constructs a {@code FormSection} with a default column span of 2.
     * Delegates to the constructor that accepts a single integer parameter,
     * defaulting the column span when unspecified.
     */
    public FormSection() {
        this(0);
    }

    /**
     * Constructs a new FormSection with the specified name.
     * This constructor initializes the object with a default column span of 2.
     *
     * @param name the name of the form section
     */
    public FormSection(String name) {
        this(name, 0);
    }

    /**
     * Constructs a FormSection with the specified component as its name.
     * This constructor initializes the object with a default column span of 2.
     *
     * @param name the component to be used as the name or label for the section
     */
    public FormSection(Component name) {
        this(name, 0);
    }

    /**
     * Constructs a {@code FormSection} with the specified number of column spans
     * and an empty name.
     *
     * @param span the number of column spans; if less than 1, it defaults to 2
     */
    public FormSection(int span) {
        this("", span);
    }

    /**
     * Constructs a new FormSection with the specified name and span.
     *
     * @param name the name of the form section
     * @param span the number of columns or space the form section spans
     */
    public FormSection(String name, int span) {
        this(new ELabel(name), span);
    }

    /**
     * Constructs a FormSection with a specified name component and column span.
     *
     * @param name the component to be used as the name or label for the section.
     * @param span the number of columns the section should span.
     */
    public FormSection(Component name, int span) {
        line.getStyle().set("align-self", "end");
        setName(name);
        setHeight("35px");
        setColumnSpan(span);
    }

    /**
     * Sets the column span attribute for the current element. The column span determines
     * how many columns the element should span across in a grid or table layout.
     * If the provided span is less than 1, a default value of 2 will be used.
     *
     * @param span the desired number of columns the element should span. Must be
     *             a positive integer; if less than 1, defaults to 2.
     */
    public void setColumnSpan(int span) {
        if(span < 1) {
            span = 2;
        }
        getElement().setAttribute("colspan", "" + span);
    }

    /**
     * Retrieves the name component associated with this instance.
     *
     * @return the name component
     */
    public Component getName() {
        return name;
    }

    /**
     * Sets the name component for this object and updates its layout.
     *
     * @param name the Component to be set as the name; can be null
     */
    public void setName(Component name) {
        this.name = name;
        removeAll();
        if(name != null) {
            name.getStyle().set("align-content", "end").set("white-space", "nowrap");
            add(name);
        }
        add(line);
    }

    /**
     * Updates the name property with the provided value. Depending on the type
     * of the current name property, this method will either modify its content
     * or assign a new value.
     *
     * @param name the new name to be set for this instance
     */
    public void setName(String name) {
        if(this.name instanceof ELabel n) {
            n.clearContent().append(name).update();
        } else if(this.name instanceof HasText t) {
            t.setText(name);
        } else {
            setName(new ELabel(name));
        }
    }

    /**
     * Retrieves the line associated with the card.
     *
     * @return the line of type Card.Line
     */
    public Card.Line getLine() {
        return line;
    }
}
