package application.controllers;

import application.Main;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import util.CryptographyService;
import util.Question;

public class HomePageController {
	
	private Stage stage;
	private Scene scene;
	
	private BorderPane pane = new BorderPane();
	private VBox vbox = new VBox(10);
	
	private Button startQuizButton = new Button("Play");
	private Button showResultsButton = new Button("Show results");
	private Button endButton = new Button("Quit");
	
	public HomePageController(Stage stage) {
		this.stage = stage;
		initialize();
	}
	
	private void initialize() {
		startQuizButton.setFont(new Font("Georgia", 16));
		startQuizButton.setPrefSize(150, 40);
		startQuizButton.setMaxSize(150, 40);
		startQuizButton.setPadding(new Insets(10, 10, 10, 10));
		startQuizButton.setBackground(new Background(new BackgroundFill(Color.rgb(152, 251, 152),  CornerRadii.EMPTY, Insets.EMPTY)));
		startQuizButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				// checks
				int number = CryptographyService.incrementNumberOfPlays(Main.currentUser.getUsername());
				if(number != -1 && number < 4) {
					if(number == 3) {
						CryptographyService.revokeCertificate(Main.currentUser);
					}
					Main.getSessionQuestions();
					Question nextQuestion = Main.getNextQuestion();
					if(nextQuestion.getAnswerList().size() != 0) {
						QuestionListAnswerController controller = new QuestionListAnswerController(stage, nextQuestion);
						controller.show();
					} else {
						QuestionAnswerController controller = new QuestionAnswerController(stage, nextQuestion);
						controller.show();
					}
				} else {
					LoginController controller = new LoginController(stage);
					controller.show();
				}
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
		vbox.getChildren().addAll(startQuizButton, showResultsButton, endButton);
		vbox.setPrefSize(200, 200);
		vbox.setMaxSize(200, 200);
		
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
