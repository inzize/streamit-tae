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
char* FileReader_0_filename;
unsigned int FileReader_0_interval;
unsigned int FileReader_0_next_block;
volatile unsigned int FileReader_0_tmp;
int32_t* scramble_1_shift = {0};
int32_t diff_encode_1_last_out;
int32_t* chunk2symbol_1_bpsk_mod = {0};
int32_t* fir_filter_half_1_rcc_taps = {0};
int32_t* fir_filter_half_2_rcc_taps = {0};
char* FileWriter_1_filename;

void InitStatics()
{
}

int32_t* FileReader_0_to_scramble_1_buffer_ptr_tail = 0;
FILE *FileInput_to_FileReader_0_file;
int32_t *FileInput_to_FileReader_0_send_start;
int32_t FileInput_to_FileReader_0_send_buffer[1];
int32_t* FileReader_0_to_scramble_1_buffer_ptr_head = 0;
int32_t FileReader_0_to_scramble_1_buffer[2+1];
int32_t* scramble_1_to_diff_encode_1_buffer_ptr_tail = 0;
int32_t* scramble_1_to_diff_encode_1_buffer_ptr_head = 0;
int32_t scramble_1_to_diff_encode_1_buffer[24+8];
int32_t* diff_encode_1_to_chunk2symbol_1_buffer_ptr_tail = 0;
int32_t* diff_encode_1_to_chunk2symbol_1_buffer_ptr_head = 0;
int32_t diff_encode_1_to_chunk2symbol_1_buffer[16+1];
int32_t* chunk2symbol_1_to_fir_filter_half_1_buffer_ptr_tail = 0;
int32_t* chunk2symbol_1_to_fir_filter_half_2_buffer_ptr_tail = 0;
int32_t split_chunk2symbol_1_buffer[8];
int32_t* chunk2symbol_1_to_fir_filter_half_1_buffer_ptr_head = 0;
int32_t chunk2symbol_1_to_fir_filter_half_1_buffer[25+1];
int32_t* fir_filter_half_1_to_FileWriter_1_buffer_ptr_tail = 0;
int32_t* chunk2symbol_1_to_fir_filter_half_2_buffer_ptr_head = 0;
int32_t chunk2symbol_1_to_fir_filter_half_2_buffer[25+1];
int32_t* fir_filter_half_2_to_FileWriter_1_buffer_ptr_tail = 0;
int32_t* fir_filter_half_1_to_FileWriter_1_buffer_ptr_head = 0;
int32_t fir_filter_half_1_to_FileWriter_1_buffer[128+0];
int32_t* fir_filter_half_2_to_FileWriter_1_buffer_ptr_head = 0;
int32_t fir_filter_half_2_to_FileWriter_1_buffer[128+0];
FILE *FileWriter_1_to_FileOutput_file;
int32_t *FileWriter_1_to_FileOutput_send_start;
int32_t *FileWriter_1_to_FileOutput_send_buffer;
int32_t merge_FileWriter_1_buffer[128];

// WORK
static void chunk2symbol(int32_t* restrict bpsk_mod, int32_t** restrict _buffer_ptr_tail, int32_t** restrict split__buffer_ptr_head)
{
    int32_t input;

    (input)=(*(*_buffer_ptr_tail)++);
    *(*split__buffer_ptr_head)++ = ((bpsk_mod)[(input)]);
}

static void diff_encode(int32_t *_last_out, int32_t** restrict _buffer_ptr_tail, int32_t** restrict _buffer_ptr_head)
{
    (*_last_out)=((*_last_out)+(*(*_buffer_ptr_tail)++))%2;
    *(*_buffer_ptr_head)++ = ((*_last_out));
}

static void fir_filter_half(int32_t* restrict rcc_taps, int32_t** restrict _buffer_ptr_tail, int32_t** restrict _buffer_ptr_head)
{
    int32_t previous;
    int32_t current;
    int32_t j;

    (previous)=(*(*_buffer_ptr_tail+0));
    (current)=(*(*_buffer_ptr_tail+1));
    for ((j)=0; (j)<8; (j)++)
        {
        *(*_buffer_ptr_head)++ = (((rcc_taps)[(j)]*((current)>>10)+(rcc_taps)[(j)+8]*((previous)>>10))>>10);
    }
    (*(*_buffer_ptr_tail)++);
}

