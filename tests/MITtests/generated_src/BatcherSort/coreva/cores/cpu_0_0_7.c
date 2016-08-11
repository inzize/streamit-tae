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

int32_t BatcherPipe_1_3_SIZE;
int32_t BatcherPipe_1_3_START;

void InitStatics_cpu_0_0_7()
{
}

MULTI_CHANNEL_SYNC IntSource_1_to_BatcherPipe_1_3_receiver;
int32_t* BatcherPipe_1_3_to_IntPrinter_1_buffer_ptr_tail = 0;
MULTI_CHANNEL_SYNC BatcherPipe_1_1_to_IntPrinter_1_receiver;
MULTI_CHANNEL_SYNC BatcherPipe_1_2_to_IntPrinter_1_receiver;
int32_t* BatcherPipe_1_3_to_IntPrinter_1_buffer_ptr_head = 0;
int32_t BatcherPipe_1_3_to_IntPrinter_1_buffer[32+0];
MULTI_CHANNEL_SYNC BatcherPipe_1_4_to_IntPrinter_1_receiver;
MULTI_CHANNEL_SYNC BatcherPipe_1_5_to_IntPrinter_1_receiver;
MULTI_CHANNEL_SYNC BatcherPipe_1_6_to_IntPrinter_1_receiver;
MULTI_CHANNEL_SYNC BatcherPipe_1_7_to_IntPrinter_1_receiver;
MULTI_CHANNEL_SYNC BatcherPipe_1_8_to_IntPrinter_1_receiver;
int32_t merge_IntPrinter_1_buffer[128];

// WORK
static void BatcherPipe(int32_t START, int32_t SIZE, int32_t** restrict _receive_buffer, int32_t** restrict _buffer_ptr_head)
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
        *(*_buffer_ptr_head)++ = ((ordering)[(i_02)]);
    }
    free(ordering);
}

static void IntPrinter(int32_t** restrict merge__buffer_ptr_tail)
{
    printf("%d\n", (*(*merge__buffer_ptr_tail)++));
}

// PREWORK + INIT
static void BatcherPipe_1_3_init()
{
    BatcherPipe_1_3_SIZE = 16;
    BatcherPipe_1_3_START = 8;
}

static void IntPrinter_1_init()
{
}

static void BatcherPipe_1_3_steady()
{
    COREVA_HWACC_MISC_TRACE(2);
    int32_t* IntSource_1_to_BatcherPipe_1_3_receive_buffer = (int32_t*)steadyClusterWaitRead(&IntSource_1_to_BatcherPipe_1_3_receiver, 16, sizeof(int32_t), 0, 0, 0, 1);
    COREVA_HWACC_MISC_TRACE(3);
    if(((BatcherPipe_1_3_to_IntPrinter_1_buffer_ptr_head-BatcherPipe_1_3_to_IntPrinter_1_buffer)+16)>32+0)
    {
        BatcherPipe_1_3_to_IntPrinter_1_buffer_ptr_head=BatcherPipe_1_3_to_IntPrinter_1_buffer+0;
    }
    COREVA_HWACC_MISC_TRACE(4);
    BatcherPipe(8, 16, &IntSource_1_to_BatcherPipe_1_3_receive_buffer, &BatcherPipe_1_3_to_IntPrinter_1_buffer_ptr_head);
    COREVA_HWACC_MISC_TRACE(5);
    steadyClusterDoneRead(&IntSource_1_to_BatcherPipe_1_3_receiver);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from IntSource_1
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to IntPrinter_1
}

