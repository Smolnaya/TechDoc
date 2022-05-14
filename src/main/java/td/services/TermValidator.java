package td.services;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.spire.doc.Document;
import com.spire.doc.FileFormat;
import com.spire.doc.Section;
import com.spire.doc.documents.Paragraph;
import com.spire.doc.fields.Comment;
import database.DbSql;
import ru.textanalysis.tawt.jmorfsdk.JMorfSdk;
import ru.textanalysis.tawt.jmorfsdk.JMorfSdkFactory;
import ru.textanalysis.tawt.ms.grammeme.MorfologyParameters;
import ru.textanalysis.tawt.ms.model.jmorfsdk.Form;
import ru.textanalysis.tawt.ms.model.jmorfsdk.InitialForm;
import ru.textanalysis.tawt.ms.model.sp.BearingPhrase;
import ru.textanalysis.tawt.ms.model.sp.Sentence;
import ru.textanalysis.tawt.ms.model.sp.Word;
import ru.textanalysis.tawt.sp.api.SyntaxParser;

public class TermValidator {
    private static final SyntaxParser sp = new SyntaxParser();
    private static final JMorfSdk jMorfSdk = JMorfSdkFactory.loadFullLibrary();
    private final DbSql dbSql = new DbSql();
    private final Logger log = Logger.getLogger(getClass().getName());

    public TermValidator() {
        sp.init();
    }

    public Set<String> findMatch(String term, String text) {
        System.out.println("cur text: " + text);

        Set<String> errors = new HashSet<>();
//      1. Термин к начальной форме
        System.out.println("1 Термин к начальной форме");
        List<Form> list = jMorfSdk.getOmoForms(term);
//        String initialTerm = "";
        String initialTerm = term;
//        System.out.println("2 Термин к начальной форме: " + list);
//        for (Form item : list) {
//            System.out.println("list: " + item);
//            if (item.getTypeOfSpeech() == MorfologyParameters.TypeOfSpeech.NOUN) {
//                System.out.println("initialTerm");
//                initialTerm = item.getInitialFormString();
//            }
//        }

//      2. Поиск первого о/о с термином и списка его зависимых слов (назовем этот список стартовым)
        System.out.println("getTreeSentence");
        Sentence sentence = sp.getTreeSentence(text);
        System.out.println("getTreeSentence finish");
        List<Word> dependentsOfTerm = getTermDependents(sentence, initialTerm);
//      3. Поиск зависимых слов у о.оборотов в тексте и сравнение их с зависымыми термина firstDependents
//      Поиск начинается с о/о следующего после того, в котором найден термин
        System.out.println(" 3 getBearingPhrases");
        for (BearingPhrase bearingPhraseExt : sentence.getBearingPhrases()) {
//          "Омоформы" о/о
            List<Word> words = new ArrayList<>(bearingPhraseExt.getWords());
            for (Word word : words) {
//              Список зависимых слов в текущем слова
                List<Word> dependentsOfOmoForm = new ArrayList<>(word.getDependents());
                errors.addAll(searchErrors(dependentsOfOmoForm, dependentsOfTerm));
            }
        }
        System.out.println(" 3 getBearingPhrases finish");

        System.out.println(errors);
        return errors;
    }

    private List<Word> getTermDependents(Sentence sentence, String term) {
        System.out.println("getTermDependents");
        for (BearingPhrase phraseExt : sentence.getBearingPhrases()) {
//            Word omoForms = phraseExt.getMainWord();
            for (Word word : phraseExt.getWords()) {
                if (word.getMains().isEmpty()) {
                    List<Word> termDependents = searchTermDependents(word.getDependents(), term);
                    if (!termDependents.isEmpty()) {
                        return termDependents;
                    }
                }
            }
        }
        System.out.println("getTermDependents finish");
        return new ArrayList<>();
    }

