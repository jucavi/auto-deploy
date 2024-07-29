package es.emi.automaticdeploy.entity;

import es.emi.automaticdeploy.constant.ExecType;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "databasechangelog")
@IdClass(ChangeLogId.class)
public class ChangeLog {

    @Id
    @Column(name = "ID", nullable = false)
    private String id;

    @Id
    @Column(name = "AUTHOR", nullable = false)
    private String author;

    @Id
    @Column(name = "FILENAME", nullable = false)
    private String filename;

    @Column(name = "DATEEXECUTED", nullable = false)
    private LocalDateTime dateTime;

    @Column(name = "ORDEREXECUTED", nullable = false)
    private Long orderExecuted;

    @Column(name = "EXECTYPE", nullable = false)
    @Enumerated(EnumType.STRING)
    private ExecType execType;

    @Column(name = "MD5SUM")
    private String md5Sum;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "COMMENTS")
    private String comments;

    @Column(name = "TAG")
    private String tag;

    @Column(name = "LIQUIBASE")
    private String liquibase;

    @Column(name = "CONTEXTS")
    private String contexts;

    @Column(name = "LABELS")
    private String labels;

    @Column(name = "DEPLOYMENT_ID")
    private String deploymentId;

    public ChangeLog() {
    }

    public ChangeLog(
            String id,
            String author,
            String filename,
            LocalDateTime dateTime,
            Long orderExecuted,
            ExecType execType,
            String md5Sum,
            String description,
            String comments,
            String tag,
            String liquibase,
            String contexts,
            String labels,
            String deploymentId) {

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

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public Long getOrderExecuted() {
        return orderExecuted;
    }

    public void setOrderExecuted(Long orderExecuted) {
        this.orderExecuted = orderExecuted;
    }

    public ExecType getExecType() {
        return execType;
    }

    public void setExecType(ExecType execType) {
        this.execType = execType;
    }

    public String getMd5Sum() {
        return md5Sum;
    }

    public void setMd5Sum(String md5Sum) {
        this.md5Sum = md5Sum;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getLiquibase() {
        return liquibase;
    }

    public void setLiquibase(String liquibase) {
        this.liquibase = liquibase;
    }

    public String getContexts() {
        return contexts;
    }

    public void setContexts(String contexts) {
        this.contexts = contexts;
    }

    public String getLabels() {
        return labels;
    }

    public void setLabels(String labels) {
        this.labels = labels;
    }

    public String getDeploymentId() {
        return deploymentId;
    }

    public void setDeploymentId(String deploymentId) {
        this.deploymentId = deploymentId;
    }
}