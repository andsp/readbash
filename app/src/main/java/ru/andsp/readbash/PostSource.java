package ru.andsp.readbash;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Locale;

class PostSource {

    private static final String URL_SOURCE = "http://bash.im/index/%d";


    private String getUrl(int page) {
        return String.format(Locale.getDefault(), URL_SOURCE, page);
    }

    LinkedList<Post> getPosts(int page) throws IOException {
        LinkedList<Post> posts = new LinkedList<>();
        String url = getUrl(page);
        Document doc = Jsoup.connect(url).get();
        doc.select("br").append("n1");
        Elements elements = doc.select("div.quote");
        for (Element element : elements) {
            Elements text = element.getElementsByClass("text");
            if (!text.isEmpty()) {
                Post post = new Post();
                post.setContent(text.first().text().replaceAll("n1","\n"));
                Elements id = element.select("a.id");
                if (!id.isEmpty()) {
                    post.setExternal(Integer.valueOf(id.first().text().replace("#", "")));
                }
                Elements date = element.select("span.date");
                if (!date.isEmpty()) {
                    post.setDate(date.first().text());
                }
                posts.addLast(post);
            }
        }
        return posts;
    }

}
