package client.logic;

import com.messages.*;
import javafx.application.Platform;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;

public class ClientDispatcherDelegate implements MessageDispatcherDelegate{

    private HashMap<String,OnlineClient> onlineClientsList;
    private Client client;

    public ClientDispatcherDelegate(Client client) {
        onlineClientsList = new HashMap<>();
        this.client = client;
    }


    @Override
    public void onMessageReceived(BaseMessage message, String... clientId) {
        switch (message.getType()) {
            case BaseMessage.MSG_TYPE_CLIENT :
                processClientMessage((ClientMessage) message);
                break;
            case BaseMessage.MSG_TYPE_CLIENT_GROUP :
                processClientGroupMessage((ClientGroupMessage) message);
                break;
            case BaseMessage.MSG_TYPE_PUBLIC_KEY_EXCHANGE :
                processPublicKeyExchangeMessage((PublicKeyExchangeMessage) message);
                break;
            case BaseMessage.MSG_TYPE_INTRODUCE :
                processIntroduceMessage((IntroduceMessage) message);
                break;
            case BaseMessage.MSG_TYPE_ERROR :
                processErrorMessage((ErrorMessage) message);
                break;
            case BaseMessage.MSG_TYPE_IN_OUT :
                processInOutMessage((ClientInOutMessage) message);
                break;
            case BaseMessage.MSG_TYPE_AVAILABLE :
                processAvailableMessage((AvailableMessage) message);
                break;
            case BaseMessage.MSG_TYPE_DECISION :
                processDecisionMessage((DecisionMessage) message);
                break;
            case BaseMessage.MSG_TYPE_SESSION :
                processSessionMessage((SessionMessage) message);
                break;
            case BaseMessage.MSG_TYPE_INTERRUPT :
                processInterruptMessage((InterruptMessage) message);
                break;
            case BaseMessage.MSG_TYPE_SESSION_END :
                processSessionEndMessage((SessionEndMessage) message);
                break;

        }
    }

    private void processSessionEndMessage(SessionEndMessage message) {
        GlobalContainer.getInstance().setCallingId(null);
        Platform.runLater(() -> {
            GlobalContainer.getInstance().getWaitingStage().hide();
        });
    }

    private void processInterruptMessage(InterruptMessage message) {
        GlobalContainer.getInstance().setCallingId(null);
        Platform.runLater(() -> {
            GlobalContainer.getInstance().getDecisionStage().hide();
        });
    }

    private void processSessionMessage(SessionMessage message) {
        Platform.runLater(() -> {
            GlobalContainer.getInstance().getWaitingStage().hide();
            GlobalContainer.getInstance().getDialogStage().show();
        });
        System.out.println("my packets will be delivered to " + message.getAddress());
        try {
            DatagramSocket socket = new DatagramSocket(56000);
            GlobalContainer.getInstance().setInSession(true);
            AudioFormat format = getAudioFormat();
            TargetDataLine audioIn = AudioSystem.getTargetDataLine(format);//(TargetDataLine) AudioSystem.getLine(info);
            SourceDataLine audioOut = AudioSystem.getSourceDataLine(format);
            audioOut.open(format);
            audioOut.start();
            audioIn.open(format);
            audioIn.start();
            new Thread(() -> {
                while (GlobalContainer.getInstance().isInSession()) {
                    byte byte_buff[] = new byte[512];
                    audioIn.read(byte_buff, 0, byte_buff.length);
                    DatagramPacket packet = new DatagramPacket(byte_buff, 0, byte_buff.length, message.getAddress(),56000);
                    try {
                        System.out.println("sending a packet!");
                        socket.send(packet);
                        //Thread.sleep(10);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                socket.close();
                audioIn.close();
                audioIn.drain();
            }).start();

            new Thread(() -> {
                while (GlobalContainer.getInstance().isInSession()) {
                    //получение
                    byte byte_buff[] = new byte[512];
                    DatagramPacket packet = new DatagramPacket(byte_buff, 0, byte_buff.length);
                    try {
                        socket.receive(packet);
                        //Thread.sleep(10);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    audioOut.write(byte_buff, 0, byte_buff.length);
                }
                audioOut.close();
                audioOut.drain();
            }).start();

        } catch (SocketException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    private AudioFormat getAudioFormat() {
        float sampleRate = 8000.0F;
        int sampleSizeInBits = 16;
        int channel = 2;
        final boolean signed = true;
        final boolean bigEndian = false;
        return new AudioFormat(sampleRate, sampleSizeInBits, channel, signed, bigEndian);
    }

    private void processDecisionMessage(DecisionMessage message) {
        GlobalContainer.getInstance().setCallingId(message.getReceiverId());
        Platform.runLater(() -> GlobalContainer.getInstance().getDecisionStage().show());
    }

    private void processAvailableMessage(AvailableMessage message) {
        if (message.isAvailable()) {
            Platform.runLater(() -> GlobalContainer.getInstance().getWaitingStage().show());
        } else {
            Platform.runLater(() -> GlobalContainer.getInstance().getErrorStage().show());
            GlobalContainer.getInstance().setCallingId(null);
        }
    }

    private void processInOutMessage(ClientInOutMessage message) {
        if (message.isOnline()) {
            onlineClientsList.put(message.getClientId(),message.getClient());
            GlobalContainer.getInstance().getController().addToList(onlineClientsList.get(message.getClientId()));
            System.out.println(message.getClient().getName() +  " " +  message.getClientId() + " just got here!");
        } else {
            GlobalContainer.getInstance().getController().removeFromList(onlineClientsList.get(message.getClientId()));
            removeClient(message.getClientId());
        }
    }

    private void processErrorMessage(ErrorMessage message) {
        System.out.println("There was an error");
    }

    @Override
    public void removeClient(String clientId) {
        onlineClientsList.remove(clientId);
    }

    private void processClientMessage(ClientMessage message) {
        OnlineClient client = onlineClientsList.get(message.getSenderId());
        client.getMessageHistory().append(client.getName() + ":" + message.getMessageBody() + "\n");
        GlobalContainer.getInstance().getController().onMessageReceived(onlineClientsList.get(message.getSenderId()));
    }

    private void processClientGroupMessage(ClientGroupMessage message) {
        //might not be implemented
    }

    private void processIntroduceMessage(IntroduceMessage message) {
        ArrayList<OnlineClient> clients = message.getClients();
        for (OnlineClient client : clients) {
            onlineClientsList.put(client.getClientId(),client);
            GlobalContainer.getInstance().getController().addToList(client);
            System.out.println(client.getName() + " " + client.getClientId() + " is available");
        }
    }

    private void processPublicKeyExchangeMessage(PublicKeyExchangeMessage message) {
        OnlineClient client = onlineClientsList.get(message.getRecipientId());
        if (client.getPublicKey() == null) {
            client.setPublicKey(message.getKey());
            PublicKeyExchangeMessage publicKeyExchangeMessage = new PublicKeyExchangeMessage(this.client.getPublicKey(), message.getSenderId());
            try {
                this.client.sendMessage(publicKeyExchangeMessage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
