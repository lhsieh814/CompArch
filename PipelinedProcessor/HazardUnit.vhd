library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use work.MIPSCPU_constants.ALL;

Entity HazardUnit is
	Port(
		StallF	: out std_logic;
		StallD	: out std_logic;
		BranchD	: in std_logic;
		ForwardAD : out std_logic;
		ForwardBD : out std_logic;
		FlushE	: out std_logic;
		ForwardAE	: out std_logic_vector(1 downto 0);
		ForwardBE	: out std_logic_vector(1 downto 0);
		MemToRegE	: in std_logic;
		RegWriteE : in std_logic;
		MemToRegM	: in std_logic;
		RegWriteM : in std_logic;		
		RegWriteW : in std_logic
	);
	End;

Architecture behave of HazardUnit is 
	begin
	end;