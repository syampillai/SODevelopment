package com.storedobject.ui;

import com.storedobject.vaadin.CompoundField;
import com.storedobject.vaadin.ImageButton;
import com.storedobject.vaadin.IntegerField;
import com.storedobject.vaadin.PaintedImage;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.Autocomplete;

import java.awt.*;
import java.util.Random;

/**
 * CRAM (Challenge Response Authentication Mechanism) field. This will ask for a challenge and the response can be
 * verified by calling the {@link #verified()} method.
 *
 * @author Syam
 */
public final class CRAMField extends CompoundField {

    private final Animation[] animation = { Animation.SHAKE, Animation.FLASH };
    private int animationIndex = 0;
    private final Random random = new Random();
    private final IntegerField cramInput;
    private final Cram cram;

    /**
     * Constructor.
     */
    public CRAMField() {
        cramInput = new IntegerField();
        cramInput.setAutocomplete(Autocomplete.OFF);
        cramInput.setLength(3);
        cramInput.setWidth("1.3cm");
        cramInput.setEmptyDisplay("");
        cramInput.setValue(0);
        //cram = new Cram(createType());
        cram = new Cram(PaintedImage.Type.JPG);
        cram.caption();
        ImageButton b = new ImageButton("Show Another", VaadinIcon.RECYCLE, e -> cram.reload());
        b.withBox().setSize("40px");
        super.add(b, cram, new ELabel(" = "), cramInput);
    }

    private PaintedImage.Type createType() {
        return Cram.Type.values()[random.nextInt(4)];
    }

    private void label(String label) {
        super.setLabel(label);
    }

    @Override
    public void setLabel(String label) {
    }

    @Override
    public void add(Component... components) {
    }

    /**
     * Check whether the current challenge is verified or not.
     *
     * @return True if verified, otherwise false.
     */
    public boolean verified() {
        return cramInput.getValue() == cram.result;
    }

    /**
     * Shake the input part to show that the challenge is incorrect.
     */
    public void shake() {
        animation[animationIndex % animation.length].animate(cramInput);
        animationIndex++;
    }

    private class Cram extends PaintedImage {

        private int result;
        private Type type;

        private Cram(Type type) {
            super(type, 160, 40);
            this.type = type;
        }

        private void caption() {
            label(type == Type.SVG ? "Number of Spots" : "Result of");
        }

        private void reload() {
            type = createType();
            caption();
            redraw();
            cramInput.focus();
        }

        @Override
        public void paint(Graphics2D g) {
            if(type == Type.SVG) {
                paintSVG(g);
                return;
            }
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
            int w = (int)g.getClipBounds().getWidth();
            do {
                result = random.nextInt(98) + 2;
            } while (result >= 70 && result <= 79);
            String s = result + " ";
            int next = random.nextInt(5) + 1;
            if(random.nextBoolean()) {
                while(result <= next) {
                    --next;
                }
                s += "- " + next;
                result -= next;
            } else {
                if(result < 6) {
                    s += "X " + next;
                    result *= next;
                } else {
                    s += "+ " + next;
                    result += next;
                }
            }
            int len = g.getFontMetrics().stringWidth(s);
            double angle = 0.1;
            int base = ((int) g.getClipBounds().getHeight()) >> 1;
            if(random.nextBoolean()) {
                angle = -angle;
            }
            g.rotate(angle, 5, 10);
            if(angle < 0) {
                base += 15;
            }
            g.setColor(random.nextBoolean() ? Color.RED : Color.BLACK);
            g.drawString(s, (w / 2) - (len / 2), base);
        }

        private void paintSVG(Graphics2D g) {
            result = random.nextInt(6) + 1;
            int shift = 0;
            double angle = 0.1;
            if(random.nextBoolean()) {
                angle = -angle;
                shift = 15;
            }
            g.rotate(angle, 5, 10);
            for(int i = 0; i < result; i++) {
                g.setColor(i % 2 == 0 ? Color.RED : Color.BLACK);
                g.fillOval(25 + i * 9, 8 + shift, 8, 8);
            }
        }
    }
}
