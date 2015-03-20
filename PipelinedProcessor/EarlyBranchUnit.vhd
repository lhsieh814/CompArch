library ieee;
use ieee.std_logic_vector.all;
use work.MIPSCPU_constants.all;

Entity EarlyBranch is 

	Port(
		D0, D1 	: in std_logic_vector(register_size downto 0); -- input of first mux
		D2, D3	: in std_logic_vector(register_size downto 0); -- input of second mux
		ForwardAD	: in std_logic;
		ForwardBD	: in std_logic;
		BranchD 	: in std_logic;
		PCSrcD 	: out std_logic;
	);
	End;
	
Architecture behave of EarlyBranch is
	component mux 
	
        port(
				D0, D1 : in std_logic_vector(register_size downto 0);
             S      : in std_logic;
             Y      : out std_logic_vector(register_size downto 0))
    end component;
    for mux0 : mux use entity work.mux;
    for mux1 : mux use entity work.mux;
	 
	component AndGate
		
		Port(
			input1, input2 	: in std_logic;
			output				: out std_logic
		);
	end component;
	for andgate : AndGate use entity work.AndGate;
	
	component Comparator
		Port(
			a, b 		: in std_logic_vector(register_size downto 0);
			output	: out std_logic
		);
	end component;
	for comparator : Comparator use entity work.Comparator;
	
	signal mux0_output, mux1_output	: std_logic_vector(register_size downto 0);
	signal EqualD 							: std_logic;
	
	begin
	
	mux0 : mux port map(
		D0 => D0;
		D1 => D1;
		S 	=> ForwardAD;
		Y  => mux0_output
	);
	
	mux1 : mux port map(
		D0 => D2;
		D0 => D3;
		S 	=> ForwardBD;
		Y 	=> mux1_output
	);
	
	comparator: Comparator port map(
		a => mux0_output;
		b => mux1_output;
		output => EqualD;
	);
	
	andGate : AndGate port map(
		input1 => BranchD;
		input2 => EqualD;
		output => PCSrcD;
	);
	
	end;