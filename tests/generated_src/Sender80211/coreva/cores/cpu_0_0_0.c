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

char* FileReader_0_filename;
unsigned int FileReader_0_interval;
unsigned int FileReader_0_next_block;
volatile unsigned int FileReader_0_tmp;
int32_t* scramble_1_shift = {0};
int32_t diff_encode_1_last_out;
int32_t* chunk2symbol_1_1_bpsk_mod = {0};
int32_t* chunk2symbol_1_2_bpsk_mod = {0};
int32_t* chunk2symbol_1_3_bpsk_mod = {0};

void InitStatics_cpu_0_0_0()
{
}

int32_t* FileReader_0_to_scramble_1_buffer_ptr_tail = 0;
MULTI_CHANNEL_SYNC FileInput_to_FileReader_0_receiver;
int32_t* FileReader_0_to_scramble_1_buffer_ptr_head = 0;
int32_t FileReader_0_to_scramble_1_buffer[9+8];
int32_t* scramble_1_to_diff_encode_1_buffer_ptr_tail = 0;
int32_t* scramble_1_to_diff_encode_1_buffer_ptr_head = 0;
int32_t scramble_1_to_diff_encode_1_buffer[72+64];
int32_t* diff_encode_1_to_chunk2symbol_1_1_buffer_ptr_tail = 0;
int32_t* diff_encode_1_to_chunk2symbol_1_2_buffer_ptr_tail = 0;
int32_t* diff_encode_1_to_chunk2symbol_1_3_buffer_ptr_tail = 0;
int32_t split_diff_encode_1_buffer[64];
int32_t* diff_encode_1_to_chunk2symbol_1_1_buffer_ptr_head = 0;
int32_t diff_encode_1_to_chunk2symbol_1_1_buffer[27+24];
MULTI_CHANNEL_SYNC chunk2symbol_1_1_to_fir_filter_half_1_1_sender;
MULTI_CHANNEL_SYNC chunk2symbol_1_1_to_fir_filter_half_1_7_sender;
MULTI_CHANNEL_SYNC chunk2symbol_1_1_to_fir_filter_half_2_1_sender;
MULTI_CHANNEL_SYNC chunk2symbol_1_1_to_fir_filter_half_2_7_sender;
MULTI_CHANNEL_SYNC chunk2symbol_1_1_to_fir_filter_half_1_2_sender;
MULTI_CHANNEL_SYNC chunk2symbol_1_1_to_fir_filter_half_2_2_sender;
int32_t split_chunk2symbol_1_1_buffer[24];
int32_t* diff_encode_1_to_chunk2symbol_1_2_buffer_ptr_head = 0;
int32_t diff_encode_1_to_chunk2symbol_1_2_buffer[27+24];
MULTI_CHANNEL_SYNC chunk2symbol_1_2_to_fir_filter_half_1_3_sender;
MULTI_CHANNEL_SYNC chunk2symbol_1_2_to_fir_filter_half_1_2_sender;
MULTI_CHANNEL_SYNC chunk2symbol_1_2_to_fir_filter_half_2_3_sender;
MULTI_CHANNEL_SYNC chunk2symbol_1_2_to_fir_filter_half_2_2_sender;
MULTI_CHANNEL_SYNC chunk2symbol_1_2_to_fir_filter_half_1_4_sender;
MULTI_CHANNEL_SYNC chunk2symbol_1_2_to_fir_filter_half_2_4_sender;
MULTI_CHANNEL_SYNC chunk2symbol_1_2_to_fir_filter_half_1_5_sender;
MULTI_CHANNEL_SYNC chunk2symbol_1_2_to_fir_filter_half_2_5_sender;
int32_t split_chunk2symbol_1_2_buffer[24];
int32_t* diff_encode_1_to_chunk2symbol_1_3_buffer_ptr_head = 0;
int32_t diff_encode_1_to_chunk2symbol_1_3_buffer[18+16];
MULTI_CHANNEL_SYNC chunk2symbol_1_3_to_fir_filter_half_1_6_sender;
MULTI_CHANNEL_SYNC chunk2symbol_1_3_to_fir_filter_half_1_5_sender;
MULTI_CHANNEL_SYNC chunk2symbol_1_3_to_fir_filter_half_2_6_sender;
MULTI_CHANNEL_SYNC chunk2symbol_1_3_to_fir_filter_half_2_5_sender;
MULTI_CHANNEL_SYNC chunk2symbol_1_3_to_fir_filter_half_1_7_sender;
MULTI_CHANNEL_SYNC chunk2symbol_1_3_to_fir_filter_half_2_7_sender;
int32_t split_chunk2symbol_1_3_buffer[16];

// WORK
static void chunk2symbol(int32_t* restrict bpsk_mod, int32_t** restrict _buffer_ptr_tail, int32_t** restrict split__buffer_ptr_head)
{
    int32_t input;

    (input)=(*(*_buffer_ptr_tail)++);
    *(*split__buffer_ptr_head)++ = ((bpsk_mod)[(input)]);
}

static void diff_encode(int32_t *_last_out, int32_t** restrict _buffer_ptr_tail, int32_t** restrict split__buffer_ptr_head)
{
    (*_last_out)=((*_last_out)+(*(*_buffer_ptr_tail)++))%2;
    *(*split__buffer_ptr_head)++ = ((*_last_out));
}

FILE *FileReader_0_file;
static void scramble(int32_t* restrict shift, int32_t** restrict _buffer_ptr_tail, int32_t** restrict _buffer_ptr_head)
{
    int32_t input;
    int32_t j;

    (input)=(*(*_buffer_ptr_tail)++);
    for ((j)=0; (j)<8; (j)++)
        {
        int32_t output;

        (output)=(((input)&(1<<(j)))>>(j))^((shift)[3]^(shift)[6]);
        (shift)[6]=(shift)[5];
        (shift)[5]=(shift)[4];
        (shift)[4]=(shift)[3];
        (shift)[3]=(shift)[2];
        (shift)[2]=(shift)[1];
        (shift)[1]=(shift)[0];
        (shift)[0]=(output);
        *(*_buffer_ptr_head)++ = ((output));
    }
}

// PREWORK + INIT
static void FileReader_0_init()
{
    FileReader_0_filename = "input.stream";
    FileReader_0_interval = 0;
    FileReader_0_next_block = 0;
    FileReader_0_tmp = 0;
    /*  FileReader SplitBuffer inlined */
    //netInit:8 netsteady: 1
    volatile int32_t* FileInput_to_FileReader_0_receive_buffer = waitMultiChannelReady(&FileInput_to_FileReader_0_receiver);
    //Split1
    //list:1
    for (unsigned int _i=0;_i<8;_i++)
    {
        *FileReader_0_to_scramble_1_buffer_ptr_head++ = ((*FileInput_to_FileReader_0_receive_buffer++));
    }
    FileInput_to_FileReader_0_receive_buffer-=8;
    initFioReadMultiChannel(&FileInput_to_FileReader_0_receiver, 0, FileReader_0_filename, (void*)FileInput_to_FileReader_0_receive_buffer, 1 * sizeof(int32_t), 1);
    /*  FileReader SplitBuffer done */
}

