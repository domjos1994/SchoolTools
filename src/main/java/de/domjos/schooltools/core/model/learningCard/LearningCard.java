package de.domjos.schooltools.core.model.learningCard;

import de.domjos.schooltools.core.model.objects.BaseObject;

public class LearningCard extends BaseObject {
    private String category;
    private String question;
    private String answer;
    private String note1;
    private String note2;
    private int priority;

    public LearningCard() {
        super();
        this.category = "";
        this.question = "";
        this.answer = "";
        this.note1 = "";
        this.note2 = "";
        this.priority = 0;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getQuestion() {
        return this.question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return this.answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getNote1() {
        return this.note1;
    }

    public void setNote1(String note1) {
        this.note1 = note1;
    }

    public String getNote2() {
        return this.note2;
    }

    public void setNote2(String note2) {
        this.note2 = note2;
    }

    public int getPriority() {
        return this.priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
