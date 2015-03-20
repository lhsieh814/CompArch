LIBRARY ieee;
USE ieee.std_logic_1164.ALL;
use work.MIPSCPU_constants.all;

Entity Comparator is 
	
	Port (
		a, b 	: in std_logic_vector(register_size downto 0); -- inputs of two vectors
		output	: out std_logic
	);
End;

Architecture behav of Comparator is 
process (a,b)
    begin
        if a = b then
            output <= '1';
        else 
            outputS <= '0';
        end if;
    end process;
end architecture;