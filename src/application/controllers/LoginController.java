package application.controllers;

import java.io.File;

import application.Main;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import util.CryptographyService;
import util.User;

public class LoginController {
	
	private Stage stage;
	private Scene scene;
	
	private File certificate;
	
	private BorderPane pane = new BorderPane();
	private VBox vBox = new VBox(10);
	
	private Label loginLabel = new Label("Login");
	private Label usernameLabel = new Label("Username");
	private Label passwordLabel = new Label("Password");
	private Label emptyLabel = new Label("");
	
	private Button submitButton = new Button("Login");
	private Button registerButton = new Button("Signup");
	private Button uploadCertificateButton = new Button("Upload cerificate");
	
	private ButtonBar buttonBar = new ButtonBar();
	
	private TextField usernameTextField = new TextField();
	private TextField certificateTextField = new TextField();
	private PasswordField passwordTextField = new PasswordField();
	
	public LoginController(Stage stage) {
		this.stage = stage;
		this.certificate = null;
		initialize();
	}
	
	private void initialize() {
		submitButton.setFont(new Font("Georgia", 16));
		submitButton.setPadding(new Insets(10, 10, 10, 10));
		submitButton.setBackground(new Background(new BackgroundFill(Color.rgb(152, 251, 152),  CornerRadii.EMPTY, Insets.EMPTY)));
		submitButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				String username = usernameTextField.getText();
				String password = passwordTextField.getText();
				String certificatePath;
				if(certificate != null) {
					certificatePath = certificate.getAbsolutePath();
				} else {
					certificatePath = "";
				}
				try {
					boolean validation = CryptographyService.validateUserParameters(username, password, certificatePath);
					if(validation) {
						Main.currentUser = new User(username, password, certificatePath);
						HomePageController homePage = new HomePageController(stage);
						homePage.show();
					} else {
						emptyLabel.setVisible(true);
					}
				} catch (Exception ex) {
					//ex.printStackTrace();
					emptyLabel.setText(ex.getMessage());
					emptyLabel.setVisible(true);
				}
			}
		});
		
		registerButton.setFont(new Font("Georgia", 16));
		registerButton.setPadding(new Insets(10, 10, 10, 10));
		registerButton.setBackground(new Background(new BackgroundFill(Color.rgb(255, 218, 185),  CornerRadii.EMPTY, Insets.EMPTY)));
		registerButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				SignUpController signup = new SignUpController();
				signup.show();
			}
		});
		
		uploadCertificateButton.setFont(new Font("Georgia", 16));
		uploadCertificateButton.setPadding(new Insets(10, 10, 10, 10));
		uploadCertificateButton.setBackground(new Background(new BackgroundFill(Color.rgb(230, 230, 250),  CornerRadii.EMPTY, Insets.EMPTY)));
		uploadCertificateButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Choose certificate");
				fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("certificates", "*.p12")); // PROMIJENITI NA PRAVU EKSTENZIJU
				fileChooser.setInitialDirectory(new File(System.getProperty("user.dir") + File.separator + "src" + File.separator + "users" + File.separator + "certificates"));
				certificate = fileChooser.showOpenDialog(stage);
				if(certificate != null) {
					certificateTextField.setPromptText(certificate.getName());
				}
			}
		});
		
		buttonBar.setPadding(new Insets(10));
		ButtonBar.setButtonData(submitButton, ButtonData.LEFT);
        ButtonBar.setButtonData(registerButton, ButtonData.RIGHT);
        buttonBar.getButtons().addAll(submitButton, registerButton);
		
		loginLabel.setFont(new Font("Georgia", 40));
		usernameLabel.setFont(new Font("Georgia", 16));
		passwordLabel.setFont(new Font("Georgia", 16));
		
		usernameTextField.setFont(new Font("Georgia", 16));
		usernameTextField.setMaxSize(200, 50);
		usernameTextField.setPrefSize(200, 50);
		
		certificateTextField.setFont(new Font("Georgia", 16));
		certificateTextField.setMaxSize(200, 50);
		certificateTextField.setPrefSize(200, 50);
		
		passwordTextField.setMaxSize(200, 50);
		passwordTextField.setPrefSize(200, 50);
		
		emptyLabel.setFont(new Font("Georgia", 15));
		emptyLabel.setStyle("-fx-background-color: white; -fx-text-fill: red;");
		emptyLabel.setVisible(false);
		
		vBox.setPadding(new Insets(10, 10, 10, 10));
		vBox.getChildren().addAll(usernameLabel, usernameTextField, passwordLabel, passwordTextField, certificateTextField, uploadCertificateButton, emptyLabel, buttonBar);
		vBox.setPrefSize(400, 400);
		vBox.setMaxSize(400, 400);
		
		BorderPane.setAlignment(vBox, Pos.CENTER);
		BorderPane.setAlignment(loginLabel, Pos.TOP_CENTER);
		
		pane.setCenter(vBox);
		pane.setTop(loginLabel);
		pane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
		
		this.scene = new Scene(pane, 800, 600);
	}
	
	public void show() {
		stage.setScene(this.scene);
		stage.setResizable(false);
		stage.setTitle("CryptoQuiz");
		stage.show();
	}
}
