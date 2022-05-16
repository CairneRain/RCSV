package com.cr.Common.Service;

import com.cr.RCSVisualizer.entity.ConsoleEntity;

public interface DebugControllerService {
    public String run(String packageName,String className,ConsoleEntity console);

    public ConsoleEntity compile(String dir);
}
