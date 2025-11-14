package dao;

import beans.Ticket;
import com.JDBC.MYSQL.TestConnectionJDBC;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TicketDAOImpl implements TicketDAO {
    
    @Override
    public boolean creerTicket(Ticket ticket) {
        String sql = "INSERT INTO ticket (numero, patient_id, medecin_id, creneau_id, statut, priorite) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = TestConnectionJDBC.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            String numeroTicket = genererNumeroTicket();
            ticket.setNumero(numeroTicket);
            
            ps.setString(1, numeroTicket);
            ps.setInt(2, ticket.getPatientId());
            ps.setInt(3, ticket.getMedecinId());
            ps.setInt(4, ticket.getCreneauId());
            ps.setString(5, ticket.getStatut());
            ps.setInt(6, ticket.getPriorite());
            
            int rows = ps.executeUpdate();
            
            if (rows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    ticket.setId(rs.getInt(1));
                }
                System.out.println("Ticket créé : " + numeroTicket);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur creerTicket : " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    @Override
    public Ticket getTicketById(int id) {
        String sql = "SELECT * FROM ticket WHERE id = ?";
        
        try (Connection conn = TestConnectionJDBC.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return createTicketFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur getTicketById : " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    @Override
    public List<Ticket> getTicketsByPatient(int patientId) {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM ticket WHERE patient_id = ? ORDER BY date_creation DESC";
        
        try (Connection conn = TestConnectionJDBC.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, patientId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                tickets.add(createTicketFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur getTicketsByPatient : " + e.getMessage());
            e.printStackTrace();
        }
        
        return tickets;
    }
    
    @Override
    public List<Ticket> getTicketsByMedecin(int medecinId) {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM ticket WHERE medecin_id = ? ORDER BY date_creation DESC";
        
        try (Connection conn = TestConnectionJDBC.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, medecinId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                tickets.add(createTicketFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur getTicketsByMedecin : " + e.getMessage());
            e.printStackTrace();
        }
        
        return tickets;
    }
    
    @Override
    public List<Ticket> getTicketsEnAttente(int medecinId) {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM ticket WHERE medecin_id = ? AND statut = 'en_attente' " +
                     "ORDER BY priorite DESC, date_creation ASC";
        
        try (Connection conn = TestConnectionJDBC.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, medecinId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                tickets.add(createTicketFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur getTicketsEnAttente : " + e.getMessage());
            e.printStackTrace();
        }
        
        return tickets;
    }
    
    @Override
    public boolean updateStatut(int ticketId, String statut) {
        String sql = "UPDATE ticket SET statut = ? WHERE id = ?";
        
        try (Connection conn = TestConnectionJDBC.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, statut);
            ps.setInt(2, ticketId);
            
            int rows = ps.executeUpdate();
            
            if (rows > 0) {
                System.out.println("Statut du ticket mis à jour : " + ticketId + " → " + statut);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur updateStatut : " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    @Override
    public boolean annulerTicket(int ticketId) {
        return updateStatut(ticketId, "annule");
    }
    
    @Override
    public String genererNumeroTicket() {
        String sql = "SELECT COUNT(*) AS count FROM ticket WHERE DATE(date_creation) = CURDATE()";
        try (Connection conn = TestConnectionJDBC.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int count = rs.getInt("count") + 1;
                return String.format("T-%s-%03d", 
                    new java.text.SimpleDateFormat("yyyyMMdd").format(new java.util.Date()), 
                    count);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "T-" + new java.text.SimpleDateFormat("yyyyMMdd").format(new java.util.Date()) + "-001";
    }
    
    private Ticket createTicketFromResultSet(ResultSet rs) throws SQLException {
        Ticket ticket = new Ticket();
        ticket.setId(rs.getInt("id"));
        ticket.setNumero(rs.getString("numero"));
        ticket.setPatientId(rs.getInt("patient_id"));
        ticket.setMedecinId(rs.getInt("medecin_id"));
        ticket.setCreneauId(rs.getInt("creneau_id"));
        ticket.setStatut(rs.getString("statut"));
        ticket.setDateCreation(rs.getTimestamp("date_creation"));
        ticket.setHeureArrivee(rs.getTime("heure_arrivee"));
        ticket.setPriorite(rs.getInt("priorite"));
        return ticket;
    }
}