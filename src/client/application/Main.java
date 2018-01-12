package client.application;

import client.logic.Client;
import client.logic.GlobalContainer;
import client.view.SceneManager;
import com.messages.ClientInOutMessage;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setOnCloseRequest(event -> {
            GlobalContainer.getInstance().setInSession(false);
            Client client = GlobalContainer.getInstance().getClient();
            if (client != null) {
                try {
                    client.sendMessage(new ClientInOutMessage(false, null,null));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                client.interrupt();
            }
        });
        GlobalContainer.getInstance().setStage(primaryStage);
        GlobalContainer.getInstance().switchScene(SceneManager.LOGIN_SCREEN_SCENE);
        GlobalContainer.getInstance().show();
        Stage stage = GlobalContainer.getInstance().initStage("/DecisionWindow.fxml");
        GlobalContainer.getInstance().setDecisionStage(stage);
        stage = GlobalContainer.getInstance().initStage("/WaitingWindow.fxml");
        GlobalContainer.getInstance().setWaitingStage(stage);
        stage = GlobalContainer.getInstance().initStage("/ErrorWindow.fxml");
        GlobalContainer.getInstance().setErrorStage(stage);
        stage =GlobalContainer.getInstance().initStage("/DialogWindow.fxml");
        GlobalContainer.getInstance().setDialogStage(stage);

    }


    public static void main(String[] args) {
        launch(args);
    }
}
