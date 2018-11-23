package io.choerodon.file.api.controller

import io.choerodon.file.IntegrationTestConfiguration
import io.choerodon.file.api.dto.FileDTO
import io.choerodon.file.app.service.FileService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.core.io.FileSystemResource
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class FileControllerSpec extends Specification {
    @Autowired
    TestRestTemplate testRestTemplate

    @Autowired
    FileController fileController

    private FileService fileService = Mock(FileService)


    def setup() {
        fileController.setFileService(fileService)
    }

    def "UploadFile"() {
        given: '请求参数准备'
        def bucket_name = "testBucket"
        def file_name = "fileName"
        def file = new FileSystemResource(new File(this.class.getResource('/testFileUpload.txt').toURI()))
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>()
        param.add("bucket_name", bucket_name)
        param.add("file_name", file_name)
        param.add("file", file)

        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<MultiValueMap<String, Object>>(param, null)

        and: 'mock'
        fileService.uploadFile(_, _, _) >> { return null }
        when: '向【上传文件】接口发送post请求'
        def entity = testRestTemplate.exchange("/v1/files", HttpMethod.POST, httpEntity, String)
        then: '状态码校验成功；方法参数调用成功'
        entity.statusCode.is5xxServerError()
        1 * fileService.uploadFile(_, _, _)
    }

    def "DeleteFile"() {
        given: "参数准备"
        def url = "testUrl"
        def bucket_name = "BucketName"
        and: 'mock'
        fileService.deleteFile(_, _) >> { return null }
        when: "向【删除文件】接口发送delete请求"
        HttpEntity<Object> httpEntity = new HttpEntity<>()
        def entity = testRestTemplate.exchange("/v1/files?bucket_name={bucket_name}&url={url}", HttpMethod.DELETE, httpEntity, Object, bucket_name, url)
        then: "状态码校验成功；方法参数调用成功"
        entity.statusCode.is2xxSuccessful()
        1 * fileService.deleteFile(_, _)
    }

    def "Upload"() {
        given: '请求参数准备'
        def bucket_name = "testBucket"
        def file_name = "fileName"
        def file = new FileSystemResource(new File(this.class.getResource('/testFileUpload.txt').toURI()))
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>()
        param.add("bucket_name", bucket_name)
        param.add("file_name", file_name)
        param.add("file", file)
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<MultiValueMap<String, Object>>(param, null)

        def fileDTO = new FileDTO("", "fileName", "originFileName")

        and: 'mock'
        fileService.uploadDocument(_, _, _) >> { return fileDTO }

        when: '向【上传文件】接口发送post请求'
        def entity = testRestTemplate.exchange("/v1/documents", HttpMethod.POST, httpEntity, Object)

        then: '状态码校验成功；方法参数调用成功'
        entity.statusCode.is2xxSuccessful()
        1 * fileService.uploadDocument(_, _, _)
    }
}
