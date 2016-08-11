#include <string.h>
#include <stdio.h>
#ifdef __GNUC__
    #include <stdint.h>
    #include <stdlib.h>
    #include <stdio.h>
   	#define __buildin__memcpy memcpy
#endif
#ifndef STEADY_ITERATIONS
    #define STEADY_ITERATIONS 100
#endif
#include "mpsoc_com-lib.h"
#include "fio_com-lib.h"
#ifndef COREVA_HWACC_MISC_TRACE
#define COREVA_HWACC_MISC_TRACE(id) 
#endif

int32_t** Sbox_3_table;
int32_t** Sbox_4_table;
int32_t** Sbox_5_table;
int32_t** Sbox_6_table;
int32_t** Sbox_8_table;
int32_t Xor_3_1_n;
int32_t Xor_3_2_n;
int32_t** Sbox_9_table;
int32_t** Sbox_11_table;
int32_t** Sbox_12_1_table;
int32_t** Sbox_12_2_table;
int32_t** Sbox_12_4_table;
int32_t** Sbox_13_table;
int32_t** Sbox_17_table;
int32_t** Sbox_20_table;
int32_t** Sbox_21_table;
int32_t** Sbox_22_table;
int32_t** Sbox_27_table;
int32_t** Sbox_28_table;
int32_t** Sbox_29_table;
int32_t** Sbox_30_table;
int32_t** Sbox_32_table;
uint8_t _cpu_0_0_0_PRINTINFO;
int32_t _cpu_0_0_0_PLAINTEXT;
int32_t _cpu_0_0_0_USERKEY;
int32_t _cpu_0_0_0_CIPHERTEXT;
int32_t _cpu_0_0_0_MAXROUNDS;
int32_t** _cpu_0_0_0_USERKEYS = {0};
int32_t* _cpu_0_0_0_PC1 = {0};
int32_t* _cpu_0_0_0_PC2 = {0};
int32_t* _cpu_0_0_0_RT = {0};
int32_t* _cpu_0_0_0_IP = {0};
int32_t* _cpu_0_0_0_E = {0};
int32_t* _cpu_0_0_0_P = {0};
int32_t* _cpu_0_0_0_IPm1 = {0};
int32_t** _cpu_0_0_0_S1 = {0};
int32_t** _cpu_0_0_0_S2 = {0};
int32_t** _cpu_0_0_0_S3 = {0};
int32_t** _cpu_0_0_0_S4 = {0};
int32_t** _cpu_0_0_0_S5 = {0};
int32_t** _cpu_0_0_0_S6 = {0};
int32_t** _cpu_0_0_0_S7 = {0};
int32_t** _cpu_0_0_0_S8 = {0};
int32_t _cpu_0_0_0_i;
int32_t _cpu_0_0_0_i_02;
int32_t _cpu_0_0_0_i_03;
int32_t _cpu_0_0_0_i_04;
int32_t _cpu_0_0_0_i_05;
int32_t _cpu_0_0_0_i_06;
int32_t _cpu_0_0_0_i_07;
int32_t _cpu_0_0_0_i_08;
int32_t _cpu_0_0_0_i_09;

void InitStatics_cpu_0_0_0()
{
    {
    (_cpu_0_0_0_PRINTINFO)=0;
    (_cpu_0_0_0_PLAINTEXT)=0;
    (_cpu_0_0_0_USERKEY)=1;
    (_cpu_0_0_0_CIPHERTEXT)=2;
    (_cpu_0_0_0_MAXROUNDS)=4;
    (_cpu_0_0_0_USERKEYS)=(int32_t**)malloc(sizeof(int32_t*)*34);
    for ((_cpu_0_0_0_i)=0; (_cpu_0_0_0_i)<34; (_cpu_0_0_0_i)++)
        {
        (_cpu_0_0_0_USERKEYS)[(_cpu_0_0_0_i)]=(int32_t*)malloc(sizeof(int32_t)*2);
    }
    (_cpu_0_0_0_USERKEYS)[0][0]=0x00000000;
    (_cpu_0_0_0_USERKEYS)[0][1]=0x00000000;
    (_cpu_0_0_0_USERKEYS)[1][0]=0xFFFFFFFF;
    (_cpu_0_0_0_USERKEYS)[1][1]=0xFFFFFFFF;
    (_cpu_0_0_0_USERKEYS)[2][0]=0x30000000;
    (_cpu_0_0_0_USERKEYS)[2][1]=0x00000000;
    (_cpu_0_0_0_USERKEYS)[3][0]=0x11111111;
    (_cpu_0_0_0_USERKEYS)[3][1]=0x11111111;
    (_cpu_0_0_0_USERKEYS)[4][0]=0x01234567;
    (_cpu_0_0_0_USERKEYS)[4][1]=0x89ABCDEF;
    (_cpu_0_0_0_USERKEYS)[5][0]=0x11111111;
    (_cpu_0_0_0_USERKEYS)[5][1]=0x11111111;
    (_cpu_0_0_0_USERKEYS)[6][0]=0x00000000;
    (_cpu_0_0_0_USERKEYS)[6][1]=0x00000000;
    (_cpu_0_0_0_USERKEYS)[7][0]=0xFEDCBA98;
    (_cpu_0_0_0_USERKEYS)[7][1]=0x76543210;
    (_cpu_0_0_0_USERKEYS)[8][0]=0x7CA11045;
    (_cpu_0_0_0_USERKEYS)[8][1]=0x4A1A6E57;
    (_cpu_0_0_0_USERKEYS)[9][0]=0x0131D961;
    (_cpu_0_0_0_USERKEYS)[9][1]=0x9DC1376E;
    (_cpu_0_0_0_USERKEYS)[10][0]=0x07A1133E;
    (_cpu_0_0_0_USERKEYS)[10][1]=0x4A0B2686;
    (_cpu_0_0_0_USERKEYS)[11][0]=0x3849674C;
    (_cpu_0_0_0_USERKEYS)[11][1]=0x2602319E;
    (_cpu_0_0_0_USERKEYS)[12][0]=0x04B915BA;
    (_cpu_0_0_0_USERKEYS)[12][1]=0x43FEB5B6;
    (_cpu_0_0_0_USERKEYS)[13][0]=0x0113B970;
    (_cpu_0_0_0_USERKEYS)[13][1]=0xFD34F2CE;
    (_cpu_0_0_0_USERKEYS)[14][0]=0x0170F175;
    (_cpu_0_0_0_USERKEYS)[14][1]=0x468FB5E6;
    (_cpu_0_0_0_USERKEYS)[15][0]=0x43297FAD;
    (_cpu_0_0_0_USERKEYS)[15][1]=0x38E373FE;
    (_cpu_0_0_0_USERKEYS)[16][0]=0x07A71370;
    (_cpu_0_0_0_USERKEYS)[16][1]=0x45DA2A16;
    (_cpu_0_0_0_USERKEYS)[17][0]=0x04689104;
    (_cpu_0_0_0_USERKEYS)[17][1]=0xC2FD3B2F;
    (_cpu_0_0_0_USERKEYS)[18][0]=0x37D06BB5;
    (_cpu_0_0_0_USERKEYS)[18][1]=0x16CB7546;
    (_cpu_0_0_0_USERKEYS)[19][0]=0x1F08260D;
    (_cpu_0_0_0_USERKEYS)[19][1]=0x1AC2465E;
    (_cpu_0_0_0_USERKEYS)[20][0]=0x58402364;
    (_cpu_0_0_0_USERKEYS)[20][1]=0x1ABA6176;
    (_cpu_0_0_0_USERKEYS)[21][0]=0x02581616;
    (_cpu_0_0_0_USERKEYS)[21][1]=0x4629B007;
    (_cpu_0_0_0_USERKEYS)[22][0]=0x49793EBC;
    (_cpu_0_0_0_USERKEYS)[22][1]=0x79B3258F;
    (_cpu_0_0_0_USERKEYS)[23][0]=0x4FB05E15;
    (_cpu_0_0_0_USERKEYS)[23][1]=0x15AB73A7;
    (_cpu_0_0_0_USERKEYS)[24][0]=0x49E95D6D;
    (_cpu_0_0_0_USERKEYS)[24][1]=0x4CA229BF;
    (_cpu_0_0_0_USERKEYS)[25][0]=0x018310DC;
    (_cpu_0_0_0_USERKEYS)[25][1]=0x409B26D6;
    (_cpu_0_0_0_USERKEYS)[26][0]=0x1C587F1C;
    (_cpu_0_0_0_USERKEYS)[26][1]=0x13924FEF;
    (_cpu_0_0_0_USERKEYS)[27][0]=0x01010101;
    (_cpu_0_0_0_USERKEYS)[27][1]=0x01010101;
    (_cpu_0_0_0_USERKEYS)[28][0]=0x1F1F1F1F;
    (_cpu_0_0_0_USERKEYS)[28][1]=0x0E0E0E0E;
    (_cpu_0_0_0_USERKEYS)[29][0]=0xE0FEE0FE;
    (_cpu_0_0_0_USERKEYS)[29][1]=0xF1FEF1FE;
    (_cpu_0_0_0_USERKEYS)[30][0]=0x00000000;
    (_cpu_0_0_0_USERKEYS)[30][1]=0x00000000;
    (_cpu_0_0_0_USERKEYS)[31][0]=0xFFFFFFFF;
    (_cpu_0_0_0_USERKEYS)[31][1]=0xFFFFFFFF;
    (_cpu_0_0_0_USERKEYS)[32][0]=0x01234567;
    (_cpu_0_0_0_USERKEYS)[32][1]=0x89ABCDEF;
    (_cpu_0_0_0_USERKEYS)[33][0]=0xFEDCBA98;
    (_cpu_0_0_0_USERKEYS)[33][1]=0x76543210;
    (_cpu_0_0_0_PC1)=(int32_t*)malloc(sizeof(int32_t)*56);
    (_cpu_0_0_0_PC1)[0]=57;
    (_cpu_0_0_0_PC1)[1]=49;
    (_cpu_0_0_0_PC1)[2]=41;
    (_cpu_0_0_0_PC1)[3]=33;
    (_cpu_0_0_0_PC1)[4]=25;
    (_cpu_0_0_0_PC1)[5]=17;
    (_cpu_0_0_0_PC1)[6]=9;
    (_cpu_0_0_0_PC1)[7]=1;
    (_cpu_0_0_0_PC1)[8]=58;
    (_cpu_0_0_0_PC1)[9]=50;
    (_cpu_0_0_0_PC1)[10]=42;
    (_cpu_0_0_0_PC1)[11]=34;
    (_cpu_0_0_0_PC1)[12]=26;
    (_cpu_0_0_0_PC1)[13]=18;
    (_cpu_0_0_0_PC1)[14]=10;
    (_cpu_0_0_0_PC1)[15]=2;
    (_cpu_0_0_0_PC1)[16]=59;
    (_cpu_0_0_0_PC1)[17]=51;
    (_cpu_0_0_0_PC1)[18]=43;
    (_cpu_0_0_0_PC1)[19]=35;
    (_cpu_0_0_0_PC1)[20]=27;
    (_cpu_0_0_0_PC1)[21]=19;
    (_cpu_0_0_0_PC1)[22]=11;
    (_cpu_0_0_0_PC1)[23]=3;
    (_cpu_0_0_0_PC1)[24]=60;
    (_cpu_0_0_0_PC1)[25]=52;
    (_cpu_0_0_0_PC1)[26]=44;
    (_cpu_0_0_0_PC1)[27]=36;
    (_cpu_0_0_0_PC1)[28]=63;
    (_cpu_0_0_0_PC1)[29]=55;
    (_cpu_0_0_0_PC1)[30]=47;
    (_cpu_0_0_0_PC1)[31]=39;
    (_cpu_0_0_0_PC1)[32]=31;
    (_cpu_0_0_0_PC1)[33]=23;
    (_cpu_0_0_0_PC1)[34]=15;
    (_cpu_0_0_0_PC1)[35]=7;
    (_cpu_0_0_0_PC1)[36]=62;
    (_cpu_0_0_0_PC1)[37]=54;
    (_cpu_0_0_0_PC1)[38]=46;
    (_cpu_0_0_0_PC1)[39]=38;
    (_cpu_0_0_0_PC1)[40]=30;
    (_cpu_0_0_0_PC1)[41]=22;
    (_cpu_0_0_0_PC1)[42]=14;
    (_cpu_0_0_0_PC1)[43]=6;
    (_cpu_0_0_0_PC1)[44]=61;
    (_cpu_0_0_0_PC1)[45]=53;
    (_cpu_0_0_0_PC1)[46]=45;
    (_cpu_0_0_0_PC1)[47]=37;
    (_cpu_0_0_0_PC1)[48]=29;
    (_cpu_0_0_0_PC1)[49]=21;
    (_cpu_0_0_0_PC1)[50]=13;
    (_cpu_0_0_0_PC1)[51]=5;
    (_cpu_0_0_0_PC1)[52]=28;
    (_cpu_0_0_0_PC1)[53]=20;
    (_cpu_0_0_0_PC1)[54]=12;
    (_cpu_0_0_0_PC1)[55]=4;
    (_cpu_0_0_0_PC2)=(int32_t*)malloc(sizeof(int32_t)*48);
    (_cpu_0_0_0_PC2)[0]=14;
    (_cpu_0_0_0_PC2)[1]=17;
    (_cpu_0_0_0_PC2)[2]=11;
    (_cpu_0_0_0_PC2)[3]=24;
    (_cpu_0_0_0_PC2)[4]=1;
    (_cpu_0_0_0_PC2)[5]=5;
    (_cpu_0_0_0_PC2)[6]=3;
    (_cpu_0_0_0_PC2)[7]=28;
    (_cpu_0_0_0_PC2)[8]=15;
    (_cpu_0_0_0_PC2)[9]=6;
    (_cpu_0_0_0_PC2)[10]=21;
    (_cpu_0_0_0_PC2)[11]=10;
    (_cpu_0_0_0_PC2)[12]=23;
    (_cpu_0_0_0_PC2)[13]=19;
    (_cpu_0_0_0_PC2)[14]=12;
    (_cpu_0_0_0_PC2)[15]=4;
    (_cpu_0_0_0_PC2)[16]=26;
    (_cpu_0_0_0_PC2)[17]=8;
    (_cpu_0_0_0_PC2)[18]=16;
    (_cpu_0_0_0_PC2)[19]=7;
    (_cpu_0_0_0_PC2)[20]=27;
    (_cpu_0_0_0_PC2)[21]=20;
    (_cpu_0_0_0_PC2)[22]=13;
    (_cpu_0_0_0_PC2)[23]=2;
    (_cpu_0_0_0_PC2)[24]=41;
    (_cpu_0_0_0_PC2)[25]=52;
    (_cpu_0_0_0_PC2)[26]=31;
    (_cpu_0_0_0_PC2)[27]=37;
    (_cpu_0_0_0_PC2)[28]=47;
    (_cpu_0_0_0_PC2)[29]=55;
    (_cpu_0_0_0_PC2)[30]=30;
    (_cpu_0_0_0_PC2)[31]=40;
    (_cpu_0_0_0_PC2)[32]=51;
    (_cpu_0_0_0_PC2)[33]=45;
    (_cpu_0_0_0_PC2)[34]=33;
    (_cpu_0_0_0_PC2)[35]=48;
    (_cpu_0_0_0_PC2)[36]=44;
    (_cpu_0_0_0_PC2)[37]=49;
    (_cpu_0_0_0_PC2)[38]=39;
    (_cpu_0_0_0_PC2)[39]=56;
    (_cpu_0_0_0_PC2)[40]=34;
    (_cpu_0_0_0_PC2)[41]=53;
    (_cpu_0_0_0_PC2)[42]=46;
    (_cpu_0_0_0_PC2)[43]=42;
    (_cpu_0_0_0_PC2)[44]=50;
    (_cpu_0_0_0_PC2)[45]=36;
    (_cpu_0_0_0_PC2)[46]=29;
    (_cpu_0_0_0_PC2)[47]=32;
    (_cpu_0_0_0_RT)=(int32_t*)malloc(sizeof(int32_t)*16);
    (_cpu_0_0_0_RT)[0]=1;
    (_cpu_0_0_0_RT)[1]=1;
    (_cpu_0_0_0_RT)[2]=2;
    (_cpu_0_0_0_RT)[3]=2;
    (_cpu_0_0_0_RT)[4]=2;
    (_cpu_0_0_0_RT)[5]=2;
    (_cpu_0_0_0_RT)[6]=2;
    (_cpu_0_0_0_RT)[7]=2;
    (_cpu_0_0_0_RT)[8]=1;
    (_cpu_0_0_0_RT)[9]=2;
    (_cpu_0_0_0_RT)[10]=2;
    (_cpu_0_0_0_RT)[11]=2;
    (_cpu_0_0_0_RT)[12]=2;
    (_cpu_0_0_0_RT)[13]=2;
    (_cpu_0_0_0_RT)[14]=2;
    (_cpu_0_0_0_RT)[15]=1;
    (_cpu_0_0_0_IP)=(int32_t*)malloc(sizeof(int32_t)*64);
    (_cpu_0_0_0_IP)[0]=58;
    (_cpu_0_0_0_IP)[1]=50;
    (_cpu_0_0_0_IP)[2]=42;
    (_cpu_0_0_0_IP)[3]=34;
    (_cpu_0_0_0_IP)[4]=26;
    (_cpu_0_0_0_IP)[5]=18;
    (_cpu_0_0_0_IP)[6]=10;
    (_cpu_0_0_0_IP)[7]=2;
    (_cpu_0_0_0_IP)[8]=60;
    (_cpu_0_0_0_IP)[9]=52;
    (_cpu_0_0_0_IP)[10]=44;
    (_cpu_0_0_0_IP)[11]=36;
    (_cpu_0_0_0_IP)[12]=28;
    (_cpu_0_0_0_IP)[13]=20;
    (_cpu_0_0_0_IP)[14]=12;
    (_cpu_0_0_0_IP)[15]=4;
    (_cpu_0_0_0_IP)[16]=62;
    (_cpu_0_0_0_IP)[17]=54;
    (_cpu_0_0_0_IP)[18]=46;
    (_cpu_0_0_0_IP)[19]=38;
    (_cpu_0_0_0_IP)[20]=30;
    (_cpu_0_0_0_IP)[21]=22;
    (_cpu_0_0_0_IP)[22]=14;
    (_cpu_0_0_0_IP)[23]=6;
    (_cpu_0_0_0_IP)[24]=64;
    (_cpu_0_0_0_IP)[25]=56;
    (_cpu_0_0_0_IP)[26]=48;
    (_cpu_0_0_0_IP)[27]=40;
    (_cpu_0_0_0_IP)[28]=32;
    (_cpu_0_0_0_IP)[29]=24;
    (_cpu_0_0_0_IP)[30]=16;
    (_cpu_0_0_0_IP)[31]=8;
    (_cpu_0_0_0_IP)[32]=57;
    (_cpu_0_0_0_IP)[33]=49;
    (_cpu_0_0_0_IP)[34]=41;
    (_cpu_0_0_0_IP)[35]=33;
    (_cpu_0_0_0_IP)[36]=25;
    (_cpu_0_0_0_IP)[37]=17;
    (_cpu_0_0_0_IP)[38]=9;
    (_cpu_0_0_0_IP)[39]=1;
    (_cpu_0_0_0_IP)[40]=59;
    (_cpu_0_0_0_IP)[41]=51;
    (_cpu_0_0_0_IP)[42]=43;
    (_cpu_0_0_0_IP)[43]=35;
    (_cpu_0_0_0_IP)[44]=27;
    (_cpu_0_0_0_IP)[45]=19;
    (_cpu_0_0_0_IP)[46]=11;
    (_cpu_0_0_0_IP)[47]=3;
    (_cpu_0_0_0_IP)[48]=61;
    (_cpu_0_0_0_IP)[49]=53;
    (_cpu_0_0_0_IP)[50]=45;
    (_cpu_0_0_0_IP)[51]=37;
    (_cpu_0_0_0_IP)[52]=29;
    (_cpu_0_0_0_IP)[53]=21;
    (_cpu_0_0_0_IP)[54]=13;
    (_cpu_0_0_0_IP)[55]=5;
    (_cpu_0_0_0_IP)[56]=63;
    (_cpu_0_0_0_IP)[57]=55;
    (_cpu_0_0_0_IP)[58]=47;
    (_cpu_0_0_0_IP)[59]=39;
    (_cpu_0_0_0_IP)[60]=31;
    (_cpu_0_0_0_IP)[61]=23;
    (_cpu_0_0_0_IP)[62]=15;
    (_cpu_0_0_0_IP)[63]=7;
    (_cpu_0_0_0_E)=(int32_t*)malloc(sizeof(int32_t)*48);
    (_cpu_0_0_0_E)[0]=32;
    (_cpu_0_0_0_E)[1]=1;
    (_cpu_0_0_0_E)[2]=2;
    (_cpu_0_0_0_E)[3]=3;
    (_cpu_0_0_0_E)[4]=4;
    (_cpu_0_0_0_E)[5]=5;
    (_cpu_0_0_0_E)[6]=4;
    (_cpu_0_0_0_E)[7]=5;
    (_cpu_0_0_0_E)[8]=6;
    (_cpu_0_0_0_E)[9]=7;
    (_cpu_0_0_0_E)[10]=8;
    (_cpu_0_0_0_E)[11]=9;
    (_cpu_0_0_0_E)[12]=8;
    (_cpu_0_0_0_E)[13]=9;
    (_cpu_0_0_0_E)[14]=10;
    (_cpu_0_0_0_E)[15]=11;
    (_cpu_0_0_0_E)[16]=12;
    (_cpu_0_0_0_E)[17]=13;
    (_cpu_0_0_0_E)[18]=12;
    (_cpu_0_0_0_E)[19]=13;
    (_cpu_0_0_0_E)[20]=14;
    (_cpu_0_0_0_E)[21]=15;
    (_cpu_0_0_0_E)[22]=16;
    (_cpu_0_0_0_E)[23]=17;
    (_cpu_0_0_0_E)[24]=16;
    (_cpu_0_0_0_E)[25]=17;
    (_cpu_0_0_0_E)[26]=18;
    (_cpu_0_0_0_E)[27]=19;
    (_cpu_0_0_0_E)[28]=20;
    (_cpu_0_0_0_E)[29]=21;
    (_cpu_0_0_0_E)[30]=20;
    (_cpu_0_0_0_E)[31]=21;
    (_cpu_0_0_0_E)[32]=22;
    (_cpu_0_0_0_E)[33]=23;
    (_cpu_0_0_0_E)[34]=24;
    (_cpu_0_0_0_E)[35]=25;
    (_cpu_0_0_0_E)[36]=24;
    (_cpu_0_0_0_E)[37]=25;
    (_cpu_0_0_0_E)[38]=26;
    (_cpu_0_0_0_E)[39]=27;
    (_cpu_0_0_0_E)[40]=28;
    (_cpu_0_0_0_E)[41]=29;
    (_cpu_0_0_0_E)[42]=28;
    (_cpu_0_0_0_E)[43]=29;
    (_cpu_0_0_0_E)[44]=30;
    (_cpu_0_0_0_E)[45]=31;
    (_cpu_0_0_0_E)[46]=32;
    (_cpu_0_0_0_E)[47]=1;
    (_cpu_0_0_0_P)=(int32_t*)malloc(sizeof(int32_t)*32);
    (_cpu_0_0_0_P)[0]=16;
    (_cpu_0_0_0_P)[1]=7;
    (_cpu_0_0_0_P)[2]=20;
    (_cpu_0_0_0_P)[3]=21;
    (_cpu_0_0_0_P)[4]=29;
    (_cpu_0_0_0_P)[5]=12;
    (_cpu_0_0_0_P)[6]=28;
    (_cpu_0_0_0_P)[7]=17;
    (_cpu_0_0_0_P)[8]=1;
    (_cpu_0_0_0_P)[9]=15;
    (_cpu_0_0_0_P)[10]=23;
    (_cpu_0_0_0_P)[11]=26;
    (_cpu_0_0_0_P)[12]=5;
    (_cpu_0_0_0_P)[13]=18;
    (_cpu_0_0_0_P)[14]=31;
    (_cpu_0_0_0_P)[15]=10;
    (_cpu_0_0_0_P)[16]=2;
    (_cpu_0_0_0_P)[17]=8;
    (_cpu_0_0_0_P)[18]=24;
    (_cpu_0_0_0_P)[19]=14;
    (_cpu_0_0_0_P)[20]=32;
    (_cpu_0_0_0_P)[21]=27;
    (_cpu_0_0_0_P)[22]=3;
    (_cpu_0_0_0_P)[23]=9;
    (_cpu_0_0_0_P)[24]=19;
    (_cpu_0_0_0_P)[25]=13;
    (_cpu_0_0_0_P)[26]=30;
    (_cpu_0_0_0_P)[27]=6;
    (_cpu_0_0_0_P)[28]=22;
    (_cpu_0_0_0_P)[29]=11;
    (_cpu_0_0_0_P)[30]=4;
    (_cpu_0_0_0_P)[31]=25;
    (_cpu_0_0_0_IPm1)=(int32_t*)malloc(sizeof(int32_t)*64);
    (_cpu_0_0_0_IPm1)[0]=40;
    (_cpu_0_0_0_IPm1)[1]=8;
    (_cpu_0_0_0_IPm1)[2]=48;
    (_cpu_0_0_0_IPm1)[3]=16;
    (_cpu_0_0_0_IPm1)[4]=56;
    (_cpu_0_0_0_IPm1)[5]=24;
    (_cpu_0_0_0_IPm1)[6]=64;
    (_cpu_0_0_0_IPm1)[7]=32;
    (_cpu_0_0_0_IPm1)[8]=39;
    (_cpu_0_0_0_IPm1)[9]=7;
    (_cpu_0_0_0_IPm1)[10]=47;
    (_cpu_0_0_0_IPm1)[11]=15;
    (_cpu_0_0_0_IPm1)[12]=55;
    (_cpu_0_0_0_IPm1)[13]=23;
    (_cpu_0_0_0_IPm1)[14]=63;
    (_cpu_0_0_0_IPm1)[15]=31;
    (_cpu_0_0_0_IPm1)[16]=38;
    (_cpu_0_0_0_IPm1)[17]=6;
    (_cpu_0_0_0_IPm1)[18]=46;
    (_cpu_0_0_0_IPm1)[19]=14;
    (_cpu_0_0_0_IPm1)[20]=54;
    (_cpu_0_0_0_IPm1)[21]=22;
    (_cpu_0_0_0_IPm1)[22]=62;
    (_cpu_0_0_0_IPm1)[23]=30;
    (_cpu_0_0_0_IPm1)[24]=37;
    (_cpu_0_0_0_IPm1)[25]=5;
    (_cpu_0_0_0_IPm1)[26]=45;
    (_cpu_0_0_0_IPm1)[27]=13;
    (_cpu_0_0_0_IPm1)[28]=53;
    (_cpu_0_0_0_IPm1)[29]=21;
    (_cpu_0_0_0_IPm1)[30]=61;
    (_cpu_0_0_0_IPm1)[31]=29;
    (_cpu_0_0_0_IPm1)[32]=36;
    (_cpu_0_0_0_IPm1)[33]=4;
    (_cpu_0_0_0_IPm1)[34]=44;
    (_cpu_0_0_0_IPm1)[35]=12;
    (_cpu_0_0_0_IPm1)[36]=52;
    (_cpu_0_0_0_IPm1)[37]=20;
    (_cpu_0_0_0_IPm1)[38]=60;
    (_cpu_0_0_0_IPm1)[39]=28;
    (_cpu_0_0_0_IPm1)[40]=35;
    (_cpu_0_0_0_IPm1)[41]=3;
    (_cpu_0_0_0_IPm1)[42]=43;
    (_cpu_0_0_0_IPm1)[43]=11;
    (_cpu_0_0_0_IPm1)[44]=51;
    (_cpu_0_0_0_IPm1)[45]=19;
    (_cpu_0_0_0_IPm1)[46]=59;
    (_cpu_0_0_0_IPm1)[47]=27;
    (_cpu_0_0_0_IPm1)[48]=34;
    (_cpu_0_0_0_IPm1)[49]=2;
    (_cpu_0_0_0_IPm1)[50]=42;
    (_cpu_0_0_0_IPm1)[51]=10;
    (_cpu_0_0_0_IPm1)[52]=50;
    (_cpu_0_0_0_IPm1)[53]=18;
    (_cpu_0_0_0_IPm1)[54]=58;
    (_cpu_0_0_0_IPm1)[55]=26;
    (_cpu_0_0_0_IPm1)[56]=33;
    (_cpu_0_0_0_IPm1)[57]=1;
    (_cpu_0_0_0_IPm1)[58]=41;
    (_cpu_0_0_0_IPm1)[59]=9;
    (_cpu_0_0_0_IPm1)[60]=49;
    (_cpu_0_0_0_IPm1)[61]=17;
    (_cpu_0_0_0_IPm1)[62]=57;
    (_cpu_0_0_0_IPm1)[63]=25;
    (_cpu_0_0_0_S1)=(int32_t**)malloc(sizeof(int32_t*)*4);
    for ((_cpu_0_0_0_i)=0; (_cpu_0_0_0_i)<4; (_cpu_0_0_0_i)++)
        {
        (_cpu_0_0_0_S1)[(_cpu_0_0_0_i)]=(int32_t*)malloc(sizeof(int32_t)*16);
    }
    (_cpu_0_0_0_S1)[0][0]=14;
    (_cpu_0_0_0_S1)[0][1]=4;
    (_cpu_0_0_0_S1)[0][2]=13;
    (_cpu_0_0_0_S1)[0][3]=1;
    (_cpu_0_0_0_S1)[0][4]=2;
    (_cpu_0_0_0_S1)[0][5]=15;
    (_cpu_0_0_0_S1)[0][6]=11;
    (_cpu_0_0_0_S1)[0][7]=8;
    (_cpu_0_0_0_S1)[0][8]=3;
    (_cpu_0_0_0_S1)[0][9]=10;
    (_cpu_0_0_0_S1)[0][10]=6;
    (_cpu_0_0_0_S1)[0][11]=12;
    (_cpu_0_0_0_S1)[0][12]=5;
    (_cpu_0_0_0_S1)[0][13]=9;
    (_cpu_0_0_0_S1)[0][14]=0;
    (_cpu_0_0_0_S1)[0][15]=7;
    (_cpu_0_0_0_S1)[1][0]=0;
    (_cpu_0_0_0_S1)[1][1]=15;
    (_cpu_0_0_0_S1)[1][2]=7;
    (_cpu_0_0_0_S1)[1][3]=4;
    (_cpu_0_0_0_S1)[1][4]=14;
    (_cpu_0_0_0_S1)[1][5]=2;
    (_cpu_0_0_0_S1)[1][6]=13;
    (_cpu_0_0_0_S1)[1][7]=1;
    (_cpu_0_0_0_S1)[1][8]=10;
    (_cpu_0_0_0_S1)[1][9]=6;
    (_cpu_0_0_0_S1)[1][10]=12;
    (_cpu_0_0_0_S1)[1][11]=11;
    (_cpu_0_0_0_S1)[1][12]=9;
    (_cpu_0_0_0_S1)[1][13]=5;
    (_cpu_0_0_0_S1)[1][14]=3;
    (_cpu_0_0_0_S1)[1][15]=8;
    (_cpu_0_0_0_S1)[2][0]=4;
    (_cpu_0_0_0_S1)[2][1]=1;
    (_cpu_0_0_0_S1)[2][2]=14;
    (_cpu_0_0_0_S1)[2][3]=8;
    (_cpu_0_0_0_S1)[2][4]=13;
    (_cpu_0_0_0_S1)[2][5]=6;
    (_cpu_0_0_0_S1)[2][6]=2;
    (_cpu_0_0_0_S1)[2][7]=11;
    (_cpu_0_0_0_S1)[2][8]=15;
    (_cpu_0_0_0_S1)[2][9]=12;
    (_cpu_0_0_0_S1)[2][10]=9;
    (_cpu_0_0_0_S1)[2][11]=7;
    (_cpu_0_0_0_S1)[2][12]=3;
    (_cpu_0_0_0_S1)[2][13]=10;
    (_cpu_0_0_0_S1)[2][14]=5;
    (_cpu_0_0_0_S1)[2][15]=0;
    (_cpu_0_0_0_S1)[3][0]=15;
    (_cpu_0_0_0_S1)[3][1]=12;
    (_cpu_0_0_0_S1)[3][2]=8;
    (_cpu_0_0_0_S1)[3][3]=2;
    (_cpu_0_0_0_S1)[3][4]=4;
    (_cpu_0_0_0_S1)[3][5]=9;
    (_cpu_0_0_0_S1)[3][6]=1;
    (_cpu_0_0_0_S1)[3][7]=7;
    (_cpu_0_0_0_S1)[3][8]=5;
    (_cpu_0_0_0_S1)[3][9]=11;
    (_cpu_0_0_0_S1)[3][10]=3;
    (_cpu_0_0_0_S1)[3][11]=14;
    (_cpu_0_0_0_S1)[3][12]=10;
    (_cpu_0_0_0_S1)[3][13]=0;
    (_cpu_0_0_0_S1)[3][14]=6;
    (_cpu_0_0_0_S1)[3][15]=13;
    (_cpu_0_0_0_S2)=(int32_t**)malloc(sizeof(int32_t*)*4);
    for ((_cpu_0_0_0_i)=0; (_cpu_0_0_0_i)<4; (_cpu_0_0_0_i)++)
        {
        (_cpu_0_0_0_S2)[(_cpu_0_0_0_i)]=(int32_t*)malloc(sizeof(int32_t)*16);
    }
    (_cpu_0_0_0_S2)[0][0]=15;
    (_cpu_0_0_0_S2)[0][1]=1;
    (_cpu_0_0_0_S2)[0][2]=8;
    (_cpu_0_0_0_S2)[0][3]=14;
    (_cpu_0_0_0_S2)[0][4]=6;
    (_cpu_0_0_0_S2)[0][5]=11;
    (_cpu_0_0_0_S2)[0][6]=3;
    (_cpu_0_0_0_S2)[0][7]=4;
    (_cpu_0_0_0_S2)[0][8]=9;
    (_cpu_0_0_0_S2)[0][9]=7;
    (_cpu_0_0_0_S2)[0][10]=2;
    (_cpu_0_0_0_S2)[0][11]=13;
    (_cpu_0_0_0_S2)[0][12]=12;
    (_cpu_0_0_0_S2)[0][13]=0;
    (_cpu_0_0_0_S2)[0][14]=5;
    (_cpu_0_0_0_S2)[0][15]=10;
    (_cpu_0_0_0_S2)[1][0]=3;
    (_cpu_0_0_0_S2)[1][1]=13;
    (_cpu_0_0_0_S2)[1][2]=4;
    (_cpu_0_0_0_S2)[1][3]=7;
    (_cpu_0_0_0_S2)[1][4]=15;
    (_cpu_0_0_0_S2)[1][5]=2;
    (_cpu_0_0_0_S2)[1][6]=8;
    (_cpu_0_0_0_S2)[1][7]=14;
    (_cpu_0_0_0_S2)[1][8]=12;
    (_cpu_0_0_0_S2)[1][9]=0;
    (_cpu_0_0_0_S2)[1][10]=1;
    (_cpu_0_0_0_S2)[1][11]=10;
    (_cpu_0_0_0_S2)[1][12]=6;
    (_cpu_0_0_0_S2)[1][13]=9;
    (_cpu_0_0_0_S2)[1][14]=11;
    (_cpu_0_0_0_S2)[1][15]=5;
    (_cpu_0_0_0_S2)[2][0]=0;
    (_cpu_0_0_0_S2)[2][1]=14;
    (_cpu_0_0_0_S2)[2][2]=7;
    (_cpu_0_0_0_S2)[2][3]=11;
    (_cpu_0_0_0_S2)[2][4]=10;
    (_cpu_0_0_0_S2)[2][5]=4;
    (_cpu_0_0_0_S2)[2][6]=13;
    (_cpu_0_0_0_S2)[2][7]=1;
    (_cpu_0_0_0_S2)[2][8]=5;
    (_cpu_0_0_0_S2)[2][9]=8;
    (_cpu_0_0_0_S2)[2][10]=12;
    (_cpu_0_0_0_S2)[2][11]=6;
    (_cpu_0_0_0_S2)[2][12]=9;
    (_cpu_0_0_0_S2)[2][13]=3;
    (_cpu_0_0_0_S2)[2][14]=2;
    (_cpu_0_0_0_S2)[2][15]=15;
    (_cpu_0_0_0_S2)[3][0]=13;
    (_cpu_0_0_0_S2)[3][1]=8;
    (_cpu_0_0_0_S2)[3][2]=10;
    (_cpu_0_0_0_S2)[3][3]=1;
    (_cpu_0_0_0_S2)[3][4]=3;
    (_cpu_0_0_0_S2)[3][5]=15;
    (_cpu_0_0_0_S2)[3][6]=4;
    (_cpu_0_0_0_S2)[3][7]=2;
    (_cpu_0_0_0_S2)[3][8]=11;
    (_cpu_0_0_0_S2)[3][9]=6;
    (_cpu_0_0_0_S2)[3][10]=7;
    (_cpu_0_0_0_S2)[3][11]=12;
    (_cpu_0_0_0_S2)[3][12]=0;
    (_cpu_0_0_0_S2)[3][13]=5;
    (_cpu_0_0_0_S2)[3][14]=14;
    (_cpu_0_0_0_S2)[3][15]=9;
    (_cpu_0_0_0_S3)=(int32_t**)malloc(sizeof(int32_t*)*4);
    for ((_cpu_0_0_0_i)=0; (_cpu_0_0_0_i)<4; (_cpu_0_0_0_i)++)
        {
        (_cpu_0_0_0_S3)[(_cpu_0_0_0_i)]=(int32_t*)malloc(sizeof(int32_t)*16);
    }
    (_cpu_0_0_0_S3)[0][0]=10;
    (_cpu_0_0_0_S3)[0][1]=0;
    (_cpu_0_0_0_S3)[0][2]=9;
    (_cpu_0_0_0_S3)[0][3]=14;
    (_cpu_0_0_0_S3)[0][4]=6;
    (_cpu_0_0_0_S3)[0][5]=3;
    (_cpu_0_0_0_S3)[0][6]=15;
    (_cpu_0_0_0_S3)[0][7]=5;
    (_cpu_0_0_0_S3)[0][8]=1;
    (_cpu_0_0_0_S3)[0][9]=13;
    (_cpu_0_0_0_S3)[0][10]=12;
    (_cpu_0_0_0_S3)[0][11]=7;
    (_cpu_0_0_0_S3)[0][12]=11;
    (_cpu_0_0_0_S3)[0][13]=4;
    (_cpu_0_0_0_S3)[0][14]=2;
    (_cpu_0_0_0_S3)[0][15]=8;
    (_cpu_0_0_0_S3)[1][0]=13;
    (_cpu_0_0_0_S3)[1][1]=7;
    (_cpu_0_0_0_S3)[1][2]=0;
    (_cpu_0_0_0_S3)[1][3]=9;
    (_cpu_0_0_0_S3)[1][4]=3;
    (_cpu_0_0_0_S3)[1][5]=4;
    (_cpu_0_0_0_S3)[1][6]=6;
    (_cpu_0_0_0_S3)[1][7]=10;
    (_cpu_0_0_0_S3)[1][8]=2;
    (_cpu_0_0_0_S3)[1][9]=8;
    (_cpu_0_0_0_S3)[1][10]=5;
    (_cpu_0_0_0_S3)[1][11]=14;
    (_cpu_0_0_0_S3)[1][12]=12;
    (_cpu_0_0_0_S3)[1][13]=11;
    (_cpu_0_0_0_S3)[1][14]=15;
    (_cpu_0_0_0_S3)[1][15]=1;
    (_cpu_0_0_0_S3)[2][0]=13;
    (_cpu_0_0_0_S3)[2][1]=6;
    (_cpu_0_0_0_S3)[2][2]=4;
    (_cpu_0_0_0_S3)[2][3]=9;
    (_cpu_0_0_0_S3)[2][4]=8;
    (_cpu_0_0_0_S3)[2][5]=15;
    (_cpu_0_0_0_S3)[2][6]=3;
    (_cpu_0_0_0_S3)[2][7]=0;
    (_cpu_0_0_0_S3)[2][8]=11;
    (_cpu_0_0_0_S3)[2][9]=1;
    (_cpu_0_0_0_S3)[2][10]=2;
    (_cpu_0_0_0_S3)[2][11]=12;
    (_cpu_0_0_0_S3)[2][12]=5;
    (_cpu_0_0_0_S3)[2][13]=10;
    (_cpu_0_0_0_S3)[2][14]=14;
    (_cpu_0_0_0_S3)[2][15]=7;
    (_cpu_0_0_0_S3)[3][0]=1;
    (_cpu_0_0_0_S3)[3][1]=10;
    (_cpu_0_0_0_S3)[3][2]=13;
    (_cpu_0_0_0_S3)[3][3]=0;
    (_cpu_0_0_0_S3)[3][4]=6;
    (_cpu_0_0_0_S3)[3][5]=9;
    (_cpu_0_0_0_S3)[3][6]=8;
    (_cpu_0_0_0_S3)[3][7]=7;
    (_cpu_0_0_0_S3)[3][8]=4;
    (_cpu_0_0_0_S3)[3][9]=15;
    (_cpu_0_0_0_S3)[3][10]=14;
    (_cpu_0_0_0_S3)[3][11]=3;
    (_cpu_0_0_0_S3)[3][12]=11;
    (_cpu_0_0_0_S3)[3][13]=5;
    (_cpu_0_0_0_S3)[3][14]=2;
    (_cpu_0_0_0_S3)[3][15]=12;
    (_cpu_0_0_0_S4)=(int32_t**)malloc(sizeof(int32_t*)*4);
    for ((_cpu_0_0_0_i)=0; (_cpu_0_0_0_i)<4; (_cpu_0_0_0_i)++)
        {
        (_cpu_0_0_0_S4)[(_cpu_0_0_0_i)]=(int32_t*)malloc(sizeof(int32_t)*16);
    }
    (_cpu_0_0_0_S4)[0][0]=7;
    (_cpu_0_0_0_S4)[0][1]=13;
    (_cpu_0_0_0_S4)[0][2]=14;
    (_cpu_0_0_0_S4)[0][3]=3;
    (_cpu_0_0_0_S4)[0][4]=0;
    (_cpu_0_0_0_S4)[0][5]=6;
    (_cpu_0_0_0_S4)[0][6]=9;
    (_cpu_0_0_0_S4)[0][7]=10;
    (_cpu_0_0_0_S4)[0][8]=1;
    (_cpu_0_0_0_S4)[0][9]=2;
    (_cpu_0_0_0_S4)[0][10]=8;
    (_cpu_0_0_0_S4)[0][11]=5;
    (_cpu_0_0_0_S4)[0][12]=11;
    (_cpu_0_0_0_S4)[0][13]=12;
    (_cpu_0_0_0_S4)[0][14]=4;
    (_cpu_0_0_0_S4)[0][15]=15;
    (_cpu_0_0_0_S4)[1][0]=13;
    (_cpu_0_0_0_S4)[1][1]=8;
    (_cpu_0_0_0_S4)[1][2]=11;
    (_cpu_0_0_0_S4)[1][3]=5;
    (_cpu_0_0_0_S4)[1][4]=6;
    (_cpu_0_0_0_S4)[1][5]=15;
    (_cpu_0_0_0_S4)[1][6]=0;
    (_cpu_0_0_0_S4)[1][7]=3;
    (_cpu_0_0_0_S4)[1][8]=4;
    (_cpu_0_0_0_S4)[1][9]=7;
    (_cpu_0_0_0_S4)[1][10]=2;
    (_cpu_0_0_0_S4)[1][11]=12;
    (_cpu_0_0_0_S4)[1][12]=1;
    (_cpu_0_0_0_S4)[1][13]=10;
    (_cpu_0_0_0_S4)[1][14]=14;
    (_cpu_0_0_0_S4)[1][15]=9;
    (_cpu_0_0_0_S4)[2][0]=10;
    (_cpu_0_0_0_S4)[2][1]=6;
    (_cpu_0_0_0_S4)[2][2]=9;
    (_cpu_0_0_0_S4)[2][3]=0;
    (_cpu_0_0_0_S4)[2][4]=12;
    (_cpu_0_0_0_S4)[2][5]=11;
    (_cpu_0_0_0_S4)[2][6]=7;
    (_cpu_0_0_0_S4)[2][7]=13;
    (_cpu_0_0_0_S4)[2][8]=15;
    (_cpu_0_0_0_S4)[2][9]=1;
    (_cpu_0_0_0_S4)[2][10]=3;
    (_cpu_0_0_0_S4)[2][11]=14;
    (_cpu_0_0_0_S4)[2][12]=5;
    (_cpu_0_0_0_S4)[2][13]=2;
    (_cpu_0_0_0_S4)[2][14]=8;
    (_cpu_0_0_0_S4)[2][15]=4;
    (_cpu_0_0_0_S4)[3][0]=3;
    (_cpu_0_0_0_S4)[3][1]=15;
    (_cpu_0_0_0_S4)[3][2]=0;
    (_cpu_0_0_0_S4)[3][3]=6;
    (_cpu_0_0_0_S4)[3][4]=10;
    (_cpu_0_0_0_S4)[3][5]=1;
    (_cpu_0_0_0_S4)[3][6]=13;
    (_cpu_0_0_0_S4)[3][7]=8;
    (_cpu_0_0_0_S4)[3][8]=9;
    (_cpu_0_0_0_S4)[3][9]=4;
    (_cpu_0_0_0_S4)[3][10]=5;
    (_cpu_0_0_0_S4)[3][11]=11;
    (_cpu_0_0_0_S4)[3][12]=12;
    (_cpu_0_0_0_S4)[3][13]=7;
    (_cpu_0_0_0_S4)[3][14]=2;
    (_cpu_0_0_0_S4)[3][15]=14;
    (_cpu_0_0_0_S5)=(int32_t**)malloc(sizeof(int32_t*)*4);
    for ((_cpu_0_0_0_i)=0; (_cpu_0_0_0_i)<4; (_cpu_0_0_0_i)++)
        {
        (_cpu_0_0_0_S5)[(_cpu_0_0_0_i)]=(int32_t*)malloc(sizeof(int32_t)*16);
    }
    (_cpu_0_0_0_S5)[0][0]=2;
    (_cpu_0_0_0_S5)[0][1]=12;
    (_cpu_0_0_0_S5)[0][2]=4;
    (_cpu_0_0_0_S5)[0][3]=1;
    (_cpu_0_0_0_S5)[0][4]=7;
    (_cpu_0_0_0_S5)[0][5]=10;
    (_cpu_0_0_0_S5)[0][6]=11;
    (_cpu_0_0_0_S5)[0][7]=6;
    (_cpu_0_0_0_S5)[0][8]=8;
    (_cpu_0_0_0_S5)[0][9]=5;
    (_cpu_0_0_0_S5)[0][10]=3;
    (_cpu_0_0_0_S5)[0][11]=15;
    (_cpu_0_0_0_S5)[0][12]=13;
    (_cpu_0_0_0_S5)[0][13]=0;
    (_cpu_0_0_0_S5)[0][14]=14;
    (_cpu_0_0_0_S5)[0][15]=9;
    (_cpu_0_0_0_S5)[1][0]=14;
    (_cpu_0_0_0_S5)[1][1]=11;
    (_cpu_0_0_0_S5)[1][2]=2;
    (_cpu_0_0_0_S5)[1][3]=12;
    (_cpu_0_0_0_S5)[1][4]=4;
    (_cpu_0_0_0_S5)[1][5]=7;
    (_cpu_0_0_0_S5)[1][6]=13;
    (_cpu_0_0_0_S5)[1][7]=1;
    (_cpu_0_0_0_S5)[1][8]=5;
    (_cpu_0_0_0_S5)[1][9]=0;
    (_cpu_0_0_0_S5)[1][10]=15;
    (_cpu_0_0_0_S5)[1][11]=10;
    (_cpu_0_0_0_S5)[1][12]=3;
    (_cpu_0_0_0_S5)[1][13]=9;
    (_cpu_0_0_0_S5)[1][14]=8;
    (_cpu_0_0_0_S5)[1][15]=6;
    (_cpu_0_0_0_S5)[2][0]=4;
    (_cpu_0_0_0_S5)[2][1]=2;
    (_cpu_0_0_0_S5)[2][2]=1;
    (_cpu_0_0_0_S5)[2][3]=11;
    (_cpu_0_0_0_S5)[2][4]=10;
    (_cpu_0_0_0_S5)[2][5]=13;
    (_cpu_0_0_0_S5)[2][6]=7;
    (_cpu_0_0_0_S5)[2][7]=8;
    (_cpu_0_0_0_S5)[2][8]=15;
    (_cpu_0_0_0_S5)[2][9]=9;
    (_cpu_0_0_0_S5)[2][10]=12;
    (_cpu_0_0_0_S5)[2][11]=5;
    (_cpu_0_0_0_S5)[2][12]=6;
    (_cpu_0_0_0_S5)[2][13]=3;
    (_cpu_0_0_0_S5)[2][14]=0;
    (_cpu_0_0_0_S5)[2][15]=14;
    (_cpu_0_0_0_S5)[3][0]=11;
    (_cpu_0_0_0_S5)[3][1]=8;
    (_cpu_0_0_0_S5)[3][2]=12;
    (_cpu_0_0_0_S5)[3][3]=7;
    (_cpu_0_0_0_S5)[3][4]=1;
    (_cpu_0_0_0_S5)[3][5]=14;
    (_cpu_0_0_0_S5)[3][6]=2;
    (_cpu_0_0_0_S5)[3][7]=13;
    (_cpu_0_0_0_S5)[3][8]=6;
    (_cpu_0_0_0_S5)[3][9]=15;
    (_cpu_0_0_0_S5)[3][10]=0;
    (_cpu_0_0_0_S5)[3][11]=9;
    (_cpu_0_0_0_S5)[3][12]=10;
    (_cpu_0_0_0_S5)[3][13]=4;
    (_cpu_0_0_0_S5)[3][14]=5;
    (_cpu_0_0_0_S5)[3][15]=3;
    (_cpu_0_0_0_S6)=(int32_t**)malloc(sizeof(int32_t*)*4);
    for ((_cpu_0_0_0_i)=0; (_cpu_0_0_0_i)<4; (_cpu_0_0_0_i)++)
        {
        (_cpu_0_0_0_S6)[(_cpu_0_0_0_i)]=(int32_t*)malloc(sizeof(int32_t)*16);
    }
    (_cpu_0_0_0_S6)[0][0]=12;
    (_cpu_0_0_0_S6)[0][1]=1;
    (_cpu_0_0_0_S6)[0][2]=10;
    (_cpu_0_0_0_S6)[0][3]=15;
    (_cpu_0_0_0_S6)[0][4]=9;
    (_cpu_0_0_0_S6)[0][5]=2;
    (_cpu_0_0_0_S6)[0][6]=6;
    (_cpu_0_0_0_S6)[0][7]=8;
    (_cpu_0_0_0_S6)[0][8]=0;
    (_cpu_0_0_0_S6)[0][9]=13;
    (_cpu_0_0_0_S6)[0][10]=3;
    (_cpu_0_0_0_S6)[0][11]=4;
    (_cpu_0_0_0_S6)[0][12]=14;
    (_cpu_0_0_0_S6)[0][13]=7;
    (_cpu_0_0_0_S6)[0][14]=5;
    (_cpu_0_0_0_S6)[0][15]=11;
    (_cpu_0_0_0_S6)[1][0]=10;
    (_cpu_0_0_0_S6)[1][1]=15;
    (_cpu_0_0_0_S6)[1][2]=4;
    (_cpu_0_0_0_S6)[1][3]=2;
    (_cpu_0_0_0_S6)[1][4]=7;
    (_cpu_0_0_0_S6)[1][5]=12;
    (_cpu_0_0_0_S6)[1][6]=9;
    (_cpu_0_0_0_S6)[1][7]=5;
    (_cpu_0_0_0_S6)[1][8]=6;
    (_cpu_0_0_0_S6)[1][9]=1;
    (_cpu_0_0_0_S6)[1][10]=13;
    (_cpu_0_0_0_S6)[1][11]=14;
    (_cpu_0_0_0_S6)[1][12]=0;
    (_cpu_0_0_0_S6)[1][13]=11;
    (_cpu_0_0_0_S6)[1][14]=3;
    (_cpu_0_0_0_S6)[1][15]=8;
    (_cpu_0_0_0_S6)[2][0]=9;
    (_cpu_0_0_0_S6)[2][1]=14;
    (_cpu_0_0_0_S6)[2][2]=15;
    (_cpu_0_0_0_S6)[2][3]=5;
    (_cpu_0_0_0_S6)[2][4]=2;
    (_cpu_0_0_0_S6)[2][5]=8;
    (_cpu_0_0_0_S6)[2][6]=12;
    (_cpu_0_0_0_S6)[2][7]=3;
    (_cpu_0_0_0_S6)[2][8]=7;
    (_cpu_0_0_0_S6)[2][9]=0;
    (_cpu_0_0_0_S6)[2][10]=4;
    (_cpu_0_0_0_S6)[2][11]=10;
    (_cpu_0_0_0_S6)[2][12]=1;
    (_cpu_0_0_0_S6)[2][13]=13;
    (_cpu_0_0_0_S6)[2][14]=11;
    (_cpu_0_0_0_S6)[2][15]=6;
    (_cpu_0_0_0_S6)[3][0]=4;
    (_cpu_0_0_0_S6)[3][1]=3;
    (_cpu_0_0_0_S6)[3][2]=2;
    (_cpu_0_0_0_S6)[3][3]=12;
    (_cpu_0_0_0_S6)[3][4]=9;
    (_cpu_0_0_0_S6)[3][5]=5;
    (_cpu_0_0_0_S6)[3][6]=15;
    (_cpu_0_0_0_S6)[3][7]=10;
    (_cpu_0_0_0_S6)[3][8]=11;
    (_cpu_0_0_0_S6)[3][9]=14;
    (_cpu_0_0_0_S6)[3][10]=1;
    (_cpu_0_0_0_S6)[3][11]=7;
    (_cpu_0_0_0_S6)[3][12]=6;
    (_cpu_0_0_0_S6)[3][13]=0;
    (_cpu_0_0_0_S6)[3][14]=8;
    (_cpu_0_0_0_S6)[3][15]=13;
    (_cpu_0_0_0_S7)=(int32_t**)malloc(sizeof(int32_t*)*4);
    for ((_cpu_0_0_0_i)=0; (_cpu_0_0_0_i)<4; (_cpu_0_0_0_i)++)
        {
        (_cpu_0_0_0_S7)[(_cpu_0_0_0_i)]=(int32_t*)malloc(sizeof(int32_t)*16);
    }
    (_cpu_0_0_0_S7)[0][0]=4;
    (_cpu_0_0_0_S7)[0][1]=11;
    (_cpu_0_0_0_S7)[0][2]=2;
    (_cpu_0_0_0_S7)[0][3]=14;
    (_cpu_0_0_0_S7)[0][4]=15;
    (_cpu_0_0_0_S7)[0][5]=0;
    (_cpu_0_0_0_S7)[0][6]=8;
    (_cpu_0_0_0_S7)[0][7]=13;
    (_cpu_0_0_0_S7)[0][8]=3;
    (_cpu_0_0_0_S7)[0][9]=12;
    (_cpu_0_0_0_S7)[0][10]=9;
    (_cpu_0_0_0_S7)[0][11]=7;
    (_cpu_0_0_0_S7)[0][12]=5;
    (_cpu_0_0_0_S7)[0][13]=10;
    (_cpu_0_0_0_S7)[0][14]=6;
    (_cpu_0_0_0_S7)[0][15]=1;
    (_cpu_0_0_0_S7)[1][0]=13;
    (_cpu_0_0_0_S7)[1][1]=0;
    (_cpu_0_0_0_S7)[1][2]=11;
    (_cpu_0_0_0_S7)[1][3]=7;
    (_cpu_0_0_0_S7)[1][4]=4;
    (_cpu_0_0_0_S7)[1][5]=9;
    (_cpu_0_0_0_S7)[1][6]=1;
    (_cpu_0_0_0_S7)[1][7]=10;
    (_cpu_0_0_0_S7)[1][8]=14;
    (_cpu_0_0_0_S7)[1][9]=3;
    (_cpu_0_0_0_S7)[1][10]=5;
    (_cpu_0_0_0_S7)[1][11]=12;
    (_cpu_0_0_0_S7)[1][12]=2;
    (_cpu_0_0_0_S7)[1][13]=15;
    (_cpu_0_0_0_S7)[1][14]=8;
    (_cpu_0_0_0_S7)[1][15]=6;
    (_cpu_0_0_0_S7)[2][0]=1;
    (_cpu_0_0_0_S7)[2][1]=4;
    (_cpu_0_0_0_S7)[2][2]=11;
    (_cpu_0_0_0_S7)[2][3]=13;
    (_cpu_0_0_0_S7)[2][4]=12;
    (_cpu_0_0_0_S7)[2][5]=3;
    (_cpu_0_0_0_S7)[2][6]=7;
    (_cpu_0_0_0_S7)[2][7]=14;
    (_cpu_0_0_0_S7)[2][8]=10;
    (_cpu_0_0_0_S7)[2][9]=15;
    (_cpu_0_0_0_S7)[2][10]=6;
    (_cpu_0_0_0_S7)[2][11]=8;
    (_cpu_0_0_0_S7)[2][12]=0;
    (_cpu_0_0_0_S7)[2][13]=5;
    (_cpu_0_0_0_S7)[2][14]=9;
    (_cpu_0_0_0_S7)[2][15]=2;
    (_cpu_0_0_0_S7)[3][0]=6;
    (_cpu_0_0_0_S7)[3][1]=11;
    (_cpu_0_0_0_S7)[3][2]=13;
    (_cpu_0_0_0_S7)[3][3]=8;
    (_cpu_0_0_0_S7)[3][4]=1;
    (_cpu_0_0_0_S7)[3][5]=4;
    (_cpu_0_0_0_S7)[3][6]=10;
    (_cpu_0_0_0_S7)[3][7]=7;
    (_cpu_0_0_0_S7)[3][8]=9;
    (_cpu_0_0_0_S7)[3][9]=5;
    (_cpu_0_0_0_S7)[3][10]=0;
    (_cpu_0_0_0_S7)[3][11]=15;
    (_cpu_0_0_0_S7)[3][12]=14;
    (_cpu_0_0_0_S7)[3][13]=2;
    (_cpu_0_0_0_S7)[3][14]=3;
    (_cpu_0_0_0_S7)[3][15]=12;
    (_cpu_0_0_0_S8)=(int32_t**)malloc(sizeof(int32_t*)*4);
    for ((_cpu_0_0_0_i)=0; (_cpu_0_0_0_i)<4; (_cpu_0_0_0_i)++)
        {
        (_cpu_0_0_0_S8)[(_cpu_0_0_0_i)]=(int32_t*)malloc(sizeof(int32_t)*16);
    }
    (_cpu_0_0_0_S8)[0][0]=13;
    (_cpu_0_0_0_S8)[0][1]=2;
    (_cpu_0_0_0_S8)[0][2]=8;
    (_cpu_0_0_0_S8)[0][3]=4;
    (_cpu_0_0_0_S8)[0][4]=6;
    (_cpu_0_0_0_S8)[0][5]=15;
    (_cpu_0_0_0_S8)[0][6]=11;
    (_cpu_0_0_0_S8)[0][7]=1;
    (_cpu_0_0_0_S8)[0][8]=10;
    (_cpu_0_0_0_S8)[0][9]=9;
    (_cpu_0_0_0_S8)[0][10]=3;
    (_cpu_0_0_0_S8)[0][11]=14;
    (_cpu_0_0_0_S8)[0][12]=5;
    (_cpu_0_0_0_S8)[0][13]=0;
    (_cpu_0_0_0_S8)[0][14]=12;
    (_cpu_0_0_0_S8)[0][15]=7;
    (_cpu_0_0_0_S8)[1][0]=1;
    (_cpu_0_0_0_S8)[1][1]=15;
    (_cpu_0_0_0_S8)[1][2]=13;
    (_cpu_0_0_0_S8)[1][3]=8;
    (_cpu_0_0_0_S8)[1][4]=10;
    (_cpu_0_0_0_S8)[1][5]=3;
    (_cpu_0_0_0_S8)[1][6]=7;
    (_cpu_0_0_0_S8)[1][7]=4;
    (_cpu_0_0_0_S8)[1][8]=12;
    (_cpu_0_0_0_S8)[1][9]=5;
    (_cpu_0_0_0_S8)[1][10]=6;
    (_cpu_0_0_0_S8)[1][11]=11;
    (_cpu_0_0_0_S8)[1][12]=0;
    (_cpu_0_0_0_S8)[1][13]=14;
    (_cpu_0_0_0_S8)[1][14]=9;
    (_cpu_0_0_0_S8)[1][15]=2;
    (_cpu_0_0_0_S8)[2][0]=7;
    (_cpu_0_0_0_S8)[2][1]=11;
    (_cpu_0_0_0_S8)[2][2]=4;
    (_cpu_0_0_0_S8)[2][3]=1;
    (_cpu_0_0_0_S8)[2][4]=9;
    (_cpu_0_0_0_S8)[2][5]=12;
    (_cpu_0_0_0_S8)[2][6]=14;
    (_cpu_0_0_0_S8)[2][7]=2;
    (_cpu_0_0_0_S8)[2][8]=0;
    (_cpu_0_0_0_S8)[2][9]=6;
    (_cpu_0_0_0_S8)[2][10]=10;
    (_cpu_0_0_0_S8)[2][11]=13;
    (_cpu_0_0_0_S8)[2][12]=15;
    (_cpu_0_0_0_S8)[2][13]=3;
    (_cpu_0_0_0_S8)[2][14]=5;
    (_cpu_0_0_0_S8)[2][15]=8;
    (_cpu_0_0_0_S8)[3][0]=2;
    (_cpu_0_0_0_S8)[3][1]=1;
    (_cpu_0_0_0_S8)[3][2]=14;
    (_cpu_0_0_0_S8)[3][3]=7;
    (_cpu_0_0_0_S8)[3][4]=4;
    (_cpu_0_0_0_S8)[3][5]=10;
    (_cpu_0_0_0_S8)[3][6]=8;
    (_cpu_0_0_0_S8)[3][7]=13;
    (_cpu_0_0_0_S8)[3][8]=15;
    (_cpu_0_0_0_S8)[3][9]=12;
    (_cpu_0_0_0_S8)[3][10]=9;
    (_cpu_0_0_0_S8)[3][11]=0;
    (_cpu_0_0_0_S8)[3][12]=3;
    (_cpu_0_0_0_S8)[3][13]=5;
    (_cpu_0_0_0_S8)[3][14]=6;
    (_cpu_0_0_0_S8)[3][15]=11;
    }
}

