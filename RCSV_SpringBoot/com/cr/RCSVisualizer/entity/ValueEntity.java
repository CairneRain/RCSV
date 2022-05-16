package com.cr.RCSVisualizer.entity;

import lombok.Data;

import java.util.List;

@Data
public class ValueEntity {

    //value local variable name
    private String variableName;

    //value type name
    private String typeName;

    //value value
    private String value;

    //annotate the value role
    private boolean isArgument;

    //object value fields
    private List<ValueEntity> fields;

    public void addField(ValueEntity field) {
        this.fields.add(field);
    }

    @Override
    public String toString() {
        if(isArgument){
            return String.format("<Argument>%s(%s):%s", variableName, typeName, value);
        }
        else{
            return String.format("%s(%s):%s", variableName, typeName, value);
        }
    }
}
