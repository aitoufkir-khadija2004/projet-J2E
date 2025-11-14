package dao;

import beans.*;
import java.util.List;

public interface UserDAO {
    User login(String email, String password);
    User getUserById(int id);
    List<Patient> getAllPatients();
    List<Medecin> getAllMedecins();
    List<Medecin> getMedecinsBySpecialite(int specialiteId);
    boolean creerPatient(Patient patient);
    boolean creerMedecin(Medecin medecin);
    boolean creerSecretaire(Secretaire secretaire);
    boolean creerAdministrateur(Administrateur admin);
    boolean updatePatient(Patient patient);
    boolean deleteUser(int userId);
}