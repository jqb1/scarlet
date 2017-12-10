package ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Scarlet extends Application{
    // ------------SCENE-CONSTANTS---------------
    private final double prefWidth = 500.0;
    private final double prefHeight = 350.0;
    private final double minWidth = 350.0;
    private final double minHeight = 300.0;

    private final KeyCombination toggleKey = new KeyCodeCombination(KeyCode.T, KeyCombination.CONTROL_DOWN);
    private final KeyCombination newLineKey = new KeyCodeCombination(KeyCode.ENTER, KeyCombination.SHIFT_DOWN);
    //private final KeyCombination biggerKey = new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN);
    //private final KeyCombination smallerKey = new KeyCodeCombination(KeyCode.MINUS, KeyCombination.CONTROL_DOWN);
    // ------------------------------------------

    private VBox messagesPane;
    private TextArea textArea;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // --------APPLICATION-STARTING-POINT--------
        Scene mainScene = buildMainScene();
        Scene sideScene = buildSideScene();

        bindToToggle(primaryStage, mainScene, sideScene, toggleKey);

        primaryStage.setMinWidth(minWidth);
        primaryStage.setMinHeight(minHeight);

        primaryStage.setScene(mainScene);
        primaryStage.show();
        // ------------------------------------------
    }

    private Scene buildMainScene() {
        BorderPane borderPane = new BorderPane();
        borderPane.setPrefWidth(prefWidth);
        borderPane.setPrefHeight(prefHeight);
        borderPane.getStyleClass().add("main-scene");

        messagesPane = new VBox();
        messagesPane.setSpacing(10);
        // TEMPORARY
        //addMessage(generateSampleMessage());
        // ---------
        messagesPane.getStyleClass().add("message-pane");
        ScrollPane scrollableMessagePane = new ScrollPane();
        scrollableMessagePane.setContent(messagesPane);
        scrollableMessagePane.setFitToWidth(true);

        textArea = new TextArea();
        textArea.setPrefHeight(25);
        textArea.setOnKeyPressed(e -> {
            if (newLineKey.match(e)) {
                textArea.appendText("\n");
            } else if (e.getCode().equals(KeyCode.ENTER)) {
                if (!textArea.getText().isEmpty()) {
                    addMessage(new Message("Kamil", getCurrentDate(),textArea.getText()));
                    textArea.clear();
                }
            } /*else if (biggerKey.match(e)) {
                System.out.println("New");
                for (Node node : messagesPane.getChildren()) {
                    if (node instanceof Message) {
                        Message message = (Message) node;
                        for (Node node2 : message.getChildren()) {
                            if (node2 instanceof Label) {
                                Label label = (Label) node;

                                double newSize = label.getFont().getSize() + 1;
                                System.out.println("New");
                                label.setStyle("-fx-font-size: " + newSize + ";");
                            }
                        }
                    }
                }
            } else if (smallerKey.match(e)) {

            }*/
        });
        //textArea.focusedProperty().addListener((observable, oldValue, newValue) -> {
        //    if (newValue) textArea.setPrefHeight(40);
        //    else textArea.setPrefHeight(20);
            //if (oldValue) textArea.setPrefHeight(15);
            //if (newValue) textArea.setPrefHeight(15);
        //});

        borderPane.setCenter(scrollableMessagePane);
        borderPane.setBottom(textArea);

        Scene mainScene = new Scene(borderPane);
        mainScene.getStylesheets().clear();
        mainScene.getStylesheets().add(getClass().getResource("/css/scarlet.css").toExternalForm());

        return mainScene;
    }

    private Scene buildSideScene() {
        //BorderPane borderPane = new BorderPane();
        //borderPane.setPrefWidth(prefWidth);
        //borderPane.setPrefHeight(prefHeight);

        //return new Scene(borderPane);

        VBox vBox = new VBox();
        vBox.setPrefWidth(prefWidth);
        vBox.setPrefHeight(prefHeight);

        Scene sideScene =  new Scene(vBox);

        sideScene.getStylesheets().clear();
        sideScene.getStylesheets().add(getClass().getResource("/css/scarlet.css").toExternalForm());

        return sideScene;
    }

    private void bindToToggle(Stage stage, Scene scene_1, Scene scene_2, KeyCombination toggleKey) {
        scene_1.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if (toggleKey.match(e)) stage.setScene(scene_2);
        });
        scene_2.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if (toggleKey.match(e)) stage.setScene(scene_1);
        });
    }

    private void addMessage(Message message) {
        messagesPane.getChildren().add(message);
    }

    private Message generateSampleMessage() {
        String author = "John";
        String date = getCurrentDate();
        String message = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cras vitae venenatis risus. Sed a hendrerit nulla, id ullamcorper justo. Nunc bibendum vulputate nunc, id consequat metus porttitor ac. Praesent euismod sem id ex auctor, ac tempor nunc sodales. Cras pharetra lobortis venenatis. Nam iaculis ornare risus vitae finibus. Suspendisse tincidunt fringilla nulla eu sodales. Nullam eget lectus vel nunc sagittis scelerisque eget et nunc. Fusce mollis pretium metus, malesuada tincidunt turpis sodales ac.";

        return new Message(author, date, message);
    }

    private String getCurrentDate() {
        return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
    }
}