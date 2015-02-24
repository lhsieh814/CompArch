/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author Gavin
 */
import java.util.*;
public class LoadStoreQueue {
	// 4 Entries
	public ArrayList<Integer> DestID = new ArrayList<Integer>();
	public ArrayList<Long> Address = new ArrayList<Long>();
	// value at the memory location specified by Address.
	public ArrayList<Long> value = new ArrayList<Long>();
	// state information.
	public ArrayList<String> Instruction = new ArrayList<String>();
	public ArrayList<String> ExecState = new ArrayList<String>();

}