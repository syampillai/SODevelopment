package com.storedobject.ui;

import com.vaadin.flow.component.AttachEvent;
import org.vaadin.hugerte.HugeRte;

/**
 * Rich text editor component.
 * <p>Note: Modified version of {@link HugeRte}.</p>
 *
 * @author Syam
 */
public class RichTextEditor extends HugeRte {

    public RichTextEditor(String label) {
        this();
        setLabel(label);
    }

    public RichTextEditor(String label, String initialValue) {
        this();
        setLabel(label);
        setValue(initialValue);
    }

    public RichTextEditor() {
        String config = """
        {
          "plugins": "lists",
          "toolbar": "undo redo | bold italic | bullist numlist | localimage",
          "automatic_uploads": false,
          "paste_data_images": true
        }""";
        setConfig(config);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        // Find the inner editorContainer ID dynamically from the DOM
        String jsInjection = """
            const element = this;
            
            const registerLocalImageButton = (editor) => {
                if (editor.__localImageInstalled) return;
                editor.__localImageInstalled = true;
                if (!editor.ui || !editor.ui.registry) return;
                editor.ui.registry.addButton('localimage', {
                    icon: 'image',
                    tooltip: 'Insert local image',
                    onAction: function () {
                        const input = document.createElement('input');
                        input.setAttribute('type', 'file');
                        input.setAttribute('accept', 'image/*');
                        input.onchange = function () {
                            if (!this.files || this.files.length === 0) return;
                            const file = this.files[0];
                            const reader = new FileReader();
                            reader.onload = function (e) {
                                const dataUrl = e.target.result;
                                const alt = (file.name || '').replace(/"/g, '&quot;');
                                // Insert the Base64 image directly into the content
                                editor.insertContent('<img src="' + dataUrl + '" alt="' + alt + '" />');
                            };
                            reader.readAsDataURL(file);
                        };
                        input.click();
                    }
                });
            };

            const tryRegister = () => {
                // 1. Find the inner div (editorContainer) that holds the HugeRTE instance
                const containerDiv = element.querySelector('div[id]');
                if (containerDiv && window.hugerte) {
                    const editor = window.hugerte.get(containerDiv.id);
                    if (editor) {
                        registerLocalImageButton(editor);
                        return true;
                    }
                }
                return false;
            };

            // Retry until the editor instance is fully initialized
            if (!tryRegister()) {
                let attempts = 0;
                const timer = setInterval(() => {
                    attempts++;
                    if (tryRegister() || attempts > 100) { // 10 seconds timeout
                        clearInterval(timer);
                    }
                }, 100);
            }
            """;
        this.getElement().executeJs(jsInjection);
    }
}