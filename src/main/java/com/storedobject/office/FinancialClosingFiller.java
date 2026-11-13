package com.storedobject.office;

/**
 * Filler implementation for formulas that must be evaluated in a financial
 * closing context.
 * <p>
 * This class specializes {@code ObjectFiller} by marking the filler as related
 * to financial closing processing. The supplied formula is initialized through
 * the parent filler mechanism and is used to derive values from stored object
 * data.
 * </p>
 *
 * @author Syam
 */
public class FinancialClosingFiller extends ObjectFiller {

    /**
     * Constructs a financial closing filler with the specified formula.
     * The formula is used to initialize the underlying formula evaluation mechanism
     * for deriving values in a financial closing context.
     *
     * @param formula the formula expression used to initialize this filler
     */
    public FinancialClosingFiller(String formula) {
        super(formula);
    }

    /**
     * Indicates that this filler operates in a financial closing context.
     *
     * @return {@code true} because this implementation is always for financial closing processing
     */
    @Override
    protected boolean isFinancialClosing() {
        return true;
    }
}
