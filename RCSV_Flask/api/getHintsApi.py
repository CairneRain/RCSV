from flask import request
import json
import pickle
import re
import operator

MAX_LEN = 10

# vocabulary
with open("./n_gram_dicts/python/1_gram_dict.pkl", 'rb') as f:
    python_dict = pickle.load(f)
with open("./n_gram_dicts/java/1_gram_dict.pkl", 'rb') as f:
    java_dict = pickle.load(f)
code_dict = {"text/x-java": java_dict,
             "python": python_dict}

# ngram
python_ngram = [python_dict]
for i in range(2, 11):
    with open("./n_gram_dicts/{}/{}_gram_dict.pkl".format("python", i), "rb") as f:
        python_ngram.append(pickle.load(f))
java_ngram = [java_dict]
for i in range(2, 11):
    with open("./n_gram_dicts/{}/{}_gram_dict.pkl".format("java", i), "rb") as f:
        java_ngram.append(pickle.load(f))
code_ngram_list = {"text/x-java": java_ngram,
                   "python": python_ngram}


def singlePredict(lang, predict, txt):
    for key in code_dict[lang]:
        if key[0][0].startswith(txt):
            predict.append({
                "text": key[0][0],
                "displayText": key[0][0],
                "displayInfo": key[1]
            })
        if len(predict) >= 10:
            return


def getHints():
    data = request.get_data()
    decoded_data = data.decode("utf-8")
    json_data = json.loads(decoded_data)
    scope = json_data.get("scope")
    lang = json_data.get("lang")

    txt = json_data.get("inputData")

    predict = []
    print(lang, txt)

    if scope == 1:
        print("single")
        singlePredict(lang, predict, txt)

    else:
        print("multiple")
        current_sentence = list(filter(None, re.split('[^A-Za-z]+', txt)))[-MAX_LEN:]
        current_prefix = current_sentence[-1]
        for count in range(len(current_sentence) - 1):
            gram = current_sentence[count:-1]
            gram_list = code_ngram_list[lang][len(gram)]
            for element in gram_list:
                if operator.eq(gram, list(element[0][0:-1])) and element[0][-1].startswith(current_prefix):
                    predict.append({
                        "text": element[0][-1],
                        "displayText": " ".join(element[0]),
                        "displayInfo": element[1]
                    })
                    if len(predict) >= 10:
                        return json.dumps(predict)

        singlePredict(lang, predict, current_prefix)

    return json.dumps(predict)
