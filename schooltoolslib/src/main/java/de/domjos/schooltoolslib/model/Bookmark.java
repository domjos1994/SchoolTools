package de.domjos.schooltoolslib.model;

import de.domjos.customwidgets.model.objects.BaseDescriptionObject;

public class Bookmark extends BaseDescriptionObject {
    private Subject subject;
    private String themes;
    private String link;
    private String tags;
    private byte[] preview;
    private byte[] data;

    public Bookmark() {
        super();

        this.subject = null;
        this.themes = "";
        this.link = "";
        this.tags = "";
        this.preview = null;
        this.data = null;
    }

    public Subject getSubject() {
        return this.subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public String getThemes() {
        return this.themes;
    }

    public void setThemes(String themes) {
        this.themes = themes;
    }

    public String getLink() {
        return this.link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTags() {
        return this.tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public byte[] getPreview() {
        return this.preview;
    }

    public void setPreview(byte[] preview) {
        this.preview = preview;
    }

    public byte[] getData() {
        return this.data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
