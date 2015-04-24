package navalbattle.boats;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import static navalbattle.boats.Aircraftcarrier.LENGTH;
import navalbattle.client.gui.utils.PicManip;

public class Destroyer extends Boat {

    /*
     ======================================================================
     This attribute is accessed by reflection and is identified by its name
     DO NOT CHANGE IT
     ======================================================================
     */
    public static final int LENGTH = 2;

    private static final String PATH_IMAGE_VERTICAL = "pictures/ships/destroyer_v.gif";
    private static final String PATH_IMAGE_HORIZONTAL = "pictures/ships/destroyer_h.gif";

    private BufferedImage imageVertical = null;
    private BufferedImage imageHorizontal = null;

    public Destroyer(int cellSize) throws IOException {
        super(LENGTH);

                imageHorizontal = PicManip.resizeImg(ImageIO.read(new File(PATH_IMAGE_HORIZONTAL)), cellSize * LENGTH - cellSize/4, cellSize - cellSize/4);
        imageVertical = PicManip.resizeImg(ImageIO.read(new File(PATH_IMAGE_VERTICAL)), cellSize - cellSize/4, cellSize * LENGTH - cellSize/4);
    }

    @Override
    public BufferedImage getImageVertical() {
        return imageVertical;
    }

    @Override
    public BufferedImage getImageHorizontal() {
        return imageHorizontal;
    }
}
