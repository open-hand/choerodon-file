package io.choerodon.file.api.dto;

import io.swagger.annotations.ApiModelProperty;

public class FileDTO {

    @ApiModelProperty("文件服务器地址")
    private String endPoint;
    @ApiModelProperty("原始文件名")
    private String originFileName;
    @ApiModelProperty("新文件名")
    private String fileName;

    public FileDTO(String endPoint, String originFileName, String fileName) {
        this.endPoint = endPoint;
        this.originFileName = originFileName;
        this.fileName = fileName;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public String getOriginFileName() {
        return originFileName;
    }

    public String getFileName() {
        return fileName;
    }
}
