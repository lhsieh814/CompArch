library ieee;
use ieee.std_logic_1164.ALL;
use work.MIPSCPU_constants.ALL;
use ieee.numeric_std.ALL;

entity instructionMem is
PORT (
	clk : IN STD_LOGIC;
	reset : IN STD_LOGIC;
	instData : IN STD_LOGIC_VECTOR(31 DOWNTO 0);
	writeInst : IN STD_LOGIC;
	instReg_opc_31to26 : OUT STD_LOGIC_VECTOR(5 DOWNTO 0);
	instReg_s_25to21 : OUT STD_LOGIC_VECTOR(4 DOWNTO 0);
	instReg_t_16to20: OUT STD_LOGIC_VECTOR(4 DOWNTO 0);
	instReg_i_0to15 : OUT STD_LOGIC_VECTOR(15 DOWNTO 0) );
END instructionMem;

ARCHITECTURE behv OF instructionMem IS
BEGIN	
	PROCESS(clk, reset)
	BEGIN
		IF reset = '0' THEN
			instReg_opc_31to26 <= (OTHERS => '0'); 
			instReg_s_25to21 <= (OTHERS => '0'); 
			instReg_t_16to20 <= (OTHERS => '0'); 
			instReg_i_0to15 <= (OTHERS => '0'); 
		ELSIF RISING_EDGE(clk) THEN
			IF(writeInst = '1') THEN
				instReg_opc_31to26 <= instData(31 DOWNTO 26);
				instReg_s_25to21 <= instData(25 DOWNTO 21);
				instReg_t_16to20<= instData(20 DOWNTO 16);
				instReg_i_0to15 <= instData(15 DOWNTO 0);
			END IF;
		END IF;
	END PROCESS;
END behv;
