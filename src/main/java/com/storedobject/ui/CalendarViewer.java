package com.storedobject.ui;

import com.storedobject.calendar.Calendar;
import com.storedobject.common.SORuntimeException;
import com.storedobject.common.StringList;
import com.storedobject.core.*;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.html.Div;
import org.vaadin.stefan.fullcalendar.CalendarViewImpl;
import org.vaadin.stefan.fullcalendar.FullCalendar;
import org.vaadin.stefan.fullcalendar.FullCalendarBuilder;

import java.sql.Date;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class CalendarViewer extends View implements CloseableView, Transactional {

    private static final String CONFIGURE = "Configure Calendar", CREATE_NEW = "Create New Calendar", DELETE = "Delete Calendar";
    private static final StringList VIEWS = StringList.create("Month View", "Week View", "Day View", "Schedule (Day)", "Schedule (Week)", "Schedule (Month)", "Schedule (Year)");
    private Id personId;
    private Calendar calendar, loaded;
    private final ComboField<Object> menu = new ComboField<>();
    private final ChoiceField view = new ChoiceField(VIEWS);
    private final ArrayList<Object> menuList = new ArrayList<>();
    private final FullCalendar calendarView;
    private ObjectEditor<Calendar> editor;
    private final DateField dateField = new DateField();

    public CalendarViewer() {
        super("My Calendar");
        menu.setItemLabelGenerator(this::menuDisplay);
        setPerson(getTransactionManager().getUser().getPersonId());
        ButtonLayout buttons = new ButtonLayout();
        buttons.add(menu, view);
        Div div = new Div();
        calendarView = FullCalendarBuilder.create().build();
        calendarView.setHeight("92%");
        div.add(buttons, calendarView);
        setComponent(div);
        trackValueChange(menu);
        calendar = (Calendar) menuList.get(0);
        menu.setValue(calendar);
        trackValueChange(view);
        Button previous = new Button("Previous", "angle_left", e -> previous());
        Button today = new Button("Today", "home", e -> today());
        Button next = new Button("Next", "angle_right", e -> next());
        buttons.add(previous, today, next, dateField);
        trackValueChange(dateField);
    }

    private void loadMenu0() {
        menuList.clear();
        StoredObject.list(Calendar.class, "Owner=" + personId, "Name").forEach(menuList::add);
        menuList.add(CONFIGURE);
        menuList.add(CREATE_NEW);
        menuList.add(DELETE);
        personId.listMasters(Calendar.class).forEach(menuList::add);
        menu.setItems(menuList);
    }

    private void loadMenu() {
        loadMenu0();
        menu.setValue(menuList.get(0));
    }

    private String menuDisplay(Object item) {
        if(item instanceof StoredObject) {
            return ((StoredObject) item).toDisplay();
        }
        return item.toString();
    }

    private void setPerson(Person person) {
        setPerson(person.getId());
    }

    private void setPerson(Id personId) {
        if(!StoredObject.exists(Calendar.class, "Owner=" + personId)) {
            calendar = new Calendar();
            calendar.setOwner(personId);
            if(personId.equals(getTransactionManager().getUser().getPersonId())) {
                calendar.setName("My Calendar");
            } else {
                Person p = StoredObject.get(Person.class, personId);
                switch(p.getGender()) {
                    case 0 -> calendar.setName("His Calendar");
                    case 1 -> calendar.setName("Her Calendar");
                    default -> calendar.setName("Its Calendar");
                }
            }
            if(!transact(t -> calendar.save(t))) {
                if(this.personId == null) {
                    throw new SORuntimeException("Can't create calendar for you!");
                } else {
                    menu.setValue(menuList.get(0));
                    return;
                }
            }
        }
        this.personId = personId;
        loadMenu();
    }

    @Override
    public void valueChanged(ChangedValues changedValues) {
        if(changedValues.getChanged() == menu) {
            Object o = menu.getValue();
            if(o == null) {
                return;
            }
            if(o instanceof Calendar) {
                calendar = (Calendar)o;
                loadCalendar();
                return;
            }
            if(o instanceof Person) {
                setPerson((Person)o);
                return;
            }
            if(o.equals(CONFIGURE)) {
                editor().editObject(calendar, this);
                return;
            }
            if(o.equals(CREATE_NEW)) {
                editor().editObject(newCalendar(), this);
                return;
            }
            if(o.equals(DELETE)) {
                new ActionForm("Do you really want to delete this calendar?", this::deleteCalendar, this::reset).execute();
            }
            return;
        }
        if(changedValues.getChanged() == view) {
            switch(view.getValue()) {
                case 0 -> {
                    calendarView.changeView(CalendarViewImpl.DAY_GRID_MONTH);
                    return;
                }
                case 1 -> {
                    calendarView.changeView(CalendarViewImpl.TIME_GRID_WEEK);
                    return;
                }
                case 2 -> {
                    calendarView.changeView(CalendarViewImpl.TIME_GRID_DAY);
                    return;
                }
                case 3 -> {
                    calendarView.changeView(CalendarViewImpl.LIST_DAY);
                    return;
                }
                case 4 -> {
                    calendarView.changeView(CalendarViewImpl.LIST_WEEK);
                    return;
                }
                case 5 -> {
                    calendarView.changeView(CalendarViewImpl.LIST_MONTH);
                    return;
                }
                case 6 -> calendarView.changeView(CalendarViewImpl.LIST_YEAR);
            }
            return;
        }
        if(changedValues.getChanged() == dateField && changedValues.isFromClient()) {
            calendarView.gotoDate(DateUtility.local(dateField.getValue()));
        }
    }

    private void previous() {
        calendarView.previous();
        moved(-1);
    }

    private void today() {
        calendarView.today();
        dateField.setValue(DateUtility.today());
    }

    private void next() {
        calendarView.next();
        moved(1);
    }

    private void moved(int direction) {
        Date d = dateField.getValue();
        d = switch(view.getValue()) {
            case 0, 5 -> DateUtility.addMonth(d, direction);
            case 1, 4 -> DateUtility.addDay(d, 7 * direction);
            case 2, 3 -> DateUtility.addDay(d, direction);
            case 6 -> DateUtility.addYear(d, direction);
            default -> dateField.getValue();
        };
        dateField.setValue(d);
    }

    private void deleteCalendar() {
        if(StoredObject.count(Calendar.class, "Owner=" + calendar.getOwnerId()) == 1) {
            if(transact(t -> calendar.removeAllLinks(t))) {
                reset();
                loaded = null;
                loadCalendar();
                return;
            } else {
                close();
            }
        }
        if(!transact(t -> calendar.delete(t))) {
            close();
            return;
        }
        loadMenu();
    }

    private Calendar newCalendar() {
        Calendar c = new Calendar();
        c.setOwner(calendar.getOwnerId());
        String name = "New Calendar";
        AtomicInteger ai = new AtomicInteger(0);
        while (calendars().anyMatch(o -> {
            String n = name;
            if (ai.get() > 0) {
                n += " " + ai.get();
            }
            return o.getOwnerId().equals(calendar.getOwnerId()) && o.getName().equals(n);
        })) {
            ai.incrementAndGet();
        }
        c.setName(name + (ai.get() == 0 ? "" : (" " + ai.get())));
        return c;
    }

    private ObjectEditor<Calendar> editor() {
        if(editor == null) {
            editor = ObjectEditor.create(Calendar.class, EditorAction.EDIT);
            editor.addObjectChangedListener(new Edited());
            editor.addObjectEditorListener(new Cancelled());
            editor.getComponent();
            editor.setFieldReadOnly("Owner");
        }
        return editor;
    }

    private void reset() {
        resetTo(calendar);
    }

    private void resetTo(Calendar calendar) {
        this.calendar = calendars().filter(o -> o.getId().equals(calendar.getId())).findAny().orElse(null);
        menu.setValue(this.calendar);
    }

    private Stream<Calendar> calendars() {
        return menuList.stream().map(o -> o instanceof Calendar ? (Calendar)o : null).filter(Objects::nonNull);
    }

    private class Edited implements ObjectChangedListener<Calendar> {
        @Override
        public void saved(Calendar object) {
            loadMenu0();
            resetTo(object);
        }
    }

    private class Cancelled implements ObjectEditorListener {
        @Override
        public void editingCancelled() {
            menu.setValue(calendar);
        }
    }

    private void loadCalendar() {
        getMenuItem().setLabel(calendar.getName());
        calendarView.setFirstDay(DayOfWeek.of(calendar.getStartOfWeek()));
        if(loaded == calendar) {
            return;
        }
        loaded = calendar;
        calendarView.removeAllEntries();
        // Add entries
    }
}