static void scramble_1_init()
{
    (scramble_1_shift)=(int32_t*)malloc(sizeof(int32_t)*7);
    (scramble_1_shift)[0]=1;
    (scramble_1_shift)[1]=1;
    (scramble_1_shift)[2]=0;
    (scramble_1_shift)[3]=1;
    (scramble_1_shift)[4]=1;
    (scramble_1_shift)[5]=0;
    (scramble_1_shift)[6]=0;
    for (unsigned int _i=0; _i<8; _i++)
    {
        scramble(scramble_1_shift, &FileReader_0_to_scramble_1_buffer_ptr_tail, &scramble_1_to_diff_encode_1_buffer_ptr_head);
    }
}

static void diff_encode_1_init()
{
    (diff_encode_1_last_out)=0;
    int32_t* split_diff_encode_1_buffer_ptr_head=split_diff_encode_1_buffer;
    for (unsigned int _i=0; _i<64; _i++)
    {
        diff_encode(&diff_encode_1_last_out, &scramble_1_to_diff_encode_1_buffer_ptr_tail, &split_diff_encode_1_buffer_ptr_head);
    }
    int32_t* split_diff_encode_1_buffer_ptr_tail=split_diff_encode_1_buffer;
    for (unsigned int _i=0; _i<8;_i++)
    {
        *diff_encode_1_to_chunk2symbol_1_1_buffer_ptr_head++ = ((*split_diff_encode_1_buffer_ptr_tail++));
        *diff_encode_1_to_chunk2symbol_1_1_buffer_ptr_head++ = ((*split_diff_encode_1_buffer_ptr_tail++));
        *diff_encode_1_to_chunk2symbol_1_1_buffer_ptr_head++ = ((*split_diff_encode_1_buffer_ptr_tail++));
        *diff_encode_1_to_chunk2symbol_1_2_buffer_ptr_head++ = ((*split_diff_encode_1_buffer_ptr_tail++));
        *diff_encode_1_to_chunk2symbol_1_2_buffer_ptr_head++ = ((*split_diff_encode_1_buffer_ptr_tail++));
        *diff_encode_1_to_chunk2symbol_1_2_buffer_ptr_head++ = ((*split_diff_encode_1_buffer_ptr_tail++));
        *diff_encode_1_to_chunk2symbol_1_3_buffer_ptr_head++ = ((*split_diff_encode_1_buffer_ptr_tail++));
        *diff_encode_1_to_chunk2symbol_1_3_buffer_ptr_head++ = ((*split_diff_encode_1_buffer_ptr_tail++));
    }
}

static void chunk2symbol_1_1_init()
{
    (chunk2symbol_1_1_bpsk_mod)=(int32_t*)malloc(sizeof(int32_t)*2);
    (chunk2symbol_1_1_bpsk_mod)[0]=741343;
    (chunk2symbol_1_1_bpsk_mod)[1]=-741343;
    int32_t* chunk2symbol_1_1_to_fir_filter_half_1_1_send_buffer = (int32_t*)initWaitWrite(&chunk2symbol_1_1_to_fir_filter_half_1_1_sender, 8);
    int32_t* chunk2symbol_1_1_to_fir_filter_half_1_7_send_buffer = (int32_t*)initWaitWrite(&chunk2symbol_1_1_to_fir_filter_half_1_7_sender, 8);
    int32_t* chunk2symbol_1_1_to_fir_filter_half_2_1_send_buffer = (int32_t*)initWaitWrite(&chunk2symbol_1_1_to_fir_filter_half_2_1_sender, 8);
    int32_t* chunk2symbol_1_1_to_fir_filter_half_2_7_send_buffer = (int32_t*)initWaitWrite(&chunk2symbol_1_1_to_fir_filter_half_2_7_sender, 8);
    int32_t* chunk2symbol_1_1_to_fir_filter_half_1_2_send_buffer = (int32_t*)initWaitWrite(&chunk2symbol_1_1_to_fir_filter_half_1_2_sender, 8);
    int32_t* chunk2symbol_1_1_to_fir_filter_half_2_2_send_buffer = (int32_t*)initWaitWrite(&chunk2symbol_1_1_to_fir_filter_half_2_2_sender, 8);
    int32_t* split_chunk2symbol_1_1_buffer_ptr_head=split_chunk2symbol_1_1_buffer;
    for (unsigned int _i=0; _i<24; _i++)
    {
        chunk2symbol(chunk2symbol_1_1_bpsk_mod, &diff_encode_1_to_chunk2symbol_1_1_buffer_ptr_tail, &split_chunk2symbol_1_1_buffer_ptr_head);
    }
    int32_t* split_chunk2symbol_1_1_buffer_ptr_tail=split_chunk2symbol_1_1_buffer;
    for (unsigned int _i=0; _i<8;_i++)
    {
        int32_t _tmp;
        _tmp = (*split_chunk2symbol_1_1_buffer_ptr_tail++);
        *chunk2symbol_1_1_to_fir_filter_half_1_1_send_buffer++ = (_tmp);
        *chunk2symbol_1_1_to_fir_filter_half_1_7_send_buffer++ = (_tmp);
        *chunk2symbol_1_1_to_fir_filter_half_2_1_send_buffer++ = (_tmp);
        *chunk2symbol_1_1_to_fir_filter_half_2_7_send_buffer++ = (_tmp);
        _tmp = (*split_chunk2symbol_1_1_buffer_ptr_tail++);
        *chunk2symbol_1_1_to_fir_filter_half_1_1_send_buffer++ = (_tmp);
        *chunk2symbol_1_1_to_fir_filter_half_2_1_send_buffer++ = (_tmp);
        _tmp = (*split_chunk2symbol_1_1_buffer_ptr_tail++);
        *chunk2symbol_1_1_to_fir_filter_half_1_2_send_buffer++ = (_tmp);
        *chunk2symbol_1_1_to_fir_filter_half_1_1_send_buffer++ = (_tmp);
        *chunk2symbol_1_1_to_fir_filter_half_2_2_send_buffer++ = (_tmp);
        *chunk2symbol_1_1_to_fir_filter_half_2_1_send_buffer++ = (_tmp);
    }
    initDoneWrite(&chunk2symbol_1_1_to_fir_filter_half_1_1_sender, 8, sizeof(int32_t), 0);
    initDoneWrite(&chunk2symbol_1_1_to_fir_filter_half_1_7_sender, 8, sizeof(int32_t), 0);
    initDoneWrite(&chunk2symbol_1_1_to_fir_filter_half_2_1_sender, 8, sizeof(int32_t), 0);
    initDoneWrite(&chunk2symbol_1_1_to_fir_filter_half_2_7_sender, 8, sizeof(int32_t), 0);
    initDoneWrite(&chunk2symbol_1_1_to_fir_filter_half_1_2_sender, 8, sizeof(int32_t), 0);
    initDoneWrite(&chunk2symbol_1_1_to_fir_filter_half_2_2_sender, 8, sizeof(int32_t), 0);
}

