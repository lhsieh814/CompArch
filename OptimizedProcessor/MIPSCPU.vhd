LIBRARY ieee;
USE ieee.std_logic_1164.ALL;
use work.MIPSCPU_constants.ALL;
use IEEE.numeric_std.all;
use STD.textio.all;
ENTITY MIPSCPU IS
END MIPSCPU;

ARCHITECTURE behavior OF MIPSCPU IS

--function to go from boolean to std_logic
function To_Std_Logic(L: BOOLEAN) return std_logic is
begin
    if L then
    	return('1');
    else
    	return('0');
    end if;
end function To_Std_Logic;

component REG_MEMWB is
  port(
      clk         : in std_logic;
      regWriteM   : in std_logic;
      memToRegM   : in std_logic;
      readDataM   : in std_logic_vector(register_size downto 0);
      aluOutM     : in std_logic_vector(register_size downto 0);
      writeRegM   : in std_logic_vector(4 downto 0);

      regWriteW   : out std_logic;
      memToRegW   : out std_logic;
      readDataW   : out std_logic_vector(register_size downto 0);
      aluOutW     : out std_logic_vector(register_size downto 0);
      writeRegW   : out std_logic_vector(4 downto 0)
      );
end component;

component REG_EXMEM is 
    port(
        clk        :   in STD_LOGIC;                    
        regWriteE  :   in STD_LOGIC;   
        memToRegE  :   in STD_LOGIC;                 
        memWriteE  :   in STD_LOGIC;                
        aluIn      :   in STD_LOGIC_VECTOR (register_size downto 0);                  
        writeDataE :   in STD_LOGIC_VECTOR (register_size downto 0); 
        writeRegE  :   in STD_LOGIC_VECTOR (4 downto 0); 
 
        regWriteM  :   out STD_LOGIC;   
        memToRegM  :   out STD_LOGIC;                 
        memWriteM  :   out STD_LOGIC;                
        aluOut     :   out STD_LOGIC_VECTOR (register_size downto 0);                  
        writeDataM :   out STD_LOGIC_VECTOR (register_size downto 0); 
        writeRegM  :   out STD_LOGIC_VECTOR (4 downto 0)
 
        );
end component;

component mux is 
	Port(
		D0, D1	: in std_logic_vector(register_size downto 0); -- two inputs
		S			: in std_logic;	-- seletion line
		Y 			: out std_logic_vector(register_size downto 0)
	);
end component;

component mux_5bit is 
	Port(
		D0, D1	: in std_logic_vector(4 downto 0); -- two inputs
		S			: in std_logic;	-- seletion line
		Y 			: out std_logic_vector(4 downto 0)
	);
end component;


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

component Mux_3 is
	port (ZERO: in std_logic_vector(31 downto 0);
		  ONE: in std_logic_vector(31 downto 0);
		  TWO: in std_logic_vector(31 downto 0);
		  CTRL: in std_logic_vector(1 downto 0);
		  OUTPUT: out std_logic_vector(31 downto 0));
end component;

component reg_idex is
    port(
        clk : in STD_LOGIC;
        reset : in STD_LOGIC;
        regWriteD : in STD_LOGIC;
        memToRegD : in STD_LOGIC;
        memWriteD : in STD_LOGIC;
        aluControlD : in STD_LOGIC_VECTOR(3 downto 0);
        aluSrcD : in STD_LOGIC;
        regDstD : in STD_LOGIC;
        rd1d : in STD_LOGIC_VECTOR(register_size downto 0);
        rd2d : in STD_LOGIC_VECTOR(register_size downto 0);
        rsD : in STD_LOGIC_VECTOR(4 downto 0);
        rtD : in STD_LOGIC_VECTOR(4 downto 0);
        rdD : in STD_LOGIC_VECTOR(4 downto 0);
        signImmD : in STD_LOGIC_VECTOR(register_size downto 0);
 		rd1e : out STD_LOGIC_VECTOR(register_size downto 0);
        rd2e : out STD_LOGIC_VECTOR(register_size downto 0);
        regWriteE : out STD_LOGIC;
        memToRegE : out STD_LOGIC;
        memWriteE : out STD_LOGIC;
        aluControlE : out STD_LOGIC_VECTOR(3 downto 0);
        aluSrcE : out STD_LOGIC;
        regDstE : out STD_LOGIC;
        rsE : out STD_LOGIC_VECTOR(4 downto 0);
        rtE : out STD_LOGIC_VECTOR(4 downto 0);
        rdE : out STD_LOGIC_VECTOR(4 downto 0);
        signImmE : out STD_LOGIC_VECTOR(register_size downto 0);
		FlushE : in std_logic
    );
