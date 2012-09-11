package org.andvicoso.promocaogroupon;

import java.awt.Desktop;
import java.awt.Toolkit;
import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Hello world!
 *
 */
public class AppJSoup {

    public static void main(String[] args) {
        int secs = 3;
        int count = 0;

        String enablementElement = "buyTheDeal";
        String disabledTxt = "<div class=\"btn btnDisabled\">";
        String payment = "https://www.groupon.com.br/payment";
        String url = "http://www.groupon.com.br/ofertas/hoteis-e-viagens";
        String userAgent = "Mozilla/5.0 (X11; CrOS i686 2268.111.0) AppleWebKit/536.11 (KHTML, like Gecko) Chrome/20.0.1132.57 Safari/536.11";

        while (true) {
            try {
                System.out.println("Getting: " + count++);
                Document doc = Jsoup.connect(url).timeout(secs * 1000).get();

                Element form = doc.body().getElementById(enablementElement);
                String txt = form.html();

                if (!txt.contains(disabledTxt)) {
                    Toolkit.getDefaultToolkit().beep();
                    System.out.println("CORRE NEGAO!");

                    Elements inputs = form.select("input");

                    final Map<String, String> map = new HashMap<String, String>();
                    for (Iterator<Element> it = inputs.iterator(); it.hasNext();) {
                        Element element = it.next();
                        map.put(element.attr("name"), element.val());
                    }

                    doc = Jsoup.connect(payment).timeout(secs * 1000).userAgent(userAgent).
                            data(map).followRedirects(true).post();

                    Desktop desktop = Desktop.getDesktop();
                    URI uri = new URI(doc.baseUri());
                    desktop.browse(uri);
                }
            } catch (Exception ex) {
                //do nothing
            }
        }
    }
}
