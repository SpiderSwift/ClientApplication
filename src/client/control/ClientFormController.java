
package client.control;

import client.logic.GlobalContainer;
import com.messages.AvailableMessage;
import com.messages.ClientMessage;
import com.messages.OnlineClient;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

import java.io.IOException;

public class ClientFormController {


    public TextArea dialogWindow;
    public TextArea messageField;

    @FXML
    private ListView<OnlineClient> list;


    public void addToList(OnlineClient client) {
        Platform.runLater(() -> list.getItems().add(client));
    }

    public void removeFromList(OnlineClient client) {
        Platform.runLater(() -> list.getItems().remove(client));

    }


    public void sendMessage() {
        OnlineClient client = list.getSelectionModel().getSelectedItem();
        if (client != null) {
            client.getMessageHistory().append(GlobalContainer.getInstance().getClient().getClientName() + ":" + messageField.getText() + "\n");
            dialogWindow.setText(client.getMessageHistory().toString());
            String recipientId = client.getClientId();
            String messageBody = messageField.getText();
            messageField.setText("");
            try {
                GlobalContainer.getInstance().getClient().sendMessage(new ClientMessage(recipientId, messageBody));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void onMessageReceived(OnlineClient client) {
        Platform.runLater(() -> {
            if (list.getItems().indexOf(client) == list.getSelectionModel().getSelectedIndex()) {
                dialogWindow.setText(client.getMessageHistory().toString());
            } else {
                client.onMessageReceived();
                list.getItems().set(list.getItems().indexOf(client), client);
            }
        });

    }

    public void changeMessageField() {
        OnlineClient client = list.getSelectionModel().getSelectedItem();
        client.resetMessageCount();
        dialogWindow.setText(client.getMessageHistory().toString());
        int selectedIndex = list.getSelectionModel().getSelectedIndex();
        list.getItems().set(selectedIndex, client);
        list.getSelectionModel().select(selectedIndex);
    }

    public void callUser() {
        if (GlobalContainer.getInstance().getCallingId() == null) {
            OnlineClient client = list.getSelectionModel().getSelectedItem();
            if (client != null) {
                GlobalContainer.getInstance().setCallingId(client.getClientId());
                AvailableMessage message = new AvailableMessage(false, client.getClientId());
                try {
                    GlobalContainer.getInstance().getClient().sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            GlobalContainer.getInstance().getErrorStage().show();
        }
    }
}
