package io.avec.ced.data.entity;

import io.avec.ced.data.AbstractEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "superhero")
@Entity
public class Superhero extends AbstractEntity {
    @Column(unique = true)
    private String nickname;

    @Lob
    private String encryptedJson;

    @ToString.Exclude
    @OneToMany(mappedBy = "superhero", orphanRemoval = true)
    private List<SuperheroManager> superheroManagers = new ArrayList<>();

    public Superhero(String nickname, String encryptedJson) {
        this.nickname = nickname;
        this.encryptedJson = encryptedJson;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Superhero.class.getSimpleName() + "(", ")")
                .add("nickname=" + nickname)
                .add("encryptedJson=" + StringUtils.left(encryptedJson, 32) + "..." + StringUtils.right(encryptedJson, 10))
                .toString();
    }
}
