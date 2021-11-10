package io.avec.ced.data;

import com.vaadin.fusion.Nonnull;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@Setter
@Getter
@MappedSuperclass
public abstract class AbstractEntity {

    @Id
    @GeneratedValue
    @Nonnull
    private Integer id;

    @Override
    public int hashCode() {
        if (id != null) {
            return id.hashCode();
        }
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
//        if (!(obj instanceof AbstractEntity)) { // Java < 17
        if (!(obj instanceof AbstractEntity other)) { // Java 17
            return false; // null or other class
        }

//        AbstractEntity other = (AbstractEntity) obj; // Java < 17

        if (id != null) {
            return id.equals(other.id);
        }
        return super.equals(other);
    }
}
