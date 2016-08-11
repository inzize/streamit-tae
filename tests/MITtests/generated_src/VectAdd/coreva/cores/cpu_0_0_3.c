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

int32_t VectSource_1_N;
int32_t* VectSource_1_Z;
int32_t VectSource_1_idx;

void InitStatics_cpu_0_0_3()
{
}

MULTI_CHANNEL_SYNC VectSource_1_to_VectAddKernel_1_1_sender;
MULTI_CHANNEL_SYNC VectSource_1_to_VectAddKernel_1_2_sender;
MULTI_CHANNEL_SYNC VectSource_1_to_VectAddKernel_1_3_sender;
MULTI_CHANNEL_SYNC VectSource_1_to_VectAddKernel_1_4_sender;
MULTI_CHANNEL_SYNC VectSource_1_to_VectAddKernel_1_5_sender;
MULTI_CHANNEL_SYNC VectSource_1_to_VectAddKernel_1_6_sender;
int32_t split_VectSource_1_buffer[12];

// WORK
static void VectSource(int32_t N, int32_t* restrict Z, int32_t *_idx, int32_t** restrict split__buffer_ptr_head)
{
    *(*split__buffer_ptr_head)++ = ((Z)[(*_idx)]);
    (*_idx)++;
    if ((*_idx)>=(N))
        (*_idx)=0;
}

// PREWORK + INIT
static void VectSource_1_init()
{
    (VectSource_1_Z)=(int*)malloc(sizeof(int)*20);

    VectSource_1_N = 20;
    VectSource_1_Z[0] = 0; VectSource_1_Z[1] = 1; VectSource_1_Z[2] = 2; VectSource_1_Z[3] = 3; VectSource_1_Z[4] = 4; VectSource_1_Z[5] = 5; VectSource_1_Z[6] = 6; VectSource_1_Z[7] = 7; VectSource_1_Z[8] = 8; VectSource_1_Z[9] = 9; VectSource_1_Z[10] = 10; VectSource_1_Z[11] = 11; VectSource_1_Z[12] = 12; VectSource_1_Z[13] = 13; VectSource_1_Z[14] = 14; VectSource_1_Z[15] = 15; VectSource_1_Z[16] = 16; VectSource_1_Z[17] = 17; VectSource_1_Z[18] = 18; VectSource_1_Z[19] = 19; 
    (VectSource_1_idx)=0;
    (VectSource_1_idx)=0;
    int32_t* split_VectSource_1_buffer_ptr_tail=split_VectSource_1_buffer;
}

