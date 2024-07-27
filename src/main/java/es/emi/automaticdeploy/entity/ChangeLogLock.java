package es.emi.automaticdeploy.entity;


import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "databasechangeloglock")
public class ChangeLogLock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "LOCKED", nullable = false)
    private boolean locked;

    @Column(name = "LOCKGRANTED")
    private Date lockGranted;

    @Column(name = "LOCKEDBY")
    private String lockedBy;

    public ChangeLogLock() {
    }

    public ChangeLogLock(Long id, boolean locked, Date lockGranted, String lockedBy) {
        this.id = id;
        this.locked = locked;
        this.lockGranted = lockGranted;
        this.lockedBy = lockedBy;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public Date getLockGranted() {
        return lockGranted;
    }

    public void setLockGranted(Date lockGranted) {
        this.lockGranted = lockGranted;
    }

    public String getLockedBy() {
        return lockedBy;
    }

    public void setLockedBy(String lockedBy) {
        this.lockedBy = lockedBy;
    }

}