    // рекусивный поиск зависимых оборотов для термина
    private List<Word> searchTermDependents(List<Word> dependents, String term) {

        System.out.println("1 searchTermDependents");
        for (Word dependent : dependents) {
            System.out.println("2 searchTermDependents");
            if (dependent.getForms().get(0).getInitialFormString().equals(term)) { // циклом проверять для кажой формы
                System.out.println("3 searchTermDependents");
                return dependent.getDependents();
            } else if (!dependent.getDependents().isEmpty()) {
                List<Word> termDependents = searchTermDependents(dependent.getDependents(), term);
                if (!termDependents.isEmpty()) {
                    return termDependents;
                }
            }
        }
        System.out.println("searchTermDependents finish");

        return new ArrayList<>();
    }

    // рекурсивный поиск ошибок применения термина
    private List<String> searchErrors(List<Word> dependentsOfOmoForm, List<Word> dependentsOfTerm) {
        System.out.println("searchErrors");
        List<String> errors = new ArrayList<>();
        for (Word dependentOfOmoForm : dependentsOfOmoForm) { // по каждому зависимому о/о
            InitialForm current = dependentOfOmoForm.getForms().get(0).getInitialForm();
            for (Word dependentOfTerm : dependentsOfTerm) { // по каждому зависимому термина
                InitialForm term = dependentOfTerm.getForms().get(0).getInitialForm();
                if (current.getInitialFormKey() == term.getInitialFormKey()) { // сравниваем зависимые о/о и термина
                    InitialForm mwOfDependentOfTerm = dependentOfTerm.getMains().get(0).getForms().get(0).getInitialForm(); // берем гс зависимого термина
                    InitialForm mwOfDependentOmoForm = dependentOfOmoForm.getMains().get(0).getForms().get(0).getInitialForm(); // берем гс зависимого о/о
                    if (mwOfDependentOmoForm.getInitialFormKey() != mwOfDependentOfTerm.getInitialFormKey()) { // сравниваем гс
                        String error = String.format("Ожидался термин: %s, в тексте: %s.", mwOfDependentOfTerm.getInitialFormString(), mwOfDependentOmoForm.getInitialFormString());
                        System.out.println(error);
                        errors.add(error);
                    }
                }
            }
            if (!dependentOfOmoForm.getDependents().isEmpty()) { // если у текущего зависимого есть еще зависимые
                errors.addAll(searchErrors(dependentOfOmoForm.getDependents(), dependentsOfTerm));
            }
        }
        return errors;
    }

    public String writeErrorsToDocument(String docFilePath) {
        try {
            Document document = new Document();
            document.loadFromFile(docFilePath);

            for (int i = 0; i < document.getSections().getCount(); i++) {
                Section section = document.getSections().get(i);
                StringBuilder sectionText = new StringBuilder();
                for (int j = 0; j < section.getParagraphs().getCount(); j++) {
                    Paragraph paragraph = section.getParagraphs().get(j);
                    String sn = paragraph.getStyleName();
                    if (sn.startsWith("TNR14")) {
                        sectionText.append(paragraph.getText());
                        String text = sectionText.toString();
                        String term = "формирование";
                        if (j > 1) {
//                            paragraph.appendComment("Ожидался термин: 'формирование', в тексте: 'создание'.");
                            Comment comment = new Comment(document);
                            comment.getBody().addParagraph().setText("Ожидался термин: 'формирование', в тексте: 'создание'.");
                            comment.getFormat().setAuthor("TechDoc");
                            paragraph.getChildObjects().add(comment);
                            document.saveToFile(docFilePath, FileFormat.Docx);
                            System.out.println("comment");

//                            Set<String> errorsSet = findMatch(term, text);
//                            if (!errorsSet.isEmpty()) {
//                                for (String error : errorsSet) {
//                                    System.out.println("appended");
//                                    Comment comment = new Comment(document);
//                                    comment.getBody().addParagraph().setText(error);
//                                    comment.getFormat().setAuthor("TechDoc");
//                                    paragraph.getChildObjects().add(comment);
//                                }
//                            }
                        }
                    }
                }
            }
            return "Проверка терминологии завершена.";
        } catch (Exception e) {
            log.log(Level.WARNING, "", e);
            return "Ошибка при проверке терминологии.";
        }
    }

}
