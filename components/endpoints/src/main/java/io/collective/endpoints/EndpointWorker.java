package io.collective.endpoints;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import io.collective.articles.ArticleDataGateway;
import io.collective.articles.ArticleInfo;
import io.collective.restsupport.RestTemplate;
import io.collective.rss.RSS;
import io.collective.workflow.Worker;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class EndpointWorker implements Worker<EndpointTask> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final RestTemplate template;
    private final ArticleDataGateway gateway;

    public EndpointWorker(RestTemplate template, ArticleDataGateway gateway) {
        this.template = template;
        this.gateway = gateway;
    }

    @NotNull
    @Override
    public String getName() {
        return "ready";
    }

    @Override
    public void execute(EndpointTask task) throws IOException {
        String response = template.get(task.getEndpoint(), task.getAccept());
        gateway.clear();

        {
            // map the response to rss object, from readme
            RSS rss = new XmlMapper().readValue(response, RSS.class);
            // map rss results to an article infos collection
            List< ArticleInfo> articleInfos = rss.getChannel().getItem().stream()
                    .map(item -> new ArticleInfo(0, item.getTitle()))
                    .collect(Collectors.toList());
            // save articles infos to the article gateway
            articleInfos.forEach(info -> gateway.save(info.getTitle()));
        }
    }
}
