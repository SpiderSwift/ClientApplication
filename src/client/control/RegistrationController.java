package client.control;

import client.logic.Client;
import client.logic.GlobalContainer;
import client.view.SceneManager;
import client.logic.ClientDispatcherDelegate;
import com.messages.IntroduceMessage;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import java.net.InetAddress;
import java.net.Socket;

public class RegistrationController {

    public Button buttonConnect;
    public TextField nameEdit;
    public TextField serverAddressEdit;
    public TextField portEdit;


    public void buttonConnectReleased() {
        try {
            GlobalContainer.getInstance().switchScene(SceneManager.MAIN_SCREEN_SCENE);
            Client client = new Client(new Socket(InetAddress.getByName(serverAddressEdit.getText()), Integer.parseInt(portEdit.getText())));
            client.setDispatcherDelegate(new ClientDispatcherDelegate(client));
            client.start();
            GlobalContainer.getInstance().setClient(client);
            GlobalContainer.getInstance().getClient().setClientName(nameEdit.getText());
            client.sendMessage(new IntroduceMessage(null, nameEdit.getText()));


        } catch (Exception e) {
            e.printStackTrace();
            GlobalContainer.getInstance().switchScene(SceneManager.LOGIN_SCREEN_SCENE);
        }
    }
}
