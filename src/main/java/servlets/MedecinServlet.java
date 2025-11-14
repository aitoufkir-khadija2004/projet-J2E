package servlets;

import dao.DAOFactory;
import dao.*;
import beans.*;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.util.List;

/**
 * Servlet implementation class MedecinServlet
 */
@WebServlet("/MedecinServlet")
public class MedecinServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	 private UserDAO userDAO;
	    private TicketDAO ticketDAO;
	    private CreneauDAO creneauDAO;
	    private ConsultationDAO consultationDAO;
	    
	    @Override
	    public void init() throws ServletException {
	        DAOFactory factory = DAOFactory.getInstance();
	        userDAO = factory.getUserDAO();
	        ticketDAO = factory.getTicketDAO();
	        creneauDAO = factory.getCreneauDAO();
	        consultationDAO = factory.getConsultationDAO();
	    }
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MedecinServlet() {
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
        if (!"MEDECIN".equals(user.getRole())) {
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
            case "/file-attente":
                afficherFileAttente(request, response, user.getId());
                break;
            case "/consultations":
                afficherConsultations(request, response, user.getId());
                break;
            case "/creneaux":
                afficherCreneaux(request, response, user.getId());
                break;
            case "/consultation":
                afficherConsultation(request, response);
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
        
        if ("/appeler-patient".equals(pathInfo)) {
            appellerPatient(request, response);
        } else if ("/creer-consultation".equals(pathInfo)) {
            creerConsultation(request, response);
        } else if ("/terminer-consultation".equals(pathInfo)) {
            terminerConsultation(request, response);
        } else if ("/ajouter-diagnostic".equals(pathInfo)) {
            ajouterDiagnostic(request, response);
        } else if ("/ajouter-prescription".equals(pathInfo)) {
            ajouterPrescription(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }	}
	
	
	
	//methodes
	
	 
    private void afficherDashboard(HttpServletRequest request, HttpServletResponse response, int medecinId) 
            throws ServletException, IOException {
        
        // Statistiques
        List<Ticket> ticketsEnAttente = ticketDAO.getTicketsEnAttente(medecinId);
        List<Consultation> consultations = consultationDAO.getConsultationsByMedecin(medecinId);
        List<Creneau> creneaux = creneauDAO.getCreneauxByMedecin(medecinId);
        
        request.setAttribute("nbTicketsEnAttente", ticketsEnAttente.size());
        request.setAttribute("nbConsultations", consultations.size());
        request.setAttribute("nbCreneaux", creneaux.size());
        request.setAttribute("tickets", ticketsEnAttente);
        
        request.getRequestDispatcher("/WEB-INF/views/medecin/dashboard.jsp").forward(request, response);
    }
    
    private void afficherFileAttente(HttpServletRequest request, HttpServletResponse response, int medecinId) 
            throws ServletException, IOException {
        
        List<Ticket> tickets = ticketDAO.getTicketsEnAttente(medecinId);
        request.setAttribute("tickets", tickets);
        
        request.getRequestDispatcher("/WEB-INF/views/medecin/file-attente.jsp").forward(request, response);
    }
    
    private void afficherConsultations(HttpServletRequest request, HttpServletResponse response, int medecinId) 
            throws ServletException, IOException {
        
        List<Consultation> consultations = consultationDAO.getConsultationsByMedecin(medecinId);
        request.setAttribute("consultations", consultations);
        
        request.getRequestDispatcher("/WEB-INF/views/medecin/consultations.jsp").forward(request, response);
    }
    
    private void afficherCreneaux(HttpServletRequest request, HttpServletResponse response, int medecinId) 
            throws ServletException, IOException {
        
        List<Creneau> creneaux = creneauDAO.getCreneauxByMedecin(medecinId);
        request.setAttribute("creneaux", creneaux);
        
        request.getRequestDispatcher("/WEB-INF/views/medecin/creneaux.jsp").forward(request, response);
    }
    
    private void afficherConsultation(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        int consultationId = Integer.parseInt(request.getParameter("id"));
        Consultation consultation = consultationDAO.getConsultationById(consultationId);
        
        request.setAttribute("consultation", consultation);
        
        request.getRequestDispatcher("/WEB-INF/views/medecin/consultation.jsp").forward(request, response);
    }
    
//post    
    private void appellerPatient(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        int ticketId = Integer.parseInt(request.getParameter("ticketId"));
        
        if (ticketDAO.updateStatut(ticketId, "appele")) {
            session.setAttribute("success", "Patient appelé avec succès");
        } else {
            session.setAttribute("error", "Erreur lors de l'appel du patient");
        }
        
        response.sendRedirect(request.getContextPath() + "/medecin/file-attente");
    }
    
    private void creerConsultation(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        int ticketId = Integer.parseInt(request.getParameter("ticketId"));
        int patientId = Integer.parseInt(request.getParameter("patientId"));
        String symptomes = request.getParameter("symptomes");
        
        Consultation consultation = new Consultation();
        consultation.setTicketId(ticketId);
        consultation.setMedecinId(user.getId());
        consultation.setPatientId(patientId);
        consultation.setSymptomes(symptomes);
        consultation.setStatut("en_cours");
        
        if (consultationDAO.creerConsultation(consultation)) {
            session.setAttribute("success", "Consultation créée avec succès");
            response.sendRedirect(request.getContextPath() + "/medecin/consultation?id=" + consultation.getId());
        } else {
            session.setAttribute("error", "Erreur lors de la création de la consultation");
            response.sendRedirect(request.getContextPath() + "/medecin/file-attente");
        }
    }
    
    private void terminerConsultation(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        int consultationId = Integer.parseInt(request.getParameter("consultationId"));
        
        if (consultationDAO.terminerConsultation(consultationId)) {
            session.setAttribute("success", "Consultation terminée avec succès");
        } else {
            session.setAttribute("error", "Erreur lors de la terminaison de la consultation");
        }
        
        response.sendRedirect(request.getContextPath() + "/medecin/consultations");
    }
    
    private void ajouterDiagnostic(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        int consultationId = Integer.parseInt(request.getParameter("consultationId"));
        String diagnostic = request.getParameter("diagnostic");
        
        if (consultationDAO.ajouterDiagnostic(consultationId, diagnostic)) {
            session.setAttribute("success", "Diagnostic ajouté avec succès");
        } else {
            session.setAttribute("error", "Erreur lors de l'ajout du diagnostic");
        }
        
        response.sendRedirect(request.getContextPath() + "/medecin/consultation?id=" + consultationId);
    }
    
    private void ajouterPrescription(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        int consultationId = Integer.parseInt(request.getParameter("consultationId"));
        String prescription = request.getParameter("prescription");
        
        if (consultationDAO.ajouterPrescription(consultationId, prescription)) {
            session.setAttribute("success", "Prescription ajoutée avec succès");
        } else {
            session.setAttribute("error", "Erreur lors de l'ajout de la prescription");
        }
        
        response.sendRedirect(request.getContextPath() + "/medecin/consultation?id=" + consultationId);
    }
	
	
	

}