MULTI_CHANNEL_SYNC Xor_1_to_Sbox_3_receiver;
uint8_t* Sbox_3_to_doP_1_buffer_ptr_tail = 0;
MULTI_CHANNEL_SYNC Xor_1_to_Sbox_4_receiver;
uint8_t* Sbox_4_to_doP_1_buffer_ptr_tail = 0;
MULTI_CHANNEL_SYNC Xor_1_to_Sbox_5_receiver;
uint8_t* Sbox_5_to_doP_1_buffer_ptr_tail = 0;
MULTI_CHANNEL_SYNC Xor_1_to_Sbox_6_receiver;
uint8_t* Sbox_6_to_doP_1_buffer_ptr_tail = 0;
MULTI_CHANNEL_SYNC Xor_1_to_Sbox_8_receiver;
uint8_t* Sbox_8_to_doP_1_buffer_ptr_tail = 0;
MULTI_CHANNEL_SYNC Sbox_1_1_to_doP_1_receiver;
MULTI_CHANNEL_SYNC Sbox_1_2_to_doP_1_receiver;
MULTI_CHANNEL_SYNC Sbox_2_1_to_doP_1_receiver;
MULTI_CHANNEL_SYNC Sbox_2_2_to_doP_1_receiver;
MULTI_CHANNEL_SYNC Sbox_2_3_to_doP_1_receiver;
uint8_t* Sbox_3_to_doP_1_buffer_ptr_head = 0;
uint8_t Sbox_3_to_doP_1_buffer[96+0];
uint8_t* Sbox_4_to_doP_1_buffer_ptr_head = 0;
uint8_t Sbox_4_to_doP_1_buffer[96+0];
uint8_t* Sbox_5_to_doP_1_buffer_ptr_head = 0;
uint8_t Sbox_5_to_doP_1_buffer[96+0];
uint8_t* Sbox_6_to_doP_1_buffer_ptr_head = 0;
uint8_t Sbox_6_to_doP_1_buffer[96+0];
MULTI_CHANNEL_SYNC Sbox_7_to_doP_1_receiver;
uint8_t* Sbox_8_to_doP_1_buffer_ptr_head = 0;
uint8_t Sbox_8_to_doP_1_buffer[96+0];
MULTI_CHANNEL_SYNC doP_1_to_Xor_2_sender;
uint8_t merge_doP_1_buffer[384];
MULTI_CHANNEL_SYNC doIP_1_1_to_Identity_1_receiver;
MULTI_CHANNEL_SYNC doIP_1_2_to_Identity_1_receiver;
MULTI_CHANNEL_SYNC Identity_1_to_Xor_2_sender;
uint8_t merge_Identity_1_buffer[384];
MULTI_CHANNEL_SYNC Xor_2_to_doE_2_receiver;
uint8_t* doE_2_to_Xor_3_1_buffer_ptr_tail = 0;
uint8_t* doE_2_to_Xor_3_2_buffer_ptr_tail = 0;
MULTI_CHANNEL_SYNC doE_2_to_Xor_3_3_sender;
uint8_t split_doE_2_buffer[576];
uint8_t* doE_2_to_Xor_3_1_buffer_ptr_head = 0;
uint8_t doE_2_to_Xor_3_1_buffer[384+0];
MULTI_CHANNEL_SYNC anonFilter3_2_to_Xor_3_1_receiver;
uint8_t* Xor_3_1_to_Sbox_9_buffer_ptr_tail = 0;
MULTI_CHANNEL_SYNC Xor_3_1_to_Sbox_10_sender;
uint8_t* Xor_3_1_to_Sbox_11_buffer_ptr_tail = 0;
uint8_t* Xor_3_1_to_Sbox_12_1_buffer_ptr_tail = 0;
uint8_t* Xor_3_1_to_Sbox_13_buffer_ptr_tail = 0;
MULTI_CHANNEL_SYNC Xor_3_1_to_Sbox_14_sender;
MULTI_CHANNEL_SYNC Xor_3_1_to_Sbox_15_sender;
MULTI_CHANNEL_SYNC Xor_3_1_to_Sbox_16_sender;
uint8_t* Xor_3_1_to_Sbox_12_2_buffer_ptr_tail = 0;
MULTI_CHANNEL_SYNC Xor_3_1_to_Sbox_12_3_sender;
uint8_t* Xor_3_1_to_Sbox_12_4_buffer_ptr_tail = 0;
uint8_t merge_Xor_3_1_buffer[384];
uint8_t split_Xor_3_1_buffer[192];
uint8_t* doE_2_to_Xor_3_2_buffer_ptr_head = 0;
uint8_t doE_2_to_Xor_3_2_buffer[384+0];
MULTI_CHANNEL_SYNC anonFilter3_2_to_Xor_3_2_receiver;
uint8_t* Xor_3_2_to_Sbox_9_buffer_ptr_tail = 0;
MULTI_CHANNEL_SYNC Xor_3_2_to_Sbox_10_sender;
uint8_t* Xor_3_2_to_Sbox_11_buffer_ptr_tail = 0;
uint8_t* Xor_3_2_to_Sbox_12_1_buffer_ptr_tail = 0;
uint8_t* Xor_3_2_to_Sbox_13_buffer_ptr_tail = 0;
MULTI_CHANNEL_SYNC Xor_3_2_to_Sbox_14_sender;
MULTI_CHANNEL_SYNC Xor_3_2_to_Sbox_15_sender;
MULTI_CHANNEL_SYNC Xor_3_2_to_Sbox_16_sender;
uint8_t* Xor_3_2_to_Sbox_12_2_buffer_ptr_tail = 0;
MULTI_CHANNEL_SYNC Xor_3_2_to_Sbox_12_3_sender;
uint8_t* Xor_3_2_to_Sbox_12_4_buffer_ptr_tail = 0;
uint8_t merge_Xor_3_2_buffer[384];
uint8_t split_Xor_3_2_buffer[192];
uint8_t* Xor_3_1_to_Sbox_9_buffer_ptr_head = 0;
uint8_t Xor_3_1_to_Sbox_9_buffer[48+0];
uint8_t* Xor_3_2_to_Sbox_9_buffer_ptr_head = 0;
uint8_t Xor_3_2_to_Sbox_9_buffer[48+0];
MULTI_CHANNEL_SYNC Xor_3_3_to_Sbox_9_receiver;
MULTI_CHANNEL_SYNC Sbox_9_to_doP_2_sender;
uint8_t merge_Sbox_9_buffer[72];
uint8_t* Xor_3_1_to_Sbox_11_buffer_ptr_head = 0;
uint8_t Xor_3_1_to_Sbox_11_buffer[48+0];
uint8_t* Xor_3_2_to_Sbox_11_buffer_ptr_head = 0;
uint8_t Xor_3_2_to_Sbox_11_buffer[48+0];
MULTI_CHANNEL_SYNC Xor_3_3_to_Sbox_11_receiver;
MULTI_CHANNEL_SYNC Sbox_11_to_doP_2_sender;
uint8_t merge_Sbox_11_buffer[72];
uint8_t* Xor_3_1_to_Sbox_12_1_buffer_ptr_head = 0;
uint8_t Xor_3_1_to_Sbox_12_1_buffer[12+0];
uint8_t* Xor_3_2_to_Sbox_12_1_buffer_ptr_head = 0;
uint8_t Xor_3_2_to_Sbox_12_1_buffer[12+0];
MULTI_CHANNEL_SYNC Xor_3_3_to_Sbox_12_1_receiver;
MULTI_CHANNEL_SYNC Sbox_12_1_to_doP_2_sender;
uint8_t merge_Sbox_12_1_buffer[18];
uint8_t* Xor_3_1_to_Sbox_12_2_buffer_ptr_head = 0;
uint8_t Xor_3_1_to_Sbox_12_2_buffer[12+0];
uint8_t* Xor_3_2_to_Sbox_12_2_buffer_ptr_head = 0;
uint8_t Xor_3_2_to_Sbox_12_2_buffer[12+0];
MULTI_CHANNEL_SYNC Xor_3_3_to_Sbox_12_2_receiver;
MULTI_CHANNEL_SYNC Sbox_12_2_to_doP_2_sender;
uint8_t merge_Sbox_12_2_buffer[18];
uint8_t* Xor_3_1_to_Sbox_12_4_buffer_ptr_head = 0;
uint8_t Xor_3_1_to_Sbox_12_4_buffer[12+0];
uint8_t* Xor_3_2_to_Sbox_12_4_buffer_ptr_head = 0;
uint8_t Xor_3_2_to_Sbox_12_4_buffer[12+0];
MULTI_CHANNEL_SYNC Xor_3_3_to_Sbox_12_4_receiver;
MULTI_CHANNEL_SYNC Sbox_12_4_to_doP_2_sender;
uint8_t merge_Sbox_12_4_buffer[18];
uint8_t* Xor_3_1_to_Sbox_13_buffer_ptr_head = 0;
uint8_t Xor_3_1_to_Sbox_13_buffer[48+0];
uint8_t* Xor_3_2_to_Sbox_13_buffer_ptr_head = 0;
uint8_t Xor_3_2_to_Sbox_13_buffer[48+0];
MULTI_CHANNEL_SYNC Xor_3_3_to_Sbox_13_receiver;
MULTI_CHANNEL_SYNC Sbox_13_to_doP_2_sender;
uint8_t merge_Sbox_13_buffer[72];
MULTI_CHANNEL_SYNC Identity_2_to_Identity_3_receiver;
MULTI_CHANNEL_SYNC Identity_3_to_Xor_4_sender;
MULTI_CHANNEL_SYNC Xor_4_to_doE_3_1_receiver;
MULTI_CHANNEL_SYNC doE_3_1_to_Xor_5_sender;
MULTI_CHANNEL_SYNC Xor_5_to_Sbox_17_receiver;
MULTI_CHANNEL_SYNC Sbox_17_to_doP_3_sender;
MULTI_CHANNEL_SYNC Xor_5_to_Sbox_20_receiver;
MULTI_CHANNEL_SYNC Sbox_20_to_doP_3_sender;
MULTI_CHANNEL_SYNC Xor_5_to_Sbox_21_receiver;
MULTI_CHANNEL_SYNC Sbox_21_to_doP_3_sender;
MULTI_CHANNEL_SYNC Xor_5_to_Sbox_22_receiver;
MULTI_CHANNEL_SYNC Sbox_22_to_doP_3_sender;
MULTI_CHANNEL_SYNC Xor_4_to_Identity_6_receiver;
uint8_t* Identity_6_to_Identity_7_buffer_ptr_tail = 0;
uint8_t* Identity_6_to_anonFilter0_4_buffer_ptr_tail = 0;
uint8_t split_Identity_6_buffer[384];
MULTI_CHANNEL_SYNC Identity_4_to_anonFilter0_3_receiver;
MULTI_CHANNEL_SYNC Xor_7_to_Sbox_27_receiver;
MULTI_CHANNEL_SYNC Sbox_27_to_doP_4_sender;
MULTI_CHANNEL_SYNC Xor_7_to_Sbox_28_receiver;
MULTI_CHANNEL_SYNC Sbox_28_to_doP_4_sender;
MULTI_CHANNEL_SYNC Xor_7_to_Sbox_29_receiver;
MULTI_CHANNEL_SYNC Sbox_29_to_doP_4_sender;
MULTI_CHANNEL_SYNC Xor_7_to_Sbox_30_receiver;
MULTI_CHANNEL_SYNC Sbox_30_to_doP_4_sender;
MULTI_CHANNEL_SYNC Xor_7_to_Sbox_32_receiver;
MULTI_CHANNEL_SYNC Sbox_32_to_doP_4_sender;
uint8_t* Identity_6_to_Identity_7_buffer_ptr_head = 0;
uint8_t Identity_6_to_Identity_7_buffer[768+0];
MULTI_CHANNEL_SYNC Identity_7_to_Xor_8_sender;
uint8_t* Identity_6_to_anonFilter0_4_buffer_ptr_head = 0;
uint8_t Identity_6_to_anonFilter0_4_buffer[3072+0];
MULTI_CHANNEL_SYNC CrissCross_1_to_doIPm1_1_receiver;
MULTI_CHANNEL_SYNC doIPm1_1_to_BitstoInts_1_sender;

