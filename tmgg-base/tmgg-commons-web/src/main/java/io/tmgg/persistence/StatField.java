package io.tmgg.persistence;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatField {
    String name;

    StatType type;
}
