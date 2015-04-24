package navalbattle.lang;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LanguageHelper {

    private final String langFolder;

    // File name 1
    //            constant1 => value1
    //            constant2 => value2
    // File name 2
    //            constant3 => value3
    private final HashMap<String, HashMap<String, String>> constants = new HashMap<>();

    public LanguageHelper(String langFolder) {
        this.langFolder = langFolder;
    }

    /**
     * Return the translated text
     *
     * @param fileName File name (without extension) in which the constant is
     * defined
     * @param constantName The constant name you want to get the translation for
     * @return The translated text
     * @throws navalbattle.lang.InvalidFileNameException
     * @throws navalbattle.lang.InvalidConstantNameException
     */
    public String getText(String fileName, String constantName) throws InvalidFileNameException, InvalidConstantNameException {

        if (fileName.isEmpty() || constantName.isEmpty()) {
            throw new InvalidParameterException();
        }

        synchronized (this.constants) {
            if (!this.constants.containsKey(fileName)) {
                throw new InvalidFileNameException();
            }

            if (!this.constants.get(fileName).containsKey(constantName)) {
                throw new InvalidConstantNameException();
            }

            return this.constants.get(fileName).get(constantName);
        }
    }

    public String getTextDef(String fileName, String constantName) {
        try {
            return this.getText(fileName, constantName);
        } catch (InvalidFileNameException | InvalidConstantNameException ex) {
            return "LANG CONSTANT NOT FOUND";
        }
    }

    /**
     * Return the file name without the extension
     *
     * @param f The file to consider
     * @return The file name (without extension) of the supplied file
     */
    private String getFileNameWithoutExtension(File f) {
        String name = f.getName();

        int posDot = name.indexOf(".");

        if (posDot > -1) {
            name = name.substring(0, posDot);
        }

        return name;
    }

    /**
     * Proceed to read all the lang files in the requested language
     *
     * @param langName The two-letter code of the language to load
     * @throws MalformedLangFileException
     * @throws navalbattle.lang.InvalidLangIdException
     * @throws navalbattle.lang.NoLangFilesException
     * @throws navalbattle.lang.CannotReadLangFileException
     * @throws navalbattle.lang.DuplicateConstantNameInLanguageFileException
     */
    public void loadLangFiles(String langName) throws MalformedLangFileException, InvalidLangIdException, NoLangFilesException, CannotReadLangFileException, DuplicateConstantNameInLanguageFileException {

        langName = langName.toLowerCase();

        if (langName.isEmpty()) {
            throw new InvalidParameterException();
        }

        synchronized (this.constants) {
            this.constants.clear();

            File langFolderFile = new File(this.langFolder);
            File langSelected = new File(langFolderFile, langName);

            if (!langSelected.exists() || !langSelected.isDirectory()) {

                throw new InvalidLangIdException();
            }

            File[] allLangFiles = langSelected.listFiles();

            if (allLangFiles.length < 1) {
                throw new NoLangFilesException();
            }

            for (int i = 0; i < allLangFiles.length; ++i) {
                String currentFileNameWithoutExt = this.getFileNameWithoutExtension(allLangFiles[i]);
                BufferedReader br = null;

                try {
                    br = new BufferedReader(new InputStreamReader(new FileInputStream(allLangFiles[i]), "UTF-8"));
                } catch (FileNotFoundException | UnsupportedEncodingException ex) {
                    throw new CannotReadLangFileException();
                }

                try {
                    String line;
                    boolean isFirstLineOfFile = true;

                    // Reading each line of the language file one by one
                    // Format :
                    // CONSTANT1=Value1
                    // CONSTANT2=Value2
                    // CONSTANT3=x + y = 3
                    while ((line = br.readLine()) != null) {
                        String[] splitted = line.split("=");

                        if (splitted.length < 2) {
                            throw new MalformedLangFileException();
                        }

                        String name = splitted[0];
                        StringBuilder concat = new StringBuilder();

                        for (int j = 1; j < splitted.length; ++j) {
                            concat.append(splitted[j]);

                            if (j != splitted.length - 1) {
                                concat.append('=');
                            }
                        }

                        String value = concat.toString();

                        if (isFirstLineOfFile) {
                            HashMap<String, String> thisConst = new HashMap<>();
                            thisConst.put(name, value);

                            constants.put(currentFileNameWithoutExt, thisConst);

                            isFirstLineOfFile = false;
                        } else {

                            // The constant is already present in the file
                            if (constants.get(currentFileNameWithoutExt).containsKey(name)) {
                                throw new DuplicateConstantNameInLanguageFileException();
                            }

                            constants.get(currentFileNameWithoutExt).put(name, value);
                        }
                    }
                } catch (IOException ex) {
                } finally {
                    try {
                        br.close();
                    } catch (IOException ex) {
                    }
                }
            }
        }
    }

}
