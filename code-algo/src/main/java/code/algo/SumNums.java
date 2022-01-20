package code.algo;

/**
 * @describe: 求N的阶和
 * 不使用乘除法和for、while、if、else、switch、case等条件判断
 * @author:  shuang.peng
 * @date: 2020/09/16
 */
public class SumNums {

    public static int sumNums(int n){
        // 利用 && 的短路功能，当n>1为false时，不执行后面的表达式
        boolean b = (n>1) && (n+=sumNums(n-1)) == 0;
        System.out.println(n);
        return 0;
    }

    public static void main(String[] args) {
        sumNums(10);
    }
}
