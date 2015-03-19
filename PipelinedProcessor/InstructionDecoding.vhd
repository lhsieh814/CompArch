library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;

entity InstructionDecoding is
port(
    clk : in STD_LOGIC;
    reset : in STD_LOGIC;
    instrD : in STD_LOGIC_VECTOR(31 downto 0);
    pcPlus4 : in STD_LOGIC_VECTOR(31 downto 0);
    aluOutM : in STD_LOGIC_VECTOR(31 downto 0);
    forwardAD : in STD_LOGIC_VECTOR(5 downto 0);
    forwardBD : in STD_LOGIC_VECTOR(5 downto 0);

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
end InstructionDecoding;

architecture behavior of InstructionDecoding is
begin
    --Declare components
    component ControlUnit is 
        port(
            op: in STD_LOGIC_VECTOR(5 downto 0);
            funct: in STD_LOGIC_VECTOR(5 downto 0);
            regwrite: out STD_LOGIC; 
            memtoreg: out STD_LOGIC;
            memwrite: out STD_LOGIC;
            alucontrol: out STD_LOGIC_VECTOR(3 downto 0);
            alusrc: out STD_LOGIC;
            regdst: out STD_LOGIC;
            branch: out STD_LOGIC;
            jump: out STD_LOGIC
        );
    end component ControlUnit;

    component RegisterFile is
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
    end component RegisterFile;

    component reg_idex is
        port(
            clk : in STD_LOGIC;
            reset : in STD_LOGIC;
            regWriteD : in STD_LOGIC;
            memToRegD : in STD_LOGIC;
            memWriteD : in STD_LOGIC;
            aluControlD : in STD_LOGIC_VECTOR(2 downto 0);
            aluSrcD : in STD_LOGIC;
            regDstD : in STD_LOGIC;
            rd1 : in STD_LOGIC_VECTOR(31 downto 0);
            rd2 : in STD_LOGIC_VECTOR(31 downto 0);
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
    end component reg_idex;

    component SignExtension is
        port(
            A   : in std_logic_vector(15 downto 0);
            Y   : out std_logic_vector(31 downto 0)
        );
    end component SignExtension;

    component Mux2_32 is
        port (
            zero: in std_logic_vector(31 downto 0);
            one: in std_logic_vector(31 downto 0);
            ctrl: in std_logic_vector(5 downto 0);
            output: out std_logic_vector(31 downto 0)
        );
    end component Mux2_32;

    component adder is
        port(
            A : in std_logic_vector(31 downto 0);
            B : in std_logic_vector(31 downto 0);
            Y : out std_logic_vector(31 downto 0)
        );
    end component adder;

    signal clk : STD_LOGIC := '0';
    signal reset : STD_LOGIC := '0';
    signal instrD : STD_LOGIC_VECTOR(register_size downto 0);
    signal signImmD : STD_LOGIC_VECTOR(15 downto 0);
    signal rd1 : STD_LOGIC_VECTOR(register_size downto 0);
    signal rd2 : STD_LOGIC_VECTOR(register_size downto 0);
    signal regWriteD : STD_LOGIC;
    signal memToRegD : STD_LOGIC;
    signal memWriteD : STD_LOGIC;
    signal aluControlD : STD_LOGIC_VECTOR(3 downto 0);
    signal aluSrcD : STD_LOGIC;
    signal regDstD : STD_LOGIC;

    begin
        --Port maps
        CONTROL_UNIT: ControlUnit 
        port map(
            op => instrD(31 downto 26),
            funct => instrD(5 downto 0),
            regwrite => regWriteD,
            memtoreg => memToRegD,
            memwrite => memWriteD,
            alucontrol => aluControlD,
            alusrc => aluSrcD,
            regdst => regDstD,
            branch
            jump
        );

        REGISTER_FILE: RegisterFile 
        port map(
            clk => clk,
            reset => reset,
            writeEnable: 
            rs => instrD(25 downto 21),
            rt => instrD(20 downto 16),
            rd: 
            regWrite: 
            writeData: 
            outA => rd1,
            outB => rd2
        );

        REG_IDEX: reg_idex
        port map(
            clk => clk,
            reset => reset,
            regWriteD => regWriteD,
            memToRegD => memToRegD,
            memWriteD => memWriteD,
            aluControlD => aluControlD,
            aluSrcD => aluSrcD,
            regDstD => regDstD,
            rd1 : 
            rd2 : 
            rsD => instrD(25 downto 21),
            rtD => instrD(20 downto 16),
            rdD => instrD(15 downto 11),
            signImmD => signImmD,

            regWriteE => regWriteE,
            memToRegE => memToRegE,
            memWriteE => memWriteE,
            aluControlE => aluControlE,
            aluSrcE => aluSrcE,
            regDstE => regDstE,
            rsE => rsE,
            rtE => rtE,
            rdE => rdE,
            signImmE => signImmE
        );

        SIGN_EXTENSION: SignExtension
        port map(
            A => instrD(15 downto 0),
            Y => 
        );

        MUX2_32_A: Mux2_32
        port map(
            zero => rd1,
            one => aluOutM,
            ctrl => forwardAD,
            output =>
        );

        MUX2_32_B: Mux2_32
        port map(
            zero => rd2,
            one => aluOutM,
            ctrl => forwardBD,
            output =>
        );

        ADDER: adder
        port map(
            A =>
            B => pcPlus4,
            Y => 
        );

end behavior;
