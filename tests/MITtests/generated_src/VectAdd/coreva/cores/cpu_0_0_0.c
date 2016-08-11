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


void InitStatics_cpu_0_0_0()
{
}

MULTI_CHANNEL_SYNC VectSource_1_to_VectAddKernel_1_6_receiver;
MULTI_CHANNEL_SYNC VectAddKernel_1_6_to_VectPrinter_1_sender;

// WORK
static void VectAddKernel(int32_t** restrict _receive_buffer, int32_t** restrict _send_buffer)
{
    int32_t t1;
    int32_t t2;

    (t1)=0;
    (t2)=0;
    (t1)=(*(*_receive_buffer)++);
    (t2)=(*(*_receive_buffer)++);
    *(*_send_buffer)++ = ((t1)+(t2));
}

// PREWORK + INIT
static void VectAddKernel_1_6_init()
{
}

static void VectAddKernel_1_6_steady()
{
    COREVA_HWACC_MISC_TRACE(2);
    int32_t* VectSource_1_to_VectAddKernel_1_6_receive_buffer = (int32_t*)steadyClusterWaitRead(&VectSource_1_to_VectAddKernel_1_6_receiver, 2, sizeof(int32_t), 0, 0, 0, 1);
    COREVA_HWACC_MISC_TRACE(3);
    int32_t* VectAddKernel_1_6_to_VectPrinter_1_send_buffer = (int32_t*)steadyClusterWaitWrite(&VectAddKernel_1_6_to_VectPrinter_1_sender);
    COREVA_HWACC_MISC_TRACE(4);
    VectAddKernel(&VectSource_1_to_VectAddKernel_1_6_receive_buffer, &VectAddKernel_1_6_to_VectPrinter_1_send_buffer);
    COREVA_HWACC_MISC_TRACE(5);
    steadyClusterDoneRead(&VectSource_1_to_VectAddKernel_1_6_receiver);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from VectSource_1
    steadyClusterDoneWrite(&VectAddKernel_1_6_to_VectPrinter_1_sender);
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to VectPrinter_1
}

static void InitChannels_cpu_0_0_0()
{
    // InitWrite VectAddKernel_1_6_to_VectPrinter_1 (0 0 0 1 1)
    initSender(&VectAddKernel_1_6_to_VectPrinter_1_sender, 0, 1, 1, sizeof(int32_t), 3, DATA_MEM);
    // InitRead VectSource_1_to_VectAddKernel_1_6 (0 0 0 2 2)
    initReceiver(&VectSource_1_to_VectAddKernel_1_6_receiver, 0, 3, 2, sizeof(int32_t), 0, 3, 0, DATA_MEM);
}

static void InitFilters_cpu_0_0_0()
{
    VectAddKernel_1_6_init();
}

static void PrimePump_cpu_0_0_0()
{
    for (int iteration=0; iteration<3; iteration++)
    {
        if (iteration >= 1)
        {
            VectAddKernel_1_6_steady();
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
        VectAddKernel_1_6_steady();
    }
    	return 0;
}

