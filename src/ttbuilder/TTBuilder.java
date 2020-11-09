/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ttbuilder;

import java.util.List;
import org.apache.commons.cli.*;

/**
 *
 * @author david
 */
public class TTBuilder {

    /**
     * @param args the command line arguments
     */
    
    
    
    public static void main(String[] args) {
        Builder ttb = new Builder();
// TODO code application logic here
        ttb.ProcessArgs(args);
    }
    
   
    
}