// WORK
static void doIPm1(uint8_t** restrict _receive_buffer, uint8_t** restrict _send_buffer)
{
    int32_t i_18;
    int32_t i_19;

    for ((i_18)=0; (i_18)<64; (i_18)++)
        {
        *(*_send_buffer)++ = ((*(*_receive_buffer+(_cpu_0_0_0_IPm1)[(i_18)]-1)));
    }
    for ((i_18)=0; (i_18)<64; (i_18)++)
        {
        (*(*_receive_buffer)++);
    }
}

static void Sbox(int32_t** restrict table, uint8_t** restrict _receive_buffer, uint8_t** restrict _buffer_ptr_head)
{
    int32_t r;
    int32_t c;
    int32_t out;

    (r)=(*(*_receive_buffer)++);
    (c)=(*(*_receive_buffer)++);
    (c)=((*(*_receive_buffer)++)<<1)|(c);
    (c)=((*(*_receive_buffer)++)<<2)|(c);
    (c)=((*(*_receive_buffer)++)<<3)|(c);
    (r)=((*(*_receive_buffer)++)<<1)|(r);
    (out)=(table)[(r)][(c)];
    *(*_buffer_ptr_head)++ = ((uint8_t)(((out)&0x1)>>0));
    *(*_buffer_ptr_head)++ = ((uint8_t)(((out)&0x2)>>1));
    *(*_buffer_ptr_head)++ = ((uint8_t)(((out)&0x4)>>2));
    *(*_buffer_ptr_head)++ = ((uint8_t)(((out)&0x8)>>3));
}

static void doP(uint8_t** restrict merge__buffer_ptr_tail, uint8_t** restrict _send_buffer)
{
    int32_t i_16;
    int32_t i_17;

    for ((i_16)=31; (i_16)>=0; (i_16)--)
        {
        *(*_send_buffer)++ = ((*(*merge__buffer_ptr_tail+32-(_cpu_0_0_0_P)[(i_16)])));
    }
    for ((i_16)=0; (i_16)<32; (i_16)++)
        {
        (*(*merge__buffer_ptr_tail)++);
    }
}

static void Identity(uint8_t** restrict merge__buffer_ptr_tail, uint8_t** restrict _send_buffer)
{
    *(*_send_buffer)++ = ((*(*merge__buffer_ptr_tail)++));
}

static void doE(uint8_t** restrict _receive_buffer, uint8_t** restrict split__buffer_ptr_head)
{
    int32_t i_14;
    int32_t i_15;

    for ((i_14)=0; (i_14)<48; (i_14)++)
        {
        *(*split__buffer_ptr_head)++ = ((*(*_receive_buffer+(_cpu_0_0_0_E)[(i_14)]-1)));
    }
    for ((i_14)=0; (i_14)<32; (i_14)++)
        {
        (*(*_receive_buffer)++);
    }
}

static void Xor(int32_t n, uint8_t** restrict merge__buffer_ptr_tail, uint8_t** restrict split__buffer_ptr_head)
{
    uint8_t x;
    int32_t i_21;

    (x)=(*(*merge__buffer_ptr_tail)++);
    for ((i_21)=1; (i_21)<(n); (i_21)++)
        {
        uint8_t y;

        (y)=(*(*merge__buffer_ptr_tail)++);
        (x)=(x)^(y);
    }
    *(*split__buffer_ptr_head)++ = ((x));
}

static void anonFilter0(uint8_t** restrict _receive_buffer)
{
    (*(*_receive_buffer)++);
}

// PREWORK + INIT
static void Sbox_3_init()
{
    int32_t i_02;

    (Sbox_3_table)=(int**)malloc(sizeof(int*)*4);

    for ((i_02)=0; (i_02)<4; (i_02)++)
        {
        (Sbox_3_table)[(i_02)]=(int*)malloc(sizeof(int)*16);
    }

    Sbox_3_table[0][0] = 12; Sbox_3_table[0][1] = 1; Sbox_3_table[0][2] = 10; Sbox_3_table[0][3] = 15; Sbox_3_table[0][4] = 9; Sbox_3_table[0][5] = 2; Sbox_3_table[0][6] = 6; Sbox_3_table[0][7] = 8; Sbox_3_table[0][8] = 0; Sbox_3_table[0][9] = 13; Sbox_3_table[0][10] = 3; Sbox_3_table[0][11] = 4; Sbox_3_table[0][12] = 14; Sbox_3_table[0][13] = 7; Sbox_3_table[0][14] = 5; Sbox_3_table[0][15] = 11; 
Sbox_3_table[1][0] = 10; Sbox_3_table[1][1] = 15; Sbox_3_table[1][2] = 4; Sbox_3_table[1][3] = 2; Sbox_3_table[1][4] = 7; Sbox_3_table[1][5] = 12; Sbox_3_table[1][6] = 9; Sbox_3_table[1][7] = 5; Sbox_3_table[1][8] = 6; Sbox_3_table[1][9] = 1; Sbox_3_table[1][10] = 13; Sbox_3_table[1][11] = 14; Sbox_3_table[1][12] = 0; Sbox_3_table[1][13] = 11; Sbox_3_table[1][14] = 3; Sbox_3_table[1][15] = 8; 
Sbox_3_table[2][0] = 9; Sbox_3_table[2][1] = 14; Sbox_3_table[2][2] = 15; Sbox_3_table[2][3] = 5; Sbox_3_table[2][4] = 2; Sbox_3_table[2][5] = 8; Sbox_3_table[2][6] = 12; Sbox_3_table[2][7] = 3; Sbox_3_table[2][8] = 7; Sbox_3_table[2][9] = 0; Sbox_3_table[2][10] = 4; Sbox_3_table[2][11] = 10; Sbox_3_table[2][12] = 1; Sbox_3_table[2][13] = 13; Sbox_3_table[2][14] = 11; Sbox_3_table[2][15] = 6; 
Sbox_3_table[3][0] = 4; Sbox_3_table[3][1] = 3; Sbox_3_table[3][2] = 2; Sbox_3_table[3][3] = 12; Sbox_3_table[3][4] = 9; Sbox_3_table[3][5] = 5; Sbox_3_table[3][6] = 15; Sbox_3_table[3][7] = 10; Sbox_3_table[3][8] = 11; Sbox_3_table[3][9] = 14; Sbox_3_table[3][10] = 1; Sbox_3_table[3][11] = 7; Sbox_3_table[3][12] = 6; Sbox_3_table[3][13] = 0; Sbox_3_table[3][14] = 8; Sbox_3_table[3][15] = 13; 

}

static void Sbox_4_init()
{
    int32_t i_03;

    (Sbox_4_table)=(int**)malloc(sizeof(int*)*4);

    for ((i_03)=0; (i_03)<4; (i_03)++)
        {
        (Sbox_4_table)[(i_03)]=(int*)malloc(sizeof(int)*16);
    }

    Sbox_4_table[0][0] = 2; Sbox_4_table[0][1] = 12; Sbox_4_table[0][2] = 4; Sbox_4_table[0][3] = 1; Sbox_4_table[0][4] = 7; Sbox_4_table[0][5] = 10; Sbox_4_table[0][6] = 11; Sbox_4_table[0][7] = 6; Sbox_4_table[0][8] = 8; Sbox_4_table[0][9] = 5; Sbox_4_table[0][10] = 3; Sbox_4_table[0][11] = 15; Sbox_4_table[0][12] = 13; Sbox_4_table[0][13] = 0; Sbox_4_table[0][14] = 14; Sbox_4_table[0][15] = 9; 
Sbox_4_table[1][0] = 14; Sbox_4_table[1][1] = 11; Sbox_4_table[1][2] = 2; Sbox_4_table[1][3] = 12; Sbox_4_table[1][4] = 4; Sbox_4_table[1][5] = 7; Sbox_4_table[1][6] = 13; Sbox_4_table[1][7] = 1; Sbox_4_table[1][8] = 5; Sbox_4_table[1][9] = 0; Sbox_4_table[1][10] = 15; Sbox_4_table[1][11] = 10; Sbox_4_table[1][12] = 3; Sbox_4_table[1][13] = 9; Sbox_4_table[1][14] = 8; Sbox_4_table[1][15] = 6; 
Sbox_4_table[2][0] = 4; Sbox_4_table[2][1] = 2; Sbox_4_table[2][2] = 1; Sbox_4_table[2][3] = 11; Sbox_4_table[2][4] = 10; Sbox_4_table[2][5] = 13; Sbox_4_table[2][6] = 7; Sbox_4_table[2][7] = 8; Sbox_4_table[2][8] = 15; Sbox_4_table[2][9] = 9; Sbox_4_table[2][10] = 12; Sbox_4_table[2][11] = 5; Sbox_4_table[2][12] = 6; Sbox_4_table[2][13] = 3; Sbox_4_table[2][14] = 0; Sbox_4_table[2][15] = 14; 
Sbox_4_table[3][0] = 11; Sbox_4_table[3][1] = 8; Sbox_4_table[3][2] = 12; Sbox_4_table[3][3] = 7; Sbox_4_table[3][4] = 1; Sbox_4_table[3][5] = 14; Sbox_4_table[3][6] = 2; Sbox_4_table[3][7] = 13; Sbox_4_table[3][8] = 6; Sbox_4_table[3][9] = 15; Sbox_4_table[3][10] = 0; Sbox_4_table[3][11] = 9; Sbox_4_table[3][12] = 10; Sbox_4_table[3][13] = 4; Sbox_4_table[3][14] = 5; Sbox_4_table[3][15] = 3; 

}

static void Sbox_5_init()
{
    int32_t i_04;

    (Sbox_5_table)=(int**)malloc(sizeof(int*)*4);

    for ((i_04)=0; (i_04)<4; (i_04)++)
        {
        (Sbox_5_table)[(i_04)]=(int*)malloc(sizeof(int)*16);
    }

    Sbox_5_table[0][0] = 7; Sbox_5_table[0][1] = 13; Sbox_5_table[0][2] = 14; Sbox_5_table[0][3] = 3; Sbox_5_table[0][4] = 0; Sbox_5_table[0][5] = 6; Sbox_5_table[0][6] = 9; Sbox_5_table[0][7] = 10; Sbox_5_table[0][8] = 1; Sbox_5_table[0][9] = 2; Sbox_5_table[0][10] = 8; Sbox_5_table[0][11] = 5; Sbox_5_table[0][12] = 11; Sbox_5_table[0][13] = 12; Sbox_5_table[0][14] = 4; Sbox_5_table[0][15] = 15; 
Sbox_5_table[1][0] = 13; Sbox_5_table[1][1] = 8; Sbox_5_table[1][2] = 11; Sbox_5_table[1][3] = 5; Sbox_5_table[1][4] = 6; Sbox_5_table[1][5] = 15; Sbox_5_table[1][6] = 0; Sbox_5_table[1][7] = 3; Sbox_5_table[1][8] = 4; Sbox_5_table[1][9] = 7; Sbox_5_table[1][10] = 2; Sbox_5_table[1][11] = 12; Sbox_5_table[1][12] = 1; Sbox_5_table[1][13] = 10; Sbox_5_table[1][14] = 14; Sbox_5_table[1][15] = 9; 
Sbox_5_table[2][0] = 10; Sbox_5_table[2][1] = 6; Sbox_5_table[2][2] = 9; Sbox_5_table[2][3] = 0; Sbox_5_table[2][4] = 12; Sbox_5_table[2][5] = 11; Sbox_5_table[2][6] = 7; Sbox_5_table[2][7] = 13; Sbox_5_table[2][8] = 15; Sbox_5_table[2][9] = 1; Sbox_5_table[2][10] = 3; Sbox_5_table[2][11] = 14; Sbox_5_table[2][12] = 5; Sbox_5_table[2][13] = 2; Sbox_5_table[2][14] = 8; Sbox_5_table[2][15] = 4; 
Sbox_5_table[3][0] = 3; Sbox_5_table[3][1] = 15; Sbox_5_table[3][2] = 0; Sbox_5_table[3][3] = 6; Sbox_5_table[3][4] = 10; Sbox_5_table[3][5] = 1; Sbox_5_table[3][6] = 13; Sbox_5_table[3][7] = 8; Sbox_5_table[3][8] = 9; Sbox_5_table[3][9] = 4; Sbox_5_table[3][10] = 5; Sbox_5_table[3][11] = 11; Sbox_5_table[3][12] = 12; Sbox_5_table[3][13] = 7; Sbox_5_table[3][14] = 2; Sbox_5_table[3][15] = 14; 

}

static void Sbox_6_init()
{
    int32_t i_05;

    (Sbox_6_table)=(int**)malloc(sizeof(int*)*4);

    for ((i_05)=0; (i_05)<4; (i_05)++)
        {
        (Sbox_6_table)[(i_05)]=(int*)malloc(sizeof(int)*16);
    }

    Sbox_6_table[0][0] = 10; Sbox_6_table[0][1] = 0; Sbox_6_table[0][2] = 9; Sbox_6_table[0][3] = 14; Sbox_6_table[0][4] = 6; Sbox_6_table[0][5] = 3; Sbox_6_table[0][6] = 15; Sbox_6_table[0][7] = 5; Sbox_6_table[0][8] = 1; Sbox_6_table[0][9] = 13; Sbox_6_table[0][10] = 12; Sbox_6_table[0][11] = 7; Sbox_6_table[0][12] = 11; Sbox_6_table[0][13] = 4; Sbox_6_table[0][14] = 2; Sbox_6_table[0][15] = 8; 
Sbox_6_table[1][0] = 13; Sbox_6_table[1][1] = 7; Sbox_6_table[1][2] = 0; Sbox_6_table[1][3] = 9; Sbox_6_table[1][4] = 3; Sbox_6_table[1][5] = 4; Sbox_6_table[1][6] = 6; Sbox_6_table[1][7] = 10; Sbox_6_table[1][8] = 2; Sbox_6_table[1][9] = 8; Sbox_6_table[1][10] = 5; Sbox_6_table[1][11] = 14; Sbox_6_table[1][12] = 12; Sbox_6_table[1][13] = 11; Sbox_6_table[1][14] = 15; Sbox_6_table[1][15] = 1; 
Sbox_6_table[2][0] = 13; Sbox_6_table[2][1] = 6; Sbox_6_table[2][2] = 4; Sbox_6_table[2][3] = 9; Sbox_6_table[2][4] = 8; Sbox_6_table[2][5] = 15; Sbox_6_table[2][6] = 3; Sbox_6_table[2][7] = 0; Sbox_6_table[2][8] = 11; Sbox_6_table[2][9] = 1; Sbox_6_table[2][10] = 2; Sbox_6_table[2][11] = 12; Sbox_6_table[2][12] = 5; Sbox_6_table[2][13] = 10; Sbox_6_table[2][14] = 14; Sbox_6_table[2][15] = 7; 
Sbox_6_table[3][0] = 1; Sbox_6_table[3][1] = 10; Sbox_6_table[3][2] = 13; Sbox_6_table[3][3] = 0; Sbox_6_table[3][4] = 6; Sbox_6_table[3][5] = 9; Sbox_6_table[3][6] = 8; Sbox_6_table[3][7] = 7; Sbox_6_table[3][8] = 4; Sbox_6_table[3][9] = 15; Sbox_6_table[3][10] = 14; Sbox_6_table[3][11] = 3; Sbox_6_table[3][12] = 11; Sbox_6_table[3][13] = 5; Sbox_6_table[3][14] = 2; Sbox_6_table[3][15] = 12; 

}

static void Sbox_8_init()
{
    int32_t i_06;

    (Sbox_8_table)=(int**)malloc(sizeof(int*)*4);

    for ((i_06)=0; (i_06)<4; (i_06)++)
        {
        (Sbox_8_table)[(i_06)]=(int*)malloc(sizeof(int)*16);
    }

    Sbox_8_table[0][0] = 14; Sbox_8_table[0][1] = 4; Sbox_8_table[0][2] = 13; Sbox_8_table[0][3] = 1; Sbox_8_table[0][4] = 2; Sbox_8_table[0][5] = 15; Sbox_8_table[0][6] = 11; Sbox_8_table[0][7] = 8; Sbox_8_table[0][8] = 3; Sbox_8_table[0][9] = 10; Sbox_8_table[0][10] = 6; Sbox_8_table[0][11] = 12; Sbox_8_table[0][12] = 5; Sbox_8_table[0][13] = 9; Sbox_8_table[0][14] = 0; Sbox_8_table[0][15] = 7; 
Sbox_8_table[1][0] = 0; Sbox_8_table[1][1] = 15; Sbox_8_table[1][2] = 7; Sbox_8_table[1][3] = 4; Sbox_8_table[1][4] = 14; Sbox_8_table[1][5] = 2; Sbox_8_table[1][6] = 13; Sbox_8_table[1][7] = 1; Sbox_8_table[1][8] = 10; Sbox_8_table[1][9] = 6; Sbox_8_table[1][10] = 12; Sbox_8_table[1][11] = 11; Sbox_8_table[1][12] = 9; Sbox_8_table[1][13] = 5; Sbox_8_table[1][14] = 3; Sbox_8_table[1][15] = 8; 
Sbox_8_table[2][0] = 4; Sbox_8_table[2][1] = 1; Sbox_8_table[2][2] = 14; Sbox_8_table[2][3] = 8; Sbox_8_table[2][4] = 13; Sbox_8_table[2][5] = 6; Sbox_8_table[2][6] = 2; Sbox_8_table[2][7] = 11; Sbox_8_table[2][8] = 15; Sbox_8_table[2][9] = 12; Sbox_8_table[2][10] = 9; Sbox_8_table[2][11] = 7; Sbox_8_table[2][12] = 3; Sbox_8_table[2][13] = 10; Sbox_8_table[2][14] = 5; Sbox_8_table[2][15] = 0; 
Sbox_8_table[3][0] = 15; Sbox_8_table[3][1] = 12; Sbox_8_table[3][2] = 8; Sbox_8_table[3][3] = 2; Sbox_8_table[3][4] = 4; Sbox_8_table[3][5] = 9; Sbox_8_table[3][6] = 1; Sbox_8_table[3][7] = 7; Sbox_8_table[3][8] = 5; Sbox_8_table[3][9] = 11; Sbox_8_table[3][10] = 3; Sbox_8_table[3][11] = 14; Sbox_8_table[3][12] = 10; Sbox_8_table[3][13] = 0; Sbox_8_table[3][14] = 6; Sbox_8_table[3][15] = 13; 

}

static void doP_1_init()
{
}

static void Identity_1_init()
{
}

static void doE_2_init()
{
    uint8_t* split_doE_2_buffer_ptr_tail=split_doE_2_buffer;
}

static void Xor_3_1_init()
{
    Xor_3_1_n = 2;
    uint8_t* split_Xor_3_1_buffer_ptr_tail=split_Xor_3_1_buffer;
}

static void Xor_3_2_init()
{
    Xor_3_2_n = 2;
    uint8_t* split_Xor_3_2_buffer_ptr_tail=split_Xor_3_2_buffer;
}

static void Sbox_9_init()
{
    int32_t i_07;

    (Sbox_9_table)=(int**)malloc(sizeof(int*)*4);

    for ((i_07)=0; (i_07)<4; (i_07)++)
        {
        (Sbox_9_table)[(i_07)]=(int*)malloc(sizeof(int)*16);
    }

    Sbox_9_table[0][0] = 13; Sbox_9_table[0][1] = 2; Sbox_9_table[0][2] = 8; Sbox_9_table[0][3] = 4; Sbox_9_table[0][4] = 6; Sbox_9_table[0][5] = 15; Sbox_9_table[0][6] = 11; Sbox_9_table[0][7] = 1; Sbox_9_table[0][8] = 10; Sbox_9_table[0][9] = 9; Sbox_9_table[0][10] = 3; Sbox_9_table[0][11] = 14; Sbox_9_table[0][12] = 5; Sbox_9_table[0][13] = 0; Sbox_9_table[0][14] = 12; Sbox_9_table[0][15] = 7; 
Sbox_9_table[1][0] = 1; Sbox_9_table[1][1] = 15; Sbox_9_table[1][2] = 13; Sbox_9_table[1][3] = 8; Sbox_9_table[1][4] = 10; Sbox_9_table[1][5] = 3; Sbox_9_table[1][6] = 7; Sbox_9_table[1][7] = 4; Sbox_9_table[1][8] = 12; Sbox_9_table[1][9] = 5; Sbox_9_table[1][10] = 6; Sbox_9_table[1][11] = 11; Sbox_9_table[1][12] = 0; Sbox_9_table[1][13] = 14; Sbox_9_table[1][14] = 9; Sbox_9_table[1][15] = 2; 
Sbox_9_table[2][0] = 7; Sbox_9_table[2][1] = 11; Sbox_9_table[2][2] = 4; Sbox_9_table[2][3] = 1; Sbox_9_table[2][4] = 9; Sbox_9_table[2][5] = 12; Sbox_9_table[2][6] = 14; Sbox_9_table[2][7] = 2; Sbox_9_table[2][8] = 0; Sbox_9_table[2][9] = 6; Sbox_9_table[2][10] = 10; Sbox_9_table[2][11] = 13; Sbox_9_table[2][12] = 15; Sbox_9_table[2][13] = 3; Sbox_9_table[2][14] = 5; Sbox_9_table[2][15] = 8; 
Sbox_9_table[3][0] = 2; Sbox_9_table[3][1] = 1; Sbox_9_table[3][2] = 14; Sbox_9_table[3][3] = 7; Sbox_9_table[3][4] = 4; Sbox_9_table[3][5] = 10; Sbox_9_table[3][6] = 8; Sbox_9_table[3][7] = 13; Sbox_9_table[3][8] = 15; Sbox_9_table[3][9] = 12; Sbox_9_table[3][10] = 9; Sbox_9_table[3][11] = 0; Sbox_9_table[3][12] = 3; Sbox_9_table[3][13] = 5; Sbox_9_table[3][14] = 6; Sbox_9_table[3][15] = 11; 

}

static void Sbox_11_init()
{
    int32_t i_08;

    (Sbox_11_table)=(int**)malloc(sizeof(int*)*4);

    for ((i_08)=0; (i_08)<4; (i_08)++)
        {
        (Sbox_11_table)[(i_08)]=(int*)malloc(sizeof(int)*16);
    }

    Sbox_11_table[0][0] = 12; Sbox_11_table[0][1] = 1; Sbox_11_table[0][2] = 10; Sbox_11_table[0][3] = 15; Sbox_11_table[0][4] = 9; Sbox_11_table[0][5] = 2; Sbox_11_table[0][6] = 6; Sbox_11_table[0][7] = 8; Sbox_11_table[0][8] = 0; Sbox_11_table[0][9] = 13; Sbox_11_table[0][10] = 3; Sbox_11_table[0][11] = 4; Sbox_11_table[0][12] = 14; Sbox_11_table[0][13] = 7; Sbox_11_table[0][14] = 5; Sbox_11_table[0][15] = 11; 
Sbox_11_table[1][0] = 10; Sbox_11_table[1][1] = 15; Sbox_11_table[1][2] = 4; Sbox_11_table[1][3] = 2; Sbox_11_table[1][4] = 7; Sbox_11_table[1][5] = 12; Sbox_11_table[1][6] = 9; Sbox_11_table[1][7] = 5; Sbox_11_table[1][8] = 6; Sbox_11_table[1][9] = 1; Sbox_11_table[1][10] = 13; Sbox_11_table[1][11] = 14; Sbox_11_table[1][12] = 0; Sbox_11_table[1][13] = 11; Sbox_11_table[1][14] = 3; Sbox_11_table[1][15] = 8; 
Sbox_11_table[2][0] = 9; Sbox_11_table[2][1] = 14; Sbox_11_table[2][2] = 15; Sbox_11_table[2][3] = 5; Sbox_11_table[2][4] = 2; Sbox_11_table[2][5] = 8; Sbox_11_table[2][6] = 12; Sbox_11_table[2][7] = 3; Sbox_11_table[2][8] = 7; Sbox_11_table[2][9] = 0; Sbox_11_table[2][10] = 4; Sbox_11_table[2][11] = 10; Sbox_11_table[2][12] = 1; Sbox_11_table[2][13] = 13; Sbox_11_table[2][14] = 11; Sbox_11_table[2][15] = 6; 
Sbox_11_table[3][0] = 4; Sbox_11_table[3][1] = 3; Sbox_11_table[3][2] = 2; Sbox_11_table[3][3] = 12; Sbox_11_table[3][4] = 9; Sbox_11_table[3][5] = 5; Sbox_11_table[3][6] = 15; Sbox_11_table[3][7] = 10; Sbox_11_table[3][8] = 11; Sbox_11_table[3][9] = 14; Sbox_11_table[3][10] = 1; Sbox_11_table[3][11] = 7; Sbox_11_table[3][12] = 6; Sbox_11_table[3][13] = 0; Sbox_11_table[3][14] = 8; Sbox_11_table[3][15] = 13; 

}

