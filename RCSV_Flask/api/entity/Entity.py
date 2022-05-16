import json


class StackEntity:
    def __init__(self, lineInfo, callMethodInfo):
        self.values = []
        self.lineInfo = lineInfo
        self.callMethodInfo = callMethodInfo
        self.console = ""

    def addValue(self, value):
        if value is not None:
            self.values.append(value)

    def toString(self):
        return "lineInfo:{},callMethod:{},values:{}".format(self.lineInfo, self.callMethodInfo, self.values)

    def toJSON(self):
        valuesJson = []
        if len(self.values) > 0:
            for value in self.values:
                valuesJson.append(value.toJSON())
        return {
            "lineInfo": self.lineInfo,
            "callMethodInfo":self.callMethodInfo,
            "console":self.console,
            "values":valuesJson
        }


class ValueEntity:
    def __init__(self, variableName, typeName, value, isArgument):
        self.fields = []
        self.variableName = variableName
        self.typeName = typeName
        self.value = value
        self.isArgument = isArgument

    def addField(self, field):
        if field is not None:
            self.fields.append(field)

    def toString(self):
        return "variableName:{},typeName:{},value:{},isArgument:{},fields:[{}]" \
            .format(self.variableName, self.typeName, self.value, self.isArgument,
                    ",".join([field.toString() for field in self.fields]))

    def toJSON(self):
        fieldsJson = []
        if len(self.fields) > 0:
            for field in self.fields:
                fieldsJson.append(field.toJSON())
        return {
            "variableName":self.variableName,
            "typeName":self.typeName,
            "value":self.value,
            "isArgument":self.isArgument,
            "fields":fieldsJson
        }


class ConsoleEntity:
    def __init__(self):
        self.stdout = None
        self.stderr = None
        self.exitValue = None

    def toJSON(self):
        return {
            'stdout':self.stdout,
            'stderr':self.stderr,
            'exitValue':self.exitValue
        }
