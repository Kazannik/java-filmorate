package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class Film {
    @Positive
    private final long id;
    @NotBlank
    @NonNull
    private final String name;
    @Size(max = 200)
    private final String description;
    @JsonFormat(pattern="yyyy-MM-dd")
    private final LocalDate releaseDate;
    @Positive
    private final int duration;
    private final Set<Long> likes = new HashSet<>();
}
