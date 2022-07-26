package io.choerodon.file.api.controller.v1;

import static io.choerodon.file.infra.constant.CommonConstant.FOLDER;
import static io.choerodon.file.infra.constant.CommonConstant.STORAGE_CODE_FORMAT;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.Results;
import org.hzero.file.domain.entity.File;
import org.hzero.fragment.service.FragmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.file.app.service.FileC7nService;
import io.choerodon.file.infra.config.OssProperties;
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
    @Autowired
    private OssProperties ossProperties;
    @Autowired
    private FragmentService fragmentService;


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
            @ApiParam(value = "前缀方式") @RequestParam(value = "prefix", required = false, defaultValue = "uuid") String prefix,
            @ApiParam(value = "默认类型 1:固定,0:不固定") @RequestParam(value = "docType", defaultValue = "0") Integer docType,
            @ApiParam(value = "存储配置编码") @RequestParam(value = "storageCode", required = false) String storageCode,
            @ApiParam(value = "上传文件") @RequestParam("file") MultipartFile multipartFile) {
        return Results.success(fileC7nService.uploadMultipart(organizationId, bucketName, null, directory, fileName, docType, storageCode, multipartFile, prefix));
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
    @ApiOperation(value = "根据文件id查询文件")
    @PostMapping("/{organizationId}/query_file_with_url")
    public ResponseEntity<File> queryFileWithUrl(
            @PathVariable Long organizationId,
            @RequestParam("bucketName") String bucketName,
            @RequestBody String fileUrl) {
        return Results.success(fileC7nService.queryFileWithUrl(organizationId, bucketName, fileUrl));
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
    @ApiOperation(value = "根据文件的keys集合 查询文件数据")
    @PostMapping("{organization_id}/file/list")
    public ResponseEntity<List<File>> queryFileDTOByIds(@PathVariable("organization_id") Long organizationId,
                                                        @RequestBody List<String> fileKeys) {
        return Results.success(fileC7nService.queryFileDTOByIds(organizationId, fileKeys));
    }

    @Permission(permissionWithin = true)
    @ApiOperation(value = "修改文件")
    @PutMapping("/{organization_id}/update_file")
    public ResponseEntity<Void> updateFile(
            @ApiParam(value = "租户ID", required = true) @PathVariable(name = "organization_id") Long organizationId,
            @RequestBody File file) {
        fileC7nService.updateFile(organizationId, file);
        return Results.success();
    }

    @PostMapping("/{organizationId}/upload/combine")
    @ApiOperation(value = "c7n分片文件合并")
    @Permission(permissionLogin = true)
    @ResponseBody
    public ResponseEntity<String> fragmentCombineBlock(@PathVariable Long organizationId, @RequestBody Map<String, String> args) {
        // 设置文件路径前缀生成方式 默认folder
        String prefix = args.getOrDefault("prefix", "folder");
        if (prefix.equalsIgnoreCase("folder")) {
            args.put("storageCode", String.format(STORAGE_CODE_FORMAT, ossProperties.getType()) + BaseConstants.Symbol.MIDDLE_LINE + FOLDER);
        }
        return Results.success(fragmentService.combineUpload(args.get("guid"), organizationId, args.get("fileName"), args));
    }
}
