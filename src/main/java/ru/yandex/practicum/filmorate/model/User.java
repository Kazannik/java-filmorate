package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class User {
    @Positive
    private final long id;
    @NonNull
    @NotBlank
    @Email
    private final String email;
    @NonNull
    @NotBlank
    private final String login;
    private final String name;
    @JsonFormat(pattern="yyyy-MM-dd")
    @Past
    private final LocalDate birthday;
    private final Set<Long> friends = new HashSet<>();
}
