package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.time.LocalDate;

@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {
    private final long id;
    @NonNull
    @NotBlank
    @Email
    private final String email;
    @NonNull
    @NotBlank
    private final String login;
    private final String name;
    @NonNull
    @JsonFormat(pattern="yyyy-MM-dd")
    @Past
    private final LocalDate birthday;
}
