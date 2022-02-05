package application;

import java.util.List;

import application.controllers.LoginController;
import javafx.application.Application;
import javafx.stage.Stage;
import util.Question;
import util.SteganographyService;
import util.User;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

	@Override
	public void start(Stage primaryStage) throws Exception {
		//SteganographyService.initialize();
		LoginController login = new LoginController(primaryStage);
		login.show();
	}
	
	public static User currentUser = null;
	private static List<Question> sessionQuestions;
	private static int questionID = 0;
		
	public static void getSessionQuestions() {
		sessionQuestions = SteganographyService.getQuestions();
		Main.questionID = 0;
	}
		
	public static Question getNextQuestion() {
		if(questionID < SteganographyService.NUMBER_OF_SESSION_QUESTIONS) {
			Question toReturn = sessionQuestions.get(questionID);
			++questionID;
			return toReturn;
		} else {
			return null;
		}
	}
	
	public static long calculateResult() {
		return sessionQuestions.stream().filter(q -> q.isCorrect()).count();
	}
}