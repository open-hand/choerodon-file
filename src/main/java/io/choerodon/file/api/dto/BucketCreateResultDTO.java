package io.choerodon.file.api.dto;

import io.swagger.annotations.ApiModelProperty;

public class BucketCreateResultDTO {
    @ApiModelProperty("是否创建成功")
    private Boolean createdSuccessfully;
    @ApiModelProperty("是否是新创建的Bucket")
    private Boolean newlyCreatedOne;


    public Boolean getCreatedSuccessfully() {
        return createdSuccessfully;
    }

    public void setCreatedSuccessfully(Boolean createdSuccessfully) {
        this.createdSuccessfully = createdSuccessfully;
    }

    public Boolean getNewlyCreatedOne() {
        return newlyCreatedOne;
    }

    public void setNewlyCreatedOne(Boolean newlyCreatedOne) {
        this.newlyCreatedOne = newlyCreatedOne;
    }
}
