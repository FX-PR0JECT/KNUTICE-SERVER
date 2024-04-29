package com.fx.knutNotice.service;

import com.fx.knutNotice.crawler.KnutCrawler;
import com.fx.knutNotice.common.KnutURL;
import com.fx.knutNotice.dto.BoardDTO;
import com.fx.knutNotice.service.newsUpdateService.AcademicNewsUpdateService;
import com.fx.knutNotice.service.newsUpdateService.EventNewsUpdateService;
import com.fx.knutNotice.service.newsUpdateService.GeneralNewsUpdateService;
import com.fx.knutNotice.service.newsUpdateService.ScholarshipNewsUpdateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardUpdateService {

    private final GeneralNewsUpdateService generalNewsUpdateService;
    private final EventNewsUpdateService eventNewsUpdateService;
    private final AcademicNewsUpdateService academicNewsUpdateService;
    private final ScholarshipNewsUpdateService scholarshipNewsUpdateService;

    private final KnutCrawler knutCrawler;

    @Transactional
    @Scheduled(fixedDelay = 1000 * 60 * 60)// 60분마다 실행
    public void updateCheck() throws IOException {
        List<BoardDTO> generalNewsList = knutCrawler.crawlBoard(KnutURL.GENERAL_NEWS.URL(),
                KnutURL.GENERAL_NEWS.articleURL());
        generalNewsUpdateService.newsCheck(generalNewsList);

        List<BoardDTO> eventNewsList = knutCrawler.crawlBoard(KnutURL.EVENT_NEWS.URL(),
                KnutURL.EVENT_NEWS.articleURL());
        eventNewsUpdateService.newsCheck(eventNewsList);

        List<BoardDTO> academicNewsList = knutCrawler.crawlBoard(KnutURL.ACADEMIC_NEWS.URL(),
                KnutURL.ACADEMIC_NEWS.articleURL());
        academicNewsUpdateService.newsCheck(academicNewsList);

        List<BoardDTO> scholarshipNewsList = knutCrawler.crawlBoard(KnutURL.SCHOLARSHIP_NEWS.URL(),
                KnutURL.SCHOLARSHIP_NEWS.articleURL());
        scholarshipNewsUpdateService.newsCheck(scholarshipNewsList);
    }
}
