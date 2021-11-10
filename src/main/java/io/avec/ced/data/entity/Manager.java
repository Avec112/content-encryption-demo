package io.avec.ced.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.avec.ced.data.AbstractEntity;
import io.avec.ced.data.Role;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
public class Manager extends AbstractEntity {

    private String username;
    private String name;
    @JsonIgnore
    private String hashedPassword;
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Role> roles;
    @Lob
    private String profilePictureUrl;
    @Lob
    private String publicKey;
    @Lob
    private String privateKey;

    @OneToMany(mappedBy = "manager", orphanRemoval = true)
    private List<SuperheroManager> superheroManagers = new ArrayList<>();

}
