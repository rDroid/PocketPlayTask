PocketPlayTask
==============

Task for Pocket Play Developer Challenge

Run the program by  calling up the ParserProgram.java through command prompt, and supply arg[0] as the path to the log file. 

I tried to keep the program modular, and in doing so, i could not avoid the extra loops that was being called for calculating the most responsive Dyno. This could be easily calulated in the same loop that calculated the Mode for the response times,but I tried to separate the two functions, hence the extra loop and longer delay in log analysis. Hopefully, the code is good enough for pocket play. Comments and suggestions, both positive and negative are welcome. 