static void Sbox_12_1_init()
{
    int32_t i_09;

    (Sbox_12_1_table)=(int**)malloc(sizeof(int*)*4);

    for ((i_09)=0; (i_09)<4; (i_09)++)
        {
        (Sbox_12_1_table)[(i_09)]=(int*)malloc(sizeof(int)*16);
    }

    Sbox_12_1_table[0][0] = 2; Sbox_12_1_table[0][1] = 12; Sbox_12_1_table[0][2] = 4; Sbox_12_1_table[0][3] = 1; Sbox_12_1_table[0][4] = 7; Sbox_12_1_table[0][5] = 10; Sbox_12_1_table[0][6] = 11; Sbox_12_1_table[0][7] = 6; Sbox_12_1_table[0][8] = 8; Sbox_12_1_table[0][9] = 5; Sbox_12_1_table[0][10] = 3; Sbox_12_1_table[0][11] = 15; Sbox_12_1_table[0][12] = 13; Sbox_12_1_table[0][13] = 0; Sbox_12_1_table[0][14] = 14; Sbox_12_1_table[0][15] = 9; 
Sbox_12_1_table[1][0] = 14; Sbox_12_1_table[1][1] = 11; Sbox_12_1_table[1][2] = 2; Sbox_12_1_table[1][3] = 12; Sbox_12_1_table[1][4] = 4; Sbox_12_1_table[1][5] = 7; Sbox_12_1_table[1][6] = 13; Sbox_12_1_table[1][7] = 1; Sbox_12_1_table[1][8] = 5; Sbox_12_1_table[1][9] = 0; Sbox_12_1_table[1][10] = 15; Sbox_12_1_table[1][11] = 10; Sbox_12_1_table[1][12] = 3; Sbox_12_1_table[1][13] = 9; Sbox_12_1_table[1][14] = 8; Sbox_12_1_table[1][15] = 6; 
Sbox_12_1_table[2][0] = 4; Sbox_12_1_table[2][1] = 2; Sbox_12_1_table[2][2] = 1; Sbox_12_1_table[2][3] = 11; Sbox_12_1_table[2][4] = 10; Sbox_12_1_table[2][5] = 13; Sbox_12_1_table[2][6] = 7; Sbox_12_1_table[2][7] = 8; Sbox_12_1_table[2][8] = 15; Sbox_12_1_table[2][9] = 9; Sbox_12_1_table[2][10] = 12; Sbox_12_1_table[2][11] = 5; Sbox_12_1_table[2][12] = 6; Sbox_12_1_table[2][13] = 3; Sbox_12_1_table[2][14] = 0; Sbox_12_1_table[2][15] = 14; 
Sbox_12_1_table[3][0] = 11; Sbox_12_1_table[3][1] = 8; Sbox_12_1_table[3][2] = 12; Sbox_12_1_table[3][3] = 7; Sbox_12_1_table[3][4] = 1; Sbox_12_1_table[3][5] = 14; Sbox_12_1_table[3][6] = 2; Sbox_12_1_table[3][7] = 13; Sbox_12_1_table[3][8] = 6; Sbox_12_1_table[3][9] = 15; Sbox_12_1_table[3][10] = 0; Sbox_12_1_table[3][11] = 9; Sbox_12_1_table[3][12] = 10; Sbox_12_1_table[3][13] = 4; Sbox_12_1_table[3][14] = 5; Sbox_12_1_table[3][15] = 3; 

}

static void Sbox_12_2_init()
{
    int32_t i_10;

    (Sbox_12_2_table)=(int**)malloc(sizeof(int*)*4);

    for ((i_10)=0; (i_10)<4; (i_10)++)
        {
        (Sbox_12_2_table)[(i_10)]=(int*)malloc(sizeof(int)*16);
    }

    Sbox_12_2_table[0][0] = 2; Sbox_12_2_table[0][1] = 12; Sbox_12_2_table[0][2] = 4; Sbox_12_2_table[0][3] = 1; Sbox_12_2_table[0][4] = 7; Sbox_12_2_table[0][5] = 10; Sbox_12_2_table[0][6] = 11; Sbox_12_2_table[0][7] = 6; Sbox_12_2_table[0][8] = 8; Sbox_12_2_table[0][9] = 5; Sbox_12_2_table[0][10] = 3; Sbox_12_2_table[0][11] = 15; Sbox_12_2_table[0][12] = 13; Sbox_12_2_table[0][13] = 0; Sbox_12_2_table[0][14] = 14; Sbox_12_2_table[0][15] = 9; 
Sbox_12_2_table[1][0] = 14; Sbox_12_2_table[1][1] = 11; Sbox_12_2_table[1][2] = 2; Sbox_12_2_table[1][3] = 12; Sbox_12_2_table[1][4] = 4; Sbox_12_2_table[1][5] = 7; Sbox_12_2_table[1][6] = 13; Sbox_12_2_table[1][7] = 1; Sbox_12_2_table[1][8] = 5; Sbox_12_2_table[1][9] = 0; Sbox_12_2_table[1][10] = 15; Sbox_12_2_table[1][11] = 10; Sbox_12_2_table[1][12] = 3; Sbox_12_2_table[1][13] = 9; Sbox_12_2_table[1][14] = 8; Sbox_12_2_table[1][15] = 6; 
Sbox_12_2_table[2][0] = 4; Sbox_12_2_table[2][1] = 2; Sbox_12_2_table[2][2] = 1; Sbox_12_2_table[2][3] = 11; Sbox_12_2_table[2][4] = 10; Sbox_12_2_table[2][5] = 13; Sbox_12_2_table[2][6] = 7; Sbox_12_2_table[2][7] = 8; Sbox_12_2_table[2][8] = 15; Sbox_12_2_table[2][9] = 9; Sbox_12_2_table[2][10] = 12; Sbox_12_2_table[2][11] = 5; Sbox_12_2_table[2][12] = 6; Sbox_12_2_table[2][13] = 3; Sbox_12_2_table[2][14] = 0; Sbox_12_2_table[2][15] = 14; 
Sbox_12_2_table[3][0] = 11; Sbox_12_2_table[3][1] = 8; Sbox_12_2_table[3][2] = 12; Sbox_12_2_table[3][3] = 7; Sbox_12_2_table[3][4] = 1; Sbox_12_2_table[3][5] = 14; Sbox_12_2_table[3][6] = 2; Sbox_12_2_table[3][7] = 13; Sbox_12_2_table[3][8] = 6; Sbox_12_2_table[3][9] = 15; Sbox_12_2_table[3][10] = 0; Sbox_12_2_table[3][11] = 9; Sbox_12_2_table[3][12] = 10; Sbox_12_2_table[3][13] = 4; Sbox_12_2_table[3][14] = 5; Sbox_12_2_table[3][15] = 3; 

}

static void Sbox_12_4_init()
{
    int32_t i_51;

    (Sbox_12_4_table)=(int**)malloc(sizeof(int*)*4);

    for ((i_51)=0; (i_51)<4; (i_51)++)
        {
        (Sbox_12_4_table)[(i_51)]=(int*)malloc(sizeof(int)*16);
    }

    Sbox_12_4_table[0][0] = 2; Sbox_12_4_table[0][1] = 12; Sbox_12_4_table[0][2] = 4; Sbox_12_4_table[0][3] = 1; Sbox_12_4_table[0][4] = 7; Sbox_12_4_table[0][5] = 10; Sbox_12_4_table[0][6] = 11; Sbox_12_4_table[0][7] = 6; Sbox_12_4_table[0][8] = 8; Sbox_12_4_table[0][9] = 5; Sbox_12_4_table[0][10] = 3; Sbox_12_4_table[0][11] = 15; Sbox_12_4_table[0][12] = 13; Sbox_12_4_table[0][13] = 0; Sbox_12_4_table[0][14] = 14; Sbox_12_4_table[0][15] = 9; 
Sbox_12_4_table[1][0] = 14; Sbox_12_4_table[1][1] = 11; Sbox_12_4_table[1][2] = 2; Sbox_12_4_table[1][3] = 12; Sbox_12_4_table[1][4] = 4; Sbox_12_4_table[1][5] = 7; Sbox_12_4_table[1][6] = 13; Sbox_12_4_table[1][7] = 1; Sbox_12_4_table[1][8] = 5; Sbox_12_4_table[1][9] = 0; Sbox_12_4_table[1][10] = 15; Sbox_12_4_table[1][11] = 10; Sbox_12_4_table[1][12] = 3; Sbox_12_4_table[1][13] = 9; Sbox_12_4_table[1][14] = 8; Sbox_12_4_table[1][15] = 6; 
Sbox_12_4_table[2][0] = 4; Sbox_12_4_table[2][1] = 2; Sbox_12_4_table[2][2] = 1; Sbox_12_4_table[2][3] = 11; Sbox_12_4_table[2][4] = 10; Sbox_12_4_table[2][5] = 13; Sbox_12_4_table[2][6] = 7; Sbox_12_4_table[2][7] = 8; Sbox_12_4_table[2][8] = 15; Sbox_12_4_table[2][9] = 9; Sbox_12_4_table[2][10] = 12; Sbox_12_4_table[2][11] = 5; Sbox_12_4_table[2][12] = 6; Sbox_12_4_table[2][13] = 3; Sbox_12_4_table[2][14] = 0; Sbox_12_4_table[2][15] = 14; 
Sbox_12_4_table[3][0] = 11; Sbox_12_4_table[3][1] = 8; Sbox_12_4_table[3][2] = 12; Sbox_12_4_table[3][3] = 7; Sbox_12_4_table[3][4] = 1; Sbox_12_4_table[3][5] = 14; Sbox_12_4_table[3][6] = 2; Sbox_12_4_table[3][7] = 13; Sbox_12_4_table[3][8] = 6; Sbox_12_4_table[3][9] = 15; Sbox_12_4_table[3][10] = 0; Sbox_12_4_table[3][11] = 9; Sbox_12_4_table[3][12] = 10; Sbox_12_4_table[3][13] = 4; Sbox_12_4_table[3][14] = 5; Sbox_12_4_table[3][15] = 3; 

}

static void Sbox_13_init()
{
    int32_t i_52;

    (Sbox_13_table)=(int**)malloc(sizeof(int*)*4);

    for ((i_52)=0; (i_52)<4; (i_52)++)
        {
        (Sbox_13_table)[(i_52)]=(int*)malloc(sizeof(int)*16);
    }

    Sbox_13_table[0][0] = 7; Sbox_13_table[0][1] = 13; Sbox_13_table[0][2] = 14; Sbox_13_table[0][3] = 3; Sbox_13_table[0][4] = 0; Sbox_13_table[0][5] = 6; Sbox_13_table[0][6] = 9; Sbox_13_table[0][7] = 10; Sbox_13_table[0][8] = 1; Sbox_13_table[0][9] = 2; Sbox_13_table[0][10] = 8; Sbox_13_table[0][11] = 5; Sbox_13_table[0][12] = 11; Sbox_13_table[0][13] = 12; Sbox_13_table[0][14] = 4; Sbox_13_table[0][15] = 15; 
Sbox_13_table[1][0] = 13; Sbox_13_table[1][1] = 8; Sbox_13_table[1][2] = 11; Sbox_13_table[1][3] = 5; Sbox_13_table[1][4] = 6; Sbox_13_table[1][5] = 15; Sbox_13_table[1][6] = 0; Sbox_13_table[1][7] = 3; Sbox_13_table[1][8] = 4; Sbox_13_table[1][9] = 7; Sbox_13_table[1][10] = 2; Sbox_13_table[1][11] = 12; Sbox_13_table[1][12] = 1; Sbox_13_table[1][13] = 10; Sbox_13_table[1][14] = 14; Sbox_13_table[1][15] = 9; 
Sbox_13_table[2][0] = 10; Sbox_13_table[2][1] = 6; Sbox_13_table[2][2] = 9; Sbox_13_table[2][3] = 0; Sbox_13_table[2][4] = 12; Sbox_13_table[2][5] = 11; Sbox_13_table[2][6] = 7; Sbox_13_table[2][7] = 13; Sbox_13_table[2][8] = 15; Sbox_13_table[2][9] = 1; Sbox_13_table[2][10] = 3; Sbox_13_table[2][11] = 14; Sbox_13_table[2][12] = 5; Sbox_13_table[2][13] = 2; Sbox_13_table[2][14] = 8; Sbox_13_table[2][15] = 4; 
Sbox_13_table[3][0] = 3; Sbox_13_table[3][1] = 15; Sbox_13_table[3][2] = 0; Sbox_13_table[3][3] = 6; Sbox_13_table[3][4] = 10; Sbox_13_table[3][5] = 1; Sbox_13_table[3][6] = 13; Sbox_13_table[3][7] = 8; Sbox_13_table[3][8] = 9; Sbox_13_table[3][9] = 4; Sbox_13_table[3][10] = 5; Sbox_13_table[3][11] = 11; Sbox_13_table[3][12] = 12; Sbox_13_table[3][13] = 7; Sbox_13_table[3][14] = 2; Sbox_13_table[3][15] = 14; 

}

static void Identity_3_init()
{
}

static void doE_3_1_init()
{
}

static void Sbox_17_init()
{
    int32_t i_53;

    (Sbox_17_table)=(int**)malloc(sizeof(int*)*4);

    for ((i_53)=0; (i_53)<4; (i_53)++)
        {
        (Sbox_17_table)[(i_53)]=(int*)malloc(sizeof(int)*16);
    }

    Sbox_17_table[0][0] = 13; Sbox_17_table[0][1] = 2; Sbox_17_table[0][2] = 8; Sbox_17_table[0][3] = 4; Sbox_17_table[0][4] = 6; Sbox_17_table[0][5] = 15; Sbox_17_table[0][6] = 11; Sbox_17_table[0][7] = 1; Sbox_17_table[0][8] = 10; Sbox_17_table[0][9] = 9; Sbox_17_table[0][10] = 3; Sbox_17_table[0][11] = 14; Sbox_17_table[0][12] = 5; Sbox_17_table[0][13] = 0; Sbox_17_table[0][14] = 12; Sbox_17_table[0][15] = 7; 
Sbox_17_table[1][0] = 1; Sbox_17_table[1][1] = 15; Sbox_17_table[1][2] = 13; Sbox_17_table[1][3] = 8; Sbox_17_table[1][4] = 10; Sbox_17_table[1][5] = 3; Sbox_17_table[1][6] = 7; Sbox_17_table[1][7] = 4; Sbox_17_table[1][8] = 12; Sbox_17_table[1][9] = 5; Sbox_17_table[1][10] = 6; Sbox_17_table[1][11] = 11; Sbox_17_table[1][12] = 0; Sbox_17_table[1][13] = 14; Sbox_17_table[1][14] = 9; Sbox_17_table[1][15] = 2; 
Sbox_17_table[2][0] = 7; Sbox_17_table[2][1] = 11; Sbox_17_table[2][2] = 4; Sbox_17_table[2][3] = 1; Sbox_17_table[2][4] = 9; Sbox_17_table[2][5] = 12; Sbox_17_table[2][6] = 14; Sbox_17_table[2][7] = 2; Sbox_17_table[2][8] = 0; Sbox_17_table[2][9] = 6; Sbox_17_table[2][10] = 10; Sbox_17_table[2][11] = 13; Sbox_17_table[2][12] = 15; Sbox_17_table[2][13] = 3; Sbox_17_table[2][14] = 5; Sbox_17_table[2][15] = 8; 
Sbox_17_table[3][0] = 2; Sbox_17_table[3][1] = 1; Sbox_17_table[3][2] = 14; Sbox_17_table[3][3] = 7; Sbox_17_table[3][4] = 4; Sbox_17_table[3][5] = 10; Sbox_17_table[3][6] = 8; Sbox_17_table[3][7] = 13; Sbox_17_table[3][8] = 15; Sbox_17_table[3][9] = 12; Sbox_17_table[3][10] = 9; Sbox_17_table[3][11] = 0; Sbox_17_table[3][12] = 3; Sbox_17_table[3][13] = 5; Sbox_17_table[3][14] = 6; Sbox_17_table[3][15] = 11; 

}

static void Sbox_20_init()
{
    int32_t i_54;

    (Sbox_20_table)=(int**)malloc(sizeof(int*)*4);

    for ((i_54)=0; (i_54)<4; (i_54)++)
        {
        (Sbox_20_table)[(i_54)]=(int*)malloc(sizeof(int)*16);
    }

    Sbox_20_table[0][0] = 2; Sbox_20_table[0][1] = 12; Sbox_20_table[0][2] = 4; Sbox_20_table[0][3] = 1; Sbox_20_table[0][4] = 7; Sbox_20_table[0][5] = 10; Sbox_20_table[0][6] = 11; Sbox_20_table[0][7] = 6; Sbox_20_table[0][8] = 8; Sbox_20_table[0][9] = 5; Sbox_20_table[0][10] = 3; Sbox_20_table[0][11] = 15; Sbox_20_table[0][12] = 13; Sbox_20_table[0][13] = 0; Sbox_20_table[0][14] = 14; Sbox_20_table[0][15] = 9; 
Sbox_20_table[1][0] = 14; Sbox_20_table[1][1] = 11; Sbox_20_table[1][2] = 2; Sbox_20_table[1][3] = 12; Sbox_20_table[1][4] = 4; Sbox_20_table[1][5] = 7; Sbox_20_table[1][6] = 13; Sbox_20_table[1][7] = 1; Sbox_20_table[1][8] = 5; Sbox_20_table[1][9] = 0; Sbox_20_table[1][10] = 15; Sbox_20_table[1][11] = 10; Sbox_20_table[1][12] = 3; Sbox_20_table[1][13] = 9; Sbox_20_table[1][14] = 8; Sbox_20_table[1][15] = 6; 
Sbox_20_table[2][0] = 4; Sbox_20_table[2][1] = 2; Sbox_20_table[2][2] = 1; Sbox_20_table[2][3] = 11; Sbox_20_table[2][4] = 10; Sbox_20_table[2][5] = 13; Sbox_20_table[2][6] = 7; Sbox_20_table[2][7] = 8; Sbox_20_table[2][8] = 15; Sbox_20_table[2][9] = 9; Sbox_20_table[2][10] = 12; Sbox_20_table[2][11] = 5; Sbox_20_table[2][12] = 6; Sbox_20_table[2][13] = 3; Sbox_20_table[2][14] = 0; Sbox_20_table[2][15] = 14; 
Sbox_20_table[3][0] = 11; Sbox_20_table[3][1] = 8; Sbox_20_table[3][2] = 12; Sbox_20_table[3][3] = 7; Sbox_20_table[3][4] = 1; Sbox_20_table[3][5] = 14; Sbox_20_table[3][6] = 2; Sbox_20_table[3][7] = 13; Sbox_20_table[3][8] = 6; Sbox_20_table[3][9] = 15; Sbox_20_table[3][10] = 0; Sbox_20_table[3][11] = 9; Sbox_20_table[3][12] = 10; Sbox_20_table[3][13] = 4; Sbox_20_table[3][14] = 5; Sbox_20_table[3][15] = 3; 

}

static void Sbox_21_init()
{
    int32_t i_55;

    (Sbox_21_table)=(int**)malloc(sizeof(int*)*4);

    for ((i_55)=0; (i_55)<4; (i_55)++)
        {
        (Sbox_21_table)[(i_55)]=(int*)malloc(sizeof(int)*16);
    }

    Sbox_21_table[0][0] = 7; Sbox_21_table[0][1] = 13; Sbox_21_table[0][2] = 14; Sbox_21_table[0][3] = 3; Sbox_21_table[0][4] = 0; Sbox_21_table[0][5] = 6; Sbox_21_table[0][6] = 9; Sbox_21_table[0][7] = 10; Sbox_21_table[0][8] = 1; Sbox_21_table[0][9] = 2; Sbox_21_table[0][10] = 8; Sbox_21_table[0][11] = 5; Sbox_21_table[0][12] = 11; Sbox_21_table[0][13] = 12; Sbox_21_table[0][14] = 4; Sbox_21_table[0][15] = 15; 
Sbox_21_table[1][0] = 13; Sbox_21_table[1][1] = 8; Sbox_21_table[1][2] = 11; Sbox_21_table[1][3] = 5; Sbox_21_table[1][4] = 6; Sbox_21_table[1][5] = 15; Sbox_21_table[1][6] = 0; Sbox_21_table[1][7] = 3; Sbox_21_table[1][8] = 4; Sbox_21_table[1][9] = 7; Sbox_21_table[1][10] = 2; Sbox_21_table[1][11] = 12; Sbox_21_table[1][12] = 1; Sbox_21_table[1][13] = 10; Sbox_21_table[1][14] = 14; Sbox_21_table[1][15] = 9; 
Sbox_21_table[2][0] = 10; Sbox_21_table[2][1] = 6; Sbox_21_table[2][2] = 9; Sbox_21_table[2][3] = 0; Sbox_21_table[2][4] = 12; Sbox_21_table[2][5] = 11; Sbox_21_table[2][6] = 7; Sbox_21_table[2][7] = 13; Sbox_21_table[2][8] = 15; Sbox_21_table[2][9] = 1; Sbox_21_table[2][10] = 3; Sbox_21_table[2][11] = 14; Sbox_21_table[2][12] = 5; Sbox_21_table[2][13] = 2; Sbox_21_table[2][14] = 8; Sbox_21_table[2][15] = 4; 
Sbox_21_table[3][0] = 3; Sbox_21_table[3][1] = 15; Sbox_21_table[3][2] = 0; Sbox_21_table[3][3] = 6; Sbox_21_table[3][4] = 10; Sbox_21_table[3][5] = 1; Sbox_21_table[3][6] = 13; Sbox_21_table[3][7] = 8; Sbox_21_table[3][8] = 9; Sbox_21_table[3][9] = 4; Sbox_21_table[3][10] = 5; Sbox_21_table[3][11] = 11; Sbox_21_table[3][12] = 12; Sbox_21_table[3][13] = 7; Sbox_21_table[3][14] = 2; Sbox_21_table[3][15] = 14; 

}

static void Sbox_22_init()
{
    int32_t i_56;

    (Sbox_22_table)=(int**)malloc(sizeof(int*)*4);

    for ((i_56)=0; (i_56)<4; (i_56)++)
        {
        (Sbox_22_table)[(i_56)]=(int*)malloc(sizeof(int)*16);
    }

    Sbox_22_table[0][0] = 10; Sbox_22_table[0][1] = 0; Sbox_22_table[0][2] = 9; Sbox_22_table[0][3] = 14; Sbox_22_table[0][4] = 6; Sbox_22_table[0][5] = 3; Sbox_22_table[0][6] = 15; Sbox_22_table[0][7] = 5; Sbox_22_table[0][8] = 1; Sbox_22_table[0][9] = 13; Sbox_22_table[0][10] = 12; Sbox_22_table[0][11] = 7; Sbox_22_table[0][12] = 11; Sbox_22_table[0][13] = 4; Sbox_22_table[0][14] = 2; Sbox_22_table[0][15] = 8; 
Sbox_22_table[1][0] = 13; Sbox_22_table[1][1] = 7; Sbox_22_table[1][2] = 0; Sbox_22_table[1][3] = 9; Sbox_22_table[1][4] = 3; Sbox_22_table[1][5] = 4; Sbox_22_table[1][6] = 6; Sbox_22_table[1][7] = 10; Sbox_22_table[1][8] = 2; Sbox_22_table[1][9] = 8; Sbox_22_table[1][10] = 5; Sbox_22_table[1][11] = 14; Sbox_22_table[1][12] = 12; Sbox_22_table[1][13] = 11; Sbox_22_table[1][14] = 15; Sbox_22_table[1][15] = 1; 
Sbox_22_table[2][0] = 13; Sbox_22_table[2][1] = 6; Sbox_22_table[2][2] = 4; Sbox_22_table[2][3] = 9; Sbox_22_table[2][4] = 8; Sbox_22_table[2][5] = 15; Sbox_22_table[2][6] = 3; Sbox_22_table[2][7] = 0; Sbox_22_table[2][8] = 11; Sbox_22_table[2][9] = 1; Sbox_22_table[2][10] = 2; Sbox_22_table[2][11] = 12; Sbox_22_table[2][12] = 5; Sbox_22_table[2][13] = 10; Sbox_22_table[2][14] = 14; Sbox_22_table[2][15] = 7; 
Sbox_22_table[3][0] = 1; Sbox_22_table[3][1] = 10; Sbox_22_table[3][2] = 13; Sbox_22_table[3][3] = 0; Sbox_22_table[3][4] = 6; Sbox_22_table[3][5] = 9; Sbox_22_table[3][6] = 8; Sbox_22_table[3][7] = 7; Sbox_22_table[3][8] = 4; Sbox_22_table[3][9] = 15; Sbox_22_table[3][10] = 14; Sbox_22_table[3][11] = 3; Sbox_22_table[3][12] = 11; Sbox_22_table[3][13] = 5; Sbox_22_table[3][14] = 2; Sbox_22_table[3][15] = 12; 

}

static void Identity_6_init()
{
    uint8_t* split_Identity_6_buffer_ptr_tail=split_Identity_6_buffer;
    uint8_t _tmp;
}

static void anonFilter0_3_init()
{
}

static void Sbox_27_init()
{
    int32_t i_57;

    (Sbox_27_table)=(int**)malloc(sizeof(int*)*4);

    for ((i_57)=0; (i_57)<4; (i_57)++)
        {
        (Sbox_27_table)[(i_57)]=(int*)malloc(sizeof(int)*16);
    }

    Sbox_27_table[0][0] = 12; Sbox_27_table[0][1] = 1; Sbox_27_table[0][2] = 10; Sbox_27_table[0][3] = 15; Sbox_27_table[0][4] = 9; Sbox_27_table[0][5] = 2; Sbox_27_table[0][6] = 6; Sbox_27_table[0][7] = 8; Sbox_27_table[0][8] = 0; Sbox_27_table[0][9] = 13; Sbox_27_table[0][10] = 3; Sbox_27_table[0][11] = 4; Sbox_27_table[0][12] = 14; Sbox_27_table[0][13] = 7; Sbox_27_table[0][14] = 5; Sbox_27_table[0][15] = 11; 
Sbox_27_table[1][0] = 10; Sbox_27_table[1][1] = 15; Sbox_27_table[1][2] = 4; Sbox_27_table[1][3] = 2; Sbox_27_table[1][4] = 7; Sbox_27_table[1][5] = 12; Sbox_27_table[1][6] = 9; Sbox_27_table[1][7] = 5; Sbox_27_table[1][8] = 6; Sbox_27_table[1][9] = 1; Sbox_27_table[1][10] = 13; Sbox_27_table[1][11] = 14; Sbox_27_table[1][12] = 0; Sbox_27_table[1][13] = 11; Sbox_27_table[1][14] = 3; Sbox_27_table[1][15] = 8; 
Sbox_27_table[2][0] = 9; Sbox_27_table[2][1] = 14; Sbox_27_table[2][2] = 15; Sbox_27_table[2][3] = 5; Sbox_27_table[2][4] = 2; Sbox_27_table[2][5] = 8; Sbox_27_table[2][6] = 12; Sbox_27_table[2][7] = 3; Sbox_27_table[2][8] = 7; Sbox_27_table[2][9] = 0; Sbox_27_table[2][10] = 4; Sbox_27_table[2][11] = 10; Sbox_27_table[2][12] = 1; Sbox_27_table[2][13] = 13; Sbox_27_table[2][14] = 11; Sbox_27_table[2][15] = 6; 
Sbox_27_table[3][0] = 4; Sbox_27_table[3][1] = 3; Sbox_27_table[3][2] = 2; Sbox_27_table[3][3] = 12; Sbox_27_table[3][4] = 9; Sbox_27_table[3][5] = 5; Sbox_27_table[3][6] = 15; Sbox_27_table[3][7] = 10; Sbox_27_table[3][8] = 11; Sbox_27_table[3][9] = 14; Sbox_27_table[3][10] = 1; Sbox_27_table[3][11] = 7; Sbox_27_table[3][12] = 6; Sbox_27_table[3][13] = 0; Sbox_27_table[3][14] = 8; Sbox_27_table[3][15] = 13; 

}

static void Sbox_28_init()
{
    int32_t i_58;

    (Sbox_28_table)=(int**)malloc(sizeof(int*)*4);

    for ((i_58)=0; (i_58)<4; (i_58)++)
        {
        (Sbox_28_table)[(i_58)]=(int*)malloc(sizeof(int)*16);
    }

    Sbox_28_table[0][0] = 2; Sbox_28_table[0][1] = 12; Sbox_28_table[0][2] = 4; Sbox_28_table[0][3] = 1; Sbox_28_table[0][4] = 7; Sbox_28_table[0][5] = 10; Sbox_28_table[0][6] = 11; Sbox_28_table[0][7] = 6; Sbox_28_table[0][8] = 8; Sbox_28_table[0][9] = 5; Sbox_28_table[0][10] = 3; Sbox_28_table[0][11] = 15; Sbox_28_table[0][12] = 13; Sbox_28_table[0][13] = 0; Sbox_28_table[0][14] = 14; Sbox_28_table[0][15] = 9; 
Sbox_28_table[1][0] = 14; Sbox_28_table[1][1] = 11; Sbox_28_table[1][2] = 2; Sbox_28_table[1][3] = 12; Sbox_28_table[1][4] = 4; Sbox_28_table[1][5] = 7; Sbox_28_table[1][6] = 13; Sbox_28_table[1][7] = 1; Sbox_28_table[1][8] = 5; Sbox_28_table[1][9] = 0; Sbox_28_table[1][10] = 15; Sbox_28_table[1][11] = 10; Sbox_28_table[1][12] = 3; Sbox_28_table[1][13] = 9; Sbox_28_table[1][14] = 8; Sbox_28_table[1][15] = 6; 
Sbox_28_table[2][0] = 4; Sbox_28_table[2][1] = 2; Sbox_28_table[2][2] = 1; Sbox_28_table[2][3] = 11; Sbox_28_table[2][4] = 10; Sbox_28_table[2][5] = 13; Sbox_28_table[2][6] = 7; Sbox_28_table[2][7] = 8; Sbox_28_table[2][8] = 15; Sbox_28_table[2][9] = 9; Sbox_28_table[2][10] = 12; Sbox_28_table[2][11] = 5; Sbox_28_table[2][12] = 6; Sbox_28_table[2][13] = 3; Sbox_28_table[2][14] = 0; Sbox_28_table[2][15] = 14; 
Sbox_28_table[3][0] = 11; Sbox_28_table[3][1] = 8; Sbox_28_table[3][2] = 12; Sbox_28_table[3][3] = 7; Sbox_28_table[3][4] = 1; Sbox_28_table[3][5] = 14; Sbox_28_table[3][6] = 2; Sbox_28_table[3][7] = 13; Sbox_28_table[3][8] = 6; Sbox_28_table[3][9] = 15; Sbox_28_table[3][10] = 0; Sbox_28_table[3][11] = 9; Sbox_28_table[3][12] = 10; Sbox_28_table[3][13] = 4; Sbox_28_table[3][14] = 5; Sbox_28_table[3][15] = 3; 

}

static void Sbox_29_init()
{
    int32_t i_59;

    (Sbox_29_table)=(int**)malloc(sizeof(int*)*4);

    for ((i_59)=0; (i_59)<4; (i_59)++)
        {
        (Sbox_29_table)[(i_59)]=(int*)malloc(sizeof(int)*16);
    }

    Sbox_29_table[0][0] = 7; Sbox_29_table[0][1] = 13; Sbox_29_table[0][2] = 14; Sbox_29_table[0][3] = 3; Sbox_29_table[0][4] = 0; Sbox_29_table[0][5] = 6; Sbox_29_table[0][6] = 9; Sbox_29_table[0][7] = 10; Sbox_29_table[0][8] = 1; Sbox_29_table[0][9] = 2; Sbox_29_table[0][10] = 8; Sbox_29_table[0][11] = 5; Sbox_29_table[0][12] = 11; Sbox_29_table[0][13] = 12; Sbox_29_table[0][14] = 4; Sbox_29_table[0][15] = 15; 
Sbox_29_table[1][0] = 13; Sbox_29_table[1][1] = 8; Sbox_29_table[1][2] = 11; Sbox_29_table[1][3] = 5; Sbox_29_table[1][4] = 6; Sbox_29_table[1][5] = 15; Sbox_29_table[1][6] = 0; Sbox_29_table[1][7] = 3; Sbox_29_table[1][8] = 4; Sbox_29_table[1][9] = 7; Sbox_29_table[1][10] = 2; Sbox_29_table[1][11] = 12; Sbox_29_table[1][12] = 1; Sbox_29_table[1][13] = 10; Sbox_29_table[1][14] = 14; Sbox_29_table[1][15] = 9; 
Sbox_29_table[2][0] = 10; Sbox_29_table[2][1] = 6; Sbox_29_table[2][2] = 9; Sbox_29_table[2][3] = 0; Sbox_29_table[2][4] = 12; Sbox_29_table[2][5] = 11; Sbox_29_table[2][6] = 7; Sbox_29_table[2][7] = 13; Sbox_29_table[2][8] = 15; Sbox_29_table[2][9] = 1; Sbox_29_table[2][10] = 3; Sbox_29_table[2][11] = 14; Sbox_29_table[2][12] = 5; Sbox_29_table[2][13] = 2; Sbox_29_table[2][14] = 8; Sbox_29_table[2][15] = 4; 
Sbox_29_table[3][0] = 3; Sbox_29_table[3][1] = 15; Sbox_29_table[3][2] = 0; Sbox_29_table[3][3] = 6; Sbox_29_table[3][4] = 10; Sbox_29_table[3][5] = 1; Sbox_29_table[3][6] = 13; Sbox_29_table[3][7] = 8; Sbox_29_table[3][8] = 9; Sbox_29_table[3][9] = 4; Sbox_29_table[3][10] = 5; Sbox_29_table[3][11] = 11; Sbox_29_table[3][12] = 12; Sbox_29_table[3][13] = 7; Sbox_29_table[3][14] = 2; Sbox_29_table[3][15] = 14; 

}

