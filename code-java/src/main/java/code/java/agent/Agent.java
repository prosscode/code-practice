package code.java.agent;

import java.lang.instrument.Instrumentation;

/**
 * @describe:
 * @author: 彭爽pross
 * @date: 2019/04/29
 */
public class Agent {

     public static void premain(String agentOps, Instrumentation inst){
         System.out.println("=====this is Agent premain function=====");
         inst.addTransformer(new TestTransformer());
     }
}