FILE *FileReader_0_file;
static void scramble(int32_t* restrict shift, int32_t** restrict _buffer_ptr_tail, int32_t** restrict _buffer_ptr_head)
{
    int32_t input;
    int32_t j;

    (input)=(*(*_buffer_ptr_tail)++);
    for ((j)=0; (j)<8; (j)++)
        {
        int32_t output;

        (output)=(((input)&(1<<(j)))>>(j))^((shift)[3]^(shift)[6]);
        (shift)[6]=(shift)[5];
        (shift)[5]=(shift)[4];
        (shift)[4]=(shift)[3];
        (shift)[3]=(shift)[2];
        (shift)[2]=(shift)[1];
        (shift)[1]=(shift)[0];
        (shift)[0]=(output);
        *(*_buffer_ptr_head)++ = ((output));
    }
}

void FileWriter_1_to_FileOutput_push(int32_t arg)
{
    *FileWriter_1_to_FileOutput_send_buffer++ = arg;
}

// PREWORK + INIT
static void FileReader_0_init()
{
    FileReader_0_filename = "input.stream";
    FileReader_0_interval = 0;
    FileReader_0_next_block = 0;
    FileReader_0_tmp = 0;
    /*  FileReader SplitBuffer inlined */
    //netInit:1 netsteady: 1
    FileInput_to_FileReader_0_send_start = FileInput_to_FileReader_0_send_buffer;
    for(int _i=0;_i<1;_i++)
    {
        fscanf(FileInput_to_FileReader_0_file,"%X",(unsigned int *)FileInput_to_FileReader_0_send_start++);
    }
    FileInput_to_FileReader_0_send_start = FileInput_to_FileReader_0_send_buffer;
    //Split1
    //list:1
    for (unsigned int _i=0;_i<1;_i++)
    {
        *FileReader_0_to_scramble_1_buffer_ptr_head++ = ((*FileInput_to_FileReader_0_send_start++));
    }
    /*  FileReader SplitBuffer done */
}

static void scramble_1_init()
{
    (scramble_1_shift)=(int32_t*)malloc(sizeof(int32_t)*7);
    (scramble_1_shift)[0]=1;
    (scramble_1_shift)[1]=1;
    (scramble_1_shift)[2]=0;
    (scramble_1_shift)[3]=1;
    (scramble_1_shift)[4]=1;
    (scramble_1_shift)[5]=0;
    (scramble_1_shift)[6]=0;
    {
        scramble(scramble_1_shift, &FileReader_0_to_scramble_1_buffer_ptr_tail, &scramble_1_to_diff_encode_1_buffer_ptr_head);
    }
}

static void diff_encode_1_init()
{
    (diff_encode_1_last_out)=0;
    {
        diff_encode(&diff_encode_1_last_out, &scramble_1_to_diff_encode_1_buffer_ptr_tail, &diff_encode_1_to_chunk2symbol_1_buffer_ptr_head);
    }
}

static void chunk2symbol_1_init()
{
    (chunk2symbol_1_bpsk_mod)=(int32_t*)malloc(sizeof(int32_t)*2);
    (chunk2symbol_1_bpsk_mod)[0]=741343;
    (chunk2symbol_1_bpsk_mod)[1]=-741343;
    int32_t* split_chunk2symbol_1_buffer_ptr_head=split_chunk2symbol_1_buffer;
    {
        chunk2symbol(chunk2symbol_1_bpsk_mod, &diff_encode_1_to_chunk2symbol_1_buffer_ptr_tail, &split_chunk2symbol_1_buffer_ptr_head);
    }
    int32_t* split_chunk2symbol_1_buffer_ptr_tail=split_chunk2symbol_1_buffer;
    for (unsigned int _i=0; _i<1;_i++)
    {
        int32_t _tmp;
        _tmp = (*split_chunk2symbol_1_buffer_ptr_tail++);
        *chunk2symbol_1_to_fir_filter_half_1_buffer_ptr_head++ = (_tmp);
        *chunk2symbol_1_to_fir_filter_half_2_buffer_ptr_head++ = (_tmp);
    }
}

