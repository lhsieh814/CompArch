
LIBRARY ieee;
USE ieee.std_logic_1164.ALL;
use work.MIPSCPU_constants.ALL;
use IEEE.numeric_std.all;
use STD.textio.all;

--This entity takes as input the instruction from the register and manipulates memory accordingly.
ENTITY ALU IS
	port (	
		A : in std_logic_vector(register_size downto 0);
		B : in std_logic_vector(register_size downto 0);
		Y : out std_logic_vector(register_size downto 0);
		alucontrol : in std_logic_vector(2 downto 0)
	);
END ALU;

ARCHITECTURE behavior of ALU IS
signal BPrime :std_logic_vector(register_size downto 0);
signal AandB : std_logic_vector(register_size downto 0);
signal AorB : std_logic_vector(register_size downto 0);
signal AplusB : std_logic_vector(register_size downto 0);
signal AminusB : std_logic_vector(register_size downto 0);
signal AsltB : std_logic_vector(register_size downto 0);

component adder is
port(a, b: in STD_LOGIC_VECTOR(register_size downto 0);
s: out STD_LOGIC_VECTOR(register_size downto 0);
cout: out STD_LOGIC);
end component;

begin
with alucontrol(2) select BPrime <=
			not(B) when '1',
			B when others;

AandB<=A and BPrime;
AorB<=A or BPrime;
AplusB <= std_logic_vector(signed(A) + signed(B));
AminusB <= std_logic_vector(signed(A) - signed(B));
AsltB<="0000000000000000000000000000000"&AminusB(register_size);

with alucontrol(2 downto 0) select Y <=
			AandB when "000",
			AorB when "001",
			AplusB when "010",
			not(B) when "011", --not used
			AandB when "100",
			AorB when "101",
			AminusB when "110",
			AsltB when "111", --slt
			AandB when others;

END behavior;
