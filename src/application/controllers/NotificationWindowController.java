package application.controllers;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class NotificationWindowController {
	
	private Stage stage;
	private Scene scene;
	
	private BorderPane pane = new BorderPane();
	private Label notification = new Label("");
	
	public NotificationWindowController(String notificationText) {
		notification.setFont(new Font("Georgia", 16));
		notification.setText(notificationText);
		notification.setMinSize(600, 100);
		notification.setMaxSize(600, 100);
		notification.setPrefSize(600, 100);
		
		BorderPane.setAlignment(notification, Pos.CENTER);
		pane.setCenter(notification);
		
		this.scene = new Scene(pane, 600, 100);
	}
	
	public void show() {
		this.stage = new Stage();
		stage.setScene(scene);
		stage.setResizable(false);
		stage.setTitle("CryptoQuiz");
		stage.show();
	}
}
