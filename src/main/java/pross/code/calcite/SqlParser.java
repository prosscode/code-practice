package pross.code.calcite;

import org.apache.calcite.config.Lex;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.parser.SqlParseException;

import static pross.code.calcite.SqlTableParse.parseTableNode;

/**
 * @describe: sql解析工具类
 * @author: 彭爽 pross.peng
 * @date: 2020/05/20
 */
public class SqlParser {

    static String insertSql = "INSERT INTO tmp_node" +
            "SELECT s1.id1, s1.id2, s2.val1" +
            "FROM source1 as s1 INNER JOIN source2 AS s2" +
            "on s1.id1 = s2.id1 and s1.id2 = s2.id2 where s1.val1 > 5 and s2.val2 = 3";

    static String selectSql = "SELECT s1.id1, s1.id2, s2.val1 FROM source1 as s1 INNER JOIN source2 AS s2 on s1.id1 = s2.id1 and s1.id2 = s2.id2 where s1.val1 > 5 and s2.val2 = 3";


    public static void main(String[] args) {
        // 配置
        org.apache.calcite.sql.parser.SqlParser.Config build = org.apache.calcite.sql.parser.SqlParser.configBuilder().setLex(Lex.JAVA).build();
        // 解析器
        org.apache.calcite.sql.parser.SqlParser parser = org.apache.calcite.sql.parser.SqlParser.create(selectSql, build);
        SqlNode sqlNode = null;
        try {
            sqlNode = parser.parseStmt();
        } catch (SqlParseException e) {
            e.printStackTrace();
        }
        SqlParseResult sqlParseResult = new SqlParseResult();
        parseTableNode(sqlNode,sqlParseResult);

    }

}
