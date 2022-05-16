package com.storedobject.ui;

import com.storedobject.ui.util.Stepper;
import com.storedobject.vaadin.*;
import com.vaadin.flow.component.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Stepped View allows you to create a multi-stepped {@link View}.
 *
 * @author Syam
 */
public abstract class SteppedView extends View implements Transactional {

    final List<Component> forms = new ArrayList<>();
    private int steps;
    private final Stepper stepper = new Stepper();

    /**
     * Constructor.
     *
     * @param numberOfSteps Number of steps in the view
     */
    public SteppedView(int numberOfSteps) {
        this(numberOfSteps, null);
    }

    /**
     * Constructor.
     *
     * @param numberOfSteps Number of steps in the view
     * @param caption Caption of the view
     */
    public SteppedView(int numberOfSteps, String caption) {
        super(caption);
        steps = numberOfSteps;
        if(steps < 1) {
            steps = 1;
        }
    }

    /**
     * This method will be invoked once forms for all steps are constructed.
     */
    protected void formsConstructed() {
    }

    @Override
    protected void initUI() {
        for(int s = 1; s <= steps; s++) {
            stepper.addStep(createStep(s, getStepCaption(s)));
        }
        stepper.setCancelledAction(ignore -> cancel());
        stepper.setCompletedAction(ignore -> complete());
        setComponent(stepper);
        formsConstructed();
        stepper.initUI();
    }

    public String getStepCaption(int step) {
        return null;
    }

    private Stepper.Step createStep(int step, String stepCaption) {
        return new Stepper.Step(step, getStepLabel(step), getStepComp(step), stepCaption) {
            @Override
            protected void onEnter() {
                clearAlerts();
                SteppedView.this.enter(step);
            }

            @Override
            protected void onAbort() {
                clearAlerts();
                SteppedView.this.back(step);
            }

            @Override
            protected boolean onComplete() {
                clearAlerts();
                try {
                    return SteppedView.this.commit(step) && SteppedView.this.complete(step);
                } catch (Throwable error) {
                    warning(error);
                }
                return false;
            }
        };
    }

    private Component getStepComp(int step) {
        int count = forms.size();
        Component c = getStepComponent(step);
        if(c != null && count < forms.size()) {
            return c;
        }
        CSSGrid div = new CSSGrid();
        div.setSizeFull();
        if(c != null) {
            div.add(c);
        }
        forms.add(c == null ? div : c);
        return div;
    }

    AbstractForm<?> getForm(int step) {
        if(step < 1 || step > forms.size()) {
            return null;
        }
        Component c = forms.get(step - 1);
        return c instanceof AbstractForm ? (AbstractForm<?>)c : null;
    }

    /**
     * Traverse to a particular step from the current step. Traversal may not be successful depending on the
     * conditions and validations at the intermediary steps.
     *
     * @param step Step to jump to
     * @return The step to which traversal is successful.
     */
    public int goToStep(int step) {
        return stepper.goToStep(step);
    }

    /**
     * Go to the next step (Equivalent of pressing the "Next" button).
     *
     * @return True if successful.
     */
    public boolean nextStep() {
        return stepper.nextStep();
    }

    /**
     * Go to the previous step (Equivalent of pressing the "Back" button).
     *
     * @return True if successful.
     */
    public boolean previousStep() {
        return stepper.previousStep();
    }

    /**
     * Invoke the finish (Equivalent of pressing the "Finish" button). This will not be successful if not called from
     * the final step.
     *
     * @return True if successful.
     */
    public boolean finish() {
        return stepper.finish();
    }

    /**
     * Check if the current step is the first step.
     *
     * @return True or false.
     */
    public boolean isFirstStep() {
        return stepper.isFirstStep();
    }

    /**
     * Check if the current step is the final step.
     *
     * @return True or false.
     */
    public boolean isFinalStep() {
        return stepper.isFinalStep();
    }

    /**
     * Add extra button to the footer. Button will be visible in all steps unless its visibility is programmatically
     * controlled.
     *
     * @param button Button to add
     */
    public void addExtraButton(Button button) {
        stepper.addExtraButton(button);
    }

    /**
     * Hide the "Back" button.
     */
    public void hideBackButton() {
        stepper.hideBackButton();
    }

    /**
     * Hide the "Next" button.
     */
    public void hideNextButton() {
        stepper.hideNextButton();
    }

    /**
     * Hide the "Finish" button.
     */
    public void hideFinishButton() {
        stepper.hideFinishButton();
    }

