package com.example.vchatserver.name;

public class NameService {

    public static int ok = 200;
    public static int lengthError = 500;
    public static int checkName(String name) {
        if (name.replace(" ", "").length() == 0) {
            return lengthError;
        }
        return ok;
    }
}
