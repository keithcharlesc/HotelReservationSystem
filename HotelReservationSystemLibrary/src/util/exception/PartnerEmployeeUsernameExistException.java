/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.exception;

/**
 *
 * @author xianhui
 */
public class PartnerEmployeeUsernameExistException extends Exception{

    public PartnerEmployeeUsernameExistException() {
    }

    public PartnerEmployeeUsernameExistException(String string) {
        super(string);
    }
    
}
