package Lcs;

import java.security.PublicKey;
import java.util.Scanner;
import java.lang.Math;

public class LCScommunity {
    public static void main(String[] args){
        Scanner input=new Scanner(System.in);
        System.out.print("请输入字符串：");
        String test1=input.nextLine();
        System.out.print("请输入字符串：");
        String test2=input.nextLine();
        System.out.println(test2.length());
        int[][] same=new int[test1.length()][test2.length()];
        int[][] test=new int[test1.length()][test2.length()];
        char[][] right=new char[test1.length()][test2.length()];
        String[] result=new String[100];
        int a,b,c,k;
        int i,j;
        for(i=0;i<=test1.length()-1;i++){
            for(j=0;j<=test2.length()-1;j++){
                if(test1.charAt(i)==test2.charAt(j)){
                    same[i][j]=1;
                }
                else {
                    same[i][j] = 0;
                }
            }
        }
        for(i=0;i<=test1.length()-1;i++){
            for(j=0;j<=test2.length()-1;j++){
                if(i==0&&j==0) {
                    test[i][j] = same[i][j];
                    if(same[i][j]==0){
                        right[0][0]=test1.charAt(i);
                        //result[0].=test1.charAt(i);
                    }
                }
                else if(i==0){
                    if(same[i][j]==0) {
                        test[i][j] = 0;
                    }
                    else{
                        test[i][j]=1;
                    }
                }
                else if(j==0){
                    if(same[i][j]==0) {
                        test[i][j] = 0;
                    }
                    else {
                        test[i][j] = 1;
                    }
                }
                else{
                    a=test[i-1][j-1]+same[i][j];
                    b=test[i-1][j];
                    c=test[i][j-1];
                    test[i][j]=Math.max(Math.max(a,b),c);
                }

            }
        }

        /*for(i=test1.length()-1;i>=0;i--){
            for(j=test2.length()-1;j>=0;j--){
                if(same[i][j]==1){
                    test[i][j]=test[i-1][j-1]+same[i][j];
                }
                if(same[i][j]==0){

                }
            }
        }*/
        for(i=0;i<=test1.length()-1;i++){
            for(j=0;j<=test2.length()-1;j++){

                System.out.print(test[i][j]);
                if(j==test2.length()-1)
                    System.out.println("\n");
            }
        }
    }
}
