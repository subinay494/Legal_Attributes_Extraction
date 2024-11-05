import torch
import json
import os
import numpy as np
from numpy.linalg import norm
from transformers import AutoTokenizer, AutoModel
from transformers import BertTokenizer, BertModel
import numpy as np
from numpy.linalg import norm
# tokenizer = AutoTokenizer.from_pretrained("law-ai/InLegalBERT")
# tokenizer = AutoTokenizer.from_pretrained("law-ai/InLegalBERT")
# model = AutoModel.from_pretrained("law-ai/InLegalBERT")
tokenizer = BertTokenizer.from_pretrained('bert-base-uncased')
model = BertModel.from_pretrained("bert-base-uncased")
#tokenizer = AutoTokenizer.from_pretrained("nlpaueb/legal-bert-base-uncased")
#model = AutoModel.from_pretrained("nlpaueb/legal-bert-base-uncased")
def sentence_to_bert_vector(text):
    encoded_input = tokenizer(text, return_tensors="pt")
    output = model(**encoded_input)
    x=output[0].tolist()
    tensor=x[0][0]
    return tensor
dict_query={}
query=[]
query_feedback=[]
dict_query_embedding={}
dict_query_feedback={}
dict_query_feedback_embedding={}
def query_to_bert_vector():
    i=0
    with open("/home/subinay/Documents/data/sequence_labeling/Reranking/physical_assault_query.txt","r") as f1:
        for line in f1:
            i=i+1
            line=line.strip()
            id="qid"+str(i)
            query.append(id)
            dict_query[id]=line
    for key, value in dict_query.items():
        vector=sentence_to_bert_vector(value)
        dict_query_embedding[key]=vector
def query_feedback_to_bert_vector():
    i=0
    with open("/home/subinay/Documents/data/sequence_labeling/Reranking/physical_assault_query_feedback.txt","r") as f1:
        for line in f1:
            i=i+1
            line=line.strip()
            id="rid"+str(i)
            query_feedback.append(id)
            dict_query_feedback[id]=line
    for key, value in dict_query_feedback.items():
        value=value.split(";")
        vector=sentence_to_bert_vector(value[0])
        dict_query_feedback_embedding[key]=vector
def sim_query_retieve(qid,rid):
    score=0
    doc1_embedding=dict_query_embedding[qid]
    doc2_embedding=dict_query_feedback_embedding[rid]
    A=np.array(doc1_embedding)
    B=np.array(doc2_embedding)
    score = np.dot(A,B)/(norm(A)*norm(B))
    return score 
query_to_bert_vector()
query_feedback_to_bert_vector()
#print(dict_query_feedback_embedding)
query_feedback_score={}
for i in range(len(query_feedback)):
    score=0
    for j in range(len(query)):
        score=score+sim_query_retieve(query[j],query_feedback[i])
    query_feedback_score[query_feedback[i]]=score
query_feedback_score=dict(sorted(query_feedback_score.items(), key=lambda item: item[1],reverse=True))
list1 = list(query_feedback_score.keys())  
with open ("physical_assault_bert_uncased.txt","w") as f1:
    for id in list1:
        f1.write(dict_query_feedback[id])
        f1.write("\n")
f1.close()