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

int32_t* fir_filter_half_2_2_rcc_taps = {0};
int32_t* fir_filter_half_2_3_rcc_taps = {0};

void InitStatics_cpu_0_0_1()
{
}

MULTI_CHANNEL_SYNC chunk2symbol_1_1_to_fir_filter_half_2_2_receiver;
MULTI_CHANNEL_SYNC chunk2symbol_1_2_to_fir_filter_half_2_2_receiver;
MULTI_CHANNEL_SYNC fir_filter_half_2_2_to_FileWriter_1_sender;
int32_t merge_fir_filter_half_2_2_buffer[1];
MULTI_CHANNEL_SYNC chunk2symbol_1_2_to_fir_filter_half_2_3_receiver;
MULTI_CHANNEL_SYNC fir_filter_half_2_3_to_FileWriter_1_sender;

// WORK
static void fir_filter_half(int32_t* restrict rcc_taps, int32_t** restrict merge__buffer_ptr_tail, int32_t** restrict _send_buffer)
{
    int32_t previous;
    int32_t current;
    int32_t j;

    (previous)=(*(*merge__buffer_ptr_tail+0));
    (current)=(*(*merge__buffer_ptr_tail+1));
    for ((j)=0; (j)<8; (j)++)
        {
        *(*_send_buffer)++ = (((rcc_taps)[(j)]*((current)>>10)+(rcc_taps)[(j)+8]*((previous)>>10))>>10);
    }
    (*(*merge__buffer_ptr_tail)++);
}

// PREWORK + INIT
static void fir_filter_half_2_2_init()
{
    (fir_filter_half_2_2_rcc_taps)=(int32_t*)malloc(sizeof(int32_t)*16);
    (fir_filter_half_2_2_rcc_taps)[0]=0;
    (fir_filter_half_2_2_rcc_taps)[1]=-666;
    (fir_filter_half_2_2_rcc_taps)[2]=69361;
    (fir_filter_half_2_2_rcc_taps)[3]=157964;
    (fir_filter_half_2_2_rcc_taps)[4]=255889;
    (fir_filter_half_2_2_rcc_taps)[5]=351141;
    (fir_filter_half_2_2_rcc_taps)[6]=430952;
    (fir_filter_half_2_2_rcc_taps)[7]=484036;
    (fir_filter_half_2_2_rcc_taps)[8]=502648;
    (fir_filter_half_2_2_rcc_taps)[9]=484036;
    (fir_filter_half_2_2_rcc_taps)[10]=430952;
    (fir_filter_half_2_2_rcc_taps)[11]=351141;
    (fir_filter_half_2_2_rcc_taps)[12]=255889;
    (fir_filter_half_2_2_rcc_taps)[13]=157964;
    (fir_filter_half_2_2_rcc_taps)[14]=69361;
    (fir_filter_half_2_2_rcc_taps)[15]=-666;
}

static void fir_filter_half_2_3_init()
{
    (fir_filter_half_2_3_rcc_taps)=(int32_t*)malloc(sizeof(int32_t)*16);
    (fir_filter_half_2_3_rcc_taps)[0]=0;
    (fir_filter_half_2_3_rcc_taps)[1]=-666;
    (fir_filter_half_2_3_rcc_taps)[2]=69361;
    (fir_filter_half_2_3_rcc_taps)[3]=157964;
    (fir_filter_half_2_3_rcc_taps)[4]=255889;
    (fir_filter_half_2_3_rcc_taps)[5]=351141;
    (fir_filter_half_2_3_rcc_taps)[6]=430952;
    (fir_filter_half_2_3_rcc_taps)[7]=484036;
    (fir_filter_half_2_3_rcc_taps)[8]=502648;
    (fir_filter_half_2_3_rcc_taps)[9]=484036;
    (fir_filter_half_2_3_rcc_taps)[10]=430952;
    (fir_filter_half_2_3_rcc_taps)[11]=351141;
    (fir_filter_half_2_3_rcc_taps)[12]=255889;
    (fir_filter_half_2_3_rcc_taps)[13]=157964;
    (fir_filter_half_2_3_rcc_taps)[14]=69361;
    (fir_filter_half_2_3_rcc_taps)[15]=-666;
}

