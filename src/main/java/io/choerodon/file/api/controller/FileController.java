package io.choerodon.file.api.controller;

import java.util.Optional;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.file.api.dto.FileDTO;
import io.choerodon.file.app.service.FileService;
import io.choerodon.file.infra.exception.FileUploadException;
import io.choerodon.swagger.annotation.Permission;

/**
 * @author HuangFuqiang@choerodon.io
 */
@RestController
public class FileController {

    private FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    public void setFileService(FileService fileService) {
        this.fileService = fileService;
    }

    @Permission(permissionLogin = true, level = ResourceLevel.SITE)
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
                .orElseThrow(() -> new FileUploadException("error.file.upload.return.null"));
    }

    @Permission(permissionLogin = true, level = ResourceLevel.SITE)
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

    @Permission(permissionPublic = true, level = ResourceLevel.SITE)
    @ApiOperation(value = "上传文件")
    @PostMapping("/v1/documents")
    public ResponseEntity<FileDTO> upload(
            @ApiParam(value = "bucket_name", required = true)
            @RequestParam("bucket_name") String bucketName,
            @ApiParam(value = "文件名", required = true)
            @RequestParam("file_name") String fileName,
            @ApiParam(value = "上传文件")
            @RequestParam("file") MultipartFile multipartFile) {
        return new ResponseEntity<>(fileService.uploadDocument(bucketName, fileName, multipartFile), HttpStatus.OK);
    }
}
