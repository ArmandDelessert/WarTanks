package navalbattle.lang.tests;

import java.util.logging.Level;
import java.util.logging.Logger;
import navalbattle.lang.CannotReadLangFileException;
import navalbattle.lang.DuplicateConstantNameInLanguageFileException;
import navalbattle.lang.InvalidConstantNameException;
import navalbattle.lang.InvalidFileNameException;
import navalbattle.lang.InvalidLangIdException;
import navalbattle.lang.LanguageHelper;
import navalbattle.lang.MalformedLangFileException;
import navalbattle.lang.NoLangFilesException;

public class Tests_LanguageHelper {
    
    public Tests_LanguageHelper()
    {
        LanguageHelper fr = new LanguageHelper("tests/res/lang/");
        try {
            fr.loadLangFiles("fr");
        } catch (MalformedLangFileException | InvalidLangIdException | NoLangFilesException | CannotReadLangFileException | DuplicateConstantNameInLanguageFileException ex) {
            Logger.getLogger(Tests_LanguageHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        LanguageHelper en = new LanguageHelper("tests/res/lang/");
        try {
            en.loadLangFiles("en");
        } catch (MalformedLangFileException | InvalidLangIdException | NoLangFilesException | CannotReadLangFileException | DuplicateConstantNameInLanguageFileException ex) {
            Logger.getLogger(Tests_LanguageHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        boolean test1 = this.checkAgainst(fr, "ui_game", "WELCOME_MESSAGE", "Salut");
        boolean test2 = this.checkAgainst(en, "ui_game", "WELCOME_MESSAGE", "Hello");
        
        boolean test3 = this.checkAgainst(fr, "ui_game", "GOODBYE_MESSAGE", "Au revoir");
        boolean test4 = this.checkAgainst(en, "ui_game", "GOODBYE_MESSAGE", "Goodbye");
        
        System.out.println("Test1 : " + ((test1) ? "Success" : "Failed"));
        System.out.println("Test2 : " + ((test2) ? "Success" : "Failed"));
        System.out.println("Test3 : " + ((test3) ? "Success" : "Failed"));
        System.out.println("Test4 : " + ((test4) ? "Success" : "Failed"));
    }
    
    private boolean checkAgainst(LanguageHelper lh, String file, String constantName, String valueExpected)
    {
        try {
            return lh.getText(file, constantName).equals(valueExpected);
        } catch (InvalidFileNameException | InvalidConstantNameException ex) {
            return false;
        }
    }
}
