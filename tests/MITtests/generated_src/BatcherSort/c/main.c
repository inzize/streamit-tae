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
#ifndef COREVA_HWACC_MISC_TRACE
    #define COREVA_HWACC_MISC_TRACE(id) 
#endif
int32_t IntSource_1_SIZE;
int32_t* IntSource_1_data = {0};
int32_t IntSource_1_index;
int32_t BatcherPipe_1_SIZE;
int32_t BatcherPipe_1_START;

void InitStatics()
{
}

int32_t* IntSource_1_to_BatcherPipe_1_buffer_ptr_tail = 0;
int32_t* IntSource_1_to_BatcherPipe_1_buffer_ptr_head = 0;
int32_t IntSource_1_to_BatcherPipe_1_buffer[256+0];
int32_t* BatcherPipe_1_to_IntPrinter_1_buffer_ptr_tail = 0;
int32_t* BatcherPipe_1_to_IntPrinter_1_buffer_ptr_head = 0;
int32_t BatcherPipe_1_to_IntPrinter_1_buffer[256+0];

// WORK
static void IntSource(int32_t SIZE, int32_t* restrict data, int32_t *_index, int32_t** restrict _buffer_ptr_head)
{
    *(*_buffer_ptr_head)++ = ((data)[(*_index)++]);
    if ((*_index)==(SIZE))
        (*_index)=0;
}

static void BatcherPipe(int32_t START, int32_t SIZE_02, int32_t** restrict _buffer_ptr_tail, int32_t** restrict _buffer_ptr_head)
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
        *(*_buffer_ptr_head)++ = ((ordering)[(i_02)]);
    }
    free(ordering);
}

static void IntPrinter(int32_t** restrict _buffer_ptr_tail)
{
    printf("%d\n", (*(*_buffer_ptr_tail)++));
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
}

static void BatcherPipe_1_init()
{
    BatcherPipe_1_SIZE = 16;
    BatcherPipe_1_START = 8;
}

static void IntPrinter_1_init()
{
}

static void IntSource_1_steady()
{
    COREVA_HWACC_MISC_TRACE(8);
    COREVA_HWACC_MISC_TRACE(2);
    if(((IntSource_1_to_BatcherPipe_1_buffer_ptr_head-IntSource_1_to_BatcherPipe_1_buffer)+128)>256+0)
    {
        IntSource_1_to_BatcherPipe_1_buffer_ptr_head=IntSource_1_to_BatcherPipe_1_buffer+0;
    }
    COREVA_HWACC_MISC_TRACE(4);
    for (int _i=0; _i<128; _i++)
        IntSource(16, IntSource_1_data, &IntSource_1_index, &IntSource_1_to_BatcherPipe_1_buffer_ptr_head);
    COREVA_HWACC_MISC_TRACE(5);
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to BatcherPipe_1
}

static void BatcherPipe_1_steady()
{
    COREVA_HWACC_MISC_TRACE(2);
    if(((IntSource_1_to_BatcherPipe_1_buffer_ptr_tail-IntSource_1_to_BatcherPipe_1_buffer)+128)>256+0)
        IntSource_1_to_BatcherPipe_1_buffer_ptr_tail=IntSource_1_to_BatcherPipe_1_buffer+0;
    COREVA_HWACC_MISC_TRACE(3);
    if(((BatcherPipe_1_to_IntPrinter_1_buffer_ptr_head-BatcherPipe_1_to_IntPrinter_1_buffer)+128)>256+0)
    {
        BatcherPipe_1_to_IntPrinter_1_buffer_ptr_head=BatcherPipe_1_to_IntPrinter_1_buffer+0;
    }
    COREVA_HWACC_MISC_TRACE(4);
    for (int _i=0; _i<8; _i++)
        BatcherPipe(8, 16, &IntSource_1_to_BatcherPipe_1_buffer_ptr_tail, &BatcherPipe_1_to_IntPrinter_1_buffer_ptr_head);
    COREVA_HWACC_MISC_TRACE(5);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from IntSource_1
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to IntPrinter_1
}

static void IntPrinter_1_steady()
{
    COREVA_HWACC_MISC_TRACE(2);
    if(((BatcherPipe_1_to_IntPrinter_1_buffer_ptr_tail-BatcherPipe_1_to_IntPrinter_1_buffer)+128)>256+0)
        BatcherPipe_1_to_IntPrinter_1_buffer_ptr_tail=BatcherPipe_1_to_IntPrinter_1_buffer+0;
    COREVA_HWACC_MISC_TRACE(3);
    for (int _i=0; _i<128; _i++)
        IntPrinter(&BatcherPipe_1_to_IntPrinter_1_buffer_ptr_tail);
    COREVA_HWACC_MISC_TRACE(5);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from BatcherPipe_1
}

static void InitChannels()
{
    // InitRead IntSource_1_to_BatcherPipe_1 (0 0 0 128 128)
    IntSource_1_to_BatcherPipe_1_buffer_ptr_tail=IntSource_1_to_BatcherPipe_1_buffer;
    // InitRead BatcherPipe_1_to_IntPrinter_1 (0 0 0 128 128)
    BatcherPipe_1_to_IntPrinter_1_buffer_ptr_tail=BatcherPipe_1_to_IntPrinter_1_buffer;
    // InitWrite IntSource_1_to_BatcherPipe_1 (0 0 0 128 128)
    IntSource_1_to_BatcherPipe_1_buffer_ptr_head=IntSource_1_to_BatcherPipe_1_buffer;
    // InitWrite BatcherPipe_1_to_IntPrinter_1 (0 0 0 128 128)
    BatcherPipe_1_to_IntPrinter_1_buffer_ptr_head=BatcherPipe_1_to_IntPrinter_1_buffer;
}

static void InitFilters()
{
    IntSource_1_init();
    BatcherPipe_1_init();
    IntPrinter_1_init();
}

static void PrimePump()
{
    for (int iteration=0; iteration<3; iteration++)
    {
        if (iteration >= 0)
        {
            IntSource_1_steady();
        }
        if (iteration >= 1)
        {
            BatcherPipe_1_steady();
        }
        if (iteration >= 2)
        {
            IntPrinter_1_steady();
        }
    }
}

int main()
{
    InitStatics();
    InitChannels();
    InitFilters();
    PrimePump();
    for(int _i=0;_i<STEADY_ITERATIONS;_i++) // Steady State Loop
    {
        COREVA_HWACC_MISC_TRACE(10);
        IntSource_1_steady();
        COREVA_HWACC_MISC_TRACE(11);
        BatcherPipe_1_steady();
        COREVA_HWACC_MISC_TRACE(12);
        IntPrinter_1_steady();
    }
    	return 0;
}

