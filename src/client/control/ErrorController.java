package client.control;

import client.logic.GlobalContainer;
import javafx.application.Platform;
import javafx.scene.input.MouseEvent;

public class ErrorController {
    public void acceptError(MouseEvent event) {
        Platform.runLater(() -> {
            GlobalContainer.getInstance().getErrorStage().close();
        });
        //GlobalContainer.getInstance().setCallingId(null);
    }

}
