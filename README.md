# CompArch

### Processor Part

Any instruction goes to here.


### Assembler Part

####Usage

Step1 : open the the 'Assembler/Assembler/src' folder in the Terminal/Command

Step2 : Use 'make' command to build project

Step3 : 'java Main <filename>' 
		For example: 'java Main fib.asm'
		
Step4 : The binary machine code stores in the 'bin_result.txt'


Flaw: The regex can not handle the follwing case:
	1. '#Array:		.word	34, 78, 43, 67, 91, 56, 25, 69'
	2. '# working: $6= end ptr, $7= current element, $8= predicate'