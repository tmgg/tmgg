package io.tmgg.lang.dao;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatField {
    String name;

    StatType type;
}
