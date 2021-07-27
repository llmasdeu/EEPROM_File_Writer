package model;

import java.util.ArrayList;

public class EEPROMFileLine {
    private static final String START_CODE = ":";
    private static final String DATA_TYPE = "00";
    private static final String EOF_TYPE = "01";

    private String numberOfBytes;
    private String address;
    private String type;
    private String data;
    private String checksum;
    private boolean endOfFile;

    public EEPROMFileLine(String address, String data, boolean endOfFile) {
        this.address = address;
        this.data = data;
        this.endOfFile = endOfFile;
        this.numberOfBytes = computeNumberOfBytes();

        if (endOfFile) {
            this.type = EOF_TYPE;
        } else {
            this.type = DATA_TYPE;
        }

        this.checksum = computeChecksum();
    }

    @Override
    public String toString() {
        String fileLine = START_CODE + numberOfBytes + address + type + data + checksum;
        fileLine = fileLine.toUpperCase();

        return fileLine;
    }

    private String computeNumberOfBytes() {
        int bytesNumber = data.length() / 2;
        String bytesNumberHex = Integer.toString(bytesNumber, 16);

        if (bytesNumberHex.length() % 2 != 0) {
            bytesNumberHex = "0" + bytesNumberHex;
        }

        return bytesNumberHex;
    }

    private String computeChecksum() {
        ArrayList<String> splitData = splitData();
        String checksumHex;
        int splitDataDecimal[] = new int[splitData.size()], auxChecksum = 0, remaining;

        for (int i = 0; i < splitDataDecimal.length; i++) {
            splitDataDecimal[i] = Integer.parseInt(splitData.get(i), 16);
        }

        for (int i = 0; i < splitDataDecimal.length; i++) {
            auxChecksum += splitDataDecimal[i];
        }

        String negation = negateBits(auxChecksum);
        auxChecksum = Integer.parseInt(negation, 2);
        auxChecksum++;
        auxChecksum = auxChecksum & 0xFF;
        checksumHex = Integer.toString(auxChecksum, 16);
        remaining = 2 - checksumHex.length();

        for (int i = 0; i < remaining; i++) {
            checksumHex = "0" + checksumHex;
        }

        return checksumHex;
    }

    private ArrayList splitData() {
        ArrayList<String> splitData = new ArrayList<>();
        String mixedData = numberOfBytes + address + type + data;
        int aux;

        for (int i = 0; i < mixedData.length(); i = i + 2) {
            aux = i + 2;

            if (aux >= mixedData.length()) {
                splitData.add(mixedData.substring(i));
            } else {
                splitData.add(mixedData.substring(i, aux));
            }
        }

        return splitData;
    }

    private String negateBits(int value) {
        String binaryRepresentation, negation = "";
        int remainingBits, tooManyBits;

        binaryRepresentation = Integer.toString(value, 2);
        tooManyBits = binaryRepresentation.length() - 8;

        if (tooManyBits > 0) {
            binaryRepresentation = binaryRepresentation.substring(tooManyBits);
        }

        remainingBits = 8 - binaryRepresentation.length();

        for (int i = 0; i < remainingBits; i++) {
            binaryRepresentation = "0" + binaryRepresentation;
        }

        for (int i = 0; i < binaryRepresentation.length(); i++) {
            if (binaryRepresentation.charAt(i) == '0') {
                negation += "1";
            } else {
                negation += "0";
            }
        }

        System.out.println(negation);

        return negation;
    }
}
