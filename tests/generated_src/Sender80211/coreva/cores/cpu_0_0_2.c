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

int32_t* fir_filter_half_1_2_rcc_taps = {0};
int32_t* fir_filter_half_2_4_rcc_taps = {0};
char* FileWriter_1_filename;

void InitStatics_cpu_0_0_2()
{
}

MULTI_CHANNEL_SYNC chunk2symbol_1_1_to_fir_filter_half_1_2_receiver;
MULTI_CHANNEL_SYNC chunk2symbol_1_2_to_fir_filter_half_1_2_receiver;
int32_t* fir_filter_half_1_2_to_FileWriter_1_buffer_ptr_tail = 0;
int32_t merge_fir_filter_half_1_2_buffer[1];
MULTI_CHANNEL_SYNC chunk2symbol_1_2_to_fir_filter_half_2_4_receiver;
int32_t* fir_filter_half_2_4_to_FileWriter_1_buffer_ptr_tail = 0;
MULTI_CHANNEL_SYNC fir_filter_half_1_1_to_FileWriter_1_receiver;
int32_t* fir_filter_half_1_2_to_FileWriter_1_buffer_ptr_head = 0;
int32_t fir_filter_half_1_2_to_FileWriter_1_buffer[16+0];
MULTI_CHANNEL_SYNC fir_filter_half_1_3_to_FileWriter_1_receiver;
MULTI_CHANNEL_SYNC fir_filter_half_1_4_to_FileWriter_1_receiver;
MULTI_CHANNEL_SYNC fir_filter_half_1_5_to_FileWriter_1_receiver;
MULTI_CHANNEL_SYNC fir_filter_half_1_6_to_FileWriter_1_receiver;
MULTI_CHANNEL_SYNC fir_filter_half_1_7_to_FileWriter_1_receiver;
MULTI_CHANNEL_SYNC fir_filter_half_2_1_to_FileWriter_1_receiver;
MULTI_CHANNEL_SYNC fir_filter_half_2_2_to_FileWriter_1_receiver;
MULTI_CHANNEL_SYNC fir_filter_half_2_3_to_FileWriter_1_receiver;
int32_t* fir_filter_half_2_4_to_FileWriter_1_buffer_ptr_head = 0;
int32_t fir_filter_half_2_4_to_FileWriter_1_buffer[16+0];
MULTI_CHANNEL_SYNC fir_filter_half_2_5_to_FileWriter_1_receiver;
MULTI_CHANNEL_SYNC fir_filter_half_2_6_to_FileWriter_1_receiver;
MULTI_CHANNEL_SYNC fir_filter_half_2_7_to_FileWriter_1_receiver;
MULTI_CHANNEL_SYNC FileWriter_1_to_FileOutput_sender;

// WORK
static void fir_filter_half(int32_t* restrict rcc_taps, int32_t** restrict merge__buffer_ptr_tail, int32_t** restrict _buffer_ptr_head)
{
    int32_t previous;
    int32_t current;
    int32_t j;

    (previous)=(*(*merge__buffer_ptr_tail+0));
    (current)=(*(*merge__buffer_ptr_tail+1));
    for ((j)=0; (j)<8; (j)++)
        {
        *(*_buffer_ptr_head)++ = (((rcc_taps)[(j)]*((current)>>10)+(rcc_taps)[(j)+8]*((previous)>>10))>>10);
    }
    (*(*merge__buffer_ptr_tail)++);
}

