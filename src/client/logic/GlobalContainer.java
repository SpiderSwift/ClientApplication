package client.logic;

import client.control.ClientFormController;
import client.view.SceneManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


public class GlobalContainer {

    private boolean inSession;
    private static volatile GlobalContainer instance;
    private SceneManager sceneManager;
    private Stage window;
    private ClientFormController controller;
    private Client client;
    private Stage errorStage;
    private Stage dialogStage;
    private Stage waitingStage;
    private Stage decisionStage;
    private String callingId;

    public void setErrorStage(Stage errorStage) {
        this.errorStage = errorStage;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setWaitingStage(Stage waitingStage) {
        this.waitingStage = waitingStage;
    }

    public void setDecisionStage(Stage decisionStage) {
        this.decisionStage = decisionStage;
    }

    public String getCallingId() {
        return callingId;
    }

    public void setCallingId(String callingId) {
        this.callingId = callingId;
    }

    public Stage getErrorStage() {
        return errorStage;
    }

    public Stage getDialogStage() {
        return dialogStage;
    }

    public Stage getWaitingStage() {
        return waitingStage;
    }

    public Stage getDecisionStage() {
        return decisionStage;
    }

    public Stage initStage(String path) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
        try {
            Parent root = (Parent) loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            //stage.show();
            return stage;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public boolean isInSession() {
        return inSession;
    }

    public void setInSession(boolean inSession) {
        this.inSession = inSession;
    }

    public void setController(ClientFormController controller) {
        this.controller = controller;
    }

    public ClientFormController getController() {
        return controller;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    private GlobalContainer() {
        sceneManager = new SceneManager();
    }

    public static GlobalContainer getInstance() {
        if (instance == null) {
            synchronized (GlobalContainer.class) {
                if (instance == null) {
                    instance = new GlobalContainer();
                }
            }
        }
        return instance;
    }

    public Stage getStage() {
        return window;
    }

    public void setStage(Stage window) {
        this.window = window;
    }

    public void switchScene(int sceneId) {
        if (window == null) {
            return;
        }
        try {
            window.setScene(sceneManager.getScene(sceneId));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void show() {
        window.show();
    }
}
