package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Film {
    private final long id;
    @NotBlank
    @NonNull
    private final String name;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private final LocalDate releaseDate;
    @Size(max = 200)
    private final String description;
    @NonNull
    @Positive
    private final Integer duration;

    private final Integer rate;
    private final Mpa mpa;
    private final List<Genre> genres;

    public Integer getRate() {
        return Objects.requireNonNullElse(rate, 0);
    }
}
