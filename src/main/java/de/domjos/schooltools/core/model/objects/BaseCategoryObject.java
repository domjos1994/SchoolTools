package de.domjos.schooltools.core.model.objects;

public class BaseCategoryObject extends BaseDescriptionObject {
    private String category;

    public BaseCategoryObject() {
        super();
        this.category = "";
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
