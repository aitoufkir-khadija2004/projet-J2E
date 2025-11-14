package servlets;
import dao.DAOFactory;
import dao.*;
import beans.*;


import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class SecretaireServlet
 */
@WebServlet("/SecretaireServlet")
public class SecretaireServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	 private UserDAO userDAO;
	    private TicketDAO ticketDAO;
	    private CreneauDAO creneauDAO;
	    private ConsultationDAO consultationDAO;
	    private SpecialiteDAO specialiteDAO;
	    private NotificationDAO notificationDAO;
	    
	    @Override
	    public void init() throws ServletException {
	        DAOFactory factory = DAOFactory.getInstance();
	        userDAO = factory.getUserDAO();
	        ticketDAO = factory.getTicketDAO();
	        creneauDAO = factory.getCreneauDAO();
	        consultationDAO = factory.getConsultationDAO();
	        specialiteDAO = factory.getSpecialiteDAO();
	        notificationDAO = factory.getNotificationDAO();
	    }
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SecretaireServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        User user = (User) session.getAttribute("user");
        if (!"SECRETAIRE".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            pathInfo = "/dashboard";
        }
        
        switch (pathInfo) {
            case "/dashboard":
                afficherDashboard(request, response);
                break;
            case "/patients":
                afficherPatients(request, response);
                break;
            case "/nouveau-patient":
                afficherNouveauPatient(request, response);
                break;
            case "/tickets":
                afficherTickets(request, response);
                break;
            case "/nouveau-ticket":
                afficherNouveauTicket(request, response);
                break;
            case "/creneaux":
                afficherCreneaux(request, response);
                break;
            case "/nouveau-creneau":
                afficherNouveauCreneau(request, response);
                break;
            case "/medecins":
                afficherMedecins(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	    
        String pathInfo = request.getPathInfo();
        
        if ("/creer-patient".equals(pathInfo)) {
            creerPatient(request, response);
        } else if ("/creer-ticket".equals(pathInfo)) {
            creerTicket(request, response);
        } else if ("/creer-creneau".equals(pathInfo)) {
            creerCreneau(request, response);
        } else if ("/annuler-ticket".equals(pathInfo)) {
            annulerTicket(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }	}
	
	
 // ==================== MÉTHODES GET ====================
    
    private void afficherDashboard(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Statistiques
        List<Patient> patients = userDAO.getAllPatients();
        List<Medecin> medecins = userDAO.getAllMedecins();
        List<Creneau> creneaux = creneauDAO.getCreneauxDisponibles();
        
        request.setAttribute("nbPatients", patients.size());
        request.setAttribute("nbMedecins", medecins.size());
        request.setAttribute("nbCreneaux", creneaux.size());
        
        request.getRequestDispatcher("/WEB-INF/views/secretaire/dashboard.jsp").forward(request, response);
    }
    
    private void afficherPatients(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        List<Patient> patients = userDAO.getAllPatients();
        request.setAttribute("patients", patients);
        
        request.getRequestDispatcher("/WEB-INF/views/secretaire/patients.jsp").forward(request, response);
    }
    
    private void afficherNouveauPatient(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.getRequestDispatcher("/WEB-INF/views/secretaire/nouveau-patient.jsp").forward(request, response);
    }
    
    private void afficherTickets(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Récupérer tous les tickets (on pourrait filtrer par date)
        List<Patient> patients = userDAO.getAllPatients();
        request.setAttribute("patients", patients);
        
        request.getRequestDispatcher("/WEB-INF/views/secretaire/tickets.jsp").forward(request, response);
    }
    
    private void afficherNouveauTicket(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        List<Patient> patients = userDAO.getAllPatients();
        List<Medecin> medecins = userDAO.getAllMedecins();
        List<Creneau> creneaux = creneauDAO.getCreneauxDisponibles();
        
        request.setAttribute("patients", patients);
        request.setAttribute("medecins", medecins);
        request.setAttribute("creneaux", creneaux);
        
        request.getRequestDispatcher("/WEB-INF/views/secretaire/nouveau-ticket.jsp").forward(request, response);
    }
    
    private void afficherCreneaux(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        List<Creneau> creneaux = creneauDAO.getCreneauxDisponibles();
        request.setAttribute("creneaux", creneaux);
        
        request.getRequestDispatcher("/WEB-INF/views/secretaire/creneaux.jsp").forward(request, response);
    }
    
    private void afficherNouveauCreneau(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        List<Medecin> medecins = userDAO.getAllMedecins();
        request.setAttribute("medecins", medecins);
        
        request.getRequestDispatcher("/WEB-INF/views/secretaire/nouveau-creneau.jsp").forward(request, response);
    }
    
    private void afficherMedecins(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        List<Medecin> medecins = userDAO.getAllMedecins();
        request.setAttribute("medecins", medecins);
        
        request.getRequestDispatcher("/WEB-INF/views/secretaire/medecins.jsp").forward(request, response);
    }
    
    // ==================== MÉTHODES POST ====================
    
    private void creerPatient(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        
        // Récupérer les paramètres
        String nom = request.getParameter("nom");
        String prenom = request.getParameter("prenom");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String telephone = request.getParameter("telephone");
        String nss = request.getParameter("numeroSecuriteSociale");
        String dateNaissanceStr = request.getParameter("dateNaissance");
        String adresse = request.getParameter("adresse");
        String groupeSanguin = request.getParameter("groupeSanguin");
        
        // Validation
        if (nom == null || nom.trim().isEmpty() || 
            prenom == null || prenom.trim().isEmpty() ||
            email == null || email.trim().isEmpty()) {
            session.setAttribute("error", "Les champs nom, prénom et email sont obligatoires");
            response.sendRedirect(request.getContextPath() + "/secretaire/nouveau-patient");
            return;
        }
        
        // Créer le patient
        Patient patient = new Patient();
        patient.setNom(nom.trim());
        patient.setPrenom(prenom.trim());
        patient.setEmail(email.trim());
        patient.setPassword(password != null ? password : "patient123");
        patient.setTelephone(telephone);
        patient.setNumeroSecuriteSociale(nss);
        
        if (dateNaissanceStr != null && !dateNaissanceStr.isEmpty()) {
            patient.setDateNaissance(Date.valueOf(dateNaissanceStr));
        }
        
        patient.setAdresse(adresse);
        patient.setGroupeSanguin(groupeSanguin);
        
        if (userDAO.creerPatient(patient)) {
            session.setAttribute("success", "Patient créé avec succès : " + patient.getCodeUser());
            response.sendRedirect(request.getContextPath() + "/secretaire/patients");
        } else {
            session.setAttribute("error", "Erreur lors de la création du patient");
            response.sendRedirect(request.getContextPath() + "/secretaire/nouveau-patient");
        }
    }
    
    private void creerTicket(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        
        // Récupérer les paramètres
        int patientId = Integer.parseInt(request.getParameter("patientId"));
        int medecinId = Integer.parseInt(request.getParameter("medecinId"));
        int creneauId = Integer.parseInt(request.getParameter("creneauId"));
        int priorite = request.getParameter("priorite") != null ? 
                       Integer.parseInt(request.getParameter("priorite")) : 1;
        
        // Créer le ticket
        Ticket ticket = new Ticket();
        ticket.setPatientId(patientId);
        ticket.setMedecinId(medecinId);
        ticket.setCreneauId(creneauId);
        ticket.setStatut("en_attente");
        ticket.setPriorite(priorite);
        
        if (ticketDAO.creerTicket(ticket)) {
            
            // Créer une notification
            Notification notification = new Notification();
            notification.setTicketId(ticket.getId());
            notification.setPatientId(patientId);
            notification.setMessage("Votre ticket " + ticket.getNumero() + " a été créé avec succès.");
            notification.setType("CREATION");
            notification.setMethode("SMS");
            notification.setStatut("en_attente");
            notificationDAO.creerNotification(notification);
            
            session.setAttribute("success", "Ticket créé avec succès : " + ticket.getNumero());
            response.sendRedirect(request.getContextPath() + "/secretaire/tickets");
        } else {
            session.setAttribute("error", "Erreur lors de la création du ticket");
            response.sendRedirect(request.getContextPath() + "/secretaire/nouveau-ticket");
        }
    }
    
    private void creerCreneau(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        
        // Récupérer les paramètres
        int medecinId = Integer.parseInt(request.getParameter("medecinId"));
        String dateStr = request.getParameter("date");
        String heureDebutStr = request.getParameter("heureDebut");
        String heureFinStr = request.getParameter("heureFin");
        int capacite = Integer.parseInt(request.getParameter("capacite"));
        
        // Créer le créneau
        Creneau creneau = new Creneau();
        creneau.setMedecinId(medecinId);
        creneau.setDate(Date.valueOf(dateStr));
        creneau.setHeureDebut(Time.valueOf(heureDebutStr + ":00"));
        creneau.setHeureFin(Time.valueOf(heureFinStr + ":00"));
        creneau.setCapacite(capacite);
        
        if (creneauDAO.creerCreneau(creneau)) {
            session.setAttribute("success", "Créneau créé avec succès : " + creneau.getCodeCreneau());
            response.sendRedirect(request.getContextPath() + "/secretaire/creneaux");
        } else {
            session.setAttribute("error", "Erreur lors de la création du créneau");
            response.sendRedirect(request.getContextPath() + "/secretaire/nouveau-creneau");
        }
    }
    
    private void annulerTicket(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        int ticketId = Integer.parseInt(request.getParameter("ticketId"));
        
        if (ticketDAO.annulerTicket(ticketId)) {
            session.setAttribute("success", "Ticket annulé avec succès");
        } else {
            session.setAttribute("error", "Erreur lors de l'annulation du ticket");
        }
        
        response.sendRedirect(request.getContextPath() + "/secretaire/tickets");
    }

}
