package pross.code.agent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @describe:
 * @author: 彭爽pross
 * @date: 2019/04/29
 */
public class TestTransformer implements ClassFileTransformer {

    final  static String prefix = "\n";
    // 被处理的方法列表
    final static Map<String, List<String>> methodMap = new HashMap<String, List<String>>();


    public void add(String methodString){
        String className = methodString.substring(0,methodString.lastIndexOf("."));
        String methodName = methodString.substring(methodString.lastIndexOf(".")+1);
        List<String> list = methodMap.get(className);


    }


    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

//        System.out.println(className.replace("/","."));
//        return classfileBuffer;
        System.out.println(className);
        return new byte[0];
    }
}
