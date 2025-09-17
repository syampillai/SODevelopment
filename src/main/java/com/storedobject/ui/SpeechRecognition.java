package com.storedobject.ui;

import com.storedobject.helper.ID;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextFieldBase;

@Tag("speech-mic")
@JsModule("./so/speech2text/speech-mic.ts")
public class SpeechRecognition extends Component {

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
    }
}
