package io.choerodon.file.api.controller.v1;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.file.app.service.FileC7nService;
import io.choerodon.swagger.annotation.Permission;

/**
 * @author scp
 * @date 2020/5/14
 * @description
 */
@RestController
@RequestMapping(value = "/choerodon/v1/{organization_id}")
public class CutImageC7nController {

    @Autowired
    private FileC7nService fileC7nService;

    @Permission(permissionPublic = true, level = ResourceLevel.SITE)
    @ApiOperation(value = "裁切图片")
    @PostMapping("/cut_image")
    public ResponseEntity<String> cutImage(
            @RequestPart MultipartFile file,
            @ApiParam(name = "rotate", value = "顺时针旋转的角度", example = "90")
            @RequestParam(required = false) Double rotate,
            @ApiParam(name = "startX", value = "裁剪的X轴", example = "100")
            @RequestParam(required = false, name = "startX") Integer axisX,
            @ApiParam(name = "startY", value = "裁剪的Y轴", example = "100")
            @RequestParam(required = false, name = "startY") Integer axisY,
            @ApiParam(name = "endX", value = "裁剪的宽度", example = "200")
            @RequestParam(required = false, name = "endX") Integer width,
            @ApiParam(name = "endY", value = "裁剪的高度", example = "200")
            @RequestParam(required = false, name = "endY") Integer height,
            @ApiParam(value = "租户ID", required = true)
            @Encrypt
            @PathVariable("organization_id") Long organizationId,
            @ApiParam(value = "桶名", required = true)
            @RequestParam("bucketName") String bucketName
    ) {
        return new ResponseEntity<>(fileC7nService.cutImage(organizationId, bucketName, file, rotate, axisX, axisY, width, height), HttpStatus.OK);
    }

}