static void fir_filter_half_1_init()
{
    (fir_filter_half_1_rcc_taps)=(int32_t*)malloc(sizeof(int32_t)*16);
    (fir_filter_half_1_rcc_taps)[0]=0;
    (fir_filter_half_1_rcc_taps)[1]=-666;
    (fir_filter_half_1_rcc_taps)[2]=69361;
    (fir_filter_half_1_rcc_taps)[3]=157964;
    (fir_filter_half_1_rcc_taps)[4]=255889;
    (fir_filter_half_1_rcc_taps)[5]=351141;
    (fir_filter_half_1_rcc_taps)[6]=430952;
    (fir_filter_half_1_rcc_taps)[7]=484036;
    (fir_filter_half_1_rcc_taps)[8]=502648;
    (fir_filter_half_1_rcc_taps)[9]=484036;
    (fir_filter_half_1_rcc_taps)[10]=430952;
    (fir_filter_half_1_rcc_taps)[11]=351141;
    (fir_filter_half_1_rcc_taps)[12]=255889;
    (fir_filter_half_1_rcc_taps)[13]=157964;
    (fir_filter_half_1_rcc_taps)[14]=69361;
    (fir_filter_half_1_rcc_taps)[15]=-666;
}

static void fir_filter_half_2_init()
{
    (fir_filter_half_2_rcc_taps)=(int32_t*)malloc(sizeof(int32_t)*16);
    (fir_filter_half_2_rcc_taps)[0]=0;
    (fir_filter_half_2_rcc_taps)[1]=-666;
    (fir_filter_half_2_rcc_taps)[2]=69361;
    (fir_filter_half_2_rcc_taps)[3]=157964;
    (fir_filter_half_2_rcc_taps)[4]=255889;
    (fir_filter_half_2_rcc_taps)[5]=351141;
    (fir_filter_half_2_rcc_taps)[6]=430952;
    (fir_filter_half_2_rcc_taps)[7]=484036;
    (fir_filter_half_2_rcc_taps)[8]=502648;
    (fir_filter_half_2_rcc_taps)[9]=484036;
    (fir_filter_half_2_rcc_taps)[10]=430952;
    (fir_filter_half_2_rcc_taps)[11]=351141;
    (fir_filter_half_2_rcc_taps)[12]=255889;
    (fir_filter_half_2_rcc_taps)[13]=157964;
    (fir_filter_half_2_rcc_taps)[14]=69361;
    (fir_filter_half_2_rcc_taps)[15]=-666;
}

static void FileWriter_1_init()
{
    FileWriter_1_filename = "output.stream";
}

static void FileReader_0_steady()
{
    COREVA_HWACC_MISC_TRACE(8);
    COREVA_HWACC_MISC_TRACE(2);
    /* SteadyWaitWrite */
    FileInput_to_FileReader_0_send_start = FileInput_to_FileReader_0_send_buffer;
    for(int _i=0;_i<1;_i++)
    {
        // read file continously
        if(fscanf(FileInput_to_FileReader_0_file,"%X",(uint32_t*)FileInput_to_FileReader_0_send_start++) == EOF)
        {
            rewind(FileInput_to_FileReader_0_file);
            fscanf(FileInput_to_FileReader_0_file,"%X",(uint32_t*)FileInput_to_FileReader_0_send_start-1);
        }
    }
    FileInput_to_FileReader_0_send_start = FileInput_to_FileReader_0_send_buffer;
    COREVA_HWACC_MISC_TRACE(3);
    if(((FileReader_0_to_scramble_1_buffer_ptr_head-FileReader_0_to_scramble_1_buffer)+1)>2+1)
    {
        FileReader_0_to_scramble_1_buffer_ptr_head=FileReader_0_to_scramble_1_buffer+1;
    }
    COREVA_HWACC_MISC_TRACE(4);
    COREVA_HWACC_MISC_TRACE(4); // SteadyWaitWrite MergeBuffer split
    COREVA_HWACC_MISC_TRACE(5);
    // FileReader SplitBuffer inlined
    for (unsigned int _i=0;_i<1;_i++)
    {
        int32_t _tmp;
        *FileReader_0_to_scramble_1_buffer_ptr_head++ = ((*FileInput_to_FileReader_0_send_start++));
    }
    COREVA_HWACC_MISC_TRACE(7);  // FileReaderSplitBuffer split
    /* SteadyDoneWriting */
    FileInput_to_FileReader_0_send_start = FileInput_to_FileReader_0_send_buffer;
    COREVA_HWACC_MISC_TRACE(7);  // SteadyDoneRead FileReader
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to scramble_1
}

