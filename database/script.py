#!/usr/bin/env python2
# -*- coding: utf-8 -*-
"""
Created on Mon Jun  3 20:56:25 2019

@author: malaksadek
"""

filePath = "/Users/malaksadek/Desktop/GardinerCode.sql"
file = open(filePath, "r") 
code = file.read() 
code = code.replace("(", "")
code = code.replace(")", "")
code = code.replace("'", "")
code = code.replace("\n", "")
code = code.replace(";", ",")
tokens = code.split(",")
i = 0

for element in tokens:
    element = element.lstrip()

    if i == 0:
        code = element
    if i == 2:
        description = element
    if i == 3:
        meaning = element
    if i == 4:
        link = element
        output = "db.collection(\"GardinerCode\").document(\""+code+"\").setData([ \n\"Description\": \""+description+"\",\n\"Link\": \""+link+"\",\n\"Image\": \"/DictionaryPictures/"+code+".bin\",\n\"Meaning\": \""+meaning+"\"\n]);"
        print output                     
    
    i = i+1
    if i == 5:
        i = 0