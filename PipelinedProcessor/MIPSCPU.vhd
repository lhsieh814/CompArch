LIBRARY ieee;
USE ieee.std_logic_1164.ALL;
use work.MIPSCPU_constants.ALL;

ENTITY MIPSCPU IS
END MIPSCPU;

ARCHITECTURE behavior OF MIPSCPU IS
component instructionFetch IS
PORT(
	clk : in std_logic;
	clk_en: in std_logic;
	PCF : in integer := 0; --address of next instruction register to be read
	InstrD : out std_logic_vector(register_size downto 0); --retrieved unformatted instruction
	InstrDReady : out std_logic := '0'; --signifies instruction is ready
	fetchNext : in std_logic := '0'; --next instruction is only fetched when set to high
	--instruction formatted with register partitions
	instReg_opc_31to26 : OUT STD_LOGIC_VECTOR(5 DOWNTO 0);
	instReg_s_25to21 : OUT STD_LOGIC_VECTOR(4 DOWNTO 0);
	instReg_t_16to20: OUT STD_LOGIC_VECTOR(4 DOWNTO 0);
	instReg_i_0to15 : OUT STD_LOGIC_VECTOR(15 DOWNTO 0)
);
END component;

Component programCounter is
port(
	clk : IN STD_LOGIC;
	clk_en : IN std_Logic;
	reset : IN STD_LOGIC;
	writeEnable : IN STD_LOGIC; -- high signifies writing to pc is enabled
	PCReady : OUT STD_LOGIC; --high signifies PC is ready to be updated
	PCIn : IN integer;
	PCOut : OUT integer
);
end component;

signal PCStartValue : integer := 0;
signal clk : std_logic := '0';
signal InstrDReady : std_logic := '0';
signal PCF : integer := 0;
signal PCIn : integer := 0;
signal InstrD : STD_LOGIC_VECTOR(register_size DOWNTO 0);
signal instReg_opc_31to26 : STD_LOGIC_VECTOR(5 DOWNTO 0);
signal instReg_s_25to21 : STD_LOGIC_VECTOR(4 DOWNTO 0);
signal instReg_t_16to20: STD_LOGIC_VECTOR(4 DOWNTO 0);
signal instReg_i_0to15 : STD_LOGIC_VECTOR(15 DOWNTO 0);
signal PCPrime : integer;
signal PCReady : STD_LOGIC;
signal PCSrcD : std_logic := '0';
begin

--PCSrcD_MUX: process(PCSrcD,PCPlus4F,PCBranchD)
--		begin
--			if( PCSrcD = '0') then
--				PCPrime <= PCPlus4F; 
--			else
--				PCPrime <= PCBranchD;
--			end if;
--		end process PCSrcD_MUX; 


PC : programCounter
PORT MAP(
	clk=>clk,
	clk_en=>'1',
	reset=>'0',
	writeEnable=>InstrDReady,
	PCReady=>PCReady,
	PCIn=>PCF,
	PCOut=>PCF
);

instFetch : instructionFetch
PORT MAP(
	clk=>clk,
	clk_en=>'1',
	PCF=>PCF,
	InstrD=>InstrD,
	InstrDReady=>InstrDReady,
	fetchNext=>'1',
	instReg_opc_31to26=>instReg_opc_31to26,
	instReg_s_25to21=>instReg_s_25to21,
	instReg_t_16to20=>instReg_t_16to20,
	instReg_i_0to15=>instReg_i_0to15
);

END;