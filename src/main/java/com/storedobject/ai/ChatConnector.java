package com.storedobject.ai;

import com.storedobject.common.StringList;
import com.storedobject.core.*;

import java.lang.reflect.Constructor;

/**
 * ChatConnector is a service responsible for managing chat-related actions, including creating
 * and interacting with chats, listing available topics, and managing chat lifecycle operations.
 * This class implements the {@code JSONService} interface and processes specific commands
 * sent through JSON input to perform its functions so that it can be invoked via SO Connector API.
 * <pre>The supported actions include:
 * - action = "close" - Closing an active chat session.
 * - action = "listTopics" - Listing available chat topics.
 * - action = "chat" - Starting a new chat session or interacting with an ongoing one.
 * </pre>
 * The class supports customization of chat behavior by allowing subclasses to override
 * specific methods for creating, customizing, or handling knowledge instances.
 *
 * @author Syam
 */
public class ChatConnector implements JSONService {

    private Chat chat;

    @Override
    public final void execute(Device device, JSON json, JSONMap result) {
        String action = json.getString("action");
        switch (action) {
            case "close" -> {
                if(chat != null) {
                    chat.close();
                    chat = null;
                }
            }
            case "listTopics" -> {
                JSONMap.Array a = result.array("topics");
                for(Topic t: Topic.list(device.getServer().getTransactionManager().getUser())) {
                    try {
                        t.save(a.map(), StringList.create(new String[] { "Topic" }), null, true, false, false);
                    } catch (Throwable ignored) {
                    }
                }
            }
            case "chat" -> {
                String query = json.getString("query");
                Id topicId = json.getId("topic");
                if(topicId != null) {
                    if(chat != null) {
                        chat.close();
                        chat = null;
                    }
                }
                if(chat == null) {
                    if(topicId == null) {
                        Knowledge k = createKnowledge(device, json, result);
                        if(k == null) {
                            result.put("result", "No chat topic specified");
                            return;
                        }
                        startChat(k, query, result);
                        return;
                    }
                    Topic topic = StoredObject.get(Topic.class, topicId);
                    if(topic == null) {
                        result.put("result", "Invalid topic ID = " + topicId);
                        return;
                    }
                    if(!topic.casAccess(device.getServer().getTransactionManager().getUser())) {
                        result.put("result", "Access denied, topic ID = " + topicId);
                        return;
                    }
                    Class<? extends Knowledge> knowledgeLogicClass = topic.getKnowledgeLogicClass();
                    if(knowledgeLogicClass == null) {
                        result.put("result", "Invalid knowledge class, topic ID = " + topicId);
                        return;
                    }
                    Constructor<?> constructor;
                    Knowledge k = null;
                    try {
                        try {
                            constructor = knowledgeLogicClass.getConstructor(Device.class);
                            k = (Knowledge) constructor.newInstance(device);
                        } catch (NoSuchMethodException e) {
                            try {
                                constructor = knowledgeLogicClass.getConstructor(TransactionManager.class);
                                k = (Knowledge) constructor.newInstance(device.getServer().getTransactionManager());
                            } catch (NoSuchMethodException ignored) {
                            }
                        }
                        if(k != null) {
                            startChat(k, query, result);
                            return;
                        }
                    } catch (Throwable ignored) {
                    }
                    result.put("result", "Unable to create knowledge, topic ID = " + topicId);
                } else {
                    chat(query, result);
                }
            }
            case null -> result.put("result", "Action not specified");
            default -> executeAction(action, device, json, result);
        }
    }

    private void startChat(Knowledge knowledge, String query, JSONMap result) {
        customize(knowledge);
        try {
            chat = knowledge.createChat();
            chat(query, result);
        } catch (Exception e) {
            result.put("result", "unable to create chat - " + e.getMessage());
        }
    }

    private void chat(String query, JSONMap result) {
        result.put("result", chat.ask(query));
    }

    /**
     * Executes a specified action on a given device using JSON data, and stores the result in a JSON map.
     *
     * @param action The action to be executed, represented as a string.
     * @param device The device on which the action is to be executed.
     * @param json The JSON data containing parameters or information required to execute the action.
     * @param result The JSON map where the output or result of the action execution will be stored.
     */
    protected void executeAction(String action, Device device, JSON json, JSONMap result) {
    }

    /**
     * Creates a Knowledge instance based on the provided device. This method is typically used to prepare
     * or initialize knowledge associated with a specific device and its context.
     *
     * @param device The device for which the knowledge is created.
     * @param json The JSON data containing additional parameters or context related to the knowledge creation.
     * @param result A JSON map where any additional information or results related to the knowledge creation might be stored.
     * @return The created Knowledge instance associated with the specified device.
     */
    protected Knowledge createKnowledge(Device device, JSON json, JSONMap result) {
        return new Knowledge(device);
    }

    /**
     * Allows customization of the provided Knowledge instance. This method can
     * be overridden to perform specific actions or adjustments on the
     * Knowledge object as required by the context or implementation.
     *
     * @param knowledge The Knowledge instance to be customized. This object
     *                   may be modified to fulfill specific customization requirements.
     */
    protected void customize(Knowledge knowledge) {
    }
}
