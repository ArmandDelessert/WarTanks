package navalbattle.client.gui.customcomponents;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import navalbattle.client.gui.utils.PicManip;

public class JFrameWithBGimg extends JFrame {

    private static final long serialVersionUID = -7170836469966138941L;

    private JLabel background;

    public JFrameWithBGimg(String path, int width, int height) throws IOException {
        BufferedImage bgImg = ImageIO.read(new File(path));

        bgImg = PicManip.resizeImg(bgImg, width, height);

        this.background = new JLabel(new ImageIcon(bgImg));
    }

    public JLabel getBG() {
        return this.background;
    }
}
