package io.choerodon.file.api.controller.v1;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hzero.core.util.Results;
import org.hzero.file.domain.entity.File;
import org.hzero.file.domain.entity.StorageConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.file.app.service.FileC7nService;
import io.choerodon.swagger.annotation.Permission;

/**
 * @author zmf
 * @since 20-4-27
 */
@RequestMapping("/choerodon/v1")
@RestController
public class C7nFileController {
    @Autowired
    private FileC7nService fileC7nService;


    @Permission(permissionPublic = true, level = ResourceLevel.SITE)
    @ApiOperation(value = "根据bucketName和文件的完全url删除文件")
    @PostMapping("/{organizationId}/delete-by-url")
    public ResponseEntity deleteByUrls(
            @ApiParam(value = "租户ID", required = true) @PathVariable Long organizationId,
            @ApiParam(value = "桶名", required = true) @RequestParam("bucketName") String bucketName,
            @ApiParam(value = "文件地址", required = true) @RequestBody List<String> urls) {
        fileC7nService.deleteByUrls(organizationId, bucketName, urls);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @Permission(permissionLogin = true, level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "基于Multipart上传文件")
    @PostMapping("/{organizationId}/files/multipart")
    public ResponseEntity<String> uploadFile(
            @ApiParam(value = "租户ID", required = true) @PathVariable Long organizationId,
            @ApiParam(value = "桶名", required = true) @RequestParam("bucketName") String bucketName,
            @ApiParam(value = "上传目录") @RequestParam(value = "directory", required = false) String directory,
            @ApiParam(value = "文件名") @RequestParam(value = "fileName", required = false) String fileName,
            @ApiParam(value = "默认类型 1:固定,0:不固定") @RequestParam(value = "docType", defaultValue = "0") Integer docType,
            @ApiParam(value = "存储配置编码") @RequestParam(value = "storageCode", required = false) String storageCode,
            @ApiParam(value = "上传文件") @RequestParam("file") MultipartFile multipartFile) {
        return Results.success(fileC7nService.uploadMultipart(organizationId, bucketName, null, directory, fileName, docType, storageCode, multipartFile));
    }

    @Permission(permissionLogin = true, level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "下载文件")
    @GetMapping("/{organizationId}/download/{file_id}")
    public void downloadByUrl(
            HttpServletRequest request, HttpServletResponse response,
            @ApiParam(value = "租户ID", required = true) @PathVariable Long organizationId,
            @ApiParam(value = "fileKey", required = true) @PathVariable("file_id") Long fileId) {
        fileC7nService.downloadFile(request, response, organizationId, fileId);
    }

    @Permission(permissionWithin = true)
    @ApiOperation(value = "根据文件id查询文件")
    @PostMapping("/list_files")
    public ResponseEntity<List<File>> listFileByIds(
            @RequestBody List<Long> fileIds) {
        return Results.success(fileC7nService.listFileByIds(fileIds));
    }

    @Permission(permissionWithin = true)
    @ApiOperation(value = "删除文件")
    @DeleteMapping("/{organizationId}/delete-by-id")
    public ResponseEntity<Void> deleteById(
            @ApiParam(value = "租户ID", required = true) @PathVariable Long organizationId,
            @RequestParam("file_id") Long fileId) {
        fileC7nService.deleteById(organizationId, fileId);
        return Results.success();
    }

    @Permission(permissionWithin = true)
    @ApiOperation(value = "查询默认的文件配置")
    @GetMapping("/default/config")
    public ResponseEntity<StorageConfig> queryDefaultConfig() {
        return Results.success(fileC7nService.queryDefaultConfig());
    }
}
