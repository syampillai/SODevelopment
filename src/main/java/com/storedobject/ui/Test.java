package com.storedobject.ui;

import com.storedobject.vaadin.ButtonLayout;
import com.storedobject.vaadin.Clickable;
import com.storedobject.vaadin.CloseableView;
import com.storedobject.vaadin.View;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.shared.Registration;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Test extends View implements CloseableView {

    public Test() {
        super("Test");
        ButtonLayout layout = new ButtonLayout();
        Card c;
        for(int i = 0; i < 10; i++) {
            c = new Card();
            c.setTitle("Card No #" + (i + 1));
            c.setContent("Content for card #" + (i + 1));
            c.addTitleClickListener(s -> message("Title clicked: " + s));
            c.addContentClickListener(s -> message("Content clicked: " + s));
            layout.add(c);
        }
        setComponent(layout);
    }

    public static class Card extends TemplateComponent {

        @Id
        private final Div title = new Div();
        @Id
        private final Div content = new Div();
        private final List<Consumer<String>> titleClickListeners = new ArrayList<>();
        private final List<Consumer<String>> contentClickListeners = new ArrayList<>();

        public Card() {
            super("""
            <style>
                .card-wrapper {
                    display: flex;
                    box-sizing: border-box;
                    justify-content: center;
                    padding: 20px;
                    background-color: #f0f0f0;
                }
        
                .card {
                    background: linear-gradient(135deg, #6a11cb, #2575fc);
                    border-radius: 20px;
                    padding: 20px;
                    width: 100%;
                    max-width: 350px;
                    box-sizing: border-box;
                    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
                    color: white;
                    transition: transform 0.3s, box-shadow 0.3s;
                }
        
                .card:hover {
                    transform: translateY(-10px);
                    box-shadow: 0 6px 15px rgba(0, 0, 0, 0.2);
                }
        
                .card-title {
                    font-size: 1.5rem;
                    margin-bottom: 10px;
                    cursor: pointer;
                }
        
                .card-content {
                    font-size: 1rem;
                    line-height: 1.5;
                    cursor: pointer;
                }
            </style>
            <div class="card-wrapper">
                <div class="card">
                    <div id = "title" class="card-title"></div>
                    <div id = "content" class="card-content"></div>
                </div>
            </div>
            """);
            new Clickable<>(title,
                    e -> titleClickListeners.forEach(c -> c.accept(title.getText())));
            new Clickable<>(content,
                    e -> contentClickListeners.forEach(c -> c.accept(content.getText())));
        }

        @Override
        protected Component createComponentForId(String id, String tag) {
            return switch (id) {
                case "title" -> title;
                case "content" -> content;
                default -> super.createComponentForId(id, tag);
            };
        }

        public String getTitle() {
            return title.getText();
        }

        public void setTitle(String title) {
            this.title.setText(title);
        }

        public String getContent() {
            return content.getText();
        }

        public void setContent(String content) {
            this.content.setText(content);
        }

        public Registration addTitleClickListener(Consumer<String> listener) {
            titleClickListeners.add(listener);
            return () -> titleClickListeners.remove(listener);
        }

        public Registration addContentClickListener(Consumer<String> listener) {
            contentClickListeners.add(listener);
            return () -> contentClickListeners.remove(listener);
        }
    }
}