package es.emi.automaticdeploy.dto;

import es.emi.automaticdeploy.constant.ExecType;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * DTO for {@link es.emi.automaticdeploy.entity.ChangeLog}
 */
public class ChangeLogDto implements Serializable {

    private final String id;
    private final String author;
    private final String filename;
    private final LocalDateTime dateTime;
    private final Long orderExecuted;
    private final ExecType execType;
    private final String md5Sum;
    private final String description;
    private final String comments;
    private final String tag;
    private final String liquibase;
    private final String contexts;
    private final String labels;
    private final String deploymentId;

    public ChangeLogDto(String id, String author, String filename, LocalDateTime dateTime, Long orderExecuted, ExecType execType, String md5Sum, String description, String comments, String tag, String liquibase, String contexts, String labels, String deploymentId) {
        this.id = id;
        this.author = author;
        this.filename = filename;
        this.dateTime = dateTime;
        this.orderExecuted = orderExecuted;
        this.execType = execType;
        this.md5Sum = md5Sum;
        this.description = description;
        this.comments = comments;
        this.tag = tag;
        this.liquibase = liquibase;
        this.contexts = contexts;
        this.labels = labels;
        this.deploymentId = deploymentId;
    }

    public String getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getFilename() {
        return filename;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public Long getOrderExecuted() {
        return orderExecuted;
    }

    public ExecType getExecType() {
        return execType;
    }

    public String getMd5Sum() {
        return md5Sum;
    }

    public String getDescription() {
        return description;
    }

    public String getComments() {
        return comments;
    }

    public String getTag() {
        return tag;
    }

    public String getLiquibase() {
        return liquibase;
    }

    public String getContexts() {
        return contexts;
    }

    public String getLabels() {
        return labels;
    }

    public String getDeploymentId() {
        return deploymentId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChangeLogDto entity = (ChangeLogDto) o;
        return Objects.equals(this.id, entity.id) &&
                Objects.equals(this.author, entity.author) &&
                Objects.equals(this.filename, entity.filename) &&
                Objects.equals(this.dateTime, entity.dateTime) &&
                Objects.equals(this.orderExecuted, entity.orderExecuted) &&
                Objects.equals(this.execType, entity.execType) &&
                Objects.equals(this.md5Sum, entity.md5Sum) &&
                Objects.equals(this.description, entity.description) &&
                Objects.equals(this.comments, entity.comments) &&
                Objects.equals(this.tag, entity.tag) &&
                Objects.equals(this.liquibase, entity.liquibase) &&
                Objects.equals(this.contexts, entity.contexts) &&
                Objects.equals(this.labels, entity.labels) &&
                Objects.equals(this.deploymentId, entity.deploymentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, author, filename, dateTime, orderExecuted, execType, md5Sum, description, comments, tag, liquibase, contexts, labels, deploymentId);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + id + ", " +
                "author = " + author + ", " +
                "filename = " + filename + ", " +
                "dateTime = " + dateTime + ", " +
                "orderExecuted = " + orderExecuted + ", " +
                "execType = " + execType + ", " +
                "md5Sum = " + md5Sum + ", " +
                "description = " + description + ", " +
                "comments = " + comments + ", " +
                "tag = " + tag + ", " +
                "liquibase = " + liquibase + ", " +
                "contexts = " + contexts + ", " +
                "labels = " + labels + ", " +
                "deploymentId = " + deploymentId + ")";
    }
}