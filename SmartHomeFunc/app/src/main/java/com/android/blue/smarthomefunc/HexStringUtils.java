package com.android.blue.smarthomefunc;

import android.util.Log;

import java.io.ByteArrayOutputStream;

/**
 * 数据转换工具类
 * 因为蓝牙传输采用的字节传输，不是字符传输，所以发送和收到都要转码
 * Created by root on 12/13/17.
 */

public class HexStringUtils {
    private static final  String TAG = "fht";
    private static String hexString = "0123456789ABCDEF";
    /**
     * 将字节转换为十六进制
     * @param b
     * @return
     */
    public static String printHexString(byte[] b){
        String temp = "";
        for(int i =0; i< b.length;i++){
            String hex = Integer.toHexString(b[i] & 0xFF);
            if(hex.length() == 1){
                hex = '0'+hex;
            }
            temp = temp+hex;
        }
        Log.i(TAG, "printHexString: "+temp);
        return temp;
    }

    /**
     * 十六进制转为字节
     * 考虑到char 转换为 byte　＇９＇　！＝　９
     * @param hexString
     * @return
     */
    public static byte[] hexStringToBytes(String hexString){
        if(hexString == null || hexString.isEmpty()){
            return null;
        }
        Log.i(TAG," H :"+hexString);
        hexString = hexString.toUpperCase();
        int length = hexString.length()/2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i<length;i++){
            int pos = i*2;
            byte high = (byte)(Character.getNumericValue(hexChars[pos]) <<4);
            byte low = (byte) Character.getNumericValue(hexChars[pos+1]);
            d[i] = (byte)(high+low);
        }
        return d;
    }

    /**
     * 字节数据转为16进制
     * @param src
     * @return
     */
    public static String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder();
        if(src == null || src.length <=0){
            return null;
        }

        for(int i =0; i< src.length;i++){
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if(hv.length() < 2){
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * 将字符串编码成１６进制数字，适用于所有字符
     * @param str
     * @return
     */
    public static String encode(String str){
        //根据默认编码获取字节数组
        byte[] bytes = str.getBytes();
        for (int j=0;j<bytes.length;j++){
            Log.i(TAG,"encode method bytes: "+bytes[j]);
        }

        StringBuilder sb = new StringBuilder(bytes.length *2);
        //将字节数组中每个字节拆解成２位16进制整数
        for(int i =0;i< bytes.length;i++){
            sb.append(hexString.charAt((bytes[i] & 0xf0) >> 4));
            sb.append(hexString.charAt((bytes[i] & 0x0f) >> 0));
        }
        return sb.toString();
    }

    /**
     * 将16进制数字解码成字符串，适用于所有字符
     * @param bytes
     * @return
     */
    public static String decode(String bytes){
        ByteArrayOutputStream bostr = new ByteArrayOutputStream(bytes.length()/2);
        //将每２位１６进制整数组装成一个字节
        for(int i = 0; i< bytes.length(); i+=2){
            bostr.write((hexString.indexOf(bytes.charAt(i)) << 4 | hexString.indexOf(bytes.charAt(i+1))));
        }
        return new String(bostr.toByteArray());
    }

    /**
     * @功能: 10进制串转为BCD码
     * @参数: 10进制串
     * @结果: BCD码
     */
    public static byte[] str2Bcd(String asc) {
        int len = asc.length();
        int mod = len % 2;
        if (mod != 0) {
            asc = "0" + asc;
            len = asc.length();
        }
        byte abt[];
        if (len >= 2) {
            len = len / 2;
        }
        byte bbt[] = new byte[len];
        abt = asc.getBytes();
        int j, k;
        for (int p = 0; p < asc.length() / 2; p++) {
            if ((abt[2 * p] >= '0') && (abt[2 * p] <= '9')) {
                j = abt[2 * p] - '0';
            } else if ((abt[2 * p] >= 'a') && (abt[2 * p] <= 'z')) {
                j = abt[2 * p] - 'a' + 0x0a;
            } else {
                j = abt[2 * p] - 'A' + 0x0a;
            }
            if ((abt[2 * p + 1] >= '0') && (abt[2 * p + 1] <= '9')) {
                k = abt[2 * p + 1] - '0';
            } else if ((abt[2 * p + 1] >= 'a') && (abt[2 * p + 1] <= 'z')) {
                k = abt[2 * p + 1] - 'a' + 0x0a;
            } else {
                k = abt[2 * p + 1] - 'A' + 0x0a;
            }
            int a = (j << 4) + k;
            byte b = (byte) a;
            bbt[p] = b;
        }
        return bbt;
    }

    /**
     * 十六进制字符串装十进制
     *
     * @param hex 十六进制字符串
     * @return 十进制数值
     */
    public static long hexStringToAlgorism(String hex) {
        hex = hex.toUpperCase();
        int max = hex.length();
        long result = 0;
        for (int i = max; i > 0; i--) {
            char c = hex.charAt(i - 1);
            int algorism = 0;
            if (c >= '0' && c <= '9') {
                algorism = c - '0';
            } else {
                algorism = c - 55;
            }
            result += Math.pow(16, max - i) * algorism;
        }
        return result;
    }

    public static String hexStringToBinary(String hex){
        hex = hex.toUpperCase();
        StringBuilder result = new StringBuilder();
        int len = hex.length();
        for (int i=0;i<len;i++){
            char c = hex.charAt(i);
            if(i%2 == 0){
                result.append(" ");
            }
            switch (c){
                case '0':
                    result.append("0000");
                    break;
                case '1':
                    result.append("0001");
                    break;
                case '2':
                    result.append("0010");
                    break;
                case '3':
                    result.append("0011");
                    break;
                case '4':
                    result.append("0100");
                    break;
                case '5':
                    result.append("0101");
                    break;
                case '6':
                    result.append("0110");
                    break;
                case '7':
                    result.append("0111");
                    break;
                case '8':
                    result.append("1000");
                    break;
                case '9':
                    result.append("1001");
                    break;
                case 'A':
                    result.append("1010");
                    break;
                case 'B':
                    result.append("1011");
                    break;
                case 'C':
                    result.append("1100");
                    break;
                case 'D':
                    result.append("1101");
                    break;
                case 'E':
                    result.append("1110");
                    break;
                case 'F':
                    result.append("1111");
                    break;
            }
        }
        return result.toString();

    }

}
