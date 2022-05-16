package com.cr.utils;

import com.cr.RCSVisualizer.entity.ConsoleEntity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CmdUtils {

    public static void main(String[] args) {
        try {
            runCommand(" cmd /c javac -g com\\cr\\debuggee\\sample\\java\\*.java");
//            runCommand("java -Xdebug com.cr.debuggee.HelloWorld");
//            runCommand(" cmd /c dir");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ConsoleEntity runCommand(String command) throws Exception {
        Process pro = Runtime.getRuntime().exec(command);
        String stdout = printLines(" stdout:", pro.getInputStream());
        String stderr = printLines(" stderr:", pro.getErrorStream());
        pro.waitFor();
        System.out.println(" exitValue() " + pro.exitValue());
        int exitValue = pro.exitValue();
        return new ConsoleEntity().setStdout(stdout).setStderr(stderr).setExitValue(exitValue);
    }

    private static String printLines(String cmd, InputStream ins) throws Exception {
        StringBuilder output = new StringBuilder();
        String line = null;
        BufferedReader in = new BufferedReader(
                new InputStreamReader(ins));
        while ((line = in.readLine()) != null) {
            output.append(line).append("\n");
            System.out.println(cmd + " " + line);
        }
        return output.toString();
    }

}
