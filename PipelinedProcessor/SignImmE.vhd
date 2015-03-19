library ieee;
use ieee.std_logic_1164.all;

Entity SignImmE is
	Port(
		A : in std_logic_vector(31 downto 0);
		Y : out std_logic_vector(31 downto 0);
	);
	
Architecture behave of SignImmE is 
	begin
		Y <= A(29 downto 0) & "000";
	end;