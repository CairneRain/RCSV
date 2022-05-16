package com.cr.RCSVisualizer.entity;

import com.sun.jdi.*;
import com.sun.jdi.request.StepRequest;

import java.util.ArrayList;
import java.util.List;

public class EntityUtils {

    public static ValueEntity buildValue(ThreadReference tr, String variableName, Value value, boolean isArgument, StepRequest stepRequest, String packageName, boolean isOrigin) {
        ValueEntity newEntity = new ValueEntity();
        newEntity.setVariableName(variableName);
        newEntity.setTypeName(purify(packageName, value == null ? "null" : value.type().name()));
        newEntity.setArgument(isArgument);
        newEntity.setFields(new ArrayList<>());

        StringBuilder valueString = new StringBuilder();

        //Array
        if (value instanceof ArrayReference) {
            dealWithArrayObject(tr, variableName, (ArrayReference) value, isArgument, stepRequest, packageName, newEntity, valueString);
        }
        // String
        else if (value instanceof StringReference) {
            valueString.append(value);
        }
        // Object
        else if (value instanceof ObjectReference) {
            List<Field> fields = ((ObjectReference) value).referenceType().fields();
            // wrapper class
            if (isWrapperClass(newEntity.getTypeName())) {
                dealWithWrapperClass(tr, (ObjectReference) value, stepRequest, packageName, newEntity);
            }
            // collection
            if (newEntity.getTypeName().contains("java.util.")) {
                //arrayList
                if (newEntity.getTypeName().equals(TypeConstant.ARRAYLIST.getTypeName())) {
                    dealWithArrayList(tr, (ObjectReference) value, stepRequest, packageName, newEntity);
                }
                //hash set
                else if (newEntity.getTypeName().equals(TypeConstant.HASHSET.getTypeName())) {
                    dealWithHashSet(tr, (ObjectReference) value, stepRequest, packageName, newEntity);
                }
                //hash map
                else if (newEntity.getTypeName().equals(TypeConstant.HASHMAP.getTypeName())) {
                    dealWithHashMap(tr, (ObjectReference) value, stepRequest, packageName, newEntity);
                }
                valueString.append(purify(packageName, value.toString()));
            }
            //other object
            else {
                // traverse class fields
                dealWithOtherObj(tr, value, stepRequest, packageName, isOrigin, newEntity, fields);
                valueString.append(purify(packageName, value.toString()));
            }
//            Method toString=((ObjectReference) value).referenceType().methodsByName("toString","()Ljava/lang/String;").get(0);
//            stepRequest.disable();
//            valueString.append(((ObjectReference) value).invokeMethod(tr,toString, Collections.emptyList(),ObjectReference.INVOKE_SINGLE_THREADED));
//            stepRequest.enable();
        }
        // primitive type
        else if (value instanceof PrimitiveValue) {
            valueString.append(value);
        }
        newEntity.setValue(valueString.toString());
        return newEntity;
    }

    private static void dealWithOtherObj(ThreadReference tr, Value value, StepRequest stepRequest, String packageName, boolean isOrigin, ValueEntity newEntity, List<Field> fields) {
        for (Field field : fields) {
            String name = field.name(); // field name
            Value fieldValue = ((ObjectReference) value).getValue(field); // field value
            // only records field in customized object
            if (value.type().name().contains("com.cr.debuggee")) {
                ValueEntity elementEntity = buildValue(tr, name, fieldValue, false, stepRequest, packageName, false);
                newEntity.addField(elementEntity);
            }
            // fields in other package
            else if (isOrigin) {
                ValueEntity elementEntity = buildValue(tr, name, fieldValue, false, stepRequest, packageName, false);
                newEntity.addField(elementEntity);
            }
        }
    }

