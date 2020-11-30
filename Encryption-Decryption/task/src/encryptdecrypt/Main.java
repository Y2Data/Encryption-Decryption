package encryptdecrypt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public class Main {
    static HashMap<String, String> commandLineArgs = new HashMap<>();
    static String inFileName;
    static String toFileName;

    public static void main(String[] args) {
        for (int i = 0; i < args.length - 1; i += 2)
            commandLineArgs.put(args[i], args[i + 1]);
        int key = getKey();
        String text = getData(isFromFile());
        var method = MethodsFactory.callAlgorithm(getAlg(), text, key);
        String result = method.operation(isEncryption());
        output(result);
    }

    private static String getAlg() {
        try {
            return commandLineArgs.get("-alg");
        } catch (Exception e) {
            System.out.println("No algorithm specified, using shift.");
            return "shift";
        }
    }

    private static void output(String result){
        if (isToFile()) {
            outputToFile(result, toFileName);
        } else {
            System.out.println(result);
        }
    }

    private static int getKey() {
        String key = commandLineArgs.get("-key");
        return key == null ? 0 : Integer.parseInt(key);
    }

    private static String getData(boolean isFromFile) {
        String data = commandLineArgs.get("-data");
        if (data != null)
            return data;
        else {
            if (isFromFile)
                return readFromFile(inFileName);
            else
                return "";
        }
    }


    private static void outputToFile(String result, String fileName){
        File file = new File(fileName);
        try (FileWriter writer = new FileWriter(file) ) {
            writer.write(result);
        } catch (IOException e) {
            System.out.println("Error!");
        }
    }

    private static boolean isToFile() {
        toFileName = commandLineArgs.get("-out");
        return toFileName != null;
    }

    private static boolean isFromFile() {
        inFileName = commandLineArgs.get("-in");
        return inFileName != null;
    }

    private static String readFromFile(String fileName){
        try {
            File file = new File(fileName);
            Scanner sc = new Scanner(file);
            StringBuilder content = new StringBuilder();
            while (sc.hasNextLine())
                content.append(sc.nextLine());
            return content.toString();
        } catch (FileNotFoundException e) {
            System.out.println("Error!");
            return "";
        }
    }

    public static boolean isEncryption() {
        String mode = commandLineArgs.get("-mode");
        try {
            return mode.equals("enc");
        } catch (Exception e) {
            return true;
        }
    }
}
abstract class Methods {
    public abstract String encrypt(String text, int key);
    public abstract String decrypt();
    public abstract String operation(boolean isEnc);
}

class Shift extends Methods {
    private String text;
    private int key;

    public Shift(String text, int key) {
        this.text = text;
        this.key = key;
    }

    public String encrypt(String text, int key) {
        var result = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (c != ' ' && c != '\n' && c != '`' && c != '\'' && c!= ',' && c!= '-') {
                int originalAlphabetPosition = c - 'a';
                int newAlphabetPosition = (originalAlphabetPosition + key) % 26;
                char newC = (char) ('a' + newAlphabetPosition);
                result.append(newC);
            } else
                result.append(c);
        }
        return result.toString();
    }

    public String decrypt() {
        return encrypt(text, 26 - (key % 26));
    }

    public String operation(boolean isEnc) {
        return isEnc ? encrypt(text, key) : decrypt();
    }
}

class Unicode extends Methods {
    private String text;
    private int key;

    public Unicode(String text, int key) {
        this.text = text;
        this.key = key;
    }

    public String encrypt(String text, int key) {
        var result = new StringBuilder();
        for (char c : text.toCharArray()) {
            result.append((char)(c + key));
        }
        return result.toString();
    }

    public String decrypt() {
        var result = new StringBuilder();
        for (char c : text.toCharArray()) {
            result.append((char) (c - key));
        }
        return result.toString();
    }

    public String operation(boolean isEnc) {
        return isEnc ? encrypt(text, key) : decrypt();
    }
}

class MethodsFactory {
    public static Methods callAlgorithm(String type, String text, int key) {
        if ("unicode".equals(type))
            return new Unicode(text, key);
        else
            return new Shift(text, key);
    }
}
