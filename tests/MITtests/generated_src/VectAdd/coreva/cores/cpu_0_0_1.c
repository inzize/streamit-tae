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


void InitStatics_cpu_0_0_1()
{
}

MULTI_CHANNEL_SYNC VectAddKernel_1_1_to_VectPrinter_1_receiver;
MULTI_CHANNEL_SYNC VectAddKernel_1_2_to_VectPrinter_1_receiver;
MULTI_CHANNEL_SYNC VectAddKernel_1_3_to_VectPrinter_1_receiver;
MULTI_CHANNEL_SYNC VectAddKernel_1_4_to_VectPrinter_1_receiver;
MULTI_CHANNEL_SYNC VectAddKernel_1_5_to_VectPrinter_1_receiver;
MULTI_CHANNEL_SYNC VectAddKernel_1_6_to_VectPrinter_1_receiver;
int32_t merge_VectPrinter_1_buffer[6];

// WORK
static void VectPrinter(int32_t** restrict merge__buffer_ptr_tail)
{
    printf("%d\n", (*(*merge__buffer_ptr_tail)++));
}

// PREWORK + INIT
static void VectPrinter_1_init()
{
}

static void VectPrinter_1_steady()
{
    COREVA_HWACC_MISC_TRACE(2);
    int32_t* VectAddKernel_1_1_to_VectPrinter_1_receive_buffer = (int32_t*)steadyClusterWaitRead(&VectAddKernel_1_1_to_VectPrinter_1_receiver, 1, sizeof(int32_t), 0, 0, 0, 1);
    COREVA_HWACC_MISC_TRACE(3);
    int32_t* VectAddKernel_1_2_to_VectPrinter_1_receive_buffer = (int32_t*)steadyClusterWaitRead(&VectAddKernel_1_2_to_VectPrinter_1_receiver, 1, sizeof(int32_t), 0, 0, 0, 1);
    COREVA_HWACC_MISC_TRACE(3);
    int32_t* VectAddKernel_1_3_to_VectPrinter_1_receive_buffer = (int32_t*)steadyClusterWaitRead(&VectAddKernel_1_3_to_VectPrinter_1_receiver, 1, sizeof(int32_t), 0, 0, 0, 1);
    COREVA_HWACC_MISC_TRACE(3);
    int32_t* VectAddKernel_1_4_to_VectPrinter_1_receive_buffer = (int32_t*)steadyClusterWaitRead(&VectAddKernel_1_4_to_VectPrinter_1_receiver, 1, sizeof(int32_t), 0, 0, 0, 1);
    COREVA_HWACC_MISC_TRACE(3);
    int32_t* VectAddKernel_1_5_to_VectPrinter_1_receive_buffer = (int32_t*)steadyClusterWaitRead(&VectAddKernel_1_5_to_VectPrinter_1_receiver, 1, sizeof(int32_t), 0, 0, 0, 1);
    COREVA_HWACC_MISC_TRACE(3);
    int32_t* VectAddKernel_1_6_to_VectPrinter_1_receive_buffer = (int32_t*)steadyClusterWaitRead(&VectAddKernel_1_6_to_VectPrinter_1_receiver, 1, sizeof(int32_t), 0, 0, 0, 1);
    COREVA_HWACC_MISC_TRACE(3);
    int32_t* merge_VectPrinter_1_buffer_ptr_head=merge_VectPrinter_1_buffer;
    // SimpleMergeSplitBuffer -> Merge
    for (unsigned int _i=0;_i<1;_i++)
    {
        *merge_VectPrinter_1_buffer_ptr_head++ = ((*VectAddKernel_1_1_to_VectPrinter_1_receive_buffer++));
        *merge_VectPrinter_1_buffer_ptr_head++ = ((*VectAddKernel_1_2_to_VectPrinter_1_receive_buffer++));
        *merge_VectPrinter_1_buffer_ptr_head++ = ((*VectAddKernel_1_3_to_VectPrinter_1_receive_buffer++));
        *merge_VectPrinter_1_buffer_ptr_head++ = ((*VectAddKernel_1_4_to_VectPrinter_1_receive_buffer++));
        *merge_VectPrinter_1_buffer_ptr_head++ = ((*VectAddKernel_1_5_to_VectPrinter_1_receive_buffer++));
        *merge_VectPrinter_1_buffer_ptr_head++ = ((*VectAddKernel_1_6_to_VectPrinter_1_receive_buffer++));
    }
    int32_t* merge_VectPrinter_1_buffer_ptr_tail=merge_VectPrinter_1_buffer;
    COREVA_HWACC_MISC_TRACE(3);
    for (int _i=0; _i<6; _i++)
        VectPrinter(&merge_VectPrinter_1_buffer_ptr_tail);
    COREVA_HWACC_MISC_TRACE(5);
    steadyClusterDoneRead(&VectAddKernel_1_1_to_VectPrinter_1_receiver);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from VectAddKernel_1_1
    steadyClusterDoneRead(&VectAddKernel_1_2_to_VectPrinter_1_receiver);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from VectAddKernel_1_2
    steadyClusterDoneRead(&VectAddKernel_1_3_to_VectPrinter_1_receiver);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from VectAddKernel_1_3
    steadyClusterDoneRead(&VectAddKernel_1_4_to_VectPrinter_1_receiver);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from VectAddKernel_1_4
    steadyClusterDoneRead(&VectAddKernel_1_5_to_VectPrinter_1_receiver);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from VectAddKernel_1_5
    steadyClusterDoneRead(&VectAddKernel_1_6_to_VectPrinter_1_receiver);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from VectAddKernel_1_6
}

