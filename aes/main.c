#include <stdio.h>
#include <unistd.h>
#include <string.h>
#include <stdlib.h>
#include "aes.h"

#define MAXLEN 1024

void getString(char *str, int len){
    int i =0;
    int slen = read(0, str, len);

    for(; i< slen; i++, str++){
        if(*str == '\n'){
            *str = '\0';
            break;
        }
    }
}


void printASCCI(char *str, int len){
    int c, i;
    for(i=0; i<len; i++){
        c = *str++;
        c = c & 0x000000ff;
        printf("0x%x ", c);
    }
    printf("\n");
}


void readPlainText(char *str, int *len){
    int plen;
    while(1){
        getString(str, MAXLEN);
        plen = strlen(str);
        if(plen != 0 && plen %16 == 0){
            printf("你输入的明文为：　%s , len = %d\n", str, plen);
            break;
        }else{
            printf("明文长度不是１６的倍数\n");
        }
    }
    *len = plen;
}

void writeStrToFile(char *str, int len, char *fileName){
    FILE *fp;
    int i = 0;
    fp = fopen(fileName, "wb");
    for(; i < len; i++){
        putc(str[i], fp);
    }
    fclose(fp);
}

void aesStrToFile(char *key){
    char p[MAXLEN];
    int plen;
    printf("请输入你的明文\n");
    readPlainText(p, &plen);
    printf(" 进行AES加密\n");
    aes(p, plen, key); // -------------加密

    printf("加密后的明文ASCCI为:\n");
    printASCCI(p, plen);

    char fileName[64];
    printf("请输入你想要写进的文件名，　比如test.txt\n");
    if(scanf("%s", fileName) == 1){
        writeStrToFile(p, plen, fileName);
    }
}


int readStrFromFile(char *fileName, char *str){
    FILE *fp = fopen(fileName, "rb");
    if(fp == NULL){
        printf("文件打开失败\n");
        exit(0);
    }

    int i = 0;
    for(i=0; i<MAXLEN && (str[i] = getc(fp)) != EOF; i++){

    }

    if(i>= MAXLEN){
        printf("文件过大\n");
        exit(0);
    }

    str[i] = '\0';
    fclose(fp);
    return i;
}

void deAesFile(char *key){
    char fileName[64];
    char c[MAXLEN];
    printf("请输入要解密的文件名\n");
    if(scanf("%s", fileName) == 1){
        int clen = readStrFromFile(fileName, c);
        printf("开始解密\n");
        deAes(c, clen, key); // ----------解密

        printf("解密后的明文:\n");
        printASCCI(c, clen);
        writeStrToFile(c, clen, fileName);
        printf("现在可以打开%s　来查看解密文件\n", fileName);
    }
}

void aesFile(char *key){
    char fileName[64];
    char fileP[MAXLEN];

    printf("请输入要加密的文件名，该文件必须和本程序在同一个目录\n");
    if(scanf("%s", fileName) == 1) {
        readStrFromFile(fileName, fileP);
        int plen = strlen(fileP);
        printf("开始加密.........\n");
        printf("加密前文件中字符的ASCII为：\n");
        printASCCI(fileP, plen);

        aes(fileP, plen, key);//开始加密

        printf("加密后的密文ASCII为：\n");
        printASCCI(fileP, plen);
        writeStrToFile(fileP,plen,fileName);
        printf("已经将加密后的密文写进%s中了\n",fileName);
    }
}

int main(int argc, char *argv[]){
    char key[17];
    printf("请输入１６个字符的密钥\n");
    int klen;
    while(1){
        getString(key, sizeof(key));
        klen = strlen(key);
        if(klen != 16){
            printf("请输入１６个字符的密钥，　当前密钥为 %s \n", key);
        }else{
            printf(" 你输入的密钥为:%s \n", key);
            break;
        }
    }

    printf("输入's'表示要加密输入的字符串,并将加密后的内容写入到文件\n");
    printf("请输入要功能选项并按回车，输入'f'表示要加密文件\n");
    printf("输入'p'表示要解密文件\n");
    char c;
    if(scanf("%s",&c) == 1) {
        if(c == 's')
            aesStrToFile(key);//用AES加密字符串，并将字符串写进文件中
        else if(c == 'p')
            deAesFile(key);//把文件中的密文解密，并写回文件中
        else if(c == 'f')//用AES加密文件
            aesFile(key);
    }
    return 0;
}

