onerror {resume}
quietly WaveActivateNextPane {} 0
add wave -noupdate /mipscpu/clk
add wave -noupdate /mipscpu/InstructionReady
add wave -noupdate -radix decimal /mipscpu/PCF
add wave -noupdate -radix decimal /mipscpu/PCIn
add wave -noupdate -radix binary /mipscpu/Instruction
add wave -noupdate -radix binary /mipscpu/InstrD
add wave -noupdate -radix decimal /mipscpu/PCPrime
add wave -noupdate -radix decimal /mipscpu/PCPlus4f
add wave -noupdate -radix decimal /mipscpu/PCplus4d
add wave -noupdate /mipscpu/PCReady
add wave -noupdate /mipscpu/StallF
add wave -noupdate /mipscpu/StallD
add wave -noupdate /mipscpu/BranchD
add wave -noupdate /mipscpu/ForwardAD
add wave -noupdate /mipscpu/ForwardBD
add wave -noupdate -radix binary /mipscpu/rsD
add wave -noupdate -radix binary /mipscpu/rtD
add wave -noupdate -radix binary /mipscpu/rdD
add wave -noupdate /mipscpu/FlushE
add wave -noupdate -radix binary /mipscpu/rsE
add wave -noupdate -radix binary /mipscpu/rtE
add wave -noupdate -radix binary /mipscpu/rdE
add wave -noupdate -radix binary /mipscpu/ForwardBE
add wave -noupdate -radix binary /mipscpu/WriteRegE
add wave -noupdate /mipscpu/MemToRegE
add wave -noupdate /mipscpu/RegWriteE
add wave -noupdate -radix binary /mipscpu/WriteRegM
add wave -noupdate /mipscpu/MemToRegM
add wave -noupdate /mipscpu/RegWriteM
add wave -noupdate /mipscpu/MemWriteM
add wave -noupdate -radix binary /mipscpu/writeDataM
add wave -noupdate -radix binary /mipscpu/WriteRegW
add wave -noupdate -radix binary /mipscpu/ForwardAE
add wave -noupdate /mipscpu/RegWriteW
add wave -noupdate /mipscpu/memoryReady
add wave -noupdate /mipscpu/fetchNextMemory
add wave -noupdate /mipscpu/readOrWrite
add wave -noupdate -radix binary /mipscpu/dataToWrite
add wave -noupdate -radix binary /mipscpu/memoryOutput
add wave -noupdate -radix binary /mipscpu/SignImmDSLL2
add wave -noupdate -radix decimal /mipscpu/PCBranchD
add wave -noupdate /mipscpu/PCSrcD
add wave -noupdate /mipscpu/RegWriteD
add wave -noupdate /mipscpu/MemToRegD
add wave -noupdate /mipscpu/MemWriteD
add wave -noupdate /mipscpu/alucontrolD
add wave -noupdate /mipscpu/alusrcD
add wave -noupdate /mipscpu/regdstD
add wave -noupdate /mipscpu/jumpD
add wave -noupdate -radix binary /mipscpu/SignImmD
add wave -noupdate -radix binary /mipscpu/RD1d
add wave -noupdate -radix binary /mipscpu/RD2d
add wave -noupdate -radix binary /mipscpu/EqualD
add wave -noupdate -radix binary /mipscpu/MuxAOut
add wave -noupdate -radix binary /mipscpu/MuxBOut
add wave -noupdate -radix binary /mipscpu/MemWriteE
add wave -noupdate -radix binary /mipscpu/aluControlE
add wave -noupdate -radix binary /mipscpu/aluSrcE
add wave -noupdate -radix binary /mipscpu/regDstE
add wave -noupdate -radix binary /mipscpu/signImmE
add wave -noupdate -radix binary /mipscpu/SrcAE
add wave -noupdate -radix binary /mipscpu/SrcBE
add wave -noupdate -radix binary /mipscpu/WriteDataE
add wave -noupdate -radix binary /mipscpu/RD1e
add wave -noupdate -radix binary /mipscpu/RD2e
add wave -noupdate -radix binary /mipscpu/ALUOutE
add wave -noupdate -radix binary /mipscpu/ALUOutM
add wave -noupdate -radix binary /mipscpu/resultW
add wave -noupdate -radix binary /mipscpu/nextAddress
add wave -noupdate -radix binary /mipscpu/ALUOutMInt
TreeUpdate [SetDefaultTree]
WaveRestoreCursors {{Cursor 1} {737 ns} 0}
quietly wave cursor active 1
configure wave -namecolwidth 344
configure wave -valuecolwidth 100
configure wave -justifyvalue left
configure wave -signalnamewidth 0
configure wave -snapdistance 10
configure wave -datasetprefix 0
configure wave -rowmargin 4
configure wave -childrowmargin 2
configure wave -gridoffset 0
configure wave -gridperiod 1
configure wave -griddelta 40
configure wave -timeline 0
configure wave -timelineunits ns
update
WaveRestoreZoom {0 ns} {1895 ns}
