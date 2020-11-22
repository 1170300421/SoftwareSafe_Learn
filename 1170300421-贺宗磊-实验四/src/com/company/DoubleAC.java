package com.company;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.NumberFormat;

public class DoubleAC {

    private int[] next = new int[256];
    private int maxLength;
    private String[] TestSet;
    private HashMap<Integer, Integer> Base = new HashMap<>();
    private HashMap<Integer, Integer> Check = new HashMap<>();
    private HashMap<Integer, Integer> fail = new HashMap<>();
    private HashMap<Integer, HashSet<String>> output = new HashMap<>();

    //构建Next表、Base表、Check表
    public void Arrays() {
        int currentState = 0;
        int a = 0;
        HashSet<Character> letterSet = new HashSet<>();
        Base.put(0, 1 - Integer.valueOf(TestSet[0].charAt(0)));
        for (int i = 0; i < TestSet.length; i++) {
            letterSet.add(TestSet[i].charAt(0));
        }

        char[] firstLetter = new char[letterSet.size()];
        for (Character character : letterSet) {
            firstLetter[a] = character;
            a++;
        }
        Arrays.sort(firstLetter);

        for (int i = 0; i < firstLetter.length; i++) {
            currentState++;
            next[Base.get(0) + Integer.valueOf(firstLetter[i])] = currentState;
            Check.put(currentState, 0);
            fail.put(currentState, 0);
        }

        for (int i = 1; i < maxLength; i++) {
            HashMap<String, HashSet<Character>> parentMap = new HashMap<>();

            for (int j = 0; j < TestSet.length; j++) {
                if (TestSet[j].length() > i) {
                    HashSet<Character> childSet = new HashSet<>();
                    String parent = TestSet[j].substring(0, i);
                    char child = TestSet[j].charAt(i);

                    if (parentMap.get(parent) != null) {
                        childSet = parentMap.get(parent);
                    }
                    childSet.add(child);
                    parentMap.put(parent, childSet);
                }
            }

            Set<Map.Entry<String, HashSet<Character>>> entries = parentMap.entrySet();
            for (Map.Entry<String, HashSet<Character>> entry : entries) {
                char[] letter = new char[entry.getValue().size()]; // 子节点
                String parentString = entry.getKey(); // 前缀

                int k = 0;
                for (Character character : entry.getValue()) {
                    letter[k] = character;
                    k++;
                }
                Arrays.sort(letter);

                int parentState = 0;
                int flag = 1;
                int pos = 0;

                for (int j = 0; j < parentString.length(); j++) {
                    pos = Position(parentState);
                    parentState = next[Base.get(parentState) + pos + Integer.valueOf(parentString.charAt(j))];
                }

                pos = Position(parentState);
                while (next[flag] != 0) {
                    flag++;
                }
                currentState++;
                next[flag] = currentState; // 第一个子节点对应的next值
                Base.put(parentState, flag - pos - Integer.valueOf(letter[0])); // 父状态的base值
                Check.put(currentState, parentState);
                fail.put(currentState, GotoStation(fail.get(parentState), letter[0]));

                for (int j = 1; j < letter.length; j++) {
                    currentState++;
                    Check.put(currentState, parentState);
                    next[pos + Base.get(parentState) + Integer.valueOf(letter[j])] = currentState;
                    fail.put(currentState, GotoStation(fail.get(parentState), letter[j]));
                }
            }
        }
    }

