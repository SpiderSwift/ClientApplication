package client.logic;

import com.messages.BaseMessage;
import com.messages.MessageDispatcherDelegate;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client extends Thread {


    private ObjectInputStream in;
    private ObjectOutputStream out;
    private MessageDispatcherDelegate delegate;
    private Socket socket;
    private boolean isRunning;
    private String name;
    private String publicKey;
    private String privateKey;

    public Client(Socket socket) throws IOException {
        this.socket = socket;
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
    }
    public String getClientName() {
        return name;
    }

    public void setClientName(String name) {
        this.name = name;
    }

    public String getPublicKey() {
        return publicKey;
    }


    @Override
    public void run() {
        System.out.println("connection: " + socket.getInetAddress() + ":" + socket.getLocalPort());
        isRunning = true;
        while (isRunning) {
            try {
                BaseMessage message = (BaseMessage) in.readObject();
                delegate.onMessageReceived(message);
                Thread.sleep(10);
            } catch (InterruptedException | IOException | ClassNotFoundException e) {
                e.printStackTrace();
                isRunning = false;
            }
        }
        try {
            if (in != null)
                in.close();
            if (out != null) {
                out.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setDispatcherDelegate(MessageDispatcherDelegate delegate) {
        this.delegate = delegate;
    }

    public void sendMessage(BaseMessage message) throws IOException {
        out.writeObject(message);
    }
}
