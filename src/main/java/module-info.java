module es.emi.automaticdeploy {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires java.naming;
    requires java.sql;
    requires org.hibernate.orm.core;
    requires java.persistence;
    requires org.apache.commons.io;
    requires mysql.connector.j;
    requires org.postgresql.jdbc;
    requires com.microsoft.sqlserver.jdbc;
    requires java.desktop;

    opens es.emi.automaticdeploy to javafx.fxml;
    exports es.emi.automaticdeploy;
    exports es.emi.automaticdeploy.controller;
    opens es.emi.automaticdeploy.controller to javafx.fxml;
    opens es.emi.automaticdeploy.entity to org.hibernate.orm.core;
}