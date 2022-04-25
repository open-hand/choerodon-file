package script.db

databaseChangeLog(logicalFilePath: 'script/db/hfle_file.groovy') {
    changeSet(author: "qixiangyu@going-link.com", id: "2022-04-25-hfle_file-index") {
        createIndex(tableName: "hfle_file", indexName: "hfle_file_cn1") {
            column(name: "tenant_id")
            column(name: "bucket_name")
            column(name: "file_url")
        }
    }
}