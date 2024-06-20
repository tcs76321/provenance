package io.collective.articles;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.collective.restsupport.BasicHandler;
import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

public class ArticlesController extends BasicHandler {
    private final ArticleDataGateway gateway;

    public ArticlesController(ObjectMapper mapper, ArticleDataGateway gateway) {
        super(mapper);
        this.gateway = gateway;
    }

    @Override
    public void handle(String target, Request request, HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        get("/articles", List.of("application/json", "text/html"), request, servletResponse, () -> {
            {
                // query the articles gateway for *all* articles,
                List<ArticleRecord> all_articles = this.gateway.findAll();
                // map record to infos,
                List<ArticleInfo> all_article_infos = new ArrayList<>();
                for(ArticleRecord article : all_articles) {
                    ArticleInfo info = new ArticleInfo(article.getId(), article.getTitle());
                    all_article_infos.add(info);
                }
                // and send back a collection of article infos
                writeJsonBody(servletResponse, all_article_infos);
            }
        });

        get("/available", List.of("application/json"), request, servletResponse, () -> {
            {
                // query the articles gateway for *available* articles
                List<ArticleRecord> available_articles = this.gateway.findAvailable();
                // map records to infos
                List<ArticleInfo> available_article_infos = new ArrayList<>();
                for(ArticleRecord article : available_articles) {
                    ArticleInfo info = new ArticleInfo(article.getId(), article.getTitle());
                    available_article_infos.add(info);
                }
                // and send back a collection of article infos
                writeJsonBody(servletResponse, available_article_infos);
            }
        });
    }
}