static void VectSource_1_steady()
{
    COREVA_HWACC_MISC_TRACE(8);
    COREVA_HWACC_MISC_TRACE(2);
    int32_t* VectSource_1_to_VectAddKernel_1_1_send_buffer = (int32_t*)steadyClusterWaitWrite(&VectSource_1_to_VectAddKernel_1_1_sender);
    COREVA_HWACC_MISC_TRACE(4);
    int32_t* VectSource_1_to_VectAddKernel_1_2_send_buffer = (int32_t*)steadyClusterWaitWrite(&VectSource_1_to_VectAddKernel_1_2_sender);
    COREVA_HWACC_MISC_TRACE(4);
    int32_t* VectSource_1_to_VectAddKernel_1_3_send_buffer = (int32_t*)steadyClusterWaitWrite(&VectSource_1_to_VectAddKernel_1_3_sender);
    COREVA_HWACC_MISC_TRACE(4);
    int32_t* VectSource_1_to_VectAddKernel_1_4_send_buffer = (int32_t*)steadyClusterWaitWrite(&VectSource_1_to_VectAddKernel_1_4_sender);
    COREVA_HWACC_MISC_TRACE(4);
    int32_t* VectSource_1_to_VectAddKernel_1_5_send_buffer = (int32_t*)steadyClusterWaitWrite(&VectSource_1_to_VectAddKernel_1_5_sender);
    COREVA_HWACC_MISC_TRACE(4);
    int32_t* VectSource_1_to_VectAddKernel_1_6_send_buffer = (int32_t*)steadyClusterWaitWrite(&VectSource_1_to_VectAddKernel_1_6_sender);
    COREVA_HWACC_MISC_TRACE(4);
    int32_t* split_VectSource_1_buffer_ptr_head=split_VectSource_1_buffer;
    COREVA_HWACC_MISC_TRACE(4); // SteadyWaitWrite MergeBuffer split
    for (int _i=0; _i<12; _i++)
        VectSource(20, VectSource_1_Z, &VectSource_1_idx, &split_VectSource_1_buffer_ptr_head);
    COREVA_HWACC_MISC_TRACE(5);
    // SimpleMergeSplitBuffer -> Split
    int32_t* split_VectSource_1_buffer_ptr_tail=split_VectSource_1_buffer;
    for (unsigned int _i=0;_i<1;_i++)
    {
        int32_t _tmp;
        *VectSource_1_to_VectAddKernel_1_1_send_buffer++ = ((*split_VectSource_1_buffer_ptr_tail++));
        *VectSource_1_to_VectAddKernel_1_1_send_buffer++ = ((*split_VectSource_1_buffer_ptr_tail++));
        *VectSource_1_to_VectAddKernel_1_2_send_buffer++ = ((*split_VectSource_1_buffer_ptr_tail++));
        *VectSource_1_to_VectAddKernel_1_2_send_buffer++ = ((*split_VectSource_1_buffer_ptr_tail++));
        *VectSource_1_to_VectAddKernel_1_3_send_buffer++ = ((*split_VectSource_1_buffer_ptr_tail++));
        *VectSource_1_to_VectAddKernel_1_3_send_buffer++ = ((*split_VectSource_1_buffer_ptr_tail++));
        *VectSource_1_to_VectAddKernel_1_4_send_buffer++ = ((*split_VectSource_1_buffer_ptr_tail++));
        *VectSource_1_to_VectAddKernel_1_4_send_buffer++ = ((*split_VectSource_1_buffer_ptr_tail++));
        *VectSource_1_to_VectAddKernel_1_5_send_buffer++ = ((*split_VectSource_1_buffer_ptr_tail++));
        *VectSource_1_to_VectAddKernel_1_5_send_buffer++ = ((*split_VectSource_1_buffer_ptr_tail++));
        *VectSource_1_to_VectAddKernel_1_6_send_buffer++ = ((*split_VectSource_1_buffer_ptr_tail++));
        *VectSource_1_to_VectAddKernel_1_6_send_buffer++ = ((*split_VectSource_1_buffer_ptr_tail++));
    }
    COREVA_HWACC_MISC_TRACE(7);  // SimpleMergeSplitBuffer split
    steadyClusterDoneWrite(&VectSource_1_to_VectAddKernel_1_1_sender);
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to VectAddKernel_1_1
    steadyClusterDoneWrite(&VectSource_1_to_VectAddKernel_1_2_sender);
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to VectAddKernel_1_2
    steadyClusterDoneWrite(&VectSource_1_to_VectAddKernel_1_3_sender);
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to VectAddKernel_1_3
    steadyClusterDoneWrite(&VectSource_1_to_VectAddKernel_1_4_sender);
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to VectAddKernel_1_4
    steadyClusterDoneWrite(&VectSource_1_to_VectAddKernel_1_5_sender);
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to VectAddKernel_1_5
    steadyClusterDoneWrite(&VectSource_1_to_VectAddKernel_1_6_sender);
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to VectAddKernel_1_6
}

static void InitChannels_cpu_0_0_3()
{
    // InitWrite VectSource_1_to_VectAddKernel_1_4 (0 0 0 2 2)
    initSender(&VectSource_1_to_VectAddKernel_1_4_sender, 0, 7, 2, sizeof(int32_t), 3, DATA_MEM);
    // InitWrite VectSource_1_to_VectAddKernel_1_1 (0 0 0 2 2)
    initSender(&VectSource_1_to_VectAddKernel_1_1_sender, 0, 6, 2, sizeof(int32_t), 3, DATA_MEM);
    // InitWrite VectSource_1_to_VectAddKernel_1_5 (0 0 0 2 2)
    initSender(&VectSource_1_to_VectAddKernel_1_5_sender, 0, 5, 2, sizeof(int32_t), 3, DATA_MEM);
    // InitWrite VectSource_1_to_VectAddKernel_1_2 (0 0 0 2 2)
    initSender(&VectSource_1_to_VectAddKernel_1_2_sender, 0, 4, 2, sizeof(int32_t), 3, DATA_MEM);
    // InitWrite VectSource_1_to_VectAddKernel_1_3 (0 0 0 2 2)
    initSender(&VectSource_1_to_VectAddKernel_1_3_sender, 0, 2, 2, sizeof(int32_t), 3, DATA_MEM);
    // InitWrite VectSource_1_to_VectAddKernel_1_6 (0 0 0 2 2)
    initSender(&VectSource_1_to_VectAddKernel_1_6_sender, 0, 0, 2, sizeof(int32_t), 3, DATA_MEM);
}

static void InitFilters_cpu_0_0_3()
{
    VectSource_1_init();
}

static void PrimePump_cpu_0_0_3()
{
    for (int iteration=0; iteration<3; iteration++)
    {
        if (iteration >= 0)
        {
            VectSource_1_steady();
        }
    }
}

int cpu_0_0_3()
{
    InitStatics_cpu_0_0_3();
    InitChannels_cpu_0_0_3();
    InitFilters_cpu_0_0_3();
    PrimePump_cpu_0_0_3();
    for(int _i=0;_i<STEADY_ITERATIONS;_i++) // Steady State Loop
    {
        COREVA_HWACC_MISC_TRACE(10);
        VectSource_1_steady();
    }
    	return 0;
}

