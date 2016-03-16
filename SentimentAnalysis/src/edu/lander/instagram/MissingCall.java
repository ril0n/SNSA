package edu.lander.instagram;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * @author Rob
 */
public class MissingCall extends Exception {
    String missing;

    public MissingCall(String missed) {
        missing = missed;
    }

    public String getMissing() {
        return missing;
    }

}