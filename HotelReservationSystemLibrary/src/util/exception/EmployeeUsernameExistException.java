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
public class EmployeeUsernameExistException extends Exception {

    public EmployeeUsernameExistException() {
    }

    public EmployeeUsernameExistException(String string) {
        super(string);
    }
    
}
