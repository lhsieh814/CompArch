
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
		Y : out std_logic_vector(register_size downto 0) := "00000000000000000000000000000000";
		HI : out std_logic_vector(register_size downto 0) := "00000000000000000000000000000000";
		LO : out std_logic_vector(register_size downto 0) := "00000000000000000000000000000000";
		alucontrol : in std_logic_vector(3 downto 0)
	);
END ALU;

ARCHITECTURE behavior of ALU IS
signal BPrime :std_logic_vector(register_size downto 0);
signal AandB : std_logic_vector(register_size downto 0);
signal AorB : std_logic_vector(register_size downto 0);
signal AplusB : std_logic_vector(register_size downto 0);
signal AminusB : std_logic_vector(register_size downto 0);
signal AsltB : std_logic_vector(register_size downto 0);
signal AmultB : std_logic_vector((register_size+1)*2-1 downto 0);
signal AdivB : std_logic_vector(register_size downto 0);
signal AmodB : std_logic_vector(register_size downto 0);
signal AxorB : std_logic_vector(register_size downto 0);
signal AsrlB : std_logic_vector(register_size downto 0);
signal AsllB : std_logic_vector(register_size downto 0);
--signal AsraB : std_logic_vector(register_size downto 0);
signal AnorB : std_logic_vector(register_size downto 0);

begin
with alucontrol(2) select BPrime <=
			not(B) when '1',
			B when others;

AandB<=A and BPrime;
AorB<=A or BPrime;
AplusB <= std_logic_vector(signed(A) + signed(B));
AminusB <= std_logic_vector(signed(A) - signed(B));
AsltB<="0000000000000000000000000000000"&AminusB(register_size);
AmultB<=std_logic_vector(signed(A)*signed(B));
AdivB<=std_logic_vector(signed(A)/signed(B));
AmodB<=std_logic_vector(signed(A) mod signed(B));
AxorB<=A xor B;
AsrlB<=std_logic_vector(signed(A) srl to_integer(unsigned(B)));
AsllB<=std_logic_vector(signed(A) sll to_integer(unsigned(B)));
--AsraB<=std_logic_vector(signed(A) sra 2));
AnorB<=A nor B;

with alucontrol(3 downto 0) select HI <=
			AmultB(63 downto 32) when "1000",
			AdivB when "1001",
			"00000000000000000000000000000000" when others;

with alucontrol(3 downto 0) select LO <=
			AmultB(31 downto 0) when "1000",
			AmodB when "1001",
			"00000000000000000000000000000000" when others;
			
with alucontrol(3 downto 0) select Y <=
			AandB when "0000",
			AorB when "0001",
			AplusB when "0010",
			not(B) when "0011", --not used
			AandB when "0100",
			AorB when "0101",
			AminusB when "0110",
			AsltB when "0111", --slt
			AmultB(31 downto 0) when "1000",
			AdivB when "1001",
			AxorB when "1010",
			AsrlB when "1011",
			AsllB when "1100",
			--AsraB when "1101",
			AnorB when "1110",
			AandB when others;

END behavior;