static void IntPrinter_1_steady()
{
    COREVA_HWACC_MISC_TRACE(2);
    int32_t* BatcherPipe_1_1_to_IntPrinter_1_receive_buffer = (int32_t*)steadyClusterWaitRead(&BatcherPipe_1_1_to_IntPrinter_1_receiver, 16, sizeof(int32_t), 0, 0, 0, 1);
    COREVA_HWACC_MISC_TRACE(3);
    int32_t* BatcherPipe_1_2_to_IntPrinter_1_receive_buffer = (int32_t*)steadyClusterWaitRead(&BatcherPipe_1_2_to_IntPrinter_1_receiver, 16, sizeof(int32_t), 0, 0, 0, 1);
    COREVA_HWACC_MISC_TRACE(3);
    if(((BatcherPipe_1_3_to_IntPrinter_1_buffer_ptr_tail-BatcherPipe_1_3_to_IntPrinter_1_buffer)+16)>32+0)
        BatcherPipe_1_3_to_IntPrinter_1_buffer_ptr_tail=BatcherPipe_1_3_to_IntPrinter_1_buffer+0;
    COREVA_HWACC_MISC_TRACE(3);
    int32_t* BatcherPipe_1_4_to_IntPrinter_1_receive_buffer = (int32_t*)steadyClusterWaitRead(&BatcherPipe_1_4_to_IntPrinter_1_receiver, 16, sizeof(int32_t), 0, 0, 0, 1);
    COREVA_HWACC_MISC_TRACE(3);
    int32_t* BatcherPipe_1_5_to_IntPrinter_1_receive_buffer = (int32_t*)steadyClusterWaitRead(&BatcherPipe_1_5_to_IntPrinter_1_receiver, 16, sizeof(int32_t), 0, 0, 0, 1);
    COREVA_HWACC_MISC_TRACE(3);
    int32_t* BatcherPipe_1_6_to_IntPrinter_1_receive_buffer = (int32_t*)steadyClusterWaitRead(&BatcherPipe_1_6_to_IntPrinter_1_receiver, 16, sizeof(int32_t), 0, 0, 0, 1);
    COREVA_HWACC_MISC_TRACE(3);
    int32_t* BatcherPipe_1_7_to_IntPrinter_1_receive_buffer = (int32_t*)steadyClusterWaitRead(&BatcherPipe_1_7_to_IntPrinter_1_receiver, 16, sizeof(int32_t), 0, 0, 0, 1);
    COREVA_HWACC_MISC_TRACE(3);
    int32_t* BatcherPipe_1_8_to_IntPrinter_1_receive_buffer = (int32_t*)steadyClusterWaitRead(&BatcherPipe_1_8_to_IntPrinter_1_receiver, 16, sizeof(int32_t), 0, 0, 0, 1);
    COREVA_HWACC_MISC_TRACE(3);
    int32_t* merge_IntPrinter_1_buffer_ptr_head=merge_IntPrinter_1_buffer;
    // SimpleMergeSplitBuffer -> Merge
    for (unsigned int _i=0;_i<1;_i++)
    {
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_1_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_1_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_1_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_1_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_1_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_1_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_1_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_1_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_1_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_1_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_1_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_1_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_1_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_1_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_1_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_1_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_2_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_2_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_2_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_2_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_2_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_2_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_2_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_2_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_2_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_2_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_2_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_2_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_2_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_2_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_2_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_2_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_3_to_IntPrinter_1_buffer_ptr_tail++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_3_to_IntPrinter_1_buffer_ptr_tail++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_3_to_IntPrinter_1_buffer_ptr_tail++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_3_to_IntPrinter_1_buffer_ptr_tail++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_3_to_IntPrinter_1_buffer_ptr_tail++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_3_to_IntPrinter_1_buffer_ptr_tail++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_3_to_IntPrinter_1_buffer_ptr_tail++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_3_to_IntPrinter_1_buffer_ptr_tail++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_3_to_IntPrinter_1_buffer_ptr_tail++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_3_to_IntPrinter_1_buffer_ptr_tail++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_3_to_IntPrinter_1_buffer_ptr_tail++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_3_to_IntPrinter_1_buffer_ptr_tail++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_3_to_IntPrinter_1_buffer_ptr_tail++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_3_to_IntPrinter_1_buffer_ptr_tail++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_3_to_IntPrinter_1_buffer_ptr_tail++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_3_to_IntPrinter_1_buffer_ptr_tail++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_4_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_4_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_4_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_4_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_4_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_4_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_4_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_4_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_4_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_4_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_4_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_4_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_4_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_4_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_4_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_4_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_5_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_5_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_5_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_5_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_5_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_5_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_5_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_5_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_5_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_5_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_5_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_5_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_5_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_5_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_5_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_5_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_6_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_6_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_6_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_6_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_6_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_6_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_6_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_6_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_6_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_6_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_6_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_6_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_6_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_6_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_6_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_6_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_7_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_7_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_7_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_7_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_7_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_7_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_7_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_7_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_7_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_7_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_7_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_7_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_7_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_7_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_7_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_7_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_8_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_8_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_8_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_8_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_8_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_8_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_8_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_8_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_8_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_8_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_8_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_8_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_8_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_8_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_8_to_IntPrinter_1_receive_buffer++));
        *merge_IntPrinter_1_buffer_ptr_head++ = ((*BatcherPipe_1_8_to_IntPrinter_1_receive_buffer++));
    }
    int32_t* merge_IntPrinter_1_buffer_ptr_tail=merge_IntPrinter_1_buffer;
    COREVA_HWACC_MISC_TRACE(3);
    for (int _i=0; _i<128; _i++)
        IntPrinter(&merge_IntPrinter_1_buffer_ptr_tail);
    COREVA_HWACC_MISC_TRACE(5);
    steadyClusterDoneRead(&BatcherPipe_1_1_to_IntPrinter_1_receiver);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from BatcherPipe_1_1
    steadyClusterDoneRead(&BatcherPipe_1_2_to_IntPrinter_1_receiver);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from BatcherPipe_1_2
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from BatcherPipe_1_3
    steadyClusterDoneRead(&BatcherPipe_1_4_to_IntPrinter_1_receiver);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from BatcherPipe_1_4
    steadyClusterDoneRead(&BatcherPipe_1_5_to_IntPrinter_1_receiver);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from BatcherPipe_1_5
    steadyClusterDoneRead(&BatcherPipe_1_6_to_IntPrinter_1_receiver);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from BatcherPipe_1_6
    steadyClusterDoneRead(&BatcherPipe_1_7_to_IntPrinter_1_receiver);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from BatcherPipe_1_7
    steadyClusterDoneRead(&BatcherPipe_1_8_to_IntPrinter_1_receiver);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from BatcherPipe_1_8
}