static void fir_filter_half_2_2_steady()
{
    COREVA_HWACC_MISC_TRACE(2);
    int32_t* chunk2symbol_1_1_to_fir_filter_half_2_2_receive_buffer = (int32_t*)steadyClusterWaitRead(&chunk2symbol_1_1_to_fir_filter_half_2_2_receiver, 1, sizeof(int32_t), 1, 0, 0, 2);
    COREVA_HWACC_MISC_TRACE(3);
    int32_t* chunk2symbol_1_2_to_fir_filter_half_2_2_receive_buffer = (int32_t*)steadyClusterWaitRead(&chunk2symbol_1_2_to_fir_filter_half_2_2_receiver, 1, sizeof(int32_t), 1, 0, 0, 2);
    COREVA_HWACC_MISC_TRACE(3);
    int32_t* merge_fir_filter_half_2_2_buffer_ptr_head=merge_fir_filter_half_2_2_buffer;
    // SimpleMergeSplitBuffer -> Merge
    for (unsigned int _i=0;_i<0;_i++)
    {
        *merge_fir_filter_half_2_2_buffer_ptr_head++ = ((*chunk2symbol_1_1_to_fir_filter_half_2_2_receive_buffer++));
        *merge_fir_filter_half_2_2_buffer_ptr_head++ = ((*chunk2symbol_1_2_to_fir_filter_half_2_2_receive_buffer++));
    }
    int32_t* merge_fir_filter_half_2_2_buffer_ptr_tail=merge_fir_filter_half_2_2_buffer;
    COREVA_HWACC_MISC_TRACE(3);
    int32_t* fir_filter_half_2_2_to_FileWriter_1_send_buffer = (int32_t*)steadyClusterWaitWrite(&fir_filter_half_2_2_to_FileWriter_1_sender);
    COREVA_HWACC_MISC_TRACE(4);
    unsigned int _i=0;
    {
        fir_filter_half(fir_filter_half_2_2_rcc_taps, &merge_fir_filter_half_2_2_buffer_ptr_tail, &fir_filter_half_2_2_to_FileWriter_1_send_buffer);
        for (int _k=0; _k<1; _k++)
        {
            (*merge_fir_filter_half_2_2_buffer_ptr_tail++);
        }
    }
    COREVA_HWACC_MISC_TRACE(5);
    steadyClusterDoneRead(&chunk2symbol_1_1_to_fir_filter_half_2_2_receiver);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from chunk2symbol_1_1
    steadyClusterDoneRead(&chunk2symbol_1_2_to_fir_filter_half_2_2_receiver);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from chunk2symbol_1_2
    steadyClusterDoneWrite(&fir_filter_half_2_2_to_FileWriter_1_sender);
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to FileWriter_1
}

static void fir_filter_half_2_3_steady()
{
    COREVA_HWACC_MISC_TRACE(2);
    int32_t* chunk2symbol_1_2_to_fir_filter_half_2_3_receive_buffer = (int32_t*)steadyClusterWaitRead(&chunk2symbol_1_2_to_fir_filter_half_2_3_receiver, 2, sizeof(int32_t), 1, 0, 0, 2);
    COREVA_HWACC_MISC_TRACE(3);
    int32_t* fir_filter_half_2_3_to_FileWriter_1_send_buffer = (int32_t*)steadyClusterWaitWrite(&fir_filter_half_2_3_to_FileWriter_1_sender);
    COREVA_HWACC_MISC_TRACE(4);
    unsigned int _i=0;
    {
        fir_filter_half(fir_filter_half_2_3_rcc_taps, &chunk2symbol_1_2_to_fir_filter_half_2_3_receive_buffer, &fir_filter_half_2_3_to_FileWriter_1_send_buffer);
        for (int _k=0; _k<1; _k++)
        {
            (*chunk2symbol_1_2_to_fir_filter_half_2_3_receive_buffer++);
        }
    }
    COREVA_HWACC_MISC_TRACE(5);
    steadyClusterDoneRead(&chunk2symbol_1_2_to_fir_filter_half_2_3_receiver);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from chunk2symbol_1_2
    steadyClusterDoneWrite(&fir_filter_half_2_3_to_FileWriter_1_sender);
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to FileWriter_1
}

static void InitChannels_cpu_0_0_1()
{
    // InitRead chunk2symbol_1_1_to_fir_filter_half_2_2 (8 0 0 1 2)
    initReceiver(&chunk2symbol_1_1_to_fir_filter_half_2_2_receiver, 0, 0, 1, sizeof(int32_t), 0, 11, 1, DATA_MEM);
    // InitRead chunk2symbol_1_2_to_fir_filter_half_2_2 (8 0 0 1 2)
    initReceiver(&chunk2symbol_1_2_to_fir_filter_half_2_2_receiver, 0, 0, 1, sizeof(int32_t), 0, 11, 1, DATA_MEM);
    // InitRead chunk2symbol_1_2_to_fir_filter_half_2_3 (16 0 0 2 3)
    initReceiver(&chunk2symbol_1_2_to_fir_filter_half_2_3_receiver, 0, 0, 2, sizeof(int32_t), 0, 11, 1, DATA_MEM);
    // InitWrite fir_filter_half_2_2_to_FileWriter_1 (0 0 0 8 8)
    initSender(&fir_filter_half_2_2_to_FileWriter_1_sender, 0, 2, 8, sizeof(int32_t), 3, DATA_MEM);
    // InitWrite fir_filter_half_2_3_to_FileWriter_1 (0 0 0 8 8)
    initSender(&fir_filter_half_2_3_to_FileWriter_1_sender, 0, 2, 8, sizeof(int32_t), 3, DATA_MEM);
}

static void InitFilters_cpu_0_0_1()
{
    fir_filter_half_2_2_init();
    fir_filter_half_2_3_init();
}

static void PrimePump_cpu_0_0_1()
{
    for (int iteration=0; iteration<6; iteration++)
    {
        if (iteration >= 4)
        {
            fir_filter_half_2_2_steady();
            fir_filter_half_2_3_steady();
        }
    }
}

int cpu_0_0_1()
{
    InitStatics_cpu_0_0_1();
    InitChannels_cpu_0_0_1();
    InitFilters_cpu_0_0_1();
    PrimePump_cpu_0_0_1();
    for(int _i=0;_i<STEADY_ITERATIONS;_i++) // Steady State Loop
    {
        COREVA_HWACC_MISC_TRACE(10);
        fir_filter_half_2_2_steady();
        COREVA_HWACC_MISC_TRACE(11);
        fir_filter_half_2_3_steady();
    }
    	return 0;
}

