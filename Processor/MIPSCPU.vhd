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
	PCReady : OUT STD_LOGIC;
	PCIn : IN integer;
	PCOut : OUT integer
);
end component;

component instructionFetch IS
PORT(
	clk : in std_logic;
	nextAddress : in integer;
	instruction : out std_logic_vector(register_size downto 0);
	instReady : out std_logic;
	fetchNext : in std_logic;
	instReg_opc_31to26 : OUT STD_LOGIC_VECTOR(5 DOWNTO 0);
	instReg_s_25to21 : OUT STD_LOGIC_VECTOR(4 DOWNTO 0);
	instReg_t_16to20: OUT STD_LOGIC_VECTOR(4 DOWNTO 0);
	instReg_i_0to15 : OUT STD_LOGIC_VECTOR(15 DOWNTO 0)
);
END component;

component ALUOP
	port (
		jumpInstruction : out integer := 0;
		start : in STD_LOGIC := '0';
		instReg_opc_31to26 : in STD_LOGIC_VECTOR(5 DOWNTO 0) := "000000";
		instReg_s_25to21 : in STD_LOGIC_VECTOR(4 DOWNTO 0) := "00000";
		instReg_t_16to20: in STD_LOGIC_VECTOR(4 DOWNTO 0) := "00100";
		instReg_i_0to15 : in STD_LOGIC_VECTOR(15 DOWNTO 0) := "0110000000100000";
		aluReady : out STD_Logic := '0';
		Clk: in std_logic
	);
END component;

signal clk : std_logic := '0';
signal instructionReady : std_logic := '0';
signal nextAddress : integer := 0;
signal PCIn : integer := 0;
signal instData : STD_LOGIC_VECTOR(register_size DOWNTO 0);
signal instReg_opc_31to26 : STD_LOGIC_VECTOR(5 DOWNTO 0);
signal instReg_s_25to21 : STD_LOGIC_VECTOR(4 DOWNTO 0);
signal instReg_t_16to20: STD_LOGIC_VECTOR(4 DOWNTO 0);
signal instReg_i_0to15 : STD_LOGIC_VECTOR(15 DOWNTO 0);
signal aluReady : std_logic := '0';
signal aluStart : std_logic := '0';
signal jumpInstruction : integer;
signal PCReady : STD_LOGIC;
--clk period for testing
constant clk_period : time := 100 ns;

begin

   -- Clock process definitions
   clk_process : process
   begin
		clk <= '0';
		wait for clk_period/2;
		clk <= '1';
		wait for clk_period/2;
   end process;

--process for testing PC
incrementPC : process (aluReady)
begin
if RISING_EDGE(aluReady) then
	PCIn <= PCIn + 4;
end if;
end process;


pc : programCounter
PORT MAP(
	clk=>clk,
	reset=>'0',
	writeEnable=>aluReady,
	PCReady=>PCReady,
	PCIn=>PCIn,
	PCOut=>nextAddress
);

alu : ALUOP
	port MAP(
		start=>instructionReady,
		instReg_opc_31to26=>instReg_opc_31to26,
		instReg_s_25to21=>instReg_s_25to21,
		instReg_t_16to20=>instReg_t_16to20,
		instReg_i_0to15=>instReg_i_0to15,
		aluReady=>aluReady,
		Clk=>clk
	);


instFetch : instructionFetch
PORT MAP(
	clk=>clk,
	nextAddress=>nextAddress,
	instruction=>instData,
	instReady=>instructionReady,
	fetchNext=>aluReady,
	instReg_opc_31to26=>instReg_opc_31to26,
	instReg_s_25to21=>instReg_s_25to21,
	instReg_t_16to20=>instReg_t_16to20,
	instReg_i_0to15=>instReg_i_0to15
);

END;