package startMenu;

/**
 * Ce le contrôler de la fenêtre principale
 * @author Julien Bignens
 * @author Bruno Carvalho
 * @author Gaëtan Djomnang Yaze
 * @author Marcel Sinniger
 *
 */
public class Controller {

   /**
    * Ouvrir la fenêtre principale
    */
   public Controller() {
      
      Model model = new Model();
      View view = new View(model);
      model.addServerIpObserver(view);
      model.addScreenObserver(view);
      model.addStartObserver(view);
     
   }

}
