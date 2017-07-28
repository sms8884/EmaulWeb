package com.jaha.web.emaul.util;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

public class PageWrapper<T> {
    public static final int MAX_PAGE_ITEM_DISPLAY = 10;
    private Page<T> page;
    private List<PageItem> items;
    private int currentNumber;
    private String url;

    private String sort;
    private String searchPart;
    private String searchWord;

    /////////////////////////////////////////////////////// 4 사용자 관리 ///////////////////////////////////////////////////////
    private String searchAptName;
    private String searchDong;
    private String searchHo;
    private String searchAuth;

    /////////////////////////////////////////////////////// 4 사용자 관리 ///////////////////////////////////////////////////////

    public PageWrapper(Page<T> page, String url) {
        this.page = page;
        this.url = url;
        items = new ArrayList<PageItem>();

        currentNumber = page.getNumber() + 1; // start from 1 to match page.page

        int start, size;
        if (page.getTotalPages() <= MAX_PAGE_ITEM_DISPLAY) {
            start = 1;
            size = page.getTotalPages();
        } else {
            if (currentNumber <= MAX_PAGE_ITEM_DISPLAY - MAX_PAGE_ITEM_DISPLAY / 2) {
                start = 1;
                size = MAX_PAGE_ITEM_DISPLAY;
            } else if (currentNumber >= page.getTotalPages() - MAX_PAGE_ITEM_DISPLAY / 2) {
                start = page.getTotalPages() - MAX_PAGE_ITEM_DISPLAY + 1;
                size = MAX_PAGE_ITEM_DISPLAY;
            } else {
                start = currentNumber - MAX_PAGE_ITEM_DISPLAY / 2;
                size = MAX_PAGE_ITEM_DISPLAY;
            }
        }

        for (int i = 0; i < size; i++) {
            items.add(new PageItem(start + i, (start + i) == currentNumber));
        }

        if (page.getSort() != null) {
            Sort sorts = page.getSort();
            String temp = "";

            // /admin/user/list?page=0&size=10&sort=house.dong,asc&sort=house.ho,asc
            for (Order o : sorts) {
                temp += "&sort=" + o.getProperty() + "," + o.getDirection().toString();
            }

            this.sort = temp;
        }
    }

    public PageWrapper(Page<T> page, String url, HttpServletRequest request) {
        this(page, url);

        this.searchPart = request.getParameter("searchPart");
        this.searchWord = request.getParameter("searchWord");

        /////////////////////////////////////////////////////// 4 사용자 관리 ///////////////////////////////////////////////////////
        this.searchAptName = request.getParameter("searchAptName");
        this.searchDong = request.getParameter("searchDong");
        this.searchHo = request.getParameter("searchHo");
        this.searchAuth = request.getParameter("searchAuth");
        /////////////////////////////////////////////////////// 4 사용자 관리 ///////////////////////////////////////////////////////
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<PageItem> getItems() {
        return items;
    }

    public int getNumber() {
        return currentNumber;
    }

    public List<T> getContent() {
        return page.getContent();
    }

    public int getSize() {
        return page.getSize();
    }

    public int getTotalPages() {
        return page.getTotalPages();
    }

    public boolean isFirstPage() {
        return page.isFirst();
    }

    public boolean isLastPage() {
        return page.isLast();
    }

    public boolean isHasPreviousPage() {
        return page.hasPrevious();
    }

    public boolean isHasNextPage() {
        return page.hasNext();
    }

    public String getSort() {
        return sort;
    }

    public long getTotalElements() {
        return this.page.getTotalElements();
    }

    public String getSearchPart() {
        return searchPart;
    }

    public String getSearchWord() {
        return searchWord;
    }

    /////////////////////////////////////////////////////// 4 사용자 관리 ///////////////////////////////////////////////////////
    public String getSearchAptName() {
        return searchAptName;
    }

    public String getSearchDong() {
        return searchDong;
    }

    public String getSearchHo() {
        return searchHo;
    }

    public String getSearchAuth() {
        return searchAuth;
    }
    /////////////////////////////////////////////////////// 4 사용자 관리 ///////////////////////////////////////////////////////

    public class PageItem {
        private int number;
        private boolean current;

        public PageItem(int number, boolean current) {
            this.number = number;
            this.current = current;
        }

        public int getNumber() {
            return this.number - 1;
        }

        public boolean isCurrent() {
            return this.current;
        }
    }

}
