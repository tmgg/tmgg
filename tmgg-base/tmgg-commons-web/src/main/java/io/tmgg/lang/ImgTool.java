package io.tmgg.lang;

import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class ImgTool {


    public static String toBase64DataUri(BufferedImage image) throws IOException {
        String mimeType = "image/jpg";

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", os);

        byte[] data = os.toByteArray();
        os.close();

        return getDataUri(mimeType, "base64", Base64.getEncoder().encodeToString(data));
    }


    public static String getDataUri(String mimeType, String encoding, String data) {
        final StringBuilder builder = new StringBuilder("data:");
        if (StringUtils.isNotBlank(mimeType)) {
            builder.append(mimeType);
        }

        if (StringUtils.isNotBlank(encoding)) {
            builder.append(';').append(encoding);
        }
        builder.append(',').append(data);

        return builder.toString();
    }


}
