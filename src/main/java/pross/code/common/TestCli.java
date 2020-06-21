package pross.code.common;


import org.apache.commons.cli.*;

import java.util.List;

/**
 * @describe: apache commons cli demo
 * @author: 彭爽pross
 * @date: 2019/08/16
 */
public class TestCli {

    public static void main(String[] args) throws ParseException {
        //1。访问参数必须加-,如果不加则认为是前一参数的value,也可以写在一起，用空格隔开
        //2.参数不能有空格，否则commandLine.getOptionValue(name.trim())获取不到值，commandLine.getArgList()则能获取到值（原因没有仔细看）
        String arg[] = { "-h", "-s", "-r","nameOne nameTwo", "-start" ,"-x", "helloWord"};
//		String arg[] = { "-h", "-s", "-r nameOne nameTwo", "-start" ,"-x", "helloWord"};
        testOptions(arg);
    }

    public static void testOptions(String[] args) throws ParseException {
        // CLI定义
        Options option = new Options();
        option.addOption("h","help", false, "this is help");
        option.addOption("start","start-all", false, "this app is start ....");
        option.addOption("stop","stop-all", false, "this app is stop ....");
        option.addOption("s", "stop-all", false, "this app is stop ....");
        option.addOption("r", "run", true, "this app is stop ....");
        option.addOption("x", "rsn", true, "this app is stop ....");
        // CLI的解析
//        CommandLineParser parser = new DefaultParser();
        CommandLine commandLine = null;

        HelpFormatter hf = new HelpFormatter();
        hf.setWidth(110);
        try {
//            commandLine = parser.parse(option, args);
        } catch (Exception e) {
            hf.printHelp("test", option, true);
            System.err.println(e.getLocalizedMessage());
            return;
        }

        // CLI询问
        if (commandLine.hasOption("h")) {
            List<?> list = commandLine.getArgList();
            args = commandLine.getArgs();
            list.forEach(h-> System.out.println(h));
            System.out.println("h execte...");

        }
        // 打印opts的名称和值
        System.out.println("--------------------------------------");
        Option[] opts = commandLine.getOptions();
        if (opts != null) {
            for (Option opt1 : opts) {
                String name = opt1.getOpt();
                String value = commandLine.getOptionValue(name.trim());
                System.out.println(name + "=>" + value);
            }
        }
        System.out.println("--------------------------------------");
    }
}