static void InitChannels_cpu_0_0_7()
{
    // InitRead BatcherPipe_1_6_to_IntPrinter_1 (0 0 0 16 16)
    initReceiver(&BatcherPipe_1_6_to_IntPrinter_1_receiver, 0, 0, 16, sizeof(int32_t), 0, 3, 0, DATA_MEM);
    // InitRead BatcherPipe_1_2_to_IntPrinter_1 (0 0 0 16 16)
    initReceiver(&BatcherPipe_1_2_to_IntPrinter_1_receiver, 0, 1, 16, sizeof(int32_t), 0, 3, 0, DATA_MEM);
    // InitRead BatcherPipe_1_4_to_IntPrinter_1 (0 0 0 16 16)
    initReceiver(&BatcherPipe_1_4_to_IntPrinter_1_receiver, 0, 2, 16, sizeof(int32_t), 0, 3, 0, DATA_MEM);
    // InitRead BatcherPipe_1_8_to_IntPrinter_1 (0 0 0 16 16)
    initReceiver(&BatcherPipe_1_8_to_IntPrinter_1_receiver, 0, 3, 16, sizeof(int32_t), 0, 3, 0, DATA_MEM);
    // InitRead BatcherPipe_1_5_to_IntPrinter_1 (0 0 0 16 16)
    initReceiver(&BatcherPipe_1_5_to_IntPrinter_1_receiver, 0, 4, 16, sizeof(int32_t), 0, 3, 0, DATA_MEM);
    // InitRead BatcherPipe_1_1_to_IntPrinter_1 (0 0 0 16 16)
    initReceiver(&BatcherPipe_1_1_to_IntPrinter_1_receiver, 0, 5, 16, sizeof(int32_t), 0, 3, 0, DATA_MEM);
    // InitRead IntSource_1_to_BatcherPipe_1_3 (0 0 0 16 16)
    initReceiver(&IntSource_1_to_BatcherPipe_1_3_receiver, 0, 5, 16, sizeof(int32_t), 0, 3, 0, DATA_MEM);
    // InitRead BatcherPipe_1_7_to_IntPrinter_1 (0 0 0 16 16)
    initReceiver(&BatcherPipe_1_7_to_IntPrinter_1_receiver, 0, 6, 16, sizeof(int32_t), 0, 3, 0, DATA_MEM);
    // InitRead BatcherPipe_1_3_to_IntPrinter_1 (0 0 0 16 16)
    BatcherPipe_1_3_to_IntPrinter_1_buffer_ptr_tail=BatcherPipe_1_3_to_IntPrinter_1_buffer;
    // InitWrite BatcherPipe_1_3_to_IntPrinter_1 (0 0 0 16 16)
    BatcherPipe_1_3_to_IntPrinter_1_buffer_ptr_head=BatcherPipe_1_3_to_IntPrinter_1_buffer;
}

static void InitFilters_cpu_0_0_7()
{
    BatcherPipe_1_3_init();
    IntPrinter_1_init();
}

static void PrimePump_cpu_0_0_7()
{
    for (int iteration=0; iteration<3; iteration++)
    {
        if (iteration >= 1)
        {
            BatcherPipe_1_3_steady();
        }
        if (iteration >= 2)
        {
            IntPrinter_1_steady();
        }
    }
}

int cpu_0_0_7()
{
    InitStatics_cpu_0_0_7();
    InitChannels_cpu_0_0_7();
    InitFilters_cpu_0_0_7();
    PrimePump_cpu_0_0_7();
    for(int _i=0;_i<STEADY_ITERATIONS;_i++) // Steady State Loop
    {
        COREVA_HWACC_MISC_TRACE(10);
        BatcherPipe_1_3_steady();
        COREVA_HWACC_MISC_TRACE(11);
        IntPrinter_1_steady();
    }
    	return 0;
}

