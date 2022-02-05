package application.controllers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import util.CryptographyService;

public class SignUpController {
	
	private Stage stage;
	private Scene scene;
	
	private BorderPane pane = new BorderPane();
	private VBox vbox = new VBox(10);
	
	private Label signUpLabel = new Label("Sign Up");
	
	private Label name = new Label("Name");
	private Label surname = new Label("Surname");
	private Label username = new Label("Username");
	private Label password = new Label("Password");
	private Label emptyLabel = new Label("");
	
	private TextField nameTextField = new TextField();
	private TextField surnameTextField = new TextField();
	private TextField usernameTextField = new TextField();
	private PasswordField passwordTextField = new PasswordField();
	
	private Button submitButton = new Button("Submit");
	
	public SignUpController() {
		initialize();
	}
	
	private void initialize() {
		signUpLabel.setFont(new Font("Georgia", 40));
		
		name.setFont(new Font("Georgia", 16));
		surname.setFont(new Font("Georgia", 16));
		username.setFont(new Font("Georgia", 16));
		password.setFont(new Font("Georgia", 16));
		
		emptyLabel.setFont(new Font("Georgia", 15));
		emptyLabel.setPrefWidth(500);
		emptyLabel.setMaxWidth(500);
		emptyLabel.setMinWidth(500);
		emptyLabel.setVisible(false);
		
		nameTextField.setFont(new Font("Georgia", 16));
		nameTextField.setMaxSize(200, 50);
		nameTextField.setPrefSize(200, 50);
		
		surnameTextField.setFont(new Font("Georgia", 16));
		surnameTextField.setMaxSize(200, 50);
		surnameTextField.setPrefSize(200, 50);
		
		usernameTextField.setFont(new Font("Georgia", 16));
		usernameTextField.setMaxSize(200, 50);
		usernameTextField.setPrefSize(200, 50);
		
		passwordTextField.setMaxSize(200, 50);
		passwordTextField.setPrefSize(200, 50);
		
		submitButton.setFont(new Font("Georgia", 16));
		submitButton.setPadding(new Insets(10, 10, 10, 10));
		submitButton.setBackground(new Background(new BackgroundFill(Color.rgb(152, 251, 152),  CornerRadii.EMPTY, Insets.EMPTY)));
		submitButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				try {
					String name = nameTextField.getText();
					String surname = surnameTextField.getText();
					String username = usernameTextField.getText();
					String password = passwordTextField.getText();
					if(name.trim().isEmpty() || surname.trim().isEmpty() || username.trim().isEmpty()  || password.isEmpty()) {
						System.out.println("Greska");
						emptyLabel.setText("Niste unijeli sve potrebne parametre!");
						emptyLabel.setStyle("-fx-background-color: white; -fx-text-fill: red;");
						emptyLabel.setVisible(true);
					} else {
						String path = CryptographyService.generateCertificate(name, surname, username, password);
						
						NotificationWindowController notificationWindow = new NotificationWindowController(path);
						notificationWindow.show();
					}
				} catch (Exception ex) {
					emptyLabel.setText(ex.getMessage());
					emptyLabel.setVisible(true);
				}
			}
		});
		
		
		vbox.setPadding(new Insets(10, 10, 10, 10));
		vbox.getChildren().addAll(name, nameTextField, surname, surnameTextField, username, usernameTextField, password, passwordTextField, emptyLabel, submitButton);
		vbox.setMinSize(500, 500);
		vbox.setPrefSize(500, 500);
		vbox.setMaxSize(500, 500);
		
		BorderPane.setAlignment(vbox, Pos.CENTER);
		BorderPane.setAlignment(signUpLabel, Pos.TOP_CENTER);
		//pane.setVisible(true);
		
		pane.setCenter(vbox);
		pane.setTop(signUpLabel);
		pane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
		
		this.scene = new Scene(pane, 800, 600);
	}
	
	public void show() {
		this.stage = new Stage();
		stage.setScene(this.scene);
		stage.setResizable(false);
		stage.setTitle("CryptoQuiz");
		stage.show();
	}
}
