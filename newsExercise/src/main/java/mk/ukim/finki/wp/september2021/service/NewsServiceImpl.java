package mk.ukim.finki.wp.september2021.service;

import mk.ukim.finki.wp.september2021.model.News;
import mk.ukim.finki.wp.september2021.model.NewsCategory;
import mk.ukim.finki.wp.september2021.model.NewsType;
import mk.ukim.finki.wp.september2021.model.exceptions.InvalidNewsCategoryIdException;
import mk.ukim.finki.wp.september2021.model.exceptions.InvalidNewsIdException;
import mk.ukim.finki.wp.september2021.repository.NewsCategoryRepository;
import mk.ukim.finki.wp.september2021.repository.NewsRepository;
import org.springframework.stereotype.Service;

import javax.transaction.*;
import java.util.List;
import java.util.Locale;

@Service
public class NewsServiceImpl implements NewsService {
    private final NewsRepository newsRepository;
    private final NewsCategoryRepository newsCategoryRepository;

    public NewsServiceImpl(NewsRepository newsRepository, NewsCategoryRepository newsCategoryRepository) {
        this.newsRepository = newsRepository;
        this.newsCategoryRepository = newsCategoryRepository;
    }

    @Override
    public List<News> listAllNews() {
        return this.newsRepository.findAll();
    }

    @Override
    public News findById(Long id) {
        return this.newsRepository.findById(id).orElseThrow(InvalidNewsIdException::new);
    }

    @Override
    @Transactional
    public News create(String name, String description, Double price, NewsType type, Long category) {
        if (category == null) {
            return this.newsRepository.save(new News(name, description, price, type, null));
        } else {
            NewsCategory newsCategory = this.newsCategoryRepository.findById(category).orElseThrow(InvalidNewsCategoryIdException::new);
            return this.newsRepository.save(new News(name, description, price, type, newsCategory));
        }

    }

    @Override
    @Transactional
    public News update(Long id, String name, String description, Double price, NewsType type, Long category) {
        News news = this.newsRepository.findById(id).orElseThrow(InvalidNewsIdException::new);
        news.setDescription(description);
        news.setName(name);
        news.setPrice(price);
        news.setType(type);
        if (category == null) {
            news.setCategory(null);
        } else {
            NewsCategory newsCategory = this.newsCategoryRepository.findById(category).orElseThrow(InvalidNewsCategoryIdException::new);
            news.setCategory(newsCategory);
        }
        return this.newsRepository.save(news);

    }

    @Override
    public News delete(Long id) {
        News news = this.newsRepository.findById(id).orElseThrow(InvalidNewsIdException::new);
        this.newsRepository.delete(news);

        return news;
    }

    @Override
    public News like(Long id) {
        News news = this.newsRepository.findById(id).orElseThrow(InvalidNewsIdException::new);
        Integer like = news.getLikes() + 1;
        news.setLikes(like);
        return this.newsRepository.save(news);
    }

    @Override
    public List<News> listNewsWithPriceLessThanAndType(Double price, NewsType type)
    {
        if (price==null && type!=null){
            return this.newsRepository.findAllByType(type);
        } else if (price!=null && type==null) {
            return this.newsRepository.findAllByPriceBefore(price);
        } else if (price!=null && type!=null) {
            return this.newsRepository.findAllByPriceBeforeAndType(price,type);
        }else return this.newsRepository.findAll();
    }
}
