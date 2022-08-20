package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class Film {
    private final int id;
    @NotBlank
    @NonNull
    private final String name;
    private final String description;
    @JsonFormat(pattern="yyyy-MM-dd")
    private final LocalDate releaseDate;
    private final int duration;
}