    public void Pattern() throws IOException {
        try {
            FileReader fileReader=new FileReader("test.txt");
            BufferedReader bufferedReader=new BufferedReader(fileReader);
            String[] patterns = bufferedReader.readLine().split(" ");
            bufferedReader.close();

            int num = patterns.length;
            char[] letter = new char[num];
            TestSet = new String[num];
            maxLength = patterns[0].length();

            for (int i = 0; i < num; i++) {
                letter[i] = patterns[i].charAt(0);
                if (patterns[i].length() > maxLength) {
                    maxLength = patterns[i].length();
                }
            }
            Arrays.sort(letter);
            for (int i = 0; i < num; i++) {
                char c = patterns[i].charAt(0);
                for (int j = 0; j < num; j++) {
                    if (c == letter[j] && TestSet[j] == null) {
                        TestSet[j] = patterns[i];
                        break;
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("No file!");
        }
    }

    //获取父状态在next表中的位置
    public int Position(int parentState) {
        int pos = 0;
        for (int l = 0; l < next.length; l++) {
            if (next[l] == parentState) {
                pos = l;
                break;
            }
        }
        return pos;
    }

    //获取转向状态
    public Integer GotoStation(int currentState, char charater) {
        if (Base.get(currentState) == null) { //当前状态为最后一层的状态
            return 0;
        }
        int pos = Position(currentState);
        int index = pos + Base.get(currentState) + Integer.valueOf(charater);
        int nextState;
        if (index >= 0) {
            nextState = next[index];
            if (nextState > 0 && nextState <= Check.size() && Check.get(nextState) == currentState) {
                return nextState;
            }
        }
        return 0;
    }



    //构造输出函数
    public void Output() {
        int[] lengths = new int[TestSet.length];
        String[] sortPaterns = new String[TestSet.length]; // 按长度排序后的模式集

        for (int i = 0; i < TestSet.length; i++) {
            lengths[i] = TestSet[i].length();
        }
        Arrays.sort(lengths); // 对模式串的长度进行排序

        for (int i = 0; i < sortPaterns.length; i++) {
            int len = TestSet[i].length();

            for (int j = 0; j < lengths.length; j++) {
                if (len == lengths[j] && sortPaterns[j] == null) {
                    sortPaterns[j] = TestSet[i];
                    break;
                }
            }
        }

        for (int i = 0; i < sortPaterns.length; i++) {
            int state = 0;
            HashSet<String> hashSet = new HashSet<>();

            for (int j = 0; j < sortPaterns[i].length(); j++) {
                state = GotoStation(state, sortPaterns[i].charAt(j));
            }

            if (output.get(fail.get(state)) != null) {
                for (String str : output.get(fail.get(state))) {
                    hashSet.add(str);
                }
            }

            hashSet.add(sortPaterns[i]);
            output.put(state, hashSet);
        }
        System.out.println();
        System.out.println("output函数:");
        Set<Entry<Integer, HashSet<String>>> entries = output.entrySet();
        for (Entry<Integer, HashSet<String>> entry : entries) {
            System.out.print(entry.getKey() + "\t");
            for (String str : entry.getValue()) {
                System.out.print(str + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    //读取文本内容进行模式匹配
    public void getText(String filename) throws IOException {
        try {
            FileReader fr = new FileReader(filename);
            BufferedReader bf = new BufferedReader(fr);
            String str;
            System.out.println("按顺序输出匹配字符串");
            while ((str = bf.readLine()) != null) {
                int currentState = 0;
                int nextState;
                for (int i = 0; i < str.length(); i++) {
                    char c = str.charAt(i);
                    nextState = GotoStation(currentState, c);
                    while (nextState==0 && currentState!=0) {
                        nextState = fail.get(currentState);
                        currentState = nextState;
                        nextState = GotoStation(currentState, c);
                    }
                    currentState = nextState;

                    if (output.get(currentState) != null) {
                        for(String strGet: output.get(currentState)) {
                            System.out.println("匹配到字符串"+": "+strGet);
                        }
                    }
                }
            }
            bf.close();
        } catch (FileNotFoundException e) {
            System.out.println("No such file!");
        }
    }

    //输出next表、base表、check表的内容
    public void print() {
        NumberFormat nf = NumberFormat.getPercentInstance();
        nf.setMaximumFractionDigits(2);
        System.out.println("空间占用率："+nf.format((double)(Check.size()+1)/next.length));
        System.out.println("Next表");
        System.out.print("Nume"+"\t");
        for (int i = 0; i < next.length; i++) {
            System.out.print(i + "\t");
        }

        System.out.println();
        System.out.print("Next\t");
        for (int i = 0; i < next.length; i++) {
            System.out.print(next[i] + "\t");
        }

        System.out.println();


        //System.out.println();
        //System.out.print("\t");
        System.out.println();
        System.out.println("Base表");
        System.out.print("Base\t");
        Set<Map.Entry<Integer, Integer>> entries1 = Base.entrySet();
        for (Entry<Integer, Integer> entry : entries1) {
            System.out.print(entry.getValue() + "  ");
        }

        //System.out.println();
        //System.out.print("\t");
        System.out.println();
        System.out.println("Check表");
        System.out.print("Check\t");
        Set<Map.Entry<Integer, Integer>> entries2 = Check.entrySet();

        for (Entry<Integer, Integer> entry : entries2) {
            System.out.print(entry.getValue() + "\t");
        }
        System.out.println();
    }

}
