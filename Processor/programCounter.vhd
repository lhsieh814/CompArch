library ieee;
use ieee.std_logic_1164.ALL;
use work.MIPSCPU_constants.ALL;
use ieee.numeric_std.ALL;

entity programCounter is
port(
	clk : IN STD_LOGIC;
	reset : IN STD_LOGIC;
	writeEnable : IN STD_LOGIC;
	PCIn : IN integer := 0;
	PCOut : OUT integer := 0
);
end programCounter;

architecture BEHV of programCounter is
begin
	process(clk,reset)
	variable currentPC : integer := 0;
	begin
		if reset = '1' then
			currentPC := 0;
		elsif RISING_EDGE(clk) then 
			if writeEnable = '1' then
				currentPC := PCIn;
			end if;
		end if;
		PCOut <= currentPC;
	end process;
end behv;