package bench;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


import bench.CommandHandler.Command;

import com.google.gson.Gson;


public class ConsoleServer {
    private CommandHandler cmdHandler;
    
    public ConsoleServer(String stdQueryFileName, String parserConf) throws IOException {
    	ParserAgent.init(parserConf);
    	BenchManager commandManager = new FileBenchManager(stdQueryFileName);
    
		cmdHandler = new CommandHandler(commandManager);
    }  
    
    public void run() throws IOException {
        BufferedReader brStdIn = new BufferedReader(
                new InputStreamReader(System.in, "utf-8"));
        String line;
        System.out.print(">> ");
        while((line = brStdIn.readLine()) != null) {
            line = line.trim();
            if(line.length() != 0) {
            	System.out.println(cmdHandler.handleCommand(line));
            }
            System.out.print(">> ");
        }
    }
    
    public static void main(String[] args) throws IOException {
//        org.apache.log4j.PropertyConfigurator.configure("./conf/log4j.properties");
//        String benchFileName = "src/tool/rm.bench/standardQuery.txt";
//        String parserConf = "./conf/qparser.conf";
//        new ConsoleServer(benchFileName, parserConf).run();

    }
}
