package navalbattle.client.gui.customcomponents;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.ButtonModel;
import javax.swing.JButton;

public class JButtonDesign extends JButton {

    private static final long serialVersionUID = 2772974931285762827L;

    public JButtonDesign(String text) {
        setText(text);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setForeground(Color.white);
    }

    // Code pris ici : http://itc.himatif.or.id/sourcecode/swing-make-over-button/
    // Drez : Note pour moi-même : new Color(0, 0, 0, 0) pour personnaliser la couleur
    // RGBa (a pour opacité, 1 opaque, 0 transparent)
    @Override
    protected void paintComponent(Graphics g) {
        ButtonModel buttonModel = getModel();

        Graphics2D gd = (Graphics2D) g.create();

        gd.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        gd.setPaint(new GradientPaint(0, 0, Color.darkGray, 0, getHeight(),
                Color.black));

        setForeground(Color.white);

        if (buttonModel.isRollover()) {
            gd.setPaint(new GradientPaint(0, 0, Color.black, 0, getHeight(),
                    Color.darkGray));

            if (buttonModel.isPressed()) {
                gd.setPaint(new GradientPaint(0, 0, Color.white, 0,
                        getHeight(), Color.white));
                setForeground(Color.black);
            }
        }

        gd.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);

        gd.dispose();

        super.paintComponent(g);
    }
}
