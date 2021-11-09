package io.avec.ced.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.avec.ced.data.AbstractEntity;
import io.avec.ced.data.Role;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import java.util.Set;

@Getter
@Setter
@Entity
public class User extends AbstractEntity {

    private String username;
    private String name;
    @JsonIgnore
    private String hashedPassword;
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Role> roles;
    @Lob
    private String profilePictureUrl;

}
