package io.tmgg.web.excel;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TableAttr {
    String name;
    String value;

    public TableAttr(String name, String value) {
        this.name = name;
        this.value = value;
    }
}
