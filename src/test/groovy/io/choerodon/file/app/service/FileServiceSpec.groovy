package io.choerodon.file.app.service

import io.choerodon.file.IntegrationTestConfiguration
import io.choerodon.file.app.service.impl.FileServiceImpl
import io.choerodon.file.infra.config.FileClient
import io.choerodon.file.infra.exception.FileUploadException
import org.apache.poi.util.IOUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.mock.web.MockMultipartFile
import org.springframework.web.multipart.MultipartFile
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class FileServiceSpec extends Specification {
    @Autowired
    FileServiceImpl fileService

    private FileClient fileClient = Mock(FileClient)

    void setup() {
        fileService.setFileClient(fileClient)
    }

    def "UploadFile"() {
        given: "参数准备"
        def bucketName = "test"
        def fileName = "testUploadFile"
        def file = new File(this.class.getResource('/testFileUpload.txt').toURI())
        FileInputStream input = new FileInputStream(file)
        MultipartFile multipartFile = new MockMultipartFile("file", file.getName(), "text/plain", IOUtils.toByteArray(input))
        def url = "xxx/xxx/testFileUpload.txt"
        and: "mock"
        fileClient.doesBucketExist(_) >> { return true }
        fileClient.getObjectUrl(_, _) >> { return url }
        when: "方法调用"
        def result = fileService.uploadFile(bucketName, fileName, multipartFile)
        then: '无异常抛出'
        result == url
    }

    def "UploadFile[Exception]"() {
        given: "参数准备"
        def bucketName = "test"
        def fileName = "testUploadFile"
        def file = new File(this.class.getResource('/testFileUpload.txt').toURI())
        FileInputStream input = new FileInputStream(file)
        MultipartFile multipartFile = new MockMultipartFile("file", file.getName(), "text/plain", IOUtils.toByteArray(input))
        and: "mock"
        fileClient.doesBucketExist(_) >> { return false }
//        fileClient.setBucketPolicy(_, _, _) >> { throw new Exception("exception") }
        when: "方法调用"
        def result = fileService.uploadFile(bucketName, fileName, multipartFile)
        then: '无异常抛出'
        result == null
        thrown(FileUploadException)

//        noExceptionThrown()
    }

    def "DeleteFile[bucketNameNotExist]"() {
        given: "参数准备"
        def bucketName = "bucketName"
        def url = "xxx/xxx/testFileUpload.txt"
        and: 'mock'
        fileClient.doesBucketExist(_) >> { return false }
        when: '方法调用'
        fileService.deleteFile(bucketName, url)
        then: "抛出异常"
        def e = thrown(FileUploadException)
//        e.getCause().getCode()=="error.bucketName.notExist"
    }

    def "DeleteFile[fileNotExist]"() {
        given: "参数准备"
        def bucketName = "bucketName"
        def url = "http://127.0.0.1:8888/bucketName/"
        and: 'mock'
        fileClient.doesBucketExist(_) >> { return true }
        when: '方法调用'
        fileService.deleteFile(bucketName, url)
        then: "抛出异常"
        def e1 = thrown(FileUploadException)
//        e1.getCause().getCode()=="error.file.notExist"

        and: '参数准备'
        def url2 = "http://127.0.0.1:8888/bucketName/fileName"
        fileClient.getObjectUrl(_, _) >> { return null }
        when: '方法调用'
        fileService.deleteFile(bucketName, url2)
        then: "抛出异常"
        def e2 = thrown(FileUploadException)
//        e2.getCause().getCode()=="error.file.notExist"
    }

    def "DeleteFile"() {
        given: "参数准备"
        def bucketName = "bucketName"
        def url = "http://127.0.0.1:8888/bucketName/fileName"
        def file = new File(this.class.getResource('/testFileUpload.txt').toURI())
        def is = new FileInputStream(file)
        and: 'mock'
        fileClient.doesBucketExist(_) >> { return true }
        fileClient.getObjectUrl(_, _) >> { return is }
        when: '方法调用'
        fileService.deleteFile(bucketName, url)
        then: "抛出异常"
        noExceptionThrown()
    }

    def "UploadDocument"() {
        given: "参数准备"
        def bucketName = "test"
        def originFileName = "testUploadFile"
        def file = new File(this.class.getResource('/testFileUpload.txt').toURI())
        FileInputStream input = new FileInputStream(file)
        MultipartFile multipartFile = new MockMultipartFile("file", file.getName(), "text/plain", IOUtils.toByteArray(input))
        and: "mock"
        fileClient.doesBucketExist(_) >> { return true }
        when: "方法调用"
        def result = fileService.uploadDocument(bucketName, originFileName, multipartFile)
        then: '无异常抛出'
        result.getEndPoint() != null
        result.getOriginFileName() == originFileName
        result.getFileName() != null
        noExceptionThrown()
    }

    def "UploadDocument[Exception]"() {
        given: "参数准备"
        def bucketName = "test"
        def originFileName = "testUploadFile"
        def file = new File(this.class.getResource('/testFileUpload.txt').toURI())
        FileInputStream input = new FileInputStream(file)
        MultipartFile multipartFile = new MockMultipartFile("file", file.getName(), "text/plain", IOUtils.toByteArray(input))
        and: "mock"
        fileClient.doesBucketExist(_) >> { return false }
//        minioClient.setBucketPolicy(_, _, _) >> { throw new Exception("exception") }
        when: "方法调用"
        def result = fileService.uploadDocument(bucketName, originFileName, multipartFile)
        then: '无异常抛出'
        result == null
        thrown(FileUploadException)
    }

    def "CutImage[Exception]"() {
        given: "参数准备"
        def file = new File(this.class.getResource('/icon.png').toURI())
        def rotate = 90
        def axisX = 100
        def axisY = 100
        def width = 300
        def height = 300

        FileInputStream input = new FileInputStream(file)
        MultipartFile multipartFile = new MockMultipartFile("file", file.getName(), "image/png", IOUtils.toByteArray(input))

        and: "mock"
        fileClient.doesBucketExist(_) >> { return false }
        when: "方法调用"
        def result = fileService.cutImage(multipartFile, rotate, axisX, axisY, width, height)
        then: '无异常抛出'
        result == null
        thrown(FileUploadException)
    }
}
