package util;

import java.util.ArrayList;
import java.util.List;

public class Question {
	
	private String question;
	private String answer;
	private List<String> answerList = new ArrayList<>();
	private boolean correct = false;
	
	public Question(String question, String answer, String[] answerList) {
		this.question = question;
		this.answer = answer;
		
		if(answerList != null) {
			for(String a : answerList) {
				this.answerList.add(a);
			}
		}
	}
	
	public String getQuestion() {
		return this.question;
	}
	
	public List<String> getAnswerList(){
		return this.answerList;
	}
	
	public boolean isCorrect() {
		return this.correct;
	}
	
	public void setCorrect(boolean correct) {
		this.correct = correct;
	}
	
	public void checkAnswer(String answer) {
		answer = answer.toLowerCase().trim();
		correct = (this.answer.compareTo(answer) == 0);
	}
	
	@Override
	public boolean equals(Object object) {
		if(object instanceof Question) {
			Question other = (Question)object;
			if(this.question.equals(other.getQuestion())) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return "Q : " + question + "\n" + "A : " + answer;
	}
}