static void scramble_1_steady()
{
    COREVA_HWACC_MISC_TRACE(2);
    if(((FileReader_0_to_scramble_1_buffer_ptr_tail-FileReader_0_to_scramble_1_buffer)+1)>2+1)
        FileReader_0_to_scramble_1_buffer_ptr_tail=FileReader_0_to_scramble_1_buffer+1;
    COREVA_HWACC_MISC_TRACE(3);
    if(((scramble_1_to_diff_encode_1_buffer_ptr_head-scramble_1_to_diff_encode_1_buffer)+8)>24+8)
    {
        __buildin__memcpy(scramble_1_to_diff_encode_1_buffer+1,scramble_1_to_diff_encode_1_buffer_ptr_head-7,7*sizeof(int32_t));
        scramble_1_to_diff_encode_1_buffer_ptr_head=scramble_1_to_diff_encode_1_buffer+8;
    }
    COREVA_HWACC_MISC_TRACE(4);
    scramble(scramble_1_shift, &FileReader_0_to_scramble_1_buffer_ptr_tail, &scramble_1_to_diff_encode_1_buffer_ptr_head);
    COREVA_HWACC_MISC_TRACE(5);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from FileReader_0
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to diff_encode_1
}

static void diff_encode_1_steady()
{
    COREVA_HWACC_MISC_TRACE(2);
    if(((scramble_1_to_diff_encode_1_buffer_ptr_tail-scramble_1_to_diff_encode_1_buffer)+8)>24+1)
        scramble_1_to_diff_encode_1_buffer_ptr_tail=scramble_1_to_diff_encode_1_buffer+1;
    COREVA_HWACC_MISC_TRACE(3);
    if(((diff_encode_1_to_chunk2symbol_1_buffer_ptr_head-diff_encode_1_to_chunk2symbol_1_buffer)+8)>16+1)
    {
        diff_encode_1_to_chunk2symbol_1_buffer_ptr_head=diff_encode_1_to_chunk2symbol_1_buffer+1;
    }
    COREVA_HWACC_MISC_TRACE(4);
    for (int _i=0; _i<8; _i++)
        diff_encode(&diff_encode_1_last_out, &scramble_1_to_diff_encode_1_buffer_ptr_tail, &diff_encode_1_to_chunk2symbol_1_buffer_ptr_head);
    COREVA_HWACC_MISC_TRACE(5);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from scramble_1
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to chunk2symbol_1
}