    private static void dealWithHashMap(ThreadReference tr, ObjectReference value, StepRequest stepRequest, String packageName, ValueEntity newEntity) {
        List<Field> fields = value.referenceType().fields();
        for (Field field : fields) {
            String name = field.name(); // field name
            Value fieldValue = value.getValue(field); // field value
//            if (name.equals("size")) {
//                ValueEntity elementEntity = buildValue(tr, name, fieldValue, false, stepRequest, packageName, true);
//                newEntity.addField(elementEntity);
//            } else if (name.equals("table")) {
//                ValueEntity elementEntity = buildValue(tr, name, fieldValue, false, stepRequest, packageName, true);
//                newEntity.addField(elementEntity);
//            } else if (name.equals("entrySet")) {
//                ValueEntity elementEntity = buildValue(tr, name, fieldValue, false, stepRequest, packageName, true);
//                newEntity.addField(elementEntity);
//            }
            //all
            ValueEntity elementEntity = buildValue(tr, name, fieldValue, false, stepRequest, packageName, true);
            newEntity.addField(elementEntity);
        }
    }

    private static void dealWithHashSet(ThreadReference tr, ObjectReference value, StepRequest stepRequest, String packageName, ValueEntity newEntity) {
        List<Field> fields = value.referenceType().fields();
        for (Field field : fields) {
            String name = field.name(); // field name
            Value fieldValue = value.getValue(field); // field value
//            if (name.equals("map")) {
//                ValueEntity elementEntity = buildValue(tr, name, fieldValue, false, stepRequest, packageName, true);
//                newEntity.addField(elementEntity);
//            }
            //all
            ValueEntity elementEntity = buildValue(tr, name, fieldValue, false, stepRequest, packageName, true);
            newEntity.addField(elementEntity);
        }
    }

    private static void dealWithArrayList(ThreadReference tr, ObjectReference value, StepRequest stepRequest, String packageName, ValueEntity newEntity) {
        List<Field> fields = value.referenceType().fields();
        for (Field field : fields) {
            String name = field.name(); // field name
            Value fieldValue = value.getValue(field); // field value
            if (name.equals("elementData")) {
                ValueEntity elementEntity = buildValue(tr, name, fieldValue, false, stepRequest, packageName, true);
                newEntity.addField(elementEntity);
            }
            else if (name.equals("size")) {
                ValueEntity elementEntity = buildValue(tr, name, fieldValue, false, stepRequest, packageName, true);
                newEntity.addField(elementEntity);
            }
        }
    }

    private static void dealWithWrapperClass(ThreadReference tr, ObjectReference value, StepRequest stepRequest, String packageName, ValueEntity newEntity) {
        List<Field> fields = value.referenceType().fields();
        for (Field field : fields) {
            String name = field.name(); // field name
            Value fieldValue = value.getValue(field); // field value
            // only save value field
            if (field.name().equals("value")) {
                ValueEntity elementEntity = buildValue(tr, name, fieldValue, false, stepRequest, packageName, true);
                newEntity.addField(elementEntity);
            }
        }
    }

    private static void dealWithArrayObject(ThreadReference tr, String variableName, ArrayReference value, boolean isArgument, StepRequest stepRequest, String packageName, ValueEntity newEntity, StringBuilder valueString) {
        List<Value> values = value.getValues();
        // traverse array element
        valueString.append("[");
        boolean flag = false;
        for (Value v : values) {
            if(v==null) continue;
            flag = true;
            ValueEntity elementEntity = buildValue(tr, "element of "+ variableName, v, isArgument, stepRequest, packageName,false);
            newEntity.addField(elementEntity);
            valueString.append(elementEntity.getValue()).append(",");
        }
        if (flag) {
            //delete extra comma
            valueString.deleteCharAt(valueString.length() - 1);
        }
        valueString.append("]");
    }

    private static boolean isWrapperClass(String typeName) {
        return (((typeName.equals(TypeConstant.BOOLEAN.getTypeName()) ||
                typeName.equals(TypeConstant.CHARACTER.getTypeName())) ||
                typeName.equals(TypeConstant.BYTE.getTypeName())) ||
                typeName.equals(TypeConstant.SHORT.getTypeName())) ||
                typeName.equals(TypeConstant.INTEGER.getTypeName()) ||
                typeName.equals(TypeConstant.LONG.getTypeName()) ||
                typeName.equals(TypeConstant.FLOAT.getTypeName()) ||
                typeName.equals(TypeConstant.DOUBLE.getTypeName());
    }

    public static String purify(String packageName,String stackFrame) {
        String[] split = stackFrame.split(packageName + ".");
        return split.length > 1 ? String.join("",split) : stackFrame;
    }

}
