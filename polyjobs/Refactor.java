import java.nio.file.*;
import java.util.regex.*;
import java.util.stream.*;
import java.io.IOException;

public class Refactor {
    public static void main(String[] args) throws IOException {
        Path controllerDir = Paths.get("d:\\TT\\canhan\\polyjobs\\src\\main\\java\\com\\polyjobs\\controller");
        Pattern p = Pattern.compile("User\\s+(\\w+)\\s*=\\s*\\(User\\)\\s*session\\.getAttribute\\(\"loggedInUser\"\\);");

        Files.walk(controllerDir)
            .filter(Files::isRegularFile)
            .filter(pth -> pth.toString().endsWith(".java"))
            .forEach(file -> {
                try {
                    String content = new String(Files.readAllBytes(file));
                    if (content.contains("session.getAttribute(\"loggedInUser\")")) {
                        boolean modified = false;
                        
                        Matcher m = p.matcher(content);
                        StringBuffer sb = new StringBuffer();
                        while (m.find()) {
                            String varName = m.group(1);
                            // Corrected to use literal newline character in Java string
                            String replacement = "com.polyjobs.dto.UserDTO " + varName + "DTO = (com.polyjobs.dto.UserDTO) session.getAttribute(\"loggedInUser\");\n        User " + varName + " = " + varName + "DTO != null ? userService.findEntityById(" + varName + "DTO.getId()) : null;";
                            // We need to quote the replacement string to avoid $ and \ issues if any, but since we don't have them except what we want, it's fine.
                            m.appendReplacement(sb, replacement.replace("$", "\\$"));
                            modified = true;
                        }
                        m.appendTail(sb);
                        
                        if (modified) {
                            String newContent = sb.toString();
                            if (!newContent.contains("UserService userService;")) {
                                // Add UserService to controller
                                newContent = newContent.replaceFirst("(public class \\w+ \\{)", "$1\n\n    @org.springframework.beans.factory.annotation.Autowired\n    private com.polyjobs.service.UserService userService;");
                            }
                            Files.write(file, newContent.getBytes());
                            System.out.println("Modified: " + file.getFileName());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
    }
}