static void chunk2symbol_1_steady()
{
    COREVA_HWACC_MISC_TRACE(2);
    if(((diff_encode_1_to_chunk2symbol_1_buffer_ptr_tail-diff_encode_1_to_chunk2symbol_1_buffer)+8)>16+1)
        diff_encode_1_to_chunk2symbol_1_buffer_ptr_tail=diff_encode_1_to_chunk2symbol_1_buffer+1;
    COREVA_HWACC_MISC_TRACE(3);
    if(((chunk2symbol_1_to_fir_filter_half_1_buffer_ptr_head-chunk2symbol_1_to_fir_filter_half_1_buffer)+8)>25+1)
    {
        __buildin__memcpy(chunk2symbol_1_to_fir_filter_half_1_buffer+0,chunk2symbol_1_to_fir_filter_half_1_buffer_ptr_head-1,1*sizeof(int32_t));
        chunk2symbol_1_to_fir_filter_half_1_buffer_ptr_head=chunk2symbol_1_to_fir_filter_half_1_buffer+1;
    }
    COREVA_HWACC_MISC_TRACE(4);
    if(((chunk2symbol_1_to_fir_filter_half_2_buffer_ptr_head-chunk2symbol_1_to_fir_filter_half_2_buffer)+8)>25+1)
    {
        __buildin__memcpy(chunk2symbol_1_to_fir_filter_half_2_buffer+0,chunk2symbol_1_to_fir_filter_half_2_buffer_ptr_head-1,1*sizeof(int32_t));
        chunk2symbol_1_to_fir_filter_half_2_buffer_ptr_head=chunk2symbol_1_to_fir_filter_half_2_buffer+1;
    }
    COREVA_HWACC_MISC_TRACE(4);
    int32_t* split_chunk2symbol_1_buffer_ptr_head=split_chunk2symbol_1_buffer;
    COREVA_HWACC_MISC_TRACE(4); // SteadyWaitWrite MergeBuffer split
    for (int _i=0; _i<8; _i++)
        chunk2symbol(chunk2symbol_1_bpsk_mod, &diff_encode_1_to_chunk2symbol_1_buffer_ptr_tail, &split_chunk2symbol_1_buffer_ptr_head);
    COREVA_HWACC_MISC_TRACE(5);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from diff_encode_1
    // SimpleMergeSplitBuffer -> Split
    int32_t* split_chunk2symbol_1_buffer_ptr_tail=split_chunk2symbol_1_buffer;
    for (unsigned int _i=0;_i<8;_i++)
    {
        int32_t _tmp;
        _tmp = (*split_chunk2symbol_1_buffer_ptr_tail++);
        *chunk2symbol_1_to_fir_filter_half_1_buffer_ptr_head++ = (_tmp);
        *chunk2symbol_1_to_fir_filter_half_2_buffer_ptr_head++ = (_tmp);
    }
    COREVA_HWACC_MISC_TRACE(7);  // SimpleMergeSplitBuffer split
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to fir_filter_half_1
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to fir_filter_half_2
}

static void fir_filter_half_1_steady()
{
    COREVA_HWACC_MISC_TRACE(2);
    if(((chunk2symbol_1_to_fir_filter_half_1_buffer_ptr_tail-chunk2symbol_1_to_fir_filter_half_1_buffer)+8)>25+0)
        chunk2symbol_1_to_fir_filter_half_1_buffer_ptr_tail=chunk2symbol_1_to_fir_filter_half_1_buffer+0;
    COREVA_HWACC_MISC_TRACE(3);
    if(((fir_filter_half_1_to_FileWriter_1_buffer_ptr_head-fir_filter_half_1_to_FileWriter_1_buffer)+64)>128+0)
    {
        fir_filter_half_1_to_FileWriter_1_buffer_ptr_head=fir_filter_half_1_to_FileWriter_1_buffer+0;
    }
    COREVA_HWACC_MISC_TRACE(4);
    for (int _i=0; _i<8; _i++)
        fir_filter_half(fir_filter_half_1_rcc_taps, &chunk2symbol_1_to_fir_filter_half_1_buffer_ptr_tail, &fir_filter_half_1_to_FileWriter_1_buffer_ptr_head);
    COREVA_HWACC_MISC_TRACE(5);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from chunk2symbol_1
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to FileWriter_1
}

