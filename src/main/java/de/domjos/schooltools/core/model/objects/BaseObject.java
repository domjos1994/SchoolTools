package de.domjos.schooltools.core.model.objects;

public class BaseObject {
    private int ID;
    private String title;

    public BaseObject() {
        this.ID = 0;
        this.title = "";
    }

    public int getID() {
        return this.ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
