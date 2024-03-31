import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HtmlAnalyzer {

  public static void main(String[] args) {
    if (args.length != 1) {
      System.out.println("Usage: java HtmlAnalyzer <URL>");
      return;
    }

    String urlString = args[0];
    try {
      String htmlContent = getHtmlContent(urlString);
      if (htmlContent == null) {
        System.out.println("URL connection error");
        return;
      }

      String deepestText = findDeepestText(htmlContent);
      if (deepestText == null) {
        System.out.println("malformed HTML");
      } else {
        System.out.println(deepestText);
      }
    } catch (IOException e) {
      System.out.println("Error: " + e.getMessage());
    }
  }

  private static String getHtmlContent(String urlString) throws IOException {
    URL url = new URL(urlString);
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("GET");

    int responseCode = connection.getResponseCode();
    if (responseCode == HttpURLConnection.HTTP_OK) {
      BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      StringBuilder content = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        content.append(line).append("\n");
      }
      reader.close();
      return content.toString();
    } else {
      return null;
    }
  }

  private static String findDeepestText(String htmlContent) {
    String[] lines = htmlContent.split("\n");
    String deepestText = null;
    int deepestLevel = -1;

    for (String line : lines) {
      line = line.trim();
      if (line.isEmpty()) {
        continue; // ignore empty lines
      }
      if (!line.startsWith("<")) {
        // It's a text
        deepestText = line;
      } else if (line.startsWith("<") && line.endsWith(">")) {
        // It's a tag
        if (line.endsWith("/>") || line.startsWith("</")) {
          continue; // ignore self-closing tags or closing tags
        } else {
          int level = getTagLevel(line);
          if (level > deepestLevel) {
            deepestLevel = level;
            deepestText = null; // Reset deepestText if a deeper level tag is encountered
          }
        }
      }
    }

    return deepestText;
  }

  private static int getTagLevel(String tag) {
    int level = 0;
    for (char c : tag.toCharArray()) {
      if (c == '<') {
        level++;
      }
    }
    return level;
  }
}
