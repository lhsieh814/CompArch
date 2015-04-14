library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use work.MIPSCPU_constants.ALL;

Entity HazardUnit is
    Port(
        StallF  : out std_logic := '0';
        StallD  : out std_logic := '0';
        BranchD : in std_logic := '0';
        ForwardAD : out std_logic := '0';
        ForwardBD : out std_logic := '0';
        rsD : in std_logic_vector(4 downto 0);
        rtD : in std_logic_vector(4 downto 0);
        FlushE : out std_logic := '0';
        rsE : in std_logic_vector(4 downto 0);
        rtE : in std_logic_vector(4 downto 0);
        
        ForwardAE : out std_logic_vector(1 downto 0) := "00";
        ForwardBE : out std_logic_vector(1 downto 0) := "00";
        WriteRegE : in std_logic_vector(4 downto 0);
        MemToRegE : in std_logic;
        RegWriteE : in std_logic;

        WriteRegM : in std_logic_vector(4 downto 0);
        MemToRegM : in std_logic;
        RegWriteM : in std_logic;
        
        WriteRegW : in std_logic_vector(4 downto 0);
        RegWriteW : in std_logic
    );
End HazardUnit;

Architecture behave of HazardUnit is 
    signal lwstall : std_logic;
    signal branchstall : std_logic;
    signal boolForwardAE : std_logic;

    function To_Std_Logic(L: BOOLEAN) return std_logic is
    begin
        if L then
        return('1');
        else
        return('0');
        end if;
    end function To_Std_Logic;

    begin
        ForwardAE<="10" when (rsE /= "00000") AND (rsE = WriteRegM) AND (RegWriteM='1') else
                    "01" when  ((rsE /= "00000") AND (rsE = WriteRegW) AND (RegWriteW='1')) else
                    "00";
        ForwardBE<="10" when (rtE /= "00000") AND (rtE = WriteRegM) AND (RegWriteM='1') else
                    "01" when  ((rtE /= "00000") AND (rtE = WriteRegW) AND (RegWriteW='1')) else
                    "00";

        lwstall<=to_std_logic((rsD=rtE OR rtD=rtE) AND (MemtoRegE='1'));
        ForwardAD<=to_std_logic((rsD /= "00000") and (rsD = WriteRegM) and (RegWriteM='1'));
        ForwardBD<= to_std_logic((rtD /= "00000") AND (rtD = WriteRegM) AND (RegWriteM='1'));
        branchstall<=to_std_logic(((BranchD='1') AND (RegWriteE='1') AND ((WriteRegE = rsD) OR (WriteRegE = rtD))) OR ((BranchD='1') AND (MemtoRegM='1') AND ((WriteRegM = rsD) OR (WriteRegM = rtD))));
        StallF<= branchstall OR lwstall;
        StallD<= branchstall OR lwstall;
        FlushE<= branchstall OR lwstall;
    end;