package client.control;

import client.logic.GlobalContainer;
import com.messages.SessionEndMessage;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

public class DialogController {

    public void endSession(MouseEvent event) {
        GlobalContainer.getInstance().setInSession(false);
        GlobalContainer.getInstance().getDialogStage().close();
        GlobalContainer.getInstance().setCallingId(null);
        SessionEndMessage message = new SessionEndMessage();
        try {
            GlobalContainer.getInstance().getClient().sendMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
