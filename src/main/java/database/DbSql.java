package database;

import database.models.Paragraph;
import database.models.Style;
import database.models.Template;
import database.models.TextRange;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DbSql {
    private final String dbPath = "tddb.db";
    private final Logger log = Logger.getLogger(getClass().getName());


    /**
     * Вставка в бд имени нового стиля.
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
        }
        else {
            log.log(Level.WARNING, String.format("Стиль с именем '%s' уже существует.", style));
            return false;
        }
    }

    /**
     *
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
     * @param paragraph database.models.Paragraph
     * @return true если строки добавлены
     */
    public Boolean insertParagraph(Paragraph paragraph) {
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
     * @param tr database.models.TextRange
     * @return true если строки добавлены
     */
    public Boolean insertTextRange(TextRange tr) {
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
                    style.setStyleId(   rs.getInt("style_id"));
                    style.setStyleName( rs.getString("style_name"));
                    style.setAlignment( rs.getString("align_name"));
                    style.setFontName(  rs.getString("font_name"));
                    style.setTextColor( rs.getString("text_color"));
                    style.setLineSpace( rs.getFloat("line_space"));
                    style.setIndent(    rs.getFloat("indent"));
                    style.setFontSize(  rs.getFloat("font_size"));
                    style.setItalic(    rs.getBoolean("italic"));
                    style.setAllCaps(   rs.getBoolean("all_caps"));
                    style.setBold(      rs.getBoolean("bold"));
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
     * @return List<Template>
     */
    public List<Template> selectTemplates() {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath)) {
            String query = "select templates.temp_id, templates.temp_name, " +
                    "templates.temp_path, styles.style_name from templates " +
                    "inner join styles on templates.temp_style_id = styles.style_id;";
            ResultSet rs = conn.createStatement().executeQuery(query);
            List<Template> templateList = new ArrayList<>();
            if (rs.next()) {
                do {
                    Template template = new Template();
                    template.setTempId(     rs.getInt("temp_id"));
                    template.setTempName(   rs.getString("temp_name"));
                    template.setTempPath(   rs.getString("temp_path"));
                    template.setStyleName(  rs.getString("style_name"));
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
     * @return List<String>
     */
    public List<String> selectAlignments() {
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
     * @return List<String>
     */
    public List<String> selectFontNames() {
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
