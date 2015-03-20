LIBRARY ieee;
USE ieee.std_logic_1164.ALL;
use work.MIPSCPU_constants.ALL;

--this entity interacts with the Data memory (DataInit.dat) and dumps written memory contents to (DataMemCon.dat)
--operates in a similar fashion to instructionFetch.vhd
ENTITY DataMemory IS
PORT(
	clk : in std_logic;
	nextAddress : in integer := 0;
	instReady : out std_logic := '0';
	fetchNext : in std_logic := '0';
	readOrWrite : in std_logic := '0';
	dataToWrite : in std_logic_vector(register_size downto 0);
	output : OUT STD_LOGIC_VECTOR(register_size DOWNTO 0)
);
END DataMemory;

ARCHITECTURE behavior OF DataMemory IS
	type state_type is (init, read_mem1, read_mem2, write_mem1, sdump,waiting);
	Constant Num_Bits_in_Byte: integer := 8; 
	Constant Num_Bytes_in_Word: integer := 4; 
	Constant Memory_Size: integer := 256; 
--initialize data memory
	COMPONENT Main_Memory
	generic (
			File_Address_Read : string :="DataInit.dat";
			File_Address_Write : string :="DataMemCon.dat";
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
   signal word_byte :std_logic := '1';
   signal fetchNext_delayed : std_logic := '0'; -- used to capture the rising edge of the fetchNext signal
   signal wr_done : std_logic;
   signal rd_ready : std_logic;
	signal state:	state_type:=init;
 
BEGIN
   main_mem: Main_Memory 
	generic map (
			File_Address_Read =>"DataInit.dat",
			File_Address_Write =>"DataMemCon.dat",
			Mem_Size_in_Word =>256,
			Num_Bytes_in_Word=>4,
			Num_Bits_in_Byte=>8,
			Read_Delay=>0,
			Write_Delay=>0
		 )
		PORT MAP (
          clk => clk,
          address => address,
          Word_Byte => word_byte,
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
      if RISING_EDGE(clk) then
			data <= (others=>'Z');
			fetchNext_delayed<=fetchNext;
			case state is
				when init =>
					initialize <= '1'; --triggerd.
					instReady <='0';
					state <= waiting;
				when waiting =>
					instReady <='0';
					word_byte<='1';
					initialize <= '0';
					address <= nextAddress;
					re<='0';
					we<='0';
					--memory continues to wait until a fetchNext call is placed 
					if fetchNext /= fetchNext_delayed and fetchNext_delayed='0' then
						if(readOrWrite='0') then
							state <= read_mem1;
						elsif(readOrWrite='1') then
							state <= write_mem1;
						end if;
					else 
						state <= waiting;
					end if;		
				when read_mem1 =>
					instReady <='0';
					we <='0';
					re <='1';
					initialize <= '0';
					dump <= '0';
					state <= read_mem2;
				when read_mem2 =>
					re <='1';
					if (rd_ready = '1') then -- the output is ready on the memory bus
						output <= data;
						address <= nextAddress;
						re <='0';
						instReady <='1';
						state <= waiting; --read finished go to test state write 
					else
						state <= read_mem2; -- stay in this state till you see rd_ready='1';
						instReady <='0';
					end if;
				when write_mem1 =>
					address <= nextAddress;
					--word_byte<='0';
					we <='1';
					re <='0';
					initialize <= '0';
					dump <= '0';
					data <= dataToWrite;
					
					if (wr_done = '1') then -- the output is ready on the memory bus
						state <= sdump; --write finished go to the dump state
						instReady <= '1';
					else
						state <= write_mem1; -- stay in this state till you see rd_ready='1';
						instReady <= '0';
					end if;	
					
				when sdump =>
					initialize <= '0'; 
					re<='0';
					we<='0';
					word_byte<='1';
					dump <= '1'; --triggerd
					state <= waiting;
				when others =>
			end case;
		end if;
   end process;

END;