package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class User {
    private final int id;
    @NonNull
    @NotBlank
    @Email
    private final String email;
    @NonNull
    @NotBlank
    private final String login;
    private final String name;
    @JsonFormat(pattern="yyyy-MM-dd")
    private final LocalDate birthday;
}
