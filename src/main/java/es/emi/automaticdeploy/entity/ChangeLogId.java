package es.emi.automaticdeploy.entity;


import java.io.Serializable;
import java.util.Objects;

public class ChangeLogId implements Serializable {

    private String id;
    private String author;
    private String filename;

    public ChangeLogId() {}

    public ChangeLogId(String id, String author, String filename) {
        this.id = id;
        this.author = author;
        this.filename = filename;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ChangeLogId that = (ChangeLogId) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(author, that.author) &&
                Objects.equals(filename, that.filename);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, author, filename);
    }
}