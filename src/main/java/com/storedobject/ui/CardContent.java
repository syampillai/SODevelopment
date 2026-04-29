package com.storedobject.ui;

/**
 * An interface for content that can be displayed in a card.
 *
 * @author Syam
 */
public interface CardContent {

    /**
     * Sets the card that this content is associated with.
     * <p>Sets the card that this content is associated with. This method is typically called by the card itself
     * when the content is set to the card.</p>
     *
     * @param card Card to be set
     */
    void setCard(Card<?> card);
}
