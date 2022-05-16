package com.cr.Common.Controller;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.cr.Common.CommonConstant;
import com.cr.Common.CommonUtils;
import com.cr.Common.Service.DebugControllerService;
import com.cr.RCSVisualizer.entity.ConsoleEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.Map;

@RestController
@RequestMapping("/debug")
@CrossOrigin
public class DebugController {

    @Autowired
    DebugControllerService debugControllerService;

    @PostMapping("/run")
    public String runDebugger(@RequestBody Map<String,Object> body) {
//        System.out.println(code);
        String className = "HelloWorld";
        Snowflake snowflake = IdUtil.getSnowflake(1, 1);
        String uuid = "user"+snowflake.nextIdStr();

        //create user dir
        String userDir = CommonConstant.FILE_DIR + "/" + uuid;
        String packageName = String.join(".", userDir.split("/"));
        File dir = new File(userDir);
        dir.mkdir();
        //create file
        String fileName = CommonConstant.FILE_DIR + "/" + uuid + "/" + className+".java";
        File file = new File(fileName);

        //write file
        String packageLine = "package " + packageName + ";\n";
        CommonUtils.writeFile(file, packageLine + body.get("code"));
        //compile
        ConsoleEntity console = debugControllerService.compile(userDir);
        return debugControllerService.run(packageName,className,console);
    }

    @PostMapping("/init")
    public String init(@Param("lang")String lang) {
        switch (lang) {
            case "text/x-java":
                File file = new File(CommonConstant.JAVA_SAMPLE_FILE);
                String result = CommonUtils.readFile(file);
                return result;
            case "python":
                return "print(\"test\")";
            default:
                return null;
        }
    }
}
