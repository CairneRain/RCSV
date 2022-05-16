package com.cr.Common.Service.ServiceImpl;

import cn.hutool.json.JSONUtil;
import com.cr.Common.Service.DebugControllerService;
import com.cr.RCSVisualizer.DebuggerUtils;
import com.cr.RCSVisualizer.entity.ConsoleEntity;
import com.cr.RCSVisualizer.entity.StackEntity;
import com.cr.utils.CmdUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DebugControllerServiceImpl implements DebugControllerService {
    @Override
    public String run(String packageName,String className,ConsoleEntity console) {
        //all step list of Stack Entity List
        List<List<StackEntity>> lists = DebuggerUtils.runDebugger(packageName, className);

        Map<String, Object> result = new HashMap<>();
        result.put("stack", lists);
        result.put("console", console);

        return JSONUtil.toJsonStr(result);
    }

    @Override
    public ConsoleEntity compile(String dir) {
        ConsoleEntity console = null;
        try {
//            CmdUtils.runCommand(" cmd /c javac -g com\\\\cr\\\\debuggee\\\\*.java\"");
            console = CmdUtils.runCommand(" cmd /c javac -g " + dir + "/*.java");
            if (console.getExitValue() == 0) {
                System.out.println("compile success");
            } else {
                System.out.println("compile fail");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            return console;
        }
    }
}