static void Sbox_30_init()
{
    int32_t i_60;

    (Sbox_30_table)=(int**)malloc(sizeof(int*)*4);

    for ((i_60)=0; (i_60)<4; (i_60)++)
        {
        (Sbox_30_table)[(i_60)]=(int*)malloc(sizeof(int)*16);
    }

    Sbox_30_table[0][0] = 10; Sbox_30_table[0][1] = 0; Sbox_30_table[0][2] = 9; Sbox_30_table[0][3] = 14; Sbox_30_table[0][4] = 6; Sbox_30_table[0][5] = 3; Sbox_30_table[0][6] = 15; Sbox_30_table[0][7] = 5; Sbox_30_table[0][8] = 1; Sbox_30_table[0][9] = 13; Sbox_30_table[0][10] = 12; Sbox_30_table[0][11] = 7; Sbox_30_table[0][12] = 11; Sbox_30_table[0][13] = 4; Sbox_30_table[0][14] = 2; Sbox_30_table[0][15] = 8; 
Sbox_30_table[1][0] = 13; Sbox_30_table[1][1] = 7; Sbox_30_table[1][2] = 0; Sbox_30_table[1][3] = 9; Sbox_30_table[1][4] = 3; Sbox_30_table[1][5] = 4; Sbox_30_table[1][6] = 6; Sbox_30_table[1][7] = 10; Sbox_30_table[1][8] = 2; Sbox_30_table[1][9] = 8; Sbox_30_table[1][10] = 5; Sbox_30_table[1][11] = 14; Sbox_30_table[1][12] = 12; Sbox_30_table[1][13] = 11; Sbox_30_table[1][14] = 15; Sbox_30_table[1][15] = 1; 
Sbox_30_table[2][0] = 13; Sbox_30_table[2][1] = 6; Sbox_30_table[2][2] = 4; Sbox_30_table[2][3] = 9; Sbox_30_table[2][4] = 8; Sbox_30_table[2][5] = 15; Sbox_30_table[2][6] = 3; Sbox_30_table[2][7] = 0; Sbox_30_table[2][8] = 11; Sbox_30_table[2][9] = 1; Sbox_30_table[2][10] = 2; Sbox_30_table[2][11] = 12; Sbox_30_table[2][12] = 5; Sbox_30_table[2][13] = 10; Sbox_30_table[2][14] = 14; Sbox_30_table[2][15] = 7; 
Sbox_30_table[3][0] = 1; Sbox_30_table[3][1] = 10; Sbox_30_table[3][2] = 13; Sbox_30_table[3][3] = 0; Sbox_30_table[3][4] = 6; Sbox_30_table[3][5] = 9; Sbox_30_table[3][6] = 8; Sbox_30_table[3][7] = 7; Sbox_30_table[3][8] = 4; Sbox_30_table[3][9] = 15; Sbox_30_table[3][10] = 14; Sbox_30_table[3][11] = 3; Sbox_30_table[3][12] = 11; Sbox_30_table[3][13] = 5; Sbox_30_table[3][14] = 2; Sbox_30_table[3][15] = 12; 

}

static void Sbox_32_init()
{
    int32_t i_61;

    (Sbox_32_table)=(int**)malloc(sizeof(int*)*4);

    for ((i_61)=0; (i_61)<4; (i_61)++)
        {
        (Sbox_32_table)[(i_61)]=(int*)malloc(sizeof(int)*16);
    }

    Sbox_32_table[0][0] = 14; Sbox_32_table[0][1] = 4; Sbox_32_table[0][2] = 13; Sbox_32_table[0][3] = 1; Sbox_32_table[0][4] = 2; Sbox_32_table[0][5] = 15; Sbox_32_table[0][6] = 11; Sbox_32_table[0][7] = 8; Sbox_32_table[0][8] = 3; Sbox_32_table[0][9] = 10; Sbox_32_table[0][10] = 6; Sbox_32_table[0][11] = 12; Sbox_32_table[0][12] = 5; Sbox_32_table[0][13] = 9; Sbox_32_table[0][14] = 0; Sbox_32_table[0][15] = 7; 
Sbox_32_table[1][0] = 0; Sbox_32_table[1][1] = 15; Sbox_32_table[1][2] = 7; Sbox_32_table[1][3] = 4; Sbox_32_table[1][4] = 14; Sbox_32_table[1][5] = 2; Sbox_32_table[1][6] = 13; Sbox_32_table[1][7] = 1; Sbox_32_table[1][8] = 10; Sbox_32_table[1][9] = 6; Sbox_32_table[1][10] = 12; Sbox_32_table[1][11] = 11; Sbox_32_table[1][12] = 9; Sbox_32_table[1][13] = 5; Sbox_32_table[1][14] = 3; Sbox_32_table[1][15] = 8; 
Sbox_32_table[2][0] = 4; Sbox_32_table[2][1] = 1; Sbox_32_table[2][2] = 14; Sbox_32_table[2][3] = 8; Sbox_32_table[2][4] = 13; Sbox_32_table[2][5] = 6; Sbox_32_table[2][6] = 2; Sbox_32_table[2][7] = 11; Sbox_32_table[2][8] = 15; Sbox_32_table[2][9] = 12; Sbox_32_table[2][10] = 9; Sbox_32_table[2][11] = 7; Sbox_32_table[2][12] = 3; Sbox_32_table[2][13] = 10; Sbox_32_table[2][14] = 5; Sbox_32_table[2][15] = 0; 
Sbox_32_table[3][0] = 15; Sbox_32_table[3][1] = 12; Sbox_32_table[3][2] = 8; Sbox_32_table[3][3] = 2; Sbox_32_table[3][4] = 4; Sbox_32_table[3][5] = 9; Sbox_32_table[3][6] = 1; Sbox_32_table[3][7] = 7; Sbox_32_table[3][8] = 5; Sbox_32_table[3][9] = 11; Sbox_32_table[3][10] = 3; Sbox_32_table[3][11] = 14; Sbox_32_table[3][12] = 10; Sbox_32_table[3][13] = 0; Sbox_32_table[3][14] = 6; Sbox_32_table[3][15] = 13; 

}

static void Identity_7_init()
{
}

static void anonFilter0_4_init()
{
}

static void doIPm1_1_init()
{
}

static void Sbox_3_steady()
{
    COREVA_HWACC_MISC_TRACE(2);
    uint8_t* Xor_1_to_Sbox_3_receive_buffer = (uint8_t*)steadyClusterWaitRead(&Xor_1_to_Sbox_3_receiver, 72, sizeof(uint8_t), 0, 0, 0, 1);
    COREVA_HWACC_MISC_TRACE(3);
    if(((Sbox_3_to_doP_1_buffer_ptr_head-Sbox_3_to_doP_1_buffer)+48)>96+0)
    {
        Sbox_3_to_doP_1_buffer_ptr_head=Sbox_3_to_doP_1_buffer+0;
    }
    COREVA_HWACC_MISC_TRACE(4);
    for (int _i=0; _i<12; _i++)
        Sbox(Sbox_3_table, &Xor_1_to_Sbox_3_receive_buffer, &Sbox_3_to_doP_1_buffer_ptr_head);
    COREVA_HWACC_MISC_TRACE(5);
    steadyClusterDoneRead(&Xor_1_to_Sbox_3_receiver);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from Xor_1
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to doP_1
}

static void Sbox_4_steady()
{
    COREVA_HWACC_MISC_TRACE(2);
    uint8_t* Xor_1_to_Sbox_4_receive_buffer = (uint8_t*)steadyClusterWaitRead(&Xor_1_to_Sbox_4_receiver, 72, sizeof(uint8_t), 0, 0, 0, 1);
    COREVA_HWACC_MISC_TRACE(3);
    if(((Sbox_4_to_doP_1_buffer_ptr_head-Sbox_4_to_doP_1_buffer)+48)>96+0)
    {
        Sbox_4_to_doP_1_buffer_ptr_head=Sbox_4_to_doP_1_buffer+0;
    }
    COREVA_HWACC_MISC_TRACE(4);
    for (int _i=0; _i<12; _i++)
        Sbox(Sbox_4_table, &Xor_1_to_Sbox_4_receive_buffer, &Sbox_4_to_doP_1_buffer_ptr_head);
    COREVA_HWACC_MISC_TRACE(5);
    steadyClusterDoneRead(&Xor_1_to_Sbox_4_receiver);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from Xor_1
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to doP_1
}

static void Sbox_5_steady()
{
    COREVA_HWACC_MISC_TRACE(2);
    uint8_t* Xor_1_to_Sbox_5_receive_buffer = (uint8_t*)steadyClusterWaitRead(&Xor_1_to_Sbox_5_receiver, 72, sizeof(uint8_t), 0, 0, 0, 1);
    COREVA_HWACC_MISC_TRACE(3);
    if(((Sbox_5_to_doP_1_buffer_ptr_head-Sbox_5_to_doP_1_buffer)+48)>96+0)
    {
        Sbox_5_to_doP_1_buffer_ptr_head=Sbox_5_to_doP_1_buffer+0;
    }
    COREVA_HWACC_MISC_TRACE(4);
    for (int _i=0; _i<12; _i++)
        Sbox(Sbox_5_table, &Xor_1_to_Sbox_5_receive_buffer, &Sbox_5_to_doP_1_buffer_ptr_head);
    COREVA_HWACC_MISC_TRACE(5);
    steadyClusterDoneRead(&Xor_1_to_Sbox_5_receiver);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from Xor_1
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to doP_1
}

static void Sbox_6_steady()
{
    COREVA_HWACC_MISC_TRACE(2);
    uint8_t* Xor_1_to_Sbox_6_receive_buffer = (uint8_t*)steadyClusterWaitRead(&Xor_1_to_Sbox_6_receiver, 72, sizeof(uint8_t), 0, 0, 0, 1);
    COREVA_HWACC_MISC_TRACE(3);
    if(((Sbox_6_to_doP_1_buffer_ptr_head-Sbox_6_to_doP_1_buffer)+48)>96+0)
    {
        Sbox_6_to_doP_1_buffer_ptr_head=Sbox_6_to_doP_1_buffer+0;
    }
    COREVA_HWACC_MISC_TRACE(4);
    for (int _i=0; _i<12; _i++)
        Sbox(Sbox_6_table, &Xor_1_to_Sbox_6_receive_buffer, &Sbox_6_to_doP_1_buffer_ptr_head);
    COREVA_HWACC_MISC_TRACE(5);
    steadyClusterDoneRead(&Xor_1_to_Sbox_6_receiver);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from Xor_1
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to doP_1
}

static void Sbox_8_steady()
{
    COREVA_HWACC_MISC_TRACE(2);
    uint8_t* Xor_1_to_Sbox_8_receive_buffer = (uint8_t*)steadyClusterWaitRead(&Xor_1_to_Sbox_8_receiver, 72, sizeof(uint8_t), 0, 0, 0, 1);
    COREVA_HWACC_MISC_TRACE(3);
    if(((Sbox_8_to_doP_1_buffer_ptr_head-Sbox_8_to_doP_1_buffer)+48)>96+0)
    {
        Sbox_8_to_doP_1_buffer_ptr_head=Sbox_8_to_doP_1_buffer+0;
    }
    COREVA_HWACC_MISC_TRACE(4);
    for (int _i=0; _i<12; _i++)
        Sbox(Sbox_8_table, &Xor_1_to_Sbox_8_receive_buffer, &Sbox_8_to_doP_1_buffer_ptr_head);
    COREVA_HWACC_MISC_TRACE(5);
    steadyClusterDoneRead(&Xor_1_to_Sbox_8_receiver);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from Xor_1
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to doP_1
}

static void doP_1_steady()
{
    COREVA_HWACC_MISC_TRACE(2);
    uint8_t* Sbox_1_1_to_doP_1_receive_buffer = (uint8_t*)steadyClusterWaitRead(&Sbox_1_1_to_doP_1_receiver, 24, sizeof(uint8_t), 0, 0, 0, 1);
    COREVA_HWACC_MISC_TRACE(3);
    uint8_t* Sbox_1_2_to_doP_1_receive_buffer = (uint8_t*)steadyClusterWaitRead(&Sbox_1_2_to_doP_1_receiver, 24, sizeof(uint8_t), 0, 0, 0, 1);
    COREVA_HWACC_MISC_TRACE(3);
    uint8_t* Sbox_2_1_to_doP_1_receive_buffer = (uint8_t*)steadyClusterWaitRead(&Sbox_2_1_to_doP_1_receiver, 16, sizeof(uint8_t), 0, 0, 0, 1);
    COREVA_HWACC_MISC_TRACE(3);
    uint8_t* Sbox_2_2_to_doP_1_receive_buffer = (uint8_t*)steadyClusterWaitRead(&Sbox_2_2_to_doP_1_receiver, 16, sizeof(uint8_t), 0, 0, 0, 1);
    COREVA_HWACC_MISC_TRACE(3);
    uint8_t* Sbox_2_3_to_doP_1_receive_buffer = (uint8_t*)steadyClusterWaitRead(&Sbox_2_3_to_doP_1_receiver, 16, sizeof(uint8_t), 0, 0, 0, 1);
    COREVA_HWACC_MISC_TRACE(3);
    if(((Sbox_3_to_doP_1_buffer_ptr_tail-Sbox_3_to_doP_1_buffer)+48)>96+0)
        Sbox_3_to_doP_1_buffer_ptr_tail=Sbox_3_to_doP_1_buffer+0;
    COREVA_HWACC_MISC_TRACE(3);
    if(((Sbox_4_to_doP_1_buffer_ptr_tail-Sbox_4_to_doP_1_buffer)+48)>96+0)
        Sbox_4_to_doP_1_buffer_ptr_tail=Sbox_4_to_doP_1_buffer+0;
    COREVA_HWACC_MISC_TRACE(3);
    if(((Sbox_5_to_doP_1_buffer_ptr_tail-Sbox_5_to_doP_1_buffer)+48)>96+0)
        Sbox_5_to_doP_1_buffer_ptr_tail=Sbox_5_to_doP_1_buffer+0;
    COREVA_HWACC_MISC_TRACE(3);
    if(((Sbox_6_to_doP_1_buffer_ptr_tail-Sbox_6_to_doP_1_buffer)+48)>96+0)
        Sbox_6_to_doP_1_buffer_ptr_tail=Sbox_6_to_doP_1_buffer+0;
    COREVA_HWACC_MISC_TRACE(3);
    uint8_t* Sbox_7_to_doP_1_receive_buffer = (uint8_t*)steadyClusterWaitRead(&Sbox_7_to_doP_1_receiver, 48, sizeof(uint8_t), 0, 0, 0, 1);
    COREVA_HWACC_MISC_TRACE(3);
    if(((Sbox_8_to_doP_1_buffer_ptr_tail-Sbox_8_to_doP_1_buffer)+48)>96+0)
        Sbox_8_to_doP_1_buffer_ptr_tail=Sbox_8_to_doP_1_buffer+0;
    COREVA_HWACC_MISC_TRACE(3);
    uint8_t* merge_doP_1_buffer_ptr_head=merge_doP_1_buffer;
    // SimpleMergeSplitBuffer -> Merge
    for (unsigned int _i=0;_i<2;_i++)
    {
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_1_1_to_doP_1_receive_buffer++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_1_1_to_doP_1_receive_buffer++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_1_1_to_doP_1_receive_buffer++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_1_1_to_doP_1_receive_buffer++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_2_1_to_doP_1_receive_buffer++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_2_1_to_doP_1_receive_buffer++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_2_1_to_doP_1_receive_buffer++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_2_1_to_doP_1_receive_buffer++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_3_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_3_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_3_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_3_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_4_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_4_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_4_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_4_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_5_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_5_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_5_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_5_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_6_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_6_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_6_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_6_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_7_to_doP_1_receive_buffer++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_7_to_doP_1_receive_buffer++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_7_to_doP_1_receive_buffer++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_7_to_doP_1_receive_buffer++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_8_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_8_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_8_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_8_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_1_2_to_doP_1_receive_buffer++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_1_2_to_doP_1_receive_buffer++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_1_2_to_doP_1_receive_buffer++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_1_2_to_doP_1_receive_buffer++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_2_2_to_doP_1_receive_buffer++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_2_2_to_doP_1_receive_buffer++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_2_2_to_doP_1_receive_buffer++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_2_2_to_doP_1_receive_buffer++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_3_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_3_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_3_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_3_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_4_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_4_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_4_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_4_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_5_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_5_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_5_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_5_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_6_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_6_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_6_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_6_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_7_to_doP_1_receive_buffer++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_7_to_doP_1_receive_buffer++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_7_to_doP_1_receive_buffer++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_7_to_doP_1_receive_buffer++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_8_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_8_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_8_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_8_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_1_1_to_doP_1_receive_buffer++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_1_1_to_doP_1_receive_buffer++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_1_1_to_doP_1_receive_buffer++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_1_1_to_doP_1_receive_buffer++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_2_3_to_doP_1_receive_buffer++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_2_3_to_doP_1_receive_buffer++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_2_3_to_doP_1_receive_buffer++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_2_3_to_doP_1_receive_buffer++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_3_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_3_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_3_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_3_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_4_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_4_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_4_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_4_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_5_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_5_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_5_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_5_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_6_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_6_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_6_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_6_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_7_to_doP_1_receive_buffer++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_7_to_doP_1_receive_buffer++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_7_to_doP_1_receive_buffer++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_7_to_doP_1_receive_buffer++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_8_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_8_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_8_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_8_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_1_2_to_doP_1_receive_buffer++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_1_2_to_doP_1_receive_buffer++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_1_2_to_doP_1_receive_buffer++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_1_2_to_doP_1_receive_buffer++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_2_1_to_doP_1_receive_buffer++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_2_1_to_doP_1_receive_buffer++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_2_1_to_doP_1_receive_buffer++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_2_1_to_doP_1_receive_buffer++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_3_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_3_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_3_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_3_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_4_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_4_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_4_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_4_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_5_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_5_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_5_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_5_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_6_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_6_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_6_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_6_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_7_to_doP_1_receive_buffer++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_7_to_doP_1_receive_buffer++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_7_to_doP_1_receive_buffer++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_7_to_doP_1_receive_buffer++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_8_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_8_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_8_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_8_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_1_1_to_doP_1_receive_buffer++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_1_1_to_doP_1_receive_buffer++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_1_1_to_doP_1_receive_buffer++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_1_1_to_doP_1_receive_buffer++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_2_2_to_doP_1_receive_buffer++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_2_2_to_doP_1_receive_buffer++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_2_2_to_doP_1_receive_buffer++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_2_2_to_doP_1_receive_buffer++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_3_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_3_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_3_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_3_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_4_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_4_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_4_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_4_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_5_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_5_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_5_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_5_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_6_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_6_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_6_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_6_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_7_to_doP_1_receive_buffer++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_7_to_doP_1_receive_buffer++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_7_to_doP_1_receive_buffer++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_7_to_doP_1_receive_buffer++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_8_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_8_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_8_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_8_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_1_2_to_doP_1_receive_buffer++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_1_2_to_doP_1_receive_buffer++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_1_2_to_doP_1_receive_buffer++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_1_2_to_doP_1_receive_buffer++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_2_3_to_doP_1_receive_buffer++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_2_3_to_doP_1_receive_buffer++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_2_3_to_doP_1_receive_buffer++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_2_3_to_doP_1_receive_buffer++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_3_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_3_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_3_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_3_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_4_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_4_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_4_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_4_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_5_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_5_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_5_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_5_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_6_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_6_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_6_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_6_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_7_to_doP_1_receive_buffer++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_7_to_doP_1_receive_buffer++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_7_to_doP_1_receive_buffer++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_7_to_doP_1_receive_buffer++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_8_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_8_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_8_to_doP_1_buffer_ptr_tail++));
        *merge_doP_1_buffer_ptr_head++ = ((*Sbox_8_to_doP_1_buffer_ptr_tail++));
    }
    uint8_t* merge_doP_1_buffer_ptr_tail=merge_doP_1_buffer;
    COREVA_HWACC_MISC_TRACE(3);
    uint8_t* doP_1_to_Xor_2_send_buffer = (uint8_t*)steadyClusterWaitWrite(&doP_1_to_Xor_2_sender);
    COREVA_HWACC_MISC_TRACE(4);
    for (int _i=0; _i<12; _i++)
        doP(&merge_doP_1_buffer_ptr_tail, &doP_1_to_Xor_2_send_buffer);
    COREVA_HWACC_MISC_TRACE(5);
    steadyClusterDoneRead(&Sbox_1_1_to_doP_1_receiver);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from Sbox_1_1
    steadyClusterDoneRead(&Sbox_1_2_to_doP_1_receiver);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from Sbox_1_2
    steadyClusterDoneRead(&Sbox_2_1_to_doP_1_receiver);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from Sbox_2_1
    steadyClusterDoneRead(&Sbox_2_2_to_doP_1_receiver);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from Sbox_2_2
    steadyClusterDoneRead(&Sbox_2_3_to_doP_1_receiver);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from Sbox_2_3
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from Sbox_3
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from Sbox_4
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from Sbox_5
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from Sbox_6
    steadyClusterDoneRead(&Sbox_7_to_doP_1_receiver);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from Sbox_7
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from Sbox_8
    steadyClusterDoneWrite(&doP_1_to_Xor_2_sender);
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to Xor_2
}

static void Identity_1_steady()
{
    COREVA_HWACC_MISC_TRACE(2);
    uint8_t* doIP_1_1_to_Identity_1_receive_buffer = (uint8_t*)steadyClusterWaitRead(&doIP_1_1_to_Identity_1_receiver, 192, sizeof(uint8_t), 0, 0, 0, 1);
    COREVA_HWACC_MISC_TRACE(3);
    uint8_t* doIP_1_2_to_Identity_1_receive_buffer = (uint8_t*)steadyClusterWaitRead(&doIP_1_2_to_Identity_1_receiver, 192, sizeof(uint8_t), 0, 0, 0, 1);
    COREVA_HWACC_MISC_TRACE(3);
    uint8_t* merge_Identity_1_buffer_ptr_head=merge_Identity_1_buffer;
    // SimpleMergeSplitBuffer -> Merge
    for (unsigned int _i=0;_i<6;_i++)
    {
        *merge_Identity_1_buffer_ptr_head++ = ((*doIP_1_1_to_Identity_1_receive_buffer++));
        *merge_Identity_1_buffer_ptr_head++ = ((*doIP_1_1_to_Identity_1_receive_buffer++));
        *merge_Identity_1_buffer_ptr_head++ = ((*doIP_1_1_to_Identity_1_receive_buffer++));
        *merge_Identity_1_buffer_ptr_head++ = ((*doIP_1_1_to_Identity_1_receive_buffer++));
        *merge_Identity_1_buffer_ptr_head++ = ((*doIP_1_1_to_Identity_1_receive_buffer++));
        *merge_Identity_1_buffer_ptr_head++ = ((*doIP_1_1_to_Identity_1_receive_buffer++));
        *merge_Identity_1_buffer_ptr_head++ = ((*doIP_1_1_to_Identity_1_receive_buffer++));
        *merge_Identity_1_buffer_ptr_head++ = ((*doIP_1_1_to_Identity_1_receive_buffer++));
        *merge_Identity_1_buffer_ptr_head++ = ((*doIP_1_1_to_Identity_1_receive_buffer++));
        *merge_Identity_1_buffer_ptr_head++ = ((*doIP_1_1_to_Identity_1_receive_buffer++));
        *merge_Identity_1_buffer_ptr_head++ = ((*doIP_1_1_to_Identity_1_receive_buffer++));
        *merge_Identity_1_buffer_ptr_head++ = ((*doIP_1_1_to_Identity_1_receive_buffer++));
        *merge_Identity_1_buffer_ptr_head++ = ((*doIP_1_1_to_Identity_1_receive_buffer++));
        *merge_Identity_1_buffer_ptr_head++ = ((*doIP_1_1_to_Identity_1_receive_buffer++));
        *merge_Identity_1_buffer_ptr_head++ = ((*doIP_1_1_to_Identity_1_receive_buffer++));
        *merge_Identity_1_buffer_ptr_head++ = ((*doIP_1_1_to_Identity_1_receive_buffer++));
        *merge_Identity_1_buffer_ptr_head++ = ((*doIP_1_1_to_Identity_1_receive_buffer++));
        *merge_Identity_1_buffer_ptr_head++ = ((*doIP_1_1_to_Identity_1_receive_buffer++));
        *merge_Identity_1_buffer_ptr_head++ = ((*doIP_1_1_to_Identity_1_receive_buffer++));
        *merge_Identity_1_buffer_ptr_head++ = ((*doIP_1_1_to_Identity_1_receive_buffer++));
        *merge_Identity_1_buffer_ptr_head++ = ((*doIP_1_1_to_Identity_1_receive_buffer++));
        *merge_Identity_1_buffer_ptr_head++ = ((*doIP_1_1_to_Identity_1_receive_buffer++));
        *merge_Identity_1_buffer_ptr_head++ = ((*doIP_1_1_to_Identity_1_receive_buffer++));
        *merge_Identity_1_buffer_ptr_head++ = ((*doIP_1_1_to_Identity_1_receive_buffer++));
        *merge_Identity_1_buffer_ptr_head++ = ((*doIP_1_1_to_Identity_1_receive_buffer++));
        *merge_Identity_1_buffer_ptr_head++ = ((*doIP_1_1_to_Identity_1_receive_buffer++));
        *merge_Identity_1_buffer_ptr_head++ = ((*doIP_1_1_to_Identity_1_receive_buffer++));
        *merge_Identity_1_buffer_ptr_head++ = ((*doIP_1_1_to_Identity_1_receive_buffer++));
        *merge_Identity_1_buffer_ptr_head++ = ((*doIP_1_1_to_Identity_1_receive_buffer++));
        *merge_Identity_1_buffer_ptr_head++ = ((*doIP_1_1_to_Identity_1_receive_buffer++));
        *merge_Identity_1_buffer_ptr_head++ = ((*doIP_1_1_to_Identity_1_receive_buffer++));
        *merge_Identity_1_buffer_ptr_head++ = ((*doIP_1_1_to_Identity_1_receive_buffer++));
        *merge_Identity_1_buffer_ptr_head++ = ((*doIP_1_2_to_Identity_1_receive_buffer++));
        *merge_Identity_1_buffer_ptr_head++ = ((*doIP_1_2_to_Identity_1_receive_buffer++));
        *merge_Identity_1_buffer_ptr_head++ = ((*doIP_1_2_to_Identity_1_receive_buffer++));
        *merge_Identity_1_buffer_ptr_head++ = ((*doIP_1_2_to_Identity_1_receive_buffer++));
        *merge_Identity_1_buffer_ptr_head++ = ((*doIP_1_2_to_Identity_1_receive_buffer++));
        *merge_Identity_1_buffer_ptr_head++ = ((*doIP_1_2_to_Identity_1_receive_buffer++));
        *merge_Identity_1_buffer_ptr_head++ = ((*doIP_1_2_to_Identity_1_receive_buffer++));
        *merge_Identity_1_buffer_ptr_head++ = ((*doIP_1_2_to_Identity_1_receive_buffer++));
        *merge_Identity_1_buffer_ptr_head++ = ((*doIP_1_2_to_Identity_1_receive_buffer++));
        *merge_Identity_1_buffer_ptr_head++ = ((*doIP_1_2_to_Identity_1_receive_buffer++));
        *merge_Identity_1_buffer_ptr_head++ = ((*doIP_1_2_to_Identity_1_receive_buffer++));
        *merge_Identity_1_buffer_ptr_head++ = ((*doIP_1_2_to_Identity_1_receive_buffer++));
        *merge_Identity_1_buffer_ptr_head++ = ((*doIP_1_2_to_Identity_1_receive_buffer++));
        *merge_Identity_1_buffer_ptr_head++ = ((*doIP_1_2_to_Identity_1_receive_buffer++));
        *merge_Identity_1_buffer_ptr_head++ = ((*doIP_1_2_to_Identity_1_receive_buffer++));
        *merge_Identity_1_buffer_ptr_head++ = ((*doIP_1_2_to_Identity_1_receive_buffer++));
        *merge_Identity_1_buffer_ptr_head++ = ((*doIP_1_2_to_Identity_1_receive_buffer++));
        *merge_Identity_1_buffer_ptr_head++ = ((*doIP_1_2_to_Identity_1_receive_buffer++));
        *merge_Identity_1_buffer_ptr_head++ = ((*doIP_1_2_to_Identity_1_receive_buffer++));
        *merge_Identity_1_buffer_ptr_head++ = ((*doIP_1_2_to_Identity_1_receive_buffer++));
        *merge_Identity_1_buffer_ptr_head++ = ((*doIP_1_2_to_Identity_1_receive_buffer++));
        *merge_Identity_1_buffer_ptr_head++ = ((*doIP_1_2_to_Identity_1_receive_buffer++));
        *merge_Identity_1_buffer_ptr_head++ = ((*doIP_1_2_to_Identity_1_receive_buffer++));
        *merge_Identity_1_buffer_ptr_head++ = ((*doIP_1_2_to_Identity_1_receive_buffer++));
        *merge_Identity_1_buffer_ptr_head++ = ((*doIP_1_2_to_Identity_1_receive_buffer++));
        *merge_Identity_1_buffer_ptr_head++ = ((*doIP_1_2_to_Identity_1_receive_buffer++));
        *merge_Identity_1_buffer_ptr_head++ = ((*doIP_1_2_to_Identity_1_receive_buffer++));
        *merge_Identity_1_buffer_ptr_head++ = ((*doIP_1_2_to_Identity_1_receive_buffer++));
        *merge_Identity_1_buffer_ptr_head++ = ((*doIP_1_2_to_Identity_1_receive_buffer++));
        *merge_Identity_1_buffer_ptr_head++ = ((*doIP_1_2_to_Identity_1_receive_buffer++));
        *merge_Identity_1_buffer_ptr_head++ = ((*doIP_1_2_to_Identity_1_receive_buffer++));
        *merge_Identity_1_buffer_ptr_head++ = ((*doIP_1_2_to_Identity_1_receive_buffer++));
    }
    uint8_t* merge_Identity_1_buffer_ptr_tail=merge_Identity_1_buffer;
    COREVA_HWACC_MISC_TRACE(3);
    uint8_t* Identity_1_to_Xor_2_send_buffer = (uint8_t*)steadyClusterWaitWrite(&Identity_1_to_Xor_2_sender);
    COREVA_HWACC_MISC_TRACE(4);
    for (int _i=0; _i<384; _i++)
        Identity(&merge_Identity_1_buffer_ptr_tail, &Identity_1_to_Xor_2_send_buffer);
    COREVA_HWACC_MISC_TRACE(5);
    steadyClusterDoneRead(&doIP_1_1_to_Identity_1_receiver);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from doIP_1_1
    steadyClusterDoneRead(&doIP_1_2_to_Identity_1_receiver);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from doIP_1_2
    steadyClusterDoneWrite(&Identity_1_to_Xor_2_sender);
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to Xor_2
}

static void doE_2_steady()
{
    COREVA_HWACC_MISC_TRACE(2);
    uint8_t* Xor_2_to_doE_2_receive_buffer = (uint8_t*)steadyClusterWaitRead(&Xor_2_to_doE_2_receiver, 384, sizeof(uint8_t), 0, 0, 0, 1);
    COREVA_HWACC_MISC_TRACE(3);
    if(((doE_2_to_Xor_3_1_buffer_ptr_head-doE_2_to_Xor_3_1_buffer)+192)>384+0)
    {
        doE_2_to_Xor_3_1_buffer_ptr_head=doE_2_to_Xor_3_1_buffer+0;
    }
    COREVA_HWACC_MISC_TRACE(4);
    if(((doE_2_to_Xor_3_2_buffer_ptr_head-doE_2_to_Xor_3_2_buffer)+192)>384+0)
    {
        doE_2_to_Xor_3_2_buffer_ptr_head=doE_2_to_Xor_3_2_buffer+0;
    }
    COREVA_HWACC_MISC_TRACE(4);
    uint8_t* doE_2_to_Xor_3_3_send_buffer = (uint8_t*)steadyClusterWaitWrite(&doE_2_to_Xor_3_3_sender);
    COREVA_HWACC_MISC_TRACE(4);
    uint8_t* split_doE_2_buffer_ptr_head=split_doE_2_buffer;
    COREVA_HWACC_MISC_TRACE(4); // SteadyWaitWrite MergeBuffer split
    for (int _i=0; _i<12; _i++)
        doE(&Xor_2_to_doE_2_receive_buffer, &split_doE_2_buffer_ptr_head);
    COREVA_HWACC_MISC_TRACE(5);
    steadyClusterDoneRead(&Xor_2_to_doE_2_receiver);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from Xor_2
    // SimpleMergeSplitBuffer -> Split
    uint8_t* split_doE_2_buffer_ptr_tail=split_doE_2_buffer;
    for (unsigned int _i=0;_i<192;_i++)
    {
        uint8_t _tmp;
        *doE_2_to_Xor_3_1_buffer_ptr_head++ = ((*split_doE_2_buffer_ptr_tail++));
        *doE_2_to_Xor_3_2_buffer_ptr_head++ = ((*split_doE_2_buffer_ptr_tail++));
        *doE_2_to_Xor_3_3_send_buffer++ = ((*split_doE_2_buffer_ptr_tail++));
    }
    COREVA_HWACC_MISC_TRACE(7);  // SimpleMergeSplitBuffer split
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to Xor_3_1
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to Xor_3_2
    steadyClusterDoneWrite(&doE_2_to_Xor_3_3_sender);
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to Xor_3_3
}

