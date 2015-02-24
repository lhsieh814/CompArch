library ieee;
use ieee.std_logic_1164.ALL;
use work.MIPSCPU_constants.ALL;
use ieee.numeric_std.ALL;

entity ALU is
port(

	opcode : in std_logic_vector(5 downto 0);
	x, y : in std_logic_vector(register_size downto 0);
	output : out std_logic_vector(register_size downto 0)	
);
end ALU;

architecture BEHV of ALU is
begin
process(opcode,x,y)
	variable varX : unsigned(register_size downto 0);
	variable varY : unsigned(register_size downto 0);
	variable varOutput : unsigned(register_size downto 0);
	begin
	varX := unsigned(x);
	varY := unsigned(y);
	varOutput := (OTHERS => '0');

	CASE opcode IS
		WHEN "000000" =>
		varOutput := varX + varY;
		WHEN "100010" =>
		varOutput := varX - varY;
		WHEN "100100" =>
		varOutput := varX AND varY;
		WHEN "100101" =>
		varOutput := varX OR varY;
		WHEN OTHERS => varOutput := (OTHERS => '1');
	END CASE;
end process;
end behv;