static void chunk2symbol_1_2_init()
{
    (chunk2symbol_1_2_bpsk_mod)=(int32_t*)malloc(sizeof(int32_t)*2);
    (chunk2symbol_1_2_bpsk_mod)[0]=741343;
    (chunk2symbol_1_2_bpsk_mod)[1]=-741343;
    int32_t* chunk2symbol_1_2_to_fir_filter_half_1_3_send_buffer = (int32_t*)initWaitWrite(&chunk2symbol_1_2_to_fir_filter_half_1_3_sender, 8);
    int32_t* chunk2symbol_1_2_to_fir_filter_half_1_2_send_buffer = (int32_t*)initWaitWrite(&chunk2symbol_1_2_to_fir_filter_half_1_2_sender, 8);
    int32_t* chunk2symbol_1_2_to_fir_filter_half_2_3_send_buffer = (int32_t*)initWaitWrite(&chunk2symbol_1_2_to_fir_filter_half_2_3_sender, 8);
    int32_t* chunk2symbol_1_2_to_fir_filter_half_2_2_send_buffer = (int32_t*)initWaitWrite(&chunk2symbol_1_2_to_fir_filter_half_2_2_sender, 8);
    int32_t* chunk2symbol_1_2_to_fir_filter_half_1_4_send_buffer = (int32_t*)initWaitWrite(&chunk2symbol_1_2_to_fir_filter_half_1_4_sender, 8);
    int32_t* chunk2symbol_1_2_to_fir_filter_half_2_4_send_buffer = (int32_t*)initWaitWrite(&chunk2symbol_1_2_to_fir_filter_half_2_4_sender, 8);
    int32_t* chunk2symbol_1_2_to_fir_filter_half_1_5_send_buffer = (int32_t*)initWaitWrite(&chunk2symbol_1_2_to_fir_filter_half_1_5_sender, 8);
    int32_t* chunk2symbol_1_2_to_fir_filter_half_2_5_send_buffer = (int32_t*)initWaitWrite(&chunk2symbol_1_2_to_fir_filter_half_2_5_sender, 8);
    int32_t* split_chunk2symbol_1_2_buffer_ptr_head=split_chunk2symbol_1_2_buffer;
    for (unsigned int _i=0; _i<24; _i++)
    {
        chunk2symbol(chunk2symbol_1_2_bpsk_mod, &diff_encode_1_to_chunk2symbol_1_2_buffer_ptr_tail, &split_chunk2symbol_1_2_buffer_ptr_head);
    }
    int32_t* split_chunk2symbol_1_2_buffer_ptr_tail=split_chunk2symbol_1_2_buffer;
    for (unsigned int _i=0; _i<8;_i++)
    {
        int32_t _tmp;
        _tmp = (*split_chunk2symbol_1_2_buffer_ptr_tail++);
        *chunk2symbol_1_2_to_fir_filter_half_1_3_send_buffer++ = (_tmp);
        *chunk2symbol_1_2_to_fir_filter_half_1_2_send_buffer++ = (_tmp);
        *chunk2symbol_1_2_to_fir_filter_half_2_3_send_buffer++ = (_tmp);
        *chunk2symbol_1_2_to_fir_filter_half_2_2_send_buffer++ = (_tmp);
        _tmp = (*split_chunk2symbol_1_2_buffer_ptr_tail++);
        *chunk2symbol_1_2_to_fir_filter_half_1_4_send_buffer++ = (_tmp);
        *chunk2symbol_1_2_to_fir_filter_half_1_3_send_buffer++ = (_tmp);
        *chunk2symbol_1_2_to_fir_filter_half_2_4_send_buffer++ = (_tmp);
        *chunk2symbol_1_2_to_fir_filter_half_2_3_send_buffer++ = (_tmp);
        _tmp = (*split_chunk2symbol_1_2_buffer_ptr_tail++);
        *chunk2symbol_1_2_to_fir_filter_half_1_5_send_buffer++ = (_tmp);
        *chunk2symbol_1_2_to_fir_filter_half_1_4_send_buffer++ = (_tmp);
        *chunk2symbol_1_2_to_fir_filter_half_2_5_send_buffer++ = (_tmp);
        *chunk2symbol_1_2_to_fir_filter_half_2_4_send_buffer++ = (_tmp);
    }
    initDoneWrite(&chunk2symbol_1_2_to_fir_filter_half_1_3_sender, 8, sizeof(int32_t), 0);
    initDoneWrite(&chunk2symbol_1_2_to_fir_filter_half_1_2_sender, 8, sizeof(int32_t), 0);
    initDoneWrite(&chunk2symbol_1_2_to_fir_filter_half_2_3_sender, 8, sizeof(int32_t), 0);
    initDoneWrite(&chunk2symbol_1_2_to_fir_filter_half_2_2_sender, 8, sizeof(int32_t), 0);
    initDoneWrite(&chunk2symbol_1_2_to_fir_filter_half_1_4_sender, 8, sizeof(int32_t), 0);
    initDoneWrite(&chunk2symbol_1_2_to_fir_filter_half_2_4_sender, 8, sizeof(int32_t), 0);
    initDoneWrite(&chunk2symbol_1_2_to_fir_filter_half_1_5_sender, 8, sizeof(int32_t), 0);
    initDoneWrite(&chunk2symbol_1_2_to_fir_filter_half_2_5_sender, 8, sizeof(int32_t), 0);
}

