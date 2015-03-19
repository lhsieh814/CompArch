library ieee;
use ieee.std_logic_1164.ALL;
use work.MIPSCPU_constants.ALL;

Entity Reg_IFID is
	port(
		clk				: in std_logic;
		instruction		: in std_logic_vector(W-1 downto 0);
		pcPlus4			: in std_logic_vector(W-1 downto 0);
		
		out_instrD		: out std_logic_vector(W-1 downto 0);
		out_pcPlus4		: out std_logic_vector(W-1 downto 0)
		);
	end Reg_IFID;
	
architecture Reg_IFID_ARC of Reg_IFID is 
	begin
		process(clk) 
			begin
				if(clk'event and clk = '1') then
					out_instrD <= instruction;
					out_pcPlus4 <= pcPlus4;
				end if;
		end process;
	end;