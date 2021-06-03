package ru.geekbrains.chat.client;


import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ru.geekbrains.april_chat.common.ChatMessage;
import ru.geekbrains.april_chat.common.MessageType;
import ru.geekbrains.chat.client.network.ChatMessageService;
import ru.geekbrains.chat.client.network.ChatMessageServiceImpl;
import ru.geekbrains.chat.client.network.MessageProcessor;


import javax.xml.xpath.XPath;
import java.awt.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainChatController implements Initializable, MessageProcessor {

    private static final String PUBLIC = "PUBLIC";
    public TextArea chatArea;
    public ListView onlineUsers;
    public TextField inputField;
    public Button btnSendMessage;
    public TextField loginField;
    public PasswordField passwordField;
    public Button btnSendAuth;
    private ChatMessageService messageService;
    private String currentName;

    public void mockAction(ActionEvent actionEvent) {
       try {
           throw new RuntimeException("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA!!!!");
       } catch (RuntimeException e) {
           showError(e);
       }
    }

    public void exit(ActionEvent actionEvent) {
       // saveToLogfileHistory();


        Platform.exit();
    }
    private void saveToLogfileHistory(String msgTextSaveToLog) {
        String logfile=currentName+"_history.txt";

        String str = null;
        String[] lines;
        BufferedWriter bw=null;



        try {
            bw = new BufferedWriter(new FileWriter(logfile,true));
//            str = chatArea.getText();
//            lines = chatArea.getText().split("\\n");
//            for (int i = 0; i < lines.length ; i++) {
//                bw.write(lines[i]+"\n");
//
//            }
            bw.write(msgTextSaveToLog);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
               // if (br!=null) br.close();
                if (bw!=null) bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    public void showHelp(ActionEvent actionEvent) throws URISyntaxException, IOException {
        Desktop desktop = Desktop.getDesktop();
        desktop.browse(new URI("https://docs.google.com/document/d/1wr0YEtIc5yZtKFu-KITqYnBtp8KC28v2FEYUANL0YAM/edit?usp=sharing"));
    }

    public void sendMessage(ActionEvent actionEvent) {
        String text = inputField.getText();
        if (text.isEmpty()) return;
        ChatMessage msg = new ChatMessage();
        String addressee = (String) this.onlineUsers.getSelectionModel().getSelectedItem();
        if (addressee.equals(PUBLIC)) msg.setMessageType(MessageType.PUBLIC);
        else {
            msg.setMessageType(MessageType.PRIVATE);
            msg.setTo(addressee);
        }

        msg.setFrom(currentName);
        msg.setBody(text);
        messageService.send(msg.marshall());
        chatArea.appendText(String.format("[ME] %s\n", text));
        saveToLogfileHistory(String.format("[ME] %s\n", text));
        inputField.clear();
    }

    private void appendTextToChatArea(ChatMessage msg) {
        if (msg.getFrom().equals(this.currentName)) return;
        String modifier = msg.getMessageType().equals(MessageType.PUBLIC) ? "[pub]" : "[priv]";
        String text = String.format("[%s] %s %s\n", msg.getFrom(), modifier, msg.getBody());
        chatArea.appendText(text);
        saveToLogfileHistory(text);
    }

    private void showError(Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Something went wrong!");
        alert.setHeaderText(e.getMessage());

        VBox dialog = new VBox();
        Label label = new Label("Trace:");
        TextArea textArea = new TextArea();

        StringBuilder builder = new StringBuilder();
        for (StackTraceElement el : e.getStackTrace()) {
            builder.append(el).append(System.lineSeparator());
        }
        textArea.setText(builder.toString());
        dialog.getChildren().addAll(label, textArea);
        alert.getDialogPane().setContent(dialog);
        alert.showAndWait();
    }

    private void showError(ChatMessage msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Something went wrong!");
        alert.setHeaderText(msg.getMessageType().toString());
        VBox dialog = new VBox();
        Label label = new Label("Error:");
        TextArea textArea = new TextArea();
        textArea.setText(msg.getBody());
        dialog.getChildren().addAll(label, textArea);
        alert.getDialogPane().setContent(dialog);
        alert.showAndWait();
    }

    public void showAbout(ActionEvent event) {
        Label secondLabel = new Label("Copyright:\nAlex Grigorev\ngb.ru\n2021");

        StackPane secondaryLayout = new StackPane();
        secondaryLayout.getChildren().add(secondLabel);

        Scene aboutScene = new Scene(secondaryLayout, 230, 100);

        Stage aboutWindow = new Stage();
        aboutWindow.setTitle("About");
        aboutWindow.setScene(aboutScene);

        Stage aboutStage = new Stage();
        aboutWindow.setX(aboutStage.getX() + 200);
        aboutWindow.setY(aboutStage.getY() + 100);

        aboutWindow.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.messageService = new ChatMessageServiceImpl("localhost", 12256, this);
//        this.messageService.connect();

    }

    private void loadHistoryFromLogFile() {

        String logfile=this.currentName+"_history.txt";
        File file = new File(logfile);
        try {
            if (!file.canRead()) file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String str;

        BufferedReader br = null;
        ArrayList<String> lines = new ArrayList<String>();

            try{
                br = new BufferedReader(new InputStreamReader(new FileInputStream(logfile)));
                while((str = br.readLine())!= null) {
                    lines.add(str);


                }
                int startHistory;
                if (lines.size()>100) startHistory=lines.size()-100;
                else startHistory=0;

                for (int i = startHistory; i < lines.size(); i++) {
                    chatArea.appendText(lines.get(i)+"\n");
                }
                //chatArea.appendText(str+"\n");
            } catch ( IOException e) {
                e.printStackTrace();
            }
            finally {
                try {
                    if (br!=null) br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }







    @Override
    public void processMessage(String msg) {
        Platform.runLater(() -> {
                    ChatMessage message = ChatMessage.unmarshall(msg);
                    System.out.println("Received message");

                    switch (message.getMessageType()) {
                        case PRIVATE:
                        case PUBLIC:
                            appendTextToChatArea(message);
                            break;
                        case CLIENT_LIST:
                            refreshOnlineUsers(message);
                            break;
                        case AUTH_CONFIRM: {
                            this.currentName = message.getBody();
                            App.stage1.setTitle(currentName);
                            loadHistoryFromLogFile();
                            break;
                        }
                        case ERROR:
                            showError(message);
                            break;
                    }
                }
        );
    }


    private void refreshOnlineUsers(ChatMessage message) {
        message.getOnlineUsers().add(0, PUBLIC);
        this.onlineUsers.setItems(FXCollections.observableArrayList(message.getOnlineUsers()));
        this.onlineUsers.getSelectionModel().selectFirst();
    }

    public void sendAuth(ActionEvent actionEvent) {
        try {
            if (!messageService.isConnected()) messageService.connect();
        } catch (Exception e) {
            showError(e);
        }

        String log = loginField.getText();
        String pass = passwordField.getText();
        if (log.isEmpty() || pass.isEmpty()) return;
        ChatMessage msg = new ChatMessage();
        msg.setMessageType(MessageType.SEND_AUTH);
        msg.setLogin(log);
        msg.setPassword(pass);
        messageService.send(msg.marshall());
    }
}
