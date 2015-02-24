LIBRARY ieee;
USE ieee.std_logic_1164.ALL;
use STD.textio.all;

ENTITY ALUOP2 IS
	port (
		instReg_opc_31to26 : in STD_LOGIC_VECTOR(5 DOWNTO 0);
		instReg_s_25to21 : in STD_LOGIC_VECTOR(4 DOWNTO 0);
		instReg_t_16to20: in STD_LOGIC_VECTOR(4 DOWNTO 0);
		instReg_i_0to15 : in STD_LOGIC_VECTOR(15 DOWNTO 0);
		ALU_CTRL: out std_logic_vector(4 downto 0);
		Clk: in std_logic
	);
END ALUOP2;

ARCHITECTURE behavior of ALUOP2 IS
signal FUNC : std_logic_vector(5 downto 0);
signal shamt : std_logic_vector(4 downto 0);
signal rd : std_logic_vector(4 downto 0);
signal ALU_OP : std_logic_vector(5 downto 0);
type state_type is (init, rLoading1, rLoading2, rWriting1, rWriting2, iLoading1, iLoading2, iWriting1, iWriting2);
type action_state_type is (waiting,add,sub,addi,mult,div,slt,slti,aluand,aluor,alunor,aluxor,andi,ori,xori,mfhi,mflo,lui,alusll,alusrl,alusra,lw,lb,sw,sb,beq,bne,j,jr,jal);
signal state: state_type:=init;
signal action_state: action_state_type:=waiting;
BEGIN
FUNC <= instReg_i_0to15(5 downto 0);
shamt <= instReg_i_0to15(10 downto 6);
rd <= instReg_i_0to15(15 downto 11);
ALU_OP <= instReg_opc_31to26;
	
	process(clk)
	BEGIN
		if RISING_EDGE(Clk) then
			case state is
			when init =>
			if ALU_OP = "000000" then
				case FUNC IS
				 	when "100000" => --ADD
				 		state <= rLoading1;
						action_state <= add;
				 	when "100010" => --SUB
				 		state <= rLoading1;
						action_state <= sub;
				 	when "011000" => --MULT
				 		state <= rLoading1;
						action_state <= mult;
				 	when "011010" => --DIV
				 		state <= rLoading1;
						action_state <= div;
				 	when "100100" => --AND
				 		state <= rLoading1;
						action_state <= aluand;
				 	when "100101" => --OR
				 		state <= rLoading1;
						action_state <= aluor;
				 	when "100110" => --XOR
				 		state <= rLoading1;
						action_state <= aluxor;
				 	when "100111" => --NOR
				 		state <= rLoading1;
						action_state <= alunor;
				 	when "000010" => --SRL
				 		state <= rLoading1;
						action_state <= alusrl;
				 	when "000000" => --SLL
				 		state <= rLoading1;
						action_state <= alusll;
				 	when "000011" => --SRA
				 		state <= rLoading1;
						action_state <= alusra;
				 	when "101010" => --SLT
				 		state <= rLoading1;
						action_state <= slt;
				 	when "010000" => --MFHI
				 		state <= rLoading1;
						action_state <= mfhi;
				 	when "010010" => --MFLO
				 		state <= rLoading1;
						action_state <= mflo;
				 	when "001000" => --JR
				 		state <= rLoading1;
						action_state <= jr;
					when others =>
				 END case;
			end if;

			if(ALU_OP = "000100") then --beq
				state <= iLoading1;
				action_state <= beq;
			END if;
			if(ALU_OP = "000101") then --bne, sub
				state <= iLoading1;
				action_state <= bne;
			END if;
			if(ALU_OP = "000010") then --j
				state <= iLoading1;
				action_state <= j;
			END if;
			if(ALU_OP = "000011") then --jal
				state <= iLoading1;
				action_state <= jal;
			END if;
			if(ALU_OP = "100011") then --lw load, add
				state <= iLoading1;
				action_state <= lw;
			END if;
			if(ALU_OP = "100000") then --lb load byte
				state <= iLoading1;
				action_state <= lb;
			END if;
			if(ALU_OP = "101000") then --sb save byte
				state <= iLoading1;
				action_state <= sb;
			END if;
			if(ALU_OP = "101011") then --sw store
				state <= iLoading1;
				action_state <= sw;
			END if;
			if(ALU_OP = "001110") then --xori, xor
				state <= iLoading1;
				action_state <= xori;
			END if;
			if(ALU_OP = "001101") then --ori, or
				state <= iLoading1;
				action_state <= ori;
			END if;
			if(ALU_OP = "001100") then --andi, and
				state <= iLoading1;
				action_state <= andi;
			END if;
			if(ALU_OP = "001010") then --slti
				state <= iLoading1;
				action_state <= slti;
			END if;
			if(ALU_OP = "001000") then --addi, add
				state <= iLoading1;
				action_state <= addi;
			END if;
			if(ALU_OP = "001111") then --lui, add
				state <= iLoading1;
				action_state <= lui;
			END if;
			when rLoading1=>
			when rLoading2=>
			when rWriting1=>
			when rWriting2=>
			when iLoading1=>
			when iLoading2=>
			when iWriting1=>
			when iWriting2=>
		end case;
		END if;
	END process;
END behavior;

