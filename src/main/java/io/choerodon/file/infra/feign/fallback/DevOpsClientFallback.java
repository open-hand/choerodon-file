package io.choerodon.file.infra.feign.fallback;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import io.choerodon.file.infra.exception.DevopsCiInvalidException;
import io.choerodon.file.infra.feign.DevOpsClient;

/**
 * @author zmf
 * @since 20-4-28
 */
@Component
public class DevOpsClientFallback implements DevOpsClient {
    @Override
    public ResponseEntity<Boolean> checkJobArtifactInfo(String token, String commit, Long ciPipelineId, Long ciJobId, String artifactName, Long fileByteSize) {
        throw new DevopsCiInvalidException("error.check.artifact.information", artifactName);
    }

    @Override
    public ResponseEntity<Void> saveJobArtifactInfo(String token, String commit, Long ciPipelineId, Long ciJobId, String artifactName, String fileUrl) {
        throw new DevopsCiInvalidException("error.save.job.artifact.information", artifactName);
    }
}
