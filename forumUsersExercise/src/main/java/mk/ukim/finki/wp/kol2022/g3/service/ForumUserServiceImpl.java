package mk.ukim.finki.wp.kol2022.g3.service;

import mk.ukim.finki.wp.kol2022.g3.model.ForumUser;
import mk.ukim.finki.wp.kol2022.g3.model.ForumUserType;
import mk.ukim.finki.wp.kol2022.g3.model.Interest;
import mk.ukim.finki.wp.kol2022.g3.model.exceptions.InvalidForumUserIdException;
import mk.ukim.finki.wp.kol2022.g3.repository.ForumUserRepository;
import mk.ukim.finki.wp.kol2022.g3.repository.InterestRepository;
import org.apache.catalina.UserDatabase;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.*;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ForumUserServiceImpl implements ForumUserService, UserDetailsService {
    private final ForumUserRepository forumUserRepository;
    private final InterestRepository interestRepository;
    private final PasswordEncoder passwordEncoder;
    private final InterestService interestService;

    public ForumUserServiceImpl(ForumUserRepository forumUserRepository, InterestRepository interestRepository, PasswordEncoder passwordEncoder, InterestService interestService) {
        this.forumUserRepository = forumUserRepository;
        this.interestRepository = interestRepository;
        this.passwordEncoder = passwordEncoder;
        this.interestService = interestService;
    }

    @Override
    public List<ForumUser> listAll() {
        return this.forumUserRepository.findAll();
    }

    @Override
    public ForumUser findById(Long id) {
        return this.forumUserRepository.findById(id).orElseThrow(InvalidForumUserIdException::new);
    }

    @Override
    @Transactional
    public ForumUser create(String name, String email, String password, ForumUserType type, List<Long> interestId, LocalDate birthday) {
        List<Interest> interestList = null;
        if (interestId != null){
            interestList = interestId.stream().map(r -> this.interestService.findById(r)).collect(Collectors.toList());
        }
        return this.forumUserRepository.save(new ForumUser(name, email, passwordEncoder.encode(password), type, interestList, birthday));
    }


    @Override
    @Transactional
    public ForumUser update(Long id, String name, String email, String password, ForumUserType type, List<Long> interestId, LocalDate birthday) {
        List<Interest> interestList = null;
        ForumUser forumUser = findById(id);
        if (interestId != null){
            interestList = interestId.stream().map(r -> this.interestService.findById(r)).collect(Collectors.toList());
        }
        forumUser.setInterests(interestList);
        forumUser.setName(name);
        forumUser.setEmail(email);
        forumUser.setPassword(password);
        forumUser.setType(type);
        forumUser.setBirthday(birthday);
        return this.forumUserRepository.save(forumUser);
    }

    @Override
    public ForumUser delete(Long id) {
        ForumUser forumUser = findById(id);
        this.forumUserRepository.delete(forumUser);
        return forumUser;
    }

    @Override
    public List<ForumUser> filter(Long interestId, Integer age) {
        Interest interest;
        if (interestId !=null && age == null){
             interest = this.interestService.findById(interestId);
            return this.forumUserRepository.findAllByInterestsContaining(interest);
        } else if (interestId ==null && age != null) {
            LocalDate ageNow = LocalDate.now().minusYears(age);
            return this.forumUserRepository.findAllByBirthdayBefore(ageNow);
        } else if (interestId!=null && age!=null) {
            interest = this.interestService.findById(interestId);
            LocalDate ageNow = LocalDate.now().minusYears(age);
            return this.forumUserRepository.findAllByInterestsContainingAndBirthdayBefore(interest,ageNow);
        }return this.forumUserRepository.findAll();

    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ForumUser user = this.forumUserRepository.findByEmail(username);

        return new User(
                user.getEmail(),
                user.getPassword(),
                Stream.of(new SimpleGrantedAuthority("ROLE_" + user.getType().toString())).collect(Collectors.toList())
        );
    }
}
