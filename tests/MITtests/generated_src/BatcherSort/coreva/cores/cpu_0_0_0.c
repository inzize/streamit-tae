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

int32_t BatcherPipe_1_6_SIZE;
int32_t BatcherPipe_1_6_START;

void InitStatics_cpu_0_0_0()
{
}

MULTI_CHANNEL_SYNC IntSource_1_to_BatcherPipe_1_6_receiver;
MULTI_CHANNEL_SYNC BatcherPipe_1_6_to_IntPrinter_1_sender;

// WORK
static void BatcherPipe(int32_t START, int32_t SIZE, int32_t** restrict _receive_buffer, int32_t** restrict _send_buffer)
{
    int32_t* ordering = {0};
    int32_t i_02;
    int32_t test1;
    int32_t p;
    int32_t i_04;

    (ordering)=(int32_t*)malloc(sizeof(int32_t)*(SIZE));
    for ((i_02)=0; (i_02)<(SIZE); (i_02)++)
        {
        (ordering)[(i_02)]=(*(*_receive_buffer)++);
    }
    (test1)=0;
    (p)=0;
    for ((p)=(START); ((p)>0); (p)=(p)/2)
        {
        int32_t d;
        int32_t r;
        int32_t q;

        (d)=(p);
        (r)=0;
        (q)=(START);
        while(1==1)
        {
            int32_t i_03;

            for ((i_03)=0; (i_03)<(SIZE)-(d); (i_03)++)
                {
                if (((i_03)&(p))==(r))
                    {
                    if ((ordering)[(i_03)]>(ordering)[(i_03)+(d)])
                        {
                        int32_t temp;

                        (temp)=(ordering)[(i_03)];
                        (ordering)[(i_03)]=(ordering)[(i_03)+(d)];
                        (ordering)[(i_03)+(d)]=(temp);
                    }
                }
            }
            if ((p)==(q))
                break;(d)=(q)-(p);
            (q)=(q)/2;
            (r)=(p);
        }
    }
    for ((i_02)=0; (i_02)<(SIZE); (i_02)++)
        {
        *(*_send_buffer)++ = ((ordering)[(i_02)]);
    }
    free(ordering);
}

// PREWORK + INIT
static void BatcherPipe_1_6_init()
{
    BatcherPipe_1_6_SIZE = 16;
    BatcherPipe_1_6_START = 8;
}

static void BatcherPipe_1_6_steady()
{
    COREVA_HWACC_MISC_TRACE(2);
    int32_t* IntSource_1_to_BatcherPipe_1_6_receive_buffer = (int32_t*)steadyClusterWaitRead(&IntSource_1_to_BatcherPipe_1_6_receiver, 16, sizeof(int32_t), 0, 0, 0, 1);
    COREVA_HWACC_MISC_TRACE(3);
    int32_t* BatcherPipe_1_6_to_IntPrinter_1_send_buffer = (int32_t*)steadyClusterWaitWrite(&BatcherPipe_1_6_to_IntPrinter_1_sender);
    COREVA_HWACC_MISC_TRACE(4);
    BatcherPipe(8, 16, &IntSource_1_to_BatcherPipe_1_6_receive_buffer, &BatcherPipe_1_6_to_IntPrinter_1_send_buffer);
    COREVA_HWACC_MISC_TRACE(5);
    steadyClusterDoneRead(&IntSource_1_to_BatcherPipe_1_6_receiver);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from IntSource_1
    steadyClusterDoneWrite(&BatcherPipe_1_6_to_IntPrinter_1_sender);
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to IntPrinter_1
}

static void InitChannels_cpu_0_0_0()
{
    // InitWrite BatcherPipe_1_6_to_IntPrinter_1 (0 0 0 16 16)
    initSender(&BatcherPipe_1_6_to_IntPrinter_1_sender, 0, 7, 16, sizeof(int32_t), 3, DATA_MEM);
    // InitRead IntSource_1_to_BatcherPipe_1_6 (0 0 0 16 16)
    initReceiver(&IntSource_1_to_BatcherPipe_1_6_receiver, 0, 5, 16, sizeof(int32_t), 0, 3, 0, DATA_MEM);
}

static void InitFilters_cpu_0_0_0()
{
    BatcherPipe_1_6_init();
}

static void PrimePump_cpu_0_0_0()
{
    for (int iteration=0; iteration<3; iteration++)
    {
        if (iteration >= 1)
        {
            BatcherPipe_1_6_steady();
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
        BatcherPipe_1_6_steady();
    }
    	return 0;
}

