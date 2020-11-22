package Lcs;

import java.util.Scanner;
import java.util.TreeSet;

public class lcscommen {

    private String X;
    private String Y;
    private int[][] table;  // 动态规划表
    private TreeSet<String> set = new TreeSet<String>();

    //功能：带参数的构造器
    public lcscommen(String X, String Y) {
        this.X = X;
        this.Y = Y;
    }

     //功能：求两个数中的较大者
    private int max(int a, int b) {
        return (a>b) ? a:b;
    }

    //功能：构造表，并返回X和Y的LCS的长度
    private int lcs(int m, int n) {
        table = new int[m+1][n+1]; // 表的大小为(m+1)*(n+1)
        for(int i=0; i<m+1; ++i) {
            for(int j=0; j<n+1; ++j) {
                // 第一行和第一列置0
                if (i == 0 || j == 0)
                    table[i][j] = 0;
                else if(X.charAt(i-1) == Y.charAt(j-1))
                    table[i][j] = table[i-1][j-1] + 1;
                else
                    table[i][j] = max(table[i-1][j], table[i][j-1]);
            }
        }
        return table[m][n];
    }

    //功能：回溯，求出所有的最长公共子序列，并放入set中
    private void traceBack(int i, int j, String lcs_str) {
        while (i>0 && j>0) {
            if (X.charAt(i-1) == Y.charAt(j-1)) {
                lcs_str += X.charAt(i-1);
                --i;
                --j;
            }
            else {
                if (table[i-1][j] > table[i][j-1])
                    --i;
                else if (table[i-1][j] < table[i][j-1])
                    --j;
                else {  // 相等的情况
                    traceBack(i-1, j, lcs_str);
                    traceBack(i, j-1, lcs_str);
                    return;
                }
            }
        }
        set.add(reverse(lcs_str));
    }

    //功能：字符串逆序
    private String reverse(String str) {
        StringBuffer strBuf = new StringBuffer(str).reverse();
        return strBuf.toString();
    }

    //功能：外部接口 —— 打印输出
    public void printLCS() {
        int m = X.length();
        int n = Y.length();
        int length = lcs(m,n);
        String str = "";
        traceBack(m,n,str);

        System.out.println("The length of LCS is: " + length);
        for(String s : set) {
            System.out.println(s);
        }
    }


    public static void main(String[] args) {
        //lcscommen lcs = new lcscommen("ABCBDAB","BDCABA");
        Scanner input=new Scanner(System.in);
        System.out.print("请输入字符串S1：");
        String test1=input.nextLine();
        System.out.print("请输入字符串S2：");
        String test2=input.nextLine();
        lcscommen lcs = new lcscommen(test1,test2);
        lcs.printLCS();
    }
}

