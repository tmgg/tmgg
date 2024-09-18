package io.tmgg.lang;

import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;

public class LongTool {


    public static long strToMd5Long(String str) {
        return Hashing.md5().hashString(str, StandardCharsets.US_ASCII).asLong();
    }


}
