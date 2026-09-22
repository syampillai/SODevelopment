package com.storedobject.ui.ai;

import com.storedobject.ai.KnowledgeModule;
import com.storedobject.common.Executable;
import com.storedobject.core.*;
import com.storedobject.ui.Application;

import java.sql.Date;
import java.util.concurrent.Semaphore;

/**
 * The Knowledge class is an extension of the com.storedobject.ai.Knowledge class 
 * and implements the Executable interface. This class provides functionality to 
 * manage knowledge topics and interact with a chat view for execution and display purposes.
 * 
 * @author Syam
 */
public class Knowledge extends com.storedobject.ai.Knowledge implements Executable {

    final Application application;
    final Semaphore semaphore = new Semaphore(1, true);
    private String topic;
    private ChatView chatView;

    /**
     * Default constructor.
     */
    public Knowledge() {
        this(null);
    }

    /**
     * Constructor with a topic.
     *
     * @param topic Topic (It could be a topic name or topic name followed by | and then class details).
     */
    public Knowledge(String topic) {
        this(Application.get(), topic);
    }

    private Knowledge(Application application, String topic) {
        super(application);
        this.application = application;
        if(topic != null) {
            if (topic.startsWith("LOG-")) {
                setLogging(true);
            }
            int p = topic.indexOf('|');
            if (p > 0) {
                add(topic.substring(p + 1));
                topic = topic.substring(0, p);
            }
            setTopic(topic);
        }
        application.closeMenu();
    }

    /**
     * Add class details to the knowledge base.
     *
     * @param classDetails Class details (Format: className1|className2|... or friendlyName,className1,param1,param2|...)
     */
    public void add(String classDetails) {
        if(classDetails == null || classDetails.isBlank()) {
            return;
        }
        String[] parts = classDetails.split("\\|");
        for(String part: parts) {
            parsePart(part);
        }
    }

    private void parsePart(String part) {
        int p = part.indexOf(',');
        if(p < 0) { // Single
            singlePart(part, null);
            return;
        }
        String first = part.substring(0, p);
        part = part.substring(p + 1);
        if(!first.contains(".")) { // First part is not a class name, it must be a friendly name
            multiPart(part, first);
        } else {
            singlePart(first, null);
        }
    }

    private void multiPart(String part, String friendlyName) {
        int p = part.indexOf(',');
        if(p < 0) { // Not a multipart
            singlePart(part, friendlyName);
            return;
        }
        String first = part.substring(0, p);
        String[] params = part.substring(p + 1).split(",");
        Class<?> c = kclass(first);
        if(c != null && StoredObject.class.isAssignableFrom(c)) {
            //noinspection unchecked
            addDataClass(friendlyName, (Class<? extends StoredObject>) c, params);
        } else {
            Application.get().log("Data class not found: " + first);
        }
    }

    private void singlePart(String part, String friendlyName) {
        Class<?> c = kclass(part);
        if(c != null) {
            if (StoredObject.class.isAssignableFrom(c)) {
                //noinspection unchecked
                addDataClass(friendlyName, (Class<? extends StoredObject>) c);
                return;
            }
            if(KnowledgeModule.class.isAssignableFrom(c)) {
                try {
                    addModules((KnowledgeModule) c.getConstructor().newInstance());
                    return;
                } catch (Exception ignored) {
                }
            }
        }
        Application.get().log("Class not found or can't be initiated: " + part);
    }

    private Class<?> kclass(String name) {
        try {
            return JavaClassLoader.getLogic(ApplicationServer.guessClass(name));
        } catch (ClassNotFoundException ignored) {
        }
        return null;
    }

    /**
     * Set the topic.
     *
     * @param topic Topic.
     */
    public void setTopic(String topic) {
        this.topic = topic == null || topic.isBlank() ? "None" : topic;
        if(chatView != null) chatView.setTopic(topic);
    }

    /**
     * Get the topic.
     *
     * @return Topic.
     */
    public String getTopic() {
        return topic;
    }

    /**
     * Execute the knowledge (opens the chat view).
     */
    @Override
    public void execute() {
        if(chatView != null) chatView.close();
        chatView = new ChatView(this, getTopic());
        chatView.execute();
    }

    /**
     * Retrieves the application instance associated with this knowledge object.
     *
     * @return The application instance.
     */
    public final Application getApplication() {
        return application;
    }

    @Override
    public <T extends StoredObject> T get(Class<T> objectClass, String purpose) throws SOException {
        AcceptObject<T> ao = new AcceptObject<>(this, objectClass, purpose);
        customize(objectClass, ao, purpose);
        T object = ao.getObject();
        if(object == null) {
            JSONMap m = ao.getMap();
            Object e = m.get("error");
            throw new SOException(e == null ? m.toString() : e.toString());
        }
        return object;
    }

    /**
     * Customizes a {@link StoredObject} class for a specific purpose using the provided {@link AcceptObject}.
     *
     * @param <T> The type of object being customized, which must extend {@link StoredObject}.
     * @param objectClass The class type of the object to be customized. This must be a class that extends {@link StoredObject}.
     * @param acceptObject An instance of {@link AcceptObject} used to process the object of type {@code T}.
     * @param purpose A string indicating the purpose of customization. This is typically used to provide descriptive context about the customization operation.
     */
    public <T extends StoredObject> void customize(Class<T> objectClass, AcceptObject<T> acceptObject, String purpose) {
    }

    @Override
    public Date getDate(String purpose) throws SOException {
        AcceptDate acceptDate = new AcceptDate(this, purpose);
        customize(acceptDate, purpose);
        Date date = acceptDate.getDate();
        if(date == null) {
            JSONMap m = acceptDate.getMap();
            Object e = m.get("error");
            throw new SOException(e == null ? m.toString() : e.toString());
        }
        return date;
    }

    /**
     * Customizes the provided {@link AcceptDate} instance for a specific purpose.
     *
     * @param acceptDate The {@link AcceptDate} instance to be customized. This object facilitates the handling
     *                   of date-related interactions within the system and maintains the selected date.
     * @param purpose    A string describing the purpose of the customization. This is typically used to
     *                   provide context or descriptive information about the operation being performed.
     */
    public void customize(AcceptDate acceptDate, String purpose) {
    }

    @Override
    public DatePeriod getDatePeriod(String purpose) throws SOException {
        AcceptDatePeriod acceptDatePeriod = new AcceptDatePeriod(this, purpose);
        customize(acceptDatePeriod, purpose);
        DatePeriod p = acceptDatePeriod.getDatePeriod();
        if(p == null) {
            JSONMap m = acceptDatePeriod.getMap();
            Object e = m.get("error");
            throw new SOException(e == null ? m.toString() : e.toString());
        }
        return p;
    }

    /**
     * Customizes the provided {@link AcceptDatePeriod} instance for a specific purpose.
     *
     * @param acceptDatePeriod The {@link AcceptDatePeriod} instance to be customized. This object is used
     *                         for handling a specific date-period-related operation within the system.
     * @param purpose          A string that provides a descriptive context or purpose for the customization.
     *                         It typically represents the reason or intent behind the operation.
     */
    public void customize(AcceptDatePeriod acceptDatePeriod, String purpose) {
    }
}
