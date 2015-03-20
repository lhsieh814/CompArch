library ieee;
use ieee.std_logic_1164.all;

Entity AndGate is

	Port(
		input1, input2 	: in std_logic;
		output				: out std_logic
	);
	end AndGate;
	
Architecture behav of AndGate is
	begin
		output <= input1 and input2;
end behav;