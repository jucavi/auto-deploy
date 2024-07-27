package es.emi.automaticdeploy.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * DTO for {@link es.emi.automaticdeploy.entity.ChangeLogLock}
 */
public class ChangeLogLockDto implements Serializable {
    private final Long id;
    private final boolean locked;
    private final Date lockGranted;
    private final String lockedBy;

    public ChangeLogLockDto(
            Long id,
            boolean locked,
            Date lockGranted,
            String lockedBy) {

        this.id = id;
        this.locked = locked;
        this.lockGranted = lockGranted;
        this.lockedBy = lockedBy;
    }

    public Long getId() {
        return id;
    }

    public boolean getLocked() {
        return locked;
    }

    public Date getLockGranted() {
        return lockGranted;
    }

    public String getLockedBy() {
        return lockedBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChangeLogLockDto entity = (ChangeLogLockDto) o;
        return Objects.equals(this.id, entity.id) &&
                Objects.equals(this.locked, entity.locked) &&
                Objects.equals(this.lockGranted, entity.lockGranted) &&
                Objects.equals(this.lockedBy, entity.lockedBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, locked, lockGranted, lockedBy);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + id + ", " +
                "locked = " + locked + ", " +
                "lockGranted = " + lockGranted + ", " +
                "lockedBy = " + lockedBy + ")";
    }
}