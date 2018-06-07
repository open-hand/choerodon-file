package io.choerodon.file.api;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.file.app.service.FileService;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

/**
 * @author HuangFuqiang@choerodon.io
 */
@RestController
public class FileController {

    @Autowired
    private FileService fileService;

    /**
     * @deprecated 已过期，url不规范
     */
    @Permission(level = ResourceLevel.ORGANIZATION)
    @Deprecated
    @ApiOperation(value = "上传文件")
    @RequestMapping(value = "/v1/organization/{organizationId}/file/backetName/{backetName}", method = RequestMethod.POST)
    public ResponseEntity<String> oldUploadFile(
            @PathVariable Long organizationId,
            @ApiParam(value = "backetName", required = true)
            @PathVariable String backetName,
            @ApiParam(value = "文件名", required = true)
            @RequestParam String fileName,
            @ApiParam(value = "上传文件")
            @RequestParam("file") MultipartFile multipartFile) {
        return Optional.ofNullable(fileService.uploadFile(backetName, fileName, multipartFile))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.file.upload"));
    }

    /**
     * @deprecated 已过期，url不规范
     */
    @Permission(level = ResourceLevel.ORGANIZATION)
    @Deprecated
    @ApiOperation(value = "删除文件")
    @RequestMapping(value = "/v1/organization/{organizationId}/file/backetName/{backetName}", method = RequestMethod.DELETE)
    public ResponseEntity oldDeleteFile(
            @PathVariable Long organizationId,
            @ApiParam(value = "backetName", required = true)
            @PathVariable String backetName,
            @ApiParam(value = "文件地址", required = true)
            @RequestParam String url) {
        fileService.deleteFile(backetName, url);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @Permission(permissionLogin = true)
    @ApiOperation(value = "上传文件")
    @PostMapping("/v1/files")
    public ResponseEntity<String> uploadFile(
            @ApiParam(value = "bucket_name", required = true)
            @RequestParam("bucket_name") String bucketName,
            @ApiParam(value = "文件名", required = true)
            @RequestParam("file_name") String fileName,
            @ApiParam(value = "上传文件")
            @RequestParam("file") MultipartFile multipartFile) {
        return Optional.ofNullable(fileService.uploadFile(bucketName, fileName, multipartFile))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.file.upload"));
    }

    @Permission(permissionLogin = true)
    @ApiOperation(value = "删除文件")
    @DeleteMapping("/v1/files")
    public ResponseEntity deleteFile(
            @ApiParam(value = "bucket_name", required = true)
            @RequestParam("bucket_name") String bucketName,
            @ApiParam(value = "文件地址", required = true)
            @RequestParam("url") String url) {
        fileService.deleteFile(bucketName, url);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
