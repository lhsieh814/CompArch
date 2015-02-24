JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java


CLASSES =\
		AddCalcRS.java \
		ArithLogicRS.java \
		BranchTargetBuffer.java \
		InstructionFetch.java \
		InstructionInfo.java \
		LoadRS.java \
		LoadStoreQueue.java \
		ReorderBuffer.java \
		StagesInCycle.java \
		Repository.java \
		Assembler.java \
		
		

default: classes

classes: $(CLASSES:.java=.class)

clean: $(RM) *.class
