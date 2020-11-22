import math
import time
import matplotlib.pyplot as plt
import numpy as np

old_settings = np.seterr(all="ignore")


# 定义节点类型
class KD_node:
    def __init__(self, point=None, split=None, left=None, right=None):
        self.point = point  # 数据点的特征向量
        self.split = split  # 切分的维度
        self.left = left  # 左儿子
        self.right = right  # 右儿子


# 计算方差，以利用方差大小进行判断在哪一维进行切分
def computeVariance(arrayList):
    for ele in arrayList:
        ele = float(ele)
    LEN = float(len(arrayList))
    array = np.array(arrayList)
    sum1 = float(array.sum())
    array2 = np.dot(array, array.T)
    sum2 = float(array2.sum())
    mean = sum1 / LEN
    variance = sum2 / LEN - mean ** 2
    return variance


# 建树
def createKDTree(root, data_list):
    LEN = len(data_list)
    if LEN == 0:
        return
    # 数据点的维度
    dimension = len(data_list[0]) - 1  # 去掉了最后一维的标签维
    # 方差
    max_var = 0
    # 最后选择的划分域
    split = 0
    for i in range(dimension):
        ll = []
        for t in data_list:
            ll.append(t[i])
        var = computeVariance(ll)  # 计算出在这一维的方差大小
        if var > max_var:
            max_var = var
            split = i
    # 根据划分域的数据对数据点进行排序
    data_list = list(data_list)
    data_list.sort(key=lambda x: x[split])
    data_list = np.array(data_list)
    # 选择下标为len / 2的点作为分割点
    x = int(LEN / 2)
    point = data_list[x]
    root = KD_node(point, split)
    # 递归的对切分到左儿子和右儿子的数据再建树
    x1 = int(x / 2)
    root.left = createKDTree(root.left, data_list[0:(x1)])
    root.right = createKDTree(root.right, data_list[(x1 + 1):LEN])
    return root


# 计算欧氏距离
def computeDist(pt1, pt2):
    sum_dis = 0.0
    for i in range(len(pt1)):
        sum_dis += (pt1[i] - pt2[i]) ** 2
    return math.sqrt(sum_dis)
    # vector1 = np.array(pt1)
    # vector2 = np.array(pt2)
    # return np.linalg.norm(vector1 - vector2)


def findNN(root, query):
    # 初始化为root的节点
    NN = root.point
    min_dist = computeDist(query, NN)
    nodeList = []
    temp_root = root
    # 用来存储前三个点
    dist_list = [temp_root.point, None, None]
    ## 二分查找建立路径
    while temp_root:
        # 对向下走的路径进行压栈处理
        nodeList.append(temp_root)
        # 计算当前最近节点和查询点的距离大小
        dd = computeDist(query, temp_root.point)
        if min_dist > dd:
            NN = temp_root.point
            min_dist = dd
        # 当前节点的划分域
        temp_split = temp_root.split
        if query[temp_split] <= temp_root.point[temp_split]:
            temp_root = temp_root.left
        else:
            temp_root = temp_root.right

    # 回溯查找
    while nodeList:
        back_point = nodeList.pop()
        back_split = back_point.split
        if dist_list[1] is None:
            dist_list[2] = dist_list[1]
            dist_list[1] = back_point.point
        elif dist_list[2] is None:
            dist_list[2] = back_point.point
        if abs(query[back_split] - back_point.point[back_split]) < min_dist:
            # 当查询点和回溯点的距离小于当前最小距离时，另一个区域有希望存在更近的节点
            if query[back_split] < back_point.point[back_split]:
                temp_root = back_point.right
            else:
                temp_root = back_point.left
            if temp_root:
                nodeList.append(temp_root)
                curDist = computeDist(query, temp_root.point)
                if min_dist > curDist:
                    min_dist = curDist
                    dist_list[2] = dist_list[1]
                    dist_list[1] = dist_list[0]
                    dist_list[0] = temp_root.point
                elif dist_list[1] is None or curDist < computeDist(dist_list[1], query):
                    dist_list[2] = dist_list[1]
                    dist_list[1] = temp_root.point
                elif dist_list[2] is None or curDist < computeDist(dist_list[1], query):
                    dist_list[2] = temp_root.point
    return dist_list


