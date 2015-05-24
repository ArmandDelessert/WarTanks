package startMenu;

import game.Game;
import java.util.Observable;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.SlickException;
import role.*;

/**
 * Cette classe contient tous les lien sur les autres composants qui sont nécessaire pour la gestion du jeu
 * @author Julien Bignens
 * @author Bruno Carvalho
 * @author Gaëtan Djomnang Yaze
 * @author Marcel Sinniger
 *
 */
public class Model {

   private Role role;
   private ServerIpObservers serverIpObservers;
   private ScreenObservers screenObservers;
   private StartObservers startObservers;
   private String screen;
   private Game game;
   
   public class ServerIpObservers extends Observable {
      public void notifyAllObservers() {
         setChanged();
         notifyObservers(role.getIpAddress());
      }
   }
   
   public class ScreenObservers extends Observable {
      public void notifyAllObservers() {
         setChanged();
         notifyObservers(screen);
      } 
   }
   
   public class StartObservers extends Observable {
      public void notifyAllObservers() {
         setChanged();
         notifyObservers(screen);
      }
   }
   
   public Model() {
      serverIpObservers = new ServerIpObservers();
      screenObservers = new ScreenObservers();
      startObservers = new StartObservers();
   }
      
   public Role getRole() {
      return role;
   }
   
   /**
    * @param view the view to set
    */
   public void addServerIpObserver(View view) {
      this.serverIpObservers.addObserver(view);
   }

   /**
    * @param view the view to set
    */
   public void addScreenObserver(View view) {
      this.screenObservers.addObserver(view);
   }
   
   /**
    * @param view the view to set
    */
   public void addStartObserver(View view) {
      this.startObservers.addObserver(view);
   }
   
   public void setRoleToClient() {
      role = new ClientRole(this);
      serverIpObservers.notifyAllObservers();
   }
   
   public void setRoleToServer() {
      role = new ServerRole(this);
      serverIpObservers.notifyAllObservers();
   }

   /**
    * @return the screen
    */
   public String getScreen() {
      return screen;
   }

   /**
    * @param screen the screen to set
    */
   public void setScreen(String screen) {
      this.screen = screen;
      screenObservers.notifyAllObservers();
   }

   public void StartGame(final String gameName) {
	   Runnable gameRunnable = new Runnable() {
         @Override
         public void run() {
            AppGameContainer appGc;
            try{
               game = new Game(gameName, role);
               appGc = new AppGameContainer(game);
               appGc.setDisplayMode(810, 650, false);
               appGc.setShowFPS(false);
               appGc.setMinimumLogicUpdateInterval(150);
               appGc.setTargetFrameRate(60);
               appGc.start();
               
            }
            catch(SlickException c){
               c.printStackTrace();
            }
         }
      };
      Thread gameThread = new Thread(gameRunnable);
      gameThread.start();
   }
   
   public Game getGame() {
      return game;
   }

   /**
    * @return the startObservers
    */
   public StartObservers getStartObservers() {
      return startObservers;
   }
   
   
}