static void InitChannels_cpu_0_0_1()
{
    // InitRead VectAddKernel_1_6_to_VectPrinter_1 (0 0 0 1 1)
    initReceiver(&VectAddKernel_1_6_to_VectPrinter_1_receiver, 0, 0, 1, sizeof(int32_t), 0, 3, 0, DATA_MEM);
    // InitRead VectAddKernel_1_3_to_VectPrinter_1 (0 0 0 1 1)
    initReceiver(&VectAddKernel_1_3_to_VectPrinter_1_receiver, 0, 2, 1, sizeof(int32_t), 0, 3, 0, DATA_MEM);
    // InitRead VectAddKernel_1_2_to_VectPrinter_1 (0 0 0 1 1)
    initReceiver(&VectAddKernel_1_2_to_VectPrinter_1_receiver, 0, 4, 1, sizeof(int32_t), 0, 3, 0, DATA_MEM);
    // InitRead VectAddKernel_1_5_to_VectPrinter_1 (0 0 0 1 1)
    initReceiver(&VectAddKernel_1_5_to_VectPrinter_1_receiver, 0, 5, 1, sizeof(int32_t), 0, 3, 0, DATA_MEM);
    // InitRead VectAddKernel_1_1_to_VectPrinter_1 (0 0 0 1 1)
    initReceiver(&VectAddKernel_1_1_to_VectPrinter_1_receiver, 0, 6, 1, sizeof(int32_t), 0, 3, 0, DATA_MEM);
    // InitRead VectAddKernel_1_4_to_VectPrinter_1 (0 0 0 1 1)
    initReceiver(&VectAddKernel_1_4_to_VectPrinter_1_receiver, 0, 7, 1, sizeof(int32_t), 0, 3, 0, DATA_MEM);
}

static void InitFilters_cpu_0_0_1()
{
    VectPrinter_1_init();
}

static void PrimePump_cpu_0_0_1()
{
    for (int iteration=0; iteration<3; iteration++)
    {
        if (iteration >= 2)
        {
            VectPrinter_1_steady();
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
        VectPrinter_1_steady();
    }
    	return 0;
}

