package de.domjos.schooltools.core.model.learningCard;

import de.domjos.schooltools.core.model.objects.BaseCategoryObject;

public class LearningCardQuery extends BaseCategoryObject {
    private LeaningCardGroup leaningCardGroup;
    private int priority;
    private int period;
    private int tries;
    private LearningCardQuery wrongCardsOfQuery;
    private boolean periodic;
    private boolean untilDeadLine;
    private boolean answerMustEqual;
    private boolean showNotes;
    private boolean showNotesImmediately;

    public LearningCardQuery() {
        super();
        this.leaningCardGroup = null;
        this.priority = 0;
        this.period = 1;
        this.tries = 1;
        this.wrongCardsOfQuery = null;
        this.periodic = false;
        this.untilDeadLine = false;
        this.answerMustEqual = true;
        this.showNotes = false;
        this.showNotes = false;
        this.showNotesImmediately = false;
    }

    public LeaningCardGroup getLeaningCardGroup() {
        return this.leaningCardGroup;
    }

    public void setLeaningCardGroup(LeaningCardGroup leaningCardGroup) {
        this.leaningCardGroup = leaningCardGroup;
    }

    public int getPriority() {
        return this.priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getPeriod() {
        return this.period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public int getTries() {
        return this.tries;
    }

    public void setTries(int tries) {
        this.tries = tries;
    }

    public LearningCardQuery getWrongCardsOfQuery() {
        return this.wrongCardsOfQuery;
    }

    public void setWrongCardsOfQuery(LearningCardQuery wrongCardsOfQuery) {
        this.wrongCardsOfQuery = wrongCardsOfQuery;
    }

    public boolean isPeriodic() {
        return this.periodic;
    }

    public void setPeriodic(boolean periodic) {
        this.periodic = periodic;
    }

    public boolean isUntilDeadLine() {
        return this.untilDeadLine;
    }

    public void setUntilDeadLine(boolean untilDeadLine) {
        this.untilDeadLine = untilDeadLine;
    }

    public boolean isAnswerMustEqual() {
        return this.answerMustEqual;
    }

    public void setAnswerMustEqual(boolean answerMustEqual) {
        this.answerMustEqual = answerMustEqual;
    }

    public boolean isShowNotes() {
        return this.showNotes;
    }

    public void setShowNotes(boolean showNotes) {
        this.showNotes = showNotes;
    }

    public boolean isShowNotesImmediately() {
        return this.showNotesImmediately;
    }

    public void setShowNotesImmediately(boolean showNotesImmediately) {
        this.showNotesImmediately = showNotesImmediately;
    }
}
