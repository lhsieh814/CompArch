# CompArch Deliverable 1
Group 6:
Yukun Su (260425096),
Yang Zhou (260401719),
Wei Sing Ta (260481021),
Lena Hsieh (260424941)

### Processor Part

Add the following vhd files to a ModelSim project (alternatively, open the MIPSCPU.mpf file provided) and compile them in the following order from first to last:

Main_Memory.vhd
Memory_in_Byte.vhd
MIPSCPU_constants.vhd
programCounter.vhd
ALUOP.vhd
dataFetch.vhd
instructionFetch.vhd
MIPSCPU.vhd

The main entity is MIPSCPU.vhd. It uses two separate memory modules: the instruction memory module (Init.dat) and the data memory module (DataInit.dat).
MIPSCPU can be compiled and run as a simulation in ModelSim whereby it will iterate through each instruction word in Init.dat and perform the associated changes to DataInit.dat.
Results of the instruction and data memory contents is dumped in MemCon.dat and DataMemCon.dat respectively.

Branching functionality was not successfully added to the CPU.


### Assembler Part

####Usage

Step1 : open the the 'Assembler/Assembler/src' folder in the Terminal/Command

Step2 : Use 'make' command to build project

Step3 : 'java Main <filename>' 
		For example: 'java Main fib.asm'
		
Step4 : The binary machine code stores in the 'bin_result.txt'
