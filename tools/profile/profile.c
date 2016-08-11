#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <limits.h>
#include <locale.h>
#include <math.h>

const int NUM_SAMPLES = 15;

struct Edge
{
    char* otherEnd;
    char* channel;
    int estimated, min, max, total, count;
    int samples[NUM_SAMPLES];
    int wait_min, wait_max, wait_total;
    int wait_samples[NUM_SAMPLES];
	int copy_min, copy_max, copy_total;
	int copy_samples[NUM_SAMPLES];
    Edge *next;
};

struct Filter 
{    
    char *name;
    int estimated, min, max, total, count;
    Edge *wait_read;
    Edge *wait_write;
    Edge *done_read;
    Edge *done_write;
    Filter *next;
};

struct Processor
{
    char* name;
    Filter *filters;
    int estimated, min, max, total, count;
    int samples[NUM_SAMPLES];
    Processor *next;
};

Processor *first = NULL, *current;
Filter *current_filter;
int tp;


void SpecialEdge(Edge *e)
{
    printf("        estimated: %'d actual: ", e->estimated);
    if (e->min != e->max)
    {
        if (e->count > 0)
        {
            if (e->copy_samples[0] != 0)
                printf("[%d+%d", e->samples[0],e->copy_samples[0]);
            else
                printf("[%d", e->samples[0]);
            for (int i=1; i<e->count && i<NUM_SAMPLES; i++) {
                if (e->copy_samples[i] != 0)
                    printf(",%d+%d", e->samples[i], e->copy_samples[i]);
                else
                    printf(",%d", e->samples[i]);
            }
            int total = (int)round((e->total + e->copy_total)*1.0/e->count);
            if (e->copy_total!=0)
                printf("] %'d-%'d (avg %'d/%'d)\n", e->min, e->max, (int)round(e->total*1.0/e->count), total);
            else
                printf("] %'d-%'d (avg %'d)\n", e->min, e->max, (int)round(e->total*1.0/e->count));
        } else
            printf("0?\n");
    } else
        printf("%'d\n", e->min);
}

void DisplayEdge(Edge *e)
{
    printf("actual:");
    if (e->min != e->max)
        if (e->count > 0)
        {
            printf("[%d", e->samples[0]);
            for (int i=1; i<e->count && i<NUM_SAMPLES; i++)
                printf(",%d", e->samples[i]);
            printf("] %'d-%'d (avg %'d)", e->min, e->max, (int)round(e->total*1.0/e->count));
        }
        else
            printf("never called???\n");
    else
        printf("%'d", e->min);

    if (e->wait_min != e->wait_max)
        if (e->count > 0)
        {
            printf(" + waiting:");
            printf("[%d", e->wait_samples[0]);
            for (int i=1; i<e->count && i<NUM_SAMPLES; i++)
                printf(",%d", e->wait_samples[i]);
            printf("] %'d-%'d (avg %'d)", e->wait_min, e->wait_max, (int)round(e->total*1.0/e->count));
        }
        else
            printf("never called???");
    else
        printf(" + waiting: %'d", e->wait_min);
    
    if (e->copy_total > 0 && e->count > 0)
    { 
        printf(" + copy:");
        printf("[%d", e->copy_samples[0]);
        for (int i=1;i<e->count && i<NUM_SAMPLES; i++)
            printf(",%d", e->copy_samples[i]);
        printf("] %'d-%'d\n", e->copy_min, e->copy_max);
    } else
        printf("\n");
    
        
}

int readnum(char* str)
{
    return atoi(strchr(str, ':')+ 2);
}

char* Copy(char* str)
{
    char* start = strstr(str, " on ") + 4;
    char* copy = (char*)malloc(strlen(start)+1);
    strcpy(copy, start);
    return copy;
}

void updateEdge(Edge* e, int cycles, int waiting, int copy_time)
{
    cycles -= waiting;
    cycles -= copy_time;
    if (cycles<0) {
        printf("Cycles below 0\n");
        exit(-1);
    }
    if (e->count < NUM_SAMPLES)
    {
        e->samples[e->count] = cycles;
        e->wait_samples[e->count] = waiting;
        e->copy_samples[e->count] = copy_time;
    }
    e->count++;
    e->total += cycles;
    if (cycles < e->min)
        e->min = cycles;
    if (cycles > e->max)
        e->max = cycles;
    e->wait_total += waiting;
       
    if (waiting < e->wait_min)
        e->wait_min = waiting;
    if (waiting > e->wait_max)
        e->wait_max = waiting;

    e->copy_total += copy_time;
    if (copy_time < e->copy_min)
        e->copy_min = copy_time;
    if (copy_time > e->copy_max)
        e->copy_max = copy_time;
}

