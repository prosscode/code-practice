package code.java.calcite;

import org.apache.calcite.sql.*;

import static org.apache.calcite.sql.SqlKind.IDENTIFIER;

/**
 * @describe: code.java.calcite demo
 * @author: 彭爽 pross.peng
 * @date: 2020/05/20
 */
public class SqlTableParse {

    // 解析出所有的表
    public static void parseTableNode(SqlNode sqlNode, SqlParseResult sqlParseResult){
        SqlKind sqlNodeKind = sqlNode.getKind();
        System.out.println("kind:"+sqlNodeKind);
        switch(sqlNodeKind){
            // insert类型
            case INSERT:
                // insert类型
                System.out.println("insert:"+sqlNode.toString());
                SqlInsert sqlInsert = (SqlInsert) sqlNode;
                SqlNode targetTable = sqlInsert.getTargetTable();
                SqlNode source = sqlInsert.getSource();
                SqlNodeList columnList = sqlInsert.getTargetColumnList();
                // set
                sqlParseResult.setResultFields(columnList);
                sqlParseResult.addSourceTable(source.toString());
                sqlParseResult.addTargetTable(targetTable.toString());

                parseTableNode(sqlNode, sqlParseResult);
                break;
            case SELECT:
                // select类型
                System.out.println("select:"+sqlNode.toString());
                SqlNode sqlFrom = ((SqlSelect) sqlNode).getFrom();
                if(null != sqlFrom) {
                    if (sqlFrom.getKind() == IDENTIFIER) {
                        System.out.println("IDENTIFIER:"+sqlFrom.toString());
                        sqlParseResult.addSourceTable(sqlFrom.toString());
                    } else {
                        System.out.println("parse:"+sqlFrom.getKind());
                        parseTableNode(sqlFrom, sqlParseResult);
                    }
                }
                break;
            case JOIN:
                // join类型
                System.out.println("join:"+sqlNode.toString());
                SqlNode left = ((SqlJoin) sqlNode).getLeft();
                SqlNode right = ((SqlJoin) sqlNode).getRight();

                System.out.println(left+"==="+right);
                System.out.println(left.getKind());

//                parseTableNode(sqlNode,sqlParseResult);
                break;
            default:
                // do nothing
                SqlKind kind = sqlNode.getKind();
                System.out.println(kind+"==");
                break;
        }

    }

}
