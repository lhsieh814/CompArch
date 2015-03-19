library ieee;
use ieee.std_logic_1164.ALL;
use ieee.numeric_std.ALL;
use work.MIPSCPU_constants.ALL;

entity reg_idex is
    port(
        clk : in STD_LOGIC;
        reset : in STD_LOGIC;
        regWriteD : in STD_LOGIC;
        memToRegD : in STD_LOGIC;
        memWriteD : in STD_LOGIC;
        aluControlD : in STD_LOGIC_VECTOR(2 downto 0);
        aluSrcD : in STD_LOGIC;
        regDstD : in STD_LOGIC;
        rd1 : in STD_LOGIC_VECTOR(register_size downto 0);
        rd2 : in STD_LOGIC_VECTOR(register_size downto 0);
        rsD : in STD_LOGIC_VECTOR(4 downto 0);
        rtD : in STD_LOGIC_VECTOR(4 downto 0);
        rdD : in STD_LOGIC_VECTOR(4 downto 0);
        signImmD : in STD_LOGIC_VECTOR(15 downto 0);

        regWriteE : out STD_LOGIC;
        memToRegE : out STD_LOGIC;
        memWriteE : out STD_LOGIC;
        aluControlE : out STD_LOGIC_VECTOR(2 downto 0);
        aluSrcE : out STD_LOGIC;
        regDstE : out STD_LOGIC;
        rsE : out STD_LOGIC_VECTOR(4 downto 0);
        rtE : out STD_LOGIC_VECTOR(4 downto 0);
        rdE : out STD_LOGIC_VECTOR(4 downto 0);
        signImmE : out STD_LOGIC_VECTOR(15 downto 0)
    );
end reg_idex;

architecture behavior of reg_idex is
begin
    process(clk, reset, rsD, rtD, rdD, regWriteD, memToRegD, memWriteD)
    begin
        if reset='1' then
            rsE <= (others => '0');
            rtE <= (others => '0');
            rdE <= (others => '0');
            regWriteE <= '0';
            memToRegE <= '0';
            memWriteE <= '0';
        elsif RISING_EDGE(clk) then
            rsE <= rsD;
            rtE <= rtD;
            rdE <= rdD;
            regWriteE <= regWriteD;
            memToRegE <= memToRegD;
            memWriteE <= memWriteD;
        end if;
    end process;
end behavior;
