import Grammer.GrammerAnalyzer;
import IOTool.Interpreter;
import Lexical.LexicalAnalyzer;
import Lexical.LexicalAnalyzerForm;
import IOTool.TurnToFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Compiler {
    public static void main(String[] args) throws IOException {
        File file = new File("testfile.txt");
        if (!file.exists()) {
            System.out.println("Filename error");
            System.exit(0);
        }
        String content = TurnToFile.readFile(file);

        //词法
        LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer(content);
        ArrayList<LexicalAnalyzerForm> lexicalAnalyzerForms = lexicalAnalyzer.LexicalAnalyze();
        lexicalAnalyzerForms = lexicalAnalyzer.clsPredict(lexicalAnalyzerForms);
        TurnToFile.LexicalToFile(true, lexicalAnalyzerForms, "output.txt");
        //语法
        GrammerAnalyzer grammerAnalyzer = new GrammerAnalyzer(lexicalAnalyzerForms);
        TurnToFile.GrammerToFile(false,grammerAnalyzer.grammerAnalyze(),"output_grammer.txt" );
        //错误处理
        //代码生成
        Interpreter interpreter = new Interpreter(grammerAnalyzer.getCodelist());
//        grammerAnalyzer.showCodelist();
        System.out.println(interpreter.interpret());
        TurnToFile.PcodeToFile(true, interpreter.interpret(),"pcoderesult.txt");
    }
}
