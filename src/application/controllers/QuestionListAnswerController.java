package application.controllers;

import application.Main;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import util.Question;

public class QuestionListAnswerController {
	
	private Stage stage;
	private Scene scene;
	
	private BorderPane pane = new BorderPane();
	private VBox vbox = new VBox(10);
	
	private TextArea questionText = new TextArea();
	
	private ToggleGroup group = new ToggleGroup();
	private RadioButton answer1 = new RadioButton();
	private RadioButton answer2 = new RadioButton();
	private RadioButton answer3 = new RadioButton();
	private RadioButton answer4 = new RadioButton();
	
	private Label emptyLabel1 = new Label("");
	private Label emptyLabel2 = new Label("");
	
	private Button nextButton = new Button("Next");
	
	private Question question;
	
	public QuestionListAnswerController(Stage stage, Question question) {
		this.stage = stage;
		this.question = question;
		initialize();
	}
	
	private void initialize() {
		questionText.setFont(new Font("Georgia", 25));
		questionText.setText(this.question.getQuestion());
		questionText.setEditable(false);
		questionText.setVisible(true);
		questionText.setMinSize(600, 100);
		questionText.setPrefSize(600, 100);
		questionText.setMaxSize(600, 100);
		questionText.setWrapText(true);
		
		answer1.setFont(new Font("Georgia", 16));
		answer1.setToggleGroup(group);
		answer1.setText(this.question.getAnswerList().get(0));
		
		answer2.setFont(new Font("Georgia", 16));
		answer2.setToggleGroup(group);
		answer2.setText(this.question.getAnswerList().get(1));
		
		answer3.setFont(new Font("Georgia", 16));
		answer3.setToggleGroup(group);
		answer3.setText(this.question.getAnswerList().get(2));
		
		answer4.setFont(new Font("Georgia", 16));
		answer4.setToggleGroup(group);
		answer4.setText(this.question.getAnswerList().get(3));
		
		nextButton.setFont(new Font("Georgia", 16));
		nextButton.setPadding(new Insets(10, 10, 10, 10));
		nextButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Toggle selected = group.getSelectedToggle();
				String answered = "";
				if(selected != null) {
					answered = ((RadioButton)selected).getText();
				}
				question.checkAnswer(answered);
				Question nextQuestion = Main.getNextQuestion();
				if(nextQuestion != null) {
					if(nextQuestion.getAnswerList().size() != 0) {
						QuestionListAnswerController controller = new QuestionListAnswerController(stage, nextQuestion);
						controller.show();
					} else {
						QuestionAnswerController controller = new QuestionAnswerController(stage, nextQuestion);
						controller.show();
					}
				} else {
					FinalPageController controller = new FinalPageController(stage);
					controller.show();
				}
			}
		});
		
		vbox.setPadding(new Insets(10, 10, 10, 10));
		vbox.getChildren().addAll(questionText, emptyLabel1, answer1, answer2, answer3, answer4, emptyLabel2, nextButton);
		vbox.setPrefSize(700, 500);
		vbox.setMaxSize(700, 500);
		vbox.setMinSize(700, 500);
		
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
