package com.jxcia202.jspdemo1.framework;
import java.io.IOException;
import java.io.Writer;

import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import io.pebbletemplates.pebble.loader.Servlet5Loader;
import jakarta.servlet.ServletContext;

public class ViewEngine {

    private final PebbleEngine engine;

    public ViewEngine(ServletContext servletContext) {
        Servlet5Loader loader = new Servlet5Loader(servletContext);
        loader.setCharset("UTF-8");
        loader.setPrefix("/WEB-INF/" +
                "");
        loader.setSuffix("");
        this.engine = new PebbleEngine.Builder().autoEscaping(true).cacheActive(false) // no cache for dev
                .loader(loader).build();
    }

    public void render(ModelAndView mv, Writer writer) throws IOException {
        PebbleTemplate template = this.engine.getTemplate(mv.view);
        template.evaluate(writer, mv.model);
    }
}