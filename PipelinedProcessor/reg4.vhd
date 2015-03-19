library ieee;
use ieee.std_logic_1164.all;

-- Pipelined register 3 - ID/EX

Entity reg4 is 
	Generic(W : integer);
	Port (
		-- input ports
		clk			: in std_logic;
		RegWriteM 	: in std_logic;
		MemtoRegM 	: in std_logic;
		ReadDataM	: in std_logic_vector(W-1 downto 0);
		AluOutM		: in std_logic_vector(W-1 downto 0);
		WriteRegM	: in std_logic_vector(4 downto 0);
		
		-- output ports
		out_RegWriteW 		: out std_logic;
		out_MemtoRegW	: out std_logic;
		out_ReadDataW		: out std_logic_vector(W-1 downto 0);
		out_AluOutW			: out std_logic_vector(W-1 downto 0);
		out_WriteRegW		: out std_logic_vector(4 downto 0);
	);
	End;
	
Architecture behave of reg4 is 
	begin 
		process(clk)
			begin 
				if(clk'event and clk = '1') then
				out_RegWriteW 	<= RegWriteM;
				out_MemtoRegW 	<= MemtoRegM;
				out_AluOutW 	<= AluOutM;
				out_ReadDataW  <= ReadDataM;
				out_WriteRegW 	<= WriteRegM;
		end process;
	end;