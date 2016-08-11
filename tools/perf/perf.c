#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <limits.h>
#include <locale.h>

int main(int argc, char* argv[])
{
    setlocale(LC_NUMERIC, "");
    FILE *trace = fopen("sim/trace/sim_cpu_0_0_0.csv", "r");
    if (trace == NULL)
    {
        fprintf(stderr, "Error: failed to open sim/trace/sim_cpu_0_0_0.csv\n");
	return 0;
    }

    char* line = NULL;
    size_t len = 0;
    ssize_t read;
    int prev_cycle = 0;
    int filter;
    bool started = false;

    while ((read = getline(&line, &len, trace)) != -1)
    {
        line[strlen(line)-1] = '\0';
        int cycle = atoi(line);
        int label = atoi(strchr(line, ';')+1);
        int diff = cycle - prev_cycle;

        if (label > 9) // new filter
	{
	    if (label == 10)
	    {
	        if (started)
		    break;
                else
		    started = true;
	    }
	    filter = label;
	}
	else if (started && label == 5) // end of work
	    printf("%d=%d\n", filter-10, diff);
	prev_cycle = cycle;
    }

    fclose(trace);

    return 0;
}
