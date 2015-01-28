/*
CS451 - Programming Assignment 3
Tai Tran
Matt DeSilvey
*/


#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <sys/ipc.h>
#include <sys/shm.h>

//Global shared memory
int shm_int;
int array[100];
int portion[4];

void* thread_sum();
pthread_mutex_t portion_lock;
pthread_mutex_t sum_lock;


main (int argc, char** argv) {
	int input_value;
	int index;
	
	 //Check number of arguments
	if(argc != 2) {
		fprintf(stderr, "Error: Incorrect number of arguments.\n");
		exit(-2);
	}
	
	//Check argument for proper input
	 if((input_value = atoi(argv[1])) == 0 && *(argv[1]) != '0') {
		 fprintf(stderr, "Error: Incorrect parameter, please enter an integer.\n");
		 exit(-2);
	 }
	 
	//Create the array of integers
	for(index = 0; index < 100; index++) {
		array[index] = input_value + index;
	}
	//Create portion array for dispenser
	for(index = 0; index < 4; index++) {
		portion[index] = index;
	}
	
	//Initialize lock for portion dispenser
	if(pthread_mutex_init(&portion_lock, NULL) == -1)
		fprintf(stderr, "Error: Failed to create portion mutex lock.\n");
	
	if(pthread_mutex_init(&sum_lock, NULL) == -1)
		fprintf(stderr, "Error: Failed to create portion mutex lock.\n");
	
	//Initialize and create the threads
	pthread_t threads[4];
	for(index = 0; index < 4; index++) {
		if(pthread_create(&threads[index], NULL, thread_sum, NULL) != 0)
			fprintf(stderr, "Error: Failed to create thread %d", index);
	}
	
	//Wait for the threads to finish
	for(index = 0; index <4; index++) {
		if(pthread_join(threads[index], NULL) != 0) {
			fprintf(stderr, "Error: Failed to join thread %d", index);
		}
	}	
	
	//Print the final sum
	printf("The sum from %d to %d is %d.\n", input_value, input_value+99, shm_int);
	
	exit(0);
}

void* thread_sum() {
	//Get portion of array to add
	int low_range = get_portion() *25;
	int high_range = low_range + 25;
	int sum = 0;
	
	//Add integers in array
	int index;
	for(index = low_range; index < high_range; index++) {
		sum += array[index];
	}
	
	//Enter critical section to add to global sum
	pthread_mutex_lock(&sum_lock);
	shm_int += sum;
	pthread_mutex_unlock(&sum_lock);
	//End critical section
		
}

int get_portion() {
	int index = 0;
	int return_portion;
	//Enter critical section to read global array and portions
	pthread_mutex_lock(&portion_lock);
		while(portion[index] == -1) {
			index++;		
		}
		return_portion = index;
		portion[index] = -1;
	pthread_mutex_unlock(&portion_lock); 
	//End critical section
	
	return return_portion;
}

