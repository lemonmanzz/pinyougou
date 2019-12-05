package com.pinyougou.page.service;

public interface PageService {
    boolean genStaticItemHtml(Long goodsId);

    void deleteStaticHtml(Long goodsId);
}