static void chunk2symbol_1_3_init()
{
    (chunk2symbol_1_3_bpsk_mod)=(int32_t*)malloc(sizeof(int32_t)*2);
    (chunk2symbol_1_3_bpsk_mod)[0]=741343;
    (chunk2symbol_1_3_bpsk_mod)[1]=-741343;
    int32_t* chunk2symbol_1_3_to_fir_filter_half_1_6_send_buffer = (int32_t*)initWaitWrite(&chunk2symbol_1_3_to_fir_filter_half_1_6_sender, 8);
    int32_t* chunk2symbol_1_3_to_fir_filter_half_1_5_send_buffer = (int32_t*)initWaitWrite(&chunk2symbol_1_3_to_fir_filter_half_1_5_sender, 8);
    int32_t* chunk2symbol_1_3_to_fir_filter_half_2_6_send_buffer = (int32_t*)initWaitWrite(&chunk2symbol_1_3_to_fir_filter_half_2_6_sender, 8);
    int32_t* chunk2symbol_1_3_to_fir_filter_half_2_5_send_buffer = (int32_t*)initWaitWrite(&chunk2symbol_1_3_to_fir_filter_half_2_5_sender, 8);
    int32_t* chunk2symbol_1_3_to_fir_filter_half_1_7_send_buffer = (int32_t*)initWaitWrite(&chunk2symbol_1_3_to_fir_filter_half_1_7_sender, 8);
    int32_t* chunk2symbol_1_3_to_fir_filter_half_2_7_send_buffer = (int32_t*)initWaitWrite(&chunk2symbol_1_3_to_fir_filter_half_2_7_sender, 8);
    int32_t* split_chunk2symbol_1_3_buffer_ptr_head=split_chunk2symbol_1_3_buffer;
    for (unsigned int _i=0; _i<16; _i++)
    {
        chunk2symbol(chunk2symbol_1_3_bpsk_mod, &diff_encode_1_to_chunk2symbol_1_3_buffer_ptr_tail, &split_chunk2symbol_1_3_buffer_ptr_head);
    }
    int32_t* split_chunk2symbol_1_3_buffer_ptr_tail=split_chunk2symbol_1_3_buffer;
    for (unsigned int _i=0; _i<8;_i++)
    {
        int32_t _tmp;
        _tmp = (*split_chunk2symbol_1_3_buffer_ptr_tail++);
        *chunk2symbol_1_3_to_fir_filter_half_1_6_send_buffer++ = (_tmp);
        *chunk2symbol_1_3_to_fir_filter_half_1_5_send_buffer++ = (_tmp);
        *chunk2symbol_1_3_to_fir_filter_half_2_6_send_buffer++ = (_tmp);
        *chunk2symbol_1_3_to_fir_filter_half_2_5_send_buffer++ = (_tmp);
        _tmp = (*split_chunk2symbol_1_3_buffer_ptr_tail++);
        *chunk2symbol_1_3_to_fir_filter_half_1_7_send_buffer++ = (_tmp);
        *chunk2symbol_1_3_to_fir_filter_half_1_6_send_buffer++ = (_tmp);
        *chunk2symbol_1_3_to_fir_filter_half_2_7_send_buffer++ = (_tmp);
        *chunk2symbol_1_3_to_fir_filter_half_2_6_send_buffer++ = (_tmp);
    }
    initDoneWrite(&chunk2symbol_1_3_to_fir_filter_half_1_6_sender, 8, sizeof(int32_t), 0);
    initDoneWrite(&chunk2symbol_1_3_to_fir_filter_half_1_5_sender, 8, sizeof(int32_t), 0);
    initDoneWrite(&chunk2symbol_1_3_to_fir_filter_half_2_6_sender, 8, sizeof(int32_t), 0);
    initDoneWrite(&chunk2symbol_1_3_to_fir_filter_half_2_5_sender, 8, sizeof(int32_t), 0);
    initDoneWrite(&chunk2symbol_1_3_to_fir_filter_half_1_7_sender, 8, sizeof(int32_t), 0);
    initDoneWrite(&chunk2symbol_1_3_to_fir_filter_half_2_7_sender, 8, sizeof(int32_t), 0);
}

static void FileReader_0_steady()
{
    COREVA_HWACC_MISC_TRACE(8);
    COREVA_HWACC_MISC_TRACE(2);
    volatile int32_t* FileInput_to_FileReader_0_receive_buffer = waitMultiChannelReady(&FileInput_to_FileReader_0_receiver);
    COREVA_HWACC_MISC_TRACE(3);
    if(((FileReader_0_to_scramble_1_buffer_ptr_head-FileReader_0_to_scramble_1_buffer)+1)>9+8)
    {
        FileReader_0_to_scramble_1_buffer_ptr_head=FileReader_0_to_scramble_1_buffer+8;
    }
    COREVA_HWACC_MISC_TRACE(4);
    COREVA_HWACC_MISC_TRACE(4); // SteadyWaitWrite MergeBuffer split
    COREVA_HWACC_MISC_TRACE(5);
    // FileReader SplitBuffer inlined
    for (unsigned int _i=0;_i<1;_i++)
    {
        int32_t _tmp;
        *FileReader_0_to_scramble_1_buffer_ptr_head++ = ((*FileInput_to_FileReader_0_receive_buffer++));
    }
    COREVA_HWACC_MISC_TRACE(7);  // FileReaderSplitBuffer split
    setMultiChannelReady(&FileInput_to_FileReader_0_receiver);
    COREVA_HWACC_MISC_TRACE(7);  // SteadyDoneRead FileReader
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to scramble_1
}

static void scramble_1_steady()
{
    COREVA_HWACC_MISC_TRACE(2);
    if(((FileReader_0_to_scramble_1_buffer_ptr_tail-FileReader_0_to_scramble_1_buffer)+1)>9+8)
        FileReader_0_to_scramble_1_buffer_ptr_tail=FileReader_0_to_scramble_1_buffer+8;
    COREVA_HWACC_MISC_TRACE(3);
    if(((scramble_1_to_diff_encode_1_buffer_ptr_head-scramble_1_to_diff_encode_1_buffer)+8)>72+64)
    {
        scramble_1_to_diff_encode_1_buffer_ptr_head=scramble_1_to_diff_encode_1_buffer+64;
    }
    COREVA_HWACC_MISC_TRACE(4);
    scramble(scramble_1_shift, &FileReader_0_to_scramble_1_buffer_ptr_tail, &scramble_1_to_diff_encode_1_buffer_ptr_head);
    COREVA_HWACC_MISC_TRACE(5);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from FileReader_0
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to diff_encode_1
}

static void diff_encode_1_steady()
{
    COREVA_HWACC_MISC_TRACE(2);
    if(((scramble_1_to_diff_encode_1_buffer_ptr_tail-scramble_1_to_diff_encode_1_buffer)+8)>72+64)
        scramble_1_to_diff_encode_1_buffer_ptr_tail=scramble_1_to_diff_encode_1_buffer+64;
    COREVA_HWACC_MISC_TRACE(3);
    if(((diff_encode_1_to_chunk2symbol_1_1_buffer_ptr_head-diff_encode_1_to_chunk2symbol_1_1_buffer)+3)>27+24)
    {
        diff_encode_1_to_chunk2symbol_1_1_buffer_ptr_head=diff_encode_1_to_chunk2symbol_1_1_buffer+24;
    }
    COREVA_HWACC_MISC_TRACE(4);
    if(((diff_encode_1_to_chunk2symbol_1_2_buffer_ptr_head-diff_encode_1_to_chunk2symbol_1_2_buffer)+3)>27+24)
    {
        diff_encode_1_to_chunk2symbol_1_2_buffer_ptr_head=diff_encode_1_to_chunk2symbol_1_2_buffer+24;
    }
    COREVA_HWACC_MISC_TRACE(4);
    if(((diff_encode_1_to_chunk2symbol_1_3_buffer_ptr_head-diff_encode_1_to_chunk2symbol_1_3_buffer)+2)>18+16)
    {
        diff_encode_1_to_chunk2symbol_1_3_buffer_ptr_head=diff_encode_1_to_chunk2symbol_1_3_buffer+16;
    }
    COREVA_HWACC_MISC_TRACE(4);
    int32_t* split_diff_encode_1_buffer_ptr_head=split_diff_encode_1_buffer;
    COREVA_HWACC_MISC_TRACE(4); // SteadyWaitWrite MergeBuffer split
    for (int _i=0; _i<8; _i++)
        diff_encode(&diff_encode_1_last_out, &scramble_1_to_diff_encode_1_buffer_ptr_tail, &split_diff_encode_1_buffer_ptr_head);
    COREVA_HWACC_MISC_TRACE(5);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from scramble_1
    // SimpleMergeSplitBuffer -> Split
    int32_t* split_diff_encode_1_buffer_ptr_tail=split_diff_encode_1_buffer;
    for (unsigned int _i=0;_i<1;_i++)
    {
        int32_t _tmp;
        *diff_encode_1_to_chunk2symbol_1_1_buffer_ptr_head++ = ((*split_diff_encode_1_buffer_ptr_tail++));
        *diff_encode_1_to_chunk2symbol_1_1_buffer_ptr_head++ = ((*split_diff_encode_1_buffer_ptr_tail++));
        *diff_encode_1_to_chunk2symbol_1_1_buffer_ptr_head++ = ((*split_diff_encode_1_buffer_ptr_tail++));
        *diff_encode_1_to_chunk2symbol_1_2_buffer_ptr_head++ = ((*split_diff_encode_1_buffer_ptr_tail++));
        *diff_encode_1_to_chunk2symbol_1_2_buffer_ptr_head++ = ((*split_diff_encode_1_buffer_ptr_tail++));
        *diff_encode_1_to_chunk2symbol_1_2_buffer_ptr_head++ = ((*split_diff_encode_1_buffer_ptr_tail++));
        *diff_encode_1_to_chunk2symbol_1_3_buffer_ptr_head++ = ((*split_diff_encode_1_buffer_ptr_tail++));
        *diff_encode_1_to_chunk2symbol_1_3_buffer_ptr_head++ = ((*split_diff_encode_1_buffer_ptr_tail++));
    }
    COREVA_HWACC_MISC_TRACE(7);  // SimpleMergeSplitBuffer split
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to chunk2symbol_1_1
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to chunk2symbol_1_2
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to chunk2symbol_1_3
}