void List(const char* channel)
{
    printf("%s\n", channel);

    printf("    Wait Read\n");
    for (Processor *p = first; p != NULL; p=p->next)
        for (Filter *f = p->filters; f!=NULL; f=f->next)
            for (Edge *e = f->wait_read; e!=NULL; e=e->next)
                if (strcmp(e->channel, channel) == 0)
                    SpecialEdge(e);
    printf("    Wait Write\n");
    for (Processor *p = first; p != NULL; p=p->next)
        for (Filter *f = p->filters; f!=NULL; f=f->next)
            for (Edge *e = f->wait_write; e!=NULL; e=e->next)
                if (strcmp(e->channel, channel) == 0)
                    SpecialEdge(e);
    printf("    Done Read\n");
    for (Processor *p = first; p != NULL; p=p->next)
        for (Filter *f = p->filters; f!=NULL; f=f->next)
            for (Edge *e = f->done_read; e!=NULL; e=e->next)
                if (strcmp(e->channel, channel) == 0)
                    SpecialEdge(e);
    printf("    Done Write\n");
    for (Processor *p = first; p != NULL; p=p->next)
        for (Filter *f = p->filters; f!=NULL; f=f->next)
            for (Edge *e = f->done_write; e!=NULL; e=e->next)
                if (strcmp(e->channel, channel) == 0)
                    SpecialEdge(e);
}

/**
  * This appends newEdge to the end of startEdge
  * 
  */ 
void appendEdge(Edge* startEdge, Edge* newEdge) {
    Edge* tmp = startEdge;
    while (tmp->next != NULL)
        tmp = tmp->next;
    tmp->next = newEdge;
}

