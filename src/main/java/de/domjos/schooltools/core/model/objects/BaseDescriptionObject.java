package de.domjos.schooltools.core.model.objects;

public class BaseDescriptionObject extends BaseObject {
    private String description;

    public BaseDescriptionObject() {
        super();
        this.description = "";
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
