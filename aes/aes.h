#ifndef AES_H
#define AES_H

/**
 * 参数p: 明文的字符串数组
 * 参数plen：明文的长度，　长度必须为１６的倍数
 * 参数key：密钥的字符串数组
 */
void aes(char *p, int plen, char *key);

/**
 * 参数c：密文的字符串数组
 * 参数clen：　密文的长度，　长度必须为１６的倍数
 * 参数key：密钥的字符串数组
 */
void deAes(char *c, int clen, char *key);
#endif