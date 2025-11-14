package servlets;
import dao.DAOFactory;
import dao.*;
import beans.*;


import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class PatientServlet
 */
@WebServlet("/PatientServlet")
public class PatientServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	 private UserDAO userDAO;
	    private TicketDAO ticketDAO;
	    private CreneauDAO creneauDAO;
	    private ConsultationDAO consultationDAO;
	    private SpecialiteDAO specialiteDAO;	    
       
    /**
     * @see HttpServlet#HttpServlet()
     */
	    
	    
	    public void init() throws ServletException {
	        DAOFactory factory = DAOFactory.getInstance();
	        userDAO = factory.getUserDAO();
	        ticketDAO = factory.getTicketDAO();
	        creneauDAO = factory.getCreneauDAO();
	        consultationDAO = factory.getConsultationDAO();
	        specialiteDAO = factory.getSpecialiteDAO();
	    }
	    
    public PatientServlet() {
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
	        if (!"PATIENT".equals(user.getRole())) {
	            response.sendRedirect(request.getContextPath() + "/login");
	            return;
	        }	
	        String pathInfo = request.getPathInfo();
	        if (pathInfo == null) {
	            pathInfo = "/dashboard";
	        }
	        
	        switch (pathInfo) {
	            case "/dashboard":
	                afficherDashboard(request, response, user.getId());
	                break;
	            case "/rendez-vous":
	                afficherRendezVous(request, response);
	                break;
	            case "/mes-tickets":
	                afficherMesTickets(request, response, user.getId());
	                break;
	            case "/mes-consultations":
	                afficherMesConsultations(request, response, user.getId());
	                break;
	            case "/profil":
	                afficherProfil(request, response, user.getId());
	                break;
	            default:
	                response.sendError(HttpServletResponse.SC_NOT_FOUND);
	        }
	        
	        
	        
	        }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		 
        String pathInfo = request.getPathInfo();
        
        if ("/creer-ticket".equals(pathInfo)) {
            creerTicket(request, response);
        } else if ("/annuler-ticket".equals(pathInfo)) {
            annulerTicket(request, response);
        } else if ("/update-profil".equals(pathInfo)) {
            updateProfil(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }	}





//methodes utilises!!!!!
private void afficherDashboard(HttpServletRequest request, HttpServletResponse response, int patientId) 
        throws ServletException, IOException {
    
    // Récupérer les tickets du patient
    List<Ticket> tickets = ticketDAO.getTicketsByPatient(patientId);
    
    // Récupérer les consultations du patient
    List<Consultation> consultations = consultationDAO.getConsultationsByPatient(patientId);
    
    request.setAttribute("tickets", tickets);
    request.setAttribute("consultations", consultations);
    request.setAttribute("nbTickets", tickets.size());
    request.setAttribute("nbConsultations", consultations.size());
    
    request.getRequestDispatcher("/WEB-INF/views/patient/dashboard.jsp").forward(request, response);
}

private void afficherRendezVous(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException {
    
    // Récupérer les spécialités
    List<Specialite> specialites = specialiteDAO.getAllSpecialites();
    
    // Récupérer les créneaux disponibles
    List<Creneau> creneaux = creneauDAO.getCreneauxDisponibles();
    
    // Récupérer les médecins
    List<Medecin> medecins = userDAO.getAllMedecins();
    
    request.setAttribute("specialites", specialites);
    request.setAttribute("creneaux", creneaux);
    request.setAttribute("medecins", medecins);
    
    request.getRequestDispatcher("/WEB-INF/views/patient/rendez-vous.jsp").forward(request, response);
}

private void afficherMesTickets(HttpServletRequest request, HttpServletResponse response, int patientId) 
        throws ServletException, IOException {
    
    List<Ticket> tickets = ticketDAO.getTicketsByPatient(patientId);
    request.setAttribute("tickets", tickets);
    
    request.getRequestDispatcher("/WEB-INF/views/patient/mes-tickets.jsp").forward(request, response);
}

private void afficherMesConsultations(HttpServletRequest request, HttpServletResponse response, int patientId) 
        throws ServletException, IOException {
    
    List<Consultation> consultations = consultationDAO.getConsultationsByPatient(patientId);
    request.setAttribute("consultations", consultations);
    
    request.getRequestDispatcher("/WEB-INF/views/patient/mes-consultations.jsp").forward(request, response);
}

private void afficherProfil(HttpServletRequest request, HttpServletResponse response, int patientId) 
        throws ServletException, IOException {
    
    User patient = userDAO.getUserById(patientId);
    request.setAttribute("patient", patient);
    
    request.getRequestDispatcher("/WEB-INF/views/patient/profil.jsp").forward(request, response);
}


private void creerTicket(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException {
    
    HttpSession session = request.getSession();
    User user = (User) session.getAttribute("user");
    
    // Récupérer les paramètres
    int medecinId = Integer.parseInt(request.getParameter("medecinId"));
    int creneauId = Integer.parseInt(request.getParameter("creneauId"));
    
    // Créer le ticket
    Ticket ticket = new Ticket();
    ticket.setPatientId(user.getId());
    ticket.setMedecinId(medecinId);
    ticket.setCreneauId(creneauId);
    ticket.setStatut("en_attente");
    ticket.setPriorite(1);
    
    if (ticketDAO.creerTicket(ticket)) {
        session.setAttribute("success", "Ticket créé avec succès : " + ticket.getNumero());
    } else {
        session.setAttribute("error", "Erreur lors de la création du ticket");
    }
    
    response.sendRedirect(request.getContextPath() + "/patient/mes-tickets");
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
    
    response.sendRedirect(request.getContextPath() + "/patient/mes-tickets");
}

private void updateProfil(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException {
    
    HttpSession session = request.getSession();
    User user = (User) session.getAttribute("user");
    
    // Récupérer le patient complet
    Patient patient = (Patient) userDAO.getUserById(user.getId());
    
    // Mettre à jour les informations
    patient.setNom(request.getParameter("nom"));
    patient.setPrenom(request.getParameter("prenom"));
    patient.setEmail(request.getParameter("email"));
    patient.setTelephone(request.getParameter("telephone"));
    patient.setAdresse(request.getParameter("adresse"));
    patient.setGroupeSanguin(request.getParameter("groupeSanguin"));
    
    if (userDAO.updatePatient(patient)) {
        session.setAttribute("success", "Profil mis à jour avec succès");
        session.setAttribute("user", patient);
    } else {
        session.setAttribute("error", "Erreur lors de la mise à jour du profil");
    }
    
    response.sendRedirect(request.getContextPath() + "/patient/profil");
}

}



