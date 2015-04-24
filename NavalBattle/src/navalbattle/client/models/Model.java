package navalbattle.client.models;

import java.util.Observable;
import navalbattle.controllers.MainController;
import navalbattle.lang.LanguageHelper;
import navalbattle.protocol.common.IRemoteMessageListener;


public abstract class Model extends Observable implements IRemoteMessageListener {
    private final MainController controller;

    public Model(MainController controller) {
        this.controller = controller;
    }

    public MainController getController() {
        return controller;
    }
    
    public LanguageHelper getLanguageHelper()
    {
        return this.controller.getLanguageHelper();
    }
}
