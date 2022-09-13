package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Mpa {
    @NonNull
    private final Integer id;
    private final String name;

    public Mpa (Integer id){
        this.id=id;
        this.name=null;
    }
}
