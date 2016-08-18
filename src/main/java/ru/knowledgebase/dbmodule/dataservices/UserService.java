package ru.knowledgebase.dbmodule.dataservices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.knowledgebase.dbmodule.repositories.UserRepository;
import ru.knowledgebase.modelsmodule.User;

import java.util.List;

/**
 * Created by root on 17.08.16.
 */

@Service("userService")
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public void create(User user) throws Exception{
        userRepository.save(user);
    }

    public User findByLogin(String login) throws Exception{
        List<User> res = userRepository.findByLogin(login);
        if (res.size() == 1){
            return res.get(0);
        }
        return null;
    }

    @Transactional
    public void update(User user) throws Exception{
        //User oldUser = userRepository.findOne(new Long(user.getId()));
        User oldUser = userRepository.findOne1(user.getId()).get(0);
        oldUser.copy(user);
        userRepository.save(oldUser);
    }

    @Transactional
    public void delete(User user) throws Exception{
        userRepository.delete(user);
    }
}
