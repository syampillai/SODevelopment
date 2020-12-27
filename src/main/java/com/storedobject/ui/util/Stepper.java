package com.storedobject.ui.util;

import com.storedobject.helper.LitComponent;
import com.storedobject.vaadin.ClickHandler;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.template.Id;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Tag("so-stepper")
@JsModule("./so/stepper/so-stepper.js")
public class Stepper extends LitComponent implements HasSize, HasStyle {

    private final List<Step> steps;
    private Step currentStep;
    private Consumer<Stepper> cancelledAction, completedAction;
    private final HorizontalLayout buttons = new HorizontalLayout();
    private List<com.storedobject.vaadin.Button> extraButtons;

    @Id
    private Div header;
    @Id
    private Div content;
    @Id
    private Div footer;

    private Button cancel;
    private Button back;
    private Button next;
    private Button finish;

    public Stepper() {
        this.steps = new ArrayList<>();
        initFooter();
    }

    private void initFooter() {
        cancel = new Button("Cancel", e -> cancelled());
        cancel.addThemeVariants(ButtonVariant.LUMO_ERROR);
        back = new Button("Back", e -> previousStep());
        next = new Button("Next", e -> nextStep());
        next.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        finish = new Button("Finish", e -> finish());
        finish.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        back.setVisible(false);
        finish.setVisible(false);
        cancel.setVisible(false);
    }

    public int goToStep(int step) {
        if(step < 1) {
            step = 1;
        }
        if(step > steps.size()) {
            step = steps.size();
        }
        if(currentStep == null && !initUI()) {
            return 0;
        }
        int c = steps.indexOf(currentStep) + 1;
        if(step == c) {
            return c;
        }
        if(step < c) {
            while(step < c) {
                if(!previousStep()) {
                    break;
                }
                --c;
            }
            return c;
        }
        while(step > c) {
            if(!nextStep()) {
                break;
            }
            ++c;
        }
        return c;
    }

    public boolean nextStep() {
        if(currentStep == null) {
            return initUI();
        }
        if(isFinalStep(currentStep)) {
            return false;
        }
        Step cs = currentStep;
        if(currentStep.complete()) {
            if(cs == currentStep) {
                Step nextStep = getNextStep(currentStep);
                changeStep(nextStep);
            }
            return true;
        }
        return false;
    }

    public boolean previousStep() {
        if(currentStep == null || isFirstStep(currentStep)) {
            return false;
        }
        Step cs = currentStep;
        currentStep.abort();
        if(cs == currentStep) {
            Step previousStep = getPreviousStep(currentStep);
            changeStep(previousStep);
        }
        return true;
    }

    public boolean finish() {
        if(currentStep == null || !isFinalStep(currentStep)) {
            return false;
        }
        Step cs = currentStep;
        if(currentStep.complete()) {
            if(cs == currentStep) {
                finish.setEnabled(false);
                cancel.setEnabled(false);
                back.setEnabled(false);
                if(completedAction != null) {
                    completedAction.accept(this);
                }
            }
            return true;
        }
        return false;
    }

    private void cancelled() {
        if(cancelledAction != null) {
            cancelledAction.accept(this);
        }
    }

    private void changeStep(Step newStep) {
        content.removeAll();
        currentStep = newStep;
        Step cs = currentStep;
        currentStep.enter();
        if(currentStep == cs) {
            content.add(currentStep.getContent());
            updateButtons();
        }
    }

    private void updateButtons() {
        if(currentStep != null) {
            updateButtonVisibility();
        }
    }

    private void updateButtonVisibility() {
        next.setVisible(!isFinalStep(currentStep));
        back.setVisible(!isFirstStep(currentStep));
        finish.setVisible(isFinalStep(currentStep));
    }

    private Step getNextStep(Step step) {
        if(isFinalStep(step)) {
            return step;
        }
        return steps.get(steps.indexOf(step) + 1);
    }

    private Step getPreviousStep(Step step) {
        if(isFirstStep(step)) {
            return step;
        }
        return steps.get(steps.indexOf(step) - 1);
    }

    public boolean isFirstStep() {
        return currentStep == null || isFirstStep(currentStep);
    }

    private boolean isFirstStep(Step step) {
        return steps.indexOf(step) == 0;
    }

    public boolean isFinalStep() {
        return currentStep != null && isFinalStep(currentStep);
    }

