package script.db

databaseChangeLog(logicalFilePath: 'script/db/datafix_for_hzero_1_4_0.groovy') {
    changeSet(author: 'wanghao', id: '2020-07-21-data-fix') {
        preConditions(onFail: "MARK_RAN") {
            tableExists(tableName: "hfle_file_edit_log")
            tableExists(tableName: "hfle_file")
        }
        sql("""
            UPDATE hfle_file_edit_log hfel
            SET hfel.tenant_id = ( SELECT hf.tenant_id FROM hfle_file hf WHERE hfel.file_id = hf.file_id)
            WHERE hfel.file_id in ( SELECT file_id FROM hfle_file);
            """)
    }
}