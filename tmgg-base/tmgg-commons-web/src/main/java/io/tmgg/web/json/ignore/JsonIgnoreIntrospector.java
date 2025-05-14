package io.tmgg.web.json.ignore;

import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;

public class JsonIgnoreIntrospector extends JacksonAnnotationIntrospector {

    @Override
    public boolean hasIgnoreMarker(AnnotatedMember m) {
        if (m.hasAnnotation(JsonIgnoreForApp.class)) {
            return true;
        }
        return super.hasIgnoreMarker(m);
    }

    @Override
    public Boolean isIgnorableType(AnnotatedClass ac) {
        return super.isIgnorableType(ac);
    }
}
