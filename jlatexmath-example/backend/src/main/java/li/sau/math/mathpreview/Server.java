package li.sau.math.mathpreview;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.util.List;
import java.util.Objects;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Server {
    private final static int PORT = 9000;

    private static BufferedImage render(String latex, float size, int margin) {
        TeXFormula formula = new TeXFormula(latex);
        TeXIcon icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, size);
        icon.setInsets(new Insets(margin, margin, margin, margin));
        icon.setForeground(Color.BLACK);

        BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        icon.paintIcon(null, g2, 0, 0);
        g2.drawImage(image, 0,0, null);

        return image;
    }

    private static void handleRequest(HttpExchange exchange) {
        try {
            URI requestURI = exchange.getRequestURI();
            List<NameValuePair> params = URLEncodedUtils.parse(requestURI, UTF_8);
            ByteArrayOutputStream tmpImage = new ByteArrayOutputStream();

            String tex = null;
            float size = 18;
            int margin = 0;

            for (NameValuePair p : params) {
                String name = URLDecoder.decode(p.getName(), String.valueOf(UTF_8));
                String value = URLDecoder.decode(p.getValue(), String.valueOf(UTF_8));
                switch (name) {
                    case "tex":
                        tex = value;
                        break;
                    case "size":
                        float s = Float.parseFloat(value);
                        if (s > 0 && s <= 30) {
                            size = s;
                        }
                    case "margin":
                        Integer m = Integer.parseInt(value);
                        if (m > 0 && m <= 30) {
                            size = m;
                        }
                        break;
                }
            }

            if (tex != null) {
                ImageIO.write(render(tex, size, margin), "png", tmpImage);
            }

            if (tmpImage.size() == 0) {
                exchange.sendResponseHeaders(400, tmpImage.size());
            } else {
                exchange.getResponseHeaders().set("Content-Type", "image/png");
                // exchange.getResponseHeaders().set("ETag", String.valueOf(Objects.hash(tex, size, margin)));
                // exchange.getResponseHeaders().set("cache-control", "public, max-age=30672000"); // 1 year
                exchange.sendResponseHeaders(200, tmpImage.size());
                OutputStream os = exchange.getResponseBody();
                os.write(tmpImage.toByteArray());
                os.close();
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    public static void main(String [] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        HttpContext context = server.createContext("/");
        context.setHandler(Server::handleRequest);
        server.start();
        System.out.println("\nThis example is only for demonstration purpose!\n");
        System.out.println("Project is running at http://localhost:" + PORT + "/");
        System.out.println("use e.g. http://localhost:" + PORT
                + "/render?margin=20&size=30&tex=%5C(x%3D%5Cfrac%7B-b%5Cpm%5Csqrt%7Bb%5E2-4ac%7D%7D%7B2a%7D%5C)");
    }
}
