LIBRARY ieee;
USE ieee.std_logic_1164.ALL;
use work.MIPSCPU_constants.ALL;

ENTITY MIPSCPU IS
END MIPSCPU;

ARCHITECTURE behavior OF MIPSCPU IS
component programCounter
port(
	clk : IN STD_LOGIC;
	reset : IN STD_LOGIC;
	writeEnable : IN STD_LOGIC;
	PCIn : IN integer;
	PCOut : OUT integer
);
end component;

component instructionMem
PORT (
	clk : IN STD_LOGIC;
	reset : IN STD_LOGIC;
	instData : IN STD_LOGIC_VECTOR(register_size DOWNTO 0);
	writeInst : IN STD_LOGIC;
	instReg_opc_31to26 : OUT STD_LOGIC_VECTOR(5 DOWNTO 0);
	instReg_s_25to21 : OUT STD_LOGIC_VECTOR(4 DOWNTO 0);
	instReg_t_16to20: OUT STD_LOGIC_VECTOR(4 DOWNTO 0);
	instReg_i_0to15 : OUT STD_LOGIC_VECTOR(15 DOWNTO 0) );
END component;

component instructionFetch IS
PORT(
	clk : in std_logic;
	nextAddress : in integer;
	instruction : out std_logic_vector(register_size downto 0);
	instReady : out std_logic;
	fetchNext : in std_logic
);
END component;

--testing stuff
signal clk : std_logic := '0';
signal instructionReady : std_logic;
signal instData : STD_LOGIC_VECTOR(register_size DOWNTO 0);
signal instReg_opc_31to26 : STD_LOGIC_VECTOR(5 DOWNTO 0);
signal instReg_s_25to21 : STD_LOGIC_VECTOR(4 DOWNTO 0);
signal instReg_t_16to20: STD_LOGIC_VECTOR(4 DOWNTO 0);
signal instReg_i_0to15 : STD_LOGIC_VECTOR(15 DOWNTO 0);
   constant clk_period : time := 10 ns;
begin
   -- Clock process definitions
   clk_process : process
   begin
		clk <= '0';
		wait for clk_period/2;
		clk <= '1';
		wait for clk_period/2;
   end process;


instFetch : instructionFetch
PORT MAP(
	clk=>clk,
	nextAddress=>0,-- to be from PC or inst>ructionregister
	instruction=>instData,
	instReady=>instructionReady,
	fetchNext=>'1'
);

instMem : instructionMem
PORT MAP(
	clk=>clk,
	reset=>'0',
	instData=>instData,
	writeInst=>'1',
	instReg_opc_31to26=>instReg_opc_31to26,
	instReg_s_25to21=>instReg_s_25to21,
	instReg_t_16to20=>instReg_t_16to20,
	instReg_i_0to15=>instReg_i_0to15);

END;