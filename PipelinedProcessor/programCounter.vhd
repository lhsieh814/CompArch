library ieee;
use ieee.std_logic_1164.ALL;
use work.MIPSCPU_constants.ALL;
use ieee.numeric_std.ALL;

entity programCounter is
port(
	clk : IN STD_LOGIC;
	clk_en : IN std_Logic;
	reset : IN STD_LOGIC;
	writeEnable : IN STD_LOGIC; -- high signifies writing to pc is enabled
	PCReady : OUT STD_LOGIC := '0'; --high signifies PC is ready to be updated
	PCIn : IN integer := 0;
	PCOut : OUT integer := 0
);
end programCounter;

--program counter provides a buffer between next address and the instruction fetcher
architecture BEHV of programCounter is
signal writeEnable_delay : std_logic := '0';
begin
process(clk,reset)
	variable newPC : integer := 0;
	begin
		if reset = '1' then
			newPC := 0;
		elsif RISING_EDGE(clk) and clk_en='1' then
			writeEnable_delay<=writeEnable;
			if writeEnable = '1' and writeEnable_delay ='0' then
				newPC := PCIn + 4;
				PCReady<='1';
			else PCReady<='0';
			end if;
		end if;
		PCOut <= newPC;
	end process;
end behv;