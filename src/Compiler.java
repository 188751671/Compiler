import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class Compiler {
    public static void main(String[] args) {
        if(args.length != 1) {
            System.err.println("Usage: java Main file.dopl");
        }
        else {
            String filename = args[0];
            if(filename.endsWith(".dopl")) {
                try {
                    Parser parser = new Parser(filename);
                    parser.parse();
                } catch (IOException ex) {
                    System.err.println("Exception parsing: " + filename);
                    System.err.println(ex);
                } catch (ParserConfigurationException e) {
                    throw new RuntimeException(e);
                }
            }
            else {
                System.err.println("Unrecognised file type: " + filename);
            }
        }
    }
}
