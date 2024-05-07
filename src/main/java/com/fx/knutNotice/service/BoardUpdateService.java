package com.fx.knutNotice.service;

import com.fx.knutNotice.crawler.KnutCrawler;
import com.fx.knutNotice.common.KnutURL;
import com.fx.knutNotice.dto.BoardDTO;
import com.fx.knutNotice.dto.FcmDTO;
import com.fx.knutNotice.service.newsUpdateService.NewsUpdateService;
import com.google.firebase.messaging.FirebaseMessagingException;
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

    private final NewsUpdateService generalNewsUpdateService;
    private final NewsUpdateService eventNewsUpdateService;
    private final NewsUpdateService academicNewsUpdateService;
    private final NewsUpdateService scholarshipNewsUpdateService;

    private final KnutCrawler knutCrawler;
    private final FcmService fcmService;

    @Transactional
    @Scheduled(fixedDelay = 1000 * 60 * 60)// 60분마다 실행
    public void updateCheck() throws IOException, FirebaseMessagingException {
        List<BoardDTO> generalNewsList = knutCrawler.crawlBoard(KnutURL.GENERAL_NEWS.URL(),
            KnutURL.GENERAL_NEWS.articleURL());
        List<String> updatedGeneralNewsTitle = generalNewsUpdateService.newsCheck(generalNewsList);

        List<BoardDTO> eventNewsList = knutCrawler.crawlBoard(KnutURL.EVENT_NEWS.URL(),
            KnutURL.EVENT_NEWS.articleURL());
        List<String> updatedEventNewsTitle = eventNewsUpdateService.newsCheck(eventNewsList);

        List<BoardDTO> academicNewsList = knutCrawler.crawlBoard(KnutURL.ACADEMIC_NEWS.URL(),
            KnutURL.ACADEMIC_NEWS.articleURL());
        List<String> updatedAcademicNewsTitle = academicNewsUpdateService.newsCheck(academicNewsList);

        List<BoardDTO> scholarshipNewsList = knutCrawler.crawlBoard(KnutURL.SCHOLARSHIP_NEWS.URL(),
            KnutURL.SCHOLARSHIP_NEWS.articleURL());
        List<String> updatedScholarshipNewsTitle = scholarshipNewsUpdateService.newsCheck(scholarshipNewsList);

        fcmTrigger(updatedGeneralNewsTitle, updatedEventNewsTitle, updatedAcademicNewsTitle, updatedScholarshipNewsTitle);

    }


    //Front와 메시지 형식 상의 후 refactoring 진행
    private void fcmTrigger(List<String> updatedGeneralNewsTitle,
        List<String> updatedEventNewsTitle,
        List<String> updatedAcademicNewsTitle,
        List<String> updatedScholarshipNewsTitle) throws FirebaseMessagingException {
        String updatedGeneralNewsTitles = String.join("\n", updatedGeneralNewsTitle);
        int updatedGeneralNewsCount = updatedGeneralNewsTitle.size();

        String updatedEventNewsTitles = String.join("\n", updatedEventNewsTitle);
        int updatedEventNewsCount = updatedEventNewsTitle.size();

        String updatedAcademicNewsTitles = String.join("\n", updatedAcademicNewsTitle);
        int updatedAcademicNewsCount = updatedAcademicNewsTitle.size();

        String updatedScholarshipNewsTitles = String.join("\n", updatedScholarshipNewsTitle);
        int updatedScholarshipNewsCount = updatedScholarshipNewsTitle.size();

        // FcmDTO 생성
        FcmDTO fcmDTO = FcmDTO.builder()
            .title("일반 뉴스 총 " + updatedGeneralNewsCount + "개 업데이트\n" +
                "이벤트 뉴스 총 " + updatedEventNewsCount + "개 업데이트\n" +
                "학술 뉴스 총 " + updatedAcademicNewsCount + "개 업데이트\n" +
                "장학 뉴스 총 " + updatedScholarshipNewsCount + "개 업데이트")
            .content(
                "일반 뉴스 업데이트:\n" + updatedGeneralNewsTitles + "\n" +
                    "이벤트 뉴스 업데이트:\n" + updatedEventNewsTitles + "\n" +
                    "학술 뉴스 업데이트:\n" + updatedAcademicNewsTitles + "\n" +
                    "장학 뉴스 업데이트:\n" + updatedScholarshipNewsTitles + "\n")
            .build();

        fcmService.sendToAllDevices(fcmDTO);
    }
}