static void chunk2symbol_1_1_steady()
{
    COREVA_HWACC_MISC_TRACE(2);
    if(((diff_encode_1_to_chunk2symbol_1_1_buffer_ptr_tail-diff_encode_1_to_chunk2symbol_1_1_buffer)+3)>27+24)
        diff_encode_1_to_chunk2symbol_1_1_buffer_ptr_tail=diff_encode_1_to_chunk2symbol_1_1_buffer+24;
    COREVA_HWACC_MISC_TRACE(3);
    int32_t* chunk2symbol_1_1_to_fir_filter_half_1_1_send_buffer = (int32_t*)steadyClusterWaitWrite(&chunk2symbol_1_1_to_fir_filter_half_1_1_sender);
    COREVA_HWACC_MISC_TRACE(4);
    int32_t* chunk2symbol_1_1_to_fir_filter_half_1_7_send_buffer = (int32_t*)steadyClusterWaitWrite(&chunk2symbol_1_1_to_fir_filter_half_1_7_sender);
    COREVA_HWACC_MISC_TRACE(4);
    int32_t* chunk2symbol_1_1_to_fir_filter_half_2_1_send_buffer = (int32_t*)steadyClusterWaitWrite(&chunk2symbol_1_1_to_fir_filter_half_2_1_sender);
    COREVA_HWACC_MISC_TRACE(4);
    int32_t* chunk2symbol_1_1_to_fir_filter_half_2_7_send_buffer = (int32_t*)steadyClusterWaitWrite(&chunk2symbol_1_1_to_fir_filter_half_2_7_sender);
    COREVA_HWACC_MISC_TRACE(4);
    int32_t* chunk2symbol_1_1_to_fir_filter_half_1_2_send_buffer = (int32_t*)steadyClusterWaitWrite(&chunk2symbol_1_1_to_fir_filter_half_1_2_sender);
    COREVA_HWACC_MISC_TRACE(4);
    int32_t* chunk2symbol_1_1_to_fir_filter_half_2_2_send_buffer = (int32_t*)steadyClusterWaitWrite(&chunk2symbol_1_1_to_fir_filter_half_2_2_sender);
    COREVA_HWACC_MISC_TRACE(4);
    int32_t* split_chunk2symbol_1_1_buffer_ptr_head=split_chunk2symbol_1_1_buffer;
    COREVA_HWACC_MISC_TRACE(4); // SteadyWaitWrite MergeBuffer split
    for (int _i=0; _i<3; _i++)
        chunk2symbol(chunk2symbol_1_1_bpsk_mod, &diff_encode_1_to_chunk2symbol_1_1_buffer_ptr_tail, &split_chunk2symbol_1_1_buffer_ptr_head);
    COREVA_HWACC_MISC_TRACE(5);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from diff_encode_1
    // SimpleMergeSplitBuffer -> Split
    int32_t* split_chunk2symbol_1_1_buffer_ptr_tail=split_chunk2symbol_1_1_buffer;
    for (unsigned int _i=0;_i<1;_i++)
    {
        int32_t _tmp;
        _tmp = (*split_chunk2symbol_1_1_buffer_ptr_tail++);
        *chunk2symbol_1_1_to_fir_filter_half_1_1_send_buffer++ = (_tmp);
        *chunk2symbol_1_1_to_fir_filter_half_1_7_send_buffer++ = (_tmp);
        *chunk2symbol_1_1_to_fir_filter_half_2_1_send_buffer++ = (_tmp);
        *chunk2symbol_1_1_to_fir_filter_half_2_7_send_buffer++ = (_tmp);
        _tmp = (*split_chunk2symbol_1_1_buffer_ptr_tail++);
        *chunk2symbol_1_1_to_fir_filter_half_1_1_send_buffer++ = (_tmp);
        *chunk2symbol_1_1_to_fir_filter_half_2_1_send_buffer++ = (_tmp);
        _tmp = (*split_chunk2symbol_1_1_buffer_ptr_tail++);
        *chunk2symbol_1_1_to_fir_filter_half_1_2_send_buffer++ = (_tmp);
        *chunk2symbol_1_1_to_fir_filter_half_1_1_send_buffer++ = (_tmp);
        *chunk2symbol_1_1_to_fir_filter_half_2_2_send_buffer++ = (_tmp);
        *chunk2symbol_1_1_to_fir_filter_half_2_1_send_buffer++ = (_tmp);
    }
    COREVA_HWACC_MISC_TRACE(7);  // SimpleMergeSplitBuffer split
    steadyClusterDoneWrite(&chunk2symbol_1_1_to_fir_filter_half_1_1_sender);
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to fir_filter_half_1_1
    steadyClusterDoneWrite(&chunk2symbol_1_1_to_fir_filter_half_1_7_sender);
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to fir_filter_half_1_7
    steadyClusterDoneWrite(&chunk2symbol_1_1_to_fir_filter_half_2_1_sender);
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to fir_filter_half_2_1
    steadyClusterDoneWrite(&chunk2symbol_1_1_to_fir_filter_half_2_7_sender);
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to fir_filter_half_2_7
    steadyClusterDoneWrite(&chunk2symbol_1_1_to_fir_filter_half_1_2_sender);
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to fir_filter_half_1_2
    steadyClusterDoneWrite(&chunk2symbol_1_1_to_fir_filter_half_2_2_sender);
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to fir_filter_half_2_2
}

