package smartron.entities;

public class Answerkey {
int questionId;
String [] answerKey;

public int getQuestionId() {
	return questionId;
}
public void setQuestionId(int questionId) {
	this.questionId = questionId;
}
public String[] getAnswerKey() {
	return answerKey;
}
public void setAnswerKey(String[] answerKey) {
	this.answerKey = answerKey;
}
}