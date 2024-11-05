import nltk
from nltk.corpus import stopwords
from nltk.tokenize import word_tokenize, sent_tokenize
nltk.download('punkt')
nltk.download('averaged_perceptron_tagger')
#stop_words = set(stopwords.words('english'))
import csv
def tag(list1,list2):
    if(len(list1)==len(list2)): # check number words equal with number of labels
        for i in range(len(list1)):
            if(list2[i]=='O'):
                list2[i]='Otag'
            print(list1[i][0],list1[i][1],list2[i]) # print word,POS,label
        print(" ")
# Open file 
with open('7_tags_equal.csv') as file_obj:
    reader_obj = csv.reader(file_obj)
    for row in reader_obj:
        for i in row:
            sent=i.split(';')[0] # split the sentence and store all the words
            sent=sent.strip()
            label=i.split(';')[1] # split the sentence and store all labels
            label=label.strip()
            k=label.split()
            wordsList=sent.split(" ") # list of words
            if(len(k)==len(wordsList)):
              tagged = nltk.pos_tag(wordsList)
              tag(tagged,k)
            elif(len(k)-len(wordsList)==1):
                tagged=nltk.pos_tag(wordsList)
                tag(tagged,k[:-1])
