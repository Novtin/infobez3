package javaClasses.service;

import javaClasses.DiffieHellman;
import javaClasses.entity.Client;
import javaClasses.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientService {
    private final ClientRepository clientRepository;

    @Autowired
    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public Client login(Client client, String secretCommonKey) {
        try {
            client.setPassword(DiffieHellman.decrypt(client.getPassword(), secretCommonKey));
            client.setLogin(DiffieHellman.decrypt(client.getLogin(), secretCommonKey));
        } catch (Exception e) {
            System.out.println("Ошибка расшифровки");
            return null;
        }
        List<Client> clientList = clientRepository.findAll();
        if (!clientList.isEmpty()) {
            for (Client client1 : clientList) {
                if (DiffieHellman.checkMessage(client.getLogin(), client1.getLogin())) {
                    if (DiffieHellman.checkMessage(client.getPassword(), client1.getPassword())) {
                        return client1;
                    }
                }
            }
        }
        return null;
    }

    public boolean save(Client client, String secretCommonKey){
        try {
            client.setPassword(DiffieHellman.decrypt(client.getPassword(), secretCommonKey));
            client.setLogin(DiffieHellman.decrypt(client.getLogin(), secretCommonKey));
        } catch (Exception e) {
            System.out.println("Ошибка расшифровки");
            return false;
        }
        List<Client> clientList = clientRepository.findAll();
        if (!clientList.isEmpty()) {
            for (Client client1 : clientList) {
                if (DiffieHellman.checkMessage(
                        client.getLogin(), client1.getLogin())) {
                        return false;
                }
            }
        }
        client.setLogin(DiffieHellman.hashMessage(client.getLogin()));
        client.setPassword(DiffieHellman.hashMessage(client.getPassword()));
        clientRepository.save(client);
        return true;
    }
}
