library ieee;
use ieee.std_logic_1164.ALL;
use work.MIPSCPU_constants.ALL;

Entity REG_MEMWB is
  port(
      clk         : in std_logic;

      regWriteM   : in std_logic;
      memToRegM   : in std_logic;
      readDataM   : in std_logic_vector(register_size downto 0);
      aluOutM     : in std_logic_vector(register_size downto 0);
      writeRegM   : in std_logic_vector(4 downto 0);

      regWriteW   : in std_logic;
      memToRegW   : in std_logic;
      readDataW   : in std_logic_vector(register_size downto 0);
      aluOutW     : in std_logic_vector(register_size downto 0);
      writeRegW   : in std_logic_vector(4 downto 0)
      );
  end REG_MEMWB;

  architecture REG_MEMWB_ARC of REG_MEMWB is
  begin
    process(clk)
    begin
        if( clk'event and clk = '1') then
          regWriteW <= regWriteM;
          memToRegW <= memToRegM;
          readDataW <= readDataM;
          aluOutW   <= aluOutM;
          writeRegW <= writeRegM;
        end if;
    end process;
  end REG_MEMWB_ARC;