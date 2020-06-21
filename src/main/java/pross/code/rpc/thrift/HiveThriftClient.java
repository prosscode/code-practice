package pross.code.rpc.thrift;

import org.apache.hive.service.rpc.thrift.*;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import javax.security.sasl.SaslException;
import java.util.*;

/**
 * @describe:
 * @author: 彭爽pross
 * @date: 2019/05/30
 */
public class HiveThriftClient {

    private static String host = "172.18.84.138";
    private static int port = 10000;
    private static String username = "pross.peng";
    private static String password = "hive";
    private static TTransport transport;
    private static TCLIService.Client client;
    private TOperationState tOperationState;
    private Map resultMap = new HashMap<String, Object>();

    static {
        try {
            transport = QueryTool.getSocketInstance(host, port,username, password);
            client = new TCLIService.Client(new TBinaryProtocol(transport));
            transport.open();
        } catch (TTransportException e) {
            e.printStackTrace();
        } catch (SaslException e) {
            e.printStackTrace();
        }
    }


    /**
     * 提交sql查询，callback执行错误信息。return resp handle（contains session）
     * @param command
     * @return
     * @throws Exception
     */
    public TOperationHandle submitQuery(String command) throws Exception {
        TOperationHandle tOperationHandle;
        TExecuteStatementResp resp = null;

        // keep session
        TOpenSessionResp sessionResp = QueryTool.openSession(client);
        TSessionHandle sessHandle = sessionResp.getSessionHandle();
        String queue = "set mapred.job.queue.name=root.bqtask";
        TExecuteStatementReq execReq = new TExecuteStatementReq(sessHandle, command);
        TExecuteStatementReq execReq1 = new TExecuteStatementReq(sessHandle, queue);
        // 异步运行
        execReq.setRunAsync(true);
        // 执行sql
        client.ExecuteStatement(execReq1);
        resp = client.ExecuteStatement(execReq);
        // 获取执行的handle
        tOperationHandle = resp.getOperationHandle();

        if (tOperationHandle == null) {
            // 通过resp.getStatus() 拿到执行异常信息
            throw new Exception(resp.getStatus().getErrorMessage());
        }
        return tOperationHandle;
    }

    /**
     * 查询日志
     *
     * @param tOperationHandle
     * @return
     */
    public String getQueryLog(TOperationHandle tOperationHandle) throws TException {
        String log = "";
        if (tOperationHandle != null) {
            StringBuffer logs = new StringBuffer();
            TFetchResultsReq fetchReq = new TFetchResultsReq(tOperationHandle, TFetchOrientation.FETCH_NEXT, 1000);
            // 获取结果类型，日志1
            fetchReq.setFetchType((short) 1);
            TFetchResultsResp resp = client.FetchResults(fetchReq);
            TRowSet results = resp.getResults();

            if (null != results) {
//                RowSet rowSet = RowSetFactory.create(results, TProtocolVersion.HIVE_CLI_SERVICE_PROTOCOL_V7);
//                for (Object[] row : rowSet) {
//                    logs.append(DateFormatUtils.format(new Date(),"HH:mm:ss "));
//                    logs.append(String.valueOf(row[0])).append("\n");
//                }
            }
            log = logs.toString();
        }
        return log;
    }

    /**
     * 获取执行的状态
     * @param tOperationHandle
     * @return
     */
    public TOperationState getQueryHandleStatus(TOperationHandle tOperationHandle) throws TException {

        if (tOperationHandle != null) {
            TGetOperationStatusReq statusReq = new TGetOperationStatusReq(tOperationHandle);
            TGetOperationStatusResp statusResp = client.GetOperationStatus(statusReq);
            // 拿到状态
            tOperationState = statusResp.getOperationState();
        }
        return tOperationState;
    }


