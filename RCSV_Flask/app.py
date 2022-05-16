from flask import Flask
from api import getHintsApi, getSampleCodeApi, runApi
from flask import request
import json

app = Flask(__name__)


@app.route('/hint/getHints', methods = ['POST'])
def getHints():
    return getHintsApi.getHints()


@app.route('/debug/getSampleCode')
def getSampleCode():
    return getSampleCodeApi.getSampleCode('./debuggeee/sample/python/Hanoi.py')


@app.route('/debug/run', methods = ['POST'])
def run():
    data = request.get_data()
    decoded_data = data.decode("utf-8")
    json_data = json.loads(decoded_data)
    code = json_data.get("code")
    return runApi.run(code)


if __name__ == '__main__':
    app.run()
