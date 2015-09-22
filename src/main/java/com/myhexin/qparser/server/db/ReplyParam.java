package com.myhexin.qparser.server.db;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReplyParam {
    private static String CONFIG_PATH;
    private static org.slf4j.Logger logger_ = org.slf4j.LoggerFactory.getLogger(ReplyParam.class.getName());
    public static String AUTO_MSG;
    public static String AUTO_TIME;
    private static String AUTO_SWITCH;
    private static String ON = "on";
    private static String OFF = "off";
    public static boolean isOn = false;

    public static void init(String confPath) {
        CONFIG_PATH = confPath;
        try {
            File f = new File(CONFIG_PATH);
            FileInputStream fis = new FileInputStream(f);
            InputStreamReader isr = new InputStreamReader(fis, "utf-8");
            BufferedReader reader = new BufferedReader(isr);
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("#"))
                    continue;
                if (line.startsWith("autoReply_switch")) {
                    String flagString = line.substring(line.indexOf("=") + 1);
                    if (flagString.equals(ON) || flagString.equals(OFF)) {
                        AUTO_SWITCH = flagString;
                        isOn = flagString.equals(ON) ? true : false;
                    }else {
                        logger_.info("AutoReply init Error:autoReply_switch[{}]",flagString);
                    }
                }
                if (line.startsWith("autoReply_startDate"))
                    AUTO_TIME = line.substring(line.indexOf("=") + 1);
                if (line.startsWith("autoReply_msg"))
                    AUTO_MSG = line.substring(line.indexOf("=") + 1);
            }
        } catch (Exception e) {
            logger_.error("Reply config initial Error [{}]", e.getMessage());
        }
    }

    public static boolean changeAutoMeg(String switchFlag, String msg) {
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        AUTO_TIME = time;
        AUTO_MSG = msg != null && msg.length() > 0 ? msg : AUTO_MSG;
        if (switchFlag!=null&&(switchFlag.equals(ON) || switchFlag.equals(OFF))){
            AUTO_SWITCH = switchFlag;
            isOn = switchFlag.equals(ON) ? true : false;
        }
        else {
            logger_.info("flash autoReply ERROR [{}]", switchFlag);
            return false;
        }
        changeFile();
        return true;
    }

    public static void changeFile() {
        try {
            File f = new File(CONFIG_PATH);
            FileInputStream fis = new FileInputStream(f);
            InputStreamReader isr = new InputStreamReader(fis, "utf-8");
            BufferedReader reader = new BufferedReader(isr);

            String line = null;
            StringBuilder sb = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("autoReply_switch")) {
                    sb.append("autoReply_switch=" + AUTO_SWITCH);
                } else if (line.startsWith("autoReply_setDate")) {
                    sb.append("autoReply_setDate=" + AUTO_TIME);
                } else if (line.startsWith("autoReply_msg")) {
                    sb.append("autoReply_msg=" + AUTO_MSG);
                }else{
                    sb.append(line);
                }
                sb.append(System.getProperty("line.separator"));
            }
            reader.close();
            RandomAccessFile raf = new RandomAccessFile(CONFIG_PATH, "rw");
            raf.write(sb.toString().getBytes());
            raf.close();
            logger_.info("switch [{}] automsg [{}]",AUTO_TIME+AUTO_SWITCH,AUTO_MSG);
        } catch (Exception e) {
            logger_.info("ReplaceFile ERROR [{}]", e.getMessage());
        }
    }
}