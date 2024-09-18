package io.tmgg.lang;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexTool {

    public static String findFirstMatch(String regex, String input, int groupIndex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        boolean b = matcher.find();

        int count = matcher.groupCount();
        if(b && (groupIndex  <= count)) {
            return matcher.group(groupIndex);
        }

        return null;
    }

}