end component;

component SignExtension is
	Port(
		A	: in std_logic_vector(15 downto 0);
		Y	: out std_logic_vector(register_size downto 0)
	);
end component;

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
end component;

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
    port(
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
end component;

component Reg_IFID is
	port(
		clk			: in std_logic;
		PCSrcD			: in std_logic;
		instruction		: in std_logic_vector(register_size downto 0);
		pcPlus4			: in integer;
		out_instrD		: out std_logic_vector(register_size downto 0);
		out_pcPlus4		: out integer;
		StallD			:  in std_logic
		);
end component;

--Declaration of signals
signal clk : std_logic := '0';
signal InstructionReady : std_logic := '0';--
signal PCF : integer := 0;--
signal PCIn : integer := 0;--
signal Instruction : STD_LOGIC_VECTOR(register_size DOWNTO 0);--
signal InstrD : STD_LOGIC_VECTOR(register_size DOWNTO 0);--
signal PCPrime : integer:= 0;--
signal PCPlus4f : integer:= 0;----
signal PCplus4d : integer:= 0;----
signal PCReady : STD_LOGIC:= '0';----
signal StallF : std_logic:= '0';
signal StallD  : std_logic:= '0';
signal BranchD : std_logic:= '0';
signal ForwardAD : std_logic:= '0';
signal ForwardBD : std_logic:= '0';
signal rsD : std_logic_vector(4 downto 0):= "00000";
signal rtD : std_logic_vector(4 downto 0):= "00000";
signal rdD : std_logic_vector(4 downto 0):= "00000";
signal FlushE :  std_logic:= '0';
signal rsE : std_logic_vector(4 downto 0):= "00000";
signal rtE : std_logic_vector(4 downto 0):= "00000";
signal rdE : std_logic_vector(4 downto 0):= "00000";
signal ForwardAE : std_logic_vector(1 downto 0):= "00";
signal ForwardBE : std_logic_vector(1 downto 0):= "00";
signal WriteRegE : std_logic_vector(4 downto 0):= "00000";
signal MemToRegE : std_logic:= '0';
signal RegWriteE : std_logic:= '0';

signal WriteRegM : std_logic_vector(4 downto 0):= "00000";
signal MemToRegM : std_logic:= '0';
signal RegWriteM : std_logic:= '0';
signal MemWriteM : std_logic:= '0';
signal writeDataM : std_logic_vector(register_size downto 0):="00000000000000000000000000000000";--  
signal WriteRegW : std_logic_vector(4 downto 0):="00000";
signal RegWriteW : std_logic:='0';--

signal memoryReady : std_logic:='0';--
signal fetchNextMemory : std_logic:='0';
signal readOrWrite : std_logic:='0';
signal dataToWrite : std_logic_vector(register_size downto 0):="00000000000000000000000000000000";-- 

signal SignImmDSLL2 : std_logic_vector(register_size downto 0):="00000000000000000000000000000000";-- 
signal PCBranchD : integer:= 0;
signal PCSrcD: STD_LOGIC:='0';
signal RegWriteD: STD_LOGIC:='0';
signal MemToRegD: STD_LOGIC:='0';
signal MemWriteD:  STD_LOGIC:='0';
signal alucontrolD:  STD_LOGIC_VECTOR(3 downto 0):="0000";
signal alusrcD:  STD_LOGIC:='0';
signal regdstD:  STD_LOGIC:='0';
signal jumpD:  STD_LOGIC:='0';
signal SignImmD : std_logic_vector(register_size downto 0):="00000000000000000000000000000000";-- 
signal RD1d : std_logic_vector(register_size downto 0):="00000000000000000000000000000000";-- 
signal RD2d : std_logic_vector(register_size downto 0):="00000000000000000000000000000000";-- 
signal EqualD : std_logic:='0';--
signal MuxAOut : std_logic_vector(register_size downto 0):="00000000000000000000000000000000";-- 
signal MuxBOut : std_logic_vector(register_size downto 0):="00000000000000000000000000000000";-- 
signal ReadDataM : std_logic_vector(register_size downto 0):="00000000000000000000000000000000";-- 
signal MemToRegW : std_logic:='0';
signal ReadDataW : std_logic_vector(register_size downto 0):="00000000000000000000000000000000";-- 
signal aluOutW : std_logic_Vector(register_size downto 0):="00000000000000000000000000000000";-- 

signal MemWriteE :std_logic:='0';
signal aluControlE:std_logic_vector(3 downto 0):="0000";--
signal aluSrcE: std_logic:='0';
signal regDstE :std_logic:='0';
signal signImmE :std_logic_vector(register_size downto 0):="00000000000000000000000000000000";-- 
signal SrcAE : STD_LOGIC_VECTOR(register_size downto 0):="00000000000000000000000000000000";-- 
signal SrcBE : STD_LOGIC_VECTOR(register_size downto 0):="00000000000000000000000000000000";-- 
signal WriteDataE : STD_LOGIC_VECTOR(register_size downto 0):="00000000000000000000000000000000";-- 
signal RD1e : std_logic_vector(register_size downto 0):="00000000000000000000000000000000";-- 
signal RD2e : std_logic_vector(register_size downto 0):="00000000000000000000000000000000";-- 
signal ALUOutE : std_logic_vector(register_size downto 0):="00000000000000000000000000000000";-- 
signal ALUOutM : STD_LOGIC_VECTOR(register_size downto 0):="00000000000000000000000000000000";--
signal resultW : STD_LOGIC_vector(register_size downto 0):="00000000000000000000000000000000";-- 
signal nextAddress : integer:=0;
signal ALUOutMInt : integer:=0;

begin

nextAddress<=to_integer(unsigned(writedatam));
PCPlus4f<=PCF+4;
rsD<=instrD(25 downto 21);
rtD<=instrD(20 downto 16);
rdD<=instrD(15 downto 11);
SignImmDSLL2<=SignImmD(register_size-2 downto 0)&"00";
EqualD<=to_std_logic(MuxAOut=MuxBOut);
PCSrcD<=(BranchD AND EqualD);
PCBranchD<=to_integer(signed(SignImmDSLL2))+PCplus4D;
ALUOutMInt<=to_integer(unsigned(ALUOutM));

PCPrime<=PCPlus4F when PCSrcD='0' else
		PCBranchD when PCSrcD='1' else
		PCPlus4F;

WriteRegE_Mux : Mux_5bit
	Port map(
		D0=>rtE,
		D1=>rdE,		
		S=>RegDstE,
		Y=>WriteRegE
	);

muxA : Mux
	Port map(
		D0=>Rd1d,
		D1=>AluOutM,		
		S=>ForwardAD,
		Y=>MuxAOut
	);

muxB : Mux
	Port map(
		D0=>Rd2d,
		D1=>AluOutM,		
		S=>ForwardBD,
		Y=>MuxBOut
	);

muxSrcBE : Mux
	Port map(
		D0=>WriteDataE,
		D1=>SignImmE,		
		S=>AluSRCE,
		Y=>SrcBE
	);

muxResultW : Mux
	Port map(
		D0=>ALUOutW,
		D1=>ReadDAtaW,		
		S=>MemToRegW,
		Y=>ResultW
	);

registerFile : RF
	port map(
		A1=>InstrD(25 downto 21),
		A2=>InstrD(20 downto 16),
		A3=>WriteRegW,
		WD3=>ResultW,
		clk=>clk,
		We3=>RegWriteW,
		RD1=>rd1d,
		RD2=>RD2d
	);

signExtend : SignExtension
	Port map(
		A=>Instrd(15 downto 0),
		Y=>SignImmD
	);

PC : programCounter
PORT MAP(
	clk=>clk,
	clk_en=>'1',
	reset=>'0',
	writeEnable=>InstructionReady,
	PCReady=>PCReady,
	PCIn=>PCPrime,
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
		PCSrcD=>PCSrcD,
		instruction=>Instruction,
		pcPlus4=>pcPlus4f,
		out_instrD=>instrd,
		out_pcPlus4=>pcplus4d,
		stallD=>stallD
		);

dataMem: DataMemory 
PORT MAP(
	clk=>clk,
	nextAddress=>ALUOutMInt,
	instReady=>memoryReady,
	fetchNext=>fetchNextMemory,
	readOrWrite=>memWritem,
	dataToWrite=>writeDataM,
	output=>ReadDataM
);

mux3A : Mux_3
	port map(ZERO=>rd1e,
		  ONE=>resultW,
		  TWO=>ALUOutM,
		  CTRL=>ForwardAE,
		  OUTPUT=>SrcAE);

mux3B : Mux_3
	port map(ZERO=>RD2e,
		  ONE=>resultW,
		  TWO=>ALUOutM,
		  CTRL=>ForwardBE,
		  OUTPUT=>WriteDataE);




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

exmem :reg_exmem
    port map(
        clk=>clk,             
        regWriteE=>regWriteE,   
        memToRegE=>memToRegE,              
        memWriteE =>memWriteE,
        aluIn=>aluOutE,
        writeDataE=>writeDataE,
        writeRegE=>WriteRegE,
        regWriteM => regWriteM,  
        memToRegM=>memToRegM,              
        memWriteM=>memWriteM,              
        aluOut=>aluoutm,                  
        writeDataM=>writeDataM,
        writeRegM =>writeRegM
 
        );

idex : reg_idex
    port map(
        clk=>clk,
        reset=>'0',
        regWriteD=>regWriteD,
        memToRegD=>memToRegD,
        memWriteD=>memWriteD,
        aluControlD=>aluControlD,
        aluSrcD=>aluSrcD,
        regDstD=>regDstD,
        rd1d=>rd1d,
        rd2d=>rd2d,
        rsD=>rsd,
        rtD=>rtd,
        rdD=>rdd,
        signImmD=>signImmD,

		rd1e=>rd1e,
        rd2e=>rd2e,
        regWriteE=>regwriteE,
        memToRegE=>MemToRegE,
        memWriteE=>memWriteE,
        aluControlE=>aluControlE,
        aluSrcE=>aluSrcE,
        regDstE=>regDstE,
        rsE=>rsE,
        rtE=>rtE,
        rdE=>rdE,
        signImmE=>signImme,
		FlushE=>FlushE
    );

alu1 : alu
	port map (	
		A=>SrcAE,
		B=>SrcBE,
		Y=>AluOUTE,
		alucontrol=>alucontrolE
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


memwb: REG_MEMWB 
  port map(
      clk=>clk,
      regWriteM=>regWriteM,
      memToRegM=>memToRegM,
      readDataM=>readDataM,
      aluOutM=>aluOutM,
      writeRegM=>writeRegM,

      regWriteW=>regWriteW,
      memToRegW=>memToRegW,
      readDataW=>readDataW,
      aluOutW=>aluOutW,
      writeRegW=>writeRegW
  );

END;