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
int32_t VectSource_1_N;
int32_t* VectSource_1_Z;
int32_t VectSource_1_idx;

void InitStatics()
{
}

int32_t* VectSource_1_to_VectAddKernel_1_buffer_ptr_tail = 0;
int32_t* VectSource_1_to_VectAddKernel_1_buffer_ptr_head = 0;
int32_t VectSource_1_to_VectAddKernel_1_buffer[24+0];
int32_t* VectAddKernel_1_to_VectPrinter_1_buffer_ptr_tail = 0;
int32_t* VectAddKernel_1_to_VectPrinter_1_buffer_ptr_head = 0;
int32_t VectAddKernel_1_to_VectPrinter_1_buffer[12+0];

// WORK
static void VectPrinter(int32_t** restrict _buffer_ptr_tail)
{
    printf("%d\n", (*(*_buffer_ptr_tail)++));
}

static void VectSource(int32_t N, int32_t* restrict Z, int32_t *_idx, int32_t** restrict _buffer_ptr_head)
{
    *(*_buffer_ptr_head)++ = ((Z)[(*_idx)]);
    (*_idx)++;
    if ((*_idx)>=(N))
        (*_idx)=0;
}

static void VectAddKernel(int32_t** restrict _buffer_ptr_tail, int32_t** restrict _buffer_ptr_head)
{
    int32_t t1;
    int32_t t2;

    (t1)=0;
    (t2)=0;
    (t1)=(*(*_buffer_ptr_tail)++);
    (t2)=(*(*_buffer_ptr_tail)++);
    *(*_buffer_ptr_head)++ = ((t1)+(t2));
}

// PREWORK + INIT
static void VectSource_1_init()
{
    (VectSource_1_Z)=(int*)malloc(sizeof(int)*20);

    VectSource_1_N = 20;
    VectSource_1_Z[0] = 0; VectSource_1_Z[1] = 1; VectSource_1_Z[2] = 2; VectSource_1_Z[3] = 3; VectSource_1_Z[4] = 4; VectSource_1_Z[5] = 5; VectSource_1_Z[6] = 6; VectSource_1_Z[7] = 7; VectSource_1_Z[8] = 8; VectSource_1_Z[9] = 9; VectSource_1_Z[10] = 10; VectSource_1_Z[11] = 11; VectSource_1_Z[12] = 12; VectSource_1_Z[13] = 13; VectSource_1_Z[14] = 14; VectSource_1_Z[15] = 15; VectSource_1_Z[16] = 16; VectSource_1_Z[17] = 17; VectSource_1_Z[18] = 18; VectSource_1_Z[19] = 19; 
    (VectSource_1_idx)=0;
    (VectSource_1_idx)=0;
}

static void VectAddKernel_1_init()
{
}

static void VectPrinter_1_init()
{
}

static void VectSource_1_steady()
{
    COREVA_HWACC_MISC_TRACE(8);
    COREVA_HWACC_MISC_TRACE(2);
    if(((VectSource_1_to_VectAddKernel_1_buffer_ptr_head-VectSource_1_to_VectAddKernel_1_buffer)+12)>24+0)
    {
        VectSource_1_to_VectAddKernel_1_buffer_ptr_head=VectSource_1_to_VectAddKernel_1_buffer+0;
    }
    COREVA_HWACC_MISC_TRACE(4);
    for (int _i=0; _i<12; _i++)
        VectSource(20, VectSource_1_Z, &VectSource_1_idx, &VectSource_1_to_VectAddKernel_1_buffer_ptr_head);
    COREVA_HWACC_MISC_TRACE(5);
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to VectAddKernel_1
}

static void VectAddKernel_1_steady()
{
    COREVA_HWACC_MISC_TRACE(2);
    if(((VectSource_1_to_VectAddKernel_1_buffer_ptr_tail-VectSource_1_to_VectAddKernel_1_buffer)+12)>24+0)
        VectSource_1_to_VectAddKernel_1_buffer_ptr_tail=VectSource_1_to_VectAddKernel_1_buffer+0;
    COREVA_HWACC_MISC_TRACE(3);
    if(((VectAddKernel_1_to_VectPrinter_1_buffer_ptr_head-VectAddKernel_1_to_VectPrinter_1_buffer)+6)>12+0)
    {
        VectAddKernel_1_to_VectPrinter_1_buffer_ptr_head=VectAddKernel_1_to_VectPrinter_1_buffer+0;
    }
    COREVA_HWACC_MISC_TRACE(4);
    for (int _i=0; _i<6; _i++)
        VectAddKernel(&VectSource_1_to_VectAddKernel_1_buffer_ptr_tail, &VectAddKernel_1_to_VectPrinter_1_buffer_ptr_head);
    COREVA_HWACC_MISC_TRACE(5);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from VectSource_1
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to VectPrinter_1
}

static void VectPrinter_1_steady()
{
    COREVA_HWACC_MISC_TRACE(2);
    if(((VectAddKernel_1_to_VectPrinter_1_buffer_ptr_tail-VectAddKernel_1_to_VectPrinter_1_buffer)+6)>12+0)
        VectAddKernel_1_to_VectPrinter_1_buffer_ptr_tail=VectAddKernel_1_to_VectPrinter_1_buffer+0;
    COREVA_HWACC_MISC_TRACE(3);
    for (int _i=0; _i<6; _i++)
        VectPrinter(&VectAddKernel_1_to_VectPrinter_1_buffer_ptr_tail);
    COREVA_HWACC_MISC_TRACE(5);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from VectAddKernel_1
}

static void InitChannels()
{
    // InitRead VectSource_1_to_VectAddKernel_1 (0 0 0 12 12)
    VectSource_1_to_VectAddKernel_1_buffer_ptr_tail=VectSource_1_to_VectAddKernel_1_buffer;
    // InitRead VectAddKernel_1_to_VectPrinter_1 (0 0 0 6 6)
    VectAddKernel_1_to_VectPrinter_1_buffer_ptr_tail=VectAddKernel_1_to_VectPrinter_1_buffer;
    // InitWrite VectSource_1_to_VectAddKernel_1 (0 0 0 12 12)
    VectSource_1_to_VectAddKernel_1_buffer_ptr_head=VectSource_1_to_VectAddKernel_1_buffer;
    // InitWrite VectAddKernel_1_to_VectPrinter_1 (0 0 0 6 6)
    VectAddKernel_1_to_VectPrinter_1_buffer_ptr_head=VectAddKernel_1_to_VectPrinter_1_buffer;
}

static void InitFilters()
{
    VectSource_1_init();
    VectAddKernel_1_init();
    VectPrinter_1_init();
}

static void PrimePump()
{
    for (int iteration=0; iteration<3; iteration++)
    {
        if (iteration >= 0)
        {
            VectSource_1_steady();
        }
        if (iteration >= 1)
        {
            VectAddKernel_1_steady();
        }
        if (iteration >= 2)
        {
            VectPrinter_1_steady();
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
        VectSource_1_steady();
        COREVA_HWACC_MISC_TRACE(11);
        VectAddKernel_1_steady();
        COREVA_HWACC_MISC_TRACE(12);
        VectPrinter_1_steady();
    }
    	return 0;
}

