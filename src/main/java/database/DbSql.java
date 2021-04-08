package database;

import database.models.ParagraphRule;
import database.models.Style;
import database.models.Template;
import database.models.TextRangeRule;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DbSql {
    private final String dbPath = "tddb.db";
    private final Logger log = Logger.getLogger(getClass().getName());
    private final DbService service = new DbService();
    // TODO: добавить проверку -> запрет на разные стили у элементов одного уровня
    // уникальность elem_header

    // выбрать id xsd по пути
    public int getTempID(String path) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath)) {
            String query = "SELECT temp_id FROM templates WHERE temp_path = '%s'";
            ResultSet rs = conn.createStatement().executeQuery(String.format(query, path));
            if (rs.next()) {
                return rs.getInt("temp_id");
            } else {
                log.log(Level.WARNING, "Не удалось выполнить 'getTempID()', пустой ответ.");
                return -1;
            }
        } catch (SQLException ex) {
            log.log(Level.WARNING, "Не удалось выполнить 'getTempID()'", ex);
            return -1;
        }
    }

    // выбрать стиль id по имени
    public int getStyleID(String name) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath)) {
            String query = "SELECT style_id FROM styles WHERE style_name = '%s'";
            ResultSet rs = conn.createStatement().executeQuery(String.format(query, name));
            if (rs.next()) {
                return rs.getInt("style_id");
            } else {
                log.log(Level.WARNING, "Не удалось выполнить 'getStyleID()', пустой ответ.");
                return -1;
            }
        } catch (SQLException ex) {
            log.log(Level.WARNING, "Не удалось выполнить 'getStyleID()'", ex);
            return -1;
        }
    }

    // выбрать имя шаблона по пути
    public String getTempName(String path) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath)) {
            String query = "SELECT temp_name FROM templates WHERE temp_path = '%s'";
            ResultSet rs = conn.createStatement().executeQuery(String.format(query, path));
            if (rs.next()) {
                return rs.getString("temp_name");
            } else {
                log.log(Level.WARNING, "Не удалось выполнить 'getTempName()', пустой ответ.");
                return "";
            }
        } catch (SQLException ex) {
            log.log(Level.WARNING, "Не удалось выполнить 'getTempName()'", ex);
            return "";
        }
    }

    // выбрать список id стилей шаблона по id
    public List<Integer> getTempStylesID(int tempID) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath)) {
            String query = "SELECT elements.elem_style_id FROM elements WHERE elements.elem_temp_id == %s;";
            ResultSet rs = conn.createStatement().executeQuery(String.format(query, tempID));
            List<Integer> idList = new ArrayList<>();
            if (rs.next()) {
                do {
                    idList.add(rs.getInt("elem_style_id"));
                } while (rs.next());
                return idList;
            } else {
                log.log(Level.WARNING, "Не удалось выполнить 'getTempStylesID()', пустой ответ.");
                return new ArrayList<>();
            }
        } catch (SQLException ex) {
            log.log(Level.WARNING, "Не удалось выполнить 'getTempStylesID()'", ex);
            return new ArrayList<>();
        }
    }

    // выбрать список имен стилей шаблона по id шаблона
    public List<String> getTempStyles(int tempID) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath)) {
            String query = "SELECT styles.style_name " +
                    "FROM elements JOIN styles ON elements.elem_style_id = styles.style_id " +
                    "WHERE elements.elem_temp_id = %s;";
            ResultSet rs = conn.createStatement().executeQuery(String.format(query, tempID));
            List<String> styleNameList = new ArrayList<>();
            if (rs.next()) {
                do {
                    styleNameList.add(rs.getString("style_name"));
                } while (rs.next());
                return styleNameList;
            } else {
                log.log(Level.WARNING, "Не удалось выполнить 'getTempName()', пустой ответ.");
                return new ArrayList<>();
            }
        } catch (SQLException ex) {
            log.log(Level.WARNING, "Не удалось выполнить 'getTempName()'", ex);
            return new ArrayList<>();
        }
    }

    // выбрать настройки TR по id стиля
    public TextRangeRule getTRRule(int styleID) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath)) {
            String query = "SELECT text_ranges.bold, " +
                    "font_names.font_name, " +
                    "text_ranges.font_size, " +
                    "text_ranges.text_color, " +
                    "text_ranges.italic, " +
                    "text_ranges.all_caps " +
                    "FROM text_ranges " +
                    "join font_names on text_ranges.font_name_id = font_names.font_id " +
                    "WHERE text_ranges.text_range_style_id = %s;";
            ResultSet rs = conn.createStatement().executeQuery(String.format(query, styleID));
            TextRangeRule tr = new TextRangeRule();
            if (rs.next()) {
                do {
                    tr.setTextBold(service.toBoolean(rs.getInt("bold")));
                    tr.setFontName(rs.getString("font_name"));
                    tr.setFontSize(rs.getFloat("font_size"));
                    tr.setTextColor(rs.getString("text_color"));
                    tr.setTextItalic(service.toBoolean(rs.getInt("italic")));
                    tr.setTextCap(service.toBoolean(rs.getInt("all_caps")));
                } while (rs.next());
            }
            return tr;
        } catch (SQLException ex) {
            log.log(Level.WARNING, "Не удалось выполнить 'getTRRule()'", ex);
            return new TextRangeRule();
        }
    }

    // выбрать настройки paragraph по id стиля
    public ParagraphRule getParaRule(int styleID) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath)) {
            String query = "SELECT alignment.align_name, " +
                    "paragraphs.line_space, " +
                    "paragraphs.indent " +
                    "FROM paragraphs " +
                    "join alignment on paragraphs.alignment_id = alignment.align_id " +
                    "WHERE paragraphs.para_style_id = %s;";
            ResultSet rs = conn.createStatement().executeQuery(String.format(query, styleID));
            ParagraphRule pr = new ParagraphRule();
            if (rs.next()) {
                do {
                    pr.setAlignName(rs.getString("align_name"));
                    pr.setLineSpace(rs.getFloat("line_space"));
                    pr.setParagraphIndent(rs.getFloat("indent"));
                } while (rs.next());
            }
            return pr;
        } catch (SQLException ex) {
            log.log(Level.WARNING, "Не удалось выполнить 'getParaRule()'", ex);
            return new ParagraphRule();
        }
    }

    // выбрать стиль элемента у которого elem_header = level
    public int getStyleID(int tempID, int lvl) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath)) {
            String query = "SELECT elem_style_id FROM elements WHERE elem_temp_id = %s and elem_header = %s";
            ResultSet rs = conn.createStatement().executeQuery(String.format(query, tempID, lvl));
            if (rs.next()) {
                return rs.getInt("elem_style_id");
            } else {
                log.log(Level.WARNING, "Не удалось выполнить 'getStyleID()', пустой ответ.");
                return -1;
            }
        } catch (SQLException ex) {
            log.log(Level.WARNING, "Не удалось выполнить 'getStyleID()'", ex);
            return -1;
        }
    }

    /**
     * Вставка в бд имени нового стиля.
     *
     * @param style String
     * @return true если строки добавлены
     */
    public Boolean insertStyle(String style) {
        int numRowsInserted;
        PreparedStatement ps;
        if (isUniqueStyleName(style)) {
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath)) {
                String insertStyle = "INSERT INTO styles (style_name) VALUES ('%s');";
                ps = conn.prepareStatement(String.format(insertStyle, style));
                numRowsInserted = ps.executeUpdate();
            } catch (SQLException ex) {
                log.log(Level.WARNING, String.format("Не удалось выполнить 'insertStyle(%s)'", style), ex);
                return false;
            }
            return numRowsInserted != 0;
        } else {
            log.log(Level.WARNING, String.format("Стиль с именем '%s' уже существует.", style));
            return false;
        }
    }

    /**
     * @return последний style_id
     */
    public int selectLastIdStyle() {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath)) {
            String selectLastIdStyle = "select seq from sqlite_sequence where name='styles';";
            ResultSet resultSet = conn.createStatement().executeQuery(selectLastIdStyle);
            return resultSet.getInt("seq");
        } catch (SQLException ex) {
            log.log(Level.WARNING, "Не удалось выполнить 'selectLastIdStyle()'", ex);
            return 0;
        }
    }

    /**
     * Вставка в бд параметров стиля абзаца.
     *
     * @param paragraph database.models.Paragraph
     * @return true если строки добавлены
     */
    public Boolean insertParagraph(ParagraphRule paragraph) {
        int numRowsInserted;
        PreparedStatement ps;
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath)) {
            String insertParagraph = "INSERT INTO paragraphs (alignment_id, line_space, indent, para_style_id) " +
                    "VALUES (%s, %s, %s, %s);";
            ps = conn.prepareStatement(String.format(insertParagraph, paragraph.getAlignId(),
                    paragraph.getLineSpace(), paragraph.getParagraphIndent(), paragraph.getStyleId()));
            numRowsInserted = ps.executeUpdate();
        } catch (SQLException ex) {
            log.log(Level.WARNING, "Не удалось выполнить 'insertParagraph()'", ex);
            return false;
        }
        return numRowsInserted != 0;
    }

    /**
     * Вставка в бд параметров стиля текста.
     *
     * @param tr database.models.TextRange
     * @return true если строки добавлены
     */
    public Boolean insertTextRange(TextRangeRule tr) {
        int numRowsInserted;
        PreparedStatement ps;
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath)) {
            String insertTextRange = "INSERT INTO text_ranges (bold, font_name_id, font_size, text_color, " +
                    "italic, all_caps, text_range_style_id) VALUES ('%s', %s, %s, '%s', '%s', '%s', %s);";
            ps = conn.prepareStatement(String.format(insertTextRange, tr.getTextBold(), tr.getFontNameId(),
                    tr.getFontSize(), tr.getTextColor(), tr.getTextItalic(), tr.getTextCap(), tr.getStyleId()));
            numRowsInserted = ps.executeUpdate();
        } catch (SQLException ex) {
            log.log(Level.WARNING, "Не удалось выполнить 'insertTextRange()'", ex);
            return false;
        }
        return numRowsInserted != 0;
    }

    /**
     * Проверка на то, что имя стиля уникально.
     *
     * @param name String
     * @return true если уникально
     */
    public Boolean isUniqueStyleName(String name) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath)) {
            String selectIdByName = "select style_id from styles where style_name='%s'";
            ResultSet rs = conn.createStatement().executeQuery(String.format(selectIdByName, name));
            return !rs.next();
        } catch (SQLException ex) {
            log.log(Level.WARNING, "Не удалось выполнить 'isUniqueStyleName()'", ex);
            return false;
        }
    }

    /**
     * Выборка всех стилей в бд
     *
     * @return список Style
     */
    public List<Style> selectStyles() {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath)) {
            String query = "SELECT styles.style_id, styles.style_name, alignment.align_name, " +
                    "paragraphs.line_space, paragraphs.indent, text_ranges.bold, " +
                    "font_names.font_name, text_ranges.font_size, text_ranges.text_color, " +
                    "text_ranges.italic, text_ranges.all_caps " +
                    "FROM styles " +
                    "join text_ranges on styles.style_id = text_ranges.text_range_style_id " +
                    "join paragraphs on styles.style_id = paragraphs.para_style_id " +
                    "join alignment on paragraphs.alignment_id = alignment.align_id " +
                    "join font_names on text_ranges.text_range_style_id = font_names.font_id;";
            ResultSet rs = conn.createStatement().executeQuery(query);
            List<Style> styleList = new ArrayList<>();
            if (rs.next()) {
                do {
                    Style style = new Style();
                    style.setStyleId(rs.getInt("style_id"));
                    style.setStyleName(rs.getString("style_name"));
                    style.setAlignment(rs.getString("align_name"));
                    style.setFontName(rs.getString("font_name"));
                    style.setTextColor(rs.getString("text_color"));
                    style.setLineSpace(rs.getFloat("line_space"));
                    style.setIndent(rs.getFloat("indent"));
                    style.setFontSize(rs.getFloat("font_size"));
                    style.setItalic(rs.getBoolean("italic"));
                    style.setAllCaps(rs.getBoolean("all_caps"));
                    style.setBold(rs.getBoolean("bold"));
                    styleList.add(style);
                } while (rs.next());
                return styleList;
            } else return new ArrayList<>();
        } catch (SQLException ex) {
            log.log(Level.WARNING, "Не удалось выполнить 'selectStyles()'", ex);
            return new ArrayList<>();
        }
    }

    /**
     * Выборка всех шаблонов в БД (id, name, path, style_name)
     *
     * @return List<Template>
     */
    public List<Template> selectTemplates() {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath)) {
            String query = "select templates.temp_id, templates.temp_name, " +
                    "templates.temp_path from templates;";
            ResultSet rs = conn.createStatement().executeQuery(query);
            List<Template> templateList = new ArrayList<>();
            if (rs.next()) {
                do {
                    Template template = new Template();
                    template.setTempId(rs.getInt("temp_id"));
                    template.setTempName(rs.getString("temp_name"));
                    template.setTempPath(rs.getString("temp_path"));
                    templateList.add(template);
                } while (rs.next());
                return templateList;
            } else return new ArrayList<>();
        } catch (SQLException ex) {
            log.log(Level.WARNING, "Не удалось выполнить 'selectTemplates()'", ex);
            return new ArrayList<>();
        }
    }

    /**
     * Выборка  всех названий отступов в БД
     *
     * @return List<String>
     */
    public List<String> getAlignments() {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath)) {
            String query = "select * from alignment;";
            ResultSet rs = conn.createStatement().executeQuery(query);
            List<String> alignList = new ArrayList<>();
            if (rs.next()) {
                do {
                    alignList.add(rs.getString(2));
                } while (rs.next());
                return alignList;
            } else return new ArrayList<>();
        } catch (SQLException ex) {
            log.log(Level.WARNING, "Не удалось выполнить 'selectAlignments()'", ex);
            return new ArrayList<>();
        }
    }

    /**
     * Выборка всех названий шрифтов в БД
     *
     * @return List<String>
     */
    public List<String> getFontNames() {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath)) {
            String query = "select * from font_names;";
            ResultSet rs = conn.createStatement().executeQuery(query);
            List<String> fontNameList = new ArrayList<>();
            if (rs.next()) {
                do {
                    fontNameList.add(rs.getString(2));
                } while (rs.next());
                return fontNameList;
            } else return new ArrayList<>();
        } catch (SQLException ex) {
            log.log(Level.WARNING, "Не удалось выполнить 'selectFontNames()'", ex);
            return new ArrayList<>();
        }
    }
}

