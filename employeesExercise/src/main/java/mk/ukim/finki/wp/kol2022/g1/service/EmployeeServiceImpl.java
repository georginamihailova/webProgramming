package mk.ukim.finki.wp.kol2022.g1.service;

import mk.ukim.finki.wp.kol2022.g1.model.Employee;
import mk.ukim.finki.wp.kol2022.g1.model.EmployeeType;
import mk.ukim.finki.wp.kol2022.g1.model.Skill;
import mk.ukim.finki.wp.kol2022.g1.model.exceptions.InvalidEmployeeIdException;
import mk.ukim.finki.wp.kol2022.g1.repository.EmployeeRepository;
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
public class EmployeeServiceImpl implements EmployeeService, UserDetailsService {
    private final EmployeeRepository employeeRepository;
    private final SkillService skillService;
    private final PasswordEncoder passwordEncoder;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, SkillService skillService, PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.skillService = skillService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<Employee> listAll() {
        return this.employeeRepository.findAll();
    }

    @Override
    public Employee findById(Long id) {
        return this.employeeRepository.findById(id).orElseThrow(InvalidEmployeeIdException::new);
    }

    @Override
    @Transactional
    public Employee create(String name, String email, String password, EmployeeType type, List<Long> skillId, LocalDate employmentDate) {
        List<Skill> skills = null;
        if (skillId != null) {
            skills = skillId.stream().map(r -> this.skillService.findById(r)).collect(Collectors.toList());
        }
        return this.employeeRepository.save(new Employee(name, email, passwordEncoder.encode(password), type, skills, employmentDate));
    }

    @Override
    @Transactional
    public Employee update(Long id, String name, String email, String password, EmployeeType type, List<Long> skillId, LocalDate employmentDate) {
        List<Skill> skills = null;
        Employee employee = findById(id);
        if (skillId != null) {
            skills = skillId.stream().map(r -> this.skillService.findById(r)).collect(Collectors.toList());
        }
        employee.setName(name);
        employee.setEmail(email);
        employee.setPassword(passwordEncoder.encode(password));
        employee.setType(type);
        employee.setSkills(skills);
        employee.setEmploymentDate(employmentDate);
        return this.employeeRepository.save(employee);
    }

    @Override
    public Employee delete(Long id) {
        Employee employee = findById(id);
        this.employeeRepository.delete(employee);
        return employee;
    }

    @Override
    public List<Employee> filter(Long skillId, Integer yearsOfService) {
        if (skillId == null && yearsOfService != null) {
            LocalDate date = LocalDate.now().minusYears(yearsOfService);
            return this.employeeRepository.findAllByEmploymentDateBefore(date);
        } else if (skillId != null && yearsOfService == null) {
            Skill skill = this.skillService.findById(skillId);
            return this.employeeRepository.findAllBySkillsContaining(skill);
        } else if (skillId != null && yearsOfService != null) {
            LocalDate date = LocalDate.now().minusYears(yearsOfService);
            Skill skill = this.skillService.findById(skillId);
            return this.employeeRepository.findAllBySkillsContainingAndAndEmploymentDateBefore(skill,date);
        } else return this.employeeRepository.findAll();

    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Employee employee = this.employeeRepository.findByEmail(username);

        return new User(
                employee.getEmail(),
                employee.getPassword(),
                Stream.of(new SimpleGrantedAuthority("ROLE_"+employee.getType().toString())).collect(Collectors.toList())
        );
    }
}
