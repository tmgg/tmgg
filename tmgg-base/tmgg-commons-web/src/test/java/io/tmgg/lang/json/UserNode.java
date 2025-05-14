package io.tmgg.lang.json;

import lombok.Data;

@Data
public class UserNode {
    String name;
    Integer age;

    public void setName(String name) {
        this.name = name;
    }
}
