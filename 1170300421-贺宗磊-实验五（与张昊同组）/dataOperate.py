#coding:utf-8
import os
import numpy as np
import pandas as pd
import csv
global label_list

#定义kdd99数据预处理函数
def preHandel_data():
    source_file='kddcup.data_10_percent'
    handled_file='kddcup.csv'
    data_file=open(handled_file,"w",newline='')
    with open(source_file,'r') as data_source:
        csv_reader = csv.reader(data_source)
        csv_writer = csv.writer(data_file)
        #csv_writer2=csv.writer(data_file2)
        count=0   #记录数据的行数，初始化为0
        for row in csv_reader:
            temp_line=np.array(row)   #将每行数据存入temp_line数组里
            if handleLabel1(row) is True:
                 csv_writer.writerow(temp_line)
                 print(temp_line)
            if handleLabel2(row) is True:
                 csv_writer.writerow(temp_line)
                 print(temp_line)
            count+=1
        data_file.close()


def handleLabel1(input):
    label_list=['normal.']
    boolen1=True
    boolen2=False
    if input[41] in label_list:
        print(label_list)
        return boolen1
    else :
        return boolen2

def handleLabel2(input):
    label_list=['smurf.']
    boolen1=True
    boolen2=False
    if input[41] in label_list:
        return boolen1
    else :
        return boolen2

def split_csv(path, total_len, per):

    with open(path, 'r', newline='') as file:
        csvreader = csv.reader(file)
        i = 0
        for row in csvreader:

            if i < round(total_len * per/100):
                # train.csv存放路径
                csv_path = os.path.join("E:\\Python\\KD_lab5", 'train.csv')
                print(csv_path)
                # 不存在此文件的时候，就创建
                if not os.path.exists(csv_path):
                    with open(csv_path, 'w', newline='') as file:
                        csvwriter = csv.writer(file)
                        csvwriter.writerow(row)
                    i += 1
                # 存在的时候就往里面添加
                else:
                    with open(csv_path, 'a', newline='') as file:
                        csvwriter = csv.writer(file)
                        csvwriter.writerow(row)
                    i += 1
            elif (i >= round(total_len * per/100)) and (i < total_len):
            	# vali.csv存放路径
                csv_path = os.path.join("E:\\Python\\KD_lab5", 'attack.csv')
                print(csv_path)
                # 不存在此文件的时候，就创建
                if not os.path.exists(csv_path):
                    with open(csv_path, 'w', newline='') as file:
                        csvwriter = csv.writer(file)
                        csvwriter.writerow(row)
                    i += 1
                # 存在的时候就往里面添加
                else:
                    with open(csv_path, 'a', newline='') as file:
                        csvwriter = csv.writer(file)
                        csvwriter.writerow(row)
                    i += 1
            else:
                break

    print("训练集和验证集分离成功")
    return

if __name__=='__main__':
    global label_list   #声明一个全局变量的列表并初始化为空
    label_list=[]
    preHandel_data()
    path = 'E:\\Python\\KD_lab5\\kddcup.csv'
    total_len = len(open(path, 'r').readlines())  # csv文件行数
    per = 70  # 分割比例%
    split_csv(path, total_len, per)