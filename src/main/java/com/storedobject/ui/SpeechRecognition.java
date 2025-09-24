package com.storedobject.ui;

import com.storedobject.core.StringUtility;
import com.storedobject.helper.ID;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextFieldBase;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Tag("speech-mic")
@JsModule("./so/speech2text/speech-recognition.ts")
@JsModule("./so/speech2text/speech-mic.ts")
public class SpeechRecognition extends Component {

    private Map<String, Consumer<String>> listeners = null;
    private final Application application;

    public SpeechRecognition(TextFieldBase<?, ?> targetField) {
        this(targetField, targetField instanceof TextArea);
    }

    public SpeechRecognition(TextFieldBase<?, ?> targetField, boolean suffix) {
        String id;
        id = targetField.getId().orElse(null);
        if(id == null) {
            id = "so-" + ID.newID();
            targetField.setId(id);
        }
        getElement().setAttribute("for", id);
        if(suffix) {
            targetField.setSuffixComponent(this);
        } else {
            targetField.setPrefixComponent(this);
        }
        targetField.addFocusListener(e -> getElement().executeJs("this.startVI()"));
        targetField.addBlurListener(e -> getElement().executeJs("this.stopVI()"));
        application = Application.get();
    }

    public void addCommand(String command, Consumer<String> listener) {
        if(listener == null || !StringUtility.isLetterOrDigit(command)) {
            return;
        }
        command = command.toLowerCase();
        if(listeners == null) {
            listeners = new HashMap<>();
        }
        if(application == null) {
            listeners.put(command, listener);
        } else {
            listeners.put(command, c -> application.access(() -> listener.accept(c)));
        }
        getElement().executeJs("this.command($0)", command);
    }

    @ClientCallable
    private void command(String command) {
        if(listeners == null) {
            return;
        }
        Consumer<String> listener = listeners.get(command);
        if(listener != null) {
            listener.accept(command);
        }
    }
}