// PREWORK + INIT
static void fir_filter_half_1_2_init()
{
    (fir_filter_half_1_2_rcc_taps)=(int32_t*)malloc(sizeof(int32_t)*16);
    (fir_filter_half_1_2_rcc_taps)[0]=0;
    (fir_filter_half_1_2_rcc_taps)[1]=-666;
    (fir_filter_half_1_2_rcc_taps)[2]=69361;
    (fir_filter_half_1_2_rcc_taps)[3]=157964;
    (fir_filter_half_1_2_rcc_taps)[4]=255889;
    (fir_filter_half_1_2_rcc_taps)[5]=351141;
    (fir_filter_half_1_2_rcc_taps)[6]=430952;
    (fir_filter_half_1_2_rcc_taps)[7]=484036;
    (fir_filter_half_1_2_rcc_taps)[8]=502648;
    (fir_filter_half_1_2_rcc_taps)[9]=484036;
    (fir_filter_half_1_2_rcc_taps)[10]=430952;
    (fir_filter_half_1_2_rcc_taps)[11]=351141;
    (fir_filter_half_1_2_rcc_taps)[12]=255889;
    (fir_filter_half_1_2_rcc_taps)[13]=157964;
    (fir_filter_half_1_2_rcc_taps)[14]=69361;
    (fir_filter_half_1_2_rcc_taps)[15]=-666;
}

static void fir_filter_half_2_4_init()
{
    (fir_filter_half_2_4_rcc_taps)=(int32_t*)malloc(sizeof(int32_t)*16);
    (fir_filter_half_2_4_rcc_taps)[0]=0;
    (fir_filter_half_2_4_rcc_taps)[1]=-666;
    (fir_filter_half_2_4_rcc_taps)[2]=69361;
    (fir_filter_half_2_4_rcc_taps)[3]=157964;
    (fir_filter_half_2_4_rcc_taps)[4]=255889;
    (fir_filter_half_2_4_rcc_taps)[5]=351141;
    (fir_filter_half_2_4_rcc_taps)[6]=430952;
    (fir_filter_half_2_4_rcc_taps)[7]=484036;
    (fir_filter_half_2_4_rcc_taps)[8]=502648;
    (fir_filter_half_2_4_rcc_taps)[9]=484036;
    (fir_filter_half_2_4_rcc_taps)[10]=430952;
    (fir_filter_half_2_4_rcc_taps)[11]=351141;
    (fir_filter_half_2_4_rcc_taps)[12]=255889;
    (fir_filter_half_2_4_rcc_taps)[13]=157964;
    (fir_filter_half_2_4_rcc_taps)[14]=69361;
    (fir_filter_half_2_4_rcc_taps)[15]=-666;
}

static void FileWriter_1_init()
{
    FileWriter_1_filename = "output.stream";
}

static void fir_filter_half_1_2_steady()
{
    COREVA_HWACC_MISC_TRACE(2);
    int32_t* chunk2symbol_1_1_to_fir_filter_half_1_2_receive_buffer = (int32_t*)steadyClusterWaitRead(&chunk2symbol_1_1_to_fir_filter_half_1_2_receiver, 1, sizeof(int32_t), 1, 0, 0, 2);
    COREVA_HWACC_MISC_TRACE(3);
    int32_t* chunk2symbol_1_2_to_fir_filter_half_1_2_receive_buffer = (int32_t*)steadyClusterWaitRead(&chunk2symbol_1_2_to_fir_filter_half_1_2_receiver, 1, sizeof(int32_t), 1, 0, 0, 2);
    COREVA_HWACC_MISC_TRACE(3);
    int32_t* merge_fir_filter_half_1_2_buffer_ptr_head=merge_fir_filter_half_1_2_buffer;
    // SimpleMergeSplitBuffer -> Merge
    for (unsigned int _i=0;_i<0;_i++)
    {
        *merge_fir_filter_half_1_2_buffer_ptr_head++ = ((*chunk2symbol_1_1_to_fir_filter_half_1_2_receive_buffer++));
        *merge_fir_filter_half_1_2_buffer_ptr_head++ = ((*chunk2symbol_1_2_to_fir_filter_half_1_2_receive_buffer++));
    }
    int32_t* merge_fir_filter_half_1_2_buffer_ptr_tail=merge_fir_filter_half_1_2_buffer;
    COREVA_HWACC_MISC_TRACE(3);
    if(((fir_filter_half_1_2_to_FileWriter_1_buffer_ptr_head-fir_filter_half_1_2_to_FileWriter_1_buffer)+8)>16+0)
    {
        fir_filter_half_1_2_to_FileWriter_1_buffer_ptr_head=fir_filter_half_1_2_to_FileWriter_1_buffer+0;
    }
    COREVA_HWACC_MISC_TRACE(4);
    unsigned int _i=0;
    {
        fir_filter_half(fir_filter_half_1_2_rcc_taps, &merge_fir_filter_half_1_2_buffer_ptr_tail, &fir_filter_half_1_2_to_FileWriter_1_buffer_ptr_head);
        for (int _k=0; _k<1; _k++)
        {
            (*merge_fir_filter_half_1_2_buffer_ptr_tail++);
        }
    }
    COREVA_HWACC_MISC_TRACE(5);
    steadyClusterDoneRead(&chunk2symbol_1_1_to_fir_filter_half_1_2_receiver);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from chunk2symbol_1_1
    steadyClusterDoneRead(&chunk2symbol_1_2_to_fir_filter_half_1_2_receiver);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from chunk2symbol_1_2
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to FileWriter_1
}

