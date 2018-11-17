package de.domjos.schooltools.core.model.learningCard;

public class LearningCardQueryResult {
    private LearningCard learningCard;
    private LearningCardQuery learningCardQuery;
    private String try1;
    private String try2;
    private String try3;
    private boolean result1;
    private boolean result2;
    private boolean result3;
    private double resultWhole;

    public LearningCardQueryResult() {
        this.learningCard = null;
        this.learningCardQuery = null;
        this.try1 = "";
        this.try2 = "";
        this.try3 = "";
        this.result1 = false;
        this.result2 = false;
        this.result3 = false;
        this.resultWhole = 0.0;
    }

    public LearningCard getLearningCard() {
        return this.learningCard;
    }

    public void setLearningCard(LearningCard learningCard) {
        this.learningCard = learningCard;
    }

    public LearningCardQuery getLearningCardQuery() {
        return this.learningCardQuery;
    }

    public void setLearningCardQuery(LearningCardQuery learningCardQuery) {
        this.learningCardQuery = learningCardQuery;
    }

    public String getTry1() {
        return this.try1;
    }

    public void setTry1(String try1) {
        this.try1 = try1;
    }

    public String getTry2() {
        return this.try2;
    }

    public void setTry2(String try2) {
        this.try2 = try2;
    }

    public String getTry3() {
        return this.try3;
    }

    public void setTry3(String try3) {
        this.try3 = try3;
    }

    public boolean isResult1() {
        return this.result1;
    }

    public void setResult1(boolean result1) {
        this.result1 = result1;
    }

    public boolean isResult2() {
        return this.result2;
    }

    public void setResult2(boolean result2) {
        this.result2 = result2;
    }

    public boolean isResult3() {
        return this.result3;
    }

    public void setResult3(boolean result3) {
        this.result3 = result3;
    }

    public double getResultWhole() {
        return this.resultWhole;
    }

    public void setResultWhole(double resultWhole) {
        this.resultWhole = resultWhole;
    }
}