static void Xor_3_1_steady()
{
    COREVA_HWACC_MISC_TRACE(2);
    if(((doE_2_to_Xor_3_1_buffer_ptr_tail-doE_2_to_Xor_3_1_buffer)+192)>384+0)
        doE_2_to_Xor_3_1_buffer_ptr_tail=doE_2_to_Xor_3_1_buffer+0;
    COREVA_HWACC_MISC_TRACE(3);
    uint8_t* anonFilter3_2_to_Xor_3_1_receive_buffer = (uint8_t*)steadyClusterWaitRead(&anonFilter3_2_to_Xor_3_1_receiver, 192, sizeof(uint8_t), 0, 0, 0, 1);
    COREVA_HWACC_MISC_TRACE(3);
    uint8_t* merge_Xor_3_1_buffer_ptr_head=merge_Xor_3_1_buffer;
    // SimpleMergeSplitBuffer -> Merge
    for (unsigned int _i=0;_i<192;_i++)
    {
        *merge_Xor_3_1_buffer_ptr_head++ = ((*doE_2_to_Xor_3_1_buffer_ptr_tail++));
        *merge_Xor_3_1_buffer_ptr_head++ = ((*anonFilter3_2_to_Xor_3_1_receive_buffer++));
    }
    uint8_t* merge_Xor_3_1_buffer_ptr_tail=merge_Xor_3_1_buffer;
    COREVA_HWACC_MISC_TRACE(3);
    if(((Xor_3_1_to_Sbox_9_buffer_ptr_head-Xor_3_1_to_Sbox_9_buffer)+24)>48+0)
    {
        Xor_3_1_to_Sbox_9_buffer_ptr_head=Xor_3_1_to_Sbox_9_buffer+0;
    }
    COREVA_HWACC_MISC_TRACE(4);
    uint8_t* Xor_3_1_to_Sbox_10_send_buffer = (uint8_t*)steadyClusterWaitWrite(&Xor_3_1_to_Sbox_10_sender);
    COREVA_HWACC_MISC_TRACE(4);
    if(((Xor_3_1_to_Sbox_11_buffer_ptr_head-Xor_3_1_to_Sbox_11_buffer)+24)>48+0)
    {
        Xor_3_1_to_Sbox_11_buffer_ptr_head=Xor_3_1_to_Sbox_11_buffer+0;
    }
    COREVA_HWACC_MISC_TRACE(4);
    if(((Xor_3_1_to_Sbox_12_1_buffer_ptr_head-Xor_3_1_to_Sbox_12_1_buffer)+6)>12+0)
    {
        Xor_3_1_to_Sbox_12_1_buffer_ptr_head=Xor_3_1_to_Sbox_12_1_buffer+0;
    }
    COREVA_HWACC_MISC_TRACE(4);
    if(((Xor_3_1_to_Sbox_13_buffer_ptr_head-Xor_3_1_to_Sbox_13_buffer)+24)>48+0)
    {
        Xor_3_1_to_Sbox_13_buffer_ptr_head=Xor_3_1_to_Sbox_13_buffer+0;
    }
    COREVA_HWACC_MISC_TRACE(4);
    uint8_t* Xor_3_1_to_Sbox_14_send_buffer = (uint8_t*)steadyClusterWaitWrite(&Xor_3_1_to_Sbox_14_sender);
    COREVA_HWACC_MISC_TRACE(4);
    uint8_t* Xor_3_1_to_Sbox_15_send_buffer = (uint8_t*)steadyClusterWaitWrite(&Xor_3_1_to_Sbox_15_sender);
    COREVA_HWACC_MISC_TRACE(4);
    uint8_t* Xor_3_1_to_Sbox_16_send_buffer = (uint8_t*)steadyClusterWaitWrite(&Xor_3_1_to_Sbox_16_sender);
    COREVA_HWACC_MISC_TRACE(4);
    if(((Xor_3_1_to_Sbox_12_2_buffer_ptr_head-Xor_3_1_to_Sbox_12_2_buffer)+6)>12+0)
    {
        Xor_3_1_to_Sbox_12_2_buffer_ptr_head=Xor_3_1_to_Sbox_12_2_buffer+0;
    }
    COREVA_HWACC_MISC_TRACE(4);
    uint8_t* Xor_3_1_to_Sbox_12_3_send_buffer = (uint8_t*)steadyClusterWaitWrite(&Xor_3_1_to_Sbox_12_3_sender);
    COREVA_HWACC_MISC_TRACE(4);
    if(((Xor_3_1_to_Sbox_12_4_buffer_ptr_head-Xor_3_1_to_Sbox_12_4_buffer)+6)>12+0)
    {
        Xor_3_1_to_Sbox_12_4_buffer_ptr_head=Xor_3_1_to_Sbox_12_4_buffer+0;
    }
    COREVA_HWACC_MISC_TRACE(4);
    uint8_t* split_Xor_3_1_buffer_ptr_head=split_Xor_3_1_buffer;
    COREVA_HWACC_MISC_TRACE(4); // SteadyWaitWrite MergeBuffer split
    for (int _i=0; _i<192; _i++)
        Xor(2, &merge_Xor_3_1_buffer_ptr_tail, &split_Xor_3_1_buffer_ptr_head);
    COREVA_HWACC_MISC_TRACE(5);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from doE_2
    steadyClusterDoneRead(&anonFilter3_2_to_Xor_3_1_receiver);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from anonFilter3_2
    // SimpleMergeSplitBuffer -> Split
    uint8_t* split_Xor_3_1_buffer_ptr_tail=split_Xor_3_1_buffer;
    for (unsigned int _i=0;_i<3;_i++)
    {
        uint8_t _tmp;
        *Xor_3_1_to_Sbox_9_buffer_ptr_head++ = ((*split_Xor_3_1_buffer_ptr_tail++));
        *Xor_3_1_to_Sbox_9_buffer_ptr_head++ = ((*split_Xor_3_1_buffer_ptr_tail++));
        *Xor_3_1_to_Sbox_10_send_buffer++ = ((*split_Xor_3_1_buffer_ptr_tail++));
        *Xor_3_1_to_Sbox_10_send_buffer++ = ((*split_Xor_3_1_buffer_ptr_tail++));
        *Xor_3_1_to_Sbox_11_buffer_ptr_head++ = ((*split_Xor_3_1_buffer_ptr_tail++));
        *Xor_3_1_to_Sbox_11_buffer_ptr_head++ = ((*split_Xor_3_1_buffer_ptr_tail++));
        *Xor_3_1_to_Sbox_12_1_buffer_ptr_head++ = ((*split_Xor_3_1_buffer_ptr_tail++));
        *Xor_3_1_to_Sbox_12_1_buffer_ptr_head++ = ((*split_Xor_3_1_buffer_ptr_tail++));
        *Xor_3_1_to_Sbox_13_buffer_ptr_head++ = ((*split_Xor_3_1_buffer_ptr_tail++));
        *Xor_3_1_to_Sbox_13_buffer_ptr_head++ = ((*split_Xor_3_1_buffer_ptr_tail++));
        *Xor_3_1_to_Sbox_14_send_buffer++ = ((*split_Xor_3_1_buffer_ptr_tail++));
        *Xor_3_1_to_Sbox_14_send_buffer++ = ((*split_Xor_3_1_buffer_ptr_tail++));
        *Xor_3_1_to_Sbox_15_send_buffer++ = ((*split_Xor_3_1_buffer_ptr_tail++));
        *Xor_3_1_to_Sbox_15_send_buffer++ = ((*split_Xor_3_1_buffer_ptr_tail++));
        *Xor_3_1_to_Sbox_16_send_buffer++ = ((*split_Xor_3_1_buffer_ptr_tail++));
        *Xor_3_1_to_Sbox_16_send_buffer++ = ((*split_Xor_3_1_buffer_ptr_tail++));
        *Xor_3_1_to_Sbox_9_buffer_ptr_head++ = ((*split_Xor_3_1_buffer_ptr_tail++));
        *Xor_3_1_to_Sbox_9_buffer_ptr_head++ = ((*split_Xor_3_1_buffer_ptr_tail++));
        *Xor_3_1_to_Sbox_10_send_buffer++ = ((*split_Xor_3_1_buffer_ptr_tail++));
        *Xor_3_1_to_Sbox_10_send_buffer++ = ((*split_Xor_3_1_buffer_ptr_tail++));
        *Xor_3_1_to_Sbox_11_buffer_ptr_head++ = ((*split_Xor_3_1_buffer_ptr_tail++));
        *Xor_3_1_to_Sbox_11_buffer_ptr_head++ = ((*split_Xor_3_1_buffer_ptr_tail++));
        *Xor_3_1_to_Sbox_12_2_buffer_ptr_head++ = ((*split_Xor_3_1_buffer_ptr_tail++));
        *Xor_3_1_to_Sbox_12_2_buffer_ptr_head++ = ((*split_Xor_3_1_buffer_ptr_tail++));
        *Xor_3_1_to_Sbox_13_buffer_ptr_head++ = ((*split_Xor_3_1_buffer_ptr_tail++));
        *Xor_3_1_to_Sbox_13_buffer_ptr_head++ = ((*split_Xor_3_1_buffer_ptr_tail++));
        *Xor_3_1_to_Sbox_14_send_buffer++ = ((*split_Xor_3_1_buffer_ptr_tail++));
        *Xor_3_1_to_Sbox_14_send_buffer++ = ((*split_Xor_3_1_buffer_ptr_tail++));
        *Xor_3_1_to_Sbox_15_send_buffer++ = ((*split_Xor_3_1_buffer_ptr_tail++));
        *Xor_3_1_to_Sbox_15_send_buffer++ = ((*split_Xor_3_1_buffer_ptr_tail++));
        *Xor_3_1_to_Sbox_16_send_buffer++ = ((*split_Xor_3_1_buffer_ptr_tail++));
        *Xor_3_1_to_Sbox_16_send_buffer++ = ((*split_Xor_3_1_buffer_ptr_tail++));
        *Xor_3_1_to_Sbox_9_buffer_ptr_head++ = ((*split_Xor_3_1_buffer_ptr_tail++));
        *Xor_3_1_to_Sbox_9_buffer_ptr_head++ = ((*split_Xor_3_1_buffer_ptr_tail++));
        *Xor_3_1_to_Sbox_10_send_buffer++ = ((*split_Xor_3_1_buffer_ptr_tail++));
        *Xor_3_1_to_Sbox_10_send_buffer++ = ((*split_Xor_3_1_buffer_ptr_tail++));
        *Xor_3_1_to_Sbox_11_buffer_ptr_head++ = ((*split_Xor_3_1_buffer_ptr_tail++));
        *Xor_3_1_to_Sbox_11_buffer_ptr_head++ = ((*split_Xor_3_1_buffer_ptr_tail++));
        *Xor_3_1_to_Sbox_12_3_send_buffer++ = ((*split_Xor_3_1_buffer_ptr_tail++));
        *Xor_3_1_to_Sbox_12_3_send_buffer++ = ((*split_Xor_3_1_buffer_ptr_tail++));
        *Xor_3_1_to_Sbox_13_buffer_ptr_head++ = ((*split_Xor_3_1_buffer_ptr_tail++));
        *Xor_3_1_to_Sbox_13_buffer_ptr_head++ = ((*split_Xor_3_1_buffer_ptr_tail++));
        *Xor_3_1_to_Sbox_14_send_buffer++ = ((*split_Xor_3_1_buffer_ptr_tail++));
        *Xor_3_1_to_Sbox_14_send_buffer++ = ((*split_Xor_3_1_buffer_ptr_tail++));
        *Xor_3_1_to_Sbox_15_send_buffer++ = ((*split_Xor_3_1_buffer_ptr_tail++));
        *Xor_3_1_to_Sbox_15_send_buffer++ = ((*split_Xor_3_1_buffer_ptr_tail++));
        *Xor_3_1_to_Sbox_16_send_buffer++ = ((*split_Xor_3_1_buffer_ptr_tail++));
        *Xor_3_1_to_Sbox_16_send_buffer++ = ((*split_Xor_3_1_buffer_ptr_tail++));
        *Xor_3_1_to_Sbox_9_buffer_ptr_head++ = ((*split_Xor_3_1_buffer_ptr_tail++));
        *Xor_3_1_to_Sbox_9_buffer_ptr_head++ = ((*split_Xor_3_1_buffer_ptr_tail++));
        *Xor_3_1_to_Sbox_10_send_buffer++ = ((*split_Xor_3_1_buffer_ptr_tail++));
        *Xor_3_1_to_Sbox_10_send_buffer++ = ((*split_Xor_3_1_buffer_ptr_tail++));
        *Xor_3_1_to_Sbox_11_buffer_ptr_head++ = ((*split_Xor_3_1_buffer_ptr_tail++));
        *Xor_3_1_to_Sbox_11_buffer_ptr_head++ = ((*split_Xor_3_1_buffer_ptr_tail++));
        *Xor_3_1_to_Sbox_12_4_buffer_ptr_head++ = ((*split_Xor_3_1_buffer_ptr_tail++));
        *Xor_3_1_to_Sbox_12_4_buffer_ptr_head++ = ((*split_Xor_3_1_buffer_ptr_tail++));
        *Xor_3_1_to_Sbox_13_buffer_ptr_head++ = ((*split_Xor_3_1_buffer_ptr_tail++));
        *Xor_3_1_to_Sbox_13_buffer_ptr_head++ = ((*split_Xor_3_1_buffer_ptr_tail++));
        *Xor_3_1_to_Sbox_14_send_buffer++ = ((*split_Xor_3_1_buffer_ptr_tail++));
        *Xor_3_1_to_Sbox_14_send_buffer++ = ((*split_Xor_3_1_buffer_ptr_tail++));
        *Xor_3_1_to_Sbox_15_send_buffer++ = ((*split_Xor_3_1_buffer_ptr_tail++));
        *Xor_3_1_to_Sbox_15_send_buffer++ = ((*split_Xor_3_1_buffer_ptr_tail++));
        *Xor_3_1_to_Sbox_16_send_buffer++ = ((*split_Xor_3_1_buffer_ptr_tail++));
        *Xor_3_1_to_Sbox_16_send_buffer++ = ((*split_Xor_3_1_buffer_ptr_tail++));
    }
    COREVA_HWACC_MISC_TRACE(7);  // SimpleMergeSplitBuffer split
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to Sbox_9
    steadyClusterDoneWrite(&Xor_3_1_to_Sbox_10_sender);
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to Sbox_10
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to Sbox_11
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to Sbox_12_1
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to Sbox_13
    steadyClusterDoneWrite(&Xor_3_1_to_Sbox_14_sender);
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to Sbox_14
    steadyClusterDoneWrite(&Xor_3_1_to_Sbox_15_sender);
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to Sbox_15
    steadyClusterDoneWrite(&Xor_3_1_to_Sbox_16_sender);
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to Sbox_16
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to Sbox_12_2
    steadyClusterDoneWrite(&Xor_3_1_to_Sbox_12_3_sender);
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to Sbox_12_3
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to Sbox_12_4
}

static void Xor_3_2_steady()
{
    COREVA_HWACC_MISC_TRACE(2);
    if(((doE_2_to_Xor_3_2_buffer_ptr_tail-doE_2_to_Xor_3_2_buffer)+192)>384+0)
        doE_2_to_Xor_3_2_buffer_ptr_tail=doE_2_to_Xor_3_2_buffer+0;
    COREVA_HWACC_MISC_TRACE(3);
    uint8_t* anonFilter3_2_to_Xor_3_2_receive_buffer = (uint8_t*)steadyClusterWaitRead(&anonFilter3_2_to_Xor_3_2_receiver, 192, sizeof(uint8_t), 0, 0, 0, 1);
    COREVA_HWACC_MISC_TRACE(3);
    uint8_t* merge_Xor_3_2_buffer_ptr_head=merge_Xor_3_2_buffer;
    // SimpleMergeSplitBuffer -> Merge
    for (unsigned int _i=0;_i<192;_i++)
    {
        *merge_Xor_3_2_buffer_ptr_head++ = ((*doE_2_to_Xor_3_2_buffer_ptr_tail++));
        *merge_Xor_3_2_buffer_ptr_head++ = ((*anonFilter3_2_to_Xor_3_2_receive_buffer++));
    }
    uint8_t* merge_Xor_3_2_buffer_ptr_tail=merge_Xor_3_2_buffer;
    COREVA_HWACC_MISC_TRACE(3);
    if(((Xor_3_2_to_Sbox_9_buffer_ptr_head-Xor_3_2_to_Sbox_9_buffer)+24)>48+0)
    {
        Xor_3_2_to_Sbox_9_buffer_ptr_head=Xor_3_2_to_Sbox_9_buffer+0;
    }
    COREVA_HWACC_MISC_TRACE(4);
    uint8_t* Xor_3_2_to_Sbox_10_send_buffer = (uint8_t*)steadyClusterWaitWrite(&Xor_3_2_to_Sbox_10_sender);
    COREVA_HWACC_MISC_TRACE(4);
    if(((Xor_3_2_to_Sbox_11_buffer_ptr_head-Xor_3_2_to_Sbox_11_buffer)+24)>48+0)
    {
        Xor_3_2_to_Sbox_11_buffer_ptr_head=Xor_3_2_to_Sbox_11_buffer+0;
    }
    COREVA_HWACC_MISC_TRACE(4);
    if(((Xor_3_2_to_Sbox_12_1_buffer_ptr_head-Xor_3_2_to_Sbox_12_1_buffer)+6)>12+0)
    {
        Xor_3_2_to_Sbox_12_1_buffer_ptr_head=Xor_3_2_to_Sbox_12_1_buffer+0;
    }
    COREVA_HWACC_MISC_TRACE(4);
    if(((Xor_3_2_to_Sbox_13_buffer_ptr_head-Xor_3_2_to_Sbox_13_buffer)+24)>48+0)
    {
        Xor_3_2_to_Sbox_13_buffer_ptr_head=Xor_3_2_to_Sbox_13_buffer+0;
    }
    COREVA_HWACC_MISC_TRACE(4);
    uint8_t* Xor_3_2_to_Sbox_14_send_buffer = (uint8_t*)steadyClusterWaitWrite(&Xor_3_2_to_Sbox_14_sender);
    COREVA_HWACC_MISC_TRACE(4);
    uint8_t* Xor_3_2_to_Sbox_15_send_buffer = (uint8_t*)steadyClusterWaitWrite(&Xor_3_2_to_Sbox_15_sender);
    COREVA_HWACC_MISC_TRACE(4);
    uint8_t* Xor_3_2_to_Sbox_16_send_buffer = (uint8_t*)steadyClusterWaitWrite(&Xor_3_2_to_Sbox_16_sender);
    COREVA_HWACC_MISC_TRACE(4);
    if(((Xor_3_2_to_Sbox_12_2_buffer_ptr_head-Xor_3_2_to_Sbox_12_2_buffer)+6)>12+0)
    {
        Xor_3_2_to_Sbox_12_2_buffer_ptr_head=Xor_3_2_to_Sbox_12_2_buffer+0;
    }
    COREVA_HWACC_MISC_TRACE(4);
    uint8_t* Xor_3_2_to_Sbox_12_3_send_buffer = (uint8_t*)steadyClusterWaitWrite(&Xor_3_2_to_Sbox_12_3_sender);
    COREVA_HWACC_MISC_TRACE(4);
    if(((Xor_3_2_to_Sbox_12_4_buffer_ptr_head-Xor_3_2_to_Sbox_12_4_buffer)+6)>12+0)
    {
        Xor_3_2_to_Sbox_12_4_buffer_ptr_head=Xor_3_2_to_Sbox_12_4_buffer+0;
    }
    COREVA_HWACC_MISC_TRACE(4);
    uint8_t* split_Xor_3_2_buffer_ptr_head=split_Xor_3_2_buffer;
    COREVA_HWACC_MISC_TRACE(4); // SteadyWaitWrite MergeBuffer split
    for (int _i=0; _i<192; _i++)
        Xor(2, &merge_Xor_3_2_buffer_ptr_tail, &split_Xor_3_2_buffer_ptr_head);
    COREVA_HWACC_MISC_TRACE(5);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from doE_2
    steadyClusterDoneRead(&anonFilter3_2_to_Xor_3_2_receiver);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from anonFilter3_2
    // SimpleMergeSplitBuffer -> Split
    uint8_t* split_Xor_3_2_buffer_ptr_tail=split_Xor_3_2_buffer;
    for (unsigned int _i=0;_i<3;_i++)
    {
        uint8_t _tmp;
        *Xor_3_2_to_Sbox_9_buffer_ptr_head++ = ((*split_Xor_3_2_buffer_ptr_tail++));
        *Xor_3_2_to_Sbox_9_buffer_ptr_head++ = ((*split_Xor_3_2_buffer_ptr_tail++));
        *Xor_3_2_to_Sbox_10_send_buffer++ = ((*split_Xor_3_2_buffer_ptr_tail++));
        *Xor_3_2_to_Sbox_10_send_buffer++ = ((*split_Xor_3_2_buffer_ptr_tail++));
        *Xor_3_2_to_Sbox_11_buffer_ptr_head++ = ((*split_Xor_3_2_buffer_ptr_tail++));
        *Xor_3_2_to_Sbox_11_buffer_ptr_head++ = ((*split_Xor_3_2_buffer_ptr_tail++));
        *Xor_3_2_to_Sbox_12_1_buffer_ptr_head++ = ((*split_Xor_3_2_buffer_ptr_tail++));
        *Xor_3_2_to_Sbox_12_1_buffer_ptr_head++ = ((*split_Xor_3_2_buffer_ptr_tail++));
        *Xor_3_2_to_Sbox_13_buffer_ptr_head++ = ((*split_Xor_3_2_buffer_ptr_tail++));
        *Xor_3_2_to_Sbox_13_buffer_ptr_head++ = ((*split_Xor_3_2_buffer_ptr_tail++));
        *Xor_3_2_to_Sbox_14_send_buffer++ = ((*split_Xor_3_2_buffer_ptr_tail++));
        *Xor_3_2_to_Sbox_14_send_buffer++ = ((*split_Xor_3_2_buffer_ptr_tail++));
        *Xor_3_2_to_Sbox_15_send_buffer++ = ((*split_Xor_3_2_buffer_ptr_tail++));
        *Xor_3_2_to_Sbox_15_send_buffer++ = ((*split_Xor_3_2_buffer_ptr_tail++));
        *Xor_3_2_to_Sbox_16_send_buffer++ = ((*split_Xor_3_2_buffer_ptr_tail++));
        *Xor_3_2_to_Sbox_16_send_buffer++ = ((*split_Xor_3_2_buffer_ptr_tail++));
        *Xor_3_2_to_Sbox_9_buffer_ptr_head++ = ((*split_Xor_3_2_buffer_ptr_tail++));
        *Xor_3_2_to_Sbox_9_buffer_ptr_head++ = ((*split_Xor_3_2_buffer_ptr_tail++));
        *Xor_3_2_to_Sbox_10_send_buffer++ = ((*split_Xor_3_2_buffer_ptr_tail++));
        *Xor_3_2_to_Sbox_10_send_buffer++ = ((*split_Xor_3_2_buffer_ptr_tail++));
        *Xor_3_2_to_Sbox_11_buffer_ptr_head++ = ((*split_Xor_3_2_buffer_ptr_tail++));
        *Xor_3_2_to_Sbox_11_buffer_ptr_head++ = ((*split_Xor_3_2_buffer_ptr_tail++));
        *Xor_3_2_to_Sbox_12_2_buffer_ptr_head++ = ((*split_Xor_3_2_buffer_ptr_tail++));
        *Xor_3_2_to_Sbox_12_2_buffer_ptr_head++ = ((*split_Xor_3_2_buffer_ptr_tail++));
        *Xor_3_2_to_Sbox_13_buffer_ptr_head++ = ((*split_Xor_3_2_buffer_ptr_tail++));
        *Xor_3_2_to_Sbox_13_buffer_ptr_head++ = ((*split_Xor_3_2_buffer_ptr_tail++));
        *Xor_3_2_to_Sbox_14_send_buffer++ = ((*split_Xor_3_2_buffer_ptr_tail++));
        *Xor_3_2_to_Sbox_14_send_buffer++ = ((*split_Xor_3_2_buffer_ptr_tail++));
        *Xor_3_2_to_Sbox_15_send_buffer++ = ((*split_Xor_3_2_buffer_ptr_tail++));
        *Xor_3_2_to_Sbox_15_send_buffer++ = ((*split_Xor_3_2_buffer_ptr_tail++));
        *Xor_3_2_to_Sbox_16_send_buffer++ = ((*split_Xor_3_2_buffer_ptr_tail++));
        *Xor_3_2_to_Sbox_16_send_buffer++ = ((*split_Xor_3_2_buffer_ptr_tail++));
        *Xor_3_2_to_Sbox_9_buffer_ptr_head++ = ((*split_Xor_3_2_buffer_ptr_tail++));
        *Xor_3_2_to_Sbox_9_buffer_ptr_head++ = ((*split_Xor_3_2_buffer_ptr_tail++));
        *Xor_3_2_to_Sbox_10_send_buffer++ = ((*split_Xor_3_2_buffer_ptr_tail++));
        *Xor_3_2_to_Sbox_10_send_buffer++ = ((*split_Xor_3_2_buffer_ptr_tail++));
        *Xor_3_2_to_Sbox_11_buffer_ptr_head++ = ((*split_Xor_3_2_buffer_ptr_tail++));
        *Xor_3_2_to_Sbox_11_buffer_ptr_head++ = ((*split_Xor_3_2_buffer_ptr_tail++));
        *Xor_3_2_to_Sbox_12_3_send_buffer++ = ((*split_Xor_3_2_buffer_ptr_tail++));
        *Xor_3_2_to_Sbox_12_3_send_buffer++ = ((*split_Xor_3_2_buffer_ptr_tail++));
        *Xor_3_2_to_Sbox_13_buffer_ptr_head++ = ((*split_Xor_3_2_buffer_ptr_tail++));
        *Xor_3_2_to_Sbox_13_buffer_ptr_head++ = ((*split_Xor_3_2_buffer_ptr_tail++));
        *Xor_3_2_to_Sbox_14_send_buffer++ = ((*split_Xor_3_2_buffer_ptr_tail++));
        *Xor_3_2_to_Sbox_14_send_buffer++ = ((*split_Xor_3_2_buffer_ptr_tail++));
        *Xor_3_2_to_Sbox_15_send_buffer++ = ((*split_Xor_3_2_buffer_ptr_tail++));
        *Xor_3_2_to_Sbox_15_send_buffer++ = ((*split_Xor_3_2_buffer_ptr_tail++));
        *Xor_3_2_to_Sbox_16_send_buffer++ = ((*split_Xor_3_2_buffer_ptr_tail++));
        *Xor_3_2_to_Sbox_16_send_buffer++ = ((*split_Xor_3_2_buffer_ptr_tail++));
        *Xor_3_2_to_Sbox_9_buffer_ptr_head++ = ((*split_Xor_3_2_buffer_ptr_tail++));
        *Xor_3_2_to_Sbox_9_buffer_ptr_head++ = ((*split_Xor_3_2_buffer_ptr_tail++));
        *Xor_3_2_to_Sbox_10_send_buffer++ = ((*split_Xor_3_2_buffer_ptr_tail++));
        *Xor_3_2_to_Sbox_10_send_buffer++ = ((*split_Xor_3_2_buffer_ptr_tail++));
        *Xor_3_2_to_Sbox_11_buffer_ptr_head++ = ((*split_Xor_3_2_buffer_ptr_tail++));
        *Xor_3_2_to_Sbox_11_buffer_ptr_head++ = ((*split_Xor_3_2_buffer_ptr_tail++));
        *Xor_3_2_to_Sbox_12_4_buffer_ptr_head++ = ((*split_Xor_3_2_buffer_ptr_tail++));
        *Xor_3_2_to_Sbox_12_4_buffer_ptr_head++ = ((*split_Xor_3_2_buffer_ptr_tail++));
        *Xor_3_2_to_Sbox_13_buffer_ptr_head++ = ((*split_Xor_3_2_buffer_ptr_tail++));
        *Xor_3_2_to_Sbox_13_buffer_ptr_head++ = ((*split_Xor_3_2_buffer_ptr_tail++));
        *Xor_3_2_to_Sbox_14_send_buffer++ = ((*split_Xor_3_2_buffer_ptr_tail++));
        *Xor_3_2_to_Sbox_14_send_buffer++ = ((*split_Xor_3_2_buffer_ptr_tail++));
        *Xor_3_2_to_Sbox_15_send_buffer++ = ((*split_Xor_3_2_buffer_ptr_tail++));
        *Xor_3_2_to_Sbox_15_send_buffer++ = ((*split_Xor_3_2_buffer_ptr_tail++));
        *Xor_3_2_to_Sbox_16_send_buffer++ = ((*split_Xor_3_2_buffer_ptr_tail++));
        *Xor_3_2_to_Sbox_16_send_buffer++ = ((*split_Xor_3_2_buffer_ptr_tail++));
    }
    COREVA_HWACC_MISC_TRACE(7);  // SimpleMergeSplitBuffer split
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to Sbox_9
    steadyClusterDoneWrite(&Xor_3_2_to_Sbox_10_sender);
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to Sbox_10
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to Sbox_11
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to Sbox_12_1
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to Sbox_13
    steadyClusterDoneWrite(&Xor_3_2_to_Sbox_14_sender);
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to Sbox_14
    steadyClusterDoneWrite(&Xor_3_2_to_Sbox_15_sender);
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to Sbox_15
    steadyClusterDoneWrite(&Xor_3_2_to_Sbox_16_sender);
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to Sbox_16
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to Sbox_12_2
    steadyClusterDoneWrite(&Xor_3_2_to_Sbox_12_3_sender);
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to Sbox_12_3
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to Sbox_12_4
}

static void Sbox_9_steady()
{
    COREVA_HWACC_MISC_TRACE(2);
    if(((Xor_3_1_to_Sbox_9_buffer_ptr_tail-Xor_3_1_to_Sbox_9_buffer)+24)>48+0)
        Xor_3_1_to_Sbox_9_buffer_ptr_tail=Xor_3_1_to_Sbox_9_buffer+0;
    COREVA_HWACC_MISC_TRACE(3);
    if(((Xor_3_2_to_Sbox_9_buffer_ptr_tail-Xor_3_2_to_Sbox_9_buffer)+24)>48+0)
        Xor_3_2_to_Sbox_9_buffer_ptr_tail=Xor_3_2_to_Sbox_9_buffer+0;
    COREVA_HWACC_MISC_TRACE(3);
    uint8_t* Xor_3_3_to_Sbox_9_receive_buffer = (uint8_t*)steadyClusterWaitRead(&Xor_3_3_to_Sbox_9_receiver, 24, sizeof(uint8_t), 0, 0, 0, 1);
    COREVA_HWACC_MISC_TRACE(3);
    uint8_t* merge_Sbox_9_buffer_ptr_head=merge_Sbox_9_buffer;
    // SimpleMergeSplitBuffer -> Merge
    for (unsigned int _i=0;_i<3;_i++)
    {
        *merge_Sbox_9_buffer_ptr_head++ = ((*Xor_3_1_to_Sbox_9_buffer_ptr_tail++));
        *merge_Sbox_9_buffer_ptr_head++ = ((*Xor_3_2_to_Sbox_9_buffer_ptr_tail++));
        *merge_Sbox_9_buffer_ptr_head++ = ((*Xor_3_3_to_Sbox_9_receive_buffer++));
        *merge_Sbox_9_buffer_ptr_head++ = ((*Xor_3_1_to_Sbox_9_buffer_ptr_tail++));
        *merge_Sbox_9_buffer_ptr_head++ = ((*Xor_3_2_to_Sbox_9_buffer_ptr_tail++));
        *merge_Sbox_9_buffer_ptr_head++ = ((*Xor_3_3_to_Sbox_9_receive_buffer++));
        *merge_Sbox_9_buffer_ptr_head++ = ((*Xor_3_1_to_Sbox_9_buffer_ptr_tail++));
        *merge_Sbox_9_buffer_ptr_head++ = ((*Xor_3_2_to_Sbox_9_buffer_ptr_tail++));
        *merge_Sbox_9_buffer_ptr_head++ = ((*Xor_3_3_to_Sbox_9_receive_buffer++));
        *merge_Sbox_9_buffer_ptr_head++ = ((*Xor_3_1_to_Sbox_9_buffer_ptr_tail++));
        *merge_Sbox_9_buffer_ptr_head++ = ((*Xor_3_2_to_Sbox_9_buffer_ptr_tail++));
        *merge_Sbox_9_buffer_ptr_head++ = ((*Xor_3_3_to_Sbox_9_receive_buffer++));
        *merge_Sbox_9_buffer_ptr_head++ = ((*Xor_3_1_to_Sbox_9_buffer_ptr_tail++));
        *merge_Sbox_9_buffer_ptr_head++ = ((*Xor_3_2_to_Sbox_9_buffer_ptr_tail++));
        *merge_Sbox_9_buffer_ptr_head++ = ((*Xor_3_3_to_Sbox_9_receive_buffer++));
        *merge_Sbox_9_buffer_ptr_head++ = ((*Xor_3_1_to_Sbox_9_buffer_ptr_tail++));
        *merge_Sbox_9_buffer_ptr_head++ = ((*Xor_3_2_to_Sbox_9_buffer_ptr_tail++));
        *merge_Sbox_9_buffer_ptr_head++ = ((*Xor_3_3_to_Sbox_9_receive_buffer++));
        *merge_Sbox_9_buffer_ptr_head++ = ((*Xor_3_1_to_Sbox_9_buffer_ptr_tail++));
        *merge_Sbox_9_buffer_ptr_head++ = ((*Xor_3_2_to_Sbox_9_buffer_ptr_tail++));
        *merge_Sbox_9_buffer_ptr_head++ = ((*Xor_3_3_to_Sbox_9_receive_buffer++));
        *merge_Sbox_9_buffer_ptr_head++ = ((*Xor_3_1_to_Sbox_9_buffer_ptr_tail++));
        *merge_Sbox_9_buffer_ptr_head++ = ((*Xor_3_2_to_Sbox_9_buffer_ptr_tail++));
        *merge_Sbox_9_buffer_ptr_head++ = ((*Xor_3_3_to_Sbox_9_receive_buffer++));
    }
    uint8_t* merge_Sbox_9_buffer_ptr_tail=merge_Sbox_9_buffer;
    COREVA_HWACC_MISC_TRACE(3);
    uint8_t* Sbox_9_to_doP_2_send_buffer = (uint8_t*)steadyClusterWaitWrite(&Sbox_9_to_doP_2_sender);
    COREVA_HWACC_MISC_TRACE(4);
    for (int _i=0; _i<12; _i++)
        Sbox(Sbox_9_table, &merge_Sbox_9_buffer_ptr_tail, &Sbox_9_to_doP_2_send_buffer);
    COREVA_HWACC_MISC_TRACE(5);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from Xor_3_1
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from Xor_3_2
    steadyClusterDoneRead(&Xor_3_3_to_Sbox_9_receiver);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from Xor_3_3
    steadyClusterDoneWrite(&Sbox_9_to_doP_2_sender);
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to doP_2
}

