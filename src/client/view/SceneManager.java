package client.view;

import client.logic.GlobalContainer;
import client.control.ClientFormController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

public class SceneManager {


    public static final int LOGIN_SCREEN_SCENE  = 1;
    public static final int MAIN_SCREEN_SCENE   = 2;

    private Scene loginScreen;
    private Scene mainScreen;

    public SceneManager() {

    }

    public Scene getScene(int sceneType) throws Exception {
        Scene scene = null;
        switch (sceneType) {
            case LOGIN_SCREEN_SCENE:
                if (loginScreen == null)
                    initScene(LOGIN_SCREEN_SCENE);
                scene = loginScreen;
                break;
            case MAIN_SCREEN_SCENE:
                if (mainScreen == null)
                    initScene(MAIN_SCREEN_SCENE);
                scene = mainScreen;
                break;
            default:

                break;
        }
        return scene;
    }


    private void initScene(int sceneId) throws Exception {
        if (sceneId == LOGIN_SCREEN_SCENE) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/LoginWindow.fxml"));
            Pane root = loader.load();
            loginScreen = new Scene(root,640,480);
        } else if (sceneId == MAIN_SCREEN_SCENE) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainWindow.fxml"));
            Pane root = loader.load();
            ClientFormController controller = loader.getController();
            GlobalContainer.getInstance().setController(controller);
            mainScreen = new Scene(root,640,480);
        }
    }
}
