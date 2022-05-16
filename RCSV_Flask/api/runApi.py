from flask import request
from api import getSampleCodeApi
import uuid
import os
import sys
import json

from api.entity.EntityUtils import toValueEntity, ConsoleOutput
from api.entity.Entity import StackEntity, ValueEntity, ConsoleEntity
import inspect
import traceback

USER_CODE_DIR = './debuggeee'
USER_FILE = 'userCode.py'
RCSV_CONSTANT = ['__name__', '__doc__', '__package__', '__loader__', '__spec__', '__annotations__', '__builtins__',
                 '__file__', '__cached__', 'request', 'getSampleCodeApi', 'uuid', 'os', 'sys', 'StackEntity',
                 'ValueEntity', 'toValueEntity', 'json', 'run', 'ConsoleOutput',
                 'ConsoleEntity', 'inspect', 'traceback', 'local_var'
                                                          'SAMPLE_CODE_PATH', 'USER_CODE_DIR', 'USER_FILE',
                 'RCSV_CONSTANT', 'Debugger', 'debugger', 'self', 'code', 'compiledCode', 'stdout']


class Debugger:

    def __init__(self, stackListList, currentStackList, stack):
        self.stackListList = stackListList
        self.currentStackList = currentStackList
        self.currentStack = stack
        self.consoleEntity = ConsoleEntity()
        self.consoleOutput = ConsoleOutput()

    def myTrace(self, frame, event, arg):
        if event == 'call':
            # only get information from User code
            if frame.f_code.co_filename != USER_FILE:
                return None
            # lineInfo = frame.f_code.co_filename + ':' + str(frame.f_lineno)
            return self.myTrace
        elif event == 'line':
            self.currentStackList = []
            # 每个 stack
            for stack in inspect.stack():
                # 在输入代码中运行的
                if stack.filename == USER_FILE:
                    self.currentStack = StackEntity(stack.filename + ":" + str(stack.lineno),
                                                    "{}({})".format(
                                                        stack.function if stack.function != "<module>" else "__main__",
                                                        ""))
                    # 从输出文件中读取输出
                    self.currentStack.console = self.consoleOutput.buff + ''
                    local_var = []
                    for key, value in stack.frame.f_locals.items():
                        if key not in RCSV_CONSTANT:
                            local_var.append(key)
                            self.currentStack.addValue(toValueEntity(key, value))

                    for key, value in stack.frame.f_globals.items():
                        if key not in RCSV_CONSTANT and key not in local_var:
                            self.currentStack.addValue(toValueEntity(key, value))
                    # 添加到 currentStackList
                    self.currentStackList.append(self.currentStack)

            # 添加到 stackListList
            if len(self.currentStackList) != 0:
                self.stackListList.append(self.currentStackList)
            return self.myTrace
        elif event == "exception":
            return None

    def run(self, code):
        # execute every line
        sys.settrace(self.myTrace)
        stdout = sys.stdout
        # execute user code
        try:
            compiledCode = compile(code, USER_FILE, 'exec')
            # 修改输出到consoleOutput
            sys.stdout = self.consoleOutput
            exec(compiledCode, {})
            self.consoleEntity.exitValue = 0
        except Exception as e:
            sys.stdout = stdout
            # 只保留"userCode.py的报错信息
            self.consoleEntity.stderr = traceback.format_exc()[traceback.format_exc().find('  File "userCode.py",'):-1]
            self.consoleEntity.exitValue = 1
            traceback.print_exc()
        # finish
        sys.settrace(None)
        sys.stdout = stdout

        # create user file
        userId = "".join(str(uuid.uuid1()).split('-'))
        userDir = os.path.join(USER_CODE_DIR, userId)
        if not os.path.exists(userDir):
            os.mkdir(userDir)
        with open(os.path.join(userDir, USER_FILE), 'w') as f:
            f.write(code)

        return json.dumps({'stack': [[stack.toJSON() for stack in stackList] for stackList in self.stackListList],
                           'console': self.consoleEntity.toJSON()})


def run(code):
    return Debugger([], [], None).run(code)


if __name__ == "__main__":
    debugger = Debugger([], [], None)
    print(debugger.run(getSampleCodeApi.getSampleCode('../debuggeee/sample/python/HelloWorld.py')))
    print(len(debugger.stackListList))
