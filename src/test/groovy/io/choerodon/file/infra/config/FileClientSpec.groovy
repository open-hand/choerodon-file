package io.choerodon.file.infra.config

import io.choerodon.file.IntegrationTestConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * @author dongfan117@gmail.com
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class FileClientSpec extends Specification {
    private FileClient fileClient = new FileClient()
    private FileClientConfiguration fileClientConfig
    def "Init" () {
        given: "参数准备"
        boolean isAwsS3 = true
        String endpoint = "http://127.0.0.1:8888"
        String accessKey = "admin"
        String secretKey = "admin"
        String region = null
        Boolean withPath = false
        String appId = null

        when: "init fileClientConfig"
        FileClientConfiguration fileClientConfig1 = new FileClientConfiguration(endpoint, accessKey, secretKey)
        fileClient.setFileClientConfig(fileClientConfig1)
        fileClient.initClient(isAwsS3)
        then: null
        when: "init"
        FileClientConfiguration fileClientConfig2 = new FileClientConfiguration(endpoint, secretKey, region, withPath)
        fileClient.setFileClientConfig(fileClientConfig2)
        fileClient.initClient(isAwsS3)
        then: null
        when: "init"
        FileClientConfiguration fileClientConfig3 = new FileClientConfiguration(endpoint, secretKey, region, withPath, isAwsS3)
        then: null
        when: "init minio"
        FileClientConfiguration fileClientConfig4 = new FileClientConfiguration(endpoint, secretKey, region, withPath, false)
        fileClient.setFileClientConfig(fileClientConfig4)
        then: null
    }
}
