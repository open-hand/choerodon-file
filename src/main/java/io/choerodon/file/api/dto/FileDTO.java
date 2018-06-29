package io.choerodon.file.api.dto;

public class FileDTO {

    private String endPoint;
    private String originFileName;
    private String fileName;

    public FileDTO(String endPoint, String originFileName, String fileName) {
        this.endPoint = endPoint;
        this.originFileName = originFileName;
        this.fileName = fileName;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public String getOriginFileName() {
        return originFileName;
    }

    public void setOriginFileName(String originFileName) {
        this.originFileName = originFileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
