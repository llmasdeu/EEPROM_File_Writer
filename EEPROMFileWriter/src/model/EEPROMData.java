package model;

import java.io.Serializable;
import java.util.ArrayList;

public class EEPROMData implements Serializable {
    // Model 27C256
    public static transient final String NAME_27C256 = "27C256";
    private static transient final int BINARY_INPUT_PINS_27C256 = 15;
    private static transient final int HEXADECIMAL_INPUT_PINS_27C256 = 4;
    private static transient final int BINARY_OUTPUT_PINS_27C256 = 8;
    private static transient final int HEXADECIMAL_OUTPUT_PINS_27C256 = 2;
    private static transient final int NUMBER_OF_ADDRESSES_PER_LINE_27C256 = 16;

    private String modelName;
    private int binaryInputPins;
    private int hexadecimalInputPins;
    private int binaryOutputPins;
    private int hexadecimalOutputPins;
    private transient int numberOfAddressesPerLine;
    private EEPROMCell[] cells;

    public EEPROMData(String modelName) {
        this.modelName = checkModelName(modelName);
        this.binaryInputPins = checkBinaryInputPins(modelName);
        this.hexadecimalInputPins = checkHexadecimalInputPins(modelName);
        this.binaryOutputPins = checkBinaryOutputPins(modelName);
        this.hexadecimalOutputPins = checkHexadecimalOutputPins(modelName);
        this.numberOfAddressesPerLine = checkNumberOfAddressesPerLine(modelName);

        if (binaryInputPins > 0 && hexadecimalInputPins > 0 && binaryOutputPins > 0 && hexadecimalOutputPins > 0) {
            int addresses = (int) Math.pow(2, binaryInputPins);
            cells = new EEPROMCell[addresses];

            for (int i = 0; i < addresses; i++) {
                cells[i] = new EEPROMCell(binaryInputPins, hexadecimalInputPins, binaryOutputPins, hexadecimalOutputPins, i);
            }
        } else {
           cells = null;
        }
    }

    public EEPROMCell[] getCells() {
        return cells;
    }

    public int doesCellExist(String binaryAddress) {
        boolean exists = false;
        int i = 0, j = -1;

        while (!exists && i < cells.length) {
            if (cells[i].isThisCell(binaryAddress)) {
                j = i;
                exists = true;
            }

            i++;
        }

        return j;
    }

    public boolean updateCell(int index, String binaryData) {
        return cells[index].updateData(binaryData);
    }

    public void updateCellsFromFile(ArrayList<String> fileLines) {
        String[] lineParts;
        int index;

        for (int i = 0; i < fileLines.size(); i++) {
            index = -1;
            lineParts = fileLines.get(i).split("-");
            index = doesCellExist(lineParts[0]);

            if (index == -1) {
                System.out.println("Error! No s'ha trobat l'adreça " + lineParts[0] + "\n");
            } else {
                if (!updateCell(index, lineParts[1])) {
                    System.out.println("Error! No s'ha pogut inserir la dada.\n");
                }
            }
        }
    }

    public void updateCellsDebugMode() {
        String input = "";

        for (int i = 0; i < binaryOutputPins; i++) {
            input += "1";
        }

        for (int i = 0; i < cells.length; i++) {
            cells[i].updateData(input);
        }
    }

    public void viewCell(int index) {
        cells[index].showData();
    }

    public ArrayList generateEEPROMFileContent() {
        ArrayList<EEPROMFileLine> eepromFileContent = new ArrayList<>();
        int addresses = (int) Math.pow(2, binaryInputPins);

        for (int i = 0; i < addresses; i = i + numberOfAddressesPerLine) {
            eepromFileContent.add(new EEPROMFileLine(cells[i].getHexadecimalAddress(), getJointData(i), false));
        }

        eepromFileContent.add(new EEPROMFileLine("0000", "", true));

        return eepromFileContent;
    }

    private String checkModelName(String modelName) {
        if (modelName.equalsIgnoreCase(NAME_27C256)) {
            return NAME_27C256;
        }

        return "";
    }

    private int checkBinaryInputPins(String modelName) {
        if (modelName.equalsIgnoreCase(NAME_27C256)) {
            return BINARY_INPUT_PINS_27C256;
        }

        return 0;
    }

    private int checkHexadecimalInputPins(String modelName) {
        if (modelName.equalsIgnoreCase(NAME_27C256)) {
            return HEXADECIMAL_INPUT_PINS_27C256;
        }

        return 0;
    }

    private int checkBinaryOutputPins(String modelName) {
        if (modelName.equalsIgnoreCase(NAME_27C256)) {
            return BINARY_OUTPUT_PINS_27C256;
        }

        return 0;
    }

    private int checkHexadecimalOutputPins(String modelName) {
        if (modelName.equalsIgnoreCase(NAME_27C256)) {
            return HEXADECIMAL_OUTPUT_PINS_27C256;
        }

        return 0;
    }

    private int checkNumberOfAddressesPerLine(String modelName) {
        if (modelName.equalsIgnoreCase(NAME_27C256)) {
            return NUMBER_OF_ADDRESSES_PER_LINE_27C256;
        }

        return 0;
    }

    private String getJointData(int initialValue) {
        String jointData = "";
        int aux;

        for (int i = 0; i < numberOfAddressesPerLine; i++) {
            aux = initialValue + i;
            jointData += cells[aux].getHexadecimalData();
        }

        return jointData;
    }
}