int main(int argc, char* argv[])
{
    setlocale(LC_NUMERIC, "");
    FILE *est = fopen("PerfEstimate.txt", "r");
    if (est == NULL)
    {
        fprintf(stderr, "Error: Failed to open PerfEstimate.txt\n");
        return 0;
    }
    char* line = NULL;
    size_t len = 0;
    ssize_t read;
    while ((read = getline(&line, &len, est)) != -1)
    {
		// remove trailing line break/carriage return
		if (line[read-1] == '\n')
			line[read-1] = '\0';
		if (line[read-2] == '\r')
			line[read-2] = '\0';
        if (line[0] == 'c') // new processor
        {
            Processor *p = (Processor*)malloc(sizeof(Processor));
            p->name = (char*)malloc(strlen(line)+1);
            p->total = 0;
            p->count = 0;
            p->min = INT_MAX;
            p->filters = NULL;
            memcpy(p->name, line, strlen(line));
            if (first == NULL)
                first = p;
            else
                current->next = p;
            current = p;
        } else if (line[0] == 't')
        {
            tp = readnum(line);
        } else if (line[9] == ':')
        {
            current->estimated = readnum(line);
        } else if (line[4] != ' ')
        {
            Filter *f = (Filter*)malloc(sizeof(Filter));
            f->min = INT_MAX;
            f->count = f->total = 0;
            f->name = (char*)malloc(strlen(line+4));
            memcpy(f->name, line+4, strlen(line+4));
            if (current->filters == NULL)
                current->filters = f;
            else
                current_filter->next = f;
            current_filter = f;
        } else if (line[10] == 'r')
        {
            current_filter->estimated = readnum(line);
        } else if (line[10] == 'i') // wait
        {
            if (line[13] == 'w') // write
            {
                Edge *e = (Edge*)malloc(sizeof(Edge));
                e->channel = Copy(line);
                e->min = e->wait_min = INT_MAX;
                e->count = e->max = e->wait_max = e->total = e->wait_total = e->copy_total = 0;
                e->estimated = readnum(line);
                *strchr(line, ':') = '\0';
                e->otherEnd = (char*)malloc(strlen(line+19));
                memcpy(e->otherEnd, line+19, strlen(line+19));
                if (current_filter->wait_write == NULL)
                    current_filter->wait_write = e;
                else
                    appendEdge(current_filter->wait_write,e);
            }
            else if (line[13] == 'r') // read
            {
                Edge *e = (Edge*)malloc(sizeof(Edge));
                e->channel = Copy(line);
                e->min = e->wait_min = e->copy_min = INT_MAX;
                e->count = e->max = e->wait_max = e->total = e->wait_total = e->copy_max = e->copy_total = 0;
                e->estimated = readnum(line);
                *strchr(line, ':') = '\0';
                e->otherEnd = (char*)malloc(strlen(line+18));
                memcpy(e->otherEnd, line+18, strlen(line+18));
                if (current_filter->wait_read == NULL)
                    current_filter->wait_read = e;
                else
                    appendEdge(current_filter->wait_read,e);
            } else
            {
                printf("unexpected input %s !!!\n", line);
                return 0;;
            }
        } else if (line[10] == 'n') // done
        {
            if (line[13] == 'w') // write
            {
                Edge *e = (Edge*)malloc(sizeof(Edge));
                e->channel = Copy(line);
                e->min = INT_MAX;
                e->estimated = readnum(line);
                *strchr(line, ':') = '\0';
                e->otherEnd = (char*)malloc(strlen(line+19));
                memcpy(e->otherEnd, line+19, strlen(line+19));
                if (current_filter->done_write == NULL)
                    current_filter->done_write = e;
                else
                    appendEdge(current_filter->done_write,e);
            } else if (line[13] == 'r') // read
            {
                Edge *e = (Edge*)malloc(sizeof(Edge));
                e->channel = Copy(line);
                e->min = INT_MAX;
                e->estimated = readnum(line);
                *strchr(line, ':') = '\0';
                e->otherEnd = (char*)malloc(strlen(line+18));
                memcpy(e->otherEnd, line+18, strlen(line+18));
                if (current_filter->done_read == NULL)
                    current_filter->done_read = e;
                else
                    appendEdge(current_filter->done_read,e);
            } else
            {
                printf("unexpected input %s !!!\n", line);
                return 0;;
            }
        } else
        {
            printf("unexpected input: %s !!!!\n", line);
            return 0;;
        }
    }

    for (Processor *p = first; p != NULL; p=p->next)
    {
        printf("processor %s\n", p->name);
        int steady = 0;
        char filename[128];
        sprintf(filename, "sim/trace/sim_%s.csv", p->name);
        FILE *trace = fopen(filename, "r");
        if (trace == NULL)
        {
            fprintf(stderr, "Error: failed to open %s\n", filename);
            break;
        }

        Filter *filter;
        char* line = NULL;
        size_t len = 0;
        ssize_t read;
        int prev_cycle = 0;
        int start_iteration = 0;
        Edge *next_wait_read, *next_wait_write, *next_done_read, *next_done_write;
        int start_waiting = -1, waiting, copy_time = 0;

        while ((read = getline(&line, &len, trace)) != -1)
        {
            line[strlen(line)-1] = '\0';
            int cycle = atoi(line);
            int label = atoi(strchr(line, ';')+1);
            int diff = cycle - prev_cycle;
            //printf("diff = %d - %d = %d\n", cycle, prev_cycle, diff);

            if (label < 10 && !steady)
                continue;

            switch (label)
            {
                case 0:
                    if (waiting == 0)
                    start_waiting = cycle;
                    //printf("case 0, start_waiting = %d\n", start_waiting);
                    break;
                case 1:
                    waiting = cycle - start_waiting;
                    //printf("case 1, waiting = %d - %d = %d\n", cycle, start_waiting, waiting);
                    break;
                case 2: // start wait
                    prev_cycle = cycle;
                    waiting = 0;
                    copy_time = 0;
                    //printf("case 2, previous_cycle = %d\n", prev_cycle);
                    break;
                case 3: // end wait read
                    //printf("case 3, diff = %d, waiting = %d\n", diff, waiting);
                    updateEdge(next_wait_read, diff, waiting, copy_time);
                    next_wait_read = next_wait_read->next;
                    prev_cycle = cycle;
                    //printf("previous_cycle = %d\n", prev_cycle);
                    waiting = 0;
                    break;
                case 4: // end wait write
                    //printf("case 4, diff = %d, waiting = %d\n", diff, waiting);
                    updateEdge(next_wait_write, diff, waiting, 0);
                    next_wait_write = next_wait_write->next;
                    prev_cycle = cycle;
                    waiting = 0;
                    //printf("previous_cycle = %d\n", prev_cycle);
                    break;
                case 5: // end work
                    //printf("case 5, diff = %d, waiting = %d\n", diff, waiting);
                    filter->total += diff;
                    filter->count++;
                    if (diff > filter->max)
                        filter->max = diff;
                    if (diff < filter->min)
                        filter->min = diff;
                    prev_cycle = cycle;
                    //printf("previous_cycle = %d\n", prev_cycle);
                    waiting = 0;
                    break;
                case 6: // end done read
                    //printf("case 6, diff = %d, waiting = %d\n", diff, waiting);
                    updateEdge(next_done_read, diff, waiting, 0);
                    next_done_read = next_done_read->next;
                    prev_cycle = cycle;
                    //printf("previous_cycle = %d\n", prev_cycle);
                    waiting = 0;
                    break;
                case 7: // end done write
                    //printf("case 7, diff = %d, waiting = %d\n", diff, waiting);
                    updateEdge(next_done_write, diff, waiting, 0);
                    next_done_write = next_done_write->next;
                    prev_cycle = cycle;
                    start_waiting = -1;
                    //printf("previous_cycle = %d\n", prev_cycle);
                    waiting = 0;
                    break;
                case 8: //copy time marker
                    if (copy_time==0)
                        copy_time = cycle;
                    else
                        copy_time = cycle-copy_time;
                    break;
                default: // new filter
                    steady = 1;
                    if (label == 10) // start of a new iteration
                    {
                        if (start_iteration > 0) // at least one iteration has completed
                        {
                            int diff = cycle - start_iteration;
                            if (p->count<NUM_SAMPLES)
                                p->samples[p->count] = diff;
                            p->count++;
                            p->total += diff;
                            if (diff < p->min)
                              p->min  = diff;
                            if (diff > p->max)
                              p->max = diff;
                        }
                        start_iteration = cycle;
                    }
                    //printf("%d------------------\n", label);
                    filter = p->filters;
                    for (int count = 10; count < label; filter=filter->next)
                        count++;
                    next_wait_read = filter->wait_read;
                    next_wait_write = filter->wait_write;
                    next_done_read = filter->done_read;
                    next_done_write = filter->done_write;
            }
        }
    fclose(trace);
    }

    int tpmin = INT_MAX, tpmax = 0, tpavg = 0;
    List("MemoryChannel");
    List("MemoryChannel2");
    List("CoreVAClusterChannel");
    List("CoreVAFileOutputChannel");
    List("CoreVANocChannel");
    printf("\n");

    for (Processor *p = first; p != NULL; p=p->next)
    {
        printf("processor %s\n", p->name);
        for (Filter *f = p->filters; f!=NULL; f=f->next)
        {
            printf("  filter %s\n", f->name);
            for (Edge *e = f->wait_read; e!=NULL; e=e->next)
            {
                printf("    wait read %s on %s: estimated: %'d ", e->otherEnd, e->channel, e->estimated);
                DisplayEdge(e);
            }
            for (Edge *e = f->wait_write; e!=NULL; e=e->next)
            {
                printf("    wait write %s on %s: estimated: %'d ", e->otherEnd, e->channel, e->estimated);
                DisplayEdge(e);
            }
            printf("    work estimated: %'d actual: ", f->estimated);
            if (f->min != f->max)
                if (f->count > 0)
                    printf("%'d-%'d (%'d avg) not const\n", f->min, f->max, (int)round(f->total*1.0/f->count));
                else
                printf("never called???\n");
            else
                printf("%'d\n", f->min);

            for (Edge *e = f->done_read; e!=NULL; e=e->next)
            {
                printf("    done read %s on %s: estimated: %'d ", e->otherEnd, e->channel, e->estimated);
                DisplayEdge(e);
            }
            for (Edge *e = f->done_write; e!=NULL; e=e->next)
            {
                printf("    done write %s on %s: estimated: %'d ", e->otherEnd, e->channel, e->estimated);
                DisplayEdge(e);
            }
        }
        if (p->min != p->max)
            if (p->count > 0) {
                printf("  total estimated: %'d actual:[%'d",p->estimated,p->samples[0]);
                for (int i=1;i<NUM_SAMPLES;i++)
                    printf(",%'d",p->samples[i]);
                printf("] %'d-%'d (avg %'d)\n", p->min, p->max, (int)round(p->total*1.0/p->count));
            }
            else
                printf("never called???\n");
        else
            printf("  total estimated: %'d actual: %'d\n", p->estimated, p->min);

        if (p->min < tpmin)
            tpmin = p->min;
        if (p->max > tpmax)
            tpmax = p->max;
        if (p->count > 0 && p->total/p->count > tpavg)
            tpavg = (int)round(p->total*1.0/p->count);
    }
    if (tpmin != tpmax)
        printf("throughput estimated %'d, actual %'d-%'d (avg %'d) cycles/iteration\n", tp, tpmin, tpmax, tpavg);
    else
        printf("throughput estimated %'d, actual %'d cycles/iteration\n", tp, tpmin);
    return 0;
}
