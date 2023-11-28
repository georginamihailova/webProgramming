package mk.ukim.finki.wp.jan2022.g2.service.impl;

import mk.ukim.finki.wp.jan2022.g2.model.Discussion;
import mk.ukim.finki.wp.jan2022.g2.model.DiscussionTag;
import mk.ukim.finki.wp.jan2022.g2.model.User;
import mk.ukim.finki.wp.jan2022.g2.model.exceptions.InvalidDiscussionIdException;
import mk.ukim.finki.wp.jan2022.g2.model.exceptions.InvalidUserIdException;
import mk.ukim.finki.wp.jan2022.g2.repository.DiscussionRepository;
import mk.ukim.finki.wp.jan2022.g2.repository.UserRepository;
import mk.ukim.finki.wp.jan2022.g2.service.DiscussionService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
public class DiscussionServiceImpl implements DiscussionService
{
    private final DiscussionRepository discussionRepository;
    private final UserRepository userRepository;

    public DiscussionServiceImpl(DiscussionRepository discussionRepository, UserRepository userRepository) {
        this.discussionRepository = discussionRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Discussion> listAll() {
        return this.discussionRepository.findAll();
    }

    @Override
    public Discussion findById(Long id) {
        return this.discussionRepository.findById(id).orElseThrow(InvalidDiscussionIdException::new);
    }

    @Override
    @Transactional
    public Discussion create(String title, String description, DiscussionTag discussionTag, List<Long> participants, LocalDate dueDate) {
        /**
         * This method is used to create a new entity, and save it in the database.
         *
         * @return The entity that is created. The id should be generated when the entity is created.
         * @throws InvalidUserIdException when there is no user with the given id
         */
        if (participants == null){
            return this.discussionRepository.save(new Discussion(title,description,discussionTag,null,dueDate));
        }else {
            List<User> participantsList = this.userRepository.findAllById(participants);
            return this.discussionRepository.save(new Discussion(title,description,discussionTag,participantsList,dueDate));
        }



    }

    @Override
    @Transactional
    public Discussion update(Long id, String title, String description, DiscussionTag discussionTag, List<Long> participants) {
        /**
         * This method is used to modify an entity, and save it in the database.
         *
         * @param id          The id of the entity that is being edited
         * @return The entity that is updated.
         * @throws InvalidDiscussionIdException when there is no entity with the given id
         * @throws InvalidUserIdException    when there is no user with the given id
         */
        Discussion discussion = this.discussionRepository.findById(id).orElseThrow(InvalidDiscussionIdException::new);
        discussion.setTitle(title);
        discussion.setId(id);
        discussion.setDescription(description);
        discussion.setTag(discussionTag);
        if (participants == null){
            discussion.setParticipants(null);
        }else {
            List<User> participantsList = this.userRepository.findAllById(participants);
            discussion.setParticipants(participantsList);
        }

        return this.discussionRepository.save(discussion);
    }

    @Override
    public Discussion delete(Long id) {
        Discussion discussion = this.discussionRepository.findById(id).orElseThrow(InvalidDiscussionIdException::new);
        this.discussionRepository.delete(discussion);
        return discussion;

    }

    @Override
    public Discussion markPopular(Long id) {
        Discussion discussion = this.discussionRepository.findById(id).orElseThrow(InvalidDiscussionIdException::new);
        discussion.setPopular(true);
        this.discussionRepository.save(discussion);
        return discussion;
    }

    @Override
    public List<Discussion> filter(Long participantId, Integer daysUntilClosing) {
        LocalDate now = LocalDate.now();
        if (participantId == null && daysUntilClosing != null){
            LocalDate days = now.plusDays(daysUntilClosing);
            return this.discussionRepository.findAllByDueDateBefore(days);
        } else if (participantId!=null && daysUntilClosing == null) {
            User user = this.userRepository.findById(participantId).orElseThrow(InvalidUserIdException::new);
            return this.discussionRepository.findAllByParticipantsContaining(user);
        } else if (participantId != null && daysUntilClosing != null) {
            LocalDate days = now.plusDays(daysUntilClosing);
            User user = this.userRepository.findById(participantId).orElseThrow(InvalidUserIdException::new);
            return this.discussionRepository.findAllByParticipantsContainingAndDueDateBefore(user,days);
        }else return this.discussionRepository.findAll();
    }
}
