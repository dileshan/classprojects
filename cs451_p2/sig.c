/*
Tai Tran and Matt DeSilvey
CS451 Signal Program
Spring '08
*/


#include <stdio.h>
#include <sys/types.h>
#include <unistd.h>
#include <signal.h>
#include <setjmp.h>
#include <stdlib.h>

#define NPROCS 5
#define NEXECS 5

static sigjmp_buf jmpbuf;

void childcode(int intid);
void scheduler();

int A[1000];

/********************************************************
* Below are the ready queue and its manipulation routines
*********************************************************/

struct node
{
   int pri;
   int count;
   int pid;
   struct node *next;
};

struct node *RQhead;

void Enqueue(struct node *new)
{
   struct node *prev, *curr;

   if(RQhead == NULL)
      RQhead = new;
   else {
     prev=RQhead;
     curr=prev;

     while ((curr != NULL) && (curr->pri < new->pri)){
           prev = curr;
           curr = curr->next;
        };
     if(curr==RQhead){
       RQhead=new;
       new->next=curr;
       }
     else{
       prev->next = new;
       new->next = curr;
       }
     }  /* end else */
}  /* end Enqueue */


struct node *Dequeue()
{
   struct node *oldhead;

   oldhead = RQhead;
   if(RQhead != NULL)
      RQhead = RQhead->next;
   return oldhead;
}

int RQempty()
{
   return (RQhead == NULL);
}

/***********************************************
* The main program (parent) and its handler
***********************************************/

void parent_handler(int errno)
{
   printf("parent caught SIGUSR2\n");
}


int main(int argc,char argv [])
{

   int i,n;
   pid_t childpid[NPROCS];  /* save child pids as they are forked */
   struct node *ptr;   
   struct sigaction act;
   sigset_t sigset, sigoldmask;

   RQhead = NULL;
   n = NPROCS;

/* set up signal handler for SIGUSR2 */
	 act.sa_handler = parent_handler;
	 act.sa_flags = 0;
	 
	 if(sigemptyset(&act.sa_mask) == -1 || (sigaction(SIGUSR2, &act, NULL) == -1)) {
	 		perror("Error: Could not set up SIGURS2 handler");
	 		exit(-1);
		}

/* add SIGUSR2 to the set of blocked signals */
	 if(sigaddset(&act.sa_mask, SIGUSR2) == -1) {
	 		perror("Error: Could not ada SIGUSR2 to blocked list");
	 		exit(-1);
	 }
	  else if (sigprocmask(SIG_BLOCK, &act.sa_mask, NULL) == -1) {
			perror("Error: Could not add SIGUSR2 to blocked list");
			exit(-1);
		}

/* fork a fan of NPROCS children */
		pid_t forkedID = 1;
		for (i = 0; i < NPROCS; i++) {
			if(forkedID != 0) {
				forkedID = fork();
				childpid[i] = forkedID;
				if(forkedID == 0) {
					childcode(i);
				}
			}
		}
		
		

/* fill node per child, enqueue in priority order*/
		if(forkedID > 0) {		
					for (i=0; i<n; i++){   
							ptr = (struct node *)malloc(sizeof(struct node));
							ptr->pri = rand() % 10;
							printf("pri = %d \n",ptr->pri);
							ptr->count = NEXECS;
							ptr->pid = childpid[i];
							Enqueue(ptr);
					}
					
		/* call the scheduler */
		printf("Calling Scheduler\n");
		scheduler();
		}	
			
}


/****************************************************
* The scheduler maintains the ready queue and signals
* children to resume execution
*****************************************************
*  while the ready list is not empty
*    remove the first child from the ready queue and signal it
*    wait for it to complete
*    adjust its priority
*    if it has run fewer than NEXECS times (count is not down to 0)
*       put it back on the queue
*    else kill it
********************************************************/

void scheduler()
{
   struct node *ptr;
   sigset_t sigset;

   while ( !RQempty() ) {
		//Remove child from queue and signal it
   ptr = Dequeue();
	 if(kill(ptr->pid, SIGUSR1) == -1) {
		 perror("Error sending activate signal to child"); 
	 }
	 
	 sigemptyset(&sigset);
	 sigaddset(&sigset, SIGUSR2);
	 int inc_sig;
	 sigwait(&sigset, &inc_sig);
	 
/* adjust priority */  

      if (ptr->pri > 5)                
         ptr->pri++;   
      else
         ptr->pri = ptr->pri * 2;
			
/* check to see if child has executed enough */
/* enqueue it or kill it */
			if(ptr->count == 0) {
				kill(ptr->pid, SIGKILL);
			}
			else {
				(ptr->count)--;
				Enqueue(ptr);
			}
   };
}


/****************************************************
* The child code and its interrupt handler 
***********************************************/

void child_handler(int errno)
{
   printf("child caught SIGUSR1 \n");
/* resume child at its "resume point" */
	 
   siglongjmp(jmpbuf, 1);
}


void childcode(int intid)
{
   struct sigaction act;
   int i,time,sum, inc_sig;
		
	 
/* set up signal handler for SIGUSR1 */
	 act.sa_handler = child_handler;
	 act.sa_flags = 0;
	 
	 sigemptyset(&act.sa_mask);
	 sigaction(SIGUSR1, &act, NULL);

/* set up resume point */
	 
		
if(sigsetjmp(jmpbuf, 1) != 0) {
/* the child will do this meaningless loop when it is reactivated */

   printf("Child %d resumes \n",getpid());

   time = (int)(100.0 * drand48());
   for (i=0; i<time; i++){ sum = sum + A[i];
      };
	
/* signal parent and rest until signalled 
*  DO NOT branch back to the code above:  
*  let the signal handler bring you back to the "resume point"*/
			kill(getppid(), SIGUSR2);
	}
			
	sigwait(&act.sa_mask, &inc_sig);

}
