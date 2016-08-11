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

int32_t IntSource_1_SIZE;
int32_t* IntSource_1_data = {0};
int32_t IntSource_1_index;
int32_t BatcherPipe_1_1_SIZE;
int32_t BatcherPipe_1_1_START;

void InitStatics_cpu_0_0_5()
{
}

int32_t* IntSource_1_to_BatcherPipe_1_1_buffer_ptr_tail = 0;
MULTI_CHANNEL_SYNC IntSource_1_to_BatcherPipe_1_2_sender;
MULTI_CHANNEL_SYNC IntSource_1_to_BatcherPipe_1_3_sender;
MULTI_CHANNEL_SYNC IntSource_1_to_BatcherPipe_1_4_sender;
MULTI_CHANNEL_SYNC IntSource_1_to_BatcherPipe_1_5_sender;
MULTI_CHANNEL_SYNC IntSource_1_to_BatcherPipe_1_6_sender;
MULTI_CHANNEL_SYNC IntSource_1_to_BatcherPipe_1_7_sender;
MULTI_CHANNEL_SYNC IntSource_1_to_BatcherPipe_1_8_sender;
int32_t split_IntSource_1_buffer[128];
int32_t* IntSource_1_to_BatcherPipe_1_1_buffer_ptr_head = 0;
int32_t IntSource_1_to_BatcherPipe_1_1_buffer[32+0];
MULTI_CHANNEL_SYNC BatcherPipe_1_1_to_IntPrinter_1_sender;

// WORK
static void IntSource(int32_t SIZE, int32_t* restrict data, int32_t *_index, int32_t** restrict split__buffer_ptr_head)
{
    *(*split__buffer_ptr_head)++ = ((data)[(*_index)++]);
    if ((*_index)==(SIZE))
        (*_index)=0;
}

