package com.jaha.web.emaul.util;

import java.util.List;

/**
 * Created by doring on 15. 3. 9..
 */
public class ScrollPage<T> {
    private List<T> content;
    private String nextPageToken;

    public void setContent(List<T> content) {
        this.content = content;
    }

    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }

    public List<T> getContent() {
        return content;
    }

    public String getNextPageToken() {
        return nextPageToken;
    }
}
