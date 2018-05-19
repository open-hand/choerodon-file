package io.choerodon.file.api;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.file.app.service.FileService;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/4/16.
 * Email: fuqianghuang01@gmail.com
 */
@RestController
@RequestMapping(value = "/v1/organization/{organizationId}/file")
public class FileController {

    @Autowired
    private FileService fileService;

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "上传文件")
    @RequestMapping(value = "/backetName/{backetName}", method = RequestMethod.POST)
    public ResponseEntity<String> uploadFile(
            @PathVariable Long organizationId,
            @ApiParam(value = "backetName", required = true)
            @PathVariable String backetName,
            @ApiParam(value = "文件名", required = true)
            @RequestParam String fileName,
            @ApiParam(value = "上传文件")
            @RequestParam("file") MultipartFile multipartFile) {
        return Optional.ofNullable(fileService.uploadFile(backetName, fileName, multipartFile))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.file.upload"));
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "删除文件")
    @RequestMapping(value = "/backetName/{backetName}",method = RequestMethod.DELETE)
    public ResponseEntity deleteFile(
            @PathVariable Long organizationId,
            @ApiParam(value = "backetName", required = true)
            @PathVariable String backetName,
            @ApiParam(value = "文件地址",required = true)
            @RequestParam String url){
        fileService.deleteFile(backetName,url);
        return  new ResponseEntity<>(HttpStatus.OK);
    }

}
