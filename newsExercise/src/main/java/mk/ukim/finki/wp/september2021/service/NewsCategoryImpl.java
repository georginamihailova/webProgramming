package mk.ukim.finki.wp.september2021.service;

import mk.ukim.finki.wp.september2021.model.NewsCategory;
import mk.ukim.finki.wp.september2021.model.exceptions.InvalidNewsCategoryIdException;
import mk.ukim.finki.wp.september2021.repository.NewsCategoryRepository;
import org.springframework.stereotype.Service;

import javax.transaction.*;
import java.util.List;
import java.util.Locale;

@Service
public class NewsCategoryImpl implements NewsCategoryService{
    private final NewsCategoryRepository newsCategoryRepository;

    public NewsCategoryImpl(NewsCategoryRepository newsCategoryRepository) {
        this.newsCategoryRepository = newsCategoryRepository;
    }

    @Override
    public NewsCategory findById(Long id) {
        return this.newsCategoryRepository.findById(id).orElseThrow(InvalidNewsCategoryIdException::new);
    }

    @Override
    public List<NewsCategory> listAll() {
        return this.newsCategoryRepository.findAll();
    }

    @Override
    @Transactional
    public NewsCategory create(String name) {
        this.newsCategoryRepository.findAll().removeIf(r->r.getName().equals(name));
        NewsCategory category = new NewsCategory(name);
        return this.newsCategoryRepository.save(category);
    }
}
