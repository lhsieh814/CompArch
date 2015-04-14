
library ieee;
use ieee.std_logic_1164.all;
use work.MIPSCPU_constants.all;

-- Implementation of multiplexer
Entity mux_5bit is 
	Port(
		D0, D1	: in std_logic_vector(4 downto 0); -- two inputs
		S			: in std_logic;	-- seletion line
		Y 			: out std_logic_vector(4 downto 0)
	);
	End;

Architecture behave of mux_5bit is 
	begin
	y <= D0 when S = '0' else D1;
	end;