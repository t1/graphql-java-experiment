package org.superheroes.hero;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Data
public class Score {
    private @Pattern(regexp = "[a-z-]{2,32}") String category;
    private @Min(value = 1, groups = NonSupers.class) @Min(value = 80, groups = Supers.class) @Max(100) short value;
    private @PastOrPresent LocalDate date;
}
