library IEEE; use IEEE.STD_LOGIC_1164.all;
entity ALUDecoder is
port(
	funct: in STD_LOGIC_VECTOR(5 downto 0);
	aluop: in STD_LOGIC_VECTOR(1 downto 0);
	alucontrol: out STD_LOGIC_VECTOR(3 downto 0));
end;

architecture behavior of ALUDecoder is
begin
process(funct,aluop) 
begin
case aluop is
	when "00" => alucontrol <= "0010"; --add (for 1w/sw/addi)
	when "01" => alucontrol <= "0110"; --sub (for beq)
when others => case funct is --R-type instructions
	when "100000" => alucontrol <= "0010"; --add
	when "100010" => alucontrol <= "0110"; --sub
	when "100100" => alucontrol <= "0000"; --and
	when "100101" => alucontrol <= "0001"; --or
	when "101010" => alucontrol <= "0111"; --slt
	when "011000" => alucontrol <= "1000"; --mult
	when "011010" => alucontrol <= "1001"; --div
	when "100110" => alucontrol <= "1010"; --xor
	when "000010" => alucontrol <= "1011"; --srl
	when "000000" => alucontrol <= "1100"; --sll
	when "000011" => alucontrol <= "1101"; --sra
	when "100111" => alucontrol <= "1110"; --nor
	when others => alucontrol <= "----"; --???
end case;
end case;
end process;
end;