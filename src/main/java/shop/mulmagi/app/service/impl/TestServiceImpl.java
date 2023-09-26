package shop.mulmagi.app.service.impl;

import shop.mulmagi.app.converter.TestConverter;
import shop.mulmagi.app.domain.Test;
import shop.mulmagi.app.repository.TestRepository;
import shop.mulmagi.app.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final TestConverter testConverter;
    private final TestRepository testRepository;

    @Transactional
    @Override
    public Test create(String name){
        Test test = testConverter.toTest(name);
        return testRepository.save(test);
    }
}
