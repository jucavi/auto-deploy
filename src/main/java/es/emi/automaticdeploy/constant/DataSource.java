package es.emi.automaticdeploy.constant;

public enum DataSource {

    MySQL("MySQL"),
    PostgreSQL("PostgreSQL"),
    SQLServer("SQLServer");

    private String label;

    DataSource(String label) {
        this.label = label;
    }

    public String toString() {
        return label;
    }
}
