package com.axtel.contratos.core;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * Representa la respuesta al cuestionario de contratacion.
 * 
 * @author vicente.garcia
 *
 */
public class QuestionnaireAnswer {

	private static final Logger log = LogManager.getLogger(QuestionnaireAnswer.class);

	/**
	 * Numero de pregunta
	 */
	private int questionId;
	/**
	 * Respuesta.
	 */
	private String answer;

	/**
	 * Texto de la pregunta.
	 */
	private String question;

	/**
	 * @return the questionId
	 */
	public int getQuestionId() {
		return questionId;
	}

	/**
	 * @param questionId the questionId to set
	 */
	public void setQuestionId(int questionId) {
		this.questionId = questionId;
	}

	/**
	 * @return the answer
	 */
	public String getAnswer() {
		return answer;
	}

	/**
	 * @param answer the answer to set
	 */
	public void setAnswer(String answer) {
		this.answer = answer;
	}

	@Override
	public String toString() {
		return "QuestionnaireAnswer [questionId=" + questionId + ", answer=" + answer + "]";
	}

	/**
	 * Crea una nueva lista de repuestas al cuestionario.
	 * 
	 * @param answersArr
	 * @return
	 */
	public static List<QuestionnaireAnswer> instanceList(String[] answersArr) {

		List<QuestionnaireAnswer> answersList = new ArrayList<QuestionnaireAnswer>();

		for (String answerStr : answersArr) {
			String answerParts[] = answerStr.split("_");

			QuestionnaireAnswer answer = new QuestionnaireAnswer();
			answer.setQuestionId(Integer.parseInt(answerParts[0]));
			answer.setAnswer(answerParts[1]);

			log.trace("Se obtuvo la respuesta: " + answer);

			answersList.add(answer);
		}

		return answersList;
	}

	/**
	 * @param questionId
	 * @param answer
	 */
	public QuestionnaireAnswer(int questionId, String answer) {
		super();
		this.questionId = questionId;
		this.answer = answer;
	}

	/**
	 * 
	 */
	public QuestionnaireAnswer() {
		super();
	}

	/**
	 * @return the question
	 */
	public String getQuestion() {
		return question;
	}

	/**
	 * @param question the question to set
	 */
	public void setQuestion(String question) {
		this.question = question;
	}

}
