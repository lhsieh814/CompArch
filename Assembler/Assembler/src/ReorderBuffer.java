/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author Yang Zhou
 */
import java.util.*;
public class ReorderBuffer {
    // 6 entries
    public ArrayList<Integer> Id = new ArrayList<Integer>();
    // Destination can be Register or null for branch or store
    public ArrayList<String> Destination = new ArrayList<String>();
    /*
     * Value -->Long value of the Destination.
     */
    public ArrayList<Long>  Value = new ArrayList<Long>();
    public ArrayList<String>  Instruction = new ArrayList<String>();
    public ArrayList<Boolean>  Ready = new ArrayList<Boolean>();
}