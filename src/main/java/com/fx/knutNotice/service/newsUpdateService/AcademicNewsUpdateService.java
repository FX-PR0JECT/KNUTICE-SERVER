package com.fx.knutNotice.service.newsUpdateService;

import com.fx.knutNotice.domain.AcademicNewsRepository;
import com.fx.knutNotice.domain.entity.AcademicNews;
import com.fx.knutNotice.dto.BoardDTO;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AcademicNewsUpdateService extends NewsUpdateService{

    private final AcademicNewsRepository academicNewsRepository;
    private static long maxNttId = 0L;

    @Override
    public List<String> newsCheck(List<BoardDTO> newList) {

        List<String> titleList = new ArrayList<>();

        //재시작시 사용
        if (maxNttId == 0L) {
            maxNttId = findMaxNttId();
        }

        //기존 데이터 false로
        academicNewsRepository.updateNewCheckToFalse();

        int newCount = 0;//새로운 글 개수

        for (BoardDTO boardDTO : newList) {
            //DB의 nttId(최신글)보다 크롤링한 nttId가 큰 경우 신규게시글로 판단
            if (boardDTO.getNttId() > maxNttId) {
                AcademicNews newEntity = AcademicNews.builder()
                    .nttId(boardDTO.getNttId())
                    .boardNumber(boardDTO.getBoardNumber())
                    .title(boardDTO.getTitle())
                    .contentURL(boardDTO.getContentURL())
                    .content(boardDTO.getContent())
                    .contentImage(boardDTO.getContentImage())
                    .departName(boardDTO.getDepartName())
                    .registrationDate(boardDTO.getRegistrationDate())
                    .newCheck("true")
                    .build();
                academicNewsRepository.save(newEntity);
                newCount++;

                // FCM발송을 위해 업데이트된 제목만 LIST에 추가.
                titleList.add(boardDTO.getTitle());
            }
        }

        maxNttId = newList.get(0).getNttId();

        //boardNumber가 가장 작은 데이터를 업데이트된 개수만큼 글 삭제
        for (int i = 0; i < newCount; i++) {
            Long minBoardNumber = academicNewsRepository.findMinBoardNumber();
            academicNewsRepository.deleteByBoardNumber(minBoardNumber);
        }

        return titleList;
    }

    private Long findMaxNttId() {
        Long maxNttId = academicNewsRepository.findMaxNttId();
        return maxNttId != null ? maxNttId : 0L;
    }
}
