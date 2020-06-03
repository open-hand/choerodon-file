package io.choerodon.file.infra.feign;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.choerodon.file.infra.feign.fallback.DevOpsClientFallback;

/**
 * @author zmf
 * @since 20-4-28
 */
@FeignClient(value = "devops-service", fallback = DevOpsClientFallback.class)
public interface DevOpsClient {
    /**
     * CI校验上传软件包的信息
     *
     * @param token        应用服务token
     * @param commit       ci的commit值
     * @param ciPipelineId 流水线id
     * @param ciJobId      流水线的job id
     * @param artifactName 软件包名称
     * @return true表示通过校验， 未通过校验则会抛出{@link io.choerodon.core.exception.FeignException}
     */
    @ApiOperation("CI过程上传软件包校验信息, 大小不得大于200Mi")
    @PostMapping("/ci/check_artifact_info")
    ResponseEntity<Boolean> checkJobArtifactInfo(
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
            @ApiParam(value = "文件字节数")
            @RequestParam(value = "file_byte_size") Long fileByteSize);


    /**
     * CI过程保存软件包信息
     *
     * @param token        应用服务token
     * @param commit       ci的commit值
     * @param ciPipelineId 流水线id
     * @param ciJobId      流水线的job id
     * @param artifactName 软件包名称
     * @param fileUrl      软件包文件地址
     * @return 200 表示ok， 400表示错误
     */
    @ApiOperation("CI过程保存软件包信息")
    @PostMapping("/ci/save_artifact")
    ResponseEntity<Void> saveJobArtifactInfo(
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
            @ApiParam(value = "软件包地址", required = true)
            @RequestParam(value = "file_url") String fileUrl);
}