static void fir_filter_half_2_4_steady()
{
    COREVA_HWACC_MISC_TRACE(2);
    int32_t* chunk2symbol_1_2_to_fir_filter_half_2_4_receive_buffer = (int32_t*)steadyClusterWaitRead(&chunk2symbol_1_2_to_fir_filter_half_2_4_receiver, 2, sizeof(int32_t), 1, 0, 0, 2);
    COREVA_HWACC_MISC_TRACE(3);
    if(((fir_filter_half_2_4_to_FileWriter_1_buffer_ptr_head-fir_filter_half_2_4_to_FileWriter_1_buffer)+8)>16+0)
    {
        fir_filter_half_2_4_to_FileWriter_1_buffer_ptr_head=fir_filter_half_2_4_to_FileWriter_1_buffer+0;
    }
    COREVA_HWACC_MISC_TRACE(4);
    unsigned int _i=0;
    {
        fir_filter_half(fir_filter_half_2_4_rcc_taps, &chunk2symbol_1_2_to_fir_filter_half_2_4_receive_buffer, &fir_filter_half_2_4_to_FileWriter_1_buffer_ptr_head);
        for (int _k=0; _k<1; _k++)
        {
            (*chunk2symbol_1_2_to_fir_filter_half_2_4_receive_buffer++);
        }
    }
    COREVA_HWACC_MISC_TRACE(5);
    steadyClusterDoneRead(&chunk2symbol_1_2_to_fir_filter_half_2_4_receiver);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from chunk2symbol_1_2
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to FileWriter_1
}

