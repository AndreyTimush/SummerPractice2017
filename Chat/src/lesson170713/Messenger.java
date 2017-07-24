package lesson170713;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.List;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;

public class Messenger {

    static Communicator chat;
    private static JTextArea textArea;
    private static JScrollPane sp;
    private static JList userList;
    static String userName = "";
    private static DefaultListModel<String> contactList;
    private static JFrame frame;

    public static void main(String[] args) {

        frame = new JFrame("Чат");

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        LayoutManager manager = new BorderLayout();

        JPanel panel = new JPanel(manager);
        panel.setPreferredSize(new Dimension(400, 400));

        textArea = new JTextArea();
        textArea.setEditable(false);

        sp = new JScrollPane(textArea);

        panel.add(sp, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel();

        JTextField textField = new JTextField(20);

        textField.addActionListener((e) ->sendText(textField));
        inputPanel.add(textField);

        JButton sendButton = new JButton("Отправить");
        inputPanel.add(sendButton);
        sendButton.addActionListener((e) -> sendText(textField));

        panel.add(inputPanel, BorderLayout.SOUTH);

        contactList = new DefaultListModel<>();
        userList = new JList<>(contactList);

        userList.addMouseListener(new MouseAdapter() {
          @Override
          public void mouseClicked(MouseEvent e) {
              String nameContact = (String) userList.getSelectedValue();
                  textField.setText(nameContact + "  ");
          }}
        );

        panel.add(userList, BorderLayout.WEST);

        frame.add(panel);

//		frame.setSize(400, 400);

        frame.pack();

        frame.setVisible(true);

        chat = new Communicator();

        chat.init(Messenger::processServerMessage);

    }

    private static void sendText(JTextField textField) {
        String[] words = textField.getText().split(" ");
        String userName = words[0];
        String text;
        if(contactList.contains(userName)){
            text="/private "+ textField.getText();
        }else {
            text=textField.getText();
        }
        textField.setText("");
        chat.sendTextToServer(text);
    }

    private static void processServerMessage(String text) {
        if (text.startsWith("/name")) {
            String[] words = text.split(" ");
            String userName = words[1];
            textArea.append("Добро пожаловать в чат, " + userName + "\n");
            return;
        }
        if (text.startsWith("/list")){
            String[] names = text.split(" ");
            for ( int i = 1; i < names.length; i++){
               contactList.addElement(names[i]);
            }
            return;
        }
        if (text.startsWith("/add")) {
            String[] words = text.split(" ");
            String userName = words[1];
            contactList.addElement(userName);
            return;
        }
        if (text.startsWith("/remove")) {
            String[] words = text.split(" ");
            String userName = words[1];
            contactList.removeElement(userName);
            textArea.append("Пользователь " + userName + " покинул чат" + "\n");

            return;
        }
        textArea.append(text + '\n');
		textArea.setCaretPosition(textArea.getDocument().getLength());
		frame.validate();
    }

}