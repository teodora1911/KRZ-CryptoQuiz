package application.controllers;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

import application.Main;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import util.CryptographyService;

public class FinalPageController {
	
	private Stage stage;
	private Scene scene;
	
	private BorderPane pane = new BorderPane();
	private VBox vbox = new VBox(10);
	
	private Label resultLabel = new Label("");
	
	private Button homePageButton = new Button("Home Page");
	private Button showResultsButton = new Button("Show results");
	private Button endButton = new Button("Quit");
	
	private Label emptyLabel = new Label("");
	
	public FinalPageController(Stage stage) {
		this.stage = stage;
		initialize();
	}
	
	private void initialize() {
		resultLabel.setFont(new Font("Georgia", 25));
		resultLabel.setText("Vas rezultat je " + Main.calculateResult() + "/5");
		resultLabel.setAlignment(Pos.CENTER);
		
		// write results
		Date dateAndTime = new Date();
		String result = Main.currentUser.getUsername() + "," + dateAndTime + "," + Main.calculateResult();
		String write;
		try {
			byte[] bytes = Files.readAllBytes(Paths.get(CryptographyService.resultsFilePath));
			if(bytes.length != 0) {
				String older = CryptographyService.decryptData(bytes, CryptographyService.resultsDataFilePath);
				write = older + "#" + result;
			} else {
				write = result;
			}
			
			CryptographyService.encryptData(write, CryptographyService.resultsFilePath, CryptographyService.resultsDataFilePath);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		homePageButton.setFont(new Font("Georgia", 16));
		homePageButton.setPrefSize(150, 40);
		homePageButton.setMaxSize(150, 40);
		homePageButton.setPadding(new Insets(10, 10, 10, 10));
		homePageButton.setBackground(new Background(new BackgroundFill(Color.rgb(152, 251, 152),  CornerRadii.EMPTY, Insets.EMPTY)));
		homePageButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				HomePageController homePage = new HomePageController(stage);
				homePage.show();
			}
		});
		
		showResultsButton.setFont(new Font("Georgia", 16));
		showResultsButton.setPrefSize(150, 40);
		showResultsButton.setMaxSize(150, 40);
		showResultsButton.setPadding(new Insets(10, 10, 10, 10));
		showResultsButton.setBackground(new Background(new BackgroundFill(Color.rgb(230, 230, 250),  CornerRadii.EMPTY, Insets.EMPTY)));
		showResultsButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				ResultsViewController controller = new ResultsViewController();
				controller.show();
			}
		});
		
		endButton.setFont(new Font("Georgia", 16));
		endButton.setPrefSize(150, 40);
		endButton.setMaxSize(150, 40);
		endButton.setPadding(new Insets(10, 10, 10, 10));
		endButton.setBackground(new Background(new BackgroundFill(Color.rgb(255, 160, 122),  CornerRadii.EMPTY, Insets.EMPTY)));
		endButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.exit(1);
			}
		});
		
		vbox.setPadding(new Insets(10, 10, 10, 10));
		vbox.setAlignment(Pos.CENTER);
		vbox.getChildren().addAll(resultLabel, emptyLabel, homePageButton, showResultsButton, endButton);
		vbox.setPrefSize(300, 300);
		vbox.setMaxSize(300, 300);
		
		BorderPane.setAlignment(vbox, Pos.CENTER);
		pane.setCenter(vbox);
		
		this.scene = new Scene(pane, 800, 600);
	}
	
	public void show() {
		stage.setScene(this.scene);
		stage.setResizable(false);
		stage.setTitle("CryptoQuiz");
		stage.show();
	}
}