static void BatcherPipe(int32_t START, int32_t SIZE_02, int32_t** restrict _buffer_ptr_tail, int32_t** restrict _send_buffer)
{
    int32_t* ordering = {0};
    int32_t i_02;
    int32_t test1;
    int32_t p;
    int32_t i_04;

    (ordering)=(int32_t*)malloc(sizeof(int32_t)*(SIZE_02));
    for ((i_02)=0; (i_02)<(SIZE_02); (i_02)++)
        {
        (ordering)[(i_02)]=(*(*_buffer_ptr_tail)++);
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

            for ((i_03)=0; (i_03)<(SIZE_02)-(d); (i_03)++)
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
    for ((i_02)=0; (i_02)<(SIZE_02); (i_02)++)
        {
        *(*_send_buffer)++ = ((ordering)[(i_02)]);
    }
    free(ordering);
}

// PREWORK + INIT
static void IntSource_1_init()
{
    IntSource_1_SIZE = 16;
    (IntSource_1_data)=(int32_t*)malloc(sizeof(int32_t)*(IntSource_1_SIZE));
    (IntSource_1_index)=0;
    (IntSource_1_data)[0]=503;
    (IntSource_1_data)[1]=87;
    (IntSource_1_data)[2]=512;
    (IntSource_1_data)[3]=61;
    (IntSource_1_data)[4]=908;
    (IntSource_1_data)[5]=170;
    (IntSource_1_data)[6]=897;
    (IntSource_1_data)[7]=275;
    (IntSource_1_data)[8]=653;
    (IntSource_1_data)[9]=426;
    (IntSource_1_data)[10]=154;
    (IntSource_1_data)[11]=509;
    (IntSource_1_data)[12]=612;
    (IntSource_1_data)[13]=677;
    (IntSource_1_data)[14]=765;
    (IntSource_1_data)[15]=703;
    int32_t* split_IntSource_1_buffer_ptr_tail=split_IntSource_1_buffer;
}

static void BatcherPipe_1_1_init()
{
    BatcherPipe_1_1_SIZE = 16;
    BatcherPipe_1_1_START = 8;
}

static void IntSource_1_steady()
{
    COREVA_HWACC_MISC_TRACE(8);
    COREVA_HWACC_MISC_TRACE(2);
    if(((IntSource_1_to_BatcherPipe_1_1_buffer_ptr_head-IntSource_1_to_BatcherPipe_1_1_buffer)+16)>32+0)
    {
        IntSource_1_to_BatcherPipe_1_1_buffer_ptr_head=IntSource_1_to_BatcherPipe_1_1_buffer+0;
    }
    COREVA_HWACC_MISC_TRACE(4);
    int32_t* IntSource_1_to_BatcherPipe_1_2_send_buffer = (int32_t*)steadyClusterWaitWrite(&IntSource_1_to_BatcherPipe_1_2_sender);
    COREVA_HWACC_MISC_TRACE(4);
    int32_t* IntSource_1_to_BatcherPipe_1_3_send_buffer = (int32_t*)steadyClusterWaitWrite(&IntSource_1_to_BatcherPipe_1_3_sender);
    COREVA_HWACC_MISC_TRACE(4);
    int32_t* IntSource_1_to_BatcherPipe_1_4_send_buffer = (int32_t*)steadyClusterWaitWrite(&IntSource_1_to_BatcherPipe_1_4_sender);
    COREVA_HWACC_MISC_TRACE(4);
    int32_t* IntSource_1_to_BatcherPipe_1_5_send_buffer = (int32_t*)steadyClusterWaitWrite(&IntSource_1_to_BatcherPipe_1_5_sender);
    COREVA_HWACC_MISC_TRACE(4);
    int32_t* IntSource_1_to_BatcherPipe_1_6_send_buffer = (int32_t*)steadyClusterWaitWrite(&IntSource_1_to_BatcherPipe_1_6_sender);
    COREVA_HWACC_MISC_TRACE(4);
    int32_t* IntSource_1_to_BatcherPipe_1_7_send_buffer = (int32_t*)steadyClusterWaitWrite(&IntSource_1_to_BatcherPipe_1_7_sender);
    COREVA_HWACC_MISC_TRACE(4);
    int32_t* IntSource_1_to_BatcherPipe_1_8_send_buffer = (int32_t*)steadyClusterWaitWrite(&IntSource_1_to_BatcherPipe_1_8_sender);
    COREVA_HWACC_MISC_TRACE(4);
    int32_t* split_IntSource_1_buffer_ptr_head=split_IntSource_1_buffer;
    COREVA_HWACC_MISC_TRACE(4); // SteadyWaitWrite MergeBuffer split
    for (int _i=0; _i<128; _i++)
        IntSource(16, IntSource_1_data, &IntSource_1_index, &split_IntSource_1_buffer_ptr_head);
    COREVA_HWACC_MISC_TRACE(5);
    // SimpleMergeSplitBuffer -> Split
    int32_t* split_IntSource_1_buffer_ptr_tail=split_IntSource_1_buffer;
    for (unsigned int _i=0;_i<1;_i++)
    {
        int32_t _tmp;
        *IntSource_1_to_BatcherPipe_1_1_buffer_ptr_head++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_1_buffer_ptr_head++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_1_buffer_ptr_head++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_1_buffer_ptr_head++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_1_buffer_ptr_head++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_1_buffer_ptr_head++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_1_buffer_ptr_head++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_1_buffer_ptr_head++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_1_buffer_ptr_head++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_1_buffer_ptr_head++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_1_buffer_ptr_head++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_1_buffer_ptr_head++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_1_buffer_ptr_head++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_1_buffer_ptr_head++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_1_buffer_ptr_head++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_1_buffer_ptr_head++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_2_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_2_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_2_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_2_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_2_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_2_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_2_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_2_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_2_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_2_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_2_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_2_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_2_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_2_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_2_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_2_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_3_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_3_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_3_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_3_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_3_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_3_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_3_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_3_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_3_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_3_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_3_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_3_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_3_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_3_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_3_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_3_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_4_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_4_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_4_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_4_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_4_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_4_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_4_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_4_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_4_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_4_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_4_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_4_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_4_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_4_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_4_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_4_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_5_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_5_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_5_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_5_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_5_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_5_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_5_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_5_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_5_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_5_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_5_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_5_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_5_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_5_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_5_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_5_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_6_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_6_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_6_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_6_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_6_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_6_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_6_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_6_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_6_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_6_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_6_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_6_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_6_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_6_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_6_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_6_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_7_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_7_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_7_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_7_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_7_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_7_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_7_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_7_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_7_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_7_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_7_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_7_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_7_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_7_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_7_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_7_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_8_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_8_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_8_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_8_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_8_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_8_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_8_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_8_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_8_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_8_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_8_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_8_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_8_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_8_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_8_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
        *IntSource_1_to_BatcherPipe_1_8_send_buffer++ = ((*split_IntSource_1_buffer_ptr_tail++));
    }
    COREVA_HWACC_MISC_TRACE(7);  // SimpleMergeSplitBuffer split
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to BatcherPipe_1_1
    steadyClusterDoneWrite(&IntSource_1_to_BatcherPipe_1_2_sender);
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to BatcherPipe_1_2
    steadyClusterDoneWrite(&IntSource_1_to_BatcherPipe_1_3_sender);
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to BatcherPipe_1_3
    steadyClusterDoneWrite(&IntSource_1_to_BatcherPipe_1_4_sender);
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to BatcherPipe_1_4
    steadyClusterDoneWrite(&IntSource_1_to_BatcherPipe_1_5_sender);
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to BatcherPipe_1_5
    steadyClusterDoneWrite(&IntSource_1_to_BatcherPipe_1_6_sender);
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to BatcherPipe_1_6
    steadyClusterDoneWrite(&IntSource_1_to_BatcherPipe_1_7_sender);
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to BatcherPipe_1_7
    steadyClusterDoneWrite(&IntSource_1_to_BatcherPipe_1_8_sender);
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to BatcherPipe_1_8
}

static void BatcherPipe_1_1_steady()
{
    COREVA_HWACC_MISC_TRACE(2);
    if(((IntSource_1_to_BatcherPipe_1_1_buffer_ptr_tail-IntSource_1_to_BatcherPipe_1_1_buffer)+16)>32+0)
        IntSource_1_to_BatcherPipe_1_1_buffer_ptr_tail=IntSource_1_to_BatcherPipe_1_1_buffer+0;
    COREVA_HWACC_MISC_TRACE(3);
    int32_t* BatcherPipe_1_1_to_IntPrinter_1_send_buffer = (int32_t*)steadyClusterWaitWrite(&BatcherPipe_1_1_to_IntPrinter_1_sender);
    COREVA_HWACC_MISC_TRACE(4);
    BatcherPipe(8, 16, &IntSource_1_to_BatcherPipe_1_1_buffer_ptr_tail, &BatcherPipe_1_1_to_IntPrinter_1_send_buffer);
    COREVA_HWACC_MISC_TRACE(5);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from IntSource_1
    steadyClusterDoneWrite(&BatcherPipe_1_1_to_IntPrinter_1_sender);
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to IntPrinter_1
}

static void InitChannels_cpu_0_0_5()
{
    // InitRead IntSource_1_to_BatcherPipe_1_1 (0 0 0 16 16)
    IntSource_1_to_BatcherPipe_1_1_buffer_ptr_tail=IntSource_1_to_BatcherPipe_1_1_buffer;
    // InitWrite BatcherPipe_1_1_to_IntPrinter_1 (0 0 0 16 16)
    initSender(&BatcherPipe_1_1_to_IntPrinter_1_sender, 0, 7, 16, sizeof(int32_t), 3, DATA_MEM);
    // InitWrite IntSource_1_to_BatcherPipe_1_3 (0 0 0 16 16)
    initSender(&IntSource_1_to_BatcherPipe_1_3_sender, 0, 7, 16, sizeof(int32_t), 3, DATA_MEM);
    // InitWrite IntSource_1_to_BatcherPipe_1_7 (0 0 0 16 16)
    initSender(&IntSource_1_to_BatcherPipe_1_7_sender, 0, 6, 16, sizeof(int32_t), 3, DATA_MEM);
    // InitWrite IntSource_1_to_BatcherPipe_1_1 (0 0 0 16 16)
    IntSource_1_to_BatcherPipe_1_1_buffer_ptr_head=IntSource_1_to_BatcherPipe_1_1_buffer;
    // InitWrite IntSource_1_to_BatcherPipe_1_5 (0 0 0 16 16)
    initSender(&IntSource_1_to_BatcherPipe_1_5_sender, 0, 4, 16, sizeof(int32_t), 3, DATA_MEM);
    // InitWrite IntSource_1_to_BatcherPipe_1_8 (0 0 0 16 16)
    initSender(&IntSource_1_to_BatcherPipe_1_8_sender, 0, 3, 16, sizeof(int32_t), 3, DATA_MEM);
    // InitWrite IntSource_1_to_BatcherPipe_1_4 (0 0 0 16 16)
    initSender(&IntSource_1_to_BatcherPipe_1_4_sender, 0, 2, 16, sizeof(int32_t), 3, DATA_MEM);
    // InitWrite IntSource_1_to_BatcherPipe_1_2 (0 0 0 16 16)
    initSender(&IntSource_1_to_BatcherPipe_1_2_sender, 0, 1, 16, sizeof(int32_t), 3, DATA_MEM);
    // InitWrite IntSource_1_to_BatcherPipe_1_6 (0 0 0 16 16)
    initSender(&IntSource_1_to_BatcherPipe_1_6_sender, 0, 0, 16, sizeof(int32_t), 3, DATA_MEM);
}

static void InitFilters_cpu_0_0_5()
{
    IntSource_1_init();
    BatcherPipe_1_1_init();
}

static void PrimePump_cpu_0_0_5()
{
    for (int iteration=0; iteration<3; iteration++)
    {
        if (iteration >= 0)
        {
            IntSource_1_steady();
        }
        if (iteration >= 1)
        {
            BatcherPipe_1_1_steady();
        }
    }
}

int cpu_0_0_5()
{
    InitStatics_cpu_0_0_5();
    InitChannels_cpu_0_0_5();
    InitFilters_cpu_0_0_5();
    PrimePump_cpu_0_0_5();
    for(int _i=0;_i<STEADY_ITERATIONS;_i++) // Steady State Loop
    {
        COREVA_HWACC_MISC_TRACE(10);
        IntSource_1_steady();
        COREVA_HWACC_MISC_TRACE(11);
        BatcherPipe_1_1_steady();
    }
    	return 0;
}

