package generator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import generator.domain.SensitiveWord;
import generator.service.SensitiveWordService;
import generator.mapper.SensitiveWordMapper;
import org.springframework.stereotype.Service;

/**
* @author 28447
* @description 针对表【sensitive_word】的数据库操作Service实现
* @createDate 2023-06-23 16:44:52
*/
@Service
public class SensitiveWordServiceImpl extends ServiceImpl<SensitiveWordMapper, SensitiveWord>
    implements SensitiveWordService{

}




