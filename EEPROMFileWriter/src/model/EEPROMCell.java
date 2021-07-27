package model;

import java.io.Serializable;

public class EEPROMCell implements Serializable {
    private int binaryInputPins;
    private int hexadecimalInputPins;
    private int binaryOutputPins;
    private int hexadecimalOutputPins;

    private String binaryAddress;
    private int decimalAddress;
    private String hexadecimalAddress;
    private String binaryData;
    private int decimalData;
    private String hexadecimalData;

    public EEPROMCell(int binaryInputPins, int hexadecimalInputPins, int binaryOutputPins, int hexadecimalOutputPins, int decimalAddress) {
        this.binaryInputPins = binaryInputPins;
        this.hexadecimalInputPins = hexadecimalInputPins;
        this.binaryOutputPins = binaryOutputPins;
        this.hexadecimalOutputPins = hexadecimalOutputPins;
        this.decimalAddress = decimalAddress;

        convertAddress();
        resetBinaryData();
    }

    public String getHexadecimalAddress() {
        return hexadecimalAddress;
    }

    public String getHexadecimalData() {
        return hexadecimalData;
    }

    public boolean isThisCell(String input) {
        input = processString(input, binaryInputPins);

        return input.equals(binaryAddress);
    }

    public boolean updateData(String input) {
        input = processString(input, binaryOutputPins);

        if (checkStringLength(input, binaryOutputPins) && checkBinaryCharacters(input)) {
            this.binaryData = input;
            this.decimalData = Integer.parseInt(input, 2);
            this.hexadecimalData = Integer.toString(decimalData, 16);
            this.hexadecimalData = processString(hexadecimalData, hexadecimalOutputPins);
            showData();

            return true;
        }

        return false;
    }

    private void resetBinaryData() {
        this.decimalData = 0;
        this.binaryData = "0";
        this.binaryData = processString(binaryData, binaryOutputPins);
        convertData();
    }

    private void convertAddress() {
        this.binaryAddress = Integer.toString(decimalAddress, 2);
        this.hexadecimalAddress = Integer.toString(decimalAddress, 16);
        this.binaryAddress = processString(binaryAddress, binaryInputPins);
        this.hexadecimalAddress = processString(hexadecimalAddress, hexadecimalInputPins);
    }

    private void convertData() {
        this.decimalData = Integer.parseInt(binaryData, 2);
        this.hexadecimalData = Integer.toString(decimalData, 16);
        this.hexadecimalData = processString(hexadecimalData, hexadecimalOutputPins);
    }

    private String processString(String input, int numberPins) {
        int remaining = numberPins - input.length();

        for (int i = 0; i < remaining; i++) {
            input = "0" + input;
        }

        return input;
    }

    private boolean checkStringLength(String input, int numberPins) {
        return input.length() == numberPins;
    }

    private boolean checkBinaryCharacters(String input) {
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) != '0' && input.charAt(i) != '1') {
                return false;
            }
        }

        return true;
    }

    public void showData() {
        System.out.println("                    ADDRESS                    ");
        System.out.println("===============================================");
        System.out.println("Decimal: " + decimalAddress);
        System.out.println("Binary: " + binaryAddress);
        System.out.println("Hexadecimal: " + hexadecimalAddress);
        System.out.println("                     DATA                      ");
        System.out.println("===============================================");
        System.out.println("Decimal: " + decimalData);
        System.out.println("Binary: " + binaryData);
        System.out.println("Hexadecimal: " + hexadecimalData);
    }
}
