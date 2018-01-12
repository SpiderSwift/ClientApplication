package client.control;

import client.logic.GlobalContainer;
import com.messages.DecisionMessage;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

public class DecisionController {
    public void accept(MouseEvent event) {
        String callingId = GlobalContainer.getInstance().getCallingId();
        DecisionMessage message = new DecisionMessage(true, callingId);
        try {
            GlobalContainer.getInstance().getClient().sendMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        GlobalContainer.getInstance().getDecisionStage().close();
    }

    public void reject(MouseEvent event) {
        String callingId = GlobalContainer.getInstance().getCallingId();
        DecisionMessage message = new DecisionMessage(false, callingId);
        try {
            GlobalContainer.getInstance().getClient().sendMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        GlobalContainer.getInstance().setInSession(false);
        GlobalContainer.getInstance().setCallingId(null);
        GlobalContainer.getInstance().getDecisionStage().close();
    }
}
