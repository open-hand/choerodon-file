package io.choerodon.file.infra.config

import io.choerodon.file.IntegrationTestConfiguration
import io.choerodon.file.infra.exception.FileUploadException
import org.apache.poi.util.IOUtils
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.mock.web.MockMultipartFile
import org.springframework.web.multipart.MultipartFile
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * @author dongfan117@gmail.com
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class FileClientSpec extends Specification {
    private FileClient fileClient = new FileClient()
    private FileClientConfiguration fileClientConfig = Mock(FileClientConfiguration)

    def "Init"() {
        given: "参数准备"
        FileClient fileClient1 = new FileClient()
        boolean isAwsS3 = true
        String endpoint = "http://127.0.0.1:8888"
        String accessKey = "admin"
        String secretKey = "admin"
        String region = null
        Boolean withPath = false
        String appId = null

        when: "init fileClientConfig"
        FileClientConfiguration fileClientConfig1 = new FileClientConfiguration(endpoint, accessKey, secretKey)
        fileClient1.setFileClientConfig(fileClientConfig1)
        fileClient1.initClient(isAwsS3)
        then:
        fileClient1.getAmazonS3() != null
        when: "init"
        FileClientConfiguration fileClientConfig2 = new FileClientConfiguration(endpoint, accessKey, secretKey, region, withPath)
        fileClient1.setFileClientConfig(fileClientConfig2)
        fileClient1.initClient(isAwsS3)
        then:
        fileClient1.getAmazonS3() != null
        when: "init"
        FileClientConfiguration fileClientConfig3 = new FileClientConfiguration(endpoint, accessKey, secretKey, region, withPath, isAwsS3)
        fileClient1.initClient()
        then:
        fileClient1.getAmazonS3() != null
        when: "init minio"
        FileClientConfiguration fileClientConfig4 = new FileClientConfiguration(endpoint, accessKey, secretKey, region, withPath, false)
        fileClient1.setFileClientConfig(fileClientConfig4)
        fileClient1.initClient()
        then:
        fileClient1.getMinioClient() != null
    }

    def "DoesBucketExist[BucketNotExistException]"() {
        given: "参数准备"
        def bucketName = "file-service"
        FileClient fileClient = new FileClient()
        FileClientConfiguration fileClientConfig
        String endpoint = "http://127.0.0.1:8888"
        String accessKey = "admin"
        String secretKey = "admin"
        String region = null
        Boolean withPath = true
        String appId = "123456"
        when: "isMinioClient"

        fileClientConfig = new FileClientConfiguration(endpoint, accessKey, secretKey, null, false, false)
        fileClient.setFileClientConfig(fileClientConfig)

        def result = fileClient.doesBucketExist(bucketName)
        then: "抛出异常"
        result == null
        def e = thrown(FileUploadException)
        e.getMessage() == "error.file.bucket.error"
        when: "isAwsS3"

        fileClientConfig = new FileClientConfiguration(endpoint, accessKey, secretKey, null, true, true)
        fileClientConfig.setAppId(appId)
        fileClient.setFileClientConfig(fileClientConfig)
        result = fileClient.doesBucketExist(bucketName)
        then: "抛出异常"
        result == null
        e = thrown(FileUploadException)
        e.getMessage() == "error.file.bucket.error"
    }

    def "MakeBucket[BucketCreateException]"() {
        given: "参数准备"
        def bucketName = "file-service"
        FileClient fileClient = new FileClient()
        FileClientConfiguration fileClientConfig
        String endpoint = "http://127.0.0.1:8888"
        String accessKey = "admin"
        String secretKey = "admin"
        String region = null
        Boolean withPath = true
        String appId = "123456"
        when: "isMinioClient"

        fileClientConfig = new FileClientConfiguration(endpoint, accessKey, secretKey, null, false, false)
        fileClient.setFileClientConfig(fileClientConfig)

        def result = fileClient.makeBucket(bucketName)
        then: "抛出异常"
        result == null
        def e = thrown(FileUploadException)
        e.getMessage() == "error.file.bucket.error"
        when: "isAwsS3"

        fileClientConfig = new FileClientConfiguration(endpoint, accessKey, secretKey, null, true, true)
        fileClientConfig.setAppId(appId)
        fileClient.setFileClientConfig(fileClientConfig)
        result = fileClient.makeBucket(bucketName)
        then: "抛出异常"
        result == null
        e = thrown(FileUploadException)
        e.getMessage() == "error.file.bucket.error"
    }

    def "GetObjectUrl[FileUploadException]"() {
        given: "参数准备"
        def bucketName = "file-service"
        def fileName = "test.png"
        FileClient fileClient = new FileClient()
        FileClientConfiguration fileClientConfig
        String endpoint = "http://127.0.0.1:8888"
        String accessKey = "admin"
        String secretKey = "admin"
        String appId = "123456"
        when: "isMinioClient"

        fileClientConfig = new FileClientConfiguration(endpoint, accessKey, secretKey, null, false, false)
        fileClient.setFileClientConfig(fileClientConfig)

        def result = fileClient.getObjectUrl(bucketName, fileName)
        then: "抛出异常"
        result == null
        def e = thrown(FileUploadException)
        e.getMessage() == "error.file.notExist"
        when: "isAwsS3"

        fileClientConfig = new FileClientConfiguration(endpoint, accessKey, secretKey, null, true, true)
        fileClientConfig.setAppId(appId)
        fileClient.setFileClientConfig(fileClientConfig)
        result = fileClient.getObjectUrl(bucketName, fileName)
        then: "抛出异常"
        result == null
        e = thrown(FileUploadException)
        e.getMessage() == "error.file.notExist"
    }

    def "PutObject[FileNotExistException]"() {
        given: "参数准备"
        def bucketName = "file-service"
        def fileName = "testFileUpload.txt"
        FileClient fileClient = new FileClient()
        FileClientConfiguration fileClientConfig
        String endpoint = "http://127.0.0.1:8888"
        String accessKey = "admin"
        String secretKey = "admin"
        String appId = "123456"
        def file = new File(this.class.getResource('/testFileUpload.txt').toURI())
        FileInputStream input = new FileInputStream(file)
        MultipartFile multipartFile = new MockMultipartFile("file", file.getName(), "text/plain", IOUtils.toByteArray(input))
        when: "isMinioClient"

        fileClientConfig = new FileClientConfiguration(endpoint, accessKey, secretKey, null, false, false)
        fileClient.setFileClientConfig(fileClientConfig)

        def result = fileClient.putObject(bucketName, fileName, multipartFile)
        then: "抛出异常"
        result == null
        def e = thrown(FileUploadException)
        e.getMessage() == "error.file.upload"
        when: "isAwsS3"

        fileClientConfig = new FileClientConfiguration(endpoint, accessKey, secretKey, null, true, true)
        fileClientConfig.setAppId(appId)
        fileClient.setFileClientConfig(fileClientConfig)
        result = fileClient.putObject(bucketName, fileName, multipartFile)
        then: "抛出异常"
        result == null
        e = thrown(FileUploadException)
        e.getMessage() == "error.file.upload"
    }

    def "RemoveObject[FileDeleteException]"() {
        given: "参数准备"
        def bucketName = "file-service"
        def fileName = "testFileUpload.txt"
        FileClient fileClient = new FileClient()
        FileClientConfiguration fileClientConfig
        String endpoint = "http://127.0.0.1:8888"
        String accessKey = "admin"
        String secretKey = "admin"
        String appId = "123456"
        when: "isMinioClient"

        fileClientConfig = new FileClientConfiguration(endpoint, accessKey, secretKey, null, false, false)
        fileClient.setFileClientConfig(fileClientConfig)

        def result = fileClient.removeObject(bucketName, fileName)
        then: "抛出异常"
        result == null
        def e = thrown(FileUploadException)
        e.getMessage() == "error.file.delete"
        when: "isAwsS3"

        fileClientConfig = new FileClientConfiguration(endpoint, accessKey, secretKey, null, true, true)
        fileClientConfig.setAppId(appId)
        fileClient.setFileClientConfig(fileClientConfig)

        result = fileClient.removeObject(bucketName, fileName)
        then: "抛出异常"
        result == null
        e = thrown(FileUploadException)
        e.getMessage() == "error.file.delete"
    }

    def "GetPrefixUrl"() {
        given: "参数准备"
        def bucketName = "file-service"
        def appId = "123456"
        FileClient fileClient = new FileClient()
        FileClientConfiguration fileClientConfig
        String endpoint = "http://127.0.0.1:8888"
        String accessKey = "admin"
        String secretKey = "admin"
        when: "查询前缀"

        fileClientConfig = new FileClientConfiguration(endpoint, accessKey, secretKey, null, false, true)
        fileClient.setFileClientConfig(fileClientConfig)

        def result = fileClient.getPrefixUrl(bucketName)
        then: "返回正确结果"
        result == endpoint + "/" + bucketName + "/"
    }

    def "GetBucketName"() {
        given: "参数准备"
        def bucketName = "file-service"
        def appId = "123456"
        FileClient fileClient = new FileClient()
        FileClientConfiguration fileClientConfig
        String endpoint = "http://127.0.0.1:8888"
        String accessKey = "admin"
        String secretKey = "admin"
        when: "不存在appId"

        fileClientConfig = new FileClientConfiguration(endpoint, accessKey, secretKey, null, true, true)
        fileClient.setFileClientConfig(fileClientConfig)

        def result = fileClient.getBucketName(bucketName)
        then: "返回正确结果"
        result == "file-service"
        when: "不存在appId"

        fileClientConfig.setAppId(appId)
        fileClient.setFileClientConfig(fileClientConfig)

        result = fileClient.getBucketName(bucketName)
        then: "返回正确结果"
        result == "file-service" + "-" + "123456"
    }

}
