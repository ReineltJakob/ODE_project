package at.fhw.commercial_test;

import java.util.Date;

public class ImageData {
    private final String fileName;
    private final Date uploadDate;

    public ImageData(String fileName, Date uploadDate) {
        this.fileName = fileName;
        this.uploadDate = uploadDate;
    }

    public String getFileName() {
        return fileName;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public long getImageAgeInMinutes() {
        return (new Date().getTime() - uploadDate.getTime()) / (60 * 1000);
    }
}