package io.github.tmgg;

import java.io.IOException;

public class SetVersion {

    public static final String NEW_VERSION = "0.1.13";


    public static void main(String[] args) throws IOException {
        SetVersion sv = new SetVersion(NEW_VERSION);
        sv.changeMaven();
        sv.changeNpm();
    }

    private void changeNpm() {
    }

    private void changeMaven() throws IOException {


    }

    public SetVersion(String version) {
        this.version = version;
    }

    private String version;



}
