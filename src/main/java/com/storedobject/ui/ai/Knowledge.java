package com.storedobject.ui.ai;

import com.storedobject.ai.KnowledgeModule;
import com.storedobject.common.Executable;
import com.storedobject.common.TriFunction;
import com.storedobject.core.*;
import com.storedobject.ui.Application;
import com.storedobject.ui.TemplateView;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;

import java.sql.Date;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;

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
        addModules(new UITools());
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
        return get(objectClass, purpose, AcceptObject::new);
    }

    /**
     * Retrieves an instance of the specified data class, configured and customized based on the provided purpose.
     * This method performs operations asynchronously and ensures thread-safety through semaphore synchronization.
     *
     * @param <T>         the type of data to be retrieved
     * @param dataClass   the `Class` object representing the type of data to be retrieved
     * @param purpose     a `String` describing the purpose or context for retrieving the data
     * @param formCreator a `TriFunction` that produces an `AcceptData` instance based on the provided arguments
     * @return an instance of type `T` representing the retrieved data
     * @throws SOException if the data retrieval is canceled, or no data is provided by the user
     */
    public <T> T get(Class<T> dataClass, String purpose, TriFunction<Class<T>, Knowledge, String, AcceptData<T>> formCreator) throws SOException {
        Semaphore semaphore = new Semaphore(1, true);
        semaphore.acquireUninterruptibly();
        AtomicReference<AcceptData<T>> adRef = new AtomicReference<>();
        application.access(() -> {
            AcceptData<T> ad = formCreator.accept(dataClass, this, purpose);
            customize(dataClass, ad, purpose);
            adRef.set(ad);
            semaphore.release();
        });
        semaphore.acquireUninterruptibly();
        semaphore.release();
        AcceptData<T> ad = adRef.get();
        T data = ad.retrieveData();
        if(data == null) {
            throw new SOException("Unable to provide the required information - user cancelled the operation");
        } else {
            String label = ad.getDataLabel();
            if(label != null) {
                putToMemory(data, label);
            }
        }
        return data;
    }

    /**
     * Customizes the data form for a specific purpose.
     *
     * @param <T> The type of data being accepted via the form.
     * @param dataClass The data class.
     * @param acceptData The form that accepts data from the user.
     * @param purpose A string indicating the purpose of customization. This is typically used to provide descriptive context about the customization operation.
     */
    public <T> void customize(Class<T> dataClass, AcceptData<T> acceptData, String purpose) {
    }

    @Override
    public Date getDate(String purpose) throws SOException {
        return get(Date.class, purpose, (c, k, p) -> new AcceptDate(k, p));
    }

    @Override
    public DatePeriod getDatePeriod(String purpose) throws SOException {
        return get(DatePeriod.class, purpose, (c, k, p) -> new AcceptDatePeriod(k, p));
    }

    /**
     * Show a dashboard to the user who is currently chatting.
     *
     * @param name             The name of the dashboard to be displayed.
     * @param dashboardContent    The HTML5/CSS text content of the dashboard to be displayed.
     */
    public void showDashboard(String name, String dashboardContent) {
        application.access(() -> {
            TemplateView dashboard = new TemplateView(name, () -> dashboardContent);
            TemplateView.clearCache();
            dashboard.execute();
        });
    }

    private class UITools implements KnowledgeModule {

        @SuppressWarnings("unused")
        @Tool("Show a dashboard to the user")
        public void showDashboard(
                @P("Name of the dashboard") String name,
                @P("""
                      HTML5/CSS content of the dashboard.
                      It should be self-containing HTML/CSS text containing only at most one <style> tag and no JavaScript.
                      Images should be inline SVGs.
                 """) String dashboardContent) {
            Knowledge.this.showDashboard(name, dashboardContent);
        }
    }
}