    private boolean isFinalStep(Step step) {
        return steps.indexOf(step) == steps.size() - 1;
    }

    /**
     * Adds the given step to the stepper.
     *
     * @param step the step to add to the stepper. Each step consists of a header and a content component.
     */
    public void addStep(Step step) throws IllegalArgumentException {
        header.add(step.getHeader());
        steps.add(step);
    }

    public boolean initUI() {
        if(currentStep != null || steps.isEmpty()) {
            return false;
        }
        currentStep = steps.get(0);
        currentStep.enter();
        content.add(currentStep.getContent());
        return true;
    }

    public void setCancelledAction(Consumer<Stepper> cancelledAction) {
        this.cancelledAction = cancelledAction;
        footer.removeAll();
        cancel.setVisible(cancelledAction != null);
        buttons();
    }

    private void buttons() {
        footer.removeAll();
        buttons.removeAll();
        if(cancelledAction == null) {
            extraButtons();
            buttons.add(next, finish);
            footer.add(back, new Div(), buttons);
        } else {
            buttons.add(back);
            extraButtons();
            buttons.add(next, finish);
            footer.add(cancel, buttons);
        }
    }

    private void extraButtons() {
        if(extraButtons != null) {
            extraButtons.forEach(buttons::add);
        }
    }

    public void addExtraButton(com.storedobject.vaadin.Button button) {
        if(button != null) {
            if(extraButtons == null) {
                extraButtons = new ArrayList<>();
            }
            extraButtons.add(button);
            buttons();
        }
    }

    public void setCancelText(String cancel) {
        this.cancel.setText(cancel);
    }

    public void setCompletedAction(Consumer<Stepper> completedAction) {
        this.completedAction = completedAction;
    }

    public void hideBackButton() {
        back.hide();
    }

    public void hideNextButton() {
        next.hide();
    }

    public void hideFinishButton() {
        finish.hide();
    }

    public void hideCancelButton() {
        cancel.hide();
    }

    public void unhideBackButton() {
        back.visible = true;
        updateButtonVisibility();
    }

    public void unhideNextButton() {
        next.visible = true;
        updateButtonVisibility();
    }

    public void unhideFinishButton() {
        finish.visible = true;
        updateButtonVisibility();
    }

    public void unhideCancelButton() {
        cancel.visible = true;
        cancel.setVisible(true);
    }

    private static class StepHeader extends Div {

        private StepHeader(int step, String title) {
            Span stepNumber = new Span(String.valueOf(step));
            stepNumber.addClassName("step-number");
            Div numberWrapper = new Div(stepNumber);
            numberWrapper.addClassName("number-wrapper");
            add(numberWrapper);
            if(title != null) {
                Label caption = new Label(title);
                caption.addClassName("step-title");
                add(numberWrapper, caption);
            }
            addClassName("step-header");
        }

        private void onAbort() {
            removeClassName("completed");
            removeClassName("active");
        }

        private void onComplete() {
            removeClassName("active");
            addClassName("completed");
        }

        private void onEnter() {
            removeClassName("completed");
            addClassName("active");
        }
    }

    /**
     * Container class containing the header and content component of a step also handles firing step events.
     */
    public static abstract class Step {

        private final StepHeader header;
        private final Component content;

        public Step(int step, String title, Component content) {
            this.header = new StepHeader(step, title);
            this.content = content;
        }

        public StepHeader getHeader() {
            return header;
        }

        public Component getContent() {
            return content;
        }

        private void enter() {
            header.onEnter();
            onEnter();
        }

        private void abort() {
            header.onAbort();
            onAbort();
        }

        private boolean complete() {
            if(onComplete()) {
                header.onComplete();
                return true;
            }
            return false;
        }

        /**
         * Called when the step is entered.
         */
        protected abstract void onEnter();

        /**
         * Called when the step is exited by hitting the back button.
         */
        protected abstract void onAbort();

        /**
         * Called when the step is exited by hitting the next button.
         *
         * @return True if successful.
         */
        protected abstract boolean onComplete();
    }

    private static class Button extends com.storedobject.vaadin.Button {

        private boolean visible = true;

        public Button(String text, ClickHandler clickHandler) {
            super(text, clickHandler);
        }

        @Override
        public void setVisible(boolean visible) {
            super.setVisible(visible && this.visible);
        }

        private void hide() {
            this.visible = false;
            if(isVisible()) {
                super.setVisible(false);
            }
        }
    }
}
