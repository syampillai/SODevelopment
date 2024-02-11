package com.storedobject.ui.tools;

import com.storedobject.core.*;
import com.storedobject.telegram.Bot;
import com.storedobject.ui.ELabel;
import com.storedobject.ui.Transactional;
import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.DataForm;
import com.storedobject.vaadin.View;
import com.vaadin.flow.component.icon.VaadinIcon;

public class TelegramRegistration extends DataForm implements Transactional {

    private final ELabel message = new ELabel();
    private final Bot bot;
    private final Button deregister = new Button("Deregister", VaadinIcon.UNLINK, e -> deregister());
    private Contact contact;

    public TelegramRegistration() {
        super("Telegram Registration");
        add(message);
        bot = StoredObject.list(Bot.class).single(false);
        deregister.setVisible(false);
    }

    @Override
    protected void buildButtons() {
        super.buildButtons();
        ok.setVisible(false);
        cancel.setVisible(false);
        buttonPanel.add(deregister);
    }

    @Override
    protected void execute(View parent, boolean doNotLock) {
        if(bot == null) {
            message.append("Telegram interface is not configured, please contact Technical Support", "red");
        } else {
            Person person = getTransactionManager().getUser().getPerson();
            contact = person
                    .listLinks(Contact.class, "Type.Type=4 AND Type.GroupingCode=0").single(false);
            if(contact == null) {
                ContactType ct = ContactType.createForTelegram(getTransactionManager());
                if(!transact(t -> {
                        contact.setType(ct);
                        contact.save(t);
                        person.addLink(t, contact);
                    })) {
                    return;
                }
            }
            if(contact.getContact().equals("Telegram")) {
                message.append("Send the following to ", "blue").append( bot.getName(), "red")
                        .append(" from your Telegram app", "blue")
                        .newLine(true).append("", "red")
                        .appendWithTag("register " + contact.getId(), "h2");
            } else {
                message.append("You are already registered", "red");
                deregister.setVisible(true);
            }
        }
        message.update();
        super.execute(parent, doNotLock);
    }

    private void deregister() {
        contact.setContact("Telegram");
        transact(contact::save);
        message(getTransactionManager().getUser().getPerson().getName() + " - Telegram registration has been removed");
        close();
    }

    @Override
    protected boolean process() {
        return false;
    }
}
