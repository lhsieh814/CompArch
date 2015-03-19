library ieee;
use ieee.std_logic_1164.ALL;
use ieee.numeric_std.ALL;
use work.MIPSCPU_constants.ALL;
  
entity REG_EXMEM is 
    port(
        clk             :   in STD_LOGIC;                   
        reset           :   in STD_LOGIC;  

        regWriteE       :   in STD_LOGIC;   
        memToRegE       :   in STD_LOGIC;                 
        memWriteE       :   in STD_LOGIC;                
        aluIn           :   in STD_LOGIC_VECTOR (register_size downto 0);                  
        writeDataE      :   in STD_LOGIC_VECTOR (register_size downto 0); 
        writeRegE       :   in STD_LOGIC_VECTOR (4 downto 0); 
 
        regWriteM       :   in STD_LOGIC;   
        memToRegM       :   in STD_LOGIC;                 
        memWriteM       :   in STD_LOGIC;                
        aluOut          :   in STD_LOGIC_VECTOR (register_size downto 0);                  
        writeDataM      :   in STD_LOGIC_VECTOR (register_size downto 0); 
        writeRegM       :   in STD_LOGIC_VECTOR (4 downto 0)
 
        );
end REG_EXMEM;
 
architecture REG_EXMEM_ARC of REG_EXMEM is        
begin 
 
    SYNC_EX_MEM:
      process(clk, reset, regWriteE, memToRegE, memWriteE, aluIn, writeDataE, writeRegE)
      begin
        if reset = '1' then
            regWriteM  <= '0';
            memToRegM  <= '0';
            memWriteM  <= '0';
            aluOut     <= "00000000000000000000000000000000";
            writeDataM <= "00000000000000000000000000000000";
            writeRegM  <= "00000";
        elsif rising_edge(clk) then
            regWriteM  <= regWriteE;
            memToRegM  <= memToRegE;
            memWriteM  <= memWriteE;
            aluOut     <= aluIn;
            writeDataM <= writeDataE
            writeRegM  <= writeRegE;
        end if;
      end process; 
 
end REG_EXMEM_ARC;