static void chunk2symbol_1_2_steady()
{
    COREVA_HWACC_MISC_TRACE(2);
    if(((diff_encode_1_to_chunk2symbol_1_2_buffer_ptr_tail-diff_encode_1_to_chunk2symbol_1_2_buffer)+3)>27+24)
        diff_encode_1_to_chunk2symbol_1_2_buffer_ptr_tail=diff_encode_1_to_chunk2symbol_1_2_buffer+24;
    COREVA_HWACC_MISC_TRACE(3);
    int32_t* chunk2symbol_1_2_to_fir_filter_half_1_3_send_buffer = (int32_t*)steadyClusterWaitWrite(&chunk2symbol_1_2_to_fir_filter_half_1_3_sender);
    COREVA_HWACC_MISC_TRACE(4);
    int32_t* chunk2symbol_1_2_to_fir_filter_half_1_2_send_buffer = (int32_t*)steadyClusterWaitWrite(&chunk2symbol_1_2_to_fir_filter_half_1_2_sender);
    COREVA_HWACC_MISC_TRACE(4);
    int32_t* chunk2symbol_1_2_to_fir_filter_half_2_3_send_buffer = (int32_t*)steadyClusterWaitWrite(&chunk2symbol_1_2_to_fir_filter_half_2_3_sender);
    COREVA_HWACC_MISC_TRACE(4);
    int32_t* chunk2symbol_1_2_to_fir_filter_half_2_2_send_buffer = (int32_t*)steadyClusterWaitWrite(&chunk2symbol_1_2_to_fir_filter_half_2_2_sender);
    COREVA_HWACC_MISC_TRACE(4);
    int32_t* chunk2symbol_1_2_to_fir_filter_half_1_4_send_buffer = (int32_t*)steadyClusterWaitWrite(&chunk2symbol_1_2_to_fir_filter_half_1_4_sender);
    COREVA_HWACC_MISC_TRACE(4);
    int32_t* chunk2symbol_1_2_to_fir_filter_half_2_4_send_buffer = (int32_t*)steadyClusterWaitWrite(&chunk2symbol_1_2_to_fir_filter_half_2_4_sender);
    COREVA_HWACC_MISC_TRACE(4);
    int32_t* chunk2symbol_1_2_to_fir_filter_half_1_5_send_buffer = (int32_t*)steadyClusterWaitWrite(&chunk2symbol_1_2_to_fir_filter_half_1_5_sender);
    COREVA_HWACC_MISC_TRACE(4);
    int32_t* chunk2symbol_1_2_to_fir_filter_half_2_5_send_buffer = (int32_t*)steadyClusterWaitWrite(&chunk2symbol_1_2_to_fir_filter_half_2_5_sender);
    COREVA_HWACC_MISC_TRACE(4);
    int32_t* split_chunk2symbol_1_2_buffer_ptr_head=split_chunk2symbol_1_2_buffer;
    COREVA_HWACC_MISC_TRACE(4); // SteadyWaitWrite MergeBuffer split
    for (int _i=0; _i<3; _i++)
        chunk2symbol(chunk2symbol_1_2_bpsk_mod, &diff_encode_1_to_chunk2symbol_1_2_buffer_ptr_tail, &split_chunk2symbol_1_2_buffer_ptr_head);
    COREVA_HWACC_MISC_TRACE(5);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from diff_encode_1
    // SimpleMergeSplitBuffer -> Split
    int32_t* split_chunk2symbol_1_2_buffer_ptr_tail=split_chunk2symbol_1_2_buffer;
    for (unsigned int _i=0;_i<1;_i++)
    {
        int32_t _tmp;
        _tmp = (*split_chunk2symbol_1_2_buffer_ptr_tail++);
        *chunk2symbol_1_2_to_fir_filter_half_1_3_send_buffer++ = (_tmp);
        *chunk2symbol_1_2_to_fir_filter_half_1_2_send_buffer++ = (_tmp);
        *chunk2symbol_1_2_to_fir_filter_half_2_3_send_buffer++ = (_tmp);
        *chunk2symbol_1_2_to_fir_filter_half_2_2_send_buffer++ = (_tmp);
        _tmp = (*split_chunk2symbol_1_2_buffer_ptr_tail++);
        *chunk2symbol_1_2_to_fir_filter_half_1_4_send_buffer++ = (_tmp);
        *chunk2symbol_1_2_to_fir_filter_half_1_3_send_buffer++ = (_tmp);
        *chunk2symbol_1_2_to_fir_filter_half_2_4_send_buffer++ = (_tmp);
        *chunk2symbol_1_2_to_fir_filter_half_2_3_send_buffer++ = (_tmp);
        _tmp = (*split_chunk2symbol_1_2_buffer_ptr_tail++);
        *chunk2symbol_1_2_to_fir_filter_half_1_5_send_buffer++ = (_tmp);
        *chunk2symbol_1_2_to_fir_filter_half_1_4_send_buffer++ = (_tmp);
        *chunk2symbol_1_2_to_fir_filter_half_2_5_send_buffer++ = (_tmp);
        *chunk2symbol_1_2_to_fir_filter_half_2_4_send_buffer++ = (_tmp);
    }
    COREVA_HWACC_MISC_TRACE(7);  // SimpleMergeSplitBuffer split
    steadyClusterDoneWrite(&chunk2symbol_1_2_to_fir_filter_half_1_3_sender);
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to fir_filter_half_1_3
    steadyClusterDoneWrite(&chunk2symbol_1_2_to_fir_filter_half_1_2_sender);
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to fir_filter_half_1_2
    steadyClusterDoneWrite(&chunk2symbol_1_2_to_fir_filter_half_2_3_sender);
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to fir_filter_half_2_3
    steadyClusterDoneWrite(&chunk2symbol_1_2_to_fir_filter_half_2_2_sender);
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to fir_filter_half_2_2
    steadyClusterDoneWrite(&chunk2symbol_1_2_to_fir_filter_half_1_4_sender);
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to fir_filter_half_1_4
    steadyClusterDoneWrite(&chunk2symbol_1_2_to_fir_filter_half_2_4_sender);
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to fir_filter_half_2_4
    steadyClusterDoneWrite(&chunk2symbol_1_2_to_fir_filter_half_1_5_sender);
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to fir_filter_half_1_5
    steadyClusterDoneWrite(&chunk2symbol_1_2_to_fir_filter_half_2_5_sender);
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to fir_filter_half_2_5
}

