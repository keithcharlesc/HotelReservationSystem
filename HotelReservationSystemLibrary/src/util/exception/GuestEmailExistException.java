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
public class GuestEmailExistException extends Exception {

    public GuestEmailExistException() {
    }

    public GuestEmailExistException(String string) {
        super(string);
    }

}
