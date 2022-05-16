import inspect

from api.entity.Entity import ValueEntity


def trimClass(clazz):
    return purifyClass(str(clazz)[:-2].split("<class '")[1])


def purifyClass(clazz):
    return clazz.replace("api.runApi.", "")


def toValueEntity(key, value):
    # Class
    if isinstance(value, type):
        # pass
        valueEntity = ValueEntity(key, trimClass(value.__class__), purifyClass(str(value)), False)
        # fields
        for fieldKey, fieldValue in value.__dict__.items():
            valueEntity.addField(toValueEntity(fieldKey, fieldValue))
        return valueEntity
    # function
    elif callable(value):
        # pass
        valueEntity = ValueEntity(key, trimClass(value.__class__), purifyClass(str(value)), False)
        # fields
        for field in value.__code__.co_varnames:
            valueEntity.addField(ValueEntity(field, "field", "field of " + key, False))
        return valueEntity
    else:
        # instance of customized class
        if 'api.runApi.' in str(value.__class__):
            valueEntity = ValueEntity(key, trimClass(value.__class__), purifyClass(str(value)), False)
            # fields
            for fieldKey, fieldValue in value.__dict__.items():
                valueEntity.addField(toValueEntity(fieldKey, fieldValue))
            return valueEntity

        elif trimClass(str(type(value))) in ['list', 'tuple', 'set']:
            valueEntity = ValueEntity(key, trimClass(value.__class__), purifyClass(str(value)), False)
            valueEntity.size = len(value)
            # fields
            for element in value:
                valueEntity.addField(toValueEntity("element of " + key, element))
            return valueEntity

        elif trimClass(str(type(value))) in ['dict']:
            valueEntity = ValueEntity(key, trimClass(value.__class__), purifyClass(str(value)), False)
            valueEntity.size = len(value)
            # fields
            for elementKey, elementValue in value.items():
                valueEntity.addField(toValueEntity(elementKey, elementValue))
            return valueEntity

        # primitive types
        else:
            valueEntity = ValueEntity(key, trimClass(value.__class__), purifyClass(str(value)), False)
            return valueEntity


class ConsoleOutput(object):
    def __init__(self):
        self.buff = ""

    def write(self, out_stream):
        self.buff += out_stream
