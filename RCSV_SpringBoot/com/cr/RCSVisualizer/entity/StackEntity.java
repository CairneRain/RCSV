package com.cr.RCSVisualizer.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class StackEntity {

    //format class:lineNum
    private String lineInfo;

    //format class.method(arguments)
    private String callMethodInfo;

    private List<ValueEntity> values;

    private String console;

    public StackEntity(String lineInfo, String callMethodInfo) {
        this.lineInfo = lineInfo;
        this.callMethodInfo = callMethodInfo;
        this.values = new ArrayList<>();
    }
}