static void Sbox_11_steady()
{
    COREVA_HWACC_MISC_TRACE(2);
    if(((Xor_3_1_to_Sbox_11_buffer_ptr_tail-Xor_3_1_to_Sbox_11_buffer)+24)>48+0)
        Xor_3_1_to_Sbox_11_buffer_ptr_tail=Xor_3_1_to_Sbox_11_buffer+0;
    COREVA_HWACC_MISC_TRACE(3);
    if(((Xor_3_2_to_Sbox_11_buffer_ptr_tail-Xor_3_2_to_Sbox_11_buffer)+24)>48+0)
        Xor_3_2_to_Sbox_11_buffer_ptr_tail=Xor_3_2_to_Sbox_11_buffer+0;
    COREVA_HWACC_MISC_TRACE(3);
    uint8_t* Xor_3_3_to_Sbox_11_receive_buffer = (uint8_t*)steadyClusterWaitRead(&Xor_3_3_to_Sbox_11_receiver, 24, sizeof(uint8_t), 0, 0, 0, 1);
    COREVA_HWACC_MISC_TRACE(3);
    uint8_t* merge_Sbox_11_buffer_ptr_head=merge_Sbox_11_buffer;
    // SimpleMergeSplitBuffer -> Merge
    for (unsigned int _i=0;_i<3;_i++)
    {
        *merge_Sbox_11_buffer_ptr_head++ = ((*Xor_3_1_to_Sbox_11_buffer_ptr_tail++));
        *merge_Sbox_11_buffer_ptr_head++ = ((*Xor_3_2_to_Sbox_11_buffer_ptr_tail++));
        *merge_Sbox_11_buffer_ptr_head++ = ((*Xor_3_3_to_Sbox_11_receive_buffer++));
        *merge_Sbox_11_buffer_ptr_head++ = ((*Xor_3_1_to_Sbox_11_buffer_ptr_tail++));
        *merge_Sbox_11_buffer_ptr_head++ = ((*Xor_3_2_to_Sbox_11_buffer_ptr_tail++));
        *merge_Sbox_11_buffer_ptr_head++ = ((*Xor_3_3_to_Sbox_11_receive_buffer++));
        *merge_Sbox_11_buffer_ptr_head++ = ((*Xor_3_1_to_Sbox_11_buffer_ptr_tail++));
        *merge_Sbox_11_buffer_ptr_head++ = ((*Xor_3_2_to_Sbox_11_buffer_ptr_tail++));
        *merge_Sbox_11_buffer_ptr_head++ = ((*Xor_3_3_to_Sbox_11_receive_buffer++));
        *merge_Sbox_11_buffer_ptr_head++ = ((*Xor_3_1_to_Sbox_11_buffer_ptr_tail++));
        *merge_Sbox_11_buffer_ptr_head++ = ((*Xor_3_2_to_Sbox_11_buffer_ptr_tail++));
        *merge_Sbox_11_buffer_ptr_head++ = ((*Xor_3_3_to_Sbox_11_receive_buffer++));
        *merge_Sbox_11_buffer_ptr_head++ = ((*Xor_3_1_to_Sbox_11_buffer_ptr_tail++));
        *merge_Sbox_11_buffer_ptr_head++ = ((*Xor_3_2_to_Sbox_11_buffer_ptr_tail++));
        *merge_Sbox_11_buffer_ptr_head++ = ((*Xor_3_3_to_Sbox_11_receive_buffer++));
        *merge_Sbox_11_buffer_ptr_head++ = ((*Xor_3_1_to_Sbox_11_buffer_ptr_tail++));
        *merge_Sbox_11_buffer_ptr_head++ = ((*Xor_3_2_to_Sbox_11_buffer_ptr_tail++));
        *merge_Sbox_11_buffer_ptr_head++ = ((*Xor_3_3_to_Sbox_11_receive_buffer++));
        *merge_Sbox_11_buffer_ptr_head++ = ((*Xor_3_1_to_Sbox_11_buffer_ptr_tail++));
        *merge_Sbox_11_buffer_ptr_head++ = ((*Xor_3_2_to_Sbox_11_buffer_ptr_tail++));
        *merge_Sbox_11_buffer_ptr_head++ = ((*Xor_3_3_to_Sbox_11_receive_buffer++));
        *merge_Sbox_11_buffer_ptr_head++ = ((*Xor_3_1_to_Sbox_11_buffer_ptr_tail++));
        *merge_Sbox_11_buffer_ptr_head++ = ((*Xor_3_2_to_Sbox_11_buffer_ptr_tail++));
        *merge_Sbox_11_buffer_ptr_head++ = ((*Xor_3_3_to_Sbox_11_receive_buffer++));
    }
    uint8_t* merge_Sbox_11_buffer_ptr_tail=merge_Sbox_11_buffer;
    COREVA_HWACC_MISC_TRACE(3);
    uint8_t* Sbox_11_to_doP_2_send_buffer = (uint8_t*)steadyClusterWaitWrite(&Sbox_11_to_doP_2_sender);
    COREVA_HWACC_MISC_TRACE(4);
    for (int _i=0; _i<12; _i++)
        Sbox(Sbox_11_table, &merge_Sbox_11_buffer_ptr_tail, &Sbox_11_to_doP_2_send_buffer);
    COREVA_HWACC_MISC_TRACE(5);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from Xor_3_1
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from Xor_3_2
    steadyClusterDoneRead(&Xor_3_3_to_Sbox_11_receiver);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from Xor_3_3
    steadyClusterDoneWrite(&Sbox_11_to_doP_2_sender);
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to doP_2
}

static void Sbox_12_1_steady()
{
    COREVA_HWACC_MISC_TRACE(2);
    if(((Xor_3_1_to_Sbox_12_1_buffer_ptr_tail-Xor_3_1_to_Sbox_12_1_buffer)+6)>12+0)
        Xor_3_1_to_Sbox_12_1_buffer_ptr_tail=Xor_3_1_to_Sbox_12_1_buffer+0;
    COREVA_HWACC_MISC_TRACE(3);
    if(((Xor_3_2_to_Sbox_12_1_buffer_ptr_tail-Xor_3_2_to_Sbox_12_1_buffer)+6)>12+0)
        Xor_3_2_to_Sbox_12_1_buffer_ptr_tail=Xor_3_2_to_Sbox_12_1_buffer+0;
    COREVA_HWACC_MISC_TRACE(3);
    uint8_t* Xor_3_3_to_Sbox_12_1_receive_buffer = (uint8_t*)steadyClusterWaitRead(&Xor_3_3_to_Sbox_12_1_receiver, 6, sizeof(uint8_t), 0, 0, 0, 1);
    COREVA_HWACC_MISC_TRACE(3);
    uint8_t* merge_Sbox_12_1_buffer_ptr_head=merge_Sbox_12_1_buffer;
    // SimpleMergeSplitBuffer -> Merge
    for (unsigned int _i=0;_i<3;_i++)
    {
        *merge_Sbox_12_1_buffer_ptr_head++ = ((*Xor_3_1_to_Sbox_12_1_buffer_ptr_tail++));
        *merge_Sbox_12_1_buffer_ptr_head++ = ((*Xor_3_2_to_Sbox_12_1_buffer_ptr_tail++));
        *merge_Sbox_12_1_buffer_ptr_head++ = ((*Xor_3_3_to_Sbox_12_1_receive_buffer++));
        *merge_Sbox_12_1_buffer_ptr_head++ = ((*Xor_3_1_to_Sbox_12_1_buffer_ptr_tail++));
        *merge_Sbox_12_1_buffer_ptr_head++ = ((*Xor_3_2_to_Sbox_12_1_buffer_ptr_tail++));
        *merge_Sbox_12_1_buffer_ptr_head++ = ((*Xor_3_3_to_Sbox_12_1_receive_buffer++));
    }
    uint8_t* merge_Sbox_12_1_buffer_ptr_tail=merge_Sbox_12_1_buffer;
    COREVA_HWACC_MISC_TRACE(3);
    uint8_t* Sbox_12_1_to_doP_2_send_buffer = (uint8_t*)steadyClusterWaitWrite(&Sbox_12_1_to_doP_2_sender);
    COREVA_HWACC_MISC_TRACE(4);
    for (int _i=0; _i<3; _i++)
        Sbox(Sbox_12_1_table, &merge_Sbox_12_1_buffer_ptr_tail, &Sbox_12_1_to_doP_2_send_buffer);
    COREVA_HWACC_MISC_TRACE(5);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from Xor_3_1
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from Xor_3_2
    steadyClusterDoneRead(&Xor_3_3_to_Sbox_12_1_receiver);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from Xor_3_3
    steadyClusterDoneWrite(&Sbox_12_1_to_doP_2_sender);
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to doP_2
}

static void Sbox_12_2_steady()
{
    COREVA_HWACC_MISC_TRACE(2);
    if(((Xor_3_1_to_Sbox_12_2_buffer_ptr_tail-Xor_3_1_to_Sbox_12_2_buffer)+6)>12+0)
        Xor_3_1_to_Sbox_12_2_buffer_ptr_tail=Xor_3_1_to_Sbox_12_2_buffer+0;
    COREVA_HWACC_MISC_TRACE(3);
    if(((Xor_3_2_to_Sbox_12_2_buffer_ptr_tail-Xor_3_2_to_Sbox_12_2_buffer)+6)>12+0)
        Xor_3_2_to_Sbox_12_2_buffer_ptr_tail=Xor_3_2_to_Sbox_12_2_buffer+0;
    COREVA_HWACC_MISC_TRACE(3);
    uint8_t* Xor_3_3_to_Sbox_12_2_receive_buffer = (uint8_t*)steadyClusterWaitRead(&Xor_3_3_to_Sbox_12_2_receiver, 6, sizeof(uint8_t), 0, 0, 0, 1);
    COREVA_HWACC_MISC_TRACE(3);
    uint8_t* merge_Sbox_12_2_buffer_ptr_head=merge_Sbox_12_2_buffer;
    // SimpleMergeSplitBuffer -> Merge
    for (unsigned int _i=0;_i<3;_i++)
    {
        *merge_Sbox_12_2_buffer_ptr_head++ = ((*Xor_3_1_to_Sbox_12_2_buffer_ptr_tail++));
        *merge_Sbox_12_2_buffer_ptr_head++ = ((*Xor_3_2_to_Sbox_12_2_buffer_ptr_tail++));
        *merge_Sbox_12_2_buffer_ptr_head++ = ((*Xor_3_3_to_Sbox_12_2_receive_buffer++));
        *merge_Sbox_12_2_buffer_ptr_head++ = ((*Xor_3_1_to_Sbox_12_2_buffer_ptr_tail++));
        *merge_Sbox_12_2_buffer_ptr_head++ = ((*Xor_3_2_to_Sbox_12_2_buffer_ptr_tail++));
        *merge_Sbox_12_2_buffer_ptr_head++ = ((*Xor_3_3_to_Sbox_12_2_receive_buffer++));
    }
    uint8_t* merge_Sbox_12_2_buffer_ptr_tail=merge_Sbox_12_2_buffer;
    COREVA_HWACC_MISC_TRACE(3);
    uint8_t* Sbox_12_2_to_doP_2_send_buffer = (uint8_t*)steadyClusterWaitWrite(&Sbox_12_2_to_doP_2_sender);
    COREVA_HWACC_MISC_TRACE(4);
    for (int _i=0; _i<3; _i++)
        Sbox(Sbox_12_2_table, &merge_Sbox_12_2_buffer_ptr_tail, &Sbox_12_2_to_doP_2_send_buffer);
    COREVA_HWACC_MISC_TRACE(5);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from Xor_3_1
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from Xor_3_2
    steadyClusterDoneRead(&Xor_3_3_to_Sbox_12_2_receiver);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from Xor_3_3
    steadyClusterDoneWrite(&Sbox_12_2_to_doP_2_sender);
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to doP_2
}

static void Sbox_12_4_steady()
{
    COREVA_HWACC_MISC_TRACE(2);
    if(((Xor_3_1_to_Sbox_12_4_buffer_ptr_tail-Xor_3_1_to_Sbox_12_4_buffer)+6)>12+0)
        Xor_3_1_to_Sbox_12_4_buffer_ptr_tail=Xor_3_1_to_Sbox_12_4_buffer+0;
    COREVA_HWACC_MISC_TRACE(3);
    if(((Xor_3_2_to_Sbox_12_4_buffer_ptr_tail-Xor_3_2_to_Sbox_12_4_buffer)+6)>12+0)
        Xor_3_2_to_Sbox_12_4_buffer_ptr_tail=Xor_3_2_to_Sbox_12_4_buffer+0;
    COREVA_HWACC_MISC_TRACE(3);
    uint8_t* Xor_3_3_to_Sbox_12_4_receive_buffer = (uint8_t*)steadyClusterWaitRead(&Xor_3_3_to_Sbox_12_4_receiver, 6, sizeof(uint8_t), 0, 0, 0, 1);
    COREVA_HWACC_MISC_TRACE(3);
    uint8_t* merge_Sbox_12_4_buffer_ptr_head=merge_Sbox_12_4_buffer;
    // SimpleMergeSplitBuffer -> Merge
    for (unsigned int _i=0;_i<3;_i++)
    {
        *merge_Sbox_12_4_buffer_ptr_head++ = ((*Xor_3_1_to_Sbox_12_4_buffer_ptr_tail++));
        *merge_Sbox_12_4_buffer_ptr_head++ = ((*Xor_3_2_to_Sbox_12_4_buffer_ptr_tail++));
        *merge_Sbox_12_4_buffer_ptr_head++ = ((*Xor_3_3_to_Sbox_12_4_receive_buffer++));
        *merge_Sbox_12_4_buffer_ptr_head++ = ((*Xor_3_1_to_Sbox_12_4_buffer_ptr_tail++));
        *merge_Sbox_12_4_buffer_ptr_head++ = ((*Xor_3_2_to_Sbox_12_4_buffer_ptr_tail++));
        *merge_Sbox_12_4_buffer_ptr_head++ = ((*Xor_3_3_to_Sbox_12_4_receive_buffer++));
    }
    uint8_t* merge_Sbox_12_4_buffer_ptr_tail=merge_Sbox_12_4_buffer;
    COREVA_HWACC_MISC_TRACE(3);
    uint8_t* Sbox_12_4_to_doP_2_send_buffer = (uint8_t*)steadyClusterWaitWrite(&Sbox_12_4_to_doP_2_sender);
    COREVA_HWACC_MISC_TRACE(4);
    for (int _i=0; _i<3; _i++)
        Sbox(Sbox_12_4_table, &merge_Sbox_12_4_buffer_ptr_tail, &Sbox_12_4_to_doP_2_send_buffer);
    COREVA_HWACC_MISC_TRACE(5);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from Xor_3_1
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from Xor_3_2
    steadyClusterDoneRead(&Xor_3_3_to_Sbox_12_4_receiver);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from Xor_3_3
    steadyClusterDoneWrite(&Sbox_12_4_to_doP_2_sender);
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to doP_2
}

static void Sbox_13_steady()
{
    COREVA_HWACC_MISC_TRACE(2);
    if(((Xor_3_1_to_Sbox_13_buffer_ptr_tail-Xor_3_1_to_Sbox_13_buffer)+24)>48+0)
        Xor_3_1_to_Sbox_13_buffer_ptr_tail=Xor_3_1_to_Sbox_13_buffer+0;
    COREVA_HWACC_MISC_TRACE(3);
    if(((Xor_3_2_to_Sbox_13_buffer_ptr_tail-Xor_3_2_to_Sbox_13_buffer)+24)>48+0)
        Xor_3_2_to_Sbox_13_buffer_ptr_tail=Xor_3_2_to_Sbox_13_buffer+0;
    COREVA_HWACC_MISC_TRACE(3);
    uint8_t* Xor_3_3_to_Sbox_13_receive_buffer = (uint8_t*)steadyClusterWaitRead(&Xor_3_3_to_Sbox_13_receiver, 24, sizeof(uint8_t), 0, 0, 0, 1);
    COREVA_HWACC_MISC_TRACE(3);
    uint8_t* merge_Sbox_13_buffer_ptr_head=merge_Sbox_13_buffer;
    // SimpleMergeSplitBuffer -> Merge
    for (unsigned int _i=0;_i<3;_i++)
    {
        *merge_Sbox_13_buffer_ptr_head++ = ((*Xor_3_1_to_Sbox_13_buffer_ptr_tail++));
        *merge_Sbox_13_buffer_ptr_head++ = ((*Xor_3_2_to_Sbox_13_buffer_ptr_tail++));
        *merge_Sbox_13_buffer_ptr_head++ = ((*Xor_3_3_to_Sbox_13_receive_buffer++));
        *merge_Sbox_13_buffer_ptr_head++ = ((*Xor_3_1_to_Sbox_13_buffer_ptr_tail++));
        *merge_Sbox_13_buffer_ptr_head++ = ((*Xor_3_2_to_Sbox_13_buffer_ptr_tail++));
        *merge_Sbox_13_buffer_ptr_head++ = ((*Xor_3_3_to_Sbox_13_receive_buffer++));
        *merge_Sbox_13_buffer_ptr_head++ = ((*Xor_3_1_to_Sbox_13_buffer_ptr_tail++));
        *merge_Sbox_13_buffer_ptr_head++ = ((*Xor_3_2_to_Sbox_13_buffer_ptr_tail++));
        *merge_Sbox_13_buffer_ptr_head++ = ((*Xor_3_3_to_Sbox_13_receive_buffer++));
        *merge_Sbox_13_buffer_ptr_head++ = ((*Xor_3_1_to_Sbox_13_buffer_ptr_tail++));
        *merge_Sbox_13_buffer_ptr_head++ = ((*Xor_3_2_to_Sbox_13_buffer_ptr_tail++));
        *merge_Sbox_13_buffer_ptr_head++ = ((*Xor_3_3_to_Sbox_13_receive_buffer++));
        *merge_Sbox_13_buffer_ptr_head++ = ((*Xor_3_1_to_Sbox_13_buffer_ptr_tail++));
        *merge_Sbox_13_buffer_ptr_head++ = ((*Xor_3_2_to_Sbox_13_buffer_ptr_tail++));
        *merge_Sbox_13_buffer_ptr_head++ = ((*Xor_3_3_to_Sbox_13_receive_buffer++));
        *merge_Sbox_13_buffer_ptr_head++ = ((*Xor_3_1_to_Sbox_13_buffer_ptr_tail++));
        *merge_Sbox_13_buffer_ptr_head++ = ((*Xor_3_2_to_Sbox_13_buffer_ptr_tail++));
        *merge_Sbox_13_buffer_ptr_head++ = ((*Xor_3_3_to_Sbox_13_receive_buffer++));
        *merge_Sbox_13_buffer_ptr_head++ = ((*Xor_3_1_to_Sbox_13_buffer_ptr_tail++));
        *merge_Sbox_13_buffer_ptr_head++ = ((*Xor_3_2_to_Sbox_13_buffer_ptr_tail++));
        *merge_Sbox_13_buffer_ptr_head++ = ((*Xor_3_3_to_Sbox_13_receive_buffer++));
        *merge_Sbox_13_buffer_ptr_head++ = ((*Xor_3_1_to_Sbox_13_buffer_ptr_tail++));
        *merge_Sbox_13_buffer_ptr_head++ = ((*Xor_3_2_to_Sbox_13_buffer_ptr_tail++));
        *merge_Sbox_13_buffer_ptr_head++ = ((*Xor_3_3_to_Sbox_13_receive_buffer++));
    }
    uint8_t* merge_Sbox_13_buffer_ptr_tail=merge_Sbox_13_buffer;
    COREVA_HWACC_MISC_TRACE(3);
    uint8_t* Sbox_13_to_doP_2_send_buffer = (uint8_t*)steadyClusterWaitWrite(&Sbox_13_to_doP_2_sender);
    COREVA_HWACC_MISC_TRACE(4);
    for (int _i=0; _i<12; _i++)
        Sbox(Sbox_13_table, &merge_Sbox_13_buffer_ptr_tail, &Sbox_13_to_doP_2_send_buffer);
    COREVA_HWACC_MISC_TRACE(5);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from Xor_3_1
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from Xor_3_2
    steadyClusterDoneRead(&Xor_3_3_to_Sbox_13_receiver);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from Xor_3_3
    steadyClusterDoneWrite(&Sbox_13_to_doP_2_sender);
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to doP_2
}

static void Identity_3_steady()
{
    COREVA_HWACC_MISC_TRACE(2);
    uint8_t* Identity_2_to_Identity_3_receive_buffer = (uint8_t*)steadyClusterWaitRead(&Identity_2_to_Identity_3_receiver, 384, sizeof(uint8_t), 0, 0, 0, 1);
    COREVA_HWACC_MISC_TRACE(3);
    uint8_t* Identity_3_to_Xor_4_send_buffer = (uint8_t*)steadyClusterWaitWrite(&Identity_3_to_Xor_4_sender);
    COREVA_HWACC_MISC_TRACE(4);
    for (int _i=0; _i<384; _i++)
        Identity(&Identity_2_to_Identity_3_receive_buffer, &Identity_3_to_Xor_4_send_buffer);
    COREVA_HWACC_MISC_TRACE(5);
    steadyClusterDoneRead(&Identity_2_to_Identity_3_receiver);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from Identity_2
    steadyClusterDoneWrite(&Identity_3_to_Xor_4_sender);
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to Xor_4
}

static void doE_3_1_steady()
{
    COREVA_HWACC_MISC_TRACE(2);
    uint8_t* Xor_4_to_doE_3_1_receive_buffer = (uint8_t*)steadyClusterWaitRead(&Xor_4_to_doE_3_1_receiver, 192, sizeof(uint8_t), 0, 0, 0, 1);
    COREVA_HWACC_MISC_TRACE(3);
    uint8_t* doE_3_1_to_Xor_5_send_buffer = (uint8_t*)steadyClusterWaitWrite(&doE_3_1_to_Xor_5_sender);
    COREVA_HWACC_MISC_TRACE(4);
    for (int _i=0; _i<6; _i++)
        doE(&Xor_4_to_doE_3_1_receive_buffer, &doE_3_1_to_Xor_5_send_buffer);
    COREVA_HWACC_MISC_TRACE(5);
    steadyClusterDoneRead(&Xor_4_to_doE_3_1_receiver);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from Xor_4
    steadyClusterDoneWrite(&doE_3_1_to_Xor_5_sender);
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to Xor_5
}

static void Sbox_17_steady()
{
    COREVA_HWACC_MISC_TRACE(2);
    uint8_t* Xor_5_to_Sbox_17_receive_buffer = (uint8_t*)steadyClusterWaitRead(&Xor_5_to_Sbox_17_receiver, 72, sizeof(uint8_t), 0, 0, 0, 1);
    COREVA_HWACC_MISC_TRACE(3);
    uint8_t* Sbox_17_to_doP_3_send_buffer = (uint8_t*)steadyClusterWaitWrite(&Sbox_17_to_doP_3_sender);
    COREVA_HWACC_MISC_TRACE(4);
    for (int _i=0; _i<12; _i++)
        Sbox(Sbox_17_table, &Xor_5_to_Sbox_17_receive_buffer, &Sbox_17_to_doP_3_send_buffer);
    COREVA_HWACC_MISC_TRACE(5);
    steadyClusterDoneRead(&Xor_5_to_Sbox_17_receiver);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from Xor_5
    steadyClusterDoneWrite(&Sbox_17_to_doP_3_sender);
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to doP_3
}

static void Sbox_20_steady()
{
    COREVA_HWACC_MISC_TRACE(2);
    uint8_t* Xor_5_to_Sbox_20_receive_buffer = (uint8_t*)steadyClusterWaitRead(&Xor_5_to_Sbox_20_receiver, 72, sizeof(uint8_t), 0, 0, 0, 1);
    COREVA_HWACC_MISC_TRACE(3);
    uint8_t* Sbox_20_to_doP_3_send_buffer = (uint8_t*)steadyClusterWaitWrite(&Sbox_20_to_doP_3_sender);
    COREVA_HWACC_MISC_TRACE(4);
    for (int _i=0; _i<12; _i++)
        Sbox(Sbox_20_table, &Xor_5_to_Sbox_20_receive_buffer, &Sbox_20_to_doP_3_send_buffer);
    COREVA_HWACC_MISC_TRACE(5);
    steadyClusterDoneRead(&Xor_5_to_Sbox_20_receiver);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from Xor_5
    steadyClusterDoneWrite(&Sbox_20_to_doP_3_sender);
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to doP_3
}

static void Sbox_21_steady()
{
    COREVA_HWACC_MISC_TRACE(2);
    uint8_t* Xor_5_to_Sbox_21_receive_buffer = (uint8_t*)steadyClusterWaitRead(&Xor_5_to_Sbox_21_receiver, 72, sizeof(uint8_t), 0, 0, 0, 1);
    COREVA_HWACC_MISC_TRACE(3);
    uint8_t* Sbox_21_to_doP_3_send_buffer = (uint8_t*)steadyClusterWaitWrite(&Sbox_21_to_doP_3_sender);
    COREVA_HWACC_MISC_TRACE(4);
    for (int _i=0; _i<12; _i++)
        Sbox(Sbox_21_table, &Xor_5_to_Sbox_21_receive_buffer, &Sbox_21_to_doP_3_send_buffer);
    COREVA_HWACC_MISC_TRACE(5);
    steadyClusterDoneRead(&Xor_5_to_Sbox_21_receiver);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from Xor_5
    steadyClusterDoneWrite(&Sbox_21_to_doP_3_sender);
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to doP_3
}

static void Sbox_22_steady()
{
    COREVA_HWACC_MISC_TRACE(2);
    uint8_t* Xor_5_to_Sbox_22_receive_buffer = (uint8_t*)steadyClusterWaitRead(&Xor_5_to_Sbox_22_receiver, 72, sizeof(uint8_t), 0, 0, 0, 1);
    COREVA_HWACC_MISC_TRACE(3);
    uint8_t* Sbox_22_to_doP_3_send_buffer = (uint8_t*)steadyClusterWaitWrite(&Sbox_22_to_doP_3_sender);
    COREVA_HWACC_MISC_TRACE(4);
    for (int _i=0; _i<12; _i++)
        Sbox(Sbox_22_table, &Xor_5_to_Sbox_22_receive_buffer, &Sbox_22_to_doP_3_send_buffer);
    COREVA_HWACC_MISC_TRACE(5);
    steadyClusterDoneRead(&Xor_5_to_Sbox_22_receiver);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from Xor_5
    steadyClusterDoneWrite(&Sbox_22_to_doP_3_sender);
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to doP_3
}

static void Identity_6_steady()
{
    COREVA_HWACC_MISC_TRACE(2);
    uint8_t* Xor_4_to_Identity_6_receive_buffer = (uint8_t*)steadyClusterWaitRead(&Xor_4_to_Identity_6_receiver, 384, sizeof(uint8_t), 0, 0, 0, 1);
    COREVA_HWACC_MISC_TRACE(3);
    if(((Identity_6_to_Identity_7_buffer_ptr_head-Identity_6_to_Identity_7_buffer)+384)>768+0)
    {
        Identity_6_to_Identity_7_buffer_ptr_head=Identity_6_to_Identity_7_buffer+0;
    }
    COREVA_HWACC_MISC_TRACE(4);
    if(((Identity_6_to_anonFilter0_4_buffer_ptr_head-Identity_6_to_anonFilter0_4_buffer)+384)>3072+0)
    {
        Identity_6_to_anonFilter0_4_buffer_ptr_head=Identity_6_to_anonFilter0_4_buffer+0;
    }
    COREVA_HWACC_MISC_TRACE(4);
    uint8_t* split_Identity_6_buffer_ptr_head=split_Identity_6_buffer;
    COREVA_HWACC_MISC_TRACE(4); // SteadyWaitWrite MergeBuffer split
    for (int _i=0; _i<384; _i++)
        Identity(&Xor_4_to_Identity_6_receive_buffer, &split_Identity_6_buffer_ptr_head);
    COREVA_HWACC_MISC_TRACE(5);
    steadyClusterDoneRead(&Xor_4_to_Identity_6_receiver);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from Xor_4
    // SimpleMergeSplitBuffer -> Split
    uint8_t* split_Identity_6_buffer_ptr_tail=split_Identity_6_buffer;
    for (unsigned int _i=0;_i<384;_i++)
    {
        uint8_t _tmp;
        _tmp = (*split_Identity_6_buffer_ptr_tail++);
        *Identity_6_to_Identity_7_buffer_ptr_head++ = (_tmp);
        *Identity_6_to_anonFilter0_4_buffer_ptr_head++ = (_tmp);
    }
    COREVA_HWACC_MISC_TRACE(7);  // SimpleMergeSplitBuffer split
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to Identity_7
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to anonFilter0_4
}

static void anonFilter0_3_steady()
{
    COREVA_HWACC_MISC_TRACE(2);
    uint8_t* Identity_4_to_anonFilter0_3_receive_buffer = (uint8_t*)steadyClusterWaitRead(&Identity_4_to_anonFilter0_3_receiver, 384, sizeof(uint8_t), 0, 0, 0, 1);
    COREVA_HWACC_MISC_TRACE(3);
    for (int _i=0; _i<384; _i++)
        anonFilter0(&Identity_4_to_anonFilter0_3_receive_buffer);
    COREVA_HWACC_MISC_TRACE(5);
    steadyClusterDoneRead(&Identity_4_to_anonFilter0_3_receiver);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from Identity_4
}

static void Sbox_27_steady()
{
    COREVA_HWACC_MISC_TRACE(2);
    uint8_t* Xor_7_to_Sbox_27_receive_buffer = (uint8_t*)steadyClusterWaitRead(&Xor_7_to_Sbox_27_receiver, 72, sizeof(uint8_t), 0, 0, 0, 1);
    COREVA_HWACC_MISC_TRACE(3);
    uint8_t* Sbox_27_to_doP_4_send_buffer = (uint8_t*)steadyClusterWaitWrite(&Sbox_27_to_doP_4_sender);
    COREVA_HWACC_MISC_TRACE(4);
    for (int _i=0; _i<12; _i++)
        Sbox(Sbox_27_table, &Xor_7_to_Sbox_27_receive_buffer, &Sbox_27_to_doP_4_send_buffer);
    COREVA_HWACC_MISC_TRACE(5);
    steadyClusterDoneRead(&Xor_7_to_Sbox_27_receiver);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from Xor_7
    steadyClusterDoneWrite(&Sbox_27_to_doP_4_sender);
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to doP_4
}

static void Sbox_28_steady()
{
    COREVA_HWACC_MISC_TRACE(2);
    uint8_t* Xor_7_to_Sbox_28_receive_buffer = (uint8_t*)steadyClusterWaitRead(&Xor_7_to_Sbox_28_receiver, 72, sizeof(uint8_t), 0, 0, 0, 1);
    COREVA_HWACC_MISC_TRACE(3);
    uint8_t* Sbox_28_to_doP_4_send_buffer = (uint8_t*)steadyClusterWaitWrite(&Sbox_28_to_doP_4_sender);
    COREVA_HWACC_MISC_TRACE(4);
    for (int _i=0; _i<12; _i++)
        Sbox(Sbox_28_table, &Xor_7_to_Sbox_28_receive_buffer, &Sbox_28_to_doP_4_send_buffer);
    COREVA_HWACC_MISC_TRACE(5);
    steadyClusterDoneRead(&Xor_7_to_Sbox_28_receiver);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from Xor_7
    steadyClusterDoneWrite(&Sbox_28_to_doP_4_sender);
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to doP_4
}

static void Sbox_29_steady()
{
    COREVA_HWACC_MISC_TRACE(2);
    uint8_t* Xor_7_to_Sbox_29_receive_buffer = (uint8_t*)steadyClusterWaitRead(&Xor_7_to_Sbox_29_receiver, 72, sizeof(uint8_t), 0, 0, 0, 1);
    COREVA_HWACC_MISC_TRACE(3);
    uint8_t* Sbox_29_to_doP_4_send_buffer = (uint8_t*)steadyClusterWaitWrite(&Sbox_29_to_doP_4_sender);
    COREVA_HWACC_MISC_TRACE(4);
    for (int _i=0; _i<12; _i++)
        Sbox(Sbox_29_table, &Xor_7_to_Sbox_29_receive_buffer, &Sbox_29_to_doP_4_send_buffer);
    COREVA_HWACC_MISC_TRACE(5);
    steadyClusterDoneRead(&Xor_7_to_Sbox_29_receiver);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from Xor_7
    steadyClusterDoneWrite(&Sbox_29_to_doP_4_sender);
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to doP_4
}

static void Sbox_30_steady()
{
    COREVA_HWACC_MISC_TRACE(2);
    uint8_t* Xor_7_to_Sbox_30_receive_buffer = (uint8_t*)steadyClusterWaitRead(&Xor_7_to_Sbox_30_receiver, 72, sizeof(uint8_t), 0, 0, 0, 1);
    COREVA_HWACC_MISC_TRACE(3);
    uint8_t* Sbox_30_to_doP_4_send_buffer = (uint8_t*)steadyClusterWaitWrite(&Sbox_30_to_doP_4_sender);
    COREVA_HWACC_MISC_TRACE(4);
    for (int _i=0; _i<12; _i++)
        Sbox(Sbox_30_table, &Xor_7_to_Sbox_30_receive_buffer, &Sbox_30_to_doP_4_send_buffer);
    COREVA_HWACC_MISC_TRACE(5);
    steadyClusterDoneRead(&Xor_7_to_Sbox_30_receiver);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from Xor_7
    steadyClusterDoneWrite(&Sbox_30_to_doP_4_sender);
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to doP_4
}

