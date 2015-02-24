LIBRARY ieee;
USE ieee.std_logic_1164.ALL;
use work.MIPSCPU_constants.ALL;

ENTITY instructionFetch IS
PORT(
	clk : in std_logic;
	nextAddress : in integer;
	instruction : out std_logic_vector(register_size downto 0);
	instReady : out std_logic;
	fetchNext : in std_logic
);
END instructionFetch;

ARCHITECTURE behavior OF instructionFetch IS
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
	
	-- Tests Simulation State 
	signal state:	state_type:=init;
	
	--Memory Data Read
	signal MDR: std_logic_vector(Num_Bytes_in_Word*Num_Bits_in_Byte-1 downto 0);
 
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
          Word_Byte => '0',
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
			case state is
				when init =>
					initialize <= '1'; --triggerd.
					instReady <='0';
					state <= read_mem1;		
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
						MDR <= data;
						state <= waiting; --read finished go to test state write 
						address <= nextAddress;
						re <='0';
						instReady <='1';
					else
						state <= read_mem2; -- stay in this state till you see rd_ready='1';
					end if;
				when waiting =>
					if(fetchNext = '1') then
						state <= read_mem1;
					else state <= waiting;
					end if;
			end case;
			
		end if;
   end process;

END;
