namespace java org.pross.rpc.thrift

enum RequestType {
   SAY_HELLO,   //问好
   QUERY_TIME,  //询问时间
}

struct Request {
   1: required RequestType type;  // 请求的类型，必选
   2: required string name;       // 发起请求的人的名字，必选
   3: optional i32 age;           // 发起请求的人的年龄，可选
}
// 异常
exception RequestException {
   1: required i32 code;
   2: optional string reason;
}

// 服务名，接口：division()和sayHello()，将会使用到
service HelloMethod{
    i32 division(1:i32 param1,2:i32 param2) throws (1:RequestException re) //可能抛出异常
    string sayHello(1:string username)
}