static void FileWriter_1_steady()
{
    COREVA_HWACC_MISC_TRACE(2);
    int32_t* fir_filter_half_1_1_to_FileWriter_1_receive_buffer = (int32_t*)steadyClusterWaitRead(&fir_filter_half_1_1_to_FileWriter_1_receiver, 16, sizeof(int32_t), 0, 0, 0, 1);
    COREVA_HWACC_MISC_TRACE(3);
    if(((fir_filter_half_1_2_to_FileWriter_1_buffer_ptr_tail-fir_filter_half_1_2_to_FileWriter_1_buffer)+8)>16+0)
        fir_filter_half_1_2_to_FileWriter_1_buffer_ptr_tail=fir_filter_half_1_2_to_FileWriter_1_buffer+0;
    COREVA_HWACC_MISC_TRACE(3);
    int32_t* fir_filter_half_1_3_to_FileWriter_1_receive_buffer = (int32_t*)steadyClusterWaitRead(&fir_filter_half_1_3_to_FileWriter_1_receiver, 8, sizeof(int32_t), 0, 0, 0, 1);
    COREVA_HWACC_MISC_TRACE(3);
    int32_t* fir_filter_half_1_4_to_FileWriter_1_receive_buffer = (int32_t*)steadyClusterWaitRead(&fir_filter_half_1_4_to_FileWriter_1_receiver, 8, sizeof(int32_t), 0, 0, 0, 1);
    COREVA_HWACC_MISC_TRACE(3);
    int32_t* fir_filter_half_1_5_to_FileWriter_1_receive_buffer = (int32_t*)steadyClusterWaitRead(&fir_filter_half_1_5_to_FileWriter_1_receiver, 8, sizeof(int32_t), 0, 0, 0, 1);
    COREVA_HWACC_MISC_TRACE(3);
    int32_t* fir_filter_half_1_6_to_FileWriter_1_receive_buffer = (int32_t*)steadyClusterWaitRead(&fir_filter_half_1_6_to_FileWriter_1_receiver, 8, sizeof(int32_t), 0, 0, 0, 1);
    COREVA_HWACC_MISC_TRACE(3);
    int32_t* fir_filter_half_1_7_to_FileWriter_1_receive_buffer = (int32_t*)steadyClusterWaitRead(&fir_filter_half_1_7_to_FileWriter_1_receiver, 8, sizeof(int32_t), 0, 0, 0, 1);
    COREVA_HWACC_MISC_TRACE(3);
    int32_t* fir_filter_half_2_1_to_FileWriter_1_receive_buffer = (int32_t*)steadyClusterWaitRead(&fir_filter_half_2_1_to_FileWriter_1_receiver, 16, sizeof(int32_t), 0, 0, 0, 1);
    COREVA_HWACC_MISC_TRACE(3);
    int32_t* fir_filter_half_2_2_to_FileWriter_1_receive_buffer = (int32_t*)steadyClusterWaitRead(&fir_filter_half_2_2_to_FileWriter_1_receiver, 8, sizeof(int32_t), 0, 0, 0, 1);
    COREVA_HWACC_MISC_TRACE(3);
    int32_t* fir_filter_half_2_3_to_FileWriter_1_receive_buffer = (int32_t*)steadyClusterWaitRead(&fir_filter_half_2_3_to_FileWriter_1_receiver, 8, sizeof(int32_t), 0, 0, 0, 1);
    COREVA_HWACC_MISC_TRACE(3);
    if(((fir_filter_half_2_4_to_FileWriter_1_buffer_ptr_tail-fir_filter_half_2_4_to_FileWriter_1_buffer)+8)>16+0)
        fir_filter_half_2_4_to_FileWriter_1_buffer_ptr_tail=fir_filter_half_2_4_to_FileWriter_1_buffer+0;
    COREVA_HWACC_MISC_TRACE(3);
    int32_t* fir_filter_half_2_5_to_FileWriter_1_receive_buffer = (int32_t*)steadyClusterWaitRead(&fir_filter_half_2_5_to_FileWriter_1_receiver, 8, sizeof(int32_t), 0, 0, 0, 1);
    COREVA_HWACC_MISC_TRACE(3);
    int32_t* fir_filter_half_2_6_to_FileWriter_1_receive_buffer = (int32_t*)steadyClusterWaitRead(&fir_filter_half_2_6_to_FileWriter_1_receiver, 8, sizeof(int32_t), 0, 0, 0, 1);
    COREVA_HWACC_MISC_TRACE(3);
    int32_t* fir_filter_half_2_7_to_FileWriter_1_receive_buffer = (int32_t*)steadyClusterWaitRead(&fir_filter_half_2_7_to_FileWriter_1_receiver, 8, sizeof(int32_t), 0, 0, 0, 1);
    COREVA_HWACC_MISC_TRACE(3);
    volatile int32_t* FileWriter_1_to_FileOutput_send_buffer = waitMultiChannelReady(&FileWriter_1_to_FileOutput_sender);
    COREVA_HWACC_MISC_TRACE(4); // SteadyWaitWrite FileWriter
    for (int _i=0; _i<1;_i++)
    {
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_1_1_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_2_1_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_1_1_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_2_1_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_1_1_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_2_1_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_1_1_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_2_1_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_1_1_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_2_1_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_1_1_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_2_1_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_1_1_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_2_1_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_1_1_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_2_1_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_1_1_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_2_1_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_1_1_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_2_1_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_1_1_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_2_1_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_1_1_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_2_1_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_1_1_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_2_1_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_1_1_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_2_1_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_1_1_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_2_1_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_1_1_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_2_1_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_1_2_to_FileWriter_1_buffer_ptr_tail++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_2_2_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_1_2_to_FileWriter_1_buffer_ptr_tail++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_2_2_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_1_2_to_FileWriter_1_buffer_ptr_tail++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_2_2_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_1_2_to_FileWriter_1_buffer_ptr_tail++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_2_2_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_1_2_to_FileWriter_1_buffer_ptr_tail++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_2_2_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_1_2_to_FileWriter_1_buffer_ptr_tail++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_2_2_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_1_2_to_FileWriter_1_buffer_ptr_tail++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_2_2_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_1_2_to_FileWriter_1_buffer_ptr_tail++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_2_2_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_1_3_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_2_3_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_1_3_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_2_3_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_1_3_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_2_3_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_1_3_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_2_3_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_1_3_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_2_3_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_1_3_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_2_3_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_1_3_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_2_3_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_1_3_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_2_3_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_1_4_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_2_4_to_FileWriter_1_buffer_ptr_tail++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_1_4_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_2_4_to_FileWriter_1_buffer_ptr_tail++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_1_4_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_2_4_to_FileWriter_1_buffer_ptr_tail++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_1_4_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_2_4_to_FileWriter_1_buffer_ptr_tail++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_1_4_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_2_4_to_FileWriter_1_buffer_ptr_tail++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_1_4_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_2_4_to_FileWriter_1_buffer_ptr_tail++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_1_4_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_2_4_to_FileWriter_1_buffer_ptr_tail++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_1_4_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_2_4_to_FileWriter_1_buffer_ptr_tail++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_1_5_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_2_5_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_1_5_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_2_5_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_1_5_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_2_5_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_1_5_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_2_5_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_1_5_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_2_5_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_1_5_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_2_5_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_1_5_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_2_5_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_1_5_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_2_5_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_1_6_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_2_6_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_1_6_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_2_6_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_1_6_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_2_6_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_1_6_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_2_6_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_1_6_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_2_6_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_1_6_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_2_6_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_1_6_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_2_6_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_1_6_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_2_6_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_1_7_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_2_7_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_1_7_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_2_7_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_1_7_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_2_7_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_1_7_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_2_7_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_1_7_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_2_7_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_1_7_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_2_7_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_1_7_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_2_7_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_1_7_to_FileWriter_1_receive_buffer++));
            *FileWriter_1_to_FileOutput_send_buffer++ = ((*fir_filter_half_2_7_to_FileWriter_1_receive_buffer++));
    }
    COREVA_HWACC_MISC_TRACE(4);
    COREVA_HWACC_MISC_TRACE(5);
    steadyClusterDoneRead(&fir_filter_half_1_1_to_FileWriter_1_receiver);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from fir_filter_half_1_1
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from fir_filter_half_1_2
    steadyClusterDoneRead(&fir_filter_half_1_3_to_FileWriter_1_receiver);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from fir_filter_half_1_3
    steadyClusterDoneRead(&fir_filter_half_1_4_to_FileWriter_1_receiver);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from fir_filter_half_1_4
    steadyClusterDoneRead(&fir_filter_half_1_5_to_FileWriter_1_receiver);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from fir_filter_half_1_5
    steadyClusterDoneRead(&fir_filter_half_1_6_to_FileWriter_1_receiver);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from fir_filter_half_1_6
    steadyClusterDoneRead(&fir_filter_half_1_7_to_FileWriter_1_receiver);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from fir_filter_half_1_7
    steadyClusterDoneRead(&fir_filter_half_2_1_to_FileWriter_1_receiver);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from fir_filter_half_2_1
    steadyClusterDoneRead(&fir_filter_half_2_2_to_FileWriter_1_receiver);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from fir_filter_half_2_2
    steadyClusterDoneRead(&fir_filter_half_2_3_to_FileWriter_1_receiver);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from fir_filter_half_2_3
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from fir_filter_half_2_4
    steadyClusterDoneRead(&fir_filter_half_2_5_to_FileWriter_1_receiver);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from fir_filter_half_2_5
    steadyClusterDoneRead(&fir_filter_half_2_6_to_FileWriter_1_receiver);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from fir_filter_half_2_6
    steadyClusterDoneRead(&fir_filter_half_2_7_to_FileWriter_1_receiver);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from fir_filter_half_2_7
    setMultiChannelReady(&FileWriter_1_to_FileOutput_sender);
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite FileWriter
    COREVA_HWACC_MISC_TRACE(9);
}

