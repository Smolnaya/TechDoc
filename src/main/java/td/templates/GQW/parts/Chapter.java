package td.templates.GQW.parts;

import td.templates.headers.FirstLevelHeader;
import td.templates.headers.SecondLevelHeader;
import td.templates.headers.ThirdLevelHeader;

import java.util.List;

/*
Несколько глав в ВКР
 */
public class Chapter {
    private FirstLevelHeader firstLevelHeader;
    private List<SecondLevelHeader> secondLevelHeaders;
    private List<ThirdLevelHeader> thirdLevelHeaders;
    private List<String> contents;
}
