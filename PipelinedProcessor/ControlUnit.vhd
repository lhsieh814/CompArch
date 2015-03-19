library IEEE; use IEEE.STD_LOGIC_1164.all;
entity ControlUnit is
port(
	op: in STD_LOGIC_VECTOR(5 downto 0);
	funct: in STD_LOGIC_VECTOR(5 downto 0);
	memtoreg, memwrite: out STD_LOGIC;
	branch, alusrc: out STD_LOGIC;
	regdst, regwrite: out STD_LOGIC;
	jump: out STD_LOGIC;
	alucontrol: out STD_LOGIC_VECTOR(3 downto 0));
end;

architecture behavior of ControlUnit is
signal aluop : std_logic_vector(1 downto 0); 
component ALUDecoder is
port(
	funct: in STD_LOGIC_VECTOR(5 downto 0);
	aluop: in STD_LOGIC_VECTOR(1 downto 0);
	alucontrol: out STD_LOGIC_VECTOR(3 downto 0));
end component;

component Decoder is
port(
	op: in STD_LOGIC_VECTOR(5 downto 0);
	memtoreg, memwrite: out STD_LOGIC;
	branch, alusrc: out STD_LOGIC;
	regdst, regwrite: out STD_LOGIC;
	jump: out STD_LOGIC;
	aluop: out STD_LOGIC_VECTOR(1 downto 0));
end component;

begin

aluDec : ALUDecoder
PORT MAP(
	funct=>funct,
	aluop=>aluop,
	alucontrol=>alucontrol
);

dec : Decoder
PORT MAP(
	op=>op,
	memtoreg=>memtoreg,
	memwrite=>memwrite,
	branch=>branch, 
	alusrc=>alusrc,
	regdst=>regdst, 
	regwrite=>regwrite,
	jump=>jump,
	aluop=>aluop
);



end;