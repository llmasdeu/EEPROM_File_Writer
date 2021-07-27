package controller;

import model.EEPROMData;
import model.EEPROMFileLine;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Menu {
    private static final String EFWF_FILE_EXTENSION = ".efwf";
    private static final String HEX_FILE_EXTENSION = ".hex";

    private EEPROMData eepromData;

    public Menu() {}

    public void mainMenu() {
        boolean exit = false;
        Scanner scr = new Scanner(System.in);
        String input;

        System.out.println("\t---------- Escriptura de memòries EEPROM ----------\n");
        newLoadFileMenu();

        while (!exit) {
            System.out.println("Menú principal:\n");
            System.out.println("\t1. Inserir noves dades");
            System.out.println("\t2. Visualitzar dades");
            System.out.println("\t3. Desar fitxer");
            System.out.println("\t4. Sortir\n");
            System.out.print("Selecciona una opció: ");
            input = scr.nextLine();

            switch (input) {
                case "1":
                    addNewDataMenu();
                    break;

                case "2":
                    viewData();
                    break;

                case "3":
                    saveFile();
                    break;

                case "4":
                    exit = true;
                    break;

                default:
                    System.out.println("Error! La opció seleccionada no existeix.\n");
                    break;
            }
        }
    }

    private void newLoadFileMenu() {
        boolean ok = false;
        Scanner scr = new Scanner(System.in);
        String input;

        while (!ok) {
            System.out.println("Opcions:\n");
            System.out.println("\t1. Crear un nou fitxer");
            System.out.println("\t2. Importar des d'un fitxer EFWF\n");
            System.out.print("Què vols fer? ");
            input = scr.nextLine();

            switch (input) {
                case "1":
                    ok = true;
                    System.out.println("");
                    modelMenu();
                    break;

                case "2":
                    System.out.println("");
                    ok = loadFile();
                    break;

                default:
                    System.out.println("Error! La opció seleccionada no és correcta.\n");
                    break;
            }
        }
    }

    private void modelMenu() {
        ArrayList<String> models = getModelsList();
        Scanner scr = new Scanner(System.in);
        boolean created = false;
        String input;
        int j;

        while (!created) {
            System.out.println("Models d'EEPROM disponibles:\n");

            for (int i = 0; i < models.size(); i++) {
                j = i + 1;
                System.out.println("\t" + j + ". " + models.get(i) + "\n");
            }

            System.out.print("Introdueix el model d'EEPROM desitjat: ");
            input = scr.nextLine();

            if (checkModel(models, input)) {
                eepromData = new EEPROMData(input);
                System.out.println("EEPROM seleccionada correctament!\n");
                created = true;
            } else {
                System.out.println("Error! El model seleccionat no es troba disponible.\n");
            }
        }
    }

    private boolean loadFile() {
        Scanner scr = new Scanner(System.in);
        String filePath;
        boolean ok = false;

        System.out.print("Introdueix la ruta del fitxer desitjat: ");
        filePath = scr.nextLine();

        if (filePath.equals("")) {
            System.out.println("Error! El nom del fitxer no és vàlid.\n");
        } else {
            try {
                FileInputStream fstream = new FileInputStream(filePath);
                ObjectInputStream ostream = new ObjectInputStream(fstream);

                while (true) {
                    Object obj;

                    try {
                        obj = ostream.readObject();
                    } catch (EOFException | ClassNotFoundException e) {
                        break;
                    }
                    eepromData = (EEPROMData) obj;
                }

                fstream.close();
                ok = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return ok;
    }

    private void addNewDataMenu() {
        boolean ok = false;
        Scanner scr = new Scanner(System.in);
        String input;

        while (!ok) {
            System.out.println("Opcions:\n");
            System.out.println("\t1. Inserir dades manualment");
            System.out.println("\t2. Importar dades des d'un fitxer");
            System.out.println("\t3. Solucionar errors\n");
            System.out.print("Què vols fer? ");
            input = scr.nextLine();

            switch (input) {
                case "1":
                    ok = true;
                    System.out.println("");
                    addNewData();
                    break;

                case "2":
                    System.out.println("");
                    ok = addNewDataFromFile();
                    break;

                case "3":
                    ok = true;
                    System.out.println("");
                    addDataDebugMode();
                    break;

                default:
                    System.out.println("Error! La opció seleccionada no és correcta.\n");
                    break;
            }
        }
    }

    private void addNewData() {
        Scanner scr = new Scanner(System.in);
        boolean exit = false;
        String input;
        char next = ' ';
        int index;

        while (!exit) {
            System.out.print("Introdueix la cel·la de memòria: ");
            input = scr.nextLine();
            index = eepromData.doesCellExist(input);

            if (index != -1) {
                System.out.print("Introdueix la informació de la cel·la: ");
                input = scr.nextLine();
                eepromData.updateCell(index, input);
            } else {
                System.out.println("Error! La cel·la introduïda no és correcta.\n");
            }

            System.out.print("\nVols tornar al menú principal [S per a tornar]? ");
            next = scr.next().charAt(0);

            if (next == 'S' || next == 's') {
                exit = true;
            }

            scr.nextLine();
        }
    }

    private boolean addNewDataFromFile() {
        ArrayList<String> fileLines = new ArrayList<>();
        Scanner scr = new Scanner(System.in);
        String filePath;
        boolean ok = false;

        System.out.print("Introdueix la ruta del fitxer desitjat: ");
        filePath = scr.nextLine();

        if (filePath.equals("")) {
            System.out.println("Error! El nom del fitxer no és vàlid.\n");
        } else {
            try {
                FileInputStream fstream = new FileInputStream(filePath);
                BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
                String strLine;

                while ((strLine = br.readLine()) != null) {
                    fileLines.add(strLine);
                }

                br.close();
                ok = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (ok == true) {
            eepromData.updateCellsFromFile(fileLines);
        }

        return ok;
    }

    private void addDataDebugMode() {
        eepromData.updateCellsDebugMode();
    }

    private void viewData() {
        Scanner scr = new Scanner(System.in);
        boolean exit = false;
        String input;
        char next = ' ';
        int index;

        while (!exit) {
            System.out.print("Quina cel·la vols consultar? ");
            input = scr.nextLine();
            index = eepromData.doesCellExist(input);

            if (index != -1) {
                eepromData.viewCell(index);
            } else {
                System.out.println("La cel·la introduïda no és correcta.");
            }

            System.out.print("\nVols tornar al menú principal [S per a tornar]? ");
            next = scr.next().charAt(0);

            if (next == 'S' || next == 's') {
                exit = true;
            }

            scr.nextLine();
        }
    }

    private void saveFile() {
        Scanner scr = new Scanner(System.in);
        boolean exit = false;
        String input;

        while (!exit) {
            System.out.println("Opcions:\n");
            System.out.println("\t1. Desar fitxer en format EFWF");
            System.out.println("\t2. Desar fitxer en format HEX");
            System.out.println("\t3. Tornar al menú principal\n");
            System.out.print("Què vols fer? ");

            input = scr.nextLine();

            switch (input) {
                case "1":
                    saveEFWFFile();
                    break;

                case "2":
                    saveHEXFile();
                    break;

                case "3":
                    exit = true;
                    System.out.println("");
                    break;

                default:
                    System.out.println("Error! La opció seleccionada no és correcta.\n");
                    break;
            }
        }
    }

    private void saveEFWFFile() {
        Scanner scr = new Scanner(System.in);
        String fileName;

        System.out.print("Introdueix el nom del fitxer desitjat (sense el .efwf): ");
        fileName = scr.nextLine();

        if (fileName.equals("")) {
            System.out.println("Error! El nom del fitxer no és vàlid.\n");
        } else {
            fileName = fileName + EFWF_FILE_EXTENSION;

            try {
                FileOutputStream fos = new FileOutputStream(fileName);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(eepromData);
                oos.close();
                System.out.println("El fitxer ha estat creat satisfactòriament.\n");
            } catch (IOException ex) {
                ex.printStackTrace();
                System.out.println("Error! Hi ha hagut un problema en desar el fitxer.\n");
            }
        }
    }

    private void saveHEXFile() {
        ArrayList<EEPROMFileLine> eepromFileContent = eepromData.generateEEPROMFileContent();
        Scanner scr = new Scanner(System.in);
        String fileName;

        System.out.print("Introdueix el nom del fitxer desitjat (sense el .hex): ");
        fileName = scr.nextLine();

        if (fileName.equals("")) {
            System.out.println("Error! El nom del fitxer no és vàlid.\n");
        } else {
            fileName = fileName + HEX_FILE_EXTENSION;

            BufferedWriter writer = null;
            try {
                writer = new BufferedWriter(new FileWriter(fileName));

                for (int i = 0; i < eepromFileContent.size(); i++) {
                    writer.write(eepromFileContent.get(i).toString() + "\n");
                }

                System.out.println("El fitxer ha estat creat satisfactòriament.\n");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error! Hi ha hagut un problema en desar el fitxer.\n");
            } finally {
                try {
                    writer.close();
                } catch (Exception e) {
                    System.out.println("Error! Hi ha hagut un problema en desar el fitxer.\n");
                }
            }
        }
    }

    private ArrayList getModelsList() {
        ArrayList<String> models = new ArrayList<>();
        models.add(EEPROMData.NAME_27C256);

        return models;
    }

    private boolean checkModel(ArrayList<String> models, String model) {
        for (int i = 0; i < models.size(); i++) {
            if (model.equalsIgnoreCase(models.get(i))) {
                return true;
            }
        }

        return false;
    }
}
