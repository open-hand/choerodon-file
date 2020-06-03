package io.choerodon.file.infra.constant;

/**
 * @author zmf
 * @since 20-4-27
 */
public class DevOpsConstants {
    /**
     * ci生成的软件包的名称的模板, abcdefg-${gitlabPipelineId}-${artifactName}
     */
    public static final String CI_JOB_ARTIFACT_NAME_TEMPLATE = "abcdevopszyx-%s-%s.tgz";

    public static final String DEV_OPS_CI_ARTIFACT_FILE_BUCKET = "devops-service-ci-artifacts";
}
