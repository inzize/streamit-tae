// generated by compiler
#include <pthread.h>
#include <mpsoc_com-lib.h>
#include <fio_com-lib.h>

extern void cpu_0_0_0();
extern void cpu_0_0_1();
extern void cpu_0_0_2();
extern void cpu_0_0_3();

int main()
{
	pthread_t threadCore[1][1][4];
	initMpsocController(1, 1, 4);

	//Intitilization for Pthread FileIO
	f = malloc(sizeof(fileio_t));
	f->channel = calloc (FIO_CHANNEL_MAX_NUM, sizeof (*f->channel));
	assert (f->channel != NULL);
	/* XXX: this is a bad design decision since programs depend on the (at
	 * compile time unknown) channel count and are _not_portable_! */
	f->scratchpadSize = SCRATCHPAD_SIZE - FIO_CHANNEL_MAX_NUM * FIO_CHANNEL_MEM_FOOTPRINT;
	f->scratchpad = calloc (f->scratchpadSize, sizeof (*f->scratchpad));
	assert (f->scratchpad != NULL);

	pthread_key_create(&procIdX, globalDestructor);
	pthread_key_create(&procIdY, globalDestructor);
	pthread_key_create(&procIdID, globalDestructor);
	pthread_key_create(&procType, globalDestructor);
	pthread_key_create(&procDebugFile, globalDestructor);

	pthread_create(&(threadCore[0][0][0]), NULL, Start, createPos(&cpu_0_0_0, 0, 0, 0, 'P'));
	pthread_create(&(threadCore[0][0][1]), NULL, Start, createPos(&cpu_0_0_1, 0, 0, 1, 'P'));
	pthread_create(&(threadCore[0][0][2]), NULL, Start, createPos(&cpu_0_0_2, 0, 0, 2, 'P'));
	pthread_create(&(threadCore[0][0][3]), NULL, Start, createPos(&cpu_0_0_3, 0, 0, 3, 'P'));

	pthread_join(threadCore[0][0][0], NULL);
	pthread_join(threadCore[0][0][1], NULL);
	pthread_join(threadCore[0][0][2], NULL);
	pthread_join(threadCore[0][0][3], NULL);
	return 0;
}
