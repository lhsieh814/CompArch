library ieee;
use ieee.std_logic_1164.ALL;
use work.MIPSCPU_constants.ALL;
use ieee.numeric_std.ALL;

entity ALU is
port(

	ALU_CTRL : in std_logic_vector(4 downto 0);
	x, y : in std_logic_vector(register_size downto 0);
	output : out std_logic_vector(register_size downto 0)	
);
end ALU;

architecture BEHV of ALU is
begin
process(ALU_CTRL,x,y)
	variable varX : unsigned(register_size downto 0);
	variable varY : unsigned(register_size downto 0);
	variable varOutput : unsigned(register_size downto 0);
	begin
	varX := unsigned(x);
	varY := unsigned(y);
	varOutput := (OTHERS => '0');

	CASE ALU_CTRL IS
		WHEN "00000" =>
		varOutput := varX + varY;
		WHEN "00001" =>
		varOutput := varX - varY;
		WHEN "00010" =>
		varOutput := varX AND varY;
		WHEN "00011" =>
		varOutput := varX OR varY;
		WHEN "00100" =>
		varOutput := NOT (varX OR varY);
		WHEN "00101" =>
		varOutput := varX*varY;
		WHEN OTHERS => varOutput := (OTHERS => '1');
	END CASE;
	output<=std_logic_vector(varOutput);
end process;
end behv;