static void fir_filter_half_2_steady()
{
    COREVA_HWACC_MISC_TRACE(2);
    if(((chunk2symbol_1_to_fir_filter_half_2_buffer_ptr_tail-chunk2symbol_1_to_fir_filter_half_2_buffer)+8)>25+0)
        chunk2symbol_1_to_fir_filter_half_2_buffer_ptr_tail=chunk2symbol_1_to_fir_filter_half_2_buffer+0;
    COREVA_HWACC_MISC_TRACE(3);
    if(((fir_filter_half_2_to_FileWriter_1_buffer_ptr_head-fir_filter_half_2_to_FileWriter_1_buffer)+64)>128+0)
    {
        fir_filter_half_2_to_FileWriter_1_buffer_ptr_head=fir_filter_half_2_to_FileWriter_1_buffer+0;
    }
    COREVA_HWACC_MISC_TRACE(4);
    for (int _i=0; _i<8; _i++)
        fir_filter_half(fir_filter_half_2_rcc_taps, &chunk2symbol_1_to_fir_filter_half_2_buffer_ptr_tail, &fir_filter_half_2_to_FileWriter_1_buffer_ptr_head);
    COREVA_HWACC_MISC_TRACE(5);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from chunk2symbol_1
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite to FileWriter_1
}

static void FileWriter_1_steady()
{
    COREVA_HWACC_MISC_TRACE(2);
    if(((fir_filter_half_1_to_FileWriter_1_buffer_ptr_tail-fir_filter_half_1_to_FileWriter_1_buffer)+64)>128+0)
        fir_filter_half_1_to_FileWriter_1_buffer_ptr_tail=fir_filter_half_1_to_FileWriter_1_buffer+0;
    COREVA_HWACC_MISC_TRACE(3);
    if(((fir_filter_half_2_to_FileWriter_1_buffer_ptr_tail-fir_filter_half_2_to_FileWriter_1_buffer)+64)>128+0)
        fir_filter_half_2_to_FileWriter_1_buffer_ptr_tail=fir_filter_half_2_to_FileWriter_1_buffer+0;
    COREVA_HWACC_MISC_TRACE(3);
    FileWriter_1_to_FileOutput_send_buffer = FileWriter_1_to_FileOutput_send_start;
    COREVA_HWACC_MISC_TRACE(4); // SteadyWaitWrite FileWriter
    for (int _i=0; _i<64;_i++)
    {
        FileWriter_1_to_FileOutput_push((*fir_filter_half_1_to_FileWriter_1_buffer_ptr_tail++));
        FileWriter_1_to_FileOutput_push((*fir_filter_half_2_to_FileWriter_1_buffer_ptr_tail++));
    }
    COREVA_HWACC_MISC_TRACE(4);
    COREVA_HWACC_MISC_TRACE(5);
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from fir_filter_half_1
    COREVA_HWACC_MISC_TRACE(6); // SteadyDoneRead from fir_filter_half_2
    for (int _i=0; _i<128; _i++)
    {
        fprintf(FileWriter_1_to_FileOutput_file,"%08X\n",*(unsigned int*)&FileWriter_1_to_FileOutput_send_start[_i]);
    }
    COREVA_HWACC_MISC_TRACE(7); // SteadyDoneWrite FileWriter
    COREVA_HWACC_MISC_TRACE(9);
}

