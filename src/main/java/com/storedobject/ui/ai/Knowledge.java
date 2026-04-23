package com.storedobject.ui.ai;

import com.storedobject.ai.KnowledgeModule;
import com.storedobject.common.Executable;
import com.storedobject.core.ApplicationServer;
import com.storedobject.core.JavaClassLoader;
import com.storedobject.core.StoredObject;
import com.storedobject.ui.Application;

/**
 * The Knowledge class is an extension of the com.storedobject.ai.Knowledge class 
 * and implements the Executable interface. This class provides functionality to 
 * manage knowledge topics and interact with a chat view for execution and display purposes.
 * 
 * @author Syam
 */
public class Knowledge extends com.storedobject.ai.Knowledge implements Executable {

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
        super(Application.get());
        if(topic == null) {
            return;
        }
        int p = topic.indexOf('|');
        if(p > 0) {
            add(topic.substring(p + 1));
            topic = topic.substring(0, p);
        }
        setTopic(topic);
        Application.get().closeMenu();
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
}
