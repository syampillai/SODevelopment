package com.storedobject.ui.util;

import com.storedobject.core.*;
import com.storedobject.ui.Application;
import com.storedobject.ui.FileField;
import com.storedobject.ui.ObjectEditor;
import com.storedobject.ui.ObjectField;
import com.storedobject.vaadin.ImageButton;
import com.storedobject.vaadin.TranslatedField;
import com.storedobject.vaadin.ValueRequired;
import com.vaadin.flow.component.HasValidation;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.util.ArrayList;
import java.util.List;

public class AttachmentField extends TranslatedField<StreamAttachment, StreamData> implements ValueRequired {

    private final StreamAttachment attachment;

    public AttachmentField(String label, StreamAttachment attachment) {
        super(new FField(attachment.getAttachmentType()), (f, s) -> convertToValue(s, attachment), (f, a) -> convertToInternalValue(a));
        this.attachment = attachment;
        setLabel(label);
        setRequiredIndicatorVisible(attachment.isRequired());
        ((FField)getField()).setRequired(attachment.isRequired());
        attachment.setField(this);
        if(getFileClass() != FileData.class) {
            ((FF)getField()).setExtraEditor(new ExtraEditor(this));
        }
    }

    @Override
    public void setRequired(boolean required) {
    }

    @Override
    public boolean isRequired() {
        return attachment.isRequired();
    }

    @Override
    public void setLabel(String label) {
        super.setLabel(label);
        ((FileField)getField()).setInternalLabel(label);
    }

    private static StreamAttachment convertToValue(StreamData stream, StreamAttachment attachment) {
        attachment.createValue().setFile(stream);
        return attachment;
    }

    private static StreamData convertToInternalValue(StreamAttachment attachment) {
        FileData fileData = attachment.getValue();
        return fileData == null || Id.isNull(fileData.getFileId()) ? null : fileData.getFile();
    }

    public StreamAttachment getAttachment() {
        return attachment;
    }

    @Override
    public boolean isEmpty() {
        StreamAttachment a = getValue();
        return a == null || a.isNull();
    }

    void update(StreamAttachment value) {
        setValue(value);
        setPresentationValue(value);
    }

    private Class<? extends FileData> getFileClass() {
        return attachment.getFileClass();
    }

    @Override
    public boolean isInvalid() {
        HasValue<?, ?> field = getField();
        if(field instanceof HasValidation) {
            return ((HasValidation) field).isInvalid();
        }
        return super.isInvalid();
    }

    private interface FF {

        void setExtraEditor(ExtraEditor extraEditor);

        ExtraEditor getExtraEditor();

        default ImageButton addExtraButton(StreamData value) {
            ExtraEditor extraEditor = getExtraEditor();
            return extraEditor == null ? null : extraEditor.extra;
        }

        default boolean isInvalid() {
            ExtraEditor extraEditor = getExtraEditor();
            return extraEditor != null && extraEditor.isInvalid();
        }
    }

    private static ObjectField.Type[] attachmentTypes(int attachmentType) {
        if(attachmentType == 0 || (attachmentType & 0b1111111) == 0b1111111) {
            return new ObjectField.Type[0];
        }
        List<ObjectField.Type> types = new ArrayList<>();
        if((attachmentType & 1) == 1) {
            types.add(ObjectField.Type.FILE);
        }
        if((attachmentType & 2) == 2) {
            types.add(ObjectField.Type.IMAGE);
        }
        if((attachmentType & 4) == 4) {
            types.add(ObjectField.Type.VIDEO);
        }
        if((attachmentType & 8) == 8) {
            types.add(ObjectField.Type.AUDIO);
        }
        if((attachmentType & 16) == 16) {
            types.add(ObjectField.Type.STILL_CAMERA);
        }
        if((attachmentType & 32) == 32) {
            types.add(ObjectField.Type.VIDEO_CAMERA);
        }
        if((attachmentType & 64) == 64) {
            types.add(ObjectField.Type.MIC);
        }
        ObjectField.Type[] array = new ObjectField.Type[types.size()];
        return types.toArray(array);
    }

    private static class FField extends FileField implements FF {

        private ExtraEditor extraEditor;

        public FField(int attachmentType) {
            super(null, attachmentTypes(attachmentType));
        }

        public void setExtraEditor(ExtraEditor extraEditor) {
            this.extraEditor = extraEditor;
        }

        public ExtraEditor getExtraEditor() {
            return extraEditor;
        }

        @Override
        public ImageButton addExtraButton(StreamData value) {
            return FF.super.addExtraButton(value);
        }

        @Override
        public boolean isInvalid() {
            if(FF.super.isInvalid()) {
                return true;
            }
            return super.isInvalid();
        }

        @Override
        protected void setModelValue(StreamData value, boolean fromClient) {
            super.setModelValue(value, fromClient);
            setVal(value);
        }

        @Override
        public void setValue(StreamData value) {
            setVal(value);
            super.setValue(value);
        }

        private void setVal(StreamData value) {
            Id id = value == null ? null : value.getId();
            if(Id.isNull(id) && extraEditor != null) {
                if(!extraEditor.field.attachment.isAdded()) {
                    extraEditor.field.attachment.setDeleted();
                }
            }
        }
    }

    @SuppressWarnings("rawtypes")
    private static class ExtraEditor {

        private final ImageButton extra;
        private final AttachmentField field;
        private final ObjectEditor fileEditor;

        public ExtraEditor(AttachmentField field) {
            this.field = field;
            fileEditor = ObjectEditor.create(field.getFileClass(), EditorAction.EDIT | EditorAction.VIEW, field.getLabel());
            fileEditor.setIncludeFieldChecker(name -> !("File".equals(name) || "Name".equals(name)));
            //noinspection unchecked
            fileEditor.setSaver(oe -> {
                field.attachment.setEdited();
                return true;
            });
            fileEditor.setWindowMode(true);
            fileEditor.getComponent();
            if(fileEditor.streamFieldsCreated().count() == 0) {
                extra = null;
            } else {
                extra = new ImageButton("More information", VaadinIcon.ELLIPSIS_DOTS_H, e -> popup()).withBox();
            }
        }

        @SuppressWarnings("unchecked")
        private void popup() {
            if(extra == null) {
                return;
            }
            FileData file = field.getValue().getValue();
            if(file == null) {
                return;
            }
            field.attachment.undelete();
            if(field.isReadOnly()) {
                if(!file.created()) {
                    fileEditor.viewObject(file);
                }
                return;
            }
            fileEditor.editObject(file);
        }

        public boolean isInvalid() {
            if(extra == null) {
                return false;
            }
            StreamAttachment value = field.getValue();
            if(value.isNull() && !field.isRequired()) {
                return false;
            }
            FileData file = value.getValue();
            try {
                if(file == null) {
                    return false;
                }
                fileEditor.validateData();
                file.validateData(fileEditor.getTransactionManager());
                return false;
            } catch(Empty_Stream_Data empty_stream_data) {
                return false;
            } catch (Exception notValid) {
                if(field.attachment.isAdded()) {
                    Application.warning(notValid);
                    return true;
                }
                popup();
                return true;
            }
        }
    }
}