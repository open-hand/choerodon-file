package io.choerodon.file.infra.config;

/**
 * @author dongfan117@gmail.com
 */
public class FileClientConfiguration {
    public static final String DEFAULT_REGION = "";
    public static final String US_EAST_1 = "us-east-1";

    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String region;
    private Boolean withPath;
    private Boolean isAwsS3;
    private String appId;

    public FileClientConfiguration(String endpoint,
                                   String accessKey,
                                   String secretKey) {
        this.endpoint = endpoint;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.withPath = false;
        this.isAwsS3 = false;
    }

    public FileClientConfiguration(String endpoint,
                                   String accessKey,
                                   String secretKey,
                                   String region,
                                   Boolean withPath) {
        this.endpoint = endpoint;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.region = region;
        this.withPath = withPath;
        this.isAwsS3 = false;
    }


    public FileClientConfiguration(String endpoint,
                                   String accessKey,
                                   String secretKey,
                                   String region,
                                   Boolean withPath,
                                   Boolean isAwsS3) {
        this.endpoint = endpoint;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.region = region;
        this.withPath = withPath;
        this.isAwsS3 = isAwsS3;
    }


    public static String getDefaultRegion() {
        return DEFAULT_REGION;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public String getRegion() {
        return region;
    }

    public Boolean getWithPath() {
        return withPath;
    }

    public Boolean getAwsS3() {
        return isAwsS3;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }
}
