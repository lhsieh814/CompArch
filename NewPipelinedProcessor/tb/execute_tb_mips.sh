# ghdl -r --ieee=synopsys -fexplicit tb_mips --vcd=mips.vcd
# gtkwave alu.vcd &
ghdl -r --ieee=synopsys -fexplicit tb_mips --wave=tb_mips.ghw
