package navalbattle.sounds;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SoundHelper {
    
    public static final String pathToSound = "sounds\\";
    public static enum SOUND_TYPE { MINE, SANK, DAMAGED, MISS, SAT, VICTORY, LOSS, POSITION, BEGIN}
    
    public static void playSound(SOUND_TYPE type)
    {
        String audioFileName;
        
        switch (type)
        {
            case MINE:
                audioFileName = "mine.wav";
                break;
                
            case SANK:
                audioFileName = "sinking.wav";
                break;
                
            case DAMAGED:
                audioFileName = "hit.wav";
                break;
                
            case MISS:
                audioFileName = "miss.wav";
                break;
                
            case SAT:
                audioFileName = "sat.wav";
                break;
                
            case VICTORY:
                audioFileName = "victory.wav";
                break;
                
            case LOSS:
                audioFileName = "loss.wav";
                break;
                
            case POSITION:
                audioFileName = "position.wav";
                break;
                
            case BEGIN:
                audioFileName = "begin.wav";
                break;
                
            default:
                throw new IllegalArgumentException("Unknown sound type");
        }
        
        String pathToMedia = pathToSound + audioFileName;
        File fileToPathToMedia = new File(pathToMedia);
        
        if (!fileToPathToMedia.exists() || !fileToPathToMedia.isFile())
            return; // TO-DO: replace with proper exception
        
        try {
            Clip clip = AudioSystem.getClip();
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(fileToPathToMedia);
            clip.open(inputStream);
            clip.start(); 
        } catch (LineUnavailableException | IOException | UnsupportedAudioFileException ex) {
        }
    }
}
