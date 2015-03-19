library ieee;
use ieee.std_logic_1164.all;

-- Implementation of multiplexer
Entity mux is 
	Generic(W : integer);
	Port(
		D0, D1	: in std_logic_vector(W-1 downto 0); -- two inputs
		S			: in std_logic;	-- seletion line
		Y 			: out std_logic_vector(W-1 downto 0);
	);
	End;

Architecture behave of mux is 
	begin
	y <= D0 when S = '0' else D1;
	end;