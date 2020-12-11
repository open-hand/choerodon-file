package io.choerodon.file.api.controller.v1;

import java.util.List;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.file.api.controller.vo.CiCdPipelineRecordVO;
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

    @Permission(permissionPublic = true)
    @ApiOperation(value = "Devops cicd流水线")
    @PostMapping("/audit")
    public ResponseEntity<Void> auditTest(
            @ApiParam(value = "应用信息", required = true)
            @RequestBody CiCdPipelineRecordVO devopsPipelineVO) {
        fileC7nService.auidt(devopsPipelineVO);
        return ResponseEntity.noContent().build();
    }
}
