LIBRARY ieee;
USE ieee.std_logic_1164.ALL;
use work.MIPSCPU_constants.ALL;

ENTITY MIPSCPU IS
END MIPSCPU;

ARCHITECTURE behavior OF MIPSCPU IS
component DataMemory IS
PORT(
	clk : in std_logic;
	nextAddress : in integer := 0;
	instReady : out std_logic := '0';
	fetchNext : in std_logic := '0';
	readOrWrite : in std_logic := '0';
	dataToWrite : in std_logic_vector(register_size downto 0);
	output : OUT STD_LOGIC_VECTOR(register_size DOWNTO 0)
);
END component;

component RF is
	port(
		A1	: in std_logic_vector(4 downto 0);
		A2	: in std_logic_vector(4 downto 0);
		A3 	: in std_logic_vector(4 downto 0);
		WD3	: in std_logic_vector(register_size downto 0);
		clk	: in std_logic;
		We3	: in std_logic;
		RD1 : out std_logic_vector(register_size downto 0);
		RD2	: out std_logic_vector(register_size downto 0)
	);
	End component;

component instructionMemory IS
PORT(
	clk : in std_logic;
	clk_en: in std_logic;
	PCF : in integer := 0; --address of next instruction register to be read
	Instruction : out std_logic_vector(register_size downto 0); --retrieved unformatted instruction
	InstructionReady : out std_logic := '0'; --signifies instruction is ready
	fetchNext : in std_logic := '0' --next instruction is only fetched when set to high
	--instruction formatted with register partitions
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

component ALU IS
	port (	
		A : in std_logic_vector(register_size downto 0);
		B : in std_logic_vector(register_size downto 0);
		Y : out std_logic_vector(register_size downto 0);
		HI : out std_logic_vector(register_size downto 0);
		LO : out std_logic_vector(register_size downto 0);
		alucontrol : in std_logic_vector(3 downto 0)
	);
END component;

component ControlUnit is
port(
	op: in STD_LOGIC_VECTOR(5 downto 0);
	funct: in STD_LOGIC_VECTOR(5 downto 0);
	regwrite: out STD_LOGIC; 
	memtoreg: out STD_LOGIC;
	memwrite: out STD_LOGIC;
	alucontrol: out STD_LOGIC_VECTOR(3 downto 0);
	alusrc: out STD_LOGIC;
	regdst: out STD_LOGIC;
	branch: out STD_LOGIC;
	jump: out STD_LOGIC
);
end component;

component HazardUnit is
    Port(
        StallF  : out std_logic;
        StallD  : out std_logic;
        BranchD : in std_logic;
        ForwardAD : out std_logic;
        ForwardBD : out std_logic;
        rsD : in std_logic_vector(4 downto 0);
        rtD : in std_logic_vector(4 downto 0);
        FlushE : out std_logic;
        rsE : in std_logic_vector(4 downto 0);
        rtE : in std_logic_vector(4 downto 0);
        
        ForwardAE : out std_logic_vector(1 downto 0);
        ForwardBE : out std_logic_vector(1 downto 0);
        WriteRegE : in std_logic_vector(4 downto 0);
        MemToRegE : in std_logic;
        RegWriteE : in std_logic;

        WriteRegM : in std_logic_vector(4 downto 0);
        MemToRegM : in std_logic;
        RegWriteM : in std_logic;
        
        WriteRegW : in std_logic_vector(4 downto 0);
        RegWriteW : in std_logic
    );
End component;

component Reg_IFID is
	port(
		clk			: in std_logic;
		instruction		: in std_logic_vector(register_size downto 0);
		pcPlus4			: in integer;
		out_instrD		: out std_logic_vector(register_size downto 0);
		out_pcPlus4		: out integer;
		StallD			:  in std_logic
		);
	end component;

		signal clk : std_logic := '0';
		signal InstructionReady : std_logic := '0';
		signal PCF : integer := 0;
		signal PCIn : integer := 0;
		signal Instruction : STD_LOGIC_VECTOR(register_size DOWNTO 0);
		signal InstrD : STD_LOGIC_VECTOR(register_size DOWNTO 0);
		signal PCPrime : integer;
		signal PCPlus4f : integer;
		signal PCplus4d : integer;
		signal PCReady : STD_LOGIC;
		signal PCSrcD : std_logic := '0';

	signal StallF : std_logic;
        signal StallD  : std_logic;
        signal BranchD : std_logic;
        signal ForwardAD : std_logic;
        signal ForwardBD : std_logic;
        signal rsD : std_logic_vector(4 downto 0);
        signal rtD : std_logic_vector(4 downto 0);
        signal FlushE :  std_logic;
        signal rsE : std_logic_vector(4 downto 0);
        signal rtE : std_logic_vector(4 downto 0);
        
        signal ForwardAE : std_logic_vector(1 downto 0);
        signal ForwardBE : std_logic_vector(1 downto 0);
        signal WriteRegE : std_logic_vector(4 downto 0);
        signal MemToRegE : std_logic;
        signal RegWriteE : std_logic;

        signal WriteRegM : std_logic_vector(4 downto 0);
        signal MemToRegM : std_logic;
        signal RegWriteM : std_logic;
        
        signal WriteRegW : std_logic_vector(4 downto 0);
        signal RegWriteW : std_logic;

		signal memoryIn : integer;
		signal memoryReady : std_logic;
		signal fetchNextMemory : std_logic;
		signal readOrWrite : std_logic;
		signal dataToWrite : std_logic_vector(register_size downto 0);
		signal memoryOutput : std_logic_vector(register_size downto 0);

		signal regwriteD: STD_LOGIC; 
		signal memtoregD: STD_LOGIC;
		signal memwriteD:  STD_LOGIC;
		signal alucontrolD:  STD_LOGIC_VECTOR(3 downto 0);
		signal alusrcD:  STD_LOGIC;
		signal regdstD:  STD_LOGIC;
		signal jumpD:  STD_LOGIC;
		
		signal RD1 : std_logic_vector(register_size downto 0);
		signal RD2 : std_logic_vector(register_size downto 0);
		signal resultW : STD_LOGIC_vector(register_size downto 0);

begin
PCPlus4f<=PCF+4;

--PCSrcD_MUX: process(PCSrcD,PCPlus4F,PCBranchD)
--		begin
--			if( PCSrcD = '0') then
--				PCPrime <= PCPlus4F; 
--			else
--				PCPrime <= PCBranchD;
--			end if;
--		end process PCSrcD_MUX; 


registerFile : RF
	port map(
		A1=>InstrD(25 downto 21),
		A2=>InstrD(20 downto 16),
		A3=>WriteRegW,
		WD3=>ResultW,
		clk=>clk,
		We3=>RegWriteW,
		RD1=>RD1,
		RD2=>RD2
	);


PC : programCounter
PORT MAP(
	clk=>clk,
	clk_en=>'1',
	reset=>'0',
	writeEnable=>InstructionReady,
	PCReady=>PCReady,
	PCIn=>PCF,
	PCOut=>PCF
);

instFetch : instructionMemory
PORT MAP(
	clk=>clk,
	clk_en=>'1',
	PCF=>PCF,
	Instruction=>Instruction,
	InstructionReady=>InstructionReady,
	fetchNext=>'1'
);

ifid : reg_ifid
	port map(
		clk=>clk,
		instruction=>Instruction,
		pcPlus4=>pcPlus4f,
		out_instrD=>instrd,
		out_pcPlus4=>pcplus4d,
		stallD=>stalld
		);

dataMem: DataMemory 
PORT MAP(
	clk=>clk,
	nextAddress=>memoryIn,
	instReady=>memoryReady,
	fetchNext=>fetchNextMemory,
	readOrWrite=>readOrWrite,
	dataToWrite=>dataToWrite,
	output=>memoryOutput
);

controlU : ControlUnit 
port map(
	op=>instrD(31 downto 26),
	funct=>instrD(5 downto 0),
	regwrite=>regwrited,
	memtoreg=>memtoregD,
	memwrite=>memwrited,
	alucontrol=>alucontrolD,
	alusrc=>alusrcd,
	regdst=>regdstd,
	branch=>branchd,
	jump=>jumpd
);

hazardU : HazardUnit
    Port map(
        StallF=>StallF,
        StallD=>StallD,
        BranchD=>BranchD,
        ForwardAD=>ForwardAD,
        ForwardBD=>ForwardBD,
        rsD=>rsD,
        rtD=>rtD,
        FlushE=>FlushE,
        rsE=>rsE,
        rtE=>rtE,
        ForwardAE=>ForwardAE,
        ForwardBE=>ForwardBE,
        WriteRegE=>WriteRegE,
        MemToRegE=>MemToRegE,
        RegWriteE=>RegWriteE,

        WriteRegM=>WriteRegM,
        MemToRegM=>MemToRegM,
        RegWriteM=>RegWriteM,
        
        WriteRegW=>WriteRegW,
        RegWriteW=>RegWriteW
    );


END;