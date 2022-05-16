package com.cr.RCSVisualizer.entity;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ConsoleEntity {
    String stdout;
    String stderr;
    int exitValue;
}
