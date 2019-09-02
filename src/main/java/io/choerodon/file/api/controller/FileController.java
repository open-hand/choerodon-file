package io.choerodon.file.api.controller;

import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.file.api.dto.BucketCreateResultDTO;
import io.choerodon.file.api.dto.FileDTO;
import io.choerodon.file.app.service.FileService;
import io.choerodon.file.infra.exception.FileUploadException;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    @Permission(permissionLogin = true, type = ResourceType.SITE)
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

    @Permission(permissionLogin = true, type = ResourceType.SITE)
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

    @Permission(permissionPublic = true, type = ResourceType.SITE)
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

    @Permission(permissionPublic = true, type = ResourceType.SITE)
    @ApiOperation(value = "裁切图片")
    @PostMapping("/v1/cut_image")
    public ResponseEntity<String> cutImage(@RequestPart MultipartFile file,
                                           @ApiParam(name = "rotate", value = "顺时针旋转的角度", example = "90")
                                           @RequestParam(required = false) Double rotate,
                                           @ApiParam(name = "startX", value = "裁剪的X轴", example = "100")
                                           @RequestParam(required = false, name = "startX") Integer axisX,
                                           @ApiParam(name = "startY", value = "裁剪的Y轴", example = "100")
                                           @RequestParam(required = false, name = "startY") Integer axisY,
                                           @ApiParam(name = "endX", value = "裁剪的宽度", example = "200")
                                           @RequestParam(required = false, name = "endX") Integer width,
                                           @ApiParam(name = "endY", value = "裁剪的高度", example = "200")
                                           @RequestParam(required = false, name = "endY") Integer height) {
        return new ResponseEntity<>(fileService.cutImage(file, rotate, axisX, axisY, width, height), HttpStatus.OK);
    }

    @Permission(permissionPublic = true, type = ResourceType.SITE)
    @ApiOperation(value = "创建无策略的Bucket")
    @GetMapping("/v1/bucket/policy_none")
    public ResponseEntity<BucketCreateResultDTO> createBucketWithNonePolicy(@ApiParam(value = "bucket_name", required = true)
                                                                            @RequestParam("bucket_name") String bucketName) {
        return new ResponseEntity<>(fileService.createBucketWithNonePolicy(bucketName), HttpStatus.OK);
    }

    @Permission(permissionPublic = true, type = ResourceType.SITE)
    @ApiOperation(value = "获取文件的临时下载路径")
    @GetMapping("/v1/tmp_download_path")
    public ResponseEntity<String> getTmpDownloadPath(@ApiParam(value = "bucket_name", required = true)
                                                     @RequestParam("bucket_name") String bucketName,
                                                     @ApiParam(value = "文件路径", required = true)
                                                     @RequestParam("file_url") String url,
                                                     @ApiParam(value = "有效时长（秒）", required = true)
                                                     @RequestParam("expires") Integer expires) {
        return new ResponseEntity<>(fileService.presignedGetObject(bucketName, url, expires), HttpStatus.OK);
    }


    @Permission(permissionPublic = true, type = ResourceType.SITE)
    @ApiOperation(value = "批量获取文件列表的临时下载路径")
    @GetMapping("/v1/tmp_download_paths")
    public ResponseEntity<Map<String, String>> getTmpDownloadPathList(@ApiParam(value = "bucket_name", required = true)
                                                                      @RequestParam("bucket_name") String bucketName,
                                                                      @ApiParam(value = "有效时长（秒）", required = true)
                                                                      @RequestParam("expires") Integer expires,
                                                                      @ApiParam(value = "文件路径列表", required = true)
                                                                      @RequestBody List<String> urls) {
        return new ResponseEntity<>(fileService.presignedGetObjectList(bucketName, urls, expires), HttpStatus.OK);
    }


}