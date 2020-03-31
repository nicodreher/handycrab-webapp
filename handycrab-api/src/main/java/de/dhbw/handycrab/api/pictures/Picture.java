package de.dhbw.handycrab.api.pictures;

import java.io.Serializable;
import java.util.UUID;

public class Picture implements Serializable {
    private UUID _id;
    private String base64;
    private String contentType;

    public Picture(String base64, String contentType) {
        this.base64 = base64;
        this.contentType = contentType;
    }

    public String getBase64() {
        return base64;
    }

    public String getContentType() {
        return contentType;
    }
}
