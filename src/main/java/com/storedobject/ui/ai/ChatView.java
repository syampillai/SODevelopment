package com.storedobject.ui.ai;

import com.storedobject.ai.Chat;
import com.storedobject.ai.Knowledge;
import com.storedobject.ai.Topic;
import com.storedobject.core.StoredObject;
import com.storedobject.core.SystemUser;
import com.storedobject.ui.Application;
import com.storedobject.ui.ObjectComboField;
import com.storedobject.ui.SpeechRecognition;
import com.storedobject.ui.Transactional;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * The view used to display a chat with AI capabilities.
 *
 * @author Syam
 */
public class ChatView extends View implements CloseableView, Transactional {

    private static final String CHAT = "AI";
    private final String you;
    private final MessageList messageList = new MessageList();
    private final List<MessageListItem> messages = new ArrayList<>();
    private final TextArea input = new TextArea("Ask me anything");
    private final Button send = new Button("Send", e -> process());
    private Knowledge knowledge;
    private Chat chat;
    private final H2 topicDisplay;

    /**
     * Default constructor for the ChatView class.
     * Initializes a ChatView instance with no specified knowledge base
     * and an empty topic. This setup provides a basic chat interface
     * without pre-defined context or configuration.
     */
    public ChatView() {
        this(null, "");
    }

    /**
     * Constructs a ChatView instance with the specified knowledge base and topic.
     * This view provides a user interface for chatting, displaying messages,
     * and interacting with a defined topic.
     *
     * @param knowledge the knowledge base to be used within the chat
     * @param topic the initial topic of discussion displayed in the view
     */
    public ChatView(Knowledge knowledge, String topic) {
        super("AI Chat");
        this.knowledge = knowledge;
        you = getTransactionManager().getUser().getName();
        VerticalLayout v = new VerticalLayout();
        messageList.setMarkdown(true);
        messageList.setWidthFull();
        topicDisplay = new H2();
        topicDisplay.setWidthFull();
        topicDisplay.getStyle().set("background-color", "var(--so-header-background-50pct)")
                .set("color", "var(--so-header-color)").set("text-overflow", "ellipsis")
                .set("padding", "5px").set("box-sizing", "border-box");
        v.add(topicDisplay, messageList, input);
        setTopic(topic);
        input.setWidthFull();
        send.setDisableOnClick(true);
        v.add(send);
        setComponent(v);
        new SpeechRecognition(input);
    }

    /**
     * Sets the topic for the chat view and updates related UI components.
     *
     * @param topic the topic to be set, which will be displayed in the topic display area
     */
    public void setTopic(String topic) {
        if(topic == null || topic.isBlank()) {
            topic = "No topic set";
        }
        topicDisplay.setText("Topic: " + topic);
    }

    /**
     * Sets the knowledge base to be used within the chat view and updates
     * the related components accordingly. If the given knowledge base is the
     * same as the current one, no action is taken. If the knowledge base is
     * null, the chat view is closed.
     *
     * @param knowledge the new knowledge base to be set; can be null to close the chat view
     */
    public void setKnowledge(Knowledge knowledge) {
        if(knowledge == this.knowledge) {
            return;
        }
        if(knowledge == null) {
            close();
            return;
        }
        this.knowledge = knowledge;
        createChat();
    }

    /**
     * Retrieves the current knowledge base associated with this ChatView instance.
     *
     * @return the knowledge base used in this chat view
     */
    public final Knowledge getKnowledge() {
        return knowledge;
    }

    private boolean createChat() {
        if(chat != null) {
            chat.close();
        }
        try {
            chat = knowledge.createChat();
            return true;
        } catch (Exception e) {
            close();
            error(e);
            return false;
        }
    }

    @Override
    protected void execute(View parent, boolean doNotLock) {
        if(knowledge == null) {
            new TopicSelector().execute();
            return;
        }
        if(chat == null && !createChat()) {
            return;
        }
        super.execute(parent, doNotLock);
        Application.get().setPollInterval(this, 1000);
    }

    @Override
    public void clean() {
        if(chat != null) {
            chat.close();
        }
        Application.get().stopPolling(this);
        super.clean();
    }

    private void process() {
        String text = input.getValue();
        if(text.isBlank()) {
            input.focus();
            return;
        }
        text = text.trim().replace("\n", "  \n");
        MessageListItem item = new MessageListItem(text, you);
        item.setUserColorIndex(1);
        messages.add(item);
        while (messages.size() > 50) {
            messages.removeFirst();
        }
        messageList.setItems(messages);
        input.clear();
        response("Thinking...", false);
        Future<String> r = chat.ask(text);
        Application a = Application.get();
        Thread.startVirtualThread(() -> {
            try {
                String response = r.get();
                messages.removeLast();
                a.access(() -> response(response, true));
            } catch (Throwable ignored) {
            }
        });
    }

    private void response(String text, boolean enable) {
        MessageListItem item = new MessageListItem(text, CHAT);
        item.setUserAbbreviation(CHAT);
        item.setUserColorIndex(2);
        messages.add(item);
        messageList.setItems(messages);
        send.setEnabled(enable);
        input.focus();
    }

    private class TopicSelector extends DataForm {

        private final ObjectComboField<Topic> topicField = new ObjectComboField<>("Topic", Topic.class, "1=0");
        private final boolean canAccess;

        private TopicSelector() {
            super("Select Topic");
            SystemUser user = getTransactionManager().getUser();
            List<Topic> topics = StoredObject.list(Topic.class).filter(t -> user.canAccess(t.getLogic())).toList();
            canAccess = !topics.isEmpty();
            topicField.load(topics);
            if(canAccess) {
                setRequired(topicField, true);
            }
            addField(topicField);
        }

        @Override
        protected void execute(View parent, boolean doNotLock) {
            if(!canAccess) {
                warning("You don't have access to any topic!");
                ChatView.this.close();
                return;
            }
            super.execute(parent, doNotLock);
        }

        @Override
        protected boolean process() {
            close();
            Topic topic = topicField.getValue();
            ChatView.this.setTopic(topic.getName());
            Object kl = Application.get().getServer().execute(topic.getKnowledgeLogicClass());
            if(kl instanceof Knowledge k) {
                ChatView.this.setKnowledge(k);
                ChatView.this.execute();
            }
            return true;
        }
    }
}
