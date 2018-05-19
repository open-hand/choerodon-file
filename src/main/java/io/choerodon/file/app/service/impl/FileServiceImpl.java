package io.choerodon.file.app.service.impl;

import io.choerodon.core.exception.CommonException;
import io.choerodon.file.app.service.FileService;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import io.minio.policy.PolicyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/4/16.
 * Email: fuqianghuang01@gmail.com
 */
@Service
public class FileServiceImpl implements FileService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileServiceImpl.class);

    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.accessKey}")
    private String accessKey;

    @Value("${minio.secretKey}")
    private String secretKey;

    @Override
    public String uploadFile(String backetName,String fileName,MultipartFile multipartFile) {
        try {
            // Create a minioClient with the Minio Server name, Port, Access key and Secret key.
            MinioClient minioClient = new MinioClient(endpoint, accessKey, secretKey);
            // Check if the bucket already exists.
            boolean isExist = minioClient.bucketExists(backetName);
            if(isExist) {
                LOGGER.debug("Bucket already exists.");
            } else {
                // Make a new bucket called asiatrip to hold a zip file of photos.
                minioClient.makeBucket(backetName);
                minioClient.setBucketPolicy(backetName,"", PolicyType.READ_ONLY);
            }
            InputStream is = multipartFile.getInputStream();
            // Get hapcloud uuid , in order to recontruct the file name
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            fileName = uuid +"_"+ fileName;
            // Upload the zip file to the bucket with putObject
            minioClient.putObject(backetName,fileName,is,"application/octet-stream");
            String url = minioClient.getObjectUrl(backetName,fileName);
            return url;
        } catch(MinioException m) {
            LOGGER.debug(m.getMessage());
        }catch (InvalidKeyException i){
            LOGGER.debug(i.getMessage());
        }catch (IOException io){
            LOGGER.debug(io.getMessage());
        }catch (NoSuchAlgorithmException n){
            LOGGER.debug(n.getMessage());
        }catch (XmlPullParserException x){
            LOGGER.debug(x.getMessage());
        }
        return null;
    }

    @Override
    public void deleteFile(String backetName,String url){
        try {
            MinioClient minioClient = new MinioClient(endpoint, accessKey, secretKey);
            boolean isExist = minioClient.bucketExists(backetName);
            if (!isExist){
                throw new CommonException("error.backetName.notExist");
            }

            String prefixUrl = endpoint+"/"+backetName+"/";
            int prefixUrlSize = prefixUrl.length();
            String fileName = url.substring(prefixUrlSize);
            if (fileName == null){
                throw new CommonException("error.file.notExist");
            }
            if (minioClient.getObject(backetName,fileName) == null){
                throw new CommonException("error.file.notExist");
            }
            minioClient.removeObject(backetName,fileName);
        } catch(MinioException m) {
            LOGGER.debug(m.getMessage());
            throw new CommonException(m.getMessage());
        }catch (InvalidKeyException i){
            LOGGER.debug(i.getMessage());
            throw new CommonException(i.getMessage());
        }catch (IOException io){
            LOGGER.debug(io.getMessage());
            throw new CommonException(io.getMessage());
        }catch (NoSuchAlgorithmException n){
            LOGGER.debug(n.getMessage());
            throw new CommonException(n.getMessage());
        }catch (XmlPullParserException x){
            LOGGER.debug(x.getMessage());
            throw new CommonException(x.getMessage());
        }
    }
}
