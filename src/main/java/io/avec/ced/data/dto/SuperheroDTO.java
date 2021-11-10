package io.avec.ced.data.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.avec.ced.data.json.LocalDateDeserializer;
import io.avec.ced.data.json.LocalDateSerializer;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SuperheroDTO {
    private String nickname;
    private String firstname;
    private String lastname;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate dateOfBirth;
    private String country;
}