    /**
     * Hide the "Cancel" button.
     */
    public void hideCancelButton() {
        stepper.hideCancelButton();
    }

    /**
     * Unhide the "Back" button.
     */
    public void unhideBackButton() {
        stepper.unhideBackButton();
    }

    /**
     * Unhide the "Next" button.
     */
    public void unhideNextButton() {
        stepper.unhideNextButton();
    }

    /**
     * Unhide the "Finish" button.
     */
    public void unhideFinishButton() {
        stepper.unhideFinishButton();
    }

    /**
     * Unhide the "Cancel" button.
     */
    public void unhideCancelButton() {
        stepper.unhideCancelButton();
    }

    /**
     * Set the label on the "Cancel" button.
     * @param label Label to set.
     */
    public void setCancelLabel(String label) {
        stepper.setCancelLabel(label);
    }

    /**
     * Set the label on the "Back" button.
     * @param label Label to set.
     */
    public void setBackLabel(String label) {
        stepper.setBackLabel(label);
    }

    /**
     * Set the label on the "Finish" button.
     * @param label Label to set.
     */
    public void setFinishLabel(String label) {
        stepper.setFinishLabel(label);
    }

    /**
     * Set the label on the "Next" button.
     * @param label Label to set.
     */
    public void setNextLabel(String label) {
        stepper.setNextLabel(label);
    }

    /**
     * Get a label for the step. If you provide a label, that will be displayed along with the step number.
     *
     * @param step Step
     * @return Default implementation returns <code>null</code> so that no label is displayed.
     */
    protected String getStepLabel(int step) {
        return null;
    }

    /**
     * Get the component to be used for the respective step. (After creating your component, you may invoke one of these
     * static methods to position the component if needed: {@link #align(Component, CSSGrid.Position)},
     * {@link #justify(Component, CSSGrid.Position)}, {@link #center(Component)}).
     *
     * @param step Step
     * @return Component to be added. (Default implementation) returns <code>null</code>.
     */
    protected Component getStepComponent(int step) {
        return null;
    }

    /**
     * This method is invoked when you enter into this step.
     *
     * @param step Step
     */
    protected void enter(int step) {
    }

    /**
     * This method is invoked when you go back to the previous step from this step.
     *
     * @param step Step
     */
    protected void back(int step) {
    }

    /**
     * This method is invoked when you complete a step. (From the last step,
     * you typically do the rest of the processing and {@link #close()} the view.
     * If it raises any error or returns <code>false</code>, forward movement will be stopped.
     *
     * @param step Step
     * @return True if the operation is successful.
     * @throws Exception In case of any error.
     */
    protected boolean complete(int step) throws Exception {
        return true;
    }

    /**
     * This method is invoked before invoking {@link #complete(int)} and if it raises any error or
     * returns <code>false</code>, forward movement will be stopped.
     *
     * @param step Step
     * @return True if the commit operation is successful.
     * @throws Exception Any exception
     */
    protected boolean commit(int step) throws Exception {
        AbstractForm<?> form = getForm(step);
        return form == null || form.commit();
    }

    /**
     * This method will be invoked when "Cancel" button is pressed. The default behaviour is to {@link #abort()}
     * the view.
     */
    public void cancel() {
        abort();
    }

    /**
     * This method is invoked when the all steps are successfully completed. The default behaviour is closes the view
     * by invoking {@link #close()}.
     */
    public void complete() {
        close();
    }

    /**
     * Justify (horizontally) a component within the view's content area. (You may use this method from within your
     * {@link #getStepComponent(int)} method to position your component).
     *
     * @param component Component
     * @param position Position
     */
    public static void justify(Component component, CSSGrid.Position position) {
        if(position == null) {
            position = CSSGrid.Position.STRETCH;
        }
        component.getElement().getStyle().set("justify-self", position.toString().toLowerCase());
    }

    /**
     * Align (vertically) a component within the view's content area. (You may use this method from within your
     * {@link #getStepComponent(int)} method to position your component).
     *
     * @param component Component
     * @param position Position
     */
    public static void align(Component component, CSSGrid.Position position) {
        if(position == null) {
            position = CSSGrid.Position.STRETCH;
        }
        component.getElement().getStyle().set("align-self", position.toString().toLowerCase());
    }

    /**
     * Center (horizontally and vertically) a component within the view's content area. (You may use this method from within your
     * {@link #getStepComponent(int)} method to position your component).
     *
     * @param component Component
     */
    public static void center(Component component) {
        justify(component, CSSGrid.Position.CENTER);
        align(component, CSSGrid.Position.CENTER);
    }
}
