package com.storedobject.ui.ai;

import com.storedobject.ai.Chat;
import com.storedobject.ai.Knowledge;
import com.storedobject.ui.Application;
import com.storedobject.ui.Transactional;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

public class ChatView extends View implements CloseableView, Transactional {

    private static final String CHAT = "AI";
    private final String you;
    private final MessageList messageList = new MessageList();
    private final List<MessageListItem> messages = new ArrayList<>();
    private final TextArea input = new TextArea("Ask me anything");
    private final Button send = new Button("Send", e -> process());
    private Knowledge knowledge;
    private Chat chat;

    public ChatView() {
        this("AI Chat");
    }

    public ChatView(String caption) {
        super("AI Chat");
        you = getTransactionManager().getUser().getName();
        VerticalLayout v = new VerticalLayout();
        messageList.setMarkdown(true);
        messageList.setWidthFull();
        H2 h = new H2(caption);
        h.setWidthFull();
        h.getStyle().set("background-color", "var(--so-header-background-50pct)")
                .set("color", "var(--so-header-color)").set("text-overflow", "ellipsis")
                .set("padding", "5px").set("box-sizing", "border-box");
        v.add(h, messageList, input);
        input.setWidthFull();
        send.setDisableOnClick(true);
        v.add(send);
        setComponent(v);
    }

    public void setKnowledge(Knowledge knowledge) {
        this.knowledge = knowledge;
    }

    public Knowledge getKnowledge() {
        if(knowledge == null) {
            knowledge = new Knowledge(getTransactionManager());
        }
        return knowledge;
    }

    @Override
    protected void execute(View parent, boolean doNotLock) {
        if(chat == null) {
            try {
                chat = getKnowledge().createChat();
            } catch (Exception e) {
                error(e);
                return;
            }
        }
        super.execute(parent, doNotLock);
        Application.get().setPollInterval(this, 1000);
    }

    @Override
    public void clean() {
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
}