    /**
     * 获取执行的结果
     *
     * @param tOperationHandle
     * @return
     */
    public List<Object> getResults(TOperationHandle tOperationHandle) throws TException {
        TFetchResultsReq fetchReq = new TFetchResultsReq(tOperationHandle, TFetchOrientation.FETCH_FIRST, 10);

        TFetchResultsResp results = client.FetchResults(fetchReq);
        List<TColumn> list = results.getResults().getColumns();

        List<Object> listRow = new ArrayList<Object>();

        for (TColumn field : list) {
            Object obj = "";
            if (field.isSetStringVal()) {
                obj = field.getStringVal().getValues();
            } else if (field.isSetDoubleVal()) {
                obj =field.getDoubleVal().getValues();
            } else if (field.isSetI16Val()) {
                obj =field.getI16Val().getValues();
            } else if (field.isSetI32Val()) {
                obj = field.getI32Val().getValues();
            } else if (field.isSetI64Val()) {
                obj = field.getI64Val().getValues();
            } else if (field.isSetBoolVal()) {
                obj = field.getBoolVal().getValues();
            } else if (field.isSetByteVal()) {
                obj = field.getByteVal().getValues();
            }
            listRow.add(obj);
        }
        return listRow;
    }

    /**
     * 获取查询字段名
     * @param tOperationHandle
     * @return
     * @throws Throwable
     */
    public List<String> getColumns(TOperationHandle tOperationHandle) throws Throwable {
        TGetResultSetMetadataResp metadataResp;
        TGetResultSetMetadataReq metadataReq;
        TTableSchema tableSchema;
        metadataReq = new TGetResultSetMetadataReq(tOperationHandle);
        metadataResp = client.GetResultSetMetadata(metadataReq);

        List<TColumnDesc> columnDescs;
        List<String> columns = null;
        tableSchema = metadataResp.getSchema();

        if (tableSchema != null) {
            columnDescs = tableSchema.getColumns();
            columns = new ArrayList<String>();
            for (TColumnDesc tColumnDesc : columnDescs) {
                columns.add(tColumnDesc.getColumnName());
            }
        }
        return columns;
    }

    /**
     * 转化查询结果，由列转为行
     * @param objs
     * @return
     */
    public List<Object> toResults(List<Object> objs){
        List<Object> rets = new ArrayList<>() ;

        if (objs != null){
            List list = (List) objs.get(0) ;
            int  rowCnt = list.size() ;
            for(int i = 0; i <  rowCnt ; i++){
                rets.add(new ArrayList()) ;
            }
            for(int i = 0; i < objs.size(); i++){
                list =  (List) objs.get(i);
                for (int j = 0; j < rowCnt; j++){
                    ((List)rets.get(j)).add(list.get(j));
                }
            }
        }

        return rets;
    }

    /**
     * 取消查询
     * @param tOperationHandle
     */
    public void cancelQuery(TOperationHandle tOperationHandle) throws TException {
        if (tOperationState != TOperationState.FINISHED_STATE) {
            TCancelOperationReq cancelOperationReq = new TCancelOperationReq();
            cancelOperationReq.setOperationHandle(tOperationHandle);
            client.CancelOperation(cancelOperationReq);
        }
    }


    public static void main(String[] args) {
        List<Map<String, Object>> result = new ArrayList<>();
        try{
            HiveThriftClient instance = new HiveThriftClient();
//            instance.submitQuery("set mapred.job.queue.name=root.bqtask");
            TOperationHandle tOperationHandle = instance.submitQuery("select link_hs from baichuan.t_lalalink_init10 group by link_hs");
//            TOperationHandle tOperationHandle = instance.submitQuery("desc baichuan.t_lalalink_init10");

            while(instance.getQueryHandleStatus(tOperationHandle) == TOperationState.RUNNING_STATE){
                Thread.sleep(500);
                System.out.println(instance.getQueryLog(tOperationHandle));
            }

            List<String> columns = instance.getColumns(tOperationHandle);
            List<Object> results = instance.getResults(tOperationHandle);
            List<Object> toResults = instance.toResults(results);

            for(Object obj:toResults){
                Map<String, Object> tableObject = new LinkedHashMap<>();
                for(int i=0;i<columns.size();i++){
                    List objList = (List) obj;
                    tableObject.put(columns.get(i),objList.get(i));
                }
                result.add(tableObject);
            }

            System.out.println("result:"+result.size());
            result.forEach((keys)->{
                keys.forEach((key,value)->{
                    System.out.println(key+":"+value);
                });
                System.out.println("========");
            });

        }catch (TException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
