package io.tmgg.web.argument;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

public class RequestBodyKeys extends ArrayList<String> {
    public RequestBodyKeys(@NotNull Collection<? extends String> c) {
        super(c);
    }
}
