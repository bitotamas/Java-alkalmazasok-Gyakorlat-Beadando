package com.example;

import java.awt.*;
import java.net.URI;

public class Main {
    public static void main(String[] args) {
        boolean started=false;
        try{
            NotebookWebApplication.main(args);
            started=true;
        } catch (Exception e) {
            System.out.println("\n\nHiba! a Weboldal nem indult el! Kérjük indítsa újra a programot!");
        }
        if (started) {
            System.out.println("\n\nA weboldal sikeresen elindult! Kérjük írja a böngészőjébe a weboldal megnyitásához: localhost:8080");
        }
    }
}
