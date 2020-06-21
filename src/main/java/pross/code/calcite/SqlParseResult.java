package pross.code.calcite;

import org.apache.calcite.sql.SqlNodeList;

import java.util.ArrayList;
import java.util.List;

/**
 * @describe: result table node
 * @author: 彭爽 pross.peng
 * @date: 2020/05/20
 */
public class SqlParseResult {

    private List<String> sourceTableList = new ArrayList<>();

    private List<String> targetTableList = new ArrayList<>();

    private String execSql;

    private SqlNodeList resultFields;

    public SqlNodeList getResultFields() {
        return resultFields;
    }

    public void setResultFields(SqlNodeList resultFields) {
        this.resultFields = resultFields;
    }

    public void addSourceTable(String sourceTable) {
        sourceTableList.add(sourceTable);
    }

    public void addTargetTable(String targetTable) {
        targetTableList.add(targetTable);
    }

    public List<String> getSourceTableList() {
        return sourceTableList;
    }

    public List<String> getTargetTableList() {
        return targetTableList;
    }

    public String getExecSql() {
        return execSql;
    }

    public void setExecSql(String execSql) {
        this.execSql = execSql;
    }
}

