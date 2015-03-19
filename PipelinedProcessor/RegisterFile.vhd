library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;

entity RegisterFile is
port(
    clk : in STD_LOGIC;
    reset: in STD_LOGIC;
    writeEnable: in STD_LOGIC;
    rs : in STD_LOGIC_VECTOR(4 downto 0);
    rt: in STD_LOGIC_VECTOR(4 downto 0);
    rd: in STD_LOGIC_VECTOR(4 downto 0);
    regWrite: in STD_LOGIC;
    writeData: in STD_LOGIC_VECTOR(31 downto 0);
    outA: out STD_LOGIC_VECTOR(31 downto 0);
    outB: out STD_LOGIC_VECTOR(31 downto 0)
);
end RegisterFile;

architecture behavior of RegisterFile is
begin
    process(clk, reset, regWrite, writeEnable, writeData, rd)
    type reg_array is array(0 to 31) of STD_LOGIC_VECTOR(31 downto 0);
    variable registers : reg_array;
    begin
        if reset='1' then
            for i in 0 to 31 loop
                registers(i) := (others => '0');
            end loop;
        elsif RISING_EDGE(clk) then
            if (regWrite='1') then
                registers(to_integer(unsigned(rs))) := writeData;
            end if;
        end if;

        if (rs="00000") then
            outA <= (others=>'0');
        else
            outA <= registers(to_integer(unsigned(rs)));
        end if;

        if (rt="00000") then
            outB <= (others=>'0');
        else
            outB <= registers(to_integer(unsigned(rt)));
        end if;
    end process;
end behavior;
