library ieee;
use ieee.std_logic_1164.ALL;
use work.MIPSCPU_constants.ALL;

Entity Reg_IFID is
	port(
		clk				: in std_logic;
		PCSrcD : in std_logic;
		instruction		: in std_logic_vector(register_size downto 0);
		pcPlus4			: in integer;
		out_instrD		: out std_logic_vector(register_size downto 0);
		out_pcPlus4		: out integer;
		StallD			:  in std_logic
		);
	end Reg_IFID;
	
architecture Reg_IFID_ARC of Reg_IFID is 
	begin
		process(clk) 
			begin
				if (PCSrcD='1') then
					out_instrD <= "00000000000000000000000000000000";
					out_pcPlus4 <= 0;
				else 
					if (clk'event and clk = '1' and stallD = '0') then
					out_instrD <= instruction;
					out_pcPlus4 <= pcPlus4;
					end if;
				end if;
		end process;
	end;