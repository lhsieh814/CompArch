library ieee;
use ieee.std_logic_1164.all;

ENTITY SignExtend is
    port (
        INPUT: IN std_logic_vector(15 DOWNTO 0);
        OUTPUT: OUT std_logic_vector(31 DOWNTO 0));
END ENTITY SignExtend;

ARCHITECTURE behavior of SignExtend is
BEGIN
    OUTPUT(31 downto 16) <= x"0000";
    OUTPUT(15 downto 0) <= INPUT;
END ARCHITECTURE behavior;