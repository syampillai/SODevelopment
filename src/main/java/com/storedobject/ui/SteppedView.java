package com.storedobject.ui;

import com.storedobject.vaadin.Button;
import com.storedobject.vaadin.CSSGrid;
import com.storedobject.vaadin.View;
import com.vaadin.flow.component.Component;

/**
 * Stepped View allows you to create a multi-stepped {@link View}.
 *
 * @author Syam
 */
public abstract class SteppedView extends View implements Transactional {

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
    }

    /**
     * This method will be invoked once forms for all steps are constructed.
     */
    protected void formsConstructed() {
    }

    /**
     * Got to next step (Equivalent of pressing the "Next" button).
     *
     * @return True if successful.
     */
    public boolean nextStep() {
        return false;
    }

    /**
     * Got to previous step (Equivalent of pressing the "Back" button).
     *
     * @return True if successful.
     */
    public boolean previousStep() {
        return false;
    }

    /**
     * Invoke the finish (Equivalent of pressing the "Finish" button). This will not be successful if not called from
     * the final step.
     *
     * @return True if successful.
     */
    public boolean finish() {
        return false;
    }

    /**
     * Check if the current step is the first step.
     *
     * @return True or false.
     */
    public boolean isFirstStep() {
        return false;
    }

    /**
     * Check if the current step is the final step.
     *
     * @return True or false.
     */
    public boolean isFinalStep() {
        return false;
    }

    /**
     * Add extra button to the footer. Button will be visible in all steps unless its visibility is programmatically
     * controlled.
     *
     * @param button Button to add
     */
    public void addExtraButton(Button button) {
    }

    /**
     * Hide the "Back" button.
     */
    public void hideBackButton() {
    }

    /**
     * Hide the "Next" button.
     */
    public void hideNextButton() {
    }

    /**
     * Hide the "Finish" button.
     */
    public void hideFinishButton() {
    }

    /**
     * Hide the "Cancel" button.
     */
    public void hideCancelButton() {
    }

    /**
     * Unhide the "Back" button.
     */
    public void unhideBackButton() {
    }

    /**
     * Unhide the "Next" button.
     */
    public void unhideNextButton() {
    }

    /**
     * Unhide the "Finish" button.
     */
    public void unhideFinishButton() {
    }

    /**
     * Unhide the "Cancel" button.
     */
    public void unhideCancelButton() {
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
    @SuppressWarnings("RedundantThrows")
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
        return true;
    }

    /**
     * This method will be invoked when "Cancel" button is pressed. The default behaviour is to {@link #abort()}
     * the view.
     */
    public void cancel() {
    }

    /**
     * This method is invoked when the all steps are successfully completed. The default behaviour is closes the view
     * by invoking {@link #close()}.
     */
    public void complete() {
    }

    /**
     * Justify (horizontally) a component within the view's content area. (You may use this method from within your
     * {@link #getStepComponent(int)} method to position your component).
     *
     * @param component Component
     * @param position Position
     */
    public static void justify(Component component, CSSGrid.Position position) {
    }

    /**
     * Align (vertically) a component within the view's content area. (You may use this method from within your
     * {@link #getStepComponent(int)} method to position your component).
     *
     * @param component Component
     * @param position Position
     */
    public static void align(Component component, CSSGrid.Position position) {
    }

    /**
     * Center (horizontally and vertically) a component within the view's content area. (You may use this method from within your
     * {@link #getStepComponent(int)} method to position your component).
     *
     * @param component Component
     */
    public static void center(Component component) {
    }
}
