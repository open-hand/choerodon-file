package io.choerodon.file.infra.enums;

/**
 * @Author: scp
 * @Description:
 * @Date: Created in 2021/9/22
 * @Modified By:
 */
public enum OssTypeEnum {
    ALIYUN(1, "public-read"),
    HUAWEI(2, "public-read"),
    MINIO(3, "read-only");

    private Integer typeNum;
    private String accessControl;

    OssTypeEnum(Integer typeNum, String accessControl) {
        this.typeNum = typeNum;
        this.accessControl = accessControl;
    }

    public Integer getTypeNum() {
        return typeNum;
    }

    public String getAccessControl() {
        return accessControl;
    }
}
