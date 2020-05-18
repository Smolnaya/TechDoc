package td.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Translator {
    private static String dictionary = "libs/dictionary.txt";
    private Map<String, String> words;
    private Logger log = Logger.getLogger(getClass().getName());

    public Translator() {
        this.words = parseDictionary(dictionary);
        log.log(Level.INFO, "Запуск переводчика");
    }

    private Map<String, String> parseDictionary(String dictionary) {
        Map<String, String> words = new HashMap<>();
        try {
            String value = Files.lines(Paths.get(dictionary)).reduce("", String::concat);
            String[] content = value.split(";");
            for (String subString : content) {
                String[] phrase = subString.split("=");
                words.put(phrase[0], phrase[1]);
            }
        } catch (IOException e) {
            log.log(Level.WARNING, "Запуск переводчика провален " + e.getLocalizedMessage());
        }
        return words;
    }

    public String toRussian(String eng) {
        String rus = "";
        for (Map.Entry<String, String> entry : words.entrySet()) {
            if (eng.equals(entry.getKey())) {
                rus = entry.getValue();
            }
        }
        if (rus.isEmpty()) {
            rus = eng;
        }
        return rus;
    }

    public String toEnglish(String rus) {
        String eng = "";
        for (Map.Entry<String, String> entry : words.entrySet()) {
            if (rus.equals(entry.getValue())) {
                eng = entry.getKey();
            }
        }
        if (eng.isEmpty()) {
            eng = rus;
        }
        return eng;
    }

    public Map<String, Map<String, String>> translateSectionRulesToRussian(Map<String, Map<String, String>> xmlSectionRulesRaw) {
        Map<String, Map<String, String>> xmlSectionRules = new HashMap<>();
        for (Map.Entry<String, Map<String, String>> entry : xmlSectionRulesRaw.entrySet()) {
            String rusKey = toRussian(entry.getKey());
            Map<String, String> currentMap = new HashMap<>();
            for (Map.Entry<String, String> stringMapEntry : entry.getValue().entrySet()) {
                String rusRule = toRussian(stringMapEntry.getKey());
                currentMap.put(rusRule, stringMapEntry.getValue());
            }
            xmlSectionRules.put(rusKey, currentMap);
        }
        return xmlSectionRules;
    }

    public Map<String, String> translateGeneralToRussian(Map<String, String> xmlGeneralRulesRaw) {
        Map<String, String> xmlGeneralRules = new HashMap<>();
        for (Map.Entry<String, String> entry : xmlGeneralRulesRaw.entrySet()) {
            String rus = toRussian(entry.getKey());
            xmlGeneralRules.put(rus, entry.getValue());
        }
        return xmlGeneralRules;
    }

    public Map<String, Map<String, String>> translateSectionRulesToEnglish(Map<String, Map<String, String>> xmlSectionRulesRaw) {
        Map<String, Map<String, String>> xmlSectionRules = new HashMap<>();
        for (Map.Entry<String, Map<String, String>> entry : xmlSectionRulesRaw.entrySet()) {
            String engKey = toEnglish(entry.getKey());
            Map<String, String> currentMap = new HashMap<>();
            for (Map.Entry<String, String> stringMapEntry : entry.getValue().entrySet()) {
                String engRule = toEnglish(stringMapEntry.getKey());
                currentMap.put(engRule, stringMapEntry.getValue());
            }
            xmlSectionRules.put(engKey, currentMap);
        }
        return xmlSectionRules;
    }

    public Map<String, String> translateGeneralToEnglish(Map<String, String> xmlGeneralRulesRaw) {
        Map<String, String> xmlGeneralRules = new HashMap<>();
        for (Map.Entry<String, String> entry : xmlGeneralRulesRaw.entrySet()) {
            String eng = toEnglish(entry.getKey());
            xmlGeneralRules.put(eng, entry.getValue());
        }
        return xmlGeneralRules;
    }
}
