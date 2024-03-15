package deneme;

// https://github.com/MustfaOzcan/PdpOdevRepo

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
   private static final Pattern JAVADOC_PATTERN = Pattern.compile("/\\**\\*(.*?)\\*/", Pattern.DOTALL | Pattern.MULTILINE);
   private static final Pattern COMMENT_PATTERN = Pattern.compile("(//.*|/\\*.*?\\*/)", Pattern.DOTALL | Pattern.MULTILINE);

   private static final Pattern FUNCTION_PATTERN = Pattern.compile("\\s*(?:public|protected|private)\\s+[\\w\\<\\>\\[\\]]+\\s+(\\w+)\\s*\\(([^)]*)\\)(\\s*throws\\s+[\\w\\,]*)?\\s*\\{");
   
    public static void main(String[] args) {
        try {
            // Kullanıcıdan GitHub depo URL'sini al
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("GitHub Depo URL'sini Girin: ");
            String repoUrl = reader.readLine();

            // Depoyu klonla
            String cloneCommand = "git clone " + repoUrl;
            Process cloneProcess = Runtime.getRuntime().exec(cloneCommand);
            cloneProcess.waitFor();

            // Klonlanan dizini bul
            File clonedRepo = new File(repoUrl.substring(repoUrl.lastIndexOf("/") + 1));
            
            // Sınıfları analiz et
            analyzeClasses(clonedRepo);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void analyzeClasses(File directory) {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles((dir, name) -> name.endsWith(".java"));

            if (files != null) {
                for (File file : files) {
                    analyzeClass(file);
                 
                    
                }
            }
        }
    }

    private static void analyzeClass(File file) {
        int javadocLineCount = 0;
        int totalCommentLineCount = 0;
        int codeLineCount = 0;
        int loc = 0;
        int functionCount = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            boolean inCommentBlock = false;

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                loc++;

                if (line.startsWith("/*")) {
                    inCommentBlock = true;
                }

                if (inCommentBlock) {
                    Matcher javadocMatcher = JAVADOC_PATTERN.matcher(line);
                    Matcher commentMatcher = COMMENT_PATTERN.matcher(line);
                    if (javadocMatcher.matches()) {
                        javadocLineCount++;
                    } else if (commentMatcher.matches()) {
                        totalCommentLineCount++;
                    }

                    if (line.endsWith("*/")) {
                        inCommentBlock = false;
                    }
                } else {
                    Matcher commentMatcher = COMMENT_PATTERN.matcher(line);
                    if (commentMatcher.find()) {
                        totalCommentLineCount++;
                    }
                    if (line.matches(".*\\{\\s*")) {
                        functionCount++;
                    }
                    if (!line.isEmpty()) {
                        codeLineCount++;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Yorum Sapma Yüzdesi hesabı
        double YG = ((javadocLineCount + totalCommentLineCount) * 0.8) / functionCount;
        double YH = (double) codeLineCount / functionCount * 0.3;
        double commentDeviationPercentage = ((100 * YG) / YH) - 100;

        // Sonuçları yazdır
        System.out.println("Sinif: " + file.getName());
        System.out.println("Javadoc Satir Say: " + javadocLineCount);
        System.out.println("Toplam Yorum Satir Say: " + totalCommentLineCount);
        System.out.println("Kod Satır Say: " + codeLineCount);
        System.out.println("LOC: " + loc);
        System.out.println("Fonksiyon Say: " + functionCount);
        System.out.println("Yorum Sapma Yuzdesi: " + "%" + commentDeviationPercentage);
        System.out.println("-----------------------------------------");
    }


}