static void InitChannels_cpu_0_0_2()
{
    // InitRead chunk2symbol_1_1_to_fir_filter_half_1_2 (8 0 0 1 2)
    initReceiver(&chunk2symbol_1_1_to_fir_filter_half_1_2_receiver, 0, 0, 1, sizeof(int32_t), 0, 11, 1, DATA_MEM);
    // InitRead chunk2symbol_1_2_to_fir_filter_half_1_2 (8 0 0 1 2)
    initReceiver(&chunk2symbol_1_2_to_fir_filter_half_1_2_receiver, 0, 0, 1, sizeof(int32_t), 0, 11, 1, DATA_MEM);
    // InitRead chunk2symbol_1_2_to_fir_filter_half_2_4 (16 0 0 2 3)
    initReceiver(&chunk2symbol_1_2_to_fir_filter_half_2_4_receiver, 0, 0, 2, sizeof(int32_t), 0, 11, 1, DATA_MEM);
    // InitRead fir_filter_half_2_2_to_FileWriter_1 (0 0 0 8 8)
    initReceiver(&fir_filter_half_2_2_to_FileWriter_1_receiver, 0, 1, 8, sizeof(int32_t), 0, 3, 0, DATA_MEM);
    // InitRead fir_filter_half_2_3_to_FileWriter_1 (0 0 0 8 8)
    initReceiver(&fir_filter_half_2_3_to_FileWriter_1_receiver, 0, 1, 8, sizeof(int32_t), 0, 3, 0, DATA_MEM);
    // InitRead fir_filter_half_1_2_to_FileWriter_1 (0 0 0 8 8)
    fir_filter_half_1_2_to_FileWriter_1_buffer_ptr_tail=fir_filter_half_1_2_to_FileWriter_1_buffer;
    // InitRead fir_filter_half_2_4_to_FileWriter_1 (0 0 0 8 8)
    fir_filter_half_2_4_to_FileWriter_1_buffer_ptr_tail=fir_filter_half_2_4_to_FileWriter_1_buffer;
    // InitWrite fir_filter_half_1_2_to_FileWriter_1 (0 0 0 8 8)
    fir_filter_half_1_2_to_FileWriter_1_buffer_ptr_head=fir_filter_half_1_2_to_FileWriter_1_buffer;
    // InitWrite fir_filter_half_2_4_to_FileWriter_1 (0 0 0 8 8)
    fir_filter_half_2_4_to_FileWriter_1_buffer_ptr_head=fir_filter_half_2_4_to_FileWriter_1_buffer;
    // InitRead fir_filter_half_1_1_to_FileWriter_1 (0 0 0 16 16)
    initReceiver(&fir_filter_half_1_1_to_FileWriter_1_receiver, 0, 3, 16, sizeof(int32_t), 0, 3, 0, DATA_MEM);
    // InitRead fir_filter_half_1_5_to_FileWriter_1 (0 0 0 8 8)
    initReceiver(&fir_filter_half_1_5_to_FileWriter_1_receiver, 0, 3, 8, sizeof(int32_t), 0, 3, 0, DATA_MEM);
    // InitRead fir_filter_half_1_3_to_FileWriter_1 (0 0 0 8 8)
    initReceiver(&fir_filter_half_1_3_to_FileWriter_1_receiver, 0, 4, 8, sizeof(int32_t), 0, 3, 0, DATA_MEM);
    // InitRead fir_filter_half_1_4_to_FileWriter_1 (0 0 0 8 8)
    initReceiver(&fir_filter_half_1_4_to_FileWriter_1_receiver, 0, 4, 8, sizeof(int32_t), 0, 3, 0, DATA_MEM);
    // InitRead fir_filter_half_2_1_to_FileWriter_1 (0 0 0 16 16)
    initReceiver(&fir_filter_half_2_1_to_FileWriter_1_receiver, 0, 5, 16, sizeof(int32_t), 0, 3, 0, DATA_MEM);
    // InitRead fir_filter_half_2_5_to_FileWriter_1 (0 0 0 8 8)
    initReceiver(&fir_filter_half_2_5_to_FileWriter_1_receiver, 0, 5, 8, sizeof(int32_t), 0, 3, 0, DATA_MEM);
    // InitRead fir_filter_half_1_6_to_FileWriter_1 (0 0 0 8 8)
    initReceiver(&fir_filter_half_1_6_to_FileWriter_1_receiver, 0, 6, 8, sizeof(int32_t), 0, 3, 0, DATA_MEM);
    // InitRead fir_filter_half_2_6_to_FileWriter_1 (0 0 0 8 8)
    initReceiver(&fir_filter_half_2_6_to_FileWriter_1_receiver, 0, 6, 8, sizeof(int32_t), 0, 3, 0, DATA_MEM);
    // InitRead fir_filter_half_1_7_to_FileWriter_1 (0 0 0 8 8)
    initReceiver(&fir_filter_half_1_7_to_FileWriter_1_receiver, 0, 7, 8, sizeof(int32_t), 0, 3, 0, DATA_MEM);
    // InitRead fir_filter_half_2_7_to_FileWriter_1 (0 0 0 8 8)
    initReceiver(&fir_filter_half_2_7_to_FileWriter_1_receiver, 0, 7, 8, sizeof(int32_t), 0, 3, 0, DATA_MEM);
    #ifdef COREVA
    FileWriter_1_filename = "output.stream_cva";
    #else
    FileWriter_1_filename = "output.stream_pth";
    #endif
    volatile int32_t* FileWriter_1_to_FileOutput_send_buffer = (int32_t*)fioAllocateSharedMem(4, 128 * sizeof(int32_t));
    initFioWriteMultiChannel(&FileWriter_1_to_FileOutput_sender, 1, INTEGER, FileWriter_1_filename, (void*)FileWriter_1_to_FileOutput_send_buffer, 128 * sizeof(int32_t), 1);
}

static void InitFilters_cpu_0_0_2()
{
    fir_filter_half_1_2_init();
    fir_filter_half_2_4_init();
    FileWriter_1_init();
}

static void PrimePump_cpu_0_0_2()
{
    for (int iteration=0; iteration<6; iteration++)
    {
        if (iteration >= 4)
        {
            fir_filter_half_1_2_steady();
            fir_filter_half_2_4_steady();
        }
        if (iteration >= 5)
        {
            FileWriter_1_steady();
        }
    }
}

int cpu_0_0_2()
{
    InitStatics_cpu_0_0_2();
    InitChannels_cpu_0_0_2();
    InitFilters_cpu_0_0_2();
    PrimePump_cpu_0_0_2();
    for(int _i=0;_i<STEADY_ITERATIONS;_i++) // Steady State Loop
    {
        COREVA_HWACC_MISC_TRACE(10);
        fir_filter_half_1_2_steady();
        COREVA_HWACC_MISC_TRACE(11);
        fir_filter_half_2_4_steady();
        COREVA_HWACC_MISC_TRACE(12);
        FileWriter_1_steady();
    }
    deleteMultiChannel(&FileWriter_1_to_FileOutput_sender);
    	return 0;
}

