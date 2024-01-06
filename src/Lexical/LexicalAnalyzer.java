package Lexical;

import java.io.IOException;
import java.util.ArrayList;


public class LexicalAnalyzer {
    public String content;

    private int arrIdx;
    private int mainIdx;
    private int clsIdx;

    public LexicalAnalyzer(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public ArrayList<LexicalAnalyzerForm> LexicalAnalyze() throws IOException {
        LexicalJudge lexicalJudge = new LexicalJudge();
        ArrayList<LexicalAnalyzerForm> res = new ArrayList<>();
        int row = 1;
        for (int i = 0; i < content.length(); i++) {
            char chara = content.charAt(i);

            //judge if is number
            if (lexicalJudge.judgeNum(chara)) {
                StringBuilder buf = new StringBuilder();
                for (int j = i; j < content.length(); j++) {
                    char tmp = content.charAt(j);
                    if (lexicalJudge.judgeNum(tmp)) {
                        buf.append(tmp);
                    } else {
                        i = j - 1;
                        break;
                    }
                }
                res.add(new LexicalAnalyzerForm(buf.toString(), 2, row));
            }

            //judge if is alphabet
            else if (lexicalJudge.judgeWordFirst(chara)) {
                StringBuilder buf = new StringBuilder();
                for (int j = i; j < content.length(); j++) {
                    char tmp = content.charAt(j);
                    if (lexicalJudge.judgeWordThen(tmp)) {
                        buf.append(tmp);
                    } else {
                        i = j - 1;
                        break;
                    }
                }
                LexicalAnalyzerForm laf = new LexicalAnalyzerForm(buf.toString(), 1, row);
                if (laf.getCategoryCode().equals("LETTK")) {
                    res.add(new LexicalAnalyzerForm("const", 1, row));
                    res.add(new LexicalAnalyzerForm("int", 1, row));
                } else if (arrIdx != 0) {
                    ArrayList<LexicalAnalyzerForm> resTMP = new ArrayList<>();
                    res.remove(arrIdx);
                    int tmpIdx = arrIdx - 1;
                    int clsFlag = 0;
                    while (tmpIdx > 0) {
                        if (res.get(tmpIdx).getCategoryCode().equals("LPARENT")) {
                            //lparent
                            resTMP.add(res.get(tmpIdx));
                            res.remove(tmpIdx);
                            tmpIdx--;
                            //idenfr
                            if (res.get(tmpIdx).getCategoryCode().equals("IDENFR")) {
                                //idenfr
                                resTMP.add(res.get(tmpIdx));
                                res.remove(tmpIdx);
                                //Btype
                                res.add(new LexicalAnalyzerForm(buf.toString(), 1, row));
                                break;
                            } else if (res.get(tmpIdx).getCategoryCode().equals("LBRACE")) {
                                clsFlag++;
                                //cls
                                //{
//                                System.out.println(res.get(tmpIdx).getCategoryCode());
                                res.remove(tmpIdx);
                                tmpIdx--;
                                //=
//                                System.out.println(res.get(tmpIdx).getCategoryCode());
                                res.remove(tmpIdx);
                                tmpIdx--;
                                //IDENFR
//                                System.out.println(res.get(tmpIdx).getCategoryCode());
                                resTMP.add(res.get(tmpIdx));
                                res.remove(tmpIdx);
                                tmpIdx--;
                                //Btype
                                //previous btype
                                res.remove(tmpIdx);
                                //new btype
                                res.add(new LexicalAnalyzerForm(buf.toString(), 1, row));
                                break;
                            }
                        }
                        resTMP.add(res.get(tmpIdx));
                        res.remove(tmpIdx);
                        tmpIdx--;
                    }
                    int resTmpSize = resTMP.size();
                    while (resTmpSize > 0) {
                        res.add(resTMP.remove(resTmpSize - 1));
                        resTmpSize--;
                    }
                    if (clsFlag > 0) {
                        res.add(new LexicalAnalyzerForm("{", 4, row));
                    }
                    arrIdx = 0;
                } else if (buf.toString().equals("in")) {
                } else if (buf.toString().equals("main")) {
                    res.add(new LexicalAnalyzerForm("main", 1, row));
                    //todo:maintk
                    mainIdx = res.size() - 1;
                } else {
                    res.add(new LexicalAnalyzerForm(buf.toString(), 1, row));
                }
            }

            //judge if is sym
            else if (lexicalJudge.judgeSingleSym(chara)) {
                StringBuilder buf = new StringBuilder();
                for (int j = i; j < content.length(); j++) {
                    char tmp = content.charAt(j);
                    if (lexicalJudge.judgeSingleSym(tmp)) {
                        if (lexicalJudge.judgeDoubleSym(tmp)) {
                            if (tmp == '&') {
                                // &&
                                if (content.charAt(j + 1) == '&' && j + 1 < content.length()) {
                                    res.add(new LexicalAnalyzerForm("&&", 4, row));
                                    j++;
                                }
                                // &
                                else {
                                    System.out.println("sym && error");
                                }
                            } else if (tmp == '|') {
                                // ||
                                if (content.charAt(j + 1) == '|' && j + 1 < content.length()) {
                                    res.add(new LexicalAnalyzerForm("||", 4, row));
                                    j++;
                                }
                                // |
                                else {
                                    System.out.println("sym || error");
                                }
                            } else if (tmp == '<') {
                                // <=
                                if (content.charAt(j + 1) == '=' && j + 1 < content.length()) {
                                    res.add(new LexicalAnalyzerForm("<=", 4, row));
                                    j++;
                                }
                                // <
                                else {
                                    res.add(new LexicalAnalyzerForm("<", 4, row));
                                }
                            } else if (tmp == '>') {
                                // >=
                                if (content.charAt(j + 1) == '=' && j + 1 < content.length()) {
                                    res.add(new LexicalAnalyzerForm(">=", 4, row));
                                    j++;
                                }
                                // >
                                else {
                                    res.add(new LexicalAnalyzerForm(">", 4, row));
                                }
                            } else if (tmp == '=') {
                                // ==
                                if (content.charAt(j + 1) == '=' && j + 1 < content.length()) {
                                    res.add(new LexicalAnalyzerForm("==", 4, row));
                                    j++;
                                }
                                // =
                                else {
                                    res.add(new LexicalAnalyzerForm("=", 4, row));
                                }
                            } else if (tmp == '!') {
                                // !=
                                if (content.charAt(j + 1) == '=' && j + 1 < content.length()) {
                                    res.add(new LexicalAnalyzerForm("!=", 4, row));
                                    j++;
                                }
                                // =
                                else {
                                    res.add(new LexicalAnalyzerForm("!", 4, row));
                                }
                            } else if (tmp == '-') {
                                // ->
                                if (content.charAt(j + 1) == '>' && j + 1 < content.length()) {
                                    res.add(new LexicalAnalyzerForm("->", 4, row));
                                    //todo:arrow loc
                                    arrIdx = res.size() - 1;
                                    j++;
                                } else {
                                    res.add(new LexicalAnalyzerForm("-", 4, row));
                                }
                            }
                        } else {
                            res.add(new LexicalAnalyzerForm(tmp + "", 4, row));
                        }
                    } else {
                        i = j - 1;
                        break;
                    }
                }
            }

            //judge format
            else if (chara == '\"') {
                StringBuilder buf = new StringBuilder();
                buf.append(chara);
                int r = row;
                for (int j = i + 1; j < content.length(); j++) {
                    char tmp = content.charAt(j);
                    if (lexicalJudge.judgeFormatChar(tmp) && tmp != '\"') {
                        // format \n
                        if (tmp == '\\') {
                            if (content.charAt(j + 1) == 'n' && j + 1 < content.length()) {
                                buf.append(tmp);
                            } else {
                                System.out.println("error1");
                            }
                        }
                        // %d
                        else if (tmp == '%') {
                            if (content.charAt(j + 1) == 'd' && j + 1 < content.length()) {
                                buf.append(tmp);
                            } else {
                                System.out.println("error2");
                            }
                        }
                        // buffer \n
                        else if (tmp == '\n') {
                            row++;
                        } else {
                            buf.append(tmp);
                        }
                    } else if (tmp == '\"') {
                        buf.append(tmp);
                        i = j;
                        break;
                    } else {
                        System.out.println("error3");
                    }
                }
                res.add(new LexicalAnalyzerForm(buf.toString(), 3, row));
            }

            //judge \n
            else if (chara == '\n') {
                row++;
                //continue;
            }

            //judge comment
            else if (chara == '/') {
                if (i + 1 < content.length()) {
                    i++;
                    if (content.charAt(i) == '/') {
                        for (i = i + 1; i < content.length(); i++) {
                            if (content.charAt(i) == '\n') {
                                row++;
                                break;
                            }
                        }
                    } else if (content.charAt(i) == '*') {
                        for (i = i + 1; i < content.length() - 1; i++) {
                            if (content.charAt(i) == '*' && content.charAt(i + 1) == '/') {
                                i++;
                                break;
                            }
                            if (content.charAt(i) == '\n') {
                                row++;
                                //continue;
                            }
                        }
                    } else {
                        res.add(new LexicalAnalyzerForm(chara + "", 4, row));
                        i--;
                    }
                } else {
                    res.add(new LexicalAnalyzerForm(chara + "", 4, row));
                    System.out.println("error4");
                }
            } else {
                if (chara != ' ' && chara != '\t') {
                    System.out.println("error5");
                }
            }
        }
        return res;
    }

    public ArrayList<LexicalAnalyzerForm> clsPredict(ArrayList<LexicalAnalyzerForm> res) {
        ArrayList<LexicalAnalyzerForm> mainLaterRet = new ArrayList<>();
        ArrayList<LexicalAnalyzerForm> mainPreviousRet = new ArrayList<>();
        int mainIdx = 0;
        for (int i = 0; i < res.size(); i++) {
            if (mainIdx == 0 && res.get(i).getCategoryCode().equals("INTTK") && res.get(i+1).getCategoryCode().equals("MAINTK")) {
                mainIdx = i + 1;
            }
            if (mainIdx == 0) {
                mainPreviousRet.add(res.get(i));
            } else {
                if (res.get(i).getCategoryCode().equals("INTTK") && i + 2 < res.size() && mainIdx > 0
                        && res.get(i + 1).getCategoryCode().equals("IDENFR")
                        && res.get(i + 2).getCategoryCode().equals("LPARENT")) {
                    //int
                    mainPreviousRet.add(res.get(i));
                    i++;
                    //idenfr
                    mainPreviousRet.add(res.get(i));
                    i++;
                    //Lparent
                    mainPreviousRet.add(res.get(i));
                    i++;
                    //param
                    while (!res.get(i).getCategoryCode().equals("RPARENT")) {
                        mainPreviousRet.add(res.get(i));
                        i++;
                    }
                    //Rparent
                    mainPreviousRet.add(res.get(i));
                    i++;
                    int braceNum = 0;
                    //LBrace
                    mainPreviousRet.add(res.get(i));
                    braceNum++;
                    i++;
                    while (true) {
                        if (res.get(i).getCategoryCode().equals("LBRACE")) {
                            braceNum++;
                        }
                        mainPreviousRet.add(res.get(i));
                        i++;
                        if (res.get(i).getCategoryCode().equals("RBRACE")) {
                            mainPreviousRet.add(res.get(i));
                            if (braceNum == 1){
                                break;
                            } else {
                                braceNum--;
                            }
                        }
                    }
                } else {
                    mainLaterRet.add(res.get(i));
                }
            }
        }
        ArrayList<LexicalAnalyzerForm> ret = new ArrayList<>();
        ret.addAll(mainPreviousRet);
        ret.addAll(mainLaterRet);
//        for (int i = 0; i < ret.size(); i++) {
//            System.out.println(ret.get(i).turnToFileFormat());
//        }
        return ret;
    }

}
