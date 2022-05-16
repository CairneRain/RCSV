package com.cr.RCSVisualizer;

import com.cr.RCSVisualizer.entity.EntityUtils;
import com.cr.RCSVisualizer.entity.StackEntity;
import com.cr.RCSVisualizer.entity.ValueEntity;
import com.sun.jdi.*;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.connect.LaunchingConnector;
import com.sun.jdi.connect.VMStartException;
import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.event.LocatableEvent;
import com.sun.jdi.request.ClassPrepareRequest;
import com.sun.jdi.request.StepRequest;
import lombok.Data;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.cr.RCSVisualizer.entity.EntityUtils.purify;

@Data
public class RCSDebugger {

    private String debuggeeName;
    private String packageName;
    private VirtualMachine vm;

    private int[] breakPointLines;
    private StepRequest stepIntoRequest;
    private StepRequest stepOverRequest;
    private StepRequest stepOutRequest;
    List<List<StackEntity>> stackListList = new ArrayList<>();
    private StringBuilder currentConsoleOutput = new StringBuilder();

    /**
     * Sets the debuggee class as the main argument in the connector
     * and launches the VM
     * @return VirtualMachine
     */
    public VirtualMachine connectAndLaunchVM() throws IOException, IllegalConnectorArgumentsException, VMStartException {
        LaunchingConnector launchingConnector = Bootstrap.virtualMachineManager().defaultConnector();
        Map<String, Connector.Argument> arguments = launchingConnector.defaultArguments();
        arguments.get("main").setValue(debuggeeName);
        //return virtualMachine
        return launchingConnector.launch(arguments);
    }

    /**
     * Creates a request to prepare the debuggee class, add filter as the debuggee class and enables it
     */
    public void enableClassPrepareRequest(VirtualMachine vm) {
        ClassPrepareRequest classPrepareRequest = vm.eventRequestManager().createClassPrepareRequest();
        classPrepareRequest.addClassFilter(debuggeeName);
        classPrepareRequest.enable();
    }

    /**
     * setup step requests
     */
    public void prepareStepRequest(VirtualMachine vm, ClassPrepareEvent event) {
        //enable step request for last break point
        this.stepOverRequest = vm.eventRequestManager().createStepRequest(event.thread(), StepRequest.STEP_LINE, StepRequest.STEP_OVER);
        this.stepOutRequest = vm.eventRequestManager().createStepRequest(event.thread(), StepRequest.STEP_LINE, StepRequest.STEP_OUT);
        this.stepIntoRequest = vm.eventRequestManager().createStepRequest(event.thread(), StepRequest.STEP_LINE, StepRequest.STEP_INTO);
        stepIntoRequest.enable();
    }

    /**
     * Only display stack frame in debuggee class
     * (Simple)
     */
    public void displayDebuggeeFrameStack(LocatableEvent event) throws AbsentInformationException, IncompatibleThreadStateException {
        ThreadReference tr = event.thread();
        StackFrame stackFrame = tr.frame(0);
        // only display frame stack from debuggee class
        // == remove if to enable the advance mode ==
        if(!stackFrame.location().toString().contains(debuggeeName)){
            return;
        }
        List<StackEntity> currentStack = new ArrayList<>();
        for (StackFrame frame : tr.frames()) {
            try {
                currentStack.add(toStackEntity(tr,frame));
            } catch (ClassNotLoadedException | InvocationException | InvalidTypeException e) {
                e.printStackTrace();
            }
        }
        // ignore redundant
        if (!(stackListList.size() == 0)) {
            List<StackEntity> prev = stackListList.get(stackListList.size() - 1);
            currentStack.get(0).setConsole(printDebuggeeConsole());
            if (!prev.equals(currentStack)) {
                stackListList.add(currentStack);
            }
        } else {
            currentStack.get(0).setConsole(printDebuggeeConsole());
            stackListList.add(currentStack);
        }
    }

    //convert stackFrame to stackEntity
    private StackEntity toStackEntity(ThreadReference tr, StackFrame stackFrame) throws AbsentInformationException, ClassNotLoadedException, IncompatibleThreadStateException, InvocationException, InvalidTypeException {
        StackEntity stackEntity = new StackEntity(purify(packageName, stackFrame.location().toString())
                , purify(packageName, stackFrame.location().method().toString()));

        //stack variables information
        Map<LocalVariable, Value> visibleVariables = stackFrame.getValues(stackFrame.visibleVariables());
        if(!visibleVariables.isEmpty()){
            for (Map.Entry<LocalVariable, Value> entry : visibleVariables.entrySet()) {
                LocalVariable variable=entry.getKey();
                Value value=entry.getValue();
                if(value==null) continue;
                // print argument
                if (variable.isArgument()) {
                    ValueEntity entity = EntityUtils.buildValue(tr, variable.name(), value, true, stepIntoRequest,packageName,true);
                    stackEntity.getValues().add(entity);
                }
                // other variables
                else{
                    ValueEntity entity = EntityUtils.buildValue(tr, variable.name(), value, false,stepIntoRequest,packageName,true);
                    stackEntity.getValues().add(entity);
                }
            }
        }
        return stackEntity;
    }

    /**
     *  output the debuggee's console
     */
    public String printDebuggeeConsole(){
        InputStreamReader reader = new InputStreamReader(vm.process().getInputStream());
        char[] buf = new char[512];
        try{
            if(reader.ready()){
                reader.read(buf);
                currentConsoleOutput.append(new String(buf).replaceAll("\\u0000", ""));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return currentConsoleOutput.toString();
    }
}