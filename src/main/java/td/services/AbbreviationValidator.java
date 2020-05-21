package td.services;

import ru.textanalysis.tawt.jmorfsdk.JMorfSdk;
import ru.textanalysis.tawt.jmorfsdk.loader.JMorfSdkFactory;
import ru.textanalysis.tawt.ms.storage.OmoFormList;
import td.domain.Section;
import td.domain.Term;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ru.textanalysis.tfwwt.morphological.structures.grammeme.MorfologyParameters.Name.ABBREVIATION;
import static ru.textanalysis.tfwwt.morphological.structures.grammeme.MorfologyParameters.Name.IDENTIFIER;

public class AbbreviationValidator {
    private static JMorfSdk jMorfSdk = JMorfSdkFactory.loadFullLibrary();

    public static List<Term> getTermList(Section document) {
        List<String> contentList = document.getContent();
        List<Term> termList = new ArrayList<>();
        if (!contentList.isEmpty()) {
            for (String s : contentList) {
                if (!s.trim().isEmpty()) {
                    String[] line = s.trim().split("–");
                    termList.add(new Term(line[0].trim(), line[1].trim()));
                }
            }
        }
        return termList;
    }

    public static Set<String> getAbbrInText(List<Section> sections) {
        List<String> content = new ArrayList<>();
        for (Section section : sections) {
            List<String> text = getContent(section);
            content.addAll(text);
        }

        Set<String> abbreviationInText = new HashSet<>();
        for (String value : content) {
            String[] word = value.trim().split(" ");
            for (String s : word) {
                Pattern pattern = Pattern.compile("([А-ЯA-Z]{2,})");
                Matcher matcher = pattern.matcher(s);
                while (matcher.find()) {
                    String string = s.replaceAll("[^A-Za-zА-Яа-я]", "");
                    OmoFormList list = jMorfSdk.getAllCharacteristicsOfForm(string.toLowerCase());
                    if (!list.isEmpty() && list.stream().anyMatch(form -> (form.getAllMorfCharacteristics() & IDENTIFIER) == ABBREVIATION)) {
                        abbreviationInText.add(string);
                    }
                }
            }
        }
        return abbreviationInText;
    }

    private static List<String> getContent(Section section) {
        List<String> content = new ArrayList<>(section.getContent());
        if (!section.getSubheadersList().isEmpty()) {
            for (int i = 0; i < section.getSubheadersList().size(); i++) {
                List<String> text = getContent(section.getSubheadersList().get(i));
                content.addAll(text);
            }
        }
        return content;
    }
}
