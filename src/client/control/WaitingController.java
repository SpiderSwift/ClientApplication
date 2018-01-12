package client.control;

import client.logic.GlobalContainer;
import com.messages.InterruptMessage;

import java.io.IOException;

public class WaitingController {
    public void interrupt() {
        GlobalContainer.getInstance().getWaitingStage().close();
        InterruptMessage message = new InterruptMessage(GlobalContainer.getInstance().getCallingId());
        try {
            GlobalContainer.getInstance().getClient().sendMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        GlobalContainer.getInstance().setCallingId(null);
    }

}
