/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ttbuilder;

/**
 *
 * @author david
 */
public class ClsTime {
    
    
    protected int time;
    
    
    public ClsTime(String newTime){
     int minutes;
    int hours;
    //if newTime.matches("")
    
    String[] aarray = newTime.split("[^\\d]+");
    
    hours = Integer.parseInt(aarray[0]);
    minutes = Integer.parseInt(aarray[1]);
    
    
    time = (hours*60+minutes) % (24*60);
    
    }
    
    
    @Override
    public String toString(){
        int hours;
        int minutes;        
        hours=(time / 60);
        minutes = time % 60;
        
        
        return (String.format("%02d:%02d", hours,minutes));
    }
    
   public void add(ClsTime newtime){
    this.time += newtime.time;
    this.time = this.time % (24*60);
   } 
    
    
}
