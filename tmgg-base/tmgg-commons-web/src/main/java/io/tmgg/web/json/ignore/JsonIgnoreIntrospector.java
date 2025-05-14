package io.tmgg.web.json.ignore;

import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;

public class JsonIgnoreIntrospector extends JacksonAnnotationIntrospector {

    @Override
    public boolean hasIgnoreMarker(AnnotatedMember m) {


        if (m.getAnnotation(JsonIgnoreForApp.class) != null) {
            return true;
        }
        return super.hasIgnoreMarker(m);
    }


}