static void chunk2symbol_1_3_steady()
{
    COREVA_HWACC_MISC_TRACE(2);
    if(((diff_encode_1_to_chunk2symbol_1_3_buffer_ptr_tail-diff_encode_1_to_chunk2symbol_1_3_buffer)+2)>18+16)
        diff_encode_1_to_chunk2symbol_1_3_buffer_ptr_tail=diff_encode_1_to_chunk2symbol_1_3_buffer+16;
    COREVA_HWACC_MISC_TRACE(3);
    int32_t* chunk2symbol_1_3_to_fir_filter_half_1_6_send_buffer = (int32_t*)steadyClusterWaitWrite(&chunk2symbol_1_3_to_fir_filter_half_1_6_sender);
    COREVA_HWACC_MISC_TRACE(4);
    int32_t* chunk2symbol_1_3_to_fir_filter_half_1_5_send_buffer = (int32_t*)steadyClusterWaitWrite(&chunk2symbol_1_3_to_fir_filter_half_1_5_sender);
    COREVA_HWACC_MISC_TRACE(4);
    int32_t* chunk2symbol_1_3_to_fir_filter_half_2_6_send_buffer = (int32_t*)steadyClusterWaitWrite(&chunk2symbol_1_3_to_fir_filter_half_2_6_sender);
    COREVA_HWACC_MISC_TRACE(4);
    int32_t* chunk2symbol_1_3_to_fir_filter_half_2_5_send_buffer = (int32_t*)steadyClusterWaitWrite(&chunk2symbol_1_3_to_fir_filter_half_2_5_sender);
    COREVA_HWACC_MISC_TRACE(4);
    int32_t* chunk2symbol_1_3_to_fir_filter_half_1_7_send_buffer = (int32_t*)steadyClusterWaitWrite(&chunk2symbol_1_3_to_fir_filter_half_1_7_sender);
    COREVA_HWACC_MISC_TRACE(4);
    int32_t* chunk2symbol_1_3_to_fir_filter_half_2_7_send_buffer = (int32_t*)steadyClusterWaitWrite(&chunk2symbol_1_3_to_fir_filter_half_2_7_sender);
    COREVA_HWACC_MISC_TRACE(4);
    int32_t* split_chunk2symbol_1_3_buffer_ptr_head=split_chunk2symbol_1_3_buffer;
    COREVA_HWACC_MISC_TRACE(4); // SteadyWaitWrite MergeBuffer split
    for (int _i=0; _i<2; _i++)
        chunk2symbol(chunk2symbol_1_3_bpsk_mod, &diff_encode_1_to_chunk2symbol_1_3_buffer_ptr_tail, &split_chunk2symbol_1_3_buffer_ptr_head);
    COREVA_HWACC_MISC_TRACE(5);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from diff_encode_1
    // SimpleMergeSplitBuffer -> Split
    int32_t* split_chunk2symbol_1_3_buffer_ptr_tail=split_chunk2symbol_1_3_buffer;
    for (unsigned int _i=0;_i<1;_i++)
    {
        int32_t _tmp;
        _tmp = (*split_chunk2symbol_1_3_buffer_ptr_tail++);
        *chunk2symbol_1_3_to_fir_filter_half_1_6_send_buffer++ = (_tmp);
        *chunk2symbol_1_3_to_fir_filter_half_1_5_send_buffer++ = (_tmp);
        *chunk2symbol_1_3_to_fir_filter_half_2_6_send_buffer++ = (_tmp);
        *chunk2symbol_1_3_to_fir_filter_half_2_5_send_buffer++ = (_tmp);
        _tmp = (*split_chunk2symbol_1_3_buffer_ptr_tail++);
        *chunk2symbol_1_3_to_fir_filter_half_1_7_send_buffer++ = (_tmp);
        *chunk2symbol_1_3_to_fir_filter_half_1_6_send_buffer++ = (_tmp);
        *chunk2symbol_1_3_to_fir_filter_half_2_7_send_buffer++ = (_tmp);
        *chunk2symbol_1_3_to_fir_filter_half_2_6_send_buffer++ = (_tmp);
    }
    COREVA_HWACC_MISC_TRACE(7);  // SimpleMergeSplitBuffer split
    steadyClusterDoneWrite(&chunk2symbol_1_3_to_fir_filter_half_1_6_sender);
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to fir_filter_half_1_6
    steadyClusterDoneWrite(&chunk2symbol_1_3_to_fir_filter_half_1_5_sender);
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to fir_filter_half_1_5
    steadyClusterDoneWrite(&chunk2symbol_1_3_to_fir_filter_half_2_6_sender);
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to fir_filter_half_2_6
    steadyClusterDoneWrite(&chunk2symbol_1_3_to_fir_filter_half_2_5_sender);
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to fir_filter_half_2_5
    steadyClusterDoneWrite(&chunk2symbol_1_3_to_fir_filter_half_1_7_sender);
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to fir_filter_half_1_7
    steadyClusterDoneWrite(&chunk2symbol_1_3_to_fir_filter_half_2_7_sender);
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to fir_filter_half_2_7
}

