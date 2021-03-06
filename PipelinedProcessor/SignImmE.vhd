library ieee;
use ieee.std_logic_1164.all;
use work.MIPSCPU_constants.all;

Entity SignImmE is
	Port(
		A : in std_logic_vector(register_size downto 0);
		Y : out std_logic_vector(register_size downto 0)
	);
end SignImme;
Architecture behave of SignImmE is 
	begin
		Y <= A(28 downto 0) & "000";
	end;