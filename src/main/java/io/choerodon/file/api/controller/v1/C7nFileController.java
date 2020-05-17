package io.choerodon.file.api.controller.v1;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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

    /**
     * 上传DevOps的CI过程产生的软件包
     *
     * @param tenantId      租户id
     * @param token         应用服务token
     * @param commit        此次ci的commit sha值
     * @param ciPipelineId  流水线id
     * @param ciJobId       流水线的job id
     * @param artifactName  软件包名称
     * @param multipartFile 软件包
     * @return 软件包地址， 如果有异常，返回400
     */
    @Permission(permissionPublic = true, level = ResourceLevel.SITE)
    @ApiOperation(value = "上传DevOps的CI软件包文件")
    @PostMapping("/devops/files")
    public ResponseEntity<String> uploadDevOpsArtifactFile(
            @ApiParam(value = "租户id")
            @RequestParam(value = "tenant_id") Long tenantId,
            @ApiParam(value = "应用服务token", required = true)
            @RequestParam(value = "token") String token,
            @ApiParam(value = "此次ci的commit", required = true)
            @RequestParam(value = "commit") String commit,
            @ApiParam(value = "gitlab内置的流水线id", required = true)
            @RequestParam(value = "ci_pipeline_id") Long ciPipelineId,
            @ApiParam(value = "gitlab内置的jobId", required = true)
            @RequestParam(value = "ci_job_id") Long ciJobId,
            @ApiParam(value = "ci流水线定义的软件包名称", required = true)
            @RequestParam(value = "artifact_name") String artifactName,
            @ApiParam(value = "上传文件", required = true)
            @RequestParam("file") MultipartFile multipartFile) {
        return ResponseEntity.ok(fileC7nService.uploadDevOpsArtifactFile(tenantId, token, commit, ciPipelineId, ciJobId, artifactName, multipartFile));
    }
}
