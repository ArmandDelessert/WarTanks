package navalbattle.boats;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import navalbattle.client.gui.utils.PicManip;

public class Aircraftcarrier extends Boat {

    /*
    ======================================================================
    This attribute is accessed by reflection and is identified by its name
    DO NOT CHANGE IT
    ======================================================================
    */
    public static final int LENGTH = 5;
    

    private static final String PATH_IMAGE_VERTICAL = "pictures/ships/aircraft_carrier_v.gif";
    private static final String PATH_IMAGE_HORIZONTAL = "pictures/ships/aircraft_carrier_h.gif";
    
    private BufferedImage imageVertical = null;
    private BufferedImage imageHorizontal = null;
    
    public Aircraftcarrier(int cellSize) throws IOException {
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
