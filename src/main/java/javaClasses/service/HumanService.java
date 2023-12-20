package javaClasses.service;

import javaClasses.DiffieHellman;
import javaClasses.entity.Human;
import javaClasses.repository.HumanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class HumanService {

    private final HumanRepository humanRepository;

    @Autowired
    public HumanService(HumanRepository humanRepository) {
        this.humanRepository = humanRepository;
    }

    public int save(Human human, String secretKey){
        try {
            human.setName(DiffieHellman.decrypt(human.getName(), secretKey));
            human.setSurname(DiffieHellman.decrypt(human.getSurname(), secretKey));
            humanRepository.save(human);
        } catch (Exception e) {
            return 0;
        }
        return 1;
    }

    public int delete(int id){
        try {
            humanRepository.deleteById(id);
        } catch (Exception exception){
            return 0;
        }
        return 1;
    }

    public List<Human> show(){
        return humanRepository.findAllByOrderById();
    }

}
