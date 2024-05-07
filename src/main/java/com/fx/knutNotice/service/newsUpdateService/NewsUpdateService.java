package com.fx.knutNotice.service.newsUpdateService;

import com.fx.knutNotice.dto.BoardDTO;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
public abstract class NewsUpdateService {

    abstract public List<String> newsCheck(List<BoardDTO> newList);

}
