package lesson170713;

import javax.swing.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import static lesson170713.Messenger.userName;

public class ChatServer {

    private static final int DEFAULT_PORT = 10000;
    private static Map<String, ChatSession> sessions;
    private static ExecutorService broadcastService;

    public static void main(String[] args) {

        System.out.println("start");

        sessions = new HashMap<>();

        broadcastService = Executors.newCachedThreadPool();

        try {
            ServerSocket serverSocket = new ServerSocket(DEFAULT_PORT);

            JOptionPane jOptionPane = new JOptionPane();

            while (true) {
                Socket socket = serverSocket.accept(); // waiting for connection
                System.out.println("Got connection " + socket);
                new Thread(() -> {  // closure
                    while (true) {
                         userName = JOptionPane.showInputDialog(
                                jOptionPane,
                                "<html><h2>Добро пожаловать");
                         if (!userName.equals("") && !sessions.containsKey(userName)){
                             break;
                         }
                        JOptionPane.showMessageDialog(jOptionPane,
                                "Ошибка ввода","Внимание", JOptionPane.WARNING_MESSAGE);
                    }
                    ChatSession chatSession = new ChatSession(socket, userName);


                    broadcastUserName(chatSession);

                    sessions.put(userName, chatSession);
                    sendNameList2Client(chatSession);
                    userName = "";
                    System.out.println("Sessions size = " + sessions.size());
                    chatSession.processConnection(
                            ChatServer::broadcast,
                            ChatServer::removeSession);
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void broadcastUserName(ChatSession chatSession) {
        String command = "/add " + chatSession.getName();
        broadcast(command);
    }

    private static void sendNameList2Client(ChatSession chatSession) {
        String nameList = "/list";
        for (Map.Entry<String, ChatSession> s: sessions.entrySet()) {

            nameList += " " + s.getKey();

        }
        chatSession.send2Client(nameList);
    }

    private static void broadcast(String line) {
        for (Map.Entry<String, ChatSession> session: sessions.entrySet()) {
            broadcastService.execute( () -> {
                session.getValue().send2Client(line);
            });
        }
    }

    private static void removeSession(ChatSession session) {
        sessions.remove(session);
        broadcast("/remove " + session.getName());
        System.out.println("removed " + session);
        System.out.println("Sessions size = " + sessions.size());
    }
}