static void InitChannels_cpu_0_0_0()
{
    // InitRead FileReader_0_to_scramble_1 (8 8 7 1 1)
    FileReader_0_to_scramble_1_buffer_ptr_tail=FileReader_0_to_scramble_1_buffer;
    // InitRead diff_encode_1_to_chunk2symbol_1_1 (24 24 23 3 3)
    diff_encode_1_to_chunk2symbol_1_1_buffer_ptr_tail=diff_encode_1_to_chunk2symbol_1_1_buffer;
    // InitRead diff_encode_1_to_chunk2symbol_1_2 (24 24 23 3 3)
    diff_encode_1_to_chunk2symbol_1_2_buffer_ptr_tail=diff_encode_1_to_chunk2symbol_1_2_buffer;
    // InitRead diff_encode_1_to_chunk2symbol_1_3 (16 16 15 2 2)
    diff_encode_1_to_chunk2symbol_1_3_buffer_ptr_tail=diff_encode_1_to_chunk2symbol_1_3_buffer;
    // InitRead scramble_1_to_diff_encode_1 (64 64 63 8 8)
    scramble_1_to_diff_encode_1_buffer_ptr_tail=scramble_1_to_diff_encode_1_buffer;
    // InitWrite chunk2symbol_1_1_to_fir_filter_half_1_7 (8 1 0 1 2)
    initSender(&chunk2symbol_1_1_to_fir_filter_half_1_7_sender, 0, 7, 1, sizeof(int32_t), 11, DATA_MEM);
    // InitWrite chunk2symbol_1_1_to_fir_filter_half_2_7 (8 1 0 1 2)
    initSender(&chunk2symbol_1_1_to_fir_filter_half_2_7_sender, 0, 7, 1, sizeof(int32_t), 11, DATA_MEM);
    // InitWrite chunk2symbol_1_3_to_fir_filter_half_1_7 (8 1 0 1 2)
    initSender(&chunk2symbol_1_3_to_fir_filter_half_1_7_sender, 0, 7, 1, sizeof(int32_t), 11, DATA_MEM);
    // InitWrite chunk2symbol_1_3_to_fir_filter_half_2_7 (8 1 0 1 2)
    initSender(&chunk2symbol_1_3_to_fir_filter_half_2_7_sender, 0, 7, 1, sizeof(int32_t), 11, DATA_MEM);
    // InitWrite chunk2symbol_1_3_to_fir_filter_half_1_6 (16 0 0 2 3)
    initSender(&chunk2symbol_1_3_to_fir_filter_half_1_6_sender, 0, 6, 2, sizeof(int32_t), 11, DATA_MEM);
    // InitWrite chunk2symbol_1_3_to_fir_filter_half_2_6 (16 0 0 2 3)
    initSender(&chunk2symbol_1_3_to_fir_filter_half_2_6_sender, 0, 6, 2, sizeof(int32_t), 11, DATA_MEM);
    // InitWrite chunk2symbol_1_1_to_fir_filter_half_2_1 (24 0 0 3 4)
    initSender(&chunk2symbol_1_1_to_fir_filter_half_2_1_sender, 0, 5, 3, sizeof(int32_t), 11, DATA_MEM);
    // InitWrite chunk2symbol_1_2_to_fir_filter_half_2_5 (8 0 0 1 2)
    initSender(&chunk2symbol_1_2_to_fir_filter_half_2_5_sender, 0, 5, 1, sizeof(int32_t), 11, DATA_MEM);
    // InitWrite chunk2symbol_1_3_to_fir_filter_half_2_5 (8 0 0 1 2)
    initSender(&chunk2symbol_1_3_to_fir_filter_half_2_5_sender, 0, 5, 1, sizeof(int32_t), 11, DATA_MEM);
    // InitWrite chunk2symbol_1_2_to_fir_filter_half_1_3 (16 0 0 2 3)
    initSender(&chunk2symbol_1_2_to_fir_filter_half_1_3_sender, 0, 4, 2, sizeof(int32_t), 11, DATA_MEM);
    // InitWrite chunk2symbol_1_2_to_fir_filter_half_1_4 (16 0 0 2 3)
    initSender(&chunk2symbol_1_2_to_fir_filter_half_1_4_sender, 0, 4, 2, sizeof(int32_t), 11, DATA_MEM);
    // InitWrite chunk2symbol_1_1_to_fir_filter_half_1_1 (24 0 0 3 4)
    initSender(&chunk2symbol_1_1_to_fir_filter_half_1_1_sender, 0, 3, 3, sizeof(int32_t), 11, DATA_MEM);
    // InitWrite chunk2symbol_1_2_to_fir_filter_half_1_5 (8 0 0 1 2)
    initSender(&chunk2symbol_1_2_to_fir_filter_half_1_5_sender, 0, 3, 1, sizeof(int32_t), 11, DATA_MEM);
    // InitWrite chunk2symbol_1_3_to_fir_filter_half_1_5 (8 0 0 1 2)
    initSender(&chunk2symbol_1_3_to_fir_filter_half_1_5_sender, 0, 3, 1, sizeof(int32_t), 11, DATA_MEM);
    // InitWrite chunk2symbol_1_1_to_fir_filter_half_1_2 (8 0 0 1 2)
    initSender(&chunk2symbol_1_1_to_fir_filter_half_1_2_sender, 0, 2, 1, sizeof(int32_t), 11, DATA_MEM);
    // InitWrite chunk2symbol_1_2_to_fir_filter_half_1_2 (8 0 0 1 2)
    initSender(&chunk2symbol_1_2_to_fir_filter_half_1_2_sender, 0, 2, 1, sizeof(int32_t), 11, DATA_MEM);
    // InitWrite chunk2symbol_1_2_to_fir_filter_half_2_4 (16 0 0 2 3)
    initSender(&chunk2symbol_1_2_to_fir_filter_half_2_4_sender, 0, 2, 2, sizeof(int32_t), 11, DATA_MEM);
    // InitWrite chunk2symbol_1_1_to_fir_filter_half_2_2 (8 0 0 1 2)
    initSender(&chunk2symbol_1_1_to_fir_filter_half_2_2_sender, 0, 1, 1, sizeof(int32_t), 11, DATA_MEM);
    // InitWrite chunk2symbol_1_2_to_fir_filter_half_2_2 (8 0 0 1 2)
    initSender(&chunk2symbol_1_2_to_fir_filter_half_2_2_sender, 0, 1, 1, sizeof(int32_t), 11, DATA_MEM);
    // InitWrite chunk2symbol_1_2_to_fir_filter_half_2_3 (16 0 0 2 3)
    initSender(&chunk2symbol_1_2_to_fir_filter_half_2_3_sender, 0, 1, 2, sizeof(int32_t), 11, DATA_MEM);
    // InitWrite FileReader_0_to_scramble_1 (8 8 7 1 1)
    FileReader_0_to_scramble_1_buffer_ptr_head=FileReader_0_to_scramble_1_buffer;
    // InitWrite diff_encode_1_to_chunk2symbol_1_1 (24 24 23 3 3)
    diff_encode_1_to_chunk2symbol_1_1_buffer_ptr_head=diff_encode_1_to_chunk2symbol_1_1_buffer;
    // InitWrite diff_encode_1_to_chunk2symbol_1_2 (24 24 23 3 3)
    diff_encode_1_to_chunk2symbol_1_2_buffer_ptr_head=diff_encode_1_to_chunk2symbol_1_2_buffer;
    // InitWrite diff_encode_1_to_chunk2symbol_1_3 (16 16 15 2 2)
    diff_encode_1_to_chunk2symbol_1_3_buffer_ptr_head=diff_encode_1_to_chunk2symbol_1_3_buffer;
    // InitWrite scramble_1_to_diff_encode_1 (64 64 63 8 8)
    scramble_1_to_diff_encode_1_buffer_ptr_head=scramble_1_to_diff_encode_1_buffer;
    FileReader_0_filename = "input.stream";
    //net_: 1 init: 8
    volatile int32_t* FileInput_to_FileReader_0_receive_buffer = (int32_t*)fioAllocateSharedMem(0, 8 * sizeof(int32_t));
    initFioReadMultiChannel(&FileInput_to_FileReader_0_receiver, 0, FileReader_0_filename, (void*)FileInput_to_FileReader_0_receive_buffer, 8 * sizeof(int32_t), 1);
}

static void InitFilters_cpu_0_0_0()
{
    FileReader_0_init();
    scramble_1_init();
    diff_encode_1_init();
    chunk2symbol_1_1_init();
    chunk2symbol_1_2_init();
    chunk2symbol_1_3_init();
}

static void PrimePump_cpu_0_0_0()
{
    for (int iteration=0; iteration<6; iteration++)
    {
        if (iteration >= 0)
        {
            FileReader_0_steady();
        }
        if (iteration >= 1)
        {
            scramble_1_steady();
        }
        if (iteration >= 2)
        {
            diff_encode_1_steady();
        }
        if (iteration >= 3)
        {
            chunk2symbol_1_1_steady();
            chunk2symbol_1_2_steady();
            chunk2symbol_1_3_steady();
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
        FileReader_0_steady();
        COREVA_HWACC_MISC_TRACE(11);
        scramble_1_steady();
        COREVA_HWACC_MISC_TRACE(12);
        diff_encode_1_steady();
        COREVA_HWACC_MISC_TRACE(13);
        chunk2symbol_1_1_steady();
        COREVA_HWACC_MISC_TRACE(14);
        chunk2symbol_1_2_steady();
        COREVA_HWACC_MISC_TRACE(15);
        chunk2symbol_1_3_steady();
    }
    	return 0;
}

