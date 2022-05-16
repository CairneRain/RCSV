package com.cr.RCSVisualizer;

import com.cr.RCSVisualizer.entity.StackEntity;
import com.sun.jdi.VMDisconnectedException;
import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.event.StepEvent;

import java.util.List;

public class DebuggerUtils {

//    public static void main(String[] args){
//        List<List<StackEntity>> helloWorld = runDebugger("com.cr.debuggee.user1512669577132249088", "HelloWorld");
//        System.out.println(helloWorld);
//    }

    public static List<List<StackEntity>> runDebugger(String packageName, String debugeeName) {
        //create a debugger
        RCSDebugger debugger = new RCSDebugger();
        //set the debuggee
        debugger.setDebuggeeName(packageName+"."+debugeeName);
        debugger.setPackageName(packageName);

        try {
            //set virtual machine
            debugger.setVm(debugger.connectAndLaunchVM());
            debugger.enableClassPrepareRequest(debugger.getVm());

            EventSet eventSet;
            while ((eventSet = debugger.getVm().eventQueue().remove()) != null) {
                for (Event event : eventSet) {
                    // first event
                    if (event instanceof ClassPrepareEvent) {
                        System.out.println("Class " + ((ClassPrepareEvent) event).referenceType().name() + " is ready");
                        debugger.prepareStepRequest(debugger.getVm(), (ClassPrepareEvent)event);
                    }
                    // step event
                    if (event instanceof StepEvent) {
                        debugger.displayDebuggeeFrameStack((StepEvent) event);
                    }
                    debugger.getVm().resume();
                }
            }
        } catch (VMDisconnectedException e) {
            System.out.println("Virtual Machine is disconnected.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return debugger.getStackListList();
    }
}