static void InitChannels()
{
    // InitRead FileReader_0_to_scramble_1 (1 1 0 1 1)
    FileReader_0_to_scramble_1_buffer_ptr_tail=FileReader_0_to_scramble_1_buffer;
    // InitRead scramble_1_to_diff_encode_1 (8 1 0 8 8)
    scramble_1_to_diff_encode_1_buffer_ptr_tail=scramble_1_to_diff_encode_1_buffer;
    // InitRead diff_encode_1_to_chunk2symbol_1 (1 1 0 8 8)
    diff_encode_1_to_chunk2symbol_1_buffer_ptr_tail=diff_encode_1_to_chunk2symbol_1_buffer;
    // InitRead chunk2symbol_1_to_fir_filter_half_1 (1 0 0 8 9)
    chunk2symbol_1_to_fir_filter_half_1_buffer_ptr_tail=chunk2symbol_1_to_fir_filter_half_1_buffer;
    // InitRead chunk2symbol_1_to_fir_filter_half_2 (1 0 0 8 9)
    chunk2symbol_1_to_fir_filter_half_2_buffer_ptr_tail=chunk2symbol_1_to_fir_filter_half_2_buffer;
    // InitRead fir_filter_half_1_to_FileWriter_1 (0 0 0 64 64)
    fir_filter_half_1_to_FileWriter_1_buffer_ptr_tail=fir_filter_half_1_to_FileWriter_1_buffer;
    // InitRead fir_filter_half_2_to_FileWriter_1 (0 0 0 64 64)
    fir_filter_half_2_to_FileWriter_1_buffer_ptr_tail=fir_filter_half_2_to_FileWriter_1_buffer;
    // InitWrite FileReader_0_to_scramble_1 (1 1 0 1 1)
    FileReader_0_to_scramble_1_buffer_ptr_head=FileReader_0_to_scramble_1_buffer;
    FileInput_to_FileReader_0_file = fopen("input.stream", "r");
    if (FileInput_to_FileReader_0_file == NULL) { fprintf(stderr, "failed to open \"input.stream\"\n"); exit(1); }
    // InitWrite scramble_1_to_diff_encode_1 (8 1 0 8 8)
    scramble_1_to_diff_encode_1_buffer_ptr_head=scramble_1_to_diff_encode_1_buffer;
    // InitWrite diff_encode_1_to_chunk2symbol_1 (1 1 0 8 8)
    diff_encode_1_to_chunk2symbol_1_buffer_ptr_head=diff_encode_1_to_chunk2symbol_1_buffer;
    // InitWrite chunk2symbol_1_to_fir_filter_half_1 (1 0 0 8 9)
    chunk2symbol_1_to_fir_filter_half_1_buffer_ptr_head=chunk2symbol_1_to_fir_filter_half_1_buffer;
    // InitWrite chunk2symbol_1_to_fir_filter_half_2 (1 0 0 8 9)
    chunk2symbol_1_to_fir_filter_half_2_buffer_ptr_head=chunk2symbol_1_to_fir_filter_half_2_buffer;
    // InitWrite fir_filter_half_1_to_FileWriter_1 (0 0 0 64 64)
    fir_filter_half_1_to_FileWriter_1_buffer_ptr_head=fir_filter_half_1_to_FileWriter_1_buffer;
    // InitWrite fir_filter_half_2_to_FileWriter_1 (0 0 0 64 64)
    fir_filter_half_2_to_FileWriter_1_buffer_ptr_head=fir_filter_half_2_to_FileWriter_1_buffer;
    FileWriter_1_to_FileOutput_file = fopen("output.stream_seq", "wb");
    FileWriter_1_to_FileOutput_send_start =  (int32_t*)malloc(128*sizeof(int32_t));
    if (FileWriter_1_to_FileOutput_file == NULL) { fprintf(stderr, "failed to open \"output.stream_seq\"\n"); exit(1); }
}

static void InitFilters()
{
    FileReader_0_init();
    scramble_1_init();
    diff_encode_1_init();
    chunk2symbol_1_init();
    fir_filter_half_1_init();
    fir_filter_half_2_init();
    FileWriter_1_init();
}

static void PrimePump()
{
    for (int iteration=0; iteration<6; iteration++)
    {
        if (iteration >= 0)
        {
            FileReader_0_steady();
        }
        if (iteration >= 1)
        {
            scramble_1_steady();
        }
        if (iteration >= 2)
        {
            diff_encode_1_steady();
        }
        if (iteration >= 3)
        {
            chunk2symbol_1_steady();
        }
        if (iteration >= 4)
        {
            fir_filter_half_1_steady();
            fir_filter_half_2_steady();
        }
        if (iteration >= 5)
        {
            FileWriter_1_steady();
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
        FileReader_0_steady();
        COREVA_HWACC_MISC_TRACE(11);
        scramble_1_steady();
        COREVA_HWACC_MISC_TRACE(12);
        diff_encode_1_steady();
        COREVA_HWACC_MISC_TRACE(13);
        chunk2symbol_1_steady();
        COREVA_HWACC_MISC_TRACE(14);
        fir_filter_half_1_steady();
        COREVA_HWACC_MISC_TRACE(15);
        fir_filter_half_2_steady();
        COREVA_HWACC_MISC_TRACE(16);
        FileWriter_1_steady();
    }
    	return 0;
}

