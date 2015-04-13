LIBRARY ieee;
USE ieee.std_logic_1164.ALL;
use work.MIPSCPU_constants.ALL;

--this entity reads instructions from memory (Init.dat) and outputs instructions partitioned into 4 vectors according to MIPS convention
--this entity also dumps instruction memory contents to (MemCon.dat)
ENTITY instructionFetch IS
PORT(
	clk : in std_logic;
	clk_en: in std_logic;
	PCF : in integer := 0; --address of next instruction register to be read
	Instruction : out std_logic_vector(register_size downto 0); --retrieved unformatted instruction
	InstructionReady : out std_logic := '0'; --signifies instruction is ready
	fetchNext : in std_logic := '0' --next instruction is only fetched when set to high
	--instruction formatted with register partitions
);
END instructionFetch;

ARCHITECTURE behavior OF instructionFetch IS
--initialize memory
	type state_type is (init, read_mem1, read_mem2, waiting);
	Constant Num_Bits_in_Byte: integer := 8; 
	Constant Num_Bytes_in_Word: integer := 4; 
	Constant Memory_Size: integer := 256; 
	COMPONENT Main_Memory
	generic (
			File_Address_Read : string :="Init.dat";
			File_Address_Write : string :="MemCon.dat";
			Mem_Size_in_Word : integer:=256;	
			Num_Bytes_in_Word: integer:=4;
			Num_Bits_in_Byte: integer := 8; 
			Read_Delay: integer:=0; 
			Write_Delay: integer:=0
		 );
   	 PORT(
			clk : IN  std_logic;
			address : IN  integer;
			Word_Byte: in std_logic;
			we : IN  std_logic;
			wr_done : OUT  std_logic;
			re : IN  std_logic;
			rd_ready : OUT  std_logic;
			data : INOUT  std_logic_vector(Num_Bytes_in_Word*Num_Bits_in_Byte-1 downto 0);
			initialize : IN  std_logic;
			dump : IN  std_logic
        );
    	END COMPONENT;
    
	
   --Inputs
   signal address : integer := 0;
   signal we : std_logic := '0';
   signal re : std_logic := '0';
   signal data : std_logic_vector(Num_Bytes_in_Word*Num_Bits_in_Byte-1 downto 0) := (others => 'Z');
   signal initialize : std_logic := '0';
   signal dump : std_logic := '0';
   signal wr_done : std_logic;
   signal rd_ready : std_logic;
	signal state:	state_type:=init;
 
BEGIN
   main_mem: Main_Memory 
	generic map (
			File_Address_Read =>"Init.dat",
			File_Address_Write =>"MemCon.dat",
			Mem_Size_in_Word =>256,
			Num_Bytes_in_Word=>4,
			Num_Bits_in_Byte=>8,
			Read_Delay=>0,
			Write_Delay=>0
		 )
		PORT MAP (
          clk => clk,
          address => address,
          Word_Byte => '1',
          we => we,
          wr_done => wr_done,
          re => re,
          rd_ready => rd_ready,
          data => data,          
          initialize => initialize,
          dump => dump
        ); 


   -- Stimulus process
   stim_proc: process (clk)
   begin
      if RISING_EDGE(clk) and clk_en='1' then
			data <= (others=>'Z');			
			case state is
				when init =>
					initialize <= '1';
					InstructionReady <='0';
					state <= read_mem1;		
				when read_mem1 =>
					InstructionReady <='0';
					we <='0';
					re <='1';
					initialize <= '0';
					dump <= '0';
					state <= read_mem2;
				when read_mem2 =>
					re <='1';
					if (rd_ready = '1') then -- the output is ready on the memory bus
						Instruction <= data;
						--move to next address
						address <= PCF;
						re <='0';
						InstructionReady <='1';
						state <= waiting; --read finished go to wait state
						dump <='1';
					else
						state <= read_mem2; -- stay in this state till you see rd_ready='1';
						InstructionReady <='0';
					end if;

				when waiting =>
					--wait until a fetch call before getting next memory address
					if(fetchNext='1') then
						address <= PCF;
						state <= read_mem1;
					else state <= waiting;
					end if;
			end case;
			
		end if;
   end process;

END;
