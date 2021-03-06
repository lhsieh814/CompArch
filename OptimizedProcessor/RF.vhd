library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use work.MIPSCPU_constants.all;

-- implementation of register file

Entity RF is
	port(
		A1	: in std_logic_vector(4 downto 0);
		A2	: in std_logic_vector(4 downto 0);
		A3 	: in std_logic_vector(4 downto 0);
		WD3	: in std_logic_vector(register_size downto 0);
		clk	: in std_logic;
		We3	: in std_logic;
		RD1 	: out std_logic_vector(register_size downto 0) := "00000000000000000000000000000000";
		RD2	: out std_logic_vector(register_size downto 0) := "00000000000000000000000000000000"
	);
	End RF;
	
Architecture RTL of RF is

	type ffd_vector is array (register_size downto 0) of std_logic_vector(register_size downto 0);
	signal ffd : ffd_vector := ((others=> (others=>'0')));
	
	begin
		process (A1, A2, A3, clk)
		Variable Aux1 : integer := 0;
		Variable Aux2 : integer := 0;
		Variable Aux3 : integer := 0;
		
	begin
		Aux1 := to_integer(unsigned(A1));
		Aux2 := to_integer(unsigned(A2));
		Aux3 := to_integer(unsigned(A3));
				
		if A1'event then
			if Aux1 = 0 then
				  RD1 <= "00000000000000000000000000000000";
			else
				  RD1 <= ffd(Aux1);
			end if;
		end if;
		if A2'event then
			if Aux2 = 0 then 
				  RD2 <= "00000000000000000000000000000000";
			else
				  RD2 <= ffd(Aux2);
			end if;
		end if;
		if clk'event and clk = '1' then 
			if We3 = '1' then
				  ffd(Aux3) <= WD3;
			end if;
		end if;
		end process;
end RTL;	