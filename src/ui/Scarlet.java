package ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import networking.Client;
import networking.messages.Credentials;
import networking.messages.Consignment;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Scarlet extends Application {
    // ------------SCENE-CONSTANTS---------------
    private final double prefWidth = 500.0;
    private final double prefHeight = 350.0;
    private final double minWidth = 350.0;
    private final double minHeight = 300.0;

    private final KeyCombination newLineKey = new KeyCodeCombination(KeyCode.ENTER, KeyCombination.SHIFT_DOWN);
    // ------------------------------------------

    // ------------NECESSARY-HANDLES-------------
    private VBox messagesPane;        // for adding messages
    private TextField loginTextField; // for reading login name
    private Client client;            // for performing I/O operations with server

    private final Thread mainThread = Thread.currentThread();
    // ------------------------------------------

    // --------------ABOUT-SERVER----------------
    private final String host = "localhost";
    private final Integer port = 6666;
    // ------------------------------------------

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // --------APPLICATION-STARTING-POINT--------
        Scene mainScene = buildMainScene();
        Scene loginScene = buildLoginScene(primaryStage, mainScene);

        primaryStage.setMinWidth(minWidth);
        primaryStage.setMinHeight(minHeight);

        primaryStage.setScene(loginScene);
        primaryStage.show();
        // ------------------------------------------
    }

    private Scene buildMainScene() {
        BorderPane borderPane = new BorderPane();
        borderPane.setPrefWidth(prefWidth);
        borderPane.setPrefHeight(prefHeight);

        messagesPane = new VBox();
        messagesPane.setSpacing(10);

        ScrollPane scrollableMessagePane = new ScrollPane();
        scrollableMessagePane.setContent(messagesPane);
        scrollableMessagePane.setFitToWidth(true);

        messagesPane.heightProperty().addListener((observable,oldValue,newValue) -> scrollableMessagePane.setVvalue((Double)newValue ));

        TextArea textArea = new TextArea();
        textArea.setPrefHeight(25);
        textArea.setOnKeyPressed(e -> {
            if (newLineKey.match(e)) {
                textArea.appendText("\n");
            } else if (e.getCode().equals(KeyCode.ENTER)) {
                if (!textArea.getText().isEmpty()) {
                    if (!client.isClosed()) {
                        client.send(new Consignment(loginTextField.getText(), getCurrentDate(), textArea.getText()));
                        textArea.clear();
                    }
                }
            }
        });

        borderPane.setCenter(scrollableMessagePane);
        borderPane.setBottom(textArea);

        Scene mainScene = new Scene(borderPane);
        mainScene.getStylesheets().clear();
        mainScene.getStylesheets().add(getClass().getResource("/css/scarlet.css").toExternalForm());

        return mainScene;
    }

    private Scene buildLoginScene(Stage stage,Scene mainScene){
        GridPane gridPane = new GridPane();
        gridPane.setMinSize(minWidth,minHeight);
        gridPane.setAlignment(Pos.CENTER);

        gridPane.setPadding(new Insets(10,10,10,10));

        gridPane.setHgap(10);
        gridPane.setVgap(10);

        gridPane.getStylesheets().add(getClass().getResource("/css/login.css").toExternalForm());

        loginTextField = new TextField("username");
        GridPane.setConstraints(loginTextField,1,0);

        Label loginLabel = new Label("Login:");
        loginLabel.getStyleClass().add("login-label");
        GridPane.setConstraints(loginLabel,0,0);

        PasswordField passwordTextField = new PasswordField();
        passwordTextField.setText("password");
        GridPane.setConstraints(passwordTextField,1,1);

        loginTextField.focusedProperty().addListener(((observable, oldValue, newValue) -> {
            loginTextField.selectAll();
            loginTextField.setStyle("-fx-text-fill: #000000");
            passwordTextField.setStyle("-fx-text-fill: #000000");
        }));

        passwordTextField.focusedProperty().addListener(((observable, oldValue, newValue) -> {
            passwordTextField.selectAll();
            loginTextField.setStyle("-fx-text-fill: #000000");
            passwordTextField.setStyle("-fx-text-fill: #000000");
        }));

        Label passwordLabel = new Label("Password:");
        passwordLabel.getStyleClass().add("login-label");
        GridPane.setConstraints(passwordLabel,0,1);

        Button loginButton = new Button("Login");
        loginButton.getStyleClass().add("login-button");
        GridPane.setConstraints(loginButton,0,3);

        /*Button addButton = new Button("Add");
        addButton.getStyleClass().add("login-button");
        GridPane.setConstraints(addButton,1,3);*/

        loginButton.setOnAction(e -> {
            establishConnection();
            if (validateUser(loginTextField.getText(), passwordTextField.getText())) {
                stage.setScene(mainScene);
            } else {
                loginTextField.setStyle("-fx-text-fill: #ff0000");
                passwordTextField.setStyle("-fx-text-fill: #ff0000");
            }
        });

        //gridPane.getChildren().addAll(loginTextField, loginLabel, passwordTextField, passwordLabel, loginButton, addButton);
        gridPane.getChildren().addAll(loginTextField, loginLabel, passwordTextField, passwordLabel, loginButton);

        Scene loginScene = new Scene(gridPane);
        return loginScene;
    }

    public void addMessage(Message message) {
        Platform.runLater( () -> messagesPane.getChildren().add(message));
    }

    private String getCurrentDate() {
        return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
    }

    private void establishConnection() {
        client = new Client(host, port, this);
    }

    private boolean validateUser(String username, String password) {
        client.send(new Credentials(username, password));

        synchronized (mainThread) {
            try {
                mainThread.wait();
            } catch (InterruptedException e) {
                // pass
            }
        }
        return !client.isClosed();
    }

    public Thread getMainThread() {
        return mainThread;
    }
}