static void Sbox_32_steady()
{
    COREVA_HWACC_MISC_TRACE(2);
    uint8_t* Xor_7_to_Sbox_32_receive_buffer = (uint8_t*)steadyClusterWaitRead(&Xor_7_to_Sbox_32_receiver, 72, sizeof(uint8_t), 0, 0, 0, 1);
    COREVA_HWACC_MISC_TRACE(3);
    uint8_t* Sbox_32_to_doP_4_send_buffer = (uint8_t*)steadyClusterWaitWrite(&Sbox_32_to_doP_4_sender);
    COREVA_HWACC_MISC_TRACE(4);
    for (int _i=0; _i<12; _i++)
        Sbox(Sbox_32_table, &Xor_7_to_Sbox_32_receive_buffer, &Sbox_32_to_doP_4_send_buffer);
    COREVA_HWACC_MISC_TRACE(5);
    steadyClusterDoneRead(&Xor_7_to_Sbox_32_receiver);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from Xor_7
    steadyClusterDoneWrite(&Sbox_32_to_doP_4_sender);
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to doP_4
}

static void Identity_7_steady()
{
    COREVA_HWACC_MISC_TRACE(2);
    if(((Identity_6_to_Identity_7_buffer_ptr_tail-Identity_6_to_Identity_7_buffer)+384)>768+0)
        Identity_6_to_Identity_7_buffer_ptr_tail=Identity_6_to_Identity_7_buffer+0;
    COREVA_HWACC_MISC_TRACE(3);
    uint8_t* Identity_7_to_Xor_8_send_buffer = (uint8_t*)steadyClusterWaitWrite(&Identity_7_to_Xor_8_sender);
    COREVA_HWACC_MISC_TRACE(4);
    for (int _i=0; _i<384; _i++)
        Identity(&Identity_6_to_Identity_7_buffer_ptr_tail, &Identity_7_to_Xor_8_send_buffer);
    COREVA_HWACC_MISC_TRACE(5);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from Identity_6
    steadyClusterDoneWrite(&Identity_7_to_Xor_8_sender);
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to Xor_8
}

static void anonFilter0_4_steady()
{
    COREVA_HWACC_MISC_TRACE(2);
    if(((Identity_6_to_anonFilter0_4_buffer_ptr_tail-Identity_6_to_anonFilter0_4_buffer)+384)>3072+0)
        Identity_6_to_anonFilter0_4_buffer_ptr_tail=Identity_6_to_anonFilter0_4_buffer+0;
    COREVA_HWACC_MISC_TRACE(3);
    for (int _i=0; _i<384; _i++)
        anonFilter0(&Identity_6_to_anonFilter0_4_buffer_ptr_tail);
    COREVA_HWACC_MISC_TRACE(5);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from Identity_6
}

static void doIPm1_1_steady()
{
    COREVA_HWACC_MISC_TRACE(2);
    uint8_t* CrissCross_1_to_doIPm1_1_receive_buffer = (uint8_t*)steadyClusterWaitRead(&CrissCross_1_to_doIPm1_1_receiver, 768, sizeof(uint8_t), 0, 0, 0, 1);
    COREVA_HWACC_MISC_TRACE(3);
    uint8_t* doIPm1_1_to_BitstoInts_1_send_buffer = (uint8_t*)steadyClusterWaitWrite(&doIPm1_1_to_BitstoInts_1_sender);
    COREVA_HWACC_MISC_TRACE(4);
    for (int _i=0; _i<12; _i++)
        doIPm1(&CrissCross_1_to_doIPm1_1_receive_buffer, &doIPm1_1_to_BitstoInts_1_send_buffer);
    COREVA_HWACC_MISC_TRACE(5);
    steadyClusterDoneRead(&CrissCross_1_to_doIPm1_1_receiver);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from CrissCross_1
    steadyClusterDoneWrite(&doIPm1_1_to_BitstoInts_1_sender);
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to BitstoInts_1
}

static void InitChannels_cpu_0_0_0()
{
    // InitRead Identity_6_to_Identity_7 (0 0 0 384 384)
    Identity_6_to_Identity_7_buffer_ptr_tail=Identity_6_to_Identity_7_buffer;
    // InitRead Identity_6_to_anonFilter0_4 (0 0 0 384 384)
    Identity_6_to_anonFilter0_4_buffer_ptr_tail=Identity_6_to_anonFilter0_4_buffer;
    // InitRead Sbox_3_to_doP_1 (0 0 0 48 48)
    Sbox_3_to_doP_1_buffer_ptr_tail=Sbox_3_to_doP_1_buffer;
    // InitRead Sbox_4_to_doP_1 (0 0 0 48 48)
    Sbox_4_to_doP_1_buffer_ptr_tail=Sbox_4_to_doP_1_buffer;
    // InitRead Sbox_5_to_doP_1 (0 0 0 48 48)
    Sbox_5_to_doP_1_buffer_ptr_tail=Sbox_5_to_doP_1_buffer;
    // InitRead Sbox_6_to_doP_1 (0 0 0 48 48)
    Sbox_6_to_doP_1_buffer_ptr_tail=Sbox_6_to_doP_1_buffer;
    // InitRead Sbox_8_to_doP_1 (0 0 0 48 48)
    Sbox_8_to_doP_1_buffer_ptr_tail=Sbox_8_to_doP_1_buffer;
    // InitRead Xor_3_1_to_Sbox_11 (0 0 0 24 24)
    Xor_3_1_to_Sbox_11_buffer_ptr_tail=Xor_3_1_to_Sbox_11_buffer;
    // InitRead Xor_3_1_to_Sbox_12_1 (0 0 0 6 6)
    Xor_3_1_to_Sbox_12_1_buffer_ptr_tail=Xor_3_1_to_Sbox_12_1_buffer;
    // InitRead Xor_3_1_to_Sbox_12_2 (0 0 0 6 6)
    Xor_3_1_to_Sbox_12_2_buffer_ptr_tail=Xor_3_1_to_Sbox_12_2_buffer;
    // InitRead Xor_3_1_to_Sbox_12_4 (0 0 0 6 6)
    Xor_3_1_to_Sbox_12_4_buffer_ptr_tail=Xor_3_1_to_Sbox_12_4_buffer;
    // InitRead Xor_3_1_to_Sbox_13 (0 0 0 24 24)
    Xor_3_1_to_Sbox_13_buffer_ptr_tail=Xor_3_1_to_Sbox_13_buffer;
    // InitRead Xor_3_1_to_Sbox_9 (0 0 0 24 24)
    Xor_3_1_to_Sbox_9_buffer_ptr_tail=Xor_3_1_to_Sbox_9_buffer;
    // InitRead Xor_3_2_to_Sbox_11 (0 0 0 24 24)
    Xor_3_2_to_Sbox_11_buffer_ptr_tail=Xor_3_2_to_Sbox_11_buffer;
    // InitRead Xor_3_2_to_Sbox_12_1 (0 0 0 6 6)
    Xor_3_2_to_Sbox_12_1_buffer_ptr_tail=Xor_3_2_to_Sbox_12_1_buffer;
    // InitRead Xor_3_2_to_Sbox_12_2 (0 0 0 6 6)
    Xor_3_2_to_Sbox_12_2_buffer_ptr_tail=Xor_3_2_to_Sbox_12_2_buffer;
    // InitRead Xor_3_2_to_Sbox_12_4 (0 0 0 6 6)
    Xor_3_2_to_Sbox_12_4_buffer_ptr_tail=Xor_3_2_to_Sbox_12_4_buffer;
    // InitRead Xor_3_2_to_Sbox_13 (0 0 0 24 24)
    Xor_3_2_to_Sbox_13_buffer_ptr_tail=Xor_3_2_to_Sbox_13_buffer;
    // InitRead Xor_3_2_to_Sbox_9 (0 0 0 24 24)
    Xor_3_2_to_Sbox_9_buffer_ptr_tail=Xor_3_2_to_Sbox_9_buffer;
    // InitRead doE_2_to_Xor_3_1 (0 0 0 192 192)
    doE_2_to_Xor_3_1_buffer_ptr_tail=doE_2_to_Xor_3_1_buffer;
    // InitRead doE_2_to_Xor_3_2 (0 0 0 192 192)
    doE_2_to_Xor_3_2_buffer_ptr_tail=doE_2_to_Xor_3_2_buffer;
    // InitWrite Identity_7_to_Xor_8 (0 0 0 384 384)
    initSender(&Identity_7_to_Xor_8_sender, 0, 3, 384, sizeof(uint8_t), 3, DATA_MEM);
    // InitWrite Sbox_27_to_doP_4 (0 0 0 48 48)
    initSender(&Sbox_27_to_doP_4_sender, 0, 3, 48, sizeof(uint8_t), 3, DATA_MEM);
    // InitWrite Sbox_28_to_doP_4 (0 0 0 48 48)
    initSender(&Sbox_28_to_doP_4_sender, 0, 3, 48, sizeof(uint8_t), 3, DATA_MEM);
    // InitWrite Sbox_29_to_doP_4 (0 0 0 48 48)
    initSender(&Sbox_29_to_doP_4_sender, 0, 3, 48, sizeof(uint8_t), 3, DATA_MEM);
    // InitWrite Sbox_30_to_doP_4 (0 0 0 48 48)
    initSender(&Sbox_30_to_doP_4_sender, 0, 3, 48, sizeof(uint8_t), 3, DATA_MEM);
    // InitWrite Sbox_32_to_doP_4 (0 0 0 48 48)
    initSender(&Sbox_32_to_doP_4_sender, 0, 3, 48, sizeof(uint8_t), 3, DATA_MEM);
    // InitWrite Xor_3_1_to_Sbox_12_3 (0 0 0 6 6)
    initSender(&Xor_3_1_to_Sbox_12_3_sender, 0, 3, 6, sizeof(uint8_t), 3, DATA_MEM);
    // InitWrite Xor_3_1_to_Sbox_16 (0 0 0 24 24)
    initSender(&Xor_3_1_to_Sbox_16_sender, 0, 3, 24, sizeof(uint8_t), 3, DATA_MEM);
    // InitWrite Xor_3_2_to_Sbox_12_3 (0 0 0 6 6)
    initSender(&Xor_3_2_to_Sbox_12_3_sender, 0, 3, 6, sizeof(uint8_t), 3, DATA_MEM);
    // InitWrite Xor_3_2_to_Sbox_16 (0 0 0 24 24)
    initSender(&Xor_3_2_to_Sbox_16_sender, 0, 3, 24, sizeof(uint8_t), 3, DATA_MEM);
    // InitWrite doIPm1_1_to_BitstoInts_1 (0 0 0 768 768)
    initSender(&doIPm1_1_to_BitstoInts_1_sender, 0, 3, 768, sizeof(uint8_t), 3, DATA_MEM);
    // InitWrite Identity_1_to_Xor_2 (0 0 0 384 384)
    initSender(&Identity_1_to_Xor_2_sender, 0, 2, 384, sizeof(uint8_t), 3, DATA_MEM);
    // InitWrite Identity_3_to_Xor_4 (0 0 0 384 384)
    initSender(&Identity_3_to_Xor_4_sender, 0, 2, 384, sizeof(uint8_t), 3, DATA_MEM);
    // InitWrite Xor_3_1_to_Sbox_10 (0 0 0 24 24)
    initSender(&Xor_3_1_to_Sbox_10_sender, 0, 2, 24, sizeof(uint8_t), 3, DATA_MEM);
    // InitWrite Xor_3_2_to_Sbox_10 (0 0 0 24 24)
    initSender(&Xor_3_2_to_Sbox_10_sender, 0, 2, 24, sizeof(uint8_t), 3, DATA_MEM);
    // InitWrite doE_2_to_Xor_3_3 (0 0 0 192 192)
    initSender(&doE_2_to_Xor_3_3_sender, 0, 2, 192, sizeof(uint8_t), 3, DATA_MEM);
    // InitWrite doP_1_to_Xor_2 (0 0 0 384 384)
    initSender(&doP_1_to_Xor_2_sender, 0, 2, 384, sizeof(uint8_t), 3, DATA_MEM);
    // InitWrite Sbox_11_to_doP_2 (0 0 0 48 48)
    initSender(&Sbox_11_to_doP_2_sender, 0, 1, 48, sizeof(uint8_t), 3, DATA_MEM);
    // InitWrite Sbox_12_1_to_doP_2 (0 0 0 12 12)
    initSender(&Sbox_12_1_to_doP_2_sender, 0, 1, 12, sizeof(uint8_t), 3, DATA_MEM);
    // InitWrite Sbox_12_2_to_doP_2 (0 0 0 12 12)
    initSender(&Sbox_12_2_to_doP_2_sender, 0, 1, 12, sizeof(uint8_t), 3, DATA_MEM);
    // InitWrite Sbox_12_4_to_doP_2 (0 0 0 12 12)
    initSender(&Sbox_12_4_to_doP_2_sender, 0, 1, 12, sizeof(uint8_t), 3, DATA_MEM);
    // InitWrite Sbox_13_to_doP_2 (0 0 0 48 48)
    initSender(&Sbox_13_to_doP_2_sender, 0, 1, 48, sizeof(uint8_t), 3, DATA_MEM);
    // InitWrite Sbox_17_to_doP_3 (0 0 0 48 48)
    initSender(&Sbox_17_to_doP_3_sender, 0, 1, 48, sizeof(uint8_t), 3, DATA_MEM);
    // InitWrite Sbox_20_to_doP_3 (0 0 0 48 48)
    initSender(&Sbox_20_to_doP_3_sender, 0, 1, 48, sizeof(uint8_t), 3, DATA_MEM);
    // InitWrite Sbox_21_to_doP_3 (0 0 0 48 48)
    initSender(&Sbox_21_to_doP_3_sender, 0, 1, 48, sizeof(uint8_t), 3, DATA_MEM);
    // InitWrite Sbox_22_to_doP_3 (0 0 0 48 48)
    initSender(&Sbox_22_to_doP_3_sender, 0, 1, 48, sizeof(uint8_t), 3, DATA_MEM);
    // InitWrite Sbox_9_to_doP_2 (0 0 0 48 48)
    initSender(&Sbox_9_to_doP_2_sender, 0, 1, 48, sizeof(uint8_t), 3, DATA_MEM);
    // InitWrite Xor_3_1_to_Sbox_14 (0 0 0 24 24)
    initSender(&Xor_3_1_to_Sbox_14_sender, 0, 1, 24, sizeof(uint8_t), 3, DATA_MEM);
    // InitWrite Xor_3_1_to_Sbox_15 (0 0 0 24 24)
    initSender(&Xor_3_1_to_Sbox_15_sender, 0, 1, 24, sizeof(uint8_t), 3, DATA_MEM);
    // InitWrite Xor_3_2_to_Sbox_14 (0 0 0 24 24)
    initSender(&Xor_3_2_to_Sbox_14_sender, 0, 1, 24, sizeof(uint8_t), 3, DATA_MEM);
    // InitWrite Xor_3_2_to_Sbox_15 (0 0 0 24 24)
    initSender(&Xor_3_2_to_Sbox_15_sender, 0, 1, 24, sizeof(uint8_t), 3, DATA_MEM);
    // InitWrite doE_3_1_to_Xor_5 (0 0 0 288 288)
    initSender(&doE_3_1_to_Xor_5_sender, 0, 1, 288, sizeof(uint8_t), 3, DATA_MEM);
    // InitWrite Identity_6_to_Identity_7 (0 0 0 384 384)
    Identity_6_to_Identity_7_buffer_ptr_head=Identity_6_to_Identity_7_buffer;
    // InitWrite Identity_6_to_anonFilter0_4 (0 0 0 384 384)
    Identity_6_to_anonFilter0_4_buffer_ptr_head=Identity_6_to_anonFilter0_4_buffer;
    // InitWrite Sbox_3_to_doP_1 (0 0 0 48 48)
    Sbox_3_to_doP_1_buffer_ptr_head=Sbox_3_to_doP_1_buffer;
    // InitWrite Sbox_4_to_doP_1 (0 0 0 48 48)
    Sbox_4_to_doP_1_buffer_ptr_head=Sbox_4_to_doP_1_buffer;
    // InitWrite Sbox_5_to_doP_1 (0 0 0 48 48)
    Sbox_5_to_doP_1_buffer_ptr_head=Sbox_5_to_doP_1_buffer;
    // InitWrite Sbox_6_to_doP_1 (0 0 0 48 48)
    Sbox_6_to_doP_1_buffer_ptr_head=Sbox_6_to_doP_1_buffer;
    // InitWrite Sbox_8_to_doP_1 (0 0 0 48 48)
    Sbox_8_to_doP_1_buffer_ptr_head=Sbox_8_to_doP_1_buffer;
    // InitWrite Xor_3_1_to_Sbox_11 (0 0 0 24 24)
    Xor_3_1_to_Sbox_11_buffer_ptr_head=Xor_3_1_to_Sbox_11_buffer;
    // InitWrite Xor_3_1_to_Sbox_12_1 (0 0 0 6 6)
    Xor_3_1_to_Sbox_12_1_buffer_ptr_head=Xor_3_1_to_Sbox_12_1_buffer;
    // InitWrite Xor_3_1_to_Sbox_12_2 (0 0 0 6 6)
    Xor_3_1_to_Sbox_12_2_buffer_ptr_head=Xor_3_1_to_Sbox_12_2_buffer;
    // InitWrite Xor_3_1_to_Sbox_12_4 (0 0 0 6 6)
    Xor_3_1_to_Sbox_12_4_buffer_ptr_head=Xor_3_1_to_Sbox_12_4_buffer;
    // InitWrite Xor_3_1_to_Sbox_13 (0 0 0 24 24)
    Xor_3_1_to_Sbox_13_buffer_ptr_head=Xor_3_1_to_Sbox_13_buffer;
    // InitWrite Xor_3_1_to_Sbox_9 (0 0 0 24 24)
    Xor_3_1_to_Sbox_9_buffer_ptr_head=Xor_3_1_to_Sbox_9_buffer;
    // InitWrite Xor_3_2_to_Sbox_11 (0 0 0 24 24)
    Xor_3_2_to_Sbox_11_buffer_ptr_head=Xor_3_2_to_Sbox_11_buffer;
    // InitWrite Xor_3_2_to_Sbox_12_1 (0 0 0 6 6)
    Xor_3_2_to_Sbox_12_1_buffer_ptr_head=Xor_3_2_to_Sbox_12_1_buffer;
    // InitWrite Xor_3_2_to_Sbox_12_2 (0 0 0 6 6)
    Xor_3_2_to_Sbox_12_2_buffer_ptr_head=Xor_3_2_to_Sbox_12_2_buffer;
    // InitWrite Xor_3_2_to_Sbox_12_4 (0 0 0 6 6)
    Xor_3_2_to_Sbox_12_4_buffer_ptr_head=Xor_3_2_to_Sbox_12_4_buffer;
    // InitWrite Xor_3_2_to_Sbox_13 (0 0 0 24 24)
    Xor_3_2_to_Sbox_13_buffer_ptr_head=Xor_3_2_to_Sbox_13_buffer;
    // InitWrite Xor_3_2_to_Sbox_9 (0 0 0 24 24)
    Xor_3_2_to_Sbox_9_buffer_ptr_head=Xor_3_2_to_Sbox_9_buffer;
    // InitWrite doE_2_to_Xor_3_1 (0 0 0 192 192)
    doE_2_to_Xor_3_1_buffer_ptr_head=doE_2_to_Xor_3_1_buffer;
    // InitWrite doE_2_to_Xor_3_2 (0 0 0 192 192)
    doE_2_to_Xor_3_2_buffer_ptr_head=doE_2_to_Xor_3_2_buffer;
    // InitRead Identity_2_to_Identity_3 (0 0 0 384 384)
    initReceiver(&Identity_2_to_Identity_3_receiver, 0, 1, 384, sizeof(uint8_t), 0, 3, 0, DATA_MEM);
    // InitRead Identity_4_to_anonFilter0_3 (0 0 0 384 384)
    initReceiver(&Identity_4_to_anonFilter0_3_receiver, 0, 1, 384, sizeof(uint8_t), 0, 14, 0, DATA_MEM);
    // InitRead Sbox_7_to_doP_1 (0 0 0 48 48)
    initReceiver(&Sbox_7_to_doP_1_receiver, 0, 1, 48, sizeof(uint8_t), 0, 3, 0, DATA_MEM);
    // InitRead Xor_1_to_Sbox_3 (0 0 0 72 72)
    initReceiver(&Xor_1_to_Sbox_3_receiver, 0, 1, 72, sizeof(uint8_t), 0, 3, 0, DATA_MEM);
    // InitRead Xor_1_to_Sbox_4 (0 0 0 72 72)
    initReceiver(&Xor_1_to_Sbox_4_receiver, 0, 1, 72, sizeof(uint8_t), 0, 3, 0, DATA_MEM);
    // InitRead Xor_1_to_Sbox_5 (0 0 0 72 72)
    initReceiver(&Xor_1_to_Sbox_5_receiver, 0, 1, 72, sizeof(uint8_t), 0, 3, 0, DATA_MEM);
    // InitRead Xor_1_to_Sbox_6 (0 0 0 72 72)
    initReceiver(&Xor_1_to_Sbox_6_receiver, 0, 1, 72, sizeof(uint8_t), 0, 3, 0, DATA_MEM);
    // InitRead Xor_1_to_Sbox_8 (0 0 0 72 72)
    initReceiver(&Xor_1_to_Sbox_8_receiver, 0, 1, 72, sizeof(uint8_t), 0, 3, 0, DATA_MEM);
    // InitRead Xor_5_to_Sbox_17 (0 0 0 72 72)
    initReceiver(&Xor_5_to_Sbox_17_receiver, 0, 1, 72, sizeof(uint8_t), 0, 3, 0, DATA_MEM);
    // InitRead Xor_5_to_Sbox_20 (0 0 0 72 72)
    initReceiver(&Xor_5_to_Sbox_20_receiver, 0, 1, 72, sizeof(uint8_t), 0, 3, 0, DATA_MEM);
    // InitRead Xor_5_to_Sbox_21 (0 0 0 72 72)
    initReceiver(&Xor_5_to_Sbox_21_receiver, 0, 1, 72, sizeof(uint8_t), 0, 3, 0, DATA_MEM);
    // InitRead Xor_5_to_Sbox_22 (0 0 0 72 72)
    initReceiver(&Xor_5_to_Sbox_22_receiver, 0, 1, 72, sizeof(uint8_t), 0, 3, 0, DATA_MEM);
    // InitRead Sbox_2_1_to_doP_1 (0 0 0 16 16)
    initReceiver(&Sbox_2_1_to_doP_1_receiver, 0, 2, 16, sizeof(uint8_t), 0, 3, 0, DATA_MEM);
    // InitRead Sbox_2_2_to_doP_1 (0 0 0 16 16)
    initReceiver(&Sbox_2_2_to_doP_1_receiver, 0, 2, 16, sizeof(uint8_t), 0, 3, 0, DATA_MEM);
    // InitRead Sbox_2_3_to_doP_1 (0 0 0 16 16)
    initReceiver(&Sbox_2_3_to_doP_1_receiver, 0, 2, 16, sizeof(uint8_t), 0, 3, 0, DATA_MEM);
    // InitRead Xor_2_to_doE_2 (0 0 0 384 384)
    initReceiver(&Xor_2_to_doE_2_receiver, 0, 2, 384, sizeof(uint8_t), 0, 3, 0, DATA_MEM);
    // InitRead Xor_3_3_to_Sbox_11 (0 0 0 24 24)
    initReceiver(&Xor_3_3_to_Sbox_11_receiver, 0, 2, 24, sizeof(uint8_t), 0, 3, 0, DATA_MEM);
    // InitRead Xor_3_3_to_Sbox_12_1 (0 0 0 6 6)
    initReceiver(&Xor_3_3_to_Sbox_12_1_receiver, 0, 2, 6, sizeof(uint8_t), 0, 3, 0, DATA_MEM);
    // InitRead Xor_3_3_to_Sbox_12_2 (0 0 0 6 6)
    initReceiver(&Xor_3_3_to_Sbox_12_2_receiver, 0, 2, 6, sizeof(uint8_t), 0, 3, 0, DATA_MEM);
    // InitRead Xor_3_3_to_Sbox_12_4 (0 0 0 6 6)
    initReceiver(&Xor_3_3_to_Sbox_12_4_receiver, 0, 2, 6, sizeof(uint8_t), 0, 3, 0, DATA_MEM);
    // InitRead Xor_3_3_to_Sbox_13 (0 0 0 24 24)
    initReceiver(&Xor_3_3_to_Sbox_13_receiver, 0, 2, 24, sizeof(uint8_t), 0, 3, 0, DATA_MEM);
    // InitRead Xor_3_3_to_Sbox_9 (0 0 0 24 24)
    initReceiver(&Xor_3_3_to_Sbox_9_receiver, 0, 2, 24, sizeof(uint8_t), 0, 3, 0, DATA_MEM);
    // InitRead Xor_4_to_Identity_6 (0 0 0 384 384)
    initReceiver(&Xor_4_to_Identity_6_receiver, 0, 2, 384, sizeof(uint8_t), 0, 10, 0, DATA_MEM);
    // InitRead Xor_4_to_doE_3_1 (0 0 0 192 192)
    initReceiver(&Xor_4_to_doE_3_1_receiver, 0, 2, 192, sizeof(uint8_t), 0, 3, 0, DATA_MEM);
    // InitRead Xor_7_to_Sbox_27 (0 0 0 72 72)
    initReceiver(&Xor_7_to_Sbox_27_receiver, 0, 2, 72, sizeof(uint8_t), 0, 3, 0, DATA_MEM);
    // InitRead Xor_7_to_Sbox_28 (0 0 0 72 72)
    initReceiver(&Xor_7_to_Sbox_28_receiver, 0, 2, 72, sizeof(uint8_t), 0, 3, 0, DATA_MEM);
    // InitRead Xor_7_to_Sbox_29 (0 0 0 72 72)
    initReceiver(&Xor_7_to_Sbox_29_receiver, 0, 2, 72, sizeof(uint8_t), 0, 3, 0, DATA_MEM);
    // InitRead Xor_7_to_Sbox_30 (0 0 0 72 72)
    initReceiver(&Xor_7_to_Sbox_30_receiver, 0, 2, 72, sizeof(uint8_t), 0, 3, 0, DATA_MEM);
    // InitRead Xor_7_to_Sbox_32 (0 0 0 72 72)
    initReceiver(&Xor_7_to_Sbox_32_receiver, 0, 2, 72, sizeof(uint8_t), 0, 3, 0, DATA_MEM);
    // InitRead anonFilter3_2_to_Xor_3_1 (0 0 0 192 192)
    initReceiver(&anonFilter3_2_to_Xor_3_1_receiver, 0, 2, 192, sizeof(uint8_t), 0, 3, 0, DATA_MEM);
    // InitRead anonFilter3_2_to_Xor_3_2 (0 0 0 192 192)
    initReceiver(&anonFilter3_2_to_Xor_3_2_receiver, 0, 2, 192, sizeof(uint8_t), 0, 3, 0, DATA_MEM);
    // InitRead doIP_1_1_to_Identity_1 (0 0 0 192 192)
    initReceiver(&doIP_1_1_to_Identity_1_receiver, 0, 2, 192, sizeof(uint8_t), 0, 6, 0, DATA_MEM);
    // InitRead CrissCross_1_to_doIPm1_1 (0 0 0 768 768)
    initReceiver(&CrissCross_1_to_doIPm1_1_receiver, 0, 3, 768, sizeof(uint8_t), 0, 3, 0, DATA_MEM);
    // InitRead Sbox_1_1_to_doP_1 (0 0 0 24 24)
    initReceiver(&Sbox_1_1_to_doP_1_receiver, 0, 3, 24, sizeof(uint8_t), 0, 3, 0, DATA_MEM);
    // InitRead Sbox_1_2_to_doP_1 (0 0 0 24 24)
    initReceiver(&Sbox_1_2_to_doP_1_receiver, 0, 3, 24, sizeof(uint8_t), 0, 3, 0, DATA_MEM);
    // InitRead doIP_1_2_to_Identity_1 (0 0 0 192 192)
    initReceiver(&doIP_1_2_to_Identity_1_receiver, 0, 3, 192, sizeof(uint8_t), 0, 6, 0, DATA_MEM);
}

static void InitFilters_cpu_0_0_0()
{
    Sbox_3_init();
    Sbox_4_init();
    Sbox_5_init();
    Sbox_6_init();
    Sbox_8_init();
    doP_1_init();
    Identity_1_init();
    doE_2_init();
    Xor_3_1_init();
    Xor_3_2_init();
    Sbox_9_init();
    Sbox_11_init();
    Sbox_12_1_init();
    Sbox_12_2_init();
    Sbox_12_4_init();
    Sbox_13_init();
    Identity_3_init();
    doE_3_1_init();
    Sbox_17_init();
    Sbox_20_init();
    Sbox_21_init();
    Sbox_22_init();
    Identity_6_init();
    anonFilter0_3_init();
    Sbox_27_init();
    Sbox_28_init();
    Sbox_29_init();
    Sbox_30_init();
    Sbox_32_init();
    Identity_7_init();
    anonFilter0_4_init();
    doIPm1_1_init();
}

static void PrimePump_cpu_0_0_0()
{
    for (int iteration=0; iteration<27; iteration++)
    {
        if (iteration >= 4)
        {
            Sbox_3_steady();
            Sbox_4_steady();
            Sbox_5_steady();
            Sbox_6_steady();
            Sbox_8_steady();
        }
        if (iteration >= 5)
        {
            doP_1_steady();
            Identity_1_steady();
        }
        if (iteration >= 7)
        {
            doE_2_steady();
        }
        if (iteration >= 8)
        {
            Xor_3_1_steady();
            Xor_3_2_steady();
        }
        if (iteration >= 9)
        {
            Sbox_9_steady();
            Sbox_11_steady();
            Sbox_12_1_steady();
            Sbox_12_2_steady();
            Sbox_12_4_steady();
            Sbox_13_steady();
        }
        if (iteration >= 10)
        {
            Identity_3_steady();
        }
        if (iteration >= 12)
        {
            doE_3_1_steady();
        }
        if (iteration >= 14)
        {
            Sbox_17_steady();
            Sbox_20_steady();
            Sbox_21_steady();
            Sbox_22_steady();
        }
        if (iteration >= 19)
        {
            Identity_6_steady();
            Sbox_27_steady();
            Sbox_28_steady();
            Sbox_29_steady();
            Sbox_30_steady();
            Sbox_32_steady();
        }
        if (iteration >= 20)
        {
            Identity_7_steady();
        }
        if (iteration >= 23)
        {
            doIPm1_1_steady();
        }
        if (iteration >= 26)
        {
            anonFilter0_3_steady();
            anonFilter0_4_steady();
        }
    }
}

int cpu_0_0_0()
{
    InitStatics_cpu_0_0_0();
    InitChannels_cpu_0_0_0();
    InitFilters_cpu_0_0_0();
    PrimePump_cpu_0_0_0();
    for(int _i=0;_i<STEADY_ITERATIONS;_i++) // Steady State Loop
    {
        COREVA_HWACC_MISC_TRACE(10);
        Sbox_3_steady();
        COREVA_HWACC_MISC_TRACE(11);
        Sbox_4_steady();
        COREVA_HWACC_MISC_TRACE(12);
        Sbox_5_steady();
        COREVA_HWACC_MISC_TRACE(13);
        Sbox_6_steady();
        COREVA_HWACC_MISC_TRACE(14);
        Sbox_8_steady();
        COREVA_HWACC_MISC_TRACE(15);
        doP_1_steady();
        COREVA_HWACC_MISC_TRACE(16);
        Identity_1_steady();
        COREVA_HWACC_MISC_TRACE(17);
        doE_2_steady();
        COREVA_HWACC_MISC_TRACE(18);
        Xor_3_1_steady();
        COREVA_HWACC_MISC_TRACE(19);
        Xor_3_2_steady();
        COREVA_HWACC_MISC_TRACE(20);
        Sbox_9_steady();
        COREVA_HWACC_MISC_TRACE(21);
        Sbox_11_steady();
        COREVA_HWACC_MISC_TRACE(22);
        Sbox_12_1_steady();
        COREVA_HWACC_MISC_TRACE(23);
        Sbox_12_2_steady();
        COREVA_HWACC_MISC_TRACE(24);
        Sbox_12_4_steady();
        COREVA_HWACC_MISC_TRACE(25);
        Sbox_13_steady();
        COREVA_HWACC_MISC_TRACE(26);
        Identity_3_steady();
        COREVA_HWACC_MISC_TRACE(27);
        doE_3_1_steady();
        COREVA_HWACC_MISC_TRACE(28);
        Sbox_17_steady();
        COREVA_HWACC_MISC_TRACE(29);
        Sbox_20_steady();
        COREVA_HWACC_MISC_TRACE(30);
        Sbox_21_steady();
        COREVA_HWACC_MISC_TRACE(31);
        Sbox_22_steady();
        COREVA_HWACC_MISC_TRACE(32);
        Identity_6_steady();
        COREVA_HWACC_MISC_TRACE(33);
        anonFilter0_3_steady();
        COREVA_HWACC_MISC_TRACE(34);
        Sbox_27_steady();
        COREVA_HWACC_MISC_TRACE(35);
        Sbox_28_steady();
        COREVA_HWACC_MISC_TRACE(36);
        Sbox_29_steady();
        COREVA_HWACC_MISC_TRACE(37);
        Sbox_30_steady();
        COREVA_HWACC_MISC_TRACE(38);
        Sbox_32_steady();
        COREVA_HWACC_MISC_TRACE(39);
        Identity_7_steady();
        COREVA_HWACC_MISC_TRACE(40);
        anonFilter0_4_steady();
        COREVA_HWACC_MISC_TRACE(41);
        doIPm1_1_steady();
    }
    	return 0;
}

