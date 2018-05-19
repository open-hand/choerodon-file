# file-service

The file service is built on minio server, we can use minio client to upload and delete files.

## Feature

- After uploading the file to the server, a http url will be returned.
- There will be nothing to return after deleting file.

## Requirements

Before starting this server, you shoud config the minio server endpoint, accessKey and secretKey.For example:

```yml
minio:
  endpoint: http:127.0.0.1:8888/minio
  accessKey: choerodon
  secretKey: 123456
```

## To get the code

```
git clone https://github.com/choerodon/file-service.git
```

## Installation and Getting Started
 
 * Build the minio server
 * Start the framework server and file server

## Dependencies

- io.minio

## Reporting Issues

If you find any shortcomings or bugs, please describe them in the Issue.
    
## How to Contribute

Pull requests are welcome! Follow this link for more information on how to contribute.