LIBRARY ieee;
USE ieee.std_logic_1164.ALL;
use STD.textio.all;

ENTITY ALUOP IS
	port (
		ALU_OP: in std_logic_vector(2 downto 0);
		FUNC: in std_logic_vector(5 downto 0);
		ALU_CTRL: out std_logic_vector(3 downto 0);
		Clk: in std_logic
	);
END ALUOP;

ARCHITECTURE behavior of ALUOP IS
BEGIN
	process(clk)
	BEGIN
		if(clk'event and clk = '1') then
			if ALU_OP = "100" then
				case FUNC IS
				 	when "100000" => --ADD
				 		ALU_Ctrl <= "0000";
				 	when "100010" => --SUB
				 		ALU_Ctrl <= "0001";
				 	when "011000" => --MULT
				 		ALU_Ctrl <= "0010";
				 	when "011010" => --DIV
				 		ALU_Ctrl <= "0011";
				 	when "100100" => --AND
				 		ALU_Ctrl <= "0100";
				 	when "100101" => --OR
				 		ALU_Ctrl <= "0101";
				 	when "100110" => --XOR
				 		ALU_Ctrl <= "0110";
				 	when "100111" => --NOR
				 		ALU_Ctrl <= "0111";
				 	when "000010" => --SRL
				 		ALU_Ctrl <= "1000";
				 	when "000000" => --SLL
				 		ALU_Ctrl <= "1001";
				 	when "000011" => --SRA
				 		ALU_Ctrl <= "1010";
				 	when "101010" => --SLT
				 		ALU_Ctrl <= "1011";
				 	when "010000" => --MFHI
				 		ALU_Ctrl <= "1100";
				 	when "010010" => --MHLD
				 		ALU_Ctrl <= "1101";
				 	when "001000" => --JR
				 		ALU_Ctrl <= "1110";
					when others =>
				 END case;
			if(ALU_OP = "010") then --branch, sub
				ALU_Ctrl <= "1101";
			END if;
			if(ALU_OP = "000") then --store/load, add
				ALU_Ctrl <= "0000";
			END if;
			if(ALU_OP = "101") then --xori, xor
				ALU_Ctrl <= "1110";
			END if;
			if(ALU_OP = "110") then --ori, or
				ALU_Ctrl <= "0111";
			END if;
			if(ALU_OP = "111") then --andi, and
				ALU_Ctrl <= "0001";
			END if;
			if(ALU_OP = "011") then --addi, add
				ALU_Ctrl <= "0000";
			END if;
			if(ALU_OP = "001") then --lui, add
				ALU_Ctrl <= "0000";
			END if;

			END if;
		END if;
	END process;
END behavior;