# 进行判断
def judge_if_normal(dist_list):
    normal_times = 0
    except_times = 0
    for i in dist_list:
        if abs(i[-1] - 0.0) < 1e-7:  # 浮点数的比较
            normal_times += 1
        else:
            except_times += 1
    if normal_times > except_times:  # 判断是normal
        return True
    else:
        return False


# 数据预处理
def pre_data(path):
    f = open(path)
    lines = f.readlines()
    f.close()
    lstall = []
    for line in lines:
        lstn = []
        lst = line.split(",")
        u = 0
        y = 0
        for i in range(0, 9):
            if lst[i].isdigit():
                lstn.append(float(lst[i]))
                u += 1
            else:
                pass
        for j in range(21, 31):
            try:
                lstn.append(float(lst[j]))
                y += 1
            except:
                pass
        if lst[len(lst) - 1] == "smurf.\n" or lst[len(lst) - 1] == "teardrop.\n":
            lstn.append(int("1"))
        else:
            lstn.append(int("0"))
        lstall.append(lstn)
    nplst = np.array(lstall, dtype=np.float16)
    return nplst


# 测试
def my_test(all_train_data, all_test_data, train_data_num):
    train_data = all_train_data[:train_data_num]
    train_time_start = time.time()
    root = KD_node()
    root = createKDTree(root, train_data)
    train_time_end = time.time()
    train_time = train_time_end - train_time_start
    right = 0
    error = 0
    smurf = 0
    normal = 0
    test_time_start = time.time()
    for i in range(len(all_test_data)):
        if judge_if_normal(findNN(root, all_test_data[i])) is True and abs(all_test_data[i][-1] - 0.0) < 1e-7:
            right += 1
            normal += 1
        elif judge_if_normal(findNN(root, all_test_data[i])) is False and abs(all_test_data[i][-1] - 1.0) < 1e-7:
            right += 1
            smurf += 1
        else:
            error += 1
    test_time_end = time.time()
    test_time = test_time_end - test_time_start
    right_ratio = (float(right) / (right + error)) * 100
    return right_ratio, train_time, test_time, smurf, normal


def draw(train_num_list=[], train_data=[], test_data=[]):
    # train_time_list = []
    # test_time_list = []
    right_ratio_list = []
    test_true_attack_num = 86968
    test_true_normal_num = 26052
    for i in train_num_list:
        print("开始运行，训练集选择大小为：" + i.__str__())
        temp = my_test(train_data, test_data, i)
        right_ratio_list.append(temp[0])
        print("正确率（数据未被抛弃）是：" + str(temp[0]) + "%")
        print("检测出来的Smurf数量是：" + str(temp[3]))
        print("检测出来的Normal数量是：" + str(temp[4]))
        print("测试集中的Smurf数量是：" + str(test_true_attack_num))
        print("测试集中的Normal数量是：" + str(test_true_normal_num))
    #     train_time_list.append(temp[1])
    #     test_time_list.append(temp[2])
    # plt.title('train data num from ' + train_num_list[0].__str__() + ' to ' + train_num_list[:-1].__str__())
    # plt.subplot(311)
    # plt.plot(train_num_list, right_ratio_list, c='b')
    # plt.xlabel('train data num (%)')
    # plt.ylabel('right ratio')
    # plt.grid(True)
    # plt.subplot(312)
    # plt.plot(train_num_list, train_time_list, c='r')
    # plt.xlabel('train data num')
    # plt.ylabel('time of train data (s)')
    # plt.grid(True)
    # plt.subplot(313)
    # plt.plot(train_num_list, test_time_list, c='g')
    # plt.xlabel('train data num')
    # plt.ylabel('time of test data (s)')
    # plt.grid(True)
    # plt.show()


data = pre_data('train.csv')
data2 = pre_data('test.csv')
draw(train_num_list=[264647], train_data=data[:], test_data=data2[:])
