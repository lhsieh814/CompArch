library ieee;
use ieee.std_logic_1164.ALL;
use ieee.numeric_std.ALL;
use work.MIPSCPU_constants.ALL;
  
entity REG_EXMEM is 
    port(
        clk        :   in STD_LOGIC;                    

        regWriteE  :   in STD_LOGIC;   
        memToRegE  :   in STD_LOGIC;                 
        memWriteE  :   in STD_LOGIC;                
        aluIn      :   in STD_LOGIC_VECTOR (register_size downto 0);                  
        writeDataE :   in STD_LOGIC_VECTOR (register_size downto 0); 
        writeRegE  :   in STD_LOGIC_VECTOR (4 downto 0); 
 
        regWriteM  :   out STD_LOGIC;   
        memToRegM  :   out STD_LOGIC;                 
        memWriteM  :   out STD_LOGIC;                
        aluOut     :   out STD_LOGIC_VECTOR (register_size downto 0);                  
        writeDataM :   out STD_LOGIC_VECTOR (register_size downto 0); 
        writeRegM  :   out STD_LOGIC_VECTOR (4 downto 0)
 
        );
end REG_EXMEM;
 
architecture REG_EXMEM_ARC of REG_EXMEM is        
begin 
    process(clk)
    begin
        if(clk'event and clk='1') then
            regWriteM  <= regWriteE;
            memToRegM  <= memToRegE;
            memWriteM  <= memWriteE;
            aluOut     <= aluIn;
            writeDataM <= writeDataE;
            writeRegM  <= writeRegE;
        end if;
    end process; 
end REG_EXMEM_ARC;