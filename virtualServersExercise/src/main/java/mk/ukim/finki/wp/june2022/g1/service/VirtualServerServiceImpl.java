package mk.ukim.finki.wp.june2022.g1.service;

import mk.ukim.finki.wp.june2022.g1.model.OSType;
import mk.ukim.finki.wp.june2022.g1.model.User;
import mk.ukim.finki.wp.june2022.g1.model.VirtualServer;
import mk.ukim.finki.wp.june2022.g1.model.exceptions.InvalidUserIdException;
import mk.ukim.finki.wp.june2022.g1.model.exceptions.InvalidVirtualMachineIdException;
import mk.ukim.finki.wp.june2022.g1.repository.UserRepository;
import mk.ukim.finki.wp.june2022.g1.repository.VirtualServerRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
public class VirtualServerServiceImpl implements VirtualServerService {
    private final VirtualServerRepository virtualServerRepository;
    private final UserRepository userRepository;

    public VirtualServerServiceImpl(VirtualServerRepository virtualServerRepository, UserRepository userRepository) {
        this.virtualServerRepository = virtualServerRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<VirtualServer> listAll() {
        return this.virtualServerRepository.findAll();
    }

    @Override
    public VirtualServer findById(Long id) {
        return this.virtualServerRepository.findById(id).orElseThrow(InvalidVirtualMachineIdException::new);
    }

    @Override
    @Transactional
    public VirtualServer create(String name, String ipAddress, OSType osType, List<Long> owners, LocalDate launchDate) {
        VirtualServer virtualServer;
        if (owners == null) {
            List<User> users = null;
            virtualServer = new VirtualServer(name,ipAddress,osType,users,launchDate);
        } else {
            List<User> users = this.userRepository.findAllById(owners);
            virtualServer = new VirtualServer(name,ipAddress,osType,users,launchDate);
        }
        return this.virtualServerRepository.save(virtualServer);
    }

    @Override
    @Transactional
    public VirtualServer update(Long id, String name, String ipAddress, OSType osType, List<Long> owners) {
        VirtualServer virtualServer = this.virtualServerRepository.findById(id).orElseThrow(InvalidVirtualMachineIdException::new);
        virtualServer.setInstanceName(name);
        virtualServer.setIpAddress(ipAddress);
        virtualServer.setOSType(osType);
        if (owners == null){
            virtualServer.setOwners(null);
        }else {
            List<User> users = this.userRepository.findAllById(owners);
            virtualServer.setOwners(users);
        }
        return this.virtualServerRepository.save(virtualServer);

    }

    @Override
    public VirtualServer delete(Long id) {
        VirtualServer virtualServer = this.virtualServerRepository.findById(id).orElseThrow(InvalidVirtualMachineIdException::new);
        this.virtualServerRepository.delete(virtualServer);
        return virtualServer;
    }

    @Override
    public VirtualServer markTerminated(Long id) {
        VirtualServer virtualServer = this.virtualServerRepository.findById(id).orElseThrow(InvalidVirtualMachineIdException::new);
        virtualServer.setTerminated(true);
        return this.virtualServerRepository.save(virtualServer);
    }

    @Override
    public List<VirtualServer> filter(Long ownerId, Integer activeMoreThanDays) {
        LocalDate now = LocalDate.now();
        if (ownerId == null && activeMoreThanDays != null){
            LocalDate filtered = now.minusDays(activeMoreThanDays);
            return this.virtualServerRepository.findAllByLaunchDateBefore(filtered);
        } else if (ownerId != null && activeMoreThanDays == null) {
            User user = this.userRepository.findById(ownerId).orElseThrow(InvalidUserIdException::new);
            return this.virtualServerRepository.findAllByOwnersContaining(user);
        } else if (ownerId != null && activeMoreThanDays != null) {
            LocalDate filtered = now.minusDays(activeMoreThanDays);
            User user = this.userRepository.findById(ownerId).orElseThrow(InvalidUserIdException::new);
            return this.virtualServerRepository.findAllByLaunchDateBeforeAndOwnersContaining(filtered,user);
        }else return this.virtualServerRepository.findAll();